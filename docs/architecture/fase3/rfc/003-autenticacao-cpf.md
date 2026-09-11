# RFC 003 — Autenticação serverless por CPF

**Status:** Aceita

A Lambda valida os dígitos do CPF, consulta o cliente por SQL parametrizado, exige
status `ATIVO` e emite JWT HS256 de curta duração. A chave fica no Secrets Manager e
é compartilhada somente com os validadores autorizados. O token contém CPF como
subject, `clientId`, tipo, emissão e expiração.

O CPF isolado atende ao enunciado, mas é um fator de conhecimento público e não seria
suficiente para um produto financeiro ou com dados sensíveis. Uma evolução recomendada
é confirmação por OTP enviado ao contato cadastrado, sem alterar o contrato JWT.
