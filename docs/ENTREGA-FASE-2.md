# Entrega Fase 2 — Grupo 310

- **Repositório:** https://github.com/daniloichaves/oficina-mecanica-tech (compartilhado com `soat-architecture`)
- **Participantes:**
  - Danilo Ischiavolini Chaves — RM372600
  - Rodrigo Dias Bragantini — RM372859
  - William de Oliveira Almeida — RM372192
- **Desenho da arquitetura:** seção "Fase 2 — Objetivos" do [README](../README.md) (diagrama Mermaid com componentes, infraestrutura e fluxo de deploy)
- **Collection das APIs:** Swagger UI (`/swagger-ui.html`) / OpenAPI (`/api-docs`)
- **Vídeo (até 15 min):** _[PENDENTE — inserir link YouTube/Vimeo público ou não listado]_

## Roteiro sugerido do vídeo

1. **Deploy da aplicação** — `cd infra && terraform apply` (cluster kind + banco) e `kubectl apply -f k8s/`; mostrar pods subindo (`kubectl -n oficina get pods -w`).
2. **Execução do CI/CD** — push na branch e acompanhar os 3 jobs no GitHub Actions (build+testes, imagem GHCR, deploy em kind com smoke test).
3. **Consumo das APIs** — via Swagger: criar cliente/veículo/serviço/peça, abrir OS (retorna id único), consultar `GET /ordens-servico/{id}/status`, aprovar/recusar orçamento pelo webhook `POST /api/webhooks/orcamento`, mostrar a listagem ordenada `GET /ordens-servico` (sem finalizadas/entregues) e o e-mail de mudança de status no Mailhog.
4. **Escalabilidade automática** — gerar carga (ex.: `hey`/`ab` contra o NodePort 30080) e mostrar o HPA escalando: `kubectl -n oficina get hpa -w`.

## Requisitos da fase × implementação

| Requisito | Onde |
|---|---|
| Clean Architecture / Hexagonal | `domain/repositories` (ports) + adapters em `infrastructure/` |
| Abertura de OS com id único | `POST /api/ordens-servico` (Fase 1, mantido) |
| Consulta de status da OS | `GET /api/ordens-servico/{id}/status` |
| Aprovação/recusa de orçamento (notificação externa) | `POST /api/webhooks/orcamento` |
| Listagem ordenada com exclusão lógica | `GET /api/ordens-servico` (`findAtivasOrdenadas`) |
| Atualização de status via e-mail | `NotificacaoPort` + `EmailNotificacaoAdapter` (Mailhog em dev) |
| Dockerfile e docker-compose revisados | raiz do projeto (Mailhog incluído) |
| Manifestos Kubernetes | [`k8s/`](../k8s/) — Deployments, Services, ConfigMap, Secrets, HPA |
| Terraform (cluster + banco, documentado) | [`infra/`](../infra/) + [`infra/README.md`](../infra/README.md) |
| Pipeline CI/CD | [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) |
| Testes automatizados | ~250 testes unitários e de integração (`mvn verify`) |
