# Validação local — vídeo de ensaio da Fase 4

Execução em 17/11/2026, no Windows. Narração por voz sintética local Microsoft Maria Desktop, em pt-BR. A voz deve ser identificada como sintética na distribuição.

## Resultado do arquivo final

- Arquivo: `saida/video-fase4.mp4`.
- Duração medida por ffprobe: **820,24 segundos (13min40,24s)**, dentro do limite de 15 minutos.
- Tamanho: 24.641.415 bytes, aproximadamente 23,5 MiB.
- Vídeo H.264, resolução real da tela 1366 × 768; áudio AAC mono, 22050 Hz.
- Decodificação completa do arquivo revisado concluída sem erro.
- Áudio inicial medido: média -23,6 dB e pico -2,7 dB; não silencioso e sem pico acima de 0 dB. A revisão preservou a faixa de áudio.
- Frames reais de Swagger, Grafana e RabbitMQ conferidos visualmente. A revisão de 30 segundos do RabbitMQ abriu os bindings, enquadrou a tabela/consumidor e ocultou o login local.
- Metadados, timeline, transcrição e IDs ficam em `saida/`, ignorada pelo Git. A montagem usa capturas reais em mais de uma tomada; a retomada não repetiu mutações de negócio.

Estados consultados após a gravação: OS 3/Saga 3 `FINALIZADA`; Saga 4 `COMPENSADA`; orçamento 4 `ESTORNADO`/`refunded`; OS 4 `CANCELADA`.

## Verificações realizadas

- Sintaxe dos módulos e quatro testes do pacote aprovados.
- Sete interfaces locais acessíveis pelo Chromium.
- Consultas GET reais no Swagger, com validação da resposta de rede e da resposta exibida na interface.
- Contrato OpenAPI da falha conferido: `ordemId` no path e `evento` na query string.
- Login e navegação reais no RabbitMQ e Grafana; credenciais apenas no `.env` local ignorado pelo Git.
- Grafana em Last 15 minutes, atualização de 5s, datasource Prometheus saudável e quatro targets UP.
- Primeiro MP4 montado com 820,24 segundos, vídeo H.264 1366×768 e áudio AAC mono 22050 Hz. Áudio inicial com volume médio -23,6 dB e pico -2,7 dB, sem silêncio integral ou clipping nessa amostra.
- Frames reais inspecionados: dashboard, finalização da Saga, cancelamento da OS e bindings do RabbitMQ.

## Resultados de negócio comprovados

| Cenário | Resultado |
| --- | --- |
| OS 3 / orçamento 3 | Saga e OS finalizadas; execução referenciada na Saga iniciada e finalizada |
| OS 4 / orçamento 4 | Evento EXECUCAO_FALHOU; Saga COMPENSADA; orçamento ESTORNADO/refunded; OS CANCELADA |

Os IDs foram obtidos das respostas reais; não foram previstos pela automação. Pagamento e estorno são simulados pelo adaptador local. Mantidas as ressalvas de duplicidade de execuções e ausência de cancelamento automático das tarefas.

## Montagem e revisão

A captura original parou na cena 10 por falha de navegação no RabbitMQ. As nove primeiras cenas foram preservadas; as cenas 10–13 foram retomadas com consultas e navegação, sem criar novas OS nem repetir pagamentos. Um trecho adicional de 30 segundos foi gravado para mostrar os bindings abertos e o consumidor, ocultando o nome de login local. A montagem conserva a ordem das 13 cenas e a narração correspondente; não representa uma tomada contínua.

As interfaces e respostas de negócio são reais. Os cartões de arquitetura e a apresentação dos arquivos de evidência são visões didáticas identificadas. O vídeo não contém resultados fictícios de testes, deploy ou pagamentos externos.

Arquivos locais de trabalho ficam em `saida/`, ignorada pelo Git: MP4, áudio, IDs, timeline e frames de revisão. `.env` e `.browser-profile` também são ignorados.

## Pendências para a entrega final

Este vídeo é um **ensaio**, não uma comprovação completa dos requisitos da Fase 4. Continuam sem comprovação nesta execução: CI/CD do commit implantado, cobertura JaCoCo atual dos quatro serviços, Sonar, rollout/imagem Kubernetes, repositórios remotos separados com proteção e traces distribuídos/APM. A integração externa Mercado Pago também não foi demonstrada. O modo final do pacote permanece bloqueado.

Os quatro testes aprovados são da automação de vídeo; não equivalem aos testes dos microsserviços. A cena 12 usa a fala alternativa que declara as evidências ausentes.
