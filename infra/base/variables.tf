variable "aws_region" {
  type        = string
  description = "AWS region"
}

variable "name" {
  type        = string
  description = "Base infrastructure name"
  default     = "school"
}

variable "environment" {
  type        = string
  description = "Environment name"
  default     = "dev"
}
