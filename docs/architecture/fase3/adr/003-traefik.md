# ADR 003 — Traefik como API Gateway

**Status:** Aceita

Traefik foi escolhido por integração nativa com Kubernetes, descoberta por Ingress,
logs JSON, métricas e suporte a ForwardAuth. O serviço da aplicação é `ClusterIP`,
impedindo bypass direto do gateway. O dashboard administrativo não é público.

Em AWS o chart oficial é instalado pelo Terraform e exposto por LoadBalancer. TLS,
DNS e o endereço definitivo do ForwardAuth serão ativados quando domínio e URL da
Lambda estiverem disponíveis.
