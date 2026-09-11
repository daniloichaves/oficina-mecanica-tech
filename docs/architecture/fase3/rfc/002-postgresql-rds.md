# RFC 002 — PostgreSQL no Amazon RDS

**Status:** Aceita

PostgreSQL 16 no RDS será o banco corporativo. A decisão prioriza transações ACID,
constraints, relacionamentos, índices compostos e compatibilidade com o sistema atual.
Produção terá Multi-AZ, storage autoscaling, backup de 30 dias, KMS, logs e Performance
Insights. O banco não terá endpoint público.

Aurora PostgreSQL foi considerado, mas seu custo e elasticidade adicional não são
necessários para a carga acadêmica inicial. DynamoDB foi descartado porque agregaria
complexidade para transações e relacionamentos já bem representados no modelo SQL.
