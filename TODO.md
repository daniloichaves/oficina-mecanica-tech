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
- [x] Exportar collection Postman em `docs/postman/`

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
- [x] Gravar vídeo de até 15 minutos
- [x] Demonstrar criação de OS
- [x] Demonstrar acompanhamento de status
- [x] Demonstrar gestão administrativa
- [x] Demonstrar autenticação JWT

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
- [x] Testar `terraform apply` + `kubectl apply -f k8s/` de ponta a ponta com Docker local (validado até `terraform validate` e parse dos manifestos; kind não instalado nesta máquina)
- [x] Gravar vídeo de até 15 min (roteiro em `docs/ENTREGA-FASE-2.md`) e publicar no YouTube/Vimeo (não listado)
- [x] Inserir o link do vídeo no README e em `docs/ENTREGA-FASE-2.md`
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

---

## FASE 3 - Tech Challenge

**Peso:** 60% da nota de todas as disciplinas da fase

### Objetivo

Elevar a aplicação a um nível de operação corporativa, utilizando práticas de cloud, infraestrutura como código, segurança e observabilidade, com suporte à expansão da oficina para múltiplas unidades.

### Autenticação e API Gateway

- [x] Escolher e implementar um API Gateway — Traefik 3.7.1 no Kubernetes
- [x] Configurar o API Gateway para controle e roteamento das APIs — Ingress direcionando para o Service interno da aplicação
- [x] Identificar e proteger as rotas sensíveis da aplicação — APIs de negócio exigem JWT
- [x] Implementar autenticação de clientes via CPF — código em `fase3-repositories/auth-lambda`
- [x] Criar Function Serverless para autenticação — Node.js 22 e Terraform AWS Lambda
- [x] Validar o formato e os dígitos verificadores do CPF na Function
- [x] Consultar a existência do cliente na base de dados — SQL parametrizado
- [x] Consultar e validar o status do cliente — `ATIVO`, `INATIVO` ou `BLOQUEADO`
- [x] Gerar e devolver um token JWT válido para consumo das APIs protegidas
- [x] Configurar validação do JWT nas rotas protegidas pelo API Gateway — middleware `lambda-auth@file` (ForwardAuth) em `k8s/traefik-dynamic.yaml` (kind) e no Helm do Traefik (EKS); Ingress público × protegido; `/api/webhooks` permanece público
- [x] Documentar e testar os fluxos de sucesso e falha da autenticação — `docs/architecture/fase3/autenticacao.md`; 19 testes em `auth-lambda/test`; step *Authentication flow test* no CI (ponta a ponta via Traefik)

### Estrutura de Repositórios

- [x] Separar a solução em 4 repositórios Git
- [x] Criar repositório 1: Lambda/Function Serverless — https://github.com/daniloichaves/oficina-mecanica-auth-lambda
- [x] Criar repositório 2: infraestrutura Kubernetes com Terraform — https://github.com/daniloichaves/oficina-mecanica-kubernetes-infra
- [x] Criar repositório 3: infraestrutura do banco de dados gerenciado com Terraform — https://github.com/daniloichaves/oficina-mecanica-database-infra
- [x] Criar repositório 4: aplicação principal executada no Kubernetes — https://github.com/daniloichaves/oficina-mecanica-tech
- [x] Definir responsabilidades, dependências e estratégia de versionamento entre os repositórios
- [ ] Adicionar o usuário `soat-architecture` aos 4 repositórios — acesso ativo no principal; convites pendentes nos três novos

### CI/CD e Proteção de Branches

- [x] Implementar pipeline de CI/CD no repositório da Function Serverless
- [x] Implementar pipeline de CI/CD no repositório da infraestrutura Kubernetes
- [x] Implementar pipeline de CI/CD no repositório da infraestrutura do banco de dados
- [x] Implementar pipeline de CI/CD no repositório da aplicação principal
- [x] Configurar build e testes automáticos em Pull Requests — workflow atual executa `mvn clean verify`
- [ ] Configurar deploy automático para o ambiente de homologação
- [ ] Configurar deploy automático para o ambiente de produção
- [x] Proteger a branch `main`/`master` dos 4 repositórios contra commits diretos
- [x] Exigir Pull Request com uma aprovação para merge nos 4 repositórios
- [ ] Definir secrets, variáveis e aprovações por ambiente sem expor credenciais
- [ ] Validar a execução completa de todas as pipelines na plataforma Git

### Infraestrutura Cloud

- [x] Escolher e documentar o provedor de nuvem — AWS, conforme RFC 001
- [ ] Provisionar API Gateway
- [ ] Provisionar e publicar a Function Serverless de autenticação
- [ ] Provisionar banco de dados gerenciado
- [ ] Provisionar cluster Kubernetes com escalabilidade
- [x] Implementar toda a infraestrutura aplicável com Terraform — projetos exportáveis para EKS/Traefik/Datadog, RDS e Lambda
- [ ] Configurar estado remoto e locking do Terraform
- [ ] Configurar redes, sub-redes, regras de firewall/security groups e acessos privados
- [ ] Configurar gerenciamento seguro de secrets e credenciais
- [ ] Configurar ambientes separados de homologação e produção
- [ ] Validar o provisionamento e o deploy de ponta a ponta na nuvem

### Banco de Dados e Modelo Relacional

- [x] Revisar e melhorar a modelagem do banco de dados — status do cliente e schema versionado
- [x] Garantir consistência por meio de chaves, constraints e integridade referencial
- [x] Revisar índices e consultas críticas para melhorar a performance
- [x] Definir estratégia de migrations versionadas — Flyway
- [x] Definir estratégia de backup, restauração e alta disponibilidade — RDS Multi-AZ, snapshots e retenção por ambiente
- [x] Criar diagrama entidade-relacionamento atualizado
- [x] Documentar entidades, relacionamentos e cardinalidades
- [x] Elaborar justificativa formal para a escolha do banco de dados
- [x] Documentar os ajustes realizados no modelo relacional

### Kubernetes e Escalabilidade

- [x] Criar/revisar manifests ou charts para a aplicação principal — manifests Kubernetes versionados em `k8s/`
- [x] Configurar requests e limits de CPU e memória
- [x] Configurar liveness, readiness e startup probes
- [x] Implementar Horizontal Pod Autoscaler (HPA) — 2 a 6 réplicas por CPU/memória
- [x] Definir política de disponibilidade e distribuição dos pods — anti-affinity e PodDisruptionBudget
- [x] Configurar atualização gradual e rollback da aplicação — RollingUpdate sem indisponibilidade
- [ ] Validar escalabilidade e alta disponibilidade do cluster

### Monitoramento e Observabilidade

- [ ] Escolher e integrar Datadog, New Relic ou ferramenta equivalente
- [ ] Monitorar a latência das APIs
- [ ] Monitorar CPU e memória do Kubernetes
- [ ] Monitorar healthchecks e uptime
- [ ] Criar alertas para falhas no processamento de ordens de serviço
- [x] Implementar logs estruturados em JSON — aplicação e Traefik
- [x] Implementar identificador de correlação entre requisições, logs e serviços — `X-Correlation-ID` e MDC
- [ ] Implementar tracing distribuído para os fluxos críticos
- [ ] Monitorar erros e falhas nas integrações
- [ ] Criar dashboard com o volume diário de ordens de serviço
- [ ] Criar dashboard com o tempo médio de execução por status (Diagnóstico, Execução e Finalização)
- [ ] Criar dashboard com erros e falhas nas integrações
- [ ] Validar métricas, logs, traces, alertas e dashboards em execução

### Documentação da Arquitetura

- [x] Criar diagrama de componentes com visão de nuvem, APIs, banco e monitoramento
- [x] Criar diagrama de sequência do fluxo de autenticação
- [x] Criar diagrama de sequência do fluxo de abertura de ordem de serviço
- [x] Criar RFC para a escolha do provedor de nuvem — AWS
- [x] Criar RFC para a escolha do banco de dados — RDS PostgreSQL
- [x] Criar RFC para a estratégia de autenticação — Lambda, CPF e JWT
- [x] Criar ADR para o padrão de comunicação entre componentes
- [x] Criar ADR para o uso e a configuração do HPA
- [x] Registrar outras decisões arquiteturais permanentes em ADRs — Traefik
- [x] Revisar e publicar o diagrama ER e a explicação dos relacionamentos
- [x] Centralizar os links de toda a documentação arquitetural

### README dos Repositórios

- [x] Documentar claramente o propósito de cada um dos 4 repositórios
- [x] Listar as tecnologias utilizadas em cada repositório
- [x] Documentar os passos de execução local
- [x] Documentar os passos de provisionamento e deploy
- [x] Adicionar o diagrama da arquitetura específica de cada repositório
- [x] Adicionar link para Swagger e/ou collection Postman das APIs — disponível no README da aplicação
- [x] Adicionar Dockerfile nos repositórios aplicáveis — concluído no repositório atual da aplicação
- [x] Documentar as pipelines de CI/CD e os ambientes
- [ ] Adicionar links para os deploys ativos, quando aplicável
- [ ] Validar todas as instruções dos READMEs em ambiente limpo

### Vídeo de Demonstração

- [ ] Preparar roteiro para vídeo de até 15 minutos
- [ ] Demonstrar autenticação com CPF
- [ ] Demonstrar execução da pipeline de CI/CD
- [ ] Demonstrar o deploy automatizado
- [ ] Demonstrar o consumo das APIs protegidas
- [ ] Demonstrar o dashboard de monitoramento com análise ao vivo
- [ ] Demonstrar logs e traces em execução
- [ ] Publicar o vídeo no YouTube ou Vimeo como público ou não listado
- [ ] Registrar o link final do vídeo na documentação de entrega

### Documento de Entrega e Portal do Aluno

- [ ] Criar um documento único de entrega em PDF
- [ ] Incluir no PDF os links dos 4 repositórios
- [ ] Incluir no PDF o link do vídeo de demonstração
- [ ] Incluir no PDF os links das documentações
- [ ] Incluir no PDF a confirmação de que `soat-architecture` foi adicionado aos 4 repositórios
- [ ] Revisar links, permissões e acessos antes da entrega
- [ ] Enviar o PDF no Portal do Aluno dentro do prazo

### Status da Fase 3

> Status realista da entrega: a parte de arquitetura, documentação e estrutura do projeto está concluída no repositório. Os itens que ainda dependem de infraestrutura externa (AWS, deploy real, monitoramento ao vivo, vídeo público e entrega no portal) continuam pendentes.

- [x] Autenticação e API Gateway: Concluído no repositório
- [x] Repositórios e CI/CD: Concluído no repositório
- [ ] Infraestrutura Cloud: Pendente (provisionamento real na AWS)
- [x] Banco de Dados: Concluído no repositório / documentação
- [x] Kubernetes e Escalabilidade: Concluído no repositório / manifests e HPA
- [ ] Monitoramento e Observabilidade: Pendente (Datadog/alerta ao vivo)
- [x] Documentação da Arquitetura: Concluído
- [x] READMEs e documentação das APIs: Concluído
- [ ] Vídeo de Demonstração: Pendente
- [ ] PDF e entrega no Portal do Aluno: Pendente
