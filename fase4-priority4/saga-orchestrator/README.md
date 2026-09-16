# Saga Orchestrator

Protótipo de mensageria e saga para a Fase 4.

## Objetivo

Validar o fluxo de eventos de uma ordem de serviço em da ordem:

1. ordem criada
2. orçamento em andamento
3. orçamento aprovado
4. execução em andamento
5. execução finalizada
6. compensação em caso de rejeição

## Endpoints principais

- POST /api/sagas/ordens/{ordemId}/iniciar
- POST /api/sagas/ordens/{ordemId}/eventos?evento=ORCAMENTO_APROVADO
- POST /api/sagas/ordens/{ordemId}/eventos?evento=ORCAMENTO_REJEITADO

## Execução local

```bash
cd fase4-priority4/saga-orchestrator
mvn spring-boot:run
```

## Validação

```bash
mvn test -q && echo 'SAGA_ORCHESTRATOR_TESTS_OK'
```
