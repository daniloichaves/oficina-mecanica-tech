# Instruções obrigatórias para o Codex — vídeo da Fase 4

## Objetivo

A origem do roteiro é `ROTEIRO-VIDEO.md`. Leia o arquivo inteiro e execute somente o que está nas seções **Mostrar**, narrando somente **Falar**. Preserve a sequência de 13 cenas e o limite total de 15:00. `src/script.mjs` lê os blocos de fala do arquivo diretamente e seleciona a fala alternativa da cena 12 no modo ensaio.

Esta é uma automação de vídeo **real**, nunca um mock. Navegação via Playwright e respostas que o próprio Swagger UI renderizou; gravação `ffmpeg -f gdigrab -i desktop` em sessão Windows desbloqueada; áudio sintetizado por cena e mixado ao vídeo. Diferentemente de um tutorial genérico de OpenAPI, o vídeo deve demonstrar uma Saga completa, uma compensação controlada e observabilidade.

## Antes de qualquer ação mutável

1. Leia `README.md`, `.env.example`, `src/swagger.mjs`, `src/record.mjs` e `docs/MAPA-CENAS.md`.
2. Execute `npm run syntax`, `npm test`, `npm run plan`, `npm run check`, `npm run inspect`, nessa ordem.
3. `inspect` deve confirmar as quatro operações Swagger e acessibilidade das sete telas. Se rotas ou seletores diferirem, inspecione o OpenAPI e corrija o código de automação — **não mude o conteúdo do roteiro para se adequar ao código**.
4. Garanta que a raiz do repositório real esteja indicada por `REPO_ROOT` e que a feature Gherkin e o README de arquitetura existam.
5. Ajuste o seletor e o corpo do `POST /api/sagas/ordens/{ordemId}/eventos` ao OpenAPI real. O campo `evento` pode ser query param, formulário ou corpo de requisição — descubra o formato, nunca adivinhe, antes de rodar a gravação.
6. Confirme manualmente os seletores dos botões de RabbitMQ, Grafana, Prometheus e painéis nas versões locais. Ajuste o time range para Last 15 minutes e refresh para 5s, com verificação real de estado. Os seletores do pacote são intencionalmente genéricos e podem exigir atualização.
7. Não execute `npm run video` sem que o proprietário tenha ativado `ALLOW_DEMO_MUTATIONS=true` em ambiente de DEMONSTRAÇÃO. POST/PATCH da aplicação realmente alteram dados (criam OS, aprovam/pagam e simulam compensação).

## IDs e estados

- Nunca preveja ID 1, 2, 3 nem converta UUID de execução em ID de OS.
- Copie `OS_A`, `ORC_A`, `EXEC_A`, `OS_B`, `ORC_B` das respostas REAIS. Valide formatos e reconfirme estados por repetição de GET com tempo de espera porque Saga é assíncrona.
- Exija código HTTP real e corpo visível no Swagger UI; não use exemplos do OpenAPI como evidência.
- Não crie endpoints novos nem acione `/entregar`, `/compensar` do Billing ou `/cancelar` da OS: essas etapas são proibidas pelo roteiro.
- Na falha, injetar **somente** `EXECUCAO_FALHOU` pelo endpoint do orquestrador e comprovar `COMPENSADA`/`ESTORNADO`/`CANCELADA` com consultas independentes.

## Qualidade e integridade

- O roteiro admite: duas execuções por OS após aprovação/pagamento; execuções prévias não canceladas pela compensação; pagamento/estorno simulados; APM/traces não validados. Narre essas ressalvas corretamente.
- A cena 12 NÃO está comprovada no ambiente local. Modo `ensaio` mostra feature e workflows reais, com fala alternativa do roteiro; não atende à entrega final.
- Modo `final` está bloqueado em `src/record.mjs` até o Codex verificar evidências reais dos quatro serviços: Actions do commit implantado, cobertura JaCoCo medida, execução Sonar, rollout Kubernetes e imagem da versão. Não remova esse bloqueio para passar testes: somente libere quando houver comprovação no vídeo, e documente as verificações.
- Se um dado ou ferramenta falhar, interrompa e relate. Nunca crie screenshot, painel, contagem, resposta, commit, número de cobertura, trace, log ou deploy fictício.
- Não inclua usuários, senhas, tokens, cookies ou chaves em prompt, código, gravação, saída ou repositório. `.env` e `.browser-profile` são ignorados pelo Git e devem permanecer locais. O roteiro distribuído foi sanitizado na linha de login.
- A voz sintética precisa ser identificada como sintética ao distribuir o vídeo.

## Entregáveis

- `saida/video-fase4.mp4` (ensaio, até 15 minutos) gerado em Windows.
- `saida/timeline.json`, `saida/narracao.txt` e `saida/ids-demo.json` (todos locais e ignorados pelo Git).
- Relatório de validação do vídeo com duração, estados comprovados, pendências de evidências e quaisquer trechos que precisaram de correção. Não afirme que o vídeo está pronto para a entrega final sem completar a cena 12.
