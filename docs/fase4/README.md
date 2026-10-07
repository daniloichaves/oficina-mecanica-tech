# Fase 4 - Resumo Executivo

## Objetivo

Validar a separação funcional da solução em microsserviços para a operação de uma ordem de serviço, com foco em:

- responsabilidade por serviço
- comunicação síncrona via REST
- coordenação assíncrona via Saga
- compensação em cenários de rejeição ou falha

## Arquitetura proposta

### 1. OS Service
Local: `fase4-priority1/os-service`

Responsabilidade:
- criação da ordem de serviço
- consulta e listagem
- atualização de status

Endpoints principais:
- POST `/api/os`
- GET `/api/os`
- GET `/api/os/{id}`
- PATCH `/api/os/{id}/status`

### 2. Billing Service
Local: `fase4-priority2/billing-service`

Responsabilidade:
- geração e gestão do orçamento
- aprovação/rejeição do orçamento
- cobrança/pagamento

Endpoints principais:
- POST `/api/orcamentos`
- GET `/api/orcamentos`
- GET `/api/orcamentos/{id}`
- PATCH `/api/orcamentos/{id}/aprovar`
- PATCH `/api/orcamentos/{id}/rejeitar`
- PATCH `/api/orcamentos/{id}/pagar`

### 3. Execution Service
Local: `fase4-priority3/execution-service`

Responsabilidade:
- iniciar execução da ordem
- atualizar status da execução
- finalizar e entregar o serviço

Endpoints principais:
- POST `/api/execucoes`
- GET `/api/execucoes`
- GET `/api/execucoes/{id}`
- PATCH `/api/execucoes/{id}/iniciar`
- PATCH `/api/execucoes/{id}/status`
- PATCH `/api/execucoes/{id}/finalizar`
- PATCH `/api/execucoes/{id}/entregar`

### 4. Saga Orchestrator
Local: `fase4-priority4/saga-orchestrator`

Responsabilidade:
- coordenar o fluxo da ordem
- avançar etapas do estado da saga
- registrar compensação em cenários de rejeição

Endpoints principais:
- POST `/api/sagas/ordens/{ordemId}/iniciar`
- POST `/api/sagas/ordens/{ordemId}/eventos?evento=...`

## Fluxo do protótipo

1. Cria-se a ordem de serviço no OS Service
2. O orchestrator dispara a etapa de orçamento
3. O Billing Service aprova ou rejeita o orçamento
4. Se aprovada, a execução é iniciada
5. O Execution Service finaliza e entrega o serviço
6. A saga avança para `EXECUCAO_EM_ANDAMENTO` ou `FINALIZADA`
7. Se houver rejeição, a saga entra em compensação

## Evidências validadas

### Testes
Os módulos do Fase 4 foram validados com Maven em cada serviço:

- OS Service: `mvn test -q`
- Billing Service: `mvn test -q`
- Execution Service: `mvn test -q`
- Saga Orchestrator: `mvn test -q`

### Runtime
Os serviços foram iniciados e validados localmente nas portas:

- 8081 → OS Service
- 8082 → Billing Service
- 8083 → Execution Service
- 8084 → Saga Orchestrator

Exemplos de validação realizados:

```bash
curl -i -X POST http://localhost:8081/api/os \
  -H 'Content-Type: application/json' \
  -d '{"placaVeiculo":"ABC1234","clienteId":"cli-1","nomeCliente":"Joao","descricaoDefeito":"Freio travado"}'

curl -i -X POST http://localhost:8082/api/orcamentos \
  -H 'Content-Type: application/json' \
  -d '{"ordemId":1,"clienteId":"cli-1","valor":1500.00}'

curl -i -X POST http://localhost:8083/api/execucoes \
  -H 'Content-Type: application/json' \
  -d '{"ordemId":1,"clienteId":"cli-1","descricaoTarefa":"Troca de pastilhas"}'

curl -i -X POST http://localhost:8084/api/sagas/ordens/1/iniciar
```

## Observação de maturidade

Este é um protótipo funcional de microsserviços para a Fase 4. Ele valida a divisão de responsabilidades, os contratos de API e a coordenação de fluxo por Saga.

O que ainda exige evolução para a entrega final da disciplina:

- repositórios independentes por serviço
- banco de dados próprio por microsserviço
- CI/CD automatizado
- deploy em Kubernetes
- observabilidade distribuída
- versão final de documentação e apresentação

## Resumo decisório

A arquitetura atende ao objetivo didático da Fase 4: validar o desenho de microsserviços e a lógica de orquestração de transações distribuídas com compensação. A implementação atual é funcional, consistente e demonstrável localmente, sendo uma base sólida para evolução para a entrega final do desafio.
