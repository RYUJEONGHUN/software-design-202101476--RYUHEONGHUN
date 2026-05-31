locals {
  tags = {
    Project     = "school"
    Environment = var.environment
    ManagedBy   = "terraform"
    Layer       = "eks"
  }
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.0"

  cluster_name    = var.cluster_name
  cluster_version = "1.30"

  cluster_endpoint_public_access           = true
  enable_cluster_creator_admin_permissions = true

  vpc_id     = var.vpc_id
  subnet_ids = var.public_subnet_ids

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
