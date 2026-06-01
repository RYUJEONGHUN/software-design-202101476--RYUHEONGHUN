# AWS 비용 절감 / 복구 Runbook

## 유지할 리소스

- Route 53 hosted zone: `classhubforgoogle.site`
- ACM certificate: `arn:aws:acm:ap-northeast-2:815459455266:certificate/7f5f3387-17ac-4cf6-8488-6c3e431d3490`
- ECR repository: `815459455266.dkr.ecr.ap-northeast-2.amazonaws.com/school-backend`
- RDS PostgreSQL endpoint: `school-dev-postgres.cvyyg04qe2ul.ap-northeast-2.rds.amazonaws.com:5432`
- RDS database: `schooldb`
- RDS username: `schooluser`
- Frontend: `https://software-design-202101476-ryuheongh.vercel.app`
- API domain: `https://api.classhubforgoogle.site`

## Terraform 레이어

### `infra/base`

장기 유지 리소스입니다.

- VPC: `vpc-0ba71f7e42e386c05`
- Public subnets:
  - `subnet-05196f37a9d88a10a`
  - `subnet-090661c2962b43a43`
- Private app subnets:
  - `subnet-00145837244cb4442`
  - `subnet-01c1ff9d8d1991fa1`
- Private DB subnets:
  - `subnet-0ee7784d3bd7791f6`
  - `subnet-0d6a7432d197385c3`
- DB subnet group: `school-base-vpc`

RDS가 이 VPC에 연결되어 있으므로 RDS를 유지하려면 `infra/base`는 destroy하지 않습니다.

### `infra/eks`

비용 절감을 위해 껐다 켤 수 있는 리소스입니다.

- EKS cluster: `school-cluster`
- Cluster endpoint: `https://DFE3E200949F9B9201AA2B989B84F917.gr7.ap-northeast-2.eks.amazonaws.com`
- Cluster security group: `sg-08a4cbd038d8a8e4c`
- Node security group: `sg-0e64087e32bba9879`

## 비용 절감 시 종료 순서

1. 현재 EKS context 확인

```powershell
aws eks update-kubeconfig --region ap-northeast-2 --name school-cluster
kubectl config current-context
```

2. ALB가 남지 않도록 Ingress 먼저 삭제

```powershell
kubectl delete ingress backend-ingress -n school
```

3. ALB 삭제 확인

```powershell
aws elbv2 describe-load-balancers --region ap-northeast-2
```

4. EKS 레이어 destroy

```powershell
cd infra/eks
terraform destroy
```

5. RDS는 데이터 보존을 위해 삭제하지 않음

필요 시 삭제가 아니라 중지만 사용합니다. RDS 중지는 최대 7일 후 자동 재시작될 수 있습니다.

```powershell
aws rds stop-db-instance --region ap-northeast-2 --db-instance-identifier school-dev-postgres
```

## 다시 복구할 때

1. RDS를 중지했다면 시작

```powershell
aws rds start-db-instance --region ap-northeast-2 --db-instance-identifier school-dev-postgres
```

2. EKS 재생성

```powershell
cd infra/eks
terraform apply
```

3. kubeconfig 갱신

```powershell
aws eks update-kubeconfig --region ap-northeast-2 --name school-cluster
```

4. RDS 보안그룹에 새 EKS node security group 허용

```powershell
terraform output node_security_group_id
```

위 값으로 RDS 보안그룹 인바운드에 PostgreSQL `5432` 허용 규칙을 추가합니다.

5. Kubernetes 배포

```powershell
kubectl apply -f k8s/
```

6. AWS Load Balancer Controller / ArgoCD가 필요한 경우 재설치

기존 설치 절차를 다시 수행한 뒤:

```powershell
kubectl apply -f argocd/school-api-application.yaml
```

7. 확인

```powershell
kubectl get pods -n school
kubectl get ingress -n school
curl -I https://api.classhubforgoogle.site/swagger-ui/index.html
```
