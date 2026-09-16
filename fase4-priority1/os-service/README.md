# Priority 1 - OS Service

Este módulo é o primeiro passo prático da Fase 4: um serviço independente de ordem de serviço, com estado e API mínima para evoluir em seguida para cobrança, execução e integração assíncrona.

## Como executar

```bash
cd fase4-priority1/os-service
mvn spring-boot:run
```

## Endpoints principais

- `POST /api/os`
- `GET /api/os/{id}`
- `GET /api/os`
- `PATCH /api/os/{id}/status`

## Observação

O objetivo aqui é manter um MVP funcional e claro, sem tentar construir a arquitetura completa de uma vez.
