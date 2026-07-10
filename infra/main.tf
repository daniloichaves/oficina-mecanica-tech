# Cluster Kubernetes local (kind). Em cloud, substituir este resource por EKS/GKE.
resource "kind_cluster" "oficina" {
  name           = "oficina"
  wait_for_ready = true

  kind_config {
    kind        = "Cluster"
    api_version = "kind.x-k8s.io/v1alpha4"

    node {
      role = "control-plane"

      extra_port_mappings {
        container_port = 30080
        host_port      = 30080
      }
    }
  }
}

provider "kubernetes" {
  host                   = kind_cluster.oficina.endpoint
  client_certificate     = kind_cluster.oficina.client_certificate
  client_key             = kind_cluster.oficina.client_key
  cluster_ca_certificate = kind_cluster.oficina.cluster_ca_certificate
}

resource "kubernetes_namespace" "oficina" {
  metadata {
    name = "oficina"
  }
}
