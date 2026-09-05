# Oficina Auth Lambda

Function Serverless responsável por validar CPF, consultar o cliente no PostgreSQL,
verificar se o status é `ATIVO` e emitir JWT HS256. O mesmo handler valida Bearer
tokens para o middleware ForwardAuth do Traefik, protegendo as rotas `/api/**` no gateway.

## Tecnologias

Node.js 22 (ESM, sem framework), `pg`, AWS SDK v3 (Secrets Manager), Terraform (AWS Lambda +
Function URL), GitHub Actions com OIDC.

## API

| Requisição | Resposta |
|------------|----------|
| `POST /` `{ "cpf": "52998224725" }` — cliente ativo | 200 `{ "token", "tokenType": "Bearer", "expiresIn": 3600 }` |
| `POST /` CPF inválido / JSON inválido | 400 |
| `POST /` CPF sem cadastro | 404 |
| `POST /` cliente `INATIVO`/`BLOQUEADO` | 403 |
| `GET /` `Authorization: Bearer <jwt>` válido (ForwardAuth) | 200 + `X-Auth-Client-Id`, `X-Auth-Cpf` |
| `GET /` sem token, expirado, adulterado, `alg≠HS256`, sem `type=access` | 401 + `WWW-Authenticate: Bearer` |
| Falha interna | 500 (detalhe apenas no log JSON) |

Claims do token: `sub` (CPF), `clientId`, `type=access`, `iat`, `exp`.

## Execução

```bash
npm ci
npm test                      # unitários + adapter HTTP (node --test)

# servidor local (mesmo handler, sem AWS) — usado no docker-compose e no kind
JWT_SECRET=... DB_HOST=localhost DB_USER=oficina DB_PASSWORD=oficina123 DB_SSL=false node src/server.js
# docker build -f Dockerfile.local -t oficina-auth-lambda .

terraform -chdir=terraform init
terraform -chdir=terraform plan -var-file=environments/homologation.tfvars
```

## Configuração

| Variável | Uso |
|----------|-----|
| `DB_SECRET_ARN`, `JWT_SECRET_ARN` | AWS: segredos do RDS e da chave JWT no Secrets Manager |
| `JWT_SECRET`, `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | modo local (ativado pela presença de `JWT_SECRET`) |
| `DB_SSL` | `false` desativa TLS (apenas local) |
| `DB_SSL_CA_PATH` | caminho do bundle de CA do RDS para `rejectUnauthorized: true` |
| `JWT_EXPIRATION_SECONDS` | validade do token (padrão 3600) |
| `PORT` | porta do adapter HTTP (padrão 8081) |

O segredo JWT precisa ser o mesmo configurado na aplicação principal (`JWT_SECRET`), pois
o Spring Security valida o token novamente após o gateway.

`db_secret_arn` aponta para o secret gerenciado pelo RDS e `jwt_secret_arn` para o
secret de assinatura criado pela infraestrutura do banco. A Lambda roda nas sub-redes
privadas com acesso ao RDS. O deploy usa GitHub Actions OIDC e environments de
homologação/produção.

## Integração com o Traefik

Middleware `forwardAuth` apontando para a Function URL (AWS) ou para o Service
`auth-lambda:8081/auth` (kind), com `authRequestHeaders: [Authorization, X-Correlation-ID]` e
`authResponseHeaders: [X-Auth-Client-Id, X-Auth-Cpf, X-Correlation-ID]`. Documentação completa
dos fluxos em `docs/architecture/fase3/autenticacao.md` do repositório da aplicação.
