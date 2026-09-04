import crypto from 'node:crypto';

const encode = (value) => Buffer.from(JSON.stringify(value)).toString('base64url');

export function signJwt(payload, secret, ttlSeconds, nowSeconds = Math.floor(Date.now() / 1000)) {
  const header = encode({ alg: 'HS256', typ: 'JWT' });
  const claims = encode({ ...payload, iat: nowSeconds, exp: nowSeconds + ttlSeconds });
  const content = `${header}.${claims}`;
  const signature = crypto.createHmac('sha256', secret).update(content).digest('base64url');
  return `${content}.${signature}`;
}

export function verifyJwt(token, secret, nowSeconds = Math.floor(Date.now() / 1000)) {
  const parts = String(token ?? '').split('.');
  if (parts.length !== 3) throw new Error('Token inválido');
  const [header, payload, signature] = parts;
  const expected = crypto.createHmac('sha256', secret).update(`${header}.${payload}`).digest();
  const received = Buffer.from(signature, 'base64url');
  if (received.length !== expected.length || !crypto.timingSafeEqual(received, expected)) {
    throw new Error('Assinatura inválida');
  }
  const claims = JSON.parse(Buffer.from(payload, 'base64url').toString('utf8'));
  if (!claims.exp || claims.exp <= nowSeconds) throw new Error('Token expirado');
  return claims;
}
