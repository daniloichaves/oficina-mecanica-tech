environment                = "homologation"
vpc_id                     = "vpc-REPLACE_ME"
private_subnet_ids         = ["subnet-REPLACE_ME_A", "subnet-REPLACE_ME_B"]
eks_node_security_group_id = "sg-REPLACE_EKS"
lambda_security_group_id   = "sg-REPLACE_LAMBDA"
instance_class             = "db.t4g.micro"
allocated_storage          = 20
max_allocated_storage      = 100
