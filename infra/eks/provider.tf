provider "aws" {
  region = var.aws_region
}

provider "tls" {}

provider "time" {}

provider "null" {}

provider "cloudinit" {}
