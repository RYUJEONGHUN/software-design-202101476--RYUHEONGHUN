resource "aws_security_group" "rds" {
  name        = "${var.cluster_name}-rds-sg"
  description = "Allow PostgreSQL access from EKS worker nodes"
  vpc_id      = module.vpc.vpc_id

  ingress {
    description     = "PostgreSQL from EKS nodes"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [module.eks.node_security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.tags, {
    Name = "${var.cluster_name}-rds-sg"
  })
}

resource "aws_db_subnet_group" "school" {
  name       = "${var.cluster_name}-db-subnet-group"
  subnet_ids = module.vpc.database_subnets

  tags = merge(local.tags, {
    Name = "${var.cluster_name}-db-subnet-group"
  })
}

resource "aws_db_instance" "school" {
  identifier = "${var.cluster_name}-postgres"

  engine         = "postgres"
  engine_version = "15"
  instance_class = "db.t3.micro"

  allocated_storage     = 20
  max_allocated_storage = 100
  storage_type          = "gp3"

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.school.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false
  multi_az               = false

  backup_retention_period = 3
  deletion_protection     = false
  skip_final_snapshot     = true

  tags = merge(local.tags, {
    Name = "${var.cluster_name}-postgres"
  })
}
