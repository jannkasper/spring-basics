# Microservices Deployment Guide

This document provides comprehensive instructions for deploying microservices using three different methods:

1. Local deployment with Docker Compose
2. Kubernetes deployment
3. Terraform-managed cloud deployment

## Project Overview

This microservices architecture consists of the following components:

- **Config Service**: Spring Cloud Config Server for centralized configuration management
- **Discovery Service**: Service registry for microservices to discover each other
- **Gateway Service**: API Gateway that routes requests to appropriate microservices
- **Sample Service**: Example microservice with basic functionality

## 1. Local Deployment

### Prerequisites

- Docker and Docker Compose installed
- JDK 17 or higher
- Maven

### Building and Running Locally

#### Option 1: Using Docker Compose

1. Clone the repository and navigate to the project root:

```bash
cd microservice-boilerplate
```

2. Build and run all services with Docker Compose:

```bash
docker-compose up -d
```

This command will:
- Build Docker images for all services
- Create and start containers for each service
- Set up a Docker network for inter-service communication
- Mount the config-repo as a volume for the Config Service

3. Verify all services are running:

```bash
docker-compose ps
```

4. Access the services:
    - Config Service: http://localhost:8888
    - Discovery Service: http://localhost:8761
    - API Gateway: http://localhost:8080
    - Sample Service (direct): http://localhost:8081

5. To stop all services:

```bash
docker-compose down
```

#### Option 2: Building and Running Manually

1. Build all services:

```bash
./build.sh
```

2. Run each service individually (in separate terminals):

```bash
# Start Config Service first
cd config-service
./mvnw spring-boot:run

# Start Discovery Service next
cd discovery-service
./mvnw spring-boot:run

# Start Sample Service
cd sample-service
./mvnw spring-boot:run

# Start Gateway Service last
cd gateway-service
./mvnw spring-boot:run
```

3. The services will be available at the same ports as in the Docker Compose setup.

### Testing the Deployment

1. Verify the Discovery Service dashboard at http://localhost:8761
2. Test the API Gateway by accessing http://localhost:8080/api/users (this will route to the Sample Service)
3. View service configurations through the Config Service at http://localhost:8888/sample-service/default

## 2. Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (local like Minikube/Kind or remote)
- kubectl configured to interact with your cluster
- Docker for building container images

### Deployment Steps

1. Create a namespace for your microservices:

```bash
kubectl apply -f k8s/namespace.yaml
```

2. Build and tag the Docker images:

```bash
./build.sh
```

This script will:
- Build Docker images for all services (config, discovery, sample, gateway)
- Tag images with a timestamp version and 'latest' tag
- Create a `.version` file with the current version tag

3. If using a remote cluster, push the images to a container registry accessible by your cluster:

```bash
# For example, with Docker Hub:
docker tag config-service:latest yourusername/config-service:latest
docker push yourusername/config-service:latest
# Repeat for other services

# Update image references in k8s/*.yaml files to use your registry
```

4. Deploy the services to Kubernetes:

```bash
./deploy.sh
```

This script will:
- Create the microservice namespace
- Apply the config-repo ConfigMap
- Deploy services in the correct order (config → discovery → sample → gateway)
- Wait for each service to be ready before proceeding to the next one

5. If you prefer to deploy manually or need more control:

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Apply ConfigMap for configuration files
kubectl apply -f k8s/config-repo-configmap.yaml

# Deploy services in order
kubectl apply -f k8s/config-service.yaml
kubectl wait --for=condition=ready pod -l app=config-service -n microservice-ns --timeout=60s

kubectl apply -f k8s/discovery-service.yaml
kubectl wait --for=condition=ready pod -l app=discovery-service -n microservice-ns --timeout=60s

kubectl apply -f k8s/sample-service.yaml
kubectl apply -f k8s/gateway-service.yaml
```

6. Alternatively, deploy everything at once using Kustomize:

```bash
kubectl apply -k k8s/
```

### Accessing Services in Kubernetes

1. To access the services from outside the cluster, set up port forwarding:

```bash
# Gateway Service
kubectl port-forward -n microservice-ns svc/gateway-service 8080:8040

# Discovery Service dashboard
kubectl port-forward -n microservice-ns svc/discovery-service 8761:8761
```

2. Or create LoadBalancer/Ingress resources for production access.

### Monitoring and Troubleshooting

1. View pod status:

```bash
kubectl get pods -n microservice-ns
```

2. Check logs for a specific service:

```bash
kubectl logs -n microservice-ns deployment/sample-service
```

3. View service details:

```bash
kubectl describe service gateway-service -n microservice-ns
```

## 3. Terraform Cloud Deployment

Terraform enables infrastructure as code for deploying microservices to cloud providers like GCP, AWS, or Azure.

### Prerequisites

- Terraform installed (v1.0.0+)
- Google Cloud SDK (for GCP deployment)
- Google Cloud project with required APIs enabled:
    - Kubernetes Engine API
    - Container Registry API
    - Cloud Resource Manager API
- Service account with appropriate permissions

### Deployment Steps

1. Navigate to the terraform directory:

```bash
cd terraform
```

2. Create a `terraform.tfvars` file from the example:

```bash
cp terraform.tfvars.example terraform.tfvars
```

3. Edit `terraform.tfvars` with your specific configuration:

```hcl
project_id           = "your-gcp-project-id"
region               = "us-central1"
zone                 = "us-central1-a"
cluster_name         = "microservices-cluster"
node_count           = 3
machine_type         = "e2-standard-2"
preemptible          = true
node_service_account = "your-service-account@your-project.iam.gserviceaccount.com"
```

4. Initialize Terraform:

```bash
terraform init
```

5. See what resources will be created:

```bash
terraform plan
```

6. Create the infrastructure:

```bash
terraform apply
```

### Terraform Infrastructure Management

1. To view current state:

```bash
terraform show
```

2. To make changes to the infrastructure:

```bash
# Edit terraform.tfvars or *.tf files
terraform plan
terraform apply
```

3. To destroy the infrastructure when no longer needed:

```bash
terraform destroy
```

### Best Practices for Terraform Deployment

1. Use remote state storage:

```hcl
terraform {
  backend "gcs" {
    bucket = "my-terraform-state"
    prefix = "microservices"
  }
}
```

2. Use workspaces for different environments:

```bash
terraform workspace new dev
terraform workspace new prod
terraform apply
```

## Additional Considerations

### Secrets Management

- For local development: Use environment variables in docker-compose.yml
- For Kubernetes: Use Kubernetes Secrets
- For production: Consider a dedicated secrets management solution like HashiCorp Vault

### CI/CD Integration

This deployment structure is compatible with CI/CD pipelines:

1. **Build Phase**:
    - Compile code and run tests
    - Build Docker images
    - Push images to container registry

2. **Deployment Phase**:
    - Development: Use Docker Compose or a dev Kubernetes cluster
    - Staging/Production: Use Terraform to provision infrastructure and Kubernetes for deployments

### Monitoring and Observability

Consider adding:
- Prometheus for metrics collection
- Grafana for visualization
- ELK stack or similar for log aggregation
- Distributed tracing with Jaeger or Zipkin

## Troubleshooting

### Common Issues in Local Deployment

- Port conflicts: Ensure ports 8080, 8081, 8761, and 8888 are available
- Service dependencies: Ensure Config Service is fully up before starting other services

### Common Issues in Kubernetes

- ImagePullBackOff: Check image references and registry credentials
- CrashLoopBackOff: Check logs with `kubectl logs` command
- ConfigMap issues: Verify config-repo-configmap is correctly applied

### Common Issues in Terraform

- Insufficient permissions: Ensure service account has required roles
- API not enabled: Enable required GCP APIs
- Quota limits: Check GCP quotas for resources being provisioned