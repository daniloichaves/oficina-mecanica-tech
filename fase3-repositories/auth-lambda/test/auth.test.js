import test from 'node:test';
import assert from 'node:assert/strict';
import { isValidCpf } from '../src/cpf.js';
import { createHandler } from '../src/index.js';
import { signJwt } from '../src/jwt.js';

const config = { jwtSecret: 'uma-chave-de-testes-com-pelo-menos-32-bytes' };
const silent = () => {};
const login = (cpf, extra = {}) => ({
  headers: { 'content-type': 'application/json' },
  requestContext: { http: { method: 'POST' } },
  body: JSON.stringify({ cpf }),
  ...extra
});
const forwardAuth = (headers = {}) => ({
  headers,
  requestContext: { http: { method: 'GET' } }
});
const activeClient = { id: 42, cpf_cnpj: '52998224725', status: 'ATIVO' };

test('valida CPF pelos dígitos verificadores', () => {
  assert.equal(isValidCpf('529.982.247-25'), true);
  assert.equal(isValidCpf('52998224725'), true);
  assert.equal(isValidCpf('111.111.111-11'), false);
  assert.equal(isValidCpf('52998224726'), false);
  assert.equal(isValidCpf(''), false);
  assert.equal(isValidCpf(undefined), false);
});

// ---------- Fluxo de login (sucesso) ----------

test('login: emite token para cliente ativo', async () => {
  const handler = createHandler({
    loadConfig: async () => config,
    findClient: async () => activeClient,
    now: () => 1000
  });
  const result = await handler(login('529.982.247-25'));
  const body = JSON.parse(result.body);
  assert.equal(result.statusCode, 200);
  assert.equal(body.tokenType, 'Bearer');
  assert.equal(body.expiresIn, 3600);
  assert.equal(body.token.split('.').length, 3);
  assert.ok(result.headers['x-correlation-id']);

  const claims = JSON.parse(Buffer.from(body.token.split('.')[1], 'base64url').toString());
  assert.equal(claims.sub, '52998224725');
  assert.equal(claims.clientId, 42);
  assert.equal(claims.type, 'access');
  assert.equal(claims.exp, 4600);
});

test('login: propaga X-Correlation-ID recebido', async () => {
  const handler = createHandler({ loadConfig: async () => config, findClient: async () => activeClient });
  const result = await handler(login('52998224725', { headers: { 'X-Correlation-ID': 'abc-123' } }));
  assert.equal(result.headers['x-correlation-id'], 'abc-123');
});

// ---------- Fluxo de login (falhas) ----------

test('login: CPF inválido retorna 400', async () => {
  const handler = createHandler({ loadConfig: async () => config, findClient: async () => activeClient });
  assert.equal((await handler(login('111.111.111-11'))).statusCode, 400);
  assert.equal((await handler(login(''))).statusCode, 400);
});

test('login: corpo JSON inválido retorna 400', async () => {
  const handler = createHandler({ loadConfig: async () => config, findClient: async () => activeClient });
  const result = await handler({ headers: {}, requestContext: { http: { method: 'POST' } }, body: '{cpf' });
  assert.equal(result.statusCode, 400);
});

test('login: cliente inexistente retorna 404', async () => {
  const handler = createHandler({ loadConfig: async () => config, findClient: async () => null });
  assert.equal((await handler(login('52998224725'))).statusCode, 404);
});

test('login: cliente bloqueado ou inativo retorna 403', async () => {
  for (const status of ['BLOQUEADO', 'INATIVO']) {
    const handler = createHandler({
      loadConfig: async () => config,
      findClient: async () => ({ ...activeClient, status })
    });
    const result = await handler(login('52998224725'));
    assert.equal(result.statusCode, 403);
    assert.equal(JSON.parse(result.body).status, status);
  }
});

test('login: falha de infraestrutura retorna 500 sem vazar detalhes', async () => {
  const handler = createHandler({
    loadConfig: async () => config,
    findClient: async () => { throw new Error('connection refused'); },
    log: silent
  });
  const result = await handler(login('52998224725'));
  assert.equal(result.statusCode, 500);
  assert.deepEqual(JSON.parse(result.body), { error: 'Falha interna na autenticação' });
});

// ---------- Fluxo ForwardAuth (Traefik) ----------

async function issueToken(now = 1000) {
  const handler = createHandler({ loadConfig: async () => config, findClient: async () => activeClient, now: () => now });
  return JSON.parse((await handler(login('52998224725'))).body).token;
}

test('forwardAuth: token válido retorna 200 com headers X-Auth-*', async () => {
  const token = await issueToken();
  const validate = createHandler({ loadConfig: async () => config, now: () => 1001 });
  const result = await validate(forwardAuth({ authorization: `Bearer ${token}` }));
  assert.equal(result.statusCode, 200);
  assert.equal(result.headers['x-auth-client-id'], '42');
  assert.equal(result.headers['x-auth-cpf'], '52998224725');
});

test('forwardAuth: aceita header Authorization com qualquer capitalização', async () => {
  const token = await issueToken();
  const validate = createHandler({ loadConfig: async () => config, now: () => 1001 });
  assert.equal((await validate(forwardAuth({ Authorization: `Bearer ${token}` }))).statusCode, 200);
});

test('forwardAuth: token ausente retorna 401 com WWW-Authenticate', async () => {
  const validate = createHandler({ loadConfig: async () => config });
  const result = await validate(forwardAuth({}));
  assert.equal(result.statusCode, 401);
  assert.match(result.headers['www-authenticate'], /Bearer/);
  assert.equal(JSON.parse(result.body).error, 'Token ausente');
});

test('forwardAuth: token expirado retorna 401', async () => {
  const token = await issueToken(1000);
  const validate = createHandler({ loadConfig: async () => config, now: () => 4600 });
  const result = await validate(forwardAuth({ authorization: `Bearer ${token}` }));
  assert.equal(result.statusCode, 401);
  assert.equal(JSON.parse(result.body).error, 'Token expirado');
});

test('forwardAuth: assinatura adulterada retorna 401', async () => {
  const token = await issueToken();
  const [header, payload] = token.split('.');
  const forged = `${header}.${payload}.AAAA`;
  const validate = createHandler({ loadConfig: async () => config, now: () => 1001 });
  assert.equal((await validate(forwardAuth({ authorization: `Bearer ${forged}` }))).statusCode, 401);
});

test('forwardAuth: token assinado com outro segredo retorna 401', async () => {
  const token = signJwt({ sub: '52998224725', clientId: 1, type: 'access' }, 'outro-segredo-qualquer-com-32-bytes!!', 3600, 1000);
  const validate = createHandler({ loadConfig: async () => config, now: () => 1001 });
  assert.equal((await validate(forwardAuth({ authorization: `Bearer ${token}` }))).statusCode, 401);
});

test('forwardAuth: token sem claim type=access retorna 401', async () => {
  const token = signJwt({ sub: '52998224725', clientId: 1 }, config.jwtSecret, 3600, 1000);
  const validate = createHandler({ loadConfig: async () => config, now: () => 1001 });
  assert.equal((await validate(forwardAuth({ authorization: `Bearer ${token}` }))).statusCode, 401);
});

test('forwardAuth: token com alg=none retorna 401', async () => {
  const encode = (value) => Buffer.from(JSON.stringify(value)).toString('base64url');
  const forged = `${encode({ alg: 'none', typ: 'JWT' })}.${encode({ sub: 'x', type: 'access', exp: 9999 })}.`;
  const validate = createHandler({ loadConfig: async () => config, now: () => 1001 });
  assert.equal((await validate(forwardAuth({ authorization: `Bearer ${forged}` }))).statusCode, 401);
});

test('forwardAuth: token malformado retorna 401', async () => {
  const validate = createHandler({ loadConfig: async () => config });
  assert.equal((await validate(forwardAuth({ authorization: 'Bearer abc' }))).statusCode, 401);
});
