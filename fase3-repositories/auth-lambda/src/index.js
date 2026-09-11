import { SecretsManagerClient, GetSecretValueCommand } from '@aws-sdk/client-secrets-manager';
import crypto from 'node:crypto';
import fs from 'node:fs';
import pg from 'pg';
import { isValidCpf, normalizeCpf } from './cpf.js';
import { signJwt, verifyJwt } from './jwt.js';

let secretsClient;
let cachedConfig;
let pool;

export class HttpError extends Error {
  constructor(statusCode, message) {
    super(message);
    this.statusCode = statusCode;
  }
}

const response = (statusCode, body, headers = {}) => ({
  statusCode,
  headers: { 'content-type': 'application/json', ...headers },
  body: JSON.stringify(body)
});

const unauthorized = (message, headers) =>
  response(401, { error: message }, {
    ...headers,
    'www-authenticate': `Bearer realm="oficina-mecanica", error="invalid_token", error_description="${message}"`
  });

/**
 * Configuração da função.
 * - AWS: segredos lidos do Secrets Manager (DB_SECRET_ARN / JWT_SECRET_ARN).
 * - Local/Kubernetes: variáveis de ambiente (JWT_SECRET, DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, DB_SSL).
 */
async function loadConfig() {
  if (cachedConfig) return cachedConfig;

  if (process.env.JWT_SECRET) {
    cachedConfig = {
      host: process.env.DB_HOST ?? 'localhost',
      port: Number(process.env.DB_PORT ?? 5432),
      dbname: process.env.DB_NAME ?? 'oficina_mecanica',
      username: process.env.DB_USER ?? 'oficina',
      password: process.env.DB_PASSWORD ?? '',
      jwtSecret: process.env.JWT_SECRET
    };
    return cachedConfig;
  }

  secretsClient ??= new SecretsManagerClient({});
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

function sslOptions() {
  if (process.env.DB_SSL === 'false') return false;
  const caPath = process.env.DB_SSL_CA_PATH;
  return {
    rejectUnauthorized: true,
    ...(caPath && fs.existsSync(caPath) ? { ca: fs.readFileSync(caPath, 'utf8') } : {})
  };
}

async function findClient(cpf) {
  const config = await loadConfig();
  pool ??= new pg.Pool({
    host: config.host,
    port: config.port ?? 5432,
    database: config.dbname,
    user: config.username,
    password: config.password,
    ssl: sslOptions(),
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
  if (typeof raw !== 'string') return raw;
  try {
    return JSON.parse(raw);
  } catch {
    throw new HttpError(400, 'Corpo da requisição inválido');
  }
}

function requestMethod(event) {
  return (event.requestContext?.http?.method ?? event.httpMethod ?? 'POST').toUpperCase();
}

function headerValue(event, name) {
  const headers = event.headers ?? {};
  const key = Object.keys(headers).find((header) => header.toLowerCase() === name);
  return key ? headers[key] : undefined;
}

/**
 * Handler único com dois fluxos:
 *  1. Login: POST { cpf } → 200 { token } | 400 CPF inválido | 404 cliente inexistente | 403 cliente não ativo.
 *  2. ForwardAuth (Traefik): qualquer método com Authorization: Bearer → 200 + headers X-Auth-* | 401 token ausente/inválido/expirado.
 */
export function createHandler(dependencies = {}) {
  const getClient = dependencies.findClient ?? findClient;
  const getConfig = dependencies.loadConfig ?? loadConfig;
  const now = dependencies.now ?? (() => Math.floor(Date.now() / 1000));
  const log = dependencies.log ?? ((entry) => console.error(JSON.stringify(entry)));

  return async (event) => {
    const correlationId = headerValue(event, 'x-correlation-id') ?? crypto.randomUUID();
    const commonHeaders = { 'x-correlation-id': correlationId };
    try {
      const config = await getConfig();
      const authorization = headerValue(event, 'authorization');

      if (authorization?.startsWith('Bearer ')) {
        let claims;
        try {
          claims = verifyJwt(authorization.slice(7), config.jwtSecret, now());
        } catch (error) {
          return unauthorized(error.message, commonHeaders);
        }
        if (claims.type !== 'access' || !claims.sub) {
          return unauthorized('Token inválido', commonHeaders);
        }
        return response(200, { authenticated: true, clientId: claims.clientId, cpf: claims.sub }, {
          ...commonHeaders,
          'x-auth-client-id': String(claims.clientId),
          'x-auth-cpf': claims.sub
        });
      }

      // Sem Bearer e fora do POST de login: é o Traefik verificando uma rota protegida.
      if (requestMethod(event) !== 'POST') {
        return unauthorized('Token ausente', commonHeaders);
      }

      const body = parseBody(event);
      if (!isValidCpf(body.cpf)) {
        return response(400, { error: 'CPF inválido' }, commonHeaders);
      }

      const cpf = normalizeCpf(body.cpf);
      const client = await getClient(cpf);
      if (!client) return response(404, { error: 'Cliente não encontrado' }, commonHeaders);
      if (client.status !== 'ATIVO') {
        return response(403, { error: 'Cliente não está ativo', status: client.status }, commonHeaders);
      }

      const expiresIn = Number(process.env.JWT_EXPIRATION_SECONDS ?? 3600);
      const token = signJwt({ sub: cpf, clientId: client.id, type: 'access' }, config.jwtSecret, expiresIn, now());
      return response(200, { token, tokenType: 'Bearer', expiresIn }, commonHeaders);
    } catch (error) {
      if (error instanceof HttpError) {
        return response(error.statusCode, { error: error.message }, commonHeaders);
      }
      log({ level: 'error', correlationId, message: error.message });
      return response(500, { error: 'Falha interna na autenticação' }, commonHeaders);
    }
  };
}

export const handler = createHandler();
