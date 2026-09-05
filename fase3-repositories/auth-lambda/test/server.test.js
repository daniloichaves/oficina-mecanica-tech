import test from 'node:test';
import assert from 'node:assert/strict';
import { createServer } from '../src/server.js';
import { createHandler } from '../src/index.js';

const config = { jwtSecret: 'uma-chave-de-testes-com-pelo-menos-32-bytes' };

async function withServer(handler, fn) {
  const server = createServer(handler);
  await new Promise((resolve) => server.listen(0, resolve));
  const base = `http://127.0.0.1:${server.address().port}`;
  try {
    await fn(base);
  } finally {
    await new Promise((resolve) => server.close(resolve));
  }
}

test('servidor local: /health responde sem autenticação', async () => {
  await withServer(createHandler({ loadConfig: async () => config }), async (base) => {
    const res = await fetch(`${base}/health`);
    assert.equal(res.status, 200);
  });
});

test('servidor local: POST /auth com CPF emite token e GET /auth valida', async () => {
  const handler = createHandler({
    loadConfig: async () => config,
    findClient: async () => ({ id: 7, status: 'ATIVO' })
  });
  await withServer(handler, async (base) => {
    const loginRes = await fetch(`${base}/auth`, {
      method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({ cpf: '52998224725' })
    });
    assert.equal(loginRes.status, 200);
    const { token } = await loginRes.json();

    const ok = await fetch(`${base}/auth`, { headers: { authorization: `Bearer ${token}` } });
    assert.equal(ok.status, 200);
    assert.equal(ok.headers.get('x-auth-client-id'), '7');

    const missing = await fetch(`${base}/auth`);
    assert.equal(missing.status, 401);
    assert.match(missing.headers.get('www-authenticate'), /Bearer/);
  });
});
