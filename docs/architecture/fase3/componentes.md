# Diagrama de Componentes

```mermaid
flowchart LR
    U[Cliente / Operador] -->|HTTPS| LB[AWS Network Load Balancer]
    LB --> T[Traefik API Gateway no EKS]
    T -->|ForwardAuth| L[AWS Lambda Auth CPF]
    T -->|JWT válido| APP[Oficina API no EKS]
    L -->|consulta parametrizada| RDS[(RDS PostgreSQL Multi-AZ)]
    APP --> RDS
    APP --> N[Serviço de notificações]
    SM[AWS Secrets Manager] --> L
    SM --> APP
    DD[Datadog] -->|métricas, logs e traces| T
    DD -->|métricas, logs e traces| APP
    DD -->|infraestrutura| EKS[EKS / Node Groups]
    GH[GitHub Actions + OIDC] -->|Terraform| AWS[AWS]
    GH -->|imagem| ECR[Amazon ECR]
    ECR --> APP
```

O Traefik é a única entrada pública das APIs. A aplicação e o banco permanecem em
redes privadas. As pipelines assumem roles temporários via OIDC, sem chaves AWS
permanentes.
