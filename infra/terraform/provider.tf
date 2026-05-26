provider "aws" {
  region = var.aws_region
}

provider "tls" {}

provider "cloudinit" {}

provider "null" {}
