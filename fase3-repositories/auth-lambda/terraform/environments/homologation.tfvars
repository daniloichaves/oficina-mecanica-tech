aws_region               = "sa-east-1"
environment              = "homologation"
db_secret_arn            = "arn:aws:secretsmanager:sa-east-1:ACCOUNT_ID:secret:REPLACE_DB"
jwt_secret_arn           = "arn:aws:secretsmanager:sa-east-1:ACCOUNT_ID:secret:REPLACE_JWT"
secrets_kms_key_arn      = "arn:aws:kms:sa-east-1:ACCOUNT_ID:key/REPLACE_KMS"
private_subnet_ids       = ["subnet-REPLACE_A", "subnet-REPLACE_B"]
lambda_security_group_id = "sg-REPLACE_LAMBDA"
allowed_origins          = ["https://homologacao.example.com"]
