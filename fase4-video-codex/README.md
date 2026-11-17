# Vídeo 100% automatizado da Fase 4 — Windows + Codex

Este projeto adapta **o seu roteiro real** de 13 cenas (`ROTEIRO-VIDEO.md`) para uma execução no Windows com Playwright (Swagger, RabbitMQ, Grafana, Prometheus), captura real da tela pelo FFmpeg e narração automática em português brasileiro. O roteiro original foi mantido em seu conteúdo demonstrativo; **a linha que continha credenciais de login foi sanitizada** no arquivo distribuído.

**Situação:** o pacote fornece uma base funcional de automação, validação, roteiro, controles de segurança e testes do parser; precisa de ajuste dos seletores contra suas interfaces reais. Não foi possível testar `localhost` nem gravar sua tela daqui. O projeto gera **ensaio** até serem verificadas as evidências finais de Actions, cobertura, Sonar e deploy Kubernetes exigidas pela cena 12.

## Arquivos

- `ROTEIRO-VIDEO.md`: texto fonte das falas (13 cenas), sanitizado na linha de login.
- `AGENTS.md`: instruções obrigatórias para o Codex.
- `docs/MAPA-CENAS.md`: tempos, cliques, respostas e verificações por cena.
- `src/script.mjs`: extrai fielmente as falas, condiciona cena 12 a `VIDEO_MODE`, valida IDs.
- `src/swagger.mjs`: interage pela interface Swagger UI; `waitForResponse` e status na tela.
- `src/inspect.mjs`: faz inspeção somente leitura e detecta serviços, endpoints e arquivos.
- `src/record.mjs`: gravação desktop, UI, síntese de áudio e mixagem do MP4.
- `src/voice-win.ps1`, `src/voices.ps1`: vozes portuguesas Windows.

## Instalando no seu repositório

Copie a pasta `fase4-video-codex` extraída **para dentro da raiz do repositório que contém** `docs/` e `fase4-priority4/`, abra essa pasta no terminal do VS Code e execute:

```powershell
npm install
npx playwright install chromium
Copy-Item .env.example .env
notepad .env
```

Instale FFmpeg e ffprobe no PATH. Com a pasta dentro do repositório, deixe `REPO_ROOT=..` no `.env`. Os endereços de localhost são os sete endereços do roteiro, e os serviços devem estar no ar. Inicie o Docker Desktop e, se necessário, execute **na raiz do projeto original**:

```powershell
$env:PATH = 'C:\Program Files\Docker\Docker\resources\bin;' + $env:PATH
docker compose -f docker-compose.fase4.yml -f docker-compose.fase4-observability.yml up -d
```

Para automatizar login nos painéis, configure as variáveis `RABBIT_USER`, `RABBIT_PASSWORD`, `GRAFANA_USER` e `GRAFANA_PASSWORD` somente no seu `.env` local se exigidas. Não cole esses valores no Codex, no chat ou em um commit; também é possível usar uma sessão local já autenticada. O Swagger dos quatro serviços não requer autenticação no roteiro.

## Valide antes de alterar dados

```powershell
npm run syntax
npm test
npm run plan
npm run check
npm run inspect
npm run voices
```

`inspect` é **somente leitura**. Confirma sete telas, rotas do Swagger, a documentação da arquitetura e a feature Gherkin. Se algo estiver diferente, interrompe e aponta o que o Codex deve corrigir. É essencial verificar o formulário do evento `EXECUCAO_FALHOU`, os botões RabbitMQ e o dashboard Grafana; cada instalação pode variar.

## Gravar o ensaio — faz operações reais

**Só depois de revisar os endpoints e confirmar que o banco é ambiente de demonstração**, altere no `.env`:

```dotenv
ALLOW_DEMO_MUTATIONS=true
VIDEO_MODE=ensaio
VOICE_PROVIDER=windows
```

**Atenção:** a gravação realmente cria duas ordens, aprova e paga orçamentos no simulador local, inicia/finaliza uma execução e injeta uma falha controlada para demonstrar a compensação. Nunca execute em produção.

```powershell
npm run video
```

A narração Windows depende de voz pt-BR SAPI. `npm run voices` identifica se uma voz compatível existe. Alternativamente, `VOICE_PROVIDER=openai` usa chave OpenAI API armazenada somente no seu `.env`, com custo separado da assinatura ChatGPT; nunca compartilhe a chave.

Saídas locais em `saida/`:

```text
video-fase4.mp4
captura-sem-audio.mp4
narracao.txt
timeline.json
ids-demo.json
```

Mantenha o Windows desbloqueado, silencie notificações, feche aplicativos sensíveis e ajuste a resolução para 1920x1080 antes de iniciar; a captura pega o desktop inteiro. Áudio sintetizado por cena é sobreposto após a gravação. A sincronização *por clique/palavra* precisa ser refinada pelo Codex após testar os tempos reais. Identifique a voz sintética na publicação.

## Ensaio não é entrega final

O documento diz que **testes atuais, cobertura, Sonar e Kubernetes ainda não foram validados neste provisionamento**. Por isso `VIDEO_MODE=final` está bloqueado em código. O ensaio mostra a feature de Gherkin e workflows reais, mas não apresenta cobertura/aprovação/deploy fictícios. Para uma versão final, o Codex deverá validar no ambiente real e gravar as provas de GitHub Actions do commit, JaCoCo dos quatro serviços, Sonar, rollout e imagem Kubernetes. Só então deve desbloquear e automatizar esse segmento.

## Prompt pronto para Codex

Abra o Codex dentro desta pasta e cole o conteúdo de `PROMPT-CODEX.md` (já incluído). O Codex pode inspecionar o projeto original e adaptar seletores; não pode adivinhar resposta, estado, cobertura, trace ou deploy.

## Ajustes locais realizados em 17/11/2026

- Chromium, FFmpeg/ffprobe e voz pt-BR Microsoft Maria Desktop disponíveis.
- Corrigidos seletores de parâmetros e da célula HTTP do Swagger; validação por consultas reais GET.
- Conferido OpenAPI: `ordemId` é parâmetro de path e `evento` é query string na injeção de falha.
- Grafana configurado e verificado em Last 15 minutes e atualização de 5s, sem deixar menus abertos.
- Corrigidas esperas de navegação do RabbitMQ e detecção do formulário de login.
- Ritmo SAPI ajustado para caber nos tempos das cenas.
- `npm run validate-ui` testa as interfaces com consultas, sem criar OS ou alterar seus estados. Exige credenciais locais e ferramentas instaladas.
- `npm run resume` é uma recuperação específica desta captura: preserva os primeiros 520 segundos das cenas 1–9 e grava apenas as cenas 10–13, sem mutações de negócio. Exige a captura parcial e os IDs recuperados em `saida/ids-demo.json`. Não usar em uma gravação diferente sem verificar seu corte e os estados reais.

O vídeo usa voz sintética do Windows e montagem de duas capturas reais. Os diagramas didáticos são identificados como tal; telas e respostas de negócio vêm das aplicações. O modo final continua bloqueado pela ausência de evidências verificadas para a cena 12.

### Referências

- https://learn.chatgpt.com/docs/codex/cli
- https://playwright.dev/docs/locators
- https://playwright.dev/docs/api/class-page
- https://ffmpeg.org/ffmpeg-devices.html#gdigrab
