# Oficina Auth Lambda

Function Serverless responsável por validar CPF, consultar o cliente no PostgreSQL,
verificar se o status é `ATIVO` e emitir JWT HS256. O mesmo endpoint valida Bearer
tokens para integração futura com o middleware ForwardAuth do Traefik.

## Execução

```bash
npm ci
npm test
terraform -chdir=terraform init
terraform -chdir=terraform plan -var-file=environments/homologation.tfvars
```

`db_secret_arn` aponta para o secret gerenciado pelo RDS e `jwt_secret_arn` para o
secret de assinatura criado pela infraestrutura do banco. A Lambda roda nas sub-redes
privadas com acesso ao RDS. O deploy usa GitHub Actions OIDC e environments de
homologação/produção.

## API

`POST /` com `{ "cpf": "52998224725" }` retorna `token`, `tokenType` e `expiresIn`.
CPF inválido retorna 400, cliente inexistente 404 e cliente não ativo 403.
