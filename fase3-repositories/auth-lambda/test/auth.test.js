import test from 'node:test';
import assert from 'node:assert/strict';
import { isValidCpf } from '../src/cpf.js';
import { createHandler } from '../src/index.js';

const config = { jwtSecret: 'uma-chave-de-testes-com-pelo-menos-32-bytes' };
const event = (cpf) => ({ headers: {}, body: JSON.stringify({ cpf }) });

test('valida CPF pelos dígitos verificadores', () => {
  assert.equal(isValidCpf('529.982.247-25'), true);
  assert.equal(isValidCpf('111.111.111-11'), false);
});

test('emite token para cliente ativo', async () => {
  const handler = createHandler({
    loadConfig: async () => config,
    findClient: async () => ({ id: 42, cpf_cnpj: '52998224725', status: 'ATIVO' }),
    now: () => 1000
  });
  const result = await handler(event('52998224725'));
  const body = JSON.parse(result.body);
  assert.equal(result.statusCode, 200);
  assert.equal(body.tokenType, 'Bearer');
  assert.equal(body.expiresIn, 3600);
  assert.equal(body.token.split('.').length, 3);
});

test('recusa cliente bloqueado', async () => {
  const handler = createHandler({
    loadConfig: async () => config,
    findClient: async () => ({ id: 42, status: 'BLOQUEADO' })
  });
  assert.equal((await handler(event('52998224725'))).statusCode, 403);
});

test('valida bearer token no fluxo ForwardAuth', async () => {
  const login = createHandler({
    loadConfig: async () => config,
    findClient: async () => ({ id: 42, status: 'ATIVO' }),
    now: () => 1000
  });
  const token = JSON.parse((await login(event('52998224725'))).body).token;
  const validate = createHandler({ loadConfig: async () => config, now: () => 1001 });
  const result = await validate({ headers: { authorization: `Bearer ${token}` } });
  assert.equal(result.statusCode, 200);
  assert.equal(result.headers['x-auth-client-id'], '42');
});
