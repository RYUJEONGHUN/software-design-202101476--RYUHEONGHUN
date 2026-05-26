output "cluster_name" {
  value = module.eks.cluster_name
}

output "cluster_endpoint" {
  value = module.eks.cluster_endpoint
}

output "region" {
  value = var.aws_region
}

output "vpc_id" {
  value = module.vpc.vpc_id
}

output "private_subnets" {
  value = module.vpc.private_subnets
}

output "public_subnets" {
  value = module.vpc.public_subnets
}

output "database_subnets" {
  value = module.vpc.database_subnets
}

output "rds_endpoint" {
  value = aws_db_instance.school.endpoint
}

output "rds_db_url" {
  value = "jdbc:postgresql://${aws_db_instance.school.endpoint}/${var.db_name}"
}
