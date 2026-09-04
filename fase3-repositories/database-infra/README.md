# Oficina Database Infrastructure

Provisiona PostgreSQL 16 no Amazon RDS com criptografia KMS, autoscaling de storage,
backup, Performance Insights, logs no CloudWatch e Multi-AZ em produção. O banco é
privado e aceita conexões apenas do EKS e da Lambda de autenticação.

```bash
terraform init -backend=false
terraform validate
terraform plan -var-file=environments/homologation.tfvars
```

As credenciais do banco são geradas e rotacionáveis pelo RDS Secrets Manager. O
secret JWT é independente e criptografado pela mesma chave KMS.
