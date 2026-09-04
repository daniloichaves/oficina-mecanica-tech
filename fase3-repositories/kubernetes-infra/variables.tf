variable "aws_region" {
  type    = string
  default = "sa-east-1"
}
variable "environment" {
  type = string
  validation {
    condition     = contains(["homologation", "production"], var.environment)
    error_message = "Use homologation ou production."
  }
}
variable "vpc_cidr" { type = string }
variable "kubernetes_version" {
  type    = string
  default = "1.36"
}
variable "cluster_endpoint_allowed_cidrs" {
  type    = list(string)
  default = []
}
variable "node_instance_types" {
  type    = list(string)
  default = ["t3.medium"]
}
variable "node_min_size" {
  type    = number
  default = 2
}
variable "node_desired_size" {
  type    = number
  default = 2
}
variable "node_max_size" {
  type    = number
  default = 6
}
variable "traefik_chart_version" { type = string }
variable "metrics_server_chart_version" { type = string }
variable "datadog_chart_version" { type = string }
variable "auth_function_url" { type = string }
variable "datadog_api_key" {
  type      = string
  sensitive = true
  default   = null
}
variable "datadog_site" {
  type    = string
  default = "datadoghq.com"
}
output "cluster_name" { value = module.eks.cluster_name }
output "vpc_id" { value = module.vpc.vpc_id }
output "private_subnet_ids" { value = module.vpc.private_subnets }
output "public_subnet_ids" { value = module.vpc.public_subnets }
output "application_namespace" { value = kubernetes_namespace.oficina.metadata[0].name }
output "eks_node_security_group_id" { value = module.eks.node_security_group_id }
output "lambda_security_group_id" { value = aws_security_group.auth_lambda.id }
