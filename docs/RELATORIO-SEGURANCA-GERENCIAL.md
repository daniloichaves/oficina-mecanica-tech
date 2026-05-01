# Relatório de Análise de Segurança - Oficina Mecânica Tech

**Data da Análise:** 2026-05-01  
**Responsável:** Pipeline de Segurança Automatizado (Trivy + SonarQube + JaCoCo)

---

## 1. Resumo Executivo

| Ferramenta | Status | Findings HIGH/CRITICAL |
|------------|--------|------------------------|
| Trivy (Dockerfile) | ALERTA | 1 HIGH |
| Trivy (docker-compose) | OK | 0 |
| Trivy (Código) | ALERTA | 5 findings (2 CRITICAL, 3 HIGH) |
| JaCoCo (Cobertura) | OK | >90% em domínios críticos |
| SonarQube | OK | Análise concluída |

---

## 2. Vulnerabilidades Identificadas

### 2.1 Infraestrutura (Dockerfile)

| ID | Severidade | Título | Mitigação Recomendada |
|----|------------|--------|----------------------|
| DS-0002 | HIGH | Container executando como root | Adicionar `USER` non-root no Dockerfile |

**Risco:** Escalada de privilégios e container escape.  
**Ação:** Criar usuário dedicado no Dockerfile e rodar a aplicação com esse usuário.

### 2.2 Dependências Java (Maven)

| CVE | Severidade | Componente | Versão Atual | Versão Corrigida | Descrição |
|-----|------------|------------|--------------|------------------|-----------|
| CVE-2024-38821 | CRITICAL | spring-security-web | 6.2.4 | 6.3.4 | Authorization Bypass em WebFlux |
| CVE-2026-22732 | CRITICAL | spring-security-web | 6.2.4 | 6.5.9/7.0.4 | Security policy bypass |
| CVE-2025-41249 | HIGH | spring-core | 6.1.6 | 6.2.11 | Annotation Detection Vulnerability |
| CVE-2024-38816 | HIGH | spring-webmvc | 6.1.6 | 6.1.13 | Path Traversal |
| CVE-2024-38819 | HIGH | spring-webmvc | 6.1.6 | 6.1.14 | Path Traversal em web frameworks |

**Risco:** Bypass de autorização, path traversal e vazamento de informação.  
**Ação:** Atualizar Spring Boot para versão 3.3.x ou superior (inclui correções das dependências).

---

## 3. Cobertura de Testes (JaCoCo)

| Domínio | Cobertura | Status |
|---------|-----------|--------|
| CpfCnpj (Value Object) | >90% | OK |
| Placa (Value Object) | >90% | OK |
| Entidades de Domínio | >80% | OK |
| Services | >70% | OK |
| Controllers | >60% | OK |

**Observação:** Cobertura mínima de 90% atingida nos domínios críticos conforme exigido.

---

## 4. Qualidade de Código (SonarQube)

- **Projeto:** oficina-mecanica
- **Dashboard:** http://localhost:9000/dashboard?id=oficina-mecanica
- **Status:** Análise concluída com sucesso
- **Principais métricas:**
  - Lines of Code: ~3.500
  - Duplicação: <5%
  - Code Smells: Baixo

---

## 5. Plano de Ação Prioritário

| Prioridade | Ação | Responsável | Prazo |
|------------|------|-------------|-------|
| 1 - URGENTE | Atualizar Spring Boot 3.2.5 → 3.3.x (CVEs CRITICAL) | Tech Lead | 1 semana |
| 2 - ALTA | Adicionar USER non-root no Dockerfile | DevOps | 1 semana |
| 3 - MÉDIA | Aumentar cobertura de Controllers para >80% | QA | 2 semanas |
| 4 - BAIXA | Revisar warnings do SonarQube | Time | Contínuo |

---

## 6. Evidências

- `target/security/trivy-dockerfile.txt` - Scan do Dockerfile
- `target/security/trivy-docker-compose.txt` - Scan do docker-compose
- `target/security/trivy-fs.txt` - Scan do filesystem/código
- `target/site/jacoco/index.html` - Relatório de cobertura JaCoCo
- http://localhost:9000 - Dashboard SonarQube

---

**Conclusão:** A aplicação possui vulnerabilidades CRITICAL em dependências Spring que devem ser corrigidas com urgência. A infraestrutura Docker precisa de hardening. Cobertura de testes e qualidade de código estão adequados.
