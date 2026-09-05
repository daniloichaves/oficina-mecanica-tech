provider "aws" { region = var.aws_region }

data "aws_availability_zones" "available" { state = "available" }
data "aws_caller_identity" "current" {}

locals {
  name = "oficina-${var.environment}"
  azs  = slice(data.aws_availability_zones.available.names, 0, 3)
  tags = { Project = "oficina-mecanica", Environment = var.environment, ManagedBy = "Terraform" }
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 6.0"

  name            = local.name
  cidr            = var.vpc_cidr
  azs             = local.azs
  private_subnets = [for index, _ in local.azs : cidrsubnet(var.vpc_cidr, 4, index)]
  public_subnets  = [for index, _ in local.azs : cidrsubnet(var.vpc_cidr, 4, index + 8)]

  enable_nat_gateway     = true
  single_nat_gateway     = var.environment != "production"
  one_nat_gateway_per_az = var.environment == "production"
  enable_dns_hostnames   = true

  public_subnet_tags  = { "kubernetes.io/role/elb" = 1 }
  private_subnet_tags = { "kubernetes.io/role/internal-elb" = 1 }
  tags                = local.tags
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "21.24.1"

  name               = local.name
  kubernetes_version = var.kubernetes_version
  vpc_id             = module.vpc.vpc_id
  subnet_ids         = module.vpc.private_subnets

  endpoint_public_access                   = true
  endpoint_public_access_cidrs             = var.cluster_endpoint_allowed_cidrs
  enable_cluster_creator_admin_permissions = true

  addons = {
    coredns                = {}
    eks-pod-identity-agent = { before_compute = true }
    kube-proxy             = {}
    vpc-cni                = { before_compute = true }
    aws-ebs-csi-driver     = {}
  }

  eks_managed_node_groups = {
    application = {
      instance_types = var.node_instance_types
      min_size       = var.node_min_size
      desired_size   = var.node_desired_size
      max_size       = var.node_max_size
      capacity_type  = "ON_DEMAND"
    }
  }
  tags = local.tags
}

resource "aws_security_group" "auth_lambda" {
  name_prefix = "${local.name}-auth-lambda-"
  description = "Outbound access for the authentication Lambda"
  vpc_id      = module.vpc.vpc_id
  tags        = local.tags

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  lifecycle { create_before_destroy = true }
}

provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "aws"
    args        = ["eks", "get-token", "--cluster-name", module.eks.cluster_name]
  }
}

provider "helm" {
  kubernetes = {
    host                   = module.eks.cluster_endpoint
    cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
    exec = {
      api_version = "client.authentication.k8s.io/v1beta1"
      command     = "aws"
      args        = ["eks", "get-token", "--cluster-name", module.eks.cluster_name]
    }
  }
}

resource "kubernetes_namespace" "oficina" {
  metadata { name = "oficina-${var.environment}" }
}

resource "helm_release" "traefik" {
  name       = "traefik"
  repository = "https://traefik.github.io/charts"
  chart      = "traefik"
  namespace  = kubernetes_namespace.oficina.metadata[0].name
  version    = var.traefik_chart_version

  values = [yamlencode({
    deployment   = { replicas = 2 }
    service      = { type = "LoadBalancer" }
    log          = { format = "json" }
    accessLog    = { enabled = true, format = "json" }
    ingressClass = { enabled = true, isDefaultClass = false, name = "traefik" }
    providers = {
      kubernetesIngress = { namespaces = [kubernetes_namespace.oficina.metadata[0].name] }
      file = {
        enabled = true
        content = {
          http = {
            middlewares = {
              lambda-auth = {
                forwardAuth = {
                  address             = var.auth_function_url
                  authRequestHeaders  = ["Authorization", "X-Correlation-ID"]
                  authResponseHeaders = ["X-Auth-Client-Id", "X-Auth-Cpf", "X-Correlation-ID"]
                  trustForwardHeader  = true
                }
              }
            }
          }
        }
      }
    }
    resources = {
      requests = { cpu = "100m", memory = "128Mi" }
      limits   = { cpu = "500m", memory = "256Mi" }
    }
  })]
}

resource "helm_release" "metrics_server" {
  name       = "metrics-server"
  repository = "https://kubernetes-sigs.github.io/metrics-server/"
  chart      = "metrics-server"
  namespace  = "kube-system"
  version    = var.metrics_server_chart_version
}

resource "kubernetes_secret" "datadog" {
  count = var.datadog_api_key == null ? 0 : 1
  metadata {
    name      = "datadog-secret"
    namespace = kubernetes_namespace.oficina.metadata[0].name
  }
  data = { api-key = var.datadog_api_key }
}

resource "helm_release" "datadog" {
  count      = var.datadog_api_key == null ? 0 : 1
  name       = "datadog"
  repository = "https://helm.datadoghq.com"
  chart      = "datadog"
  namespace  = kubernetes_namespace.oficina.metadata[0].name
  version    = var.datadog_chart_version
  values = [yamlencode({
    datadog = {
      apiKeyExistingSecret = kubernetes_secret.datadog[0].metadata[0].name
      site                 = var.datadog_site
      logs                 = { enabled = true, containerCollectAll = true }
      apm                  = { portEnabled = true }
      processAgent         = { enabled = true }
      kubeStateMetricsCore = { enabled = true }
    }
    clusterAgent = { enabled = true, replicas = 2 }
  })]
}
