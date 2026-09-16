# Priority 2 - Billing Service

Este módulo representa o segundo passo da Fase 4: um microsserviço de orçamento e pagamento, com fluxo básico de criação, aprovação e pagamento.

## Como executar

```bash
cd fase4-priority2/billing-service
mvn spring-boot:run
```

## Endpoints principais

- `POST /api/orcamentos`
- `GET /api/orcamentos`
- `GET /api/orcamentos/{id}`
- `GET /api/orcamentos/ordem/{ordemId}`
- `PATCH /api/orcamentos/{id}/aprovar`
- `PATCH /api/orcamentos/{id}/rejeitar`
- `PATCH /api/orcamentos/{id}/pagar`

## Observação

A ideia aqui é manter uma base funcional para evoluir para a etapa seguinte de integração assíncrona e saga distribuída.
