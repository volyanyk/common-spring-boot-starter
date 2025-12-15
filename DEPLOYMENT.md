# Deployment Plan

## 1. Containerization
The application is containerized using Docker. A multi-stage `Dockerfile` is provided to ensure a small final image size (using Alpine JRE).

**Build Command:**
```bash
docker build -t microservice-starter:latest .
```

## 2. CI/CD Pipeline
Select your CI/CD provider using the provided script:
```bash
./scripts/setup-cicd.sh [github|gitlab|jenkins]
```
The pipeline includes stages for:
1.  **Build**: Compiles Java code.
2.  **Test**: Runs unit and integration tests.
3.  **Package**: Builds the Docker image.
4.  **Publish**: Pushes the image to a container registry (Docker Hub / ECR / Nexus).

## 3. Infrastructure Requirements
- **Kubernetes Cluster** (EKS, GKE, AKS, or local Minikube)
- **PostgreSQL Database** (managed RDS or in-cluster Helm chart)
- **Redis Config** (ElastiCache or Helm chart)
- **Kafka/RabbitMQ** (MSK or Helm chart)

## 4. Kubernetes Deployment (Example)
Create `k8s/deployment.yaml` (not included in starter but here is a sample):

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: microservice-starter
spec:
  replicas: 2
  selector:
    matchLabels:
      app: microservice-starter
  template:
    metadata:
      labels:
        app: microservice-starter
    spec:
      containers:
        - name: app
          image: microservice-starter:latest
          env:
            - name: SPRING_DATASOURCE_URL
              valueFrom:
                secretKeyRef:
                  name: db-secret
                  key: url
            - name: SPRING_DATA_REDIS_HOST
              value: "redis-service"
          ports:
            - containerPort: 8080
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
```

## 5. Rollout Strategy
- **Rolling Update**: Default strategy in Kubernetes. Zero downtime deployment.
- **Canary**: Use Istio or Argo Rollouts for advanced traffic splitting if needed.
