# CI/CD and ArgoCD

## GitHub Actions

The workflow at `.github/workflows/backend-cicd.yml` builds the Spring Boot jar,
builds a Docker image, pushes it to ECR, and restarts the EKS backend deployment.

Required GitHub repository secrets:

- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

The AWS user or role needs permission for:

- ECR login and push to `school-backend`
- `eks:DescribeCluster`
- Kubernetes deployment restart through the EKS cluster credentials

## ArgoCD

ArgoCD manages Kubernetes manifests from the `k8s` directory.

The application manifest is:

```text
argocd/school-api-application.yaml
```

Apply it after ArgoCD is installed:

```powershell
kubectl apply -f argocd/school-api-application.yaml
```

`k8s/01-secret.yaml` is intentionally excluded from ArgoCD because it contains
deployment secrets and is ignored by Git. Keep it created manually in the
cluster.

## Current Flow

```text
Developer push
-> GitHub Actions
-> Build jar
-> Build Docker image
-> Push ECR latest and SHA tag
-> Restart EKS backend deployment

Git manifest change
-> ArgoCD
-> Sync k8s manifests
```
