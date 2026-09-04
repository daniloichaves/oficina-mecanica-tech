provider "aws" { region = var.aws_region }

locals {
  name = "oficina-${var.environment}"
  tags = { Project = "oficina-mecanica", Environment = var.environment, ManagedBy = "Terraform" }
}

resource "aws_kms_key" "database" {
  description             = "RDS encryption key for ${local.name}"
  deletion_window_in_days = 30
  enable_key_rotation     = true
  tags                    = local.tags
}

resource "aws_db_subnet_group" "database" {
  name       = local.name
  subnet_ids = var.private_subnet_ids
  tags       = local.tags
}

resource "aws_security_group" "database" {
  name_prefix = "${local.name}-database-"
  description = "PostgreSQL access restricted to application and auth Lambda"
  vpc_id      = var.vpc_id
  tags        = local.tags

  ingress {
    description     = "PostgreSQL from EKS application and auth Lambda"
    protocol        = "tcp"
    from_port       = 5432
    to_port         = 5432
    security_groups = [var.eks_node_security_group_id, var.lambda_security_group_id]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  lifecycle { create_before_destroy = true }
}

resource "aws_iam_role" "rds_monitoring" {
  name = "${local.name}-rds-monitoring"
  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [{ Effect = "Allow", Principal = { Service = "monitoring.rds.amazonaws.com" }, Action = "sts:AssumeRole" }]
  })
  tags = local.tags
}

resource "aws_iam_role_policy_attachment" "rds_monitoring" {
  role       = aws_iam_role.rds_monitoring.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonRDSEnhancedMonitoringRole"
}

resource "aws_db_instance" "postgres" {
  identifier     = local.name
  engine         = "postgres"
  engine_version = var.postgres_engine_version
  instance_class = var.instance_class

  db_name                       = "oficina_mecanica"
  username                      = "oficina_admin"
  manage_master_user_password   = true
  master_user_secret_kms_key_id = aws_kms_key.database.key_id

  allocated_storage     = var.allocated_storage
  max_allocated_storage = var.max_allocated_storage
  storage_type          = "gp3"
  storage_encrypted     = true
  kms_key_id            = aws_kms_key.database.arn

  db_subnet_group_name   = aws_db_subnet_group.database.name
  vpc_security_group_ids = [aws_security_group.database.id]
  publicly_accessible    = false
  multi_az               = var.environment == "production"

  backup_retention_period         = var.environment == "production" ? 30 : 7
  backup_window                   = "03:00-04:00"
  maintenance_window              = "sun:04:00-sun:05:00"
  auto_minor_version_upgrade      = true
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]
  performance_insights_enabled    = true
  monitoring_interval             = 60
  monitoring_role_arn             = aws_iam_role.rds_monitoring.arn
  deletion_protection             = var.environment == "production"
  skip_final_snapshot             = var.environment != "production"
  final_snapshot_identifier       = var.environment == "production" ? "${local.name}-final" : null
  copy_tags_to_snapshot           = true
  apply_immediately               = var.environment != "production"
  tags                            = local.tags
}

resource "random_password" "jwt" {
  length  = 64
  special = false
}

resource "aws_secretsmanager_secret" "jwt" {
  name                    = "${local.name}/jwt"
  kms_key_id              = aws_kms_key.database.arn
  recovery_window_in_days = var.environment == "production" ? 30 : 0
  tags                    = local.tags
}

resource "aws_secretsmanager_secret_version" "jwt" {
  secret_id     = aws_secretsmanager_secret.jwt.id
  secret_string = jsonencode({ jwtSecret = random_password.jwt.result })
}
