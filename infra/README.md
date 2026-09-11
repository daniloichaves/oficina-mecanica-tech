# Infraestrutura como Código (Terraform)

## Recursos criados

| Recurso | Descrição |
|---|---|
| `kind_cluster.oficina` | Cluster Kubernetes local (kind), com port-mapping 30080 → NodePort do Traefik |
| `kubernetes_namespace.oficina` | Namespace `oficina` |
| `kubernetes_secret.postgres` | Credenciais do banco |
| `kubernetes_persistent_volume_claim.postgres` | Volume de dados (1Gi) |
| `kubernetes_deployment.postgres` + `kubernetes_service.postgres` | Banco PostgreSQL 16 |

## Pré-requisitos

- Docker em execução
- Terraform >= 1.5
- kubectl

## Como aplicar

```bash
cd infra
terraform init
terraform apply
kubectl config use-context kind-oficina
kubectl apply -f ../k8s/   # sobe o app (o banco já provisionado converge sem mudanças)
kubectl -n oficina rollout status deployment/traefik
curl http://localhost:30080/actuator/health
```

O serviço da aplicação é interno (`ClusterIP`). Todo acesso HTTP externo passa pelo
Traefik, exposto no NodePort `30080`; o dashboard administrativo permanece interno
e não possui rota pública.

O HPA precisa do metrics-server (não instalado por padrão no kind):

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl -n kube-system patch deployment metrics-server --type=json \
  -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'
```

## Como destruir

```bash
terraform destroy
```

## Cloud

Para EKS/GKE, substituir o resource `kind_cluster` pelo módulo do provedor
(ex.: `terraform-aws-modules/eks/aws`) e apontar o provider `kubernetes` para o
endpoint do cluster gerenciado; namespace e banco permanecem iguais.
