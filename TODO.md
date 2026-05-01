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
- [ ] Finalizar documentação DDD no Miro
- [ ] Revisar diagramas
- [ ] Verificar aplicação da linguagem ubíqua

### Relatório de Vulnerabilidades
- [x] Executar scan de segurança no código (Trivy + SonarQube)
- [x] Analisar resultados (5 findings: 2 CRITICAL, 3 HIGH)
- [x] Documentar vulnerabilidades encontradas (docs/RELATORIO-SEGURANCA-GERENCIAL.md)
- [x] Documentar mitigações aplicadas (plano de ação no relatório)

### Documento de Entrega (PDF)
- [ ] Criar PDF com nome do grupo
- [ ] Adicionar participantes e usernames no Discord
- [ ] Incluir link da documentação DDD
- [ ] Incluir link do repositório
- [ ] Incluir relatório de análise de vulnerabilidades

---

## Fase 8 - Finalização

### Repositório
- [ ] Configurar repositório privado
- [ ] Dar acesso ao usuário soat-architecture
- [ ] Verificar commit de todo código-fonte
- [x] Verificar presença de Dockerfile e docker-compose.yml
- [x] Verificar README.md completo

### Revisão Final
- [ ] Revisar todos os requisitos técnicos
- [x] Verificar todas as funcionalidades obrigatórias
- [x] Testar execução local via README (aplicação iniciou com sucesso)
- [x] Testar build com Docker (Maven build validado)
- [x] Validar cobertura de testes

---

## Status Atual
- [x] Planejamento: Concluído
- [x] Arquitetura: Concluído
- [x] Implementação: Concluído
- [x] Testes Unitários: Concluído (192 testes passando)
- [x] Testes de Integração: Concluído (47 testes de integração passando)
- [x] Cobertura de Testes: Concluído (90% em domínios críticos: CpfCnpj, Placa)
- [ ] Entregáveis: Pendente
