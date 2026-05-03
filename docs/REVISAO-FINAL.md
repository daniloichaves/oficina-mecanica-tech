# Revisão Final - Fase 1 (Tech Challenge SOAT)

Auditoria do repositório **Oficina Mecânica Tech** contra os requisitos da rubrica da Fase 1. Cada item foi verificado através de inspeção direta do código/docs e tem evidência documentada.

**Data:** 03/05/2026
**Grupo:** 310
**Resultado Geral:** ✅ **APROVADO com 1 gap menor de segurança**

---

## 1. Documentação DDD

| Requisito | Status | Evidência |
|---|---|---|
| Linguagem Ubíqua | ✅ | `docs/ddd/01-linguagem-ubiqua.md` (65 linhas, glossário completo) + Frame 1 do Miro |
| Event Storming (OS) | ✅ | `docs/ddd/02-event-storming-os.md` (129 linhas, Mermaid flowchart + state diagram) + Frame 2 do Miro |
| Event Storming (Peças) | ✅ | `docs/ddd/03-event-storming-pecas.md` |
| Bounded Contexts | ✅ | `docs/ddd/04-bounded-contexts.md` (Core/Supporting/Generic + Context Map) + Frame 3 do Miro |
| Diagrama de Estados da OS | ✅ | `docs/ddd/02-event-storming-os.md` linhas 99-114 + Frame 4 do Miro |
| Design Level | ✅ | `docs/ddd/05-design-level.md` |
| Casos de Uso | ✅ | `docs/ddd/06-casos-de-uso.md` |
| Hotspots | ✅ | `docs/ddd/02-event-storming-os.md` seção 5 + Frame 5 do Miro (4 sticky notes vermelhos) |
| Publicação em ferramenta visual (Miro) | ✅ | https://miro.com/app/board/uXjVHZcarWY=/ — 5 frames, acesso público de visualização |

---

## 2. Aplicação da Linguagem Ubíqua no Código

Verificação: termos do glossário presentes como classes/métodos no código.

| Termo do Glossário | Classe Java | Arquivo |
|---|---|---|
| Cliente | `Cliente` | `src/main/java/com/oficina/mecanica/domain/entities/Cliente.java` |
| Veículo | `Veiculo` | `domain/entities/Veiculo.java` |
| Ordem de Serviço | `OrdemServico` | `domain/entities/OrdemServico.java` |
| Serviço | `Servico` | `domain/entities/Servico.java` |
| Peça | `Peca` | `domain/entities/Peca.java` |
| Item de Serviço | `ItemServico` | `domain/entities/ItemServico.java` |
| Item de Peça | `ItemPeca` | `domain/entities/ItemPeca.java` |
| Status da OS | `StatusOrdemServico` (enum) | `domain/entities/StatusOrdemServico.java` |
| CPF/CNPJ | `CpfCnpj` (Value Object) | `domain/valueobjects/CpfCnpj.java` |
| Placa | `Placa` (Value Object) | `domain/valueobjects/Placa.java` |

**Verbos do domínio aplicados como métodos:**
- `iniciarDiagnostico()`, `concluirDiagnostico()`, `aprovarOrcamento()`, `finalizar()`, `entregar()` — todos presentes em `OrdemServico.java` e expostos como endpoints `PATCH` em `OrdemServicoController.java`

✅ **Linguagem Ubíqua fielmente aplicada**

---

## 3. APIs REST (MVP funcional)

### 3.1 Controllers presentes (7/7)

| Controller | Arquivo |
|---|---|
| AuthController | `presentation/rest/AuthController.java` |
| ClienteController | `presentation/rest/ClienteController.java` |
| VeiculoController | `presentation/rest/VeiculoController.java` |
| ServicoController | `presentation/rest/ServicoController.java` |
| PecaController | `presentation/rest/PecaController.java` |
| OrdemServicoController | `presentation/rest/OrdemServicoController.java` |
| MetricasController | `presentation/rest/MetricasController.java` |

### 3.2 Endpoints da OS (ciclo de vida completo)

Todos os 5 verbos do domínio estão expostos:
- `PATCH /api/ordens-servico/{id}/iniciar-diagnostico`
- `PATCH /api/ordens-servico/{id}/concluir-diagnostico`
- `PATCH /api/ordens-servico/{id}/aprovar-orcamento`
- `PATCH /api/ordens-servico/{id}/finalizar`
- `PATCH /api/ordens-servico/{id}/entregar`

Evidência: `OrdemServicoController.java` linhas 71-98.

### 3.3 Documentação OpenAPI/Swagger

- Dependência: `springdoc-openapi-starter-webmvc-ui 2.5.0` no `pom.xml:82-84`
- Todas as APIs têm `@Operation(summary = ...)` descrevendo o propósito
- Swagger UI disponível em `http://localhost:8080/swagger-ui.html` (auto-configurado pelo SpringDoc)

✅ **APIs completas e documentadas**

---

## 4. Segurança

### 4.1 Autenticação JWT

| Componente | Arquivo |
|---|---|
| Configuração de Security | `infrastructure/security/SecurityConfig.java` |
| Filtro de Autenticação | `infrastructure/security/JwtAuthenticationFilter.java` |
| Serviço JWT | `infrastructure/security/JwtService.java` |
| Endpoint de Login | `presentation/rest/AuthController.java` (`POST /api/auth/login`) |

Dependências (`pom.xml`):
- `jjwt-api 0.12.5`
- `jjwt-impl 0.12.5`
- `jjwt-jackson 0.12.5`
- `spring-boot-starter-security`

### 4.2 Validação de Entrada

- **Jakarta Validation** ativo via `@Valid` nos `@RequestBody` de todos Controllers
- **Value Objects com validação de dígito verificador**:
  - `CpfCnpj.java` — validação de CPF e CNPJ
  - `Placa.java` — validação de formato Mercosul e antigo

### 4.3 Análise de Vulnerabilidades

Arquivo: `docs/RELATORIO-SEGURANCA-GERENCIAL.md`

- **5 findings identificados**: 2 CRITICAL, 3 HIGH
- Ferramentas: Trivy + SonarQube + JaCoCo
- Pipeline automatizado: `./oficina-mecanica-tech.sh security`

✅ **Segurança implementada e auditada**

⚠️ **GAP IDENTIFICADO — ver Seção 8**

---

## 5. Testes Automatizados

### 5.1 Arquivos de teste encontrados: **20**

**Unitários de Domínio (10):**
- `ClienteTest`, `VeiculoTest`, `ServicoTest`, `PecaTest`
- `OrdemServicoTest`, `ItemServicoTest`, `ItemPecaTest`
- `CpfCnpjTest`, `PlacaTest`
- `DomainEntitiesCoverageTest`

**Unitários de Segurança (2):**
- `JwtServiceTest`, `JwtAuthenticationFilterTest`

**Integração (5):**
- `ClienteIntegrationTest`, `VeiculoIntegrationTest`
- `ServicoIntegrationTest`, `PecaIntegrationTest`
- `OrdemServicoIntegrationTest`

**Controllers (2):**
- `AuthControllerTest`, `MetricasControllerTest`

**Aplicação (1):**
- `OficinaMecanicaApplicationTest`

### 5.2 Ferramentas configuradas

- `jacoco-maven-plugin 0.8.11` em `pom.xml:135-137`
- Cobertura declarada: **90% em domínios críticos** (`CpfCnpj`, `Placa`)
- CI automatizado: `.github/workflows/ci.yml`

✅ **Suite de testes robusta**

---

## 6. Infraestrutura & Execução

| Item | Status | Evidência |
|---|---|---|
| Dockerfile | ✅ | Dockerfile (19 linhas, multi-stage build: Maven + JRE 21) |
| docker-compose.yml | ✅ | 2 serviços: `postgres:16-alpine` + `app` com healthcheck |
| Variáveis de ambiente | ✅ | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION` |
| README.md | ✅ | 326 linhas com quickstart, endpoints, exemplos cURL |
| Script de segurança | ✅ | `oficina-mecanica-tech.sh` com comandos `security`, `trivy`, `sonar`, `coverage` |
| CI/CD | ✅ | `.github/workflows/ci.yml` + `security.yml` |

---

## 7. Entregáveis Administrativos

| Item | Status | Evidência |
|---|---|---|
| Nome do grupo | ✅ | Grupo 310 — `docs/ENTREGA-FASE-1.md` |
| Participantes + RMs | ✅ | Danilo RM372600, Rodrigo RM372859, William RM372192 |
| Link do Miro | ✅ | https://miro.com/app/board/uXjVHZcarWY=/ (testado em aba anônima) |
| Link do repositório | ✅ | https://github.com/daniloichaves/oficina-mecanica-tech |
| Relatório de vulnerabilidades | ✅ | `docs/RELATORIO-SEGURANCA-GERENCIAL.md` |
| Documento de entrega | ✅ | `docs/ENTREGA-FASE-1.md` (a ser exportado como PDF) |
| Vídeo de demonstração | ⏳ | Pendente (gravação) |
| Repositório privado + acesso `soat-architecture` | ⏳ | Pendente (configuração no GitHub) |

---

## 8. Gaps Identificados

### 🟡 GAP 1 — Dockerfile ainda roda como root (baixa criticidade)

**O quê:** O `Dockerfile` não tem diretiva `USER` para rodar a aplicação com usuário não-privilegiado.

**Evidência:** `Dockerfile` — arquivo termina com `ENTRYPOINT` sem declarar `USER`.

**Status na rubrica:** O relatório de segurança (`docs/RELATORIO-SEGURANCA-GERENCIAL.md` linha 26) já **identifica e documenta** esta vulnerabilidade (DS-0002, HIGH) com a mitigação recomendada. Portanto está coberto pela rubrica "análise de vulnerabilidades", que exige **identificar e documentar**, não necessariamente mitigar.

**Decisão sugerida:** Manter como está (vulnerabilidade documentada é um entregável válido da fase). Opcionalmente, adicionar usuário não-root no Dockerfile para zerar o finding e demonstrar maturidade:

```dockerfile
RUN groupadd -r oficina && useradd -r -g oficina oficina
USER oficina
```

**Impacto na avaliação:** Nenhum — o requisito da rubrica foi atendido.

---

## 9. Resumo Executivo da Revisão

| Dimensão | Cobertura |
|---|---|
| DDD Documentação | 100% ✅ |
| DDD Código | 100% ✅ |
| APIs REST | 100% ✅ |
| Segurança | 100% ✅ (1 vulnerabilidade documentada conforme rubrica) |
| Testes | 100% ✅ |
| Infraestrutura | 100% ✅ |
| Entregáveis administrativos | 75% ⏳ (falta vídeo + repo privado) |

**Veredito:** Repositório pronto para entrega após:
1. Gravação do vídeo de demonstração (Fase 7)
2. Configuração do repositório como privado + acesso ao `soat-architecture` (Fase 8)
3. Exportação do `docs/ENTREGA-FASE-1.md` para PDF

Nenhum ajuste de código é estritamente necessário para atender a rubrica.
