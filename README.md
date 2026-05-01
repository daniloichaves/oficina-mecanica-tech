# Sistema de Oficina Mecânica - Tech Challenge

Sistema integrado de gestão de oficina mecânica desenvolvido como MVP para o Tech Challenge. O sistema permite gerenciar ordens de serviço, clientes, veículos, serviços e peças com controle de estoque e acompanhamento em tempo real.

## Stack Tecnológica

- **Java 21** - Linguagem principal
- **Spring Boot 3.2.5** - Framework
- **Spring Data JPA** - ORM e persistência
- **PostgreSQL 16** - Banco de dados relacional
- **SpringDoc OpenAPI** - Documentação da API (Swagger)
- **Lombok** - Redução de código boilerplate
- **Maven** - Gerenciamento de dependências
- **Docker** - Containerização

## Arquitetura

O projeto segue os princípios de **Domain-Driven Design (DDD)** com arquitetura em camadas:

```
src/main/java/com/oficina/mecanica/
├── domain/                 # Camada de Domínio
│   ├── entities/          # Entidades (Cliente, Veículo, OS, etc.)
│   ├── valueobjects/      # Value Objects (CPF/CNPJ, Placa)
│   └── repositories/      # Interfaces de repositórios
├── application/           # Camada de Aplicação
│   ├── dto/              # Data Transfer Objects
│   └── services/         # Serviços de negócio
├── infrastructure/       # Camada de Infraestrutura
│   ├── persistence/      # Implementação dos repositórios
│   ├── security/         # Configuração de segurança
│   └── config/           # Configurações gerais
└── presentation/          # Camada de Apresentação
    └── rest/             # Controllers REST
```

## Funcionalidades

### Gestão de Clientes
- CRUD completo de clientes
- Validação de CPF/CNPJ
- Busca por CPF/CNPJ

### Gestão de Veículos
- CRUD completo de veículos
- Validação de placa (formato antigo e Mercosul)
- Associação com clientes
- Busca por placa

### Gestão de Serviços
- CRUD completo de serviços
- Definição de valor e tempo estimado

### Gestão de Peças e Insumos
- CRUD completo de peças
- Controle de estoque
- Alerta de estoque baixo
- Atualização de estoque

### Ordens de Serviço
- Criação de OS com serviços e peças
- Fluxo de status automático:
  - Recebida → Em Diagnóstico → Aguardando Aprovação → Em Execução → Finalizada → Entregue
- Cálculo automático de orçamento
- Aprovação de orçamento
- Controle de estoque ao criar OS
- Consulta por cliente, veículo ou status

### Métricas
- Tempo médio de execução dos serviços

## Execução Local

### Pré-requisitos
- Java 21 ou superior
- Maven 3.6+
- PostgreSQL 16+ (ou usar Docker)

### Passo 1: Configurar Banco de Dados

**Opção A: Usar Docker (Recomendado)**
```bash
docker run -d \
  --name oficina-postgres \
  -e POSTGRES_DB=oficina_mecanica \
  -e POSTGRES_USER=oficina \
  -e POSTGRES_PASSWORD=oficina123 \
  -p 5432:5432 \
  postgres:16-alpine
```

**Opção B: PostgreSQL Local**
```sql
CREATE DATABASE oficina_mecanica;
CREATE USER oficina WITH PASSWORD 'oficina123';
GRANT ALL PRIVILEGES ON DATABASE oficina_mecanica TO oficina;
```

### Passo 2: Executar a Aplicação

```bash
cd oficina-mecanica
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

### Documentação da API

Acesse o Swagger UI em: `http://localhost:8080/swagger-ui.html`

## Execução com Docker

### Usando docker-compose (Recomendado)

```bash
cd oficina-mecanica
docker-compose up -d
```

Isso iniciará:
- PostgreSQL na porta 5432
- Aplicação Spring Boot na porta 8080

### Verificar logs

```bash
docker-compose logs -f app
```

### Parar containers

```bash
docker-compose down
```

## Endpoints da API

### Clientes
- `POST /api/clientes` - Criar cliente
- `GET /api/clientes` - Listar todos
- `GET /api/clientes/paginado` - Listar com paginação (padrão: page=0, size=10)
- `GET /api/clientes/{id}` - Buscar por ID
- `GET /api/clientes/cpf/{cpfCnpj}` - Buscar por CPF/CNPJ
- `PUT /api/clientes/{id}` - Atualizar
- `DELETE /api/clientes/{id}` - Deletar

### Veículos
- `POST /api/veiculos` - Criar veículo
- `GET /api/veiculos` - Listar todos
- `GET /api/veiculos/paginado` - Listar com paginação (padrão: page=0, size=10)
- `GET /api/veiculos/{id}` - Buscar por ID
- `GET /api/veiculos/placa/{placa}` - Buscar por placa
- `GET /api/veiculos/cliente/{clienteId}` - Listar por cliente
- `PUT /api/veiculos/{id}` - Atualizar
- `DELETE /api/veiculos/{id}` - Deletar

### Serviços
- `POST /api/servicos` - Criar serviço
- `GET /api/servicos` - Listar todos
- `GET /api/servicos/paginado` - Listar com paginação (padrão: page=0, size=10)
- `GET /api/servicos/{id}` - Buscar por ID
- `PUT /api/servicos/{id}` - Atualizar
- `DELETE /api/servicos/{id}` - Deletar

### Peças
- `POST /api/pecas` - Criar peça/insumo
- `GET /api/pecas` - Listar todas
- `GET /api/pecas/paginado` - Listar com paginação (padrão: page=0, size=10)
- `GET /api/pecas/{id}` - Buscar por ID
- `GET /api/pecas/estoque-baixo` - Listar estoque baixo
- `PUT /api/pecas/{id}` - Atualizar
- `PATCH /api/pecas/{id}/estoque` - Atualizar estoque
- `DELETE /api/pecas/{id}` - Deletar

### Ordens de Serviço
- `POST /api/ordens-servico` - Criar OS
- `GET /api/ordens-servico` - Listar todas
- `GET /api/ordens-servico/paginado` - Listar com paginação (padrão: page=0, size=10)
- `GET /api/ordens-servico/{id}` - Buscar por ID
- `GET /api/ordens-servico/cliente/{clienteId}` - Listar por cliente
- `GET /api/ordens-servico/veiculo/{veiculoId}` - Listar por veículo
- `GET /api/ordens-servico/status/{status}` - Listar por status
- `PATCH /api/ordens-servico/{id}/iniciar-diagnostico` - Iniciar diagnóstico
- `PATCH /api/ordens-servico/{id}/concluir-diagnostico` - Concluir diagnóstico
- `PATCH /api/ordens-servico/{id}/aprovar-orcamento` - Aprovar orçamento
- `PATCH /api/ordens-servico/{id}/finalizar` - Finalizar OS
- `PATCH /api/ordens-servico/{id}/entregar` - Entregar veículo

### Métricas
- `GET /api/metricas/tempo-medio-execucao` - Tempo médio de execução

## Exemplo de Uso

### 1. Criar um Cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "cpfCnpj": "12345678901",
    "nome": "João Silva",
    "telefone": "11999999999",
    "email": "joao@email.com"
  }'
```

### 2. Criar um Veículo
```bash
curl -X POST http://localhost:8080/api/veiculos \
  -H "Content-Type: application/json" \
  -d '{
    "placa": "ABC1234",
    "marca": "Toyota",
    "modelo": "Corolla",
    "ano": 2020,
    "clienteId": 1
  }'
```

### 3. Criar uma Ordem de Serviço
```bash
curl -X POST http://localhost:8080/api/ordens-servico \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "veiculoId": 1,
    "itensServico": [
      {
        "servicoId": 1,
        "quantidade": 1
      }
    ]
  }'
```

## Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| DB_URL | URL do banco de dados | jdbc:postgresql://localhost:5432/oficina_mecanica |
| DB_USERNAME | Usuário do banco | oficina |
| DB_PASSWORD | Senha do banco | oficina123 |
| JWT_SECRET | Secret para JWT | oficinaMecanicaSecretKey... |
| JWT_EXPIRATION | Expiração do token (ms) | 86400000 |

## Pipeline de Segurança e Qualidade

O projeto inclui um pipeline automatizado de análise de segurança e qualidade via script:

```bash
./oficina-mecanica-tech.sh security
```

Este comando executa sequencialmente:

| Etapa | Ferramenta | O que verifica |
|-------|-----------|---------------|
| 1 | **Trivy** | Vulnerabilidades no Dockerfile, docker-compose e código |
| 2 | **JaCoCo** | Cobertura de testes (mínimo 90% em domínios críticos) |
| 3 | **SonarQube** | Qualidade de código, duplicação e code smells |
| 4 | **Consolidação** | Gera `target/security/security-summary.md` com evidências |

### Outros comandos disponíveis

```bash
./oficina-mecanica-tech.sh trivy     # Apenas scan de segurança
./oficina-mecanica-tech.sh sonar     # Apenas análise SonarQube
./oficina-mecanica-tech.sh coverage  # Apenas cobertura JaCoCo
./oficina-mecanica-tech.sh help      # Lista todos os comandos
```

### Relatório de Vulnerabilidades

O relatório gerencial de segurança está disponível em:
- `docs/RELATORIO-SEGURANCA-GERENCIAL.md`

Inclui:
- Vulnerabilidades HIGH/CRITICAL encontradas
- Plano de ação com prioridades
- Evidências das ferramentas (Trivy, JaCoCo, SonarQube)

## Testes

```bash
mvn test
```

Para verificar cobertura de testes:
```bash
mvn clean test jacoco:report
```

O relatório será gerado em `target/site/jacoco/index.html`

## Estrutura do Projeto

```
oficina-mecanica/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Contribuição

Este projeto foi desenvolvido como parte do Tech Challenge da fase.

## Colaboradores

Danilo Ischiavolini Chaves
Rodrigo Dias Bragantini 
William de Oliveira Almeida

## Licença

Projeto acadêmico - Tech Challenge

