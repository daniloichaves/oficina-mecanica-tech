# Design Level — Aggregates, Entidades e Value Objects

Modelo tático DDD do sistema. Todos os elementos refletem o código atual em `src/main/java/com/oficina/mecanica/domain/`.

## 1. Diagrama de Classes (Aggregates)

```mermaid
classDiagram
    direction LR

    class Cliente {
        <<Aggregate Root>>
        +Long id
        +String nome
        +CpfCnpj cpfCnpj
        +String telefone
        +String email
    }

    class Veiculo {
        <<Entity>>
        +Long id
        +Placa placa
        +String marca
        +String modelo
        +Integer ano
    }

    class OrdemServico {
        <<Aggregate Root>>
        +Long id
        +StatusOrdemServico status
        +BigDecimal valorTotal
        +Boolean orcamentoAprovado
        +LocalDateTime dataCriacao
        +LocalDateTime dataEntrega
        +iniciarDiagnostico()
        +concluirDiagnostico()
        +aprovarOrcamento()
        +finalizar()
        +entregar()
        +calcularOrcamento()
    }

    class ItemServico {
        <<Entity>>
        +Long id
        +Integer quantidade
    }

    class ItemPeca {
        <<Entity>>
        +Long id
        +Integer quantidade
    }

    class Servico {
        <<Aggregate Root>>
        +Long id
        +String descricao
        +BigDecimal valor
        +Integer tempoEstimadoMinutos
    }

    class Peca {
        <<Aggregate Root>>
        +Long id
        +String descricao
        +BigDecimal valor
        +Integer quantidadeEstoque
        +atualizarEstoque(int)
    }

    class CpfCnpj {
        <<Value Object>>
        +String valor
        +validar()
    }

    class Placa {
        <<Value Object>>
        +String valor
        +validar()
    }

    class StatusOrdemServico {
        <<enum>>
        RECEBIDA
        EM_DIAGNOSTICO
        AGUARDANDO_APROVACAO
        EM_EXECUCAO
        FINALIZADA
        ENTREGUE
    }

    Cliente "1" o-- "*" Veiculo : possui
    Cliente *-- CpfCnpj
    Veiculo *-- Placa

    OrdemServico "1" --> "1" Cliente : referência
    OrdemServico "1" --> "1" Veiculo : referência
    OrdemServico "1" *-- "*" ItemServico
    OrdemServico "1" *-- "*" ItemPeca
    OrdemServico --> StatusOrdemServico

    ItemServico --> Servico : referência
    ItemPeca   --> Peca    : referência
```

## 2. Aggregates × Invariantes × Repositórios

| Aggregate Root | Invariantes principais | Repositório (porta) |
|----------------|------------------------|---------------------|
| `Cliente` | CPF/CNPJ válido e único; nome obrigatório; pode possuir 0..N `Veiculo`. | `ClienteRepository` |
| `Veiculo` | Placa válida e única; pertence a exatamente 1 Cliente. (No MVP, modelado como aggregate root próprio para reuso de queries.) | `VeiculoRepository` |
| `Servico` | Valor > 0; descrição obrigatória. | `ServicoRepository` |
| `Peca` | `quantidadeEstoque >= 0`; valor > 0. | `PecaRepository` |
| `OrdemServico` | Transições de status válidas; `valorTotal` derivado dos itens; sempre referencia 1 Cliente + 1 Veículo. | `OrdemServicoRepository` |

> **Regra geral**: cada operação de mudança de estado de OS usa **um único Aggregate por transação** (consistência forte interna), e referencia outros aggregates **apenas por id** quando possível.

## 3. Diagrama de Sequência — Criar OS

```mermaid
sequenceDiagram
    autonumber
    actor Atendente
    participant API as OrdemServicoController
    participant App as OrdemServicoService
    participant CliRepo as ClienteRepository
    participant VeicRepo as VeiculoRepository
    participant OSRepo as OrdemServicoRepository

    Atendente->>API: POST /api/ordens-servico {clienteId, veiculoId}
    API->>App: criarOS(dto)
    App->>CliRepo: findById(clienteId)
    CliRepo-->>App: Cliente
    App->>VeicRepo: findById(veiculoId)
    VeicRepo-->>App: Veiculo
    App->>App: new OrdemServico(status=RECEBIDA)
    App->>OSRepo: save(os)
    OSRepo-->>App: os persistida
    App-->>API: OrdemServicoDTO
    API-->>Atendente: 201 Created
```

## 4. Diagrama de Sequência — Aprovar Orçamento

```mermaid
sequenceDiagram
    autonumber
    actor Atendente
    participant API as OrdemServicoController
    participant App as OrdemServicoService
    participant OS as OrdemServico (Aggregate)
    participant Repo as OrdemServicoRepository

    Atendente->>API: PATCH /api/ordens-servico/{id}/aprovar-orcamento
    API->>App: aprovarOrcamento(id)
    App->>Repo: findById(id)
    Repo-->>App: OrdemServico
    App->>OS: aprovarOrcamento()
    alt status != AGUARDANDO_APROVACAO
        OS-->>App: IllegalStateException
        App-->>API: 409 Conflict
    else
        OS-->>App: status=EM_EXECUCAO, orcamentoAprovado=true
        App->>Repo: save(os)
        App-->>API: OrdemServicoDTO
        API-->>Atendente: 200 OK
    end
```

## 5. Camadas DDD do projeto

```mermaid
flowchart TB
    UI[Presentation<br/>Controllers + DTOs]:::ui
    APP[Application<br/>Services + Use Cases]:::app
    DOM[Domain<br/>Entities, VOs, Aggregates]:::dom
    INF[Infrastructure<br/>JPA Repositories, Config, Security]:::inf

    UI --> APP --> DOM
    INF -.implementa portas de.-> DOM
    UI -.depende de DTOs de.-> APP

    classDef ui  fill:#E599F7,stroke:#862E9C,color:#000
    classDef app fill:#FFE066,stroke:#F08C00,color:#000
    classDef dom fill:#FFA8A8,stroke:#C92A2A,color:#000
    classDef inf fill:#74C0FC,stroke:#1864AB,color:#000
```

| Camada | Pacote |
|--------|--------|
| Presentation | `presentation.rest` |
| Application | `application.services`, `application.dto` |
| Domain | `domain.entities`, `domain.valueobjects` |
| Infrastructure | `infrastructure.persistence`, `infrastructure.security`, `infrastructure.config` |

## 6. Decisões de modelagem relevantes

- **`Veiculo` como aggregate root próprio**: facilita consultas por placa e listagens, mesmo que conceitualmente seja parte do agregado Cliente. Trade-off consciente para suportar APIs `GET /api/veiculos/placa/{placa}`.
- **`ItemServico` e `ItemPeca` são entidades internas** do agregado `OrdemServico`: ciclo de vida atrelado à OS, não acessíveis fora dela.
- **`StatusOrdemServico` como enum**: máquina de estados pequena e estável; transições protegidas por métodos de domínio em `OrdemServico`.
- **`calcularOrcamento()` é determinístico**: não persiste eventos, apenas recalcula `valorTotal` a partir dos itens — pode ser chamado várias vezes.
