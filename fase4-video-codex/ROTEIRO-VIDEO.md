# Roteiro de gravação — Fase 4

Leia somente os trechos **Falar**. Execute as instruções **Mostrar**. Duração prevista: 13 a 14 minutos, com limite de 15 minutos.

## Antes de gravar

Organize as abas que já estão abertas:

| Aba | Tela | URL |
| --- | --- | --- |
| 1 | OS | http://localhost:8081/swagger-ui/index.html |
| 2 | Billing | http://localhost:8082/swagger-ui/index.html |
| 3 | Execution | http://localhost:8083/swagger-ui/index.html |
| 4 | Saga | http://localhost:8084/swagger-ui/index.html |
| 5 | RabbitMQ | http://localhost:15672 |
| 6 | Grafana | http://localhost:3000/d/oficina-fase4 |
| 7 | Prometheus | http://localhost:9090/targets |

Autentique-se em RabbitMQ e Grafana usando SOMENTE configurações locais do `.env`. Não grave ou publique credenciais. No Grafana selecione **Last 15 minutes** e atualização de **5s**. Deixe também o diagrama de `docs/architecture/fase4/README.md` renderizado.

No Swagger, o padrão é: expandir a operação → **Try it out** → preencher → **Execute** → descer até **Server response / Response body**. Mostre a resposta real do servidor, não o exemplo da documentação.

Anote os valores conforme aparecerem:

| Nome no roteiro | Valor a copiar |
| --- | --- |
| OS_A | `id` da primeira OS criada no vídeo |
| ORC_A | `orcamentoId` da Saga da primeira OS |
| EXEC_A | `execucaoId` da Saga depois do pagamento |
| OS_B | `id` da segunda OS |
| ORC_B | `orcamentoId` da Saga da segunda OS |

Substitua OS_A, ORC_A etc. pelos valores copiados. OS e orçamento usam números. Execução usa um UUID inteiro. Os registros 1 e 2 já foram usados no ensaio; crie novos registros, sem presumir o próximo ID.

Aguarde alguns segundos entre uma ação e a consulta seguinte. Os eventos são assíncronos; se ainda aparecer o estado anterior, execute a consulta novamente.

**Limitações confirmadas no ensaio:**

- Aprovação e pagamento criam duas execuções por OS. Acompanhe o ID referenciado pela Saga depois do pagamento e explique a limitação.
- A compensação financeira e da OS não cancela automaticamente as execuções já criadas.
- Pagamento e estorno são simulações locais, sem chamada externa ao Mercado Pago.
- Grafana coleta métricas reais. Traces APM e correlação completa por X-Correlation-ID não foram validados.
- Testes atuais, cobertura, Sonar e deploy Kubernetes não foram validados neste provisionamento. O trecho 12 exige evidências reais antes da gravação final.

## 1. Apresentação — 00:00 a 00:40

**Mostrar:** diagrama da arquitetura ou README com o nome do projeto.

**Falar:**

> Olá, somos do grupo 267 composta pelos seguintes integrates Danilo Ischiavolini,Rodrigo Bragantini e William Almeida. Vou apresentar a Fase 4 do projeto de oficina mecânica. O foco é a arquitetura de microsserviços e a coordenação das transações distribuídas com Saga Pattern. Vamos acompanhar uma ordem de serviço, uma falha com compensação e as métricas no dashboard.
>
> A demonstração acontece em ambiente local, com serviços e bancos em containers. O pagamento e o estorno serão simulados pelo adaptador local.

## 2. Arquitetura — 00:40 a 01:50

**Mostrar:** apontar no diagrama OS, Billing, Execution, Saga, bancos e RabbitMQ. Mostrar repositórios separados somente se estiverem publicados.

**Falar:**

> Temos três serviços de negócio. O OS recebe e acompanha as ordens. O Billing concentra orçamento e pagamento. O Execution gerencia as tarefas de execução. Um quarto serviço, o Saga Orchestrator, coordena o avanço das etapas e as compensações.
>
> OS, Billing e Saga usam bancos PostgreSQL próprios. O Execution usa MongoDB. A integração acontece por APIs REST e eventos no RabbitMQ, sem acesso direto ao banco de outro serviço.
>
> Escolhemos a Saga orquestrada para centralizar as decisões de avanço e compensação e manter um histórico consultável por ordem de serviço.

## 3. Dashboard antes do fluxo — 01:50 a 02:20

**Mostrar:** aba 6 → painel **Serviços disponíveis / 4** e contadores de Sagas. Observe os valores iniciais; não precisam começar em zero.

**Falar:**

> Temos quatro serviços disponíveis. Os contadores mostram as Sagas iniciadas, finalizadas e compensadas durante a execução atual das aplicações. Vamos voltar aqui após os dois cenários para observar a mudança.

## 4. Abrir a primeira OS — 02:20 a 03:00

**Mostrar:** aba 1 → `POST /api/os` → **Try it out** → colar o JSON → **Execute**.

```json
{
  "placaVeiculo": "ABC1234",
  "clienteId": "cli-video-sucesso",
  "nomeCliente": "Cliente Demonstracao",
  "descricaoDefeito": "Revisao do sistema de freios"
}
```

Mostrar HTTP **201** e copiar `id` para **OS_A**. O estado inicial pode aparecer como `RECEBIDA`.

**Falar:**

> Vou abrir uma ordem para revisão dos freios. A API retornou o identificador da ordem. A criação publica um evento no RabbitMQ, permitindo ao orquestrador iniciar a Saga e solicitar o orçamento.

## 5. Mostrar o orçamento automático — 03:00 a 03:50

**Mostrar:**

1. Aba 4 → `GET /api/sagas/ordens/{ordemId}` → **Try it out** → preencher **OS_A** → **Execute**.
2. Mostrar `ORCAMENTO_EM_ANDAMENTO` e `historicoEtapas`. Copiar `orcamentoId` para **ORC_A**.
3. Aba 2 → `GET /api/orcamentos/{id}` → preencher **ORC_A** → **Execute**.
4. Mostrar `ordemId`, `valor` e `status: PENDENTE`.

**Falar:**

> Consultando a Saga pelo ID da ordem, vemos o orçamento gerado e o histórico inicial. No Billing, esse orçamento está pendente e associado à mesma ordem. Não cadastramos um orçamento manualmente: essa etapa foi coordenada entre os serviços.

O orçamento automático usa valor padrão de R$ 500,00 e pode mostrar `cliente-padrao`. Não afirmar cálculo de peças ou propagação correta do cliente nesta versão.

## 6. Aprovar e pagar — 03:50 a 04:40

**Mostrar:** aba 2:

1. `PATCH /api/orcamentos/{id}/aprovar` → **Try it out** → **ORC_A** → **Execute**. Mostrar `APROVADO`.
2. `PATCH /api/orcamentos/{id}/pagar` → mesmo ID → **Execute**. Mostrar `PAGO`, `paymentId`, `paymentStatus` e `paymentProvider`.

**Falar:**

> Agora aprovamos o orçamento e registramos o pagamento simulado. O identificador com prefixo mp-sandbox é gerado localmente, sem cobrança externa. Com esse resultado, acompanhamos como a aplicação reage à confirmação de pagamento.

## 7. Executar e finalizar — 04:40 a 06:10

**Mostrar:**

1. Aba 4 → consultar novamente a Saga de **OS_A**, até aparecer `EXECUCAO_EM_ANDAMENTO`.
2. Copiar o campo `execucaoId` inteiro para **EXEC_A**.
3. Aba 3 → `GET /api/execucoes/{id}` → **EXEC_A** → **Execute**. Mostrar a tarefa e `ordemId`.
4. `PATCH /api/execucoes/{id}/iniciar` → **EXEC_A** → **Execute**. Mostrar `EM_EXECUCAO`.
5. `PATCH /api/execucoes/{id}/finalizar` → **EXEC_A** → **Execute**. Mostrar `FINALIZADA`.
6. Aba 1 → `GET /api/os/{id}` com **OS_A** → **Execute**. Aguardar o evento e mostrar `FINALIZADA`.
7. Aba 4 → consultar a Saga de **OS_A**. Mostrar `FINALIZADA` e `SAGA_FINALIZADA` no histórico.

**Falar:**

> A Saga aponta a execução que vamos acompanhar. Consultamos a tarefa no Execution Service, registramos o início e depois a conclusão.
>
> A conclusão gera um evento, e o orquestrador atualiza a ordem. Agora vemos a finalização tanto na OS quanto na Saga, com as etapas registradas no histórico.
>
> Uma limitação desta versão é que aprovação e pagamento podem gerar duas tarefas para a mesma OS. Estamos acompanhando a tarefa referenciada pela Saga; a deduplicação desses eventos ainda precisa ser corrigida.

Não usar o número da OS no campo de ID da execução. Não é necessário chamar `/entregar` neste roteiro.

## 8. Preparar a segunda OS — 06:10 a 07:10

**Mostrar:** aba 1 → executar novamente `POST /api/os`:

```json
{
  "placaVeiculo": "DEF5678",
  "clienteId": "cli-video-falha",
  "nomeCliente": "Cliente Compensacao",
  "descricaoDefeito": "Reparo com falha simulada na execucao"
}
```

1. Copiar o novo `id` para **OS_B**.
2. Aba 4 → consultar a Saga de **OS_B**. Copiar `orcamentoId` para **ORC_B**.
3. Aba 2 → aprovar **ORC_B** com `/aprovar` e pagar com `/pagar`.
4. Aba 4 → consultar **OS_B** até mostrar `EXECUCAO_EM_ANDAMENTO`. Só então disparar a falha.

**Falar:**

> Para demonstrar a compensação, abrimos outra ordem, aprovamos seu orçamento e registramos o pagamento simulado. Usamos uma ordem diferente para separar os cenários de sucesso e falha.

## 9. Falha e compensação — 07:10 a 08:40

**Mostrar:** aba 4 → `POST /api/sagas/ordens/{ordemId}/eventos` → **Try it out**:

- `ordemId`: valor de **OS_B**.
- `evento`: exatamente `EXECUCAO_FALHOU`.
- Clicar em **Execute**.

Mostrar `COMPENSADA` e as etapas `FALHA_EXECUCAO`, `COMPENSACAO_ORCAMENTO_ESTORNADO` e `COMPENSACAO_OS_CANCELADA`.

Depois:

1. Aba 2 → `GET /api/orcamentos/{id}` com **ORC_B** → mostrar `ESTORNADO` e `paymentStatus: refunded`.
2. Aba 1 → `GET /api/os/{id}` com **OS_B** → mostrar `CANCELADA`.

**Falar:**

> Vou reportar uma falha pela API do orquestrador. É uma injeção controlada de evento para demonstrar o tratamento de falhas, sem derrubar a infraestrutura.
>
> A Saga solicita a compensação ao Billing e o cancelamento ao OS. Confirmamos o resultado nos próprios serviços: o orçamento está estornado e a ordem está cancelada. O estorno financeiro também é simulado.
>
> Cada serviço realiza sua própria operação. A Saga coordena ações de negócio que compensam etapas anteriores, em vez de usar uma única transação de banco entre todos os serviços.
>
> Nesta versão, as tarefas de execução já criadas não são canceladas automaticamente. O que comprovamos aqui é o estorno simulado e o cancelamento da OS.

Não chamar manualmente `/compensar` no Billing nem `/cancelar` no OS: isso esconderia a coordenação da Saga. Mostrar as consultas independentes; o status da Saga sozinho não comprova compensação.

## 10. Mensageria e métricas — 08:40 a 10:40

**Mostrar:**

1. Aba 5 → **Exchanges** → `oficina.events` → mostrar os bindings.
2. **Queues and Streams** → `saga-orchestrator.events` → mostrar consumidor e indicadores.
3. Aba 7 → mostrar quatro targets **UP**.
4. Aba 6 → mostrar os quatro serviços e os contadores de Sagas.
5. Rolar para gráficos de requisições de negócio, latência, erros, CPU e memória.

Após os cenários, o esperado é aumento de **2 iniciadas, 1 finalizada e 1 compensada** em relação ao início, se não houve outras operações ou reinício. Para tráfego recente, consultar OS e Saga algumas vezes pelo Swagger.

**Falar:**

> No RabbitMQ vemos o exchange e a fila consumida pelo orquestrador. A fila pode estar vazia porque os eventos já foram processados. Os logs complementam essa visualização.
>
> O Prometheus coleta os quatro serviços, e o Grafana atualiza os painéis a cada cinco segundos. Aqui vemos a disponibilidade e os contadores dos fluxos executados.
>
> Os gráficos mostram o tráfego das APIs, a latência média, erros HTTP, memória e CPU. Essas métricas ajudam a identificar indisponibilidade, lentidão e mudanças na operação.

Os contadores reiniciam junto com a aplicação. Gráficos de taxa precisam de pelo menos um minuto de coleta e podem voltar a zero sem novas chamadas.

## 11. Relacionar as etapas — 10:40 a 11:20

**Mostrar:** aba 4 → consultar **OS_B** → destacar `ordemId`, `orcamentoId`, `execucaoId` e `historicoEtapas`.

Se incluir logs, deixar um terminal preparado antes da gravação, na raiz:

```powershell
$env:PATH = 'C:\Program Files\Docker\Docker\resources\bin;' + $env:PATH
docker compose -f docker-compose.fase4.yml logs --tail 100 saga-orchestrator
```

Mostrar mensagens de eventos e ações da OS demonstrada, sem ler todo o log.

**Falar:**

> Pelo ID da ordem consultamos o histórico e identificamos o orçamento e a execução relacionados. Os logs complementam essa consulta com os eventos recebidos e as ações do orquestrador.
>
> O dashboard apresentado coleta métricas. Traces distribuídos de APM ainda não estão configurados neste ambiente.

Não apresentar o histórico da Saga como um trace APM nem afirmar correlação completa por X-Correlation-ID.

## 12. Testes, qualidade e deploy — 11:20 a 13:10

**Este trecho depende de evidências ainda não validadas no provisionamento local.** As URLs abertas não comprovam CI/CD, cobertura, Sonar ou Kubernetes.

**Mostrar, se as evidências reais estiverem disponíveis:**

1. GitHub → repositório do serviço → **Actions** → execução do commit implantado.
2. Testes aprovados e relatórios JaCoCo dos quatro serviços, com percentuais medidos.
3. Sonar executado e resultado, sem etapa pulada.
4. Deploy e rollout Kubernetes concluídos e imagem da versão implantada.
5. Feature em `fase4-priority4/saga-orchestrator/src/test/resources/features/saga_ordem_servico.feature`, apontando sucesso e compensação.

**Falar somente com os resultados na tela:**

> Nesta execução podemos acompanhar os testes, a análise de qualidade e o deploy. Os relatórios apresentam a cobertura medida por serviço. O rollout Kubernetes e a imagem permitem identificar a versão implantada.
>
> Também temos cenários BDD em Gherkin para as transições da Saga e as compensações. A suíte usa gateway local; a demonstração das APIs complementa os testes com os serviços integrados em execução.

**Se ainda não houver essas evidências:** para um ensaio, mostrar a feature e os arquivos dos pipelines e dizer:

> O repositório contém cenários BDD e configuração dos pipelines. Neste ensaio local, ainda não demonstramos a execução das verificações de qualidade nem o deploy automatizado em Kubernetes.

Essa alternativa descreve o ensaio, mas não substitui o requisito obrigatório do vídeo final. O build do Compose usa `-DskipTests`; não apresentá-lo como execução de testes.

## 13. Encerramento — 13:10 a 13:40

**Mostrar:** diagrama ou dashboard. Mostrar documento de entrega com links se já estiver pronto.

**Falar:**

> Demonstramos o fluxo de uma ordem entre os microsserviços, sua finalização e uma falha com compensação financeira simulada e cancelamento da OS. Acompanhamos as métricas no dashboard e as etapas no histórico da Saga. A documentação reúne as APIs, a arquitetura e a justificativa da estratégia de orquestração. Obrigado.

## Cola rápida

| Passo | Aba | Ação |
| --- | --- | --- |
| 1 | Diagrama | Explicar serviços, bancos e Saga |
| 2 | Grafana | Mostrar valores iniciais |
| 3 | OS | Criar OS_A |
| 4 | Saga | Obter ORC_A |
| 5 | Billing | Consultar, aprovar e pagar ORC_A |
| 6 | Saga | Obter EXEC_A após pagamento |
| 7 | Execution | Consultar, iniciar e finalizar EXEC_A |
| 8 | OS + Saga | Confirmar FINALIZADA |
| 9 | OS | Criar OS_B |
| 10 | Saga + Billing | Obter ORC_B, aprovar e pagar |
| 11 | Saga | Aguardar execução e enviar EXECUCAO_FALHOU |
| 12 | Billing + OS | Mostrar ESTORNADO e CANCELADA |
| 13 | RabbitMQ + Prometheus | Mostrar integração e coleta |
| 14 | Grafana + Saga | Mostrar métricas e histórico |
| 15 | Actions / evidências | Testes, qualidade e Kubernetes, se comprovados |
| 16 | Diagrama | Encerrar |

## Depois de gravar

- Conferir duração máxima de 15 minutos e legibilidade das respostas.
- Publicar no YouTube ou Vimeo. O enunciado aceita público ou não listado; o grupo pediu público.
- Adicionar o link real ao README, TODO e PDF.
- Incluir no PDF participantes, repositórios separados, vídeo, diagrama e justificativas.
- Confirmar proteção da main e demais requisitos antes da entrega.

## Para iniciar novamente

Com Docker Desktop aberto, na raiz:

```powershell
$env:PATH = 'C:\Program Files\Docker\Docker\resources\bin;' + $env:PATH
docker compose -f docker-compose.fase4.yml -f docker-compose.fase4-observability.yml up -d
```

Instruções do dashboard: `docs/observability/fase4/README.md`.
