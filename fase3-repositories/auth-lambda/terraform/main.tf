provider "aws" { region = var.aws_region }

data "archive_file" "lambda" {
  type        = "zip"
  source_dir  = "${path.module}/../"
  output_path = "${path.module}/auth-lambda.zip"
  excludes    = ["terraform", "test", ".github", ".git", "auth-lambda.zip"]
}

resource "aws_iam_role" "lambda" {
  name = "${var.name}-${var.environment}"
  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [{ Effect = "Allow", Principal = { Service = "lambda.amazonaws.com" }, Action = "sts:AssumeRole" }]
  })
}

resource "aws_iam_role_policy_attachment" "vpc" {
  role       = aws_iam_role.lambda.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy" "secrets" {
  role = aws_iam_role.lambda.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      { Effect = "Allow", Action = ["secretsmanager:GetSecretValue"], Resource = [var.db_secret_arn, var.jwt_secret_arn] },
      { Effect = "Allow", Action = ["kms:Decrypt"], Resource = var.secrets_kms_key_arn }
    ]
  })
}

resource "aws_cloudwatch_log_group" "lambda" {
  name              = "/aws/lambda/${var.name}-${var.environment}"
  retention_in_days = var.log_retention_days
}

resource "aws_lambda_function" "auth" {
  function_name    = "${var.name}-${var.environment}"
  role             = aws_iam_role.lambda.arn
  runtime          = "nodejs22.x"
  handler          = "src/index.handler"
  filename         = data.archive_file.lambda.output_path
  source_code_hash = data.archive_file.lambda.output_base64sha256
  timeout          = 10
  memory_size      = 256

  vpc_config {
    subnet_ids         = var.private_subnet_ids
    security_group_ids = [var.lambda_security_group_id]
  }

  environment {
    variables = {
      DB_SECRET_ARN          = var.db_secret_arn
      JWT_SECRET_ARN         = var.jwt_secret_arn
      JWT_EXPIRATION_SECONDS = tostring(var.jwt_expiration_seconds)
    }
  }

  depends_on = [aws_cloudwatch_log_group.lambda]
}

resource "aws_lambda_function_url" "auth" {
  function_name      = aws_lambda_function.auth.function_name
  authorization_type = "NONE"
  cors {
    allow_origins = var.allowed_origins
    allow_methods = ["POST"]
    allow_headers = ["authorization", "content-type", "x-correlation-id"]
    max_age       = 300
  }
}

resource "aws_lambda_permission" "function_url" {
  statement_id           = "AllowPublicFunctionUrl"
  action                 = "lambda:InvokeFunctionUrl"
  function_name          = aws_lambda_function.auth.function_name
  principal              = "*"
  function_url_auth_type = "NONE"
}
