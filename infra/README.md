# Infrastructure Layout

This project separates long-lived network resources from disposable EKS resources.

## `base`

Creates resources that should stay alive while the manually managed RDS database exists:

- VPC
- Public subnets
- Private subnets
- Database subnets

Do not destroy this layer while the manual RDS instance is attached to this VPC.

## `eks`

Creates resources that can be turned on/off for demos:

- EKS cluster
- EKS managed node group
- EKS IAM/security groups

This layer does not create or delete RDS.

## Flow

1. Apply `infra/base`.
2. Create RDS manually in the base VPC database subnets.
3. Apply `infra/eks`.
4. Add the EKS node security group to the RDS security group's PostgreSQL inbound rule.
5. Set Kubernetes `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
6. Destroy only `infra/eks` when you want to stop EKS costs.
