# Mapa operacional — vídeo Fase 4

Cenas derivadas de `ROTEIRO-VIDEO.md`. Tempo total **13min40s**; teto **15min**. A narração é extraída da origem, exceto a fala alternativa da cena 12 no modo ensaio.

| Nº | Janela | Tela e ações Playwright | Condição verificada |
|---:|---|---|---|
| 1 | 00:00–00:40 | Diagrama/README arquitetura | Fonte local existe |
| 2 | 00:40–01:50 | Destacar OS, Billing, Execution, Saga, bancos e RabbitMQ | Sem repositórios inexistentes |
| 3 | 01:50–02:20 | Grafana, availability e contadores prévios | Não presumir contadores zerados |
| 4 | 02:20–03:00 | Swagger OS POST `/api/os` corpo A | HTTP 201, `OS_A` do JSON retornado |
| 5 | 03:00–03:50 | Swagger Saga GET OS_A, Billing GET ORC_A | `orcamentoId` numérico, resposta real, PENDENTE |
| 6 | 03:50–04:40 | Swagger Billing PATCH aprovar e pagar ORC_A | APROVADO/PAGO e metadados reais do pagamento |
| 7 | 04:40–06:10 | Saga GET, Execution GET/PATCH iniciar e finalizar, OS GET, Saga GET | `EXEC_A` UUID; execução FINALIZADA; OS e Saga FINALIZADA |
| 8 | 06:10–07:10 | Swagger OS POST corpo B, Saga GET, Billing PATCH aprovar+pagar | OS_B e ORC_B novos; Saga EXECUCAO_EM_ANDAMENTO |
| 9 | 07:10–08:40 | Saga POST evento `EXECUCAO_FALHOU`; Saga GET; Billing GET; OS GET | Saga COMPENSADA com histórico; Billing ESTORNADO/refunded; OS CANCELADA |
| 10 | 08:40–10:40 | RabbitMQ Exchange/Queue; Prometheus targets; Grafana gráficos | Evidências reais na interface e sem inventar contagens |
| 11 | 10:40–11:20 | Saga GET por OS_B; opcional logs seguros do docker-compose | IDs e `historicoEtapas` reais; sem alegar tracing APM |
| 12 | 11:20–13:10 | Ensaio: feature Gherkin + workflows existentes | **Não comprova execução de CI/CD / Sonar / K8s**. Final: bloqueado até provas reais |
| 13 | 13:10–13:40 | Diagrama/dashboard e encerramento | Duração < 15min |

## IDs dinâmicos (nunca valores fixos)

- `OS_A`: campo `id` do POST de criação da primeira ordem
- `ORC_A`: `orcamentoId` retornado pela Saga da primeira OS
- `EXEC_A`: UUID completo `execucaoId` retornado pela Saga depois do pagamento
- `OS_B`: ID novo da segunda OS
- `ORC_B`: `orcamentoId` da segunda Saga

## Cliques/seletor (a adaptar via inspeção real)

No Swagger UI: `.swagger-ui .opblock`, `.opblock-summary-method`, `.opblock-summary-path`, `.opblock-summary`, `button.try-out__btn`, `tr.parameters`, `textarea.body-param__text`, `button.execute`, `.responses-wrapper .live-responses-table` e `.response-col_status`. `page.waitForResponse` identifica a operação e método antes de confiar na resposta visível.

**Atenção:** controles do Grafana e RabbitMQ variam por versão. Este pacote possui tentativas genéricas; o Codex deve inspecionar e substituir por seletores verificados. Para o evento de falha, confirme se `evento` é parâmetro ou corpo, antes de autorizar POST. Para material final, não basta colocar URLs de Actions/Sonar; é preciso exibir resultados reais.

## Regras de sincronização

1. Uma fala sintética por cena é preparada antes da gravação.
2. Cada cena começa com áudio sincronizado ao início da ação de navegação; o script segura a tela até a duração prevista quando ações terminam antes do tempo.
3. Consultas assíncronas podem demorar mais que o planejado. Se passar 15min, o processo aborta em vez de entregar arquivo incompatível.
4. Sincronização refinada de cada clique com frases individuais requer o Codex subdividir as cenas após observar o comportamento real do ambiente. O pacote faz sincronização **por cena**; não promete correspondência palavra a palavra.
