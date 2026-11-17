# Execution Service (`execution-service`)

Microsserviço responsável pela fila operacional de diagnóstico, reparo, finalização e entrega de veículos, persistindo documentos em banco **NoSQL (MongoDB)**.

## Tecnologias

- Java 21 + Spring Boot 3.2.5
- Spring Data **MongoDB** (NoSQL — `@Document(collection = "execucoes")`)
- RabbitMQ (`spring-boot-starter-amqp`) — publica `execucao.criada`, `execucao.iniciada`, `execucao.finalizada`, `execucao.entregue`, `execucao.cancelada`
- SpringDoc OpenAPI (Swagger) + Actuator + Prometheus + Logstash JSON (`X-Correlation-ID`)
- JUnit 5 + Mockito + JaCoCo (cobertura mínima de 80%)

## Arquitetura do Serviço

```mermaid
flowchart LR
    C[Operador / Saga Orchestrator] -->|REST :8083| API[ExecucaoController]
    API --> SVC[ExecucaoService]
    SVC --> DB[(MongoDB NoSQL - executiondb)]
    SVC -->|Publica execucao.*| MQ((RabbitMQ))
```

## Endpoints e Documentação

- Swagger UI: `http://localhost:8083/swagger-ui.html`
- Collection Postman: [`docs/postman/oficina-mecanica-fase4.postman_collection.json`](../../docs/postman/oficina-mecanica-fase4.postman_collection.json)
- `POST /api/execucoes` — Criar item na fila de execução
- `GET /api/execucoes` | `GET /api/execucoes/{id}` | `GET /api/execucoes/ordem/{ordemId}`
- `PATCH /api/execucoes/{id}/iniciar` | `/status` | `/finalizar` | `/entregar` | `/cancelar`

## Execução, Testes e Deploy

```bash
# Testes + verificação de cobertura JaCoCo >= 80%
mvn -B clean verify

# Docker & Kubernetes
docker build -t oficina-execution-service .
kubectl apply -f k8s/
```

## Evidências de Cobertura de Testes

Execução de `mvn -B clean verify` em 17/11/2026 — gate JaCoCo (`BUNDLE` / `LINE`) aprovado, build `EXIT=0`.

| Métrica | Resultado | Gate |
|---------|-----------|------|
| Cobertura de linhas | **98,6%** (137/139 linhas) | ≥ 80% ✅ |
| Testes executados | 4 testes | 0 falhas, 0 erros ✅ |

![Relatório JaCoCo — execution-service](docs/coverage-jacoco.png)

- Relatório HTML completo: `target/site/jacoco/index.html` (gerado por `mvn clean verify`)
- Gate executado no pipeline: [CI/CD Microsserviços (Fase 4)](https://github.com/daniloichaves/oficina-mecanica-tech/actions/workflows/fase4-microservices.yml)
