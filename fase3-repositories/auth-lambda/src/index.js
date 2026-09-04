import { SecretsManagerClient, GetSecretValueCommand } from '@aws-sdk/client-secrets-manager';
import crypto from 'node:crypto';
import pg from 'pg';
import { isValidCpf, normalizeCpf } from './cpf.js';
import { signJwt, verifyJwt } from './jwt.js';

const secretsClient = new SecretsManagerClient({});
let cachedConfig;
let pool;

const response = (statusCode, body, headers = {}) => ({
  statusCode,
  headers: { 'content-type': 'application/json', ...headers },
  body: JSON.stringify(body)
});

async function loadConfig() {
  if (cachedConfig) return cachedConfig;
  const [databaseResult, jwtResult] = await Promise.all([
    secretsClient.send(new GetSecretValueCommand({ SecretId: process.env.DB_SECRET_ARN })),
    secretsClient.send(new GetSecretValueCommand({ SecretId: process.env.JWT_SECRET_ARN }))
  ]);
  cachedConfig = {
    ...JSON.parse(databaseResult.SecretString),
    jwtSecret: JSON.parse(jwtResult.SecretString).jwtSecret
  };
  return cachedConfig;
}

async function findClient(cpf) {
  const config = await loadConfig();
  pool ??= new pg.Pool({
    host: config.host,
    port: config.port ?? 5432,
    database: config.dbname,
    user: config.username,
    password: config.password,
    ssl: { rejectUnauthorized: true },
    max: 2,
    connectionTimeoutMillis: 3000
  });
  const result = await pool.query(
    'SELECT id, cpf_cnpj, status FROM clientes WHERE cpf_cnpj = $1 LIMIT 1',
    [cpf]
  );
  return result.rows[0] ?? null;
}

function parseBody(event) {
  if (!event.body) return {};
  const raw = event.isBase64Encoded
    ? Buffer.from(event.body, 'base64').toString('utf8')
    : event.body;
  return typeof raw === 'string' ? JSON.parse(raw) : raw;
}

export function createHandler(dependencies = {}) {
  const getClient = dependencies.findClient ?? findClient;
  const getConfig = dependencies.loadConfig ?? loadConfig;
  const now = dependencies.now ?? (() => Math.floor(Date.now() / 1000));

  return async (event) => {
    const correlationId = event.headers?.['x-correlation-id'] ?? crypto.randomUUID();
    const commonHeaders = { 'x-correlation-id': correlationId };
    try {
      const config = await getConfig();
      const authorization = event.headers?.authorization ?? event.headers?.Authorization;

      if (authorization?.startsWith('Bearer ')) {
        const claims = verifyJwt(authorization.slice(7), config.jwtSecret, now());
        return response(200, { authenticated: true }, {
          ...commonHeaders,
          'x-auth-client-id': String(claims.clientId),
          'x-auth-cpf': claims.sub
        });
      }

      const body = parseBody(event);
      if (!isValidCpf(body.cpf)) {
        return response(400, { error: 'CPF inválido' }, commonHeaders);
      }

      const cpf = normalizeCpf(body.cpf);
      const client = await getClient(cpf);
      if (!client) return response(404, { error: 'Cliente não encontrado' }, commonHeaders);
      if (client.status !== 'ATIVO') return response(403, { error: 'Cliente não está ativo' }, commonHeaders);

      const expiresIn = Number(process.env.JWT_EXPIRATION_SECONDS ?? 3600);
      const token = signJwt({ sub: cpf, clientId: client.id, type: 'access' }, config.jwtSecret, expiresIn, now());
      return response(200, { token, tokenType: 'Bearer', expiresIn }, commonHeaders);
    } catch (error) {
      console.error(JSON.stringify({ level: 'error', correlationId, message: error.message }));
      return response(401, { error: 'Não autorizado' }, commonHeaders);
    }
  };
}

export const handler = createHandler();
