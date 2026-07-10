output "cluster_endpoint" {
  value = kind_cluster.oficina.endpoint
}

output "kubeconfig_hint" {
  value = "kubectl config use-context kind-oficina"
}
