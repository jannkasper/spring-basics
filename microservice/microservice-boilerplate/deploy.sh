#!/bin/bash

# Exit on any error
set -e

echo "Starting deployment process..."

# Set the registry prefix (if using a container registry)
# REGISTRY="yourregistry.com/"
# Remove or set as needed
REGISTRY=""

# Check if version file exists and read version from it
if [ -f .version ]; then
  VERSION=$(cat .version)
  echo "Using version: ${VERSION} from .version file"
else
  # If no version file, use latest tag
  VERSION="latest"
  echo "No version file found, using 'latest' tag"
fi

# Apply Kubernetes configurations
echo "Applying Kubernetes configurations..."

# Create namespace first (if it doesn't exist)
kubectl apply -f k8s/namespace.yaml

# Apply ConfigMap for config repo
kubectl apply -f k8s/config-repo-configmap.yaml

# Apply services in order
echo "Deploying Config Service..."
kubectl apply -f k8s/config-service.yaml

# Wait for Config Service to be ready
echo "Waiting for Config Service to be ready..."
kubectl wait --for=condition=ready pod -l app=config-service -n microservice-ns --timeout=60s

echo "Deploying Discovery Service..."
kubectl apply -f k8s/discovery-service.yaml

# Wait for Discovery Service to be ready
echo "Waiting for Discovery Service to be ready..."
kubectl wait --for=condition=ready pod -l app=discovery-service -n microservice-ns --timeout=60s

echo "Deploying Sample Service..."
kubectl apply -f k8s/sample-service.yaml

# Wait for Sample Service to be ready
echo "Waiting for Sample Service to be ready..."
kubectl wait --for=condition=ready pod -l app=sample-service -n microservice-ns --timeout=60s

echo "Deploying Gateway Service..."
kubectl apply -f k8s/gateway-service.yaml

# Wait for Gateway Service to be ready
echo "Waiting for Gateway Service to be ready..."
kubectl wait --for=condition=ready pod -l app=gateway-service -n microservice-ns --timeout=60s

echo "Deployment complete! Services are ready."

#echo "Setting up port forwarding for Gateway Service to localhost:8040..."
#GATEWAY_POD=$(kubectl get pod -n microservice-ns -l app=gateway-service -o jsonpath="{.items[0].metadata.name}")
#kubectl port-forward -n microservice-ns pod/$GATEWAY_POD 8040:8040

# Uncomment below for port forwarding to sample-service directly if needed
# SAMPLE_POD=$(kubectl get pod -n microservice-ns -l app=sample-service -o jsonpath="{.items[0].metadata.name}")
# kubectl port-forward -n microservice-ns pod/$SAMPLE_POD 8060:8060 