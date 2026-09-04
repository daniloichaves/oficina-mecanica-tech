# Modelo Relacional e Justificativa

## Diagrama ER

```mermaid
erDiagram
    CLIENTES ||--o{ VEICULOS : possui
    CLIENTES ||--o{ ORDENS_SERVICO : solicita
    VEICULOS ||--o{ ORDENS_SERVICO : recebe
    ORDENS_SERVICO ||--o{ ITENS_SERVICO : contem
    SERVICOS ||--o{ ITENS_SERVICO : referencia
    ORDENS_SERVICO ||--o{ ITENS_PECA : utiliza
    PECAS ||--o{ ITENS_PECA : referencia
    CLIENTES {
      bigint id PK
      varchar cpf_cnpj UK
      varchar nome
      varchar status
      timestamp data_cadastro
    }
    ORDENS_SERVICO {
      bigint id PK
      bigint cliente_id FK
      bigint veiculo_id FK
      varchar status
      numeric valor_total
      timestamp data_criacao
      timestamp data_entrega
    }
```

## Justificativa

PostgreSQL foi mantido porque o domínio exige transações consistentes entre ordem,
itens e estoque, integridade referencial e consultas analíticas por status e período.
RDS reduz o trabalho operacional com backup, patching, criptografia, monitoramento e
Multi-AZ, preservando SQL e compatibilidade com JPA/Flyway.

O schema agora é governado pelo Flyway. Constraints impedem status desconhecidos,
estoque negativo e quantidades inválidas. Índices cobrem CPF, cliente/veículo das
ordens, fila por status/data e foreign keys dos itens. Produção usa RDS privado,
criptografia KMS, backup de 30 dias, Performance Insights e Multi-AZ.
