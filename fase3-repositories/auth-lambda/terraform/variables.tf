variable "aws_region" { type = string }
variable "environment" { type = string }
variable "name" {
  type    = string
  default = "oficina-auth"
}
variable "db_secret_arn" {
  type      = string
  sensitive = true
}
variable "jwt_secret_arn" {
  type      = string
  sensitive = true
}
variable "secrets_kms_key_arn" { type = string }
variable "private_subnet_ids" { type = list(string) }
variable "lambda_security_group_id" { type = string }
variable "allowed_origins" {
  type    = list(string)
  default = ["http://localhost:30080"]
}
variable "jwt_expiration_seconds" {
  type    = number
  default = 3600
}
variable "log_retention_days" {
  type    = number
  default = 30
}
output "function_url" { value = aws_lambda_function_url.auth.function_url }
