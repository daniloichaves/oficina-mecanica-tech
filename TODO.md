# TODO - Tech Challenge - Sistema de Oficina Mecânica Tech
 
## Visão Geral
Desenvolver MVP de back-end para sistema integrado de atendimento e execução de serviços de oficina mecânica, utilizando DDD e boas práticas de qualidade e segurança.

**Peso**: 90% da nota da fase

---

## Fase 1 - Planejamento e Arquitetura

### Documentação DDD
- [x] Criar Event Storming completo do fluxo de criação e acompanhamento da OS
- [x] Criar Event Storming completo do fluxo de gestão de peças e insumos
- [x] Definir diagramas DDD conforme disciplina
- [x] Estabelecer Linguagem Ubíqua do domínio
- [x] Documentar no Miro ou equivalente — versionado em `docs/ddd/` (Markdown + Mermaid, equivalente ao Miro conforme rubrica "ou equivalente")

### Escolha Tecnológica
- [x] Definir banco de dados (com justificativa) - PostgreSQL 16 (relacional, open-source, suporte a transações)
- [x] Definir linguagem/framework de back-end - Java 21 + Spring Boot 3.2.5
- [x] Definir bibliotecas de autenticação (JWT) - jjwt 0.12.5 + Spring Security
- [x] Definir bibliotecas de validação - Jakarta Validation + Value Objects customizados
- [x] Definir bibliotecas de teste - JUnit 5 + Mockito + Jacoco

---

## Fase 2 - Estrutura do Projeto

### Configuração Inicial
- [x] Criar estrutura de repositório
- [x] Configurar projeto (dependências, build tool) - Maven + Spring Boot
- [x] Configurar arquitetura em camadas (DDD)
- [x] Criar Dockerfile
- [x] Criar docker-compose.yml
- [x] Escrever README.md com instruções de uso

### Camadas DDD
- [x] Domain Layer (Entidades, Value Objects, Aggregates)
- [x] Application Layer (Use Cases, Services)
- [x] Infrastructure Layer (Repositories, External Services)
- [x] Presentation Layer (API Controllers, DTOs)

---

## Fase 3 - Implementação do Domínio

### Entidades
- [x] Cliente (CPF/CNPJ, nome, contato)
- [x] Veículo (placa, marca, modelo, ano, cliente)
- [x] Serviço (descrição, valor, tempo estimado)
- [x] Peça/Insumo (descrição, valor, quantidade em estoque)
- [x] Ordem de Serviço (status, cliente, veículo, serviços, peças, orçamento)

### Value Objects
- [x] CPF/CNPJ (validação)
- [x] Placa de Veículo (validação)
- [x] Monetário (valor)
- [x] Status da OS (enum)

### Aggregates
- [x] Cliente Aggregate (cliente + veículos)
- [x] Ordem de Serviço Aggregate (OS + serviços + peças)

---

## Fase 4 - Implementação das APIs

### APIs de Clientes
- [x] POST /clientes - Criar cliente
- [x] GET /clientes - Listar clientes
- [x] GET /clientes/{id} - Buscar cliente por ID
- [x] GET /clientes/cpf/{cpfCnpj} - Buscar por CPF/CNPJ
- [x] PUT /clientes/{id} - Atualizar cliente
- [x] DELETE /clientes/{id} - Remover cliente

### APIs de Veículos
- [x] POST /veiculos - Criar veículo
- [x] GET /veiculos - Listar veículos
- [x] GET /veiculos/{id} - Buscar veículo por ID
- [x] GET /veiculos/placa/{placa} - Buscar por placa
- [x] GET /veiculos/cliente/{clienteId} - Listar por cliente
- [x] PUT /veiculos/{id} - Atualizar veículo
- [x] DELETE /veiculos/{id} - Remover veículo

### APIs de Serviços
- [x] POST /servicos - Criar serviço
- [x] GET /servicos - Listar serviços
- [x] GET /servicos/{id} - Buscar serviço por ID
- [x] PUT /servicos/{id} - Atualizar serviço
- [x] DELETE /servicos/{id} - Remover serviço

### APIs de Peças/Insumos
- [x] POST /pecas - Criar peça/insumo
- [x] GET /pecas - Listar peças/insumos
- [x] GET /pecas/{id} - Buscar peça/insumo por ID
- [x] GET /pecas/estoque-baixo - Listar estoque baixo
- [x] PUT /pecas/{id} - Atualizar peça/insumo
- [x] PATCH /pecas/{id}/estoque - Atualizar estoque
- [x] DELETE /pecas/{id} - Remover peça/insumo

### APIs de Ordens de Serviço
- [x] POST /ordens-servico - Criar OS
- [x] GET /ordens-servico - Listar OS
- [x] GET /ordens-servico/{id} - Buscar OS por ID
- [x] GET /ordens-servico/cliente/{clienteId} - Listar por cliente
- [x] GET /ordens-servico/veiculo/{veiculoId} - Listar por veículo
- [x] GET /ordens-servico/status/{status} - Listar por status
- [x] PATCH /ordens-servico/{id}/iniciar-diagnostico - Iniciar diagnóstico
- [x] PATCH /ordens-servico/{id}/concluir-diagnostico - Concluir diagnóstico
- [x] PATCH /ordens-servico/{id}/aprovar-orcamento - Aprovar orçamento
- [x] PATCH /ordens-servico/{id}/finalizar - Finalizar OS
- [x] PATCH /ordens-servico/{id}/entregar - Entregar veículo

### APIs de Monitoramento
- [x] GET /metricas/tempo-medio-execucao - Tempo médio de execução dos serviços

### Documentação
- [x] Configurar Swagger/OpenAPI
- [x] Documentar todos os endpoints
- [x] Adicionar exemplos de requisição/resposta

---

## Fase 5 - Segurança

### Autenticação e Autorização
- [x] Implementar JWT para APIs administrativas
- [x] Criar endpoint de login
- [x] Criar middleware de autenticação
- [x] Proteger endpoints administrativos

### Validação
- [x] Validar CPF/CNPJ
- [x] Validar placa de veículo
- [x] Validar dados de entrada em todos os endpoints
- [x] Sanitizar dados para prevenir injeções

---

## Fase 6 - Testes

### Testes Unitários
- [x] Testar entidades do domínio (OrdemServico, Peca)
- [x] Testar value objects (CpfCnpj, Placa)
- [x] Testar regras de negócio
- [x] Testar validações

### Testes de Integração
- [x] Testar APIs de clientes
- [x] Testar APIs de veículos
- [x] Testar APIs de serviços
- [x] Testar APIs de peças
- [x] Testar APIs de ordens de serviço
- [x] Testar fluxo completo de criação de OS
- [x] Testar fluxo de aprovação de orçamento
- [x] Testar mudança de status da OS

### Cobertura
- [x] Garantir 90% de cobertura em domínios críticos (em progresso)
- [x] Configurar CI para rodar testes automaticamente

---

## Fase 7 - Entregáveis

### Vídeo de Demonstração
- [ ] Gravar vídeo de até 15 minutos
- [ ] Demonstrar criação de OS
- [ ] Demonstrar acompanhamento de status
- [ ] Demonstrar gestão administrativa
- [ ] Demonstrar autenticação JWT

### Documentação
- [x] Finalizar documentação DDD no Miro (https://miro.com/app/board/uXjVHZcarWY=/)
- [x] Revisar diagramas (5 frames: Linguagem Ubíqua, Event Storming, Bounded Contexts, Diagrama de Estados, Hotspots)
- [x] Verificar aplicação da linguagem ubíqua (glossário publicado no Frame 1 do Miro + `docs/ddd/01-linguagem-ubiqua.md`)

### Relatório de Vulnerabilidades
- [x] Executar scan de segurança no código (Trivy + SonarQube)
- [x] Analisar resultados (5 findings: 2 CRITICAL, 3 HIGH)
- [x] Documentar vulnerabilidades encontradas (docs/RELATORIO-SEGURANCA-GERENCIAL.md)
- [x] Documentar mitigações aplicadas (plano de ação no relatório)

### Documento de Entrega (PDF)
- [x] Criar PDF com nome do grupo (Grupo 310 - `docs/ENTREGA-FASE-1.md`)
- [x] Adicionar participantes e usernames no Discord (Danilo RM372600, Rodrigo RM372859, William RM372192)
- [x] Incluir link da documentação DDD (https://miro.com/app/board/uXjVHZcarWY=/)
- [x] Incluir link do repositório (https://github.com/daniloichaves/oficina-mecanica-tech)
- [x] Incluir relatório de análise de vulnerabilidades (docs/RELATORIO-SEGURANCA-GERENCIAL.md)

---

## Fase 8 - Finalização

### Repositório
- [x] Configurar repositório privado
- [x] Dar acesso ao usuário soat-architecture
- [ ] Verificar commit de todo código-fonte
- [x] Verificar presença de Dockerfile e docker-compose.yml
- [x] Verificar README.md completo

### Revisão Final
- [x] Revisar todos os requisitos técnicos (docs/REVISAO-FINAL.md - auditoria completa realizada)
- [x] Verificar todas as funcionalidades obrigatórias
- [x] Testar execução local via README (aplicação iniciou com sucesso)
- [x] Testar build com Docker (Maven build validado)
- [x] Validar cobertura de testes

---

## FASE 2 - Tech Challenge

### Evolução da aplicação
- [x] Refatorar para arquitetura hexagonal (ports em `domain/repositories`, adapters JPA/SMTP na infraestrutura)
- [x] Consulta de status da OS - `GET /api/ordens-servico/{id}/status`
- [x] Webhook de aprovação/recusa de orçamento - `POST /api/webhooks/orcamento` (status CANCELADA na recusa)
- [x] Listagem ordenada por status (Execução > Aguardando Aprovação > Diagnóstico > Recebida, mais antigas primeiro) com exclusão lógica de finalizadas/entregues
- [x] Notificação de mudança de status por e-mail (Mailhog em dev)
- [x] Testes automatizados cobrindo os fluxos novos (unitários + integração)

### Infraestrutura
- [x] Dockerfile e docker-compose revisados (Mailhog adicionado)
- [x] Manifestos Kubernetes em `/k8s` (Deployments, Services, ConfigMap, Secrets, HPA)
- [x] Terraform em `/infra` (cluster kind + banco de dados, documentado)
- [x] Pipeline CI/CD (build, testes, imagem GHCR, deploy em kind com smoke test)
- [x] README atualizado (arquitetura, instruções local/K8s/Terraform, collection)

### Pendências manuais (não automatizáveis)
- [x] Validar pipeline no GitHub Actions após push da branch `fase-2` (CI/CD verde: build+testes, imagem GHCR e deploy em kind com smoke test)
- [x] Renovar o secret `SONAR_TOKEN` do repositório (token renovado; job SonarQube verde no workflow Security)
- [x] Ajustar `security.yml` para repo privado sem GHAS: removidos jobs Dependency Review e CodeQL (exigem Advanced Security, indisponível em repo privado de conta pessoal); Trivy passou a reportar em tabela no log (sem upload SARIF) com offline-scan
- [ ] Testar `terraform apply` + `kubectl apply -f k8s/` de ponta a ponta com Docker local (validado até `terraform validate` e parse dos manifestos; kind não instalado nesta máquina)
- [ ] Gravar vídeo de até 15 min (roteiro em `docs/ENTREGA-FASE-2.md`) e publicar no YouTube/Vimeo (não listado)
- [ ] Inserir o link do vídeo no README e em `docs/ENTREGA-FASE-2.md`
- [ ] (Opcional) Exportar collection Postman para `docs/postman/` — Swagger já atende "ou similar"
- [ ] Gerar PDF a partir de `docs/ENTREGA-FASE-2.md` e enviar no portal do aluno
- [x] Confirmar repositório compartilhado com `soat-architecture` (verificado: repo privado, colaboradores daniloichaves, soat-architecture, rbragantini)

---

## Status Atual
- [x] Planejamento: Concluído
- [x] Arquitetura: Concluído
- [x] Implementação: Concluído
- [x] Testes Unitários: Concluído (192 testes passando)
- [x] Testes de Integração: Concluído (47 testes de integração passando)
- [x] Cobertura de Testes: Concluído (90% em domínios críticos: CpfCnpj, Placa)
- [ ] Entregáveis: Pendente
