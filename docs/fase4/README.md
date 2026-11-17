# Fase 4 - Resumo Executivo

## Objetivo

Refatorar a solução para arquitetura de microsserviços independentes com gestão transacional distribuída via **Saga Pattern Orquestrado**, bancos próprios (**PostgreSQL SQL** e **MongoDB NoSQL**), mensageria assíncrona (**RabbitMQ**), integração de pagamento (**Mercado Pago**), testes unitários + **BDD (Cucumber)** com cobertura JaCoCo ≥ 80%, Docker, Kubernetes e CI/CD.

## Arquitetura Implementada

Documentação completa, diagramas e ADRs em [`docs/architecture/fase4/README.md`](../architecture/fase4/README.md).

### 1. OS Service (`fase4-priority1/os-service`) — PostgreSQL (SQL)
- Abertura, consulta por ID/status, atualização de status e cancelamento por compensação.
- Publica `os.criada` / `os.status.atualizado` no RabbitMQ e consome comandos `saga.os.*`.
- Swagger: `http://localhost:8081/swagger-ui.html`

### 2. Billing Service (`fase4-priority2/billing-service`) — PostgreSQL (SQL) + Mercado Pago
- Geração, aprovação, rejeição, pagamento/estorno integrado ao Mercado Pago (`MercadoPagoPaymentGateway`) e webhook `/api/orcamentos/webhook/mercadopago`.
- Publica `orcamento.*` no RabbitMQ e expõe `/api/orcamentos/{id}/compensar` para rollback financeiro.
- Swagger: `http://localhost:8082/swagger-ui.html`

### 3. Execution Service (`fase4-priority3/execution-service`) — MongoDB (NoSQL)
- Fila de execução persistida em **MongoDB** (`@Document(collection = "execucoes")`).
- Transições `AGUARDANDO` → `EM_EXECUCAO` → `FINALIZADA` → `ENTREGUE` / `CANCELADA` + eventos RabbitMQ `execucao.*`.
- Swagger: `http://localhost:8083/swagger-ui.html`

### 4. Saga Orchestrator (`fase4-priority4/saga-orchestrator`) — PostgreSQL (SQL) + RabbitMQ + REST
- Orquestrador central do fluxo distribuído com persistência de histórico (`historicoEtapas`) e `X-Correlation-ID`.
- Executa compensação automática (estorno no Billing + cancelamento da OS) quando há rejeição de orçamento ou falha em etapa downstream.
- Suíte **BDD (Cucumber)** cobrindo fluxo completo e cenários de compensação.
- Swagger: `http://localhost:8084/swagger-ui.html`

## Execução com Docker Compose (Bancos SQL + NoSQL + RabbitMQ)

```bash
docker compose -f docker-compose.fase4.yml up --build -d
```

## Evidências validadas (`mvn -B clean verify`)

Todos os 4 serviços executam build, testes unitários, BDD (no `saga-orchestrator`) e validação obrigatória de cobertura **JaCoCo ≥ 80%**:

- `mvn -B -f fase4-priority1/os-service/pom.xml clean verify`
- `mvn -B -f fase4-priority2/billing-service/pom.xml clean verify`
- `mvn -B -f fase4-priority3/execution-service/pom.xml clean verify`
- `mvn -B -f fase4-priority4/saga-orchestrator/pom.xml clean verify`
