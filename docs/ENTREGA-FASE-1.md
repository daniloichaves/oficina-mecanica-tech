<style>
body { font-family: -apple-system, 'Segoe UI', Arial, sans-serif; line-height: 1.5; color: #222; max-width: 900px; margin: 0 auto; }
h1 { color: #1a365d; border-bottom: 3px solid #1a365d; padding-bottom: 8px; }
h2 { color: #2c5282; border-bottom: 1px solid #cbd5e0; padding-bottom: 4px; margin-top: 28px; }
h3 { color: #2d3748; margin-top: 20px; }
table { border-collapse: collapse; width: 100%; margin: 12px 0; font-size: 0.95em; }
th { background-color: #2c5282; color: white; padding: 8px 12px; text-align: left; border: 1px solid #2c5282; }
td { padding: 8px 12px; border: 1px solid #cbd5e0; vertical-align: top; }
tr:nth-child(even) { background-color: #f7fafc; }
code { background-color: #edf2f7; padding: 2px 6px; border-radius: 3px; font-size: 0.9em; }
pre { background-color: #1a202c; color: #e2e8f0; padding: 12px; border-radius: 6px; overflow-x: auto; }
pre code { background-color: transparent; color: inherit; padding: 0; }
hr { border: none; border-top: 1px solid #e2e8f0; margin: 24px 0; }
ul { padding-left: 22px; }
li { margin: 4px 0; }
a { color: #2b6cb0; text-decoration: none; }
</style>

# Tech Challenge — Fase 1
## Sistema de Oficina Mecânica

---

## Grupo 310

<table>
  <thead>
    <tr><th>Participante</th><th>RM</th><th>GitHub</th><th>Discord</th></tr>
  </thead>
  <tbody>
    <tr><td>Danilo Ischiavolini Chaves</td><td>RM372600</td><td><a href="https://github.com/daniloichaves">@daniloichaves</a></td><td><code>danilo.ischiavolini</code></td></tr>
    <tr><td>Rodrigo Dias Bragantini</td><td>RM372859</td><td><a href="https://github.com/rbragantini">@rbragantini</a></td><td><code>rbragantini</code></td></tr>
    <tr><td>William de Oliveira Almeida</td><td>RM372192</td><td><a href="https://github.com/wiloalmeida">@wiloalmeida</a></td><td><code>wiloalmeida</code></td></tr>
  </tbody>
</table>

---

## Links da Entrega

<table>
  <thead>
    <tr><th>Recurso</th><th>Link</th></tr>
  </thead>
  <tbody>
    <tr><td><strong>Repositório GitHub</strong></td><td><a href="https://github.com/daniloichaves/oficina-mecanica-tech">https://github.com/daniloichaves/oficina-mecanica-tech</a></td></tr>
    <tr><td><strong>Documentação DDD (Miro)</strong></td><td><a href="https://miro.com/app/board/uXjVHZcarWY=/">https://miro.com/app/board/uXjVHZcarWY=/</a></td></tr>
    <tr><td><strong>Documentação DDD (Markdown versionada)</strong></td><td><a href="ddd/"><code>docs/ddd/</code></a></td></tr>
    <tr><td><strong>Relatório de Vulnerabilidades</strong></td><td><a href="RELATORIO-SEGURANCA-GERENCIAL.md"><code>docs/RELATORIO-SEGURANCA-GERENCIAL.md</code></a></td></tr>
  </tbody>
</table>

---

## Escopo do MVP

Sistema back-end integrado para atendimento e execução de serviços de oficina mecânica, aplicando **Domain-Driven Design (DDD)** e boas práticas de qualidade e segurança.

### Funcionalidades principais
- Cadastro e gestão de **Clientes** (PF/PJ com validação de CPF/CNPJ)
- Cadastro e gestão de **Veículos** (validação de placa Mercosul e antiga)
- Catálogo de **Serviços** (descrição, valor, tempo estimado)
- Gestão de **Peças/Insumos** com controle de **estoque** e alerta de estoque baixo
- **Ordem de Serviço (OS)** com ciclo de vida completo:
  `RECEBIDA → EM_DIAGNOSTICO → AGUARDANDO_APROVACAO → EM_EXECUCAO → FINALIZADA → ENTREGUE`
- Cálculo automático de **Orçamento** (serviços + peças)
- **Autenticação JWT** para endpoints administrativos
- Métrica de **Tempo Médio de Execução** das OS

---

## Stack Tecnológica

<table>
  <thead>
    <tr><th>Camada</th><th>Tecnologia</th><th>Justificativa</th></tr>
  </thead>
  <tbody>
    <tr><td>Linguagem</td><td><strong>Java 21</strong></td><td>LTS, ecossistema maduro</td></tr>
    <tr><td>Framework</td><td><strong>Spring Boot 3.2.5</strong></td><td>Produtividade + padrões corporativos</td></tr>
    <tr><td>Persistência</td><td><strong>Spring Data JPA</strong> + <strong>PostgreSQL 16</strong></td><td>Relacional, transacional, open-source</td></tr>
    <tr><td>Segurança</td><td><strong>Spring Security + jjwt 0.12.5</strong></td><td>JWT para APIs admin</td></tr>
    <tr><td>Validação</td><td><strong>Jakarta Validation</strong> + Value Objects</td><td>Dupla camada de validação</td></tr>
    <tr><td>Documentação API</td><td><strong>SpringDoc OpenAPI (Swagger UI)</strong></td><td>Endpoint em <code>/swagger-ui.html</code></td></tr>
    <tr><td>Testes</td><td><strong>JUnit 5 + Mockito + JaCoCo</strong></td><td>192 unitários + 47 integração</td></tr>
    <tr><td>Qualidade</td><td><strong>SonarQube + Trivy</strong></td><td>Pipeline de segurança automatizado</td></tr>
    <tr><td>Build</td><td><strong>Maven</strong></td><td>Padrão do ecossistema Java</td></tr>
    <tr><td>Containerização</td><td><strong>Docker + docker-compose</strong></td><td>Execução local em 1 comando</td></tr>
  </tbody>
</table>

---

## Arquitetura DDD

Separação em camadas conforme DDD, respeitando Bounded Contexts:

```
src/main/java/com/oficina/mecanica/
├── domain/          # Entidades, Value Objects, Repositórios (interfaces)
├── application/     # DTOs e Services (Use Cases)
├── infrastructure/  # Persistência JPA, Security, Configs
└── presentation/    # Controllers REST
```

**Bounded Contexts identificados:**
- 🟡 **Ordem de Serviço** (Core Domain)
- 🔵 **Atendimento** (Cliente + Veículo) — Supporting
- 🔵 **Catálogo de Serviços** — Supporting
- 🔵 **Estoque / Peças** — Supporting
- 🟢 **Identidade & Acesso (JWT)** — Generic
- 🟢 **Monitoramento** — Generic

Detalhamento completo em [`docs/ddd/04-bounded-contexts.md`](ddd/04-bounded-contexts.md) e no **Frame 3 do Miro**.

---

## Qualidade & Segurança

### Cobertura de Testes
- **239 testes totais** (192 unitários + 47 integração)
- **90% de cobertura** em domínios críticos (`CpfCnpj`, `Placa`)
- CI automatizado via GitHub Actions

### Pipeline de Segurança
Script `./oficina-mecanica-tech.sh security` executa:
1. **Trivy** — scan de vulnerabilidades (Dockerfile, compose, código)
2. **JaCoCo** — cobertura de testes
3. **SonarQube** — qualidade de código
4. **Consolidação** em `target/security/security-summary.md`

### Vulnerabilidades Identificadas & Mitigadas
Relatório gerencial completo em [`docs/RELATORIO-SEGURANCA-GERENCIAL.md`](RELATORIO-SEGURANCA-GERENCIAL.md):
- 5 findings totais (**2 CRITICAL**, **3 HIGH**)
- Plano de ação documentado por prioridade
- Evidências das 3 ferramentas anexadas

---

## Como Executar

```bash
git clone https://github.com/daniloichaves/oficina-mecanica-tech.git
cd oficina-mecanica-tech
docker-compose up -d
```

- Aplicação: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

Detalhes completos no [README.md](../README.md).

---

## Entregáveis desta Fase

- ✅ Código-fonte no GitHub (repositório privado com acesso a `soat-architecture`)
- ✅ Documentação DDD no Miro (5 frames) + versionada em `docs/ddd/`
- ✅ Dockerfile e docker-compose.yml funcionais
- ✅ README.md completo com instruções de execução
- ✅ Relatório de análise de vulnerabilidades
- ✅ Testes automatizados (unitários + integração)
- ✅ Vídeo de demonstração (link: _a adicionar após gravação_)

---

_Documento gerado em 03/05/2026 — Tech Challenge SOAT Fase 1_
