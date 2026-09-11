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
variable "vpc_id" { type = string }
variable "private_subnet_ids" { type = list(string) }
variable "eks_node_security_group_id" { type = string }
variable "lambda_security_group_id" { type = string }
variable "postgres_engine_version" {
  type    = string
  default = "16.8"
}
variable "instance_class" {
  type    = string
  default = "db.t4g.micro"
}
variable "allocated_storage" {
  type    = number
  default = 20
}
variable "max_allocated_storage" {
  type    = number
  default = 100
}
output "database_endpoint" { value = aws_db_instance.postgres.address }
output "database_port" { value = aws_db_instance.postgres.port }
output "database_secret_arn" {
  value     = one(aws_db_instance.postgres.master_user_secret).secret_arn
  sensitive = true
}
output "jwt_secret_arn" {
  value     = aws_secretsmanager_secret.jwt.arn
  sensitive = true
}
output "database_security_group_id" { value = aws_security_group.database.id }
output "secrets_kms_key_arn" { value = aws_kms_key.database.arn }
