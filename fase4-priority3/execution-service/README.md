# Priority 3 - Execution Service

Este módulo representa a terceira etapa da Fase 4: um protótipo do serviço de execução/produção, responsável por acompanhar a etapa operacional da ordem de serviço.

## Como executar

```bash
cd fase4-priority3/execution-service
mvn spring-boot:run
```

## Endpoints principais

- `POST /api/execucoes`
- `GET /api/execucoes`
- `GET /api/execucoes/{id}`
- `GET /api/execucoes/ordem/{ordemId}`
- `PATCH /api/execucoes/{id}/iniciar`
- `PATCH /api/execucoes/{id}/status`
- `PATCH /api/execucoes/{id}/finalizar`
- `PATCH /api/execucoes/{id}/entregar`

## Observação

Esse serviço é um ponto de partida para a cadeia distribuída da Fase 4, antes da introdução de mensageria, compensação e integração completa com OS Service e Billing Service.
