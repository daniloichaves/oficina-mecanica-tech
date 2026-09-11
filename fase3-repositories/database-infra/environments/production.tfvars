environment                = "production"
vpc_id                     = "vpc-REPLACE_ME"
private_subnet_ids         = ["subnet-REPLACE_ME_A", "subnet-REPLACE_ME_B", "subnet-REPLACE_ME_C"]
eks_node_security_group_id = "sg-REPLACE_EKS"
lambda_security_group_id   = "sg-REPLACE_LAMBDA"
instance_class             = "db.m7g.large"
allocated_storage          = 100
max_allocated_storage      = 1000
