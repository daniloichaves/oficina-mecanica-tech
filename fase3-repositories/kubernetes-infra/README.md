# Oficina Kubernetes Infrastructure

Provisiona VPC multi-AZ, EKS, node groups escaláveis, Traefik, metrics-server e
Datadog para homologação e produção. O estado remoto usa S3 com locking nativo.

```bash
terraform init -backend=false
terraform validate
terraform plan -var-file=environments/homologation.tfvars
```

Antes do deploy, restrinja `cluster_endpoint_allowed_cidrs`, configure o backend S3,
o role OIDC `AWS_DEPLOY_ROLE_ARN` e a chave Datadog como secret do environment.
