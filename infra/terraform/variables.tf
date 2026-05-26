variable "aws_region" {
  type        = string
  description = "AWS region"
}

variable "cluster_name" {
  type        = string
  description = "EKS cluster name"
}

variable "environment" {
  type        = string
  description = "Environment name"
}

variable "db_name" {
  type        = string
  description = "RDS database name"
  default     = "schooldb"
}

variable "db_username" {
  type        = string
  description = "RDS master username"
  default     = "admin"
}

variable "db_password" {
  type        = string
  description = "RDS master password"
  sensitive   = true
}
