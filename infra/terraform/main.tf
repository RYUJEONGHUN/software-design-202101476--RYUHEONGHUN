data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  azs = slice(data.aws_availability_zones.available.names, 0, 2)

  tags = {
    Project     = "school"
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = "${var.cluster_name}-vpc"
  cidr = "10.0.0.0/16"

  azs = local.azs

  public_subnets = [
    "10.0.1.0/24",
    "10.0.2.0/24"
  ]
  private_subnets = [
    "10.0.101.0/24",
    "10.0.102.0/24"
  ]
  database_subnets = [
    "10.0.201.0/24",
    "10.0.202.0/24"
  ]

  enable_nat_gateway = true
  single_nat_gateway = true

  enable_dns_hostnames = true
  enable_dns_support   = true

  public_subnet_tags = {
    "kubernetes.io/role/elb" = 1
  }

  private_subnet_tags = {
    "kubernetes.io/role/internal-elb" = 1
  }

  tags = local.tags
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.0"

  cluster_name    = var.cluster_name
  cluster_version = "1.30"

  cluster_endpoint_public_access = true

  enable_cluster_creator_admin_permissions = true

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  eks_managed_node_groups = {
    default = {
      name = "ng"

      instance_types = ["t3.small"]

      min_size     = 2
      max_size     = 3
      desired_size = 2

      disk_size = 20
    }
  }

  tags = local.tags
}
