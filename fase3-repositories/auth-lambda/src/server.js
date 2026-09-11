import http from 'node:http';
import { pathToFileURL } from 'node:url';
import { handler as lambdaHandler } from './index.js';

/**
 * Adapter HTTP para executar a mesma função fora da AWS (docker-compose, kind, CI).
 * Converte a requisição HTTP em um evento no formato Lambda Function URL (payload v2).
 *   GET /health → 200 (sem autenticação)
 *   demais rotas → handler (login por CPF ou validação ForwardAuth)
 */
export function createServer(handler = lambdaHandler) {
  return http.createServer(async (req, res) => {
    if (req.method === 'GET' && req.url === '/health') {
      res.writeHead(200, { 'content-type': 'application/json' });
      res.end('{"status":"UP"}');
      return;
    }

    const chunks = [];
    for await (const chunk of req) chunks.push(chunk);
    const body = Buffer.concat(chunks).toString('utf8');
    const url = new URL(req.url, 'http://localhost');

    const event = {
      version: '2.0',
      rawPath: url.pathname,
      rawQueryString: url.search.slice(1),
      headers: req.headers,
      requestContext: { http: { method: req.method, path: url.pathname } },
      body: body.length > 0 ? body : undefined,
      isBase64Encoded: false
    };

    const result = await handler(event);
    res.writeHead(result.statusCode, result.headers ?? {});
    res.end(result.body ?? '');
  });
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  const port = Number(process.env.PORT ?? 8081);
  createServer().listen(port, () => {
    console.log(JSON.stringify({ level: 'info', message: `auth server listening on ${port}` }));
  });
}
