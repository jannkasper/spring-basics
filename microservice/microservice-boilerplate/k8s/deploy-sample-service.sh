#!/bin/bash

echo "Building sample-service Docker image..."
cd ../sample-service
docker build --no-cache -t sample-service:latest .
cd ..

echo "Deploying ConfigMap..."
kubectl apply -f k8s/sample-service-configmap.yaml

echo "Deploying sample-service instances..."
kubectl apply -f k8s/sample-service-deployment.yaml

echo "Waiting for instance1 deployment to complete..."
kubectl rollout status deployment/sample-service-instance1

echo "Waiting for instance2 deployment to complete..."
kubectl rollout status deployment/sample-service-instance2

echo "Sample-service deployment complete!"
echo "Service is available at: sample-service within the cluster"
echo "  - instance1: sample-service:8081/api/sample"
echo "  - instance2: sample-service:8082/api/sample"
echo ""
echo "To access the sample-service from localhost, run one of:"
echo "  ./k8s/port-forward-sample-service-instance1.sh  # For instance1 on port 8081"
echo "  ./k8s/port-forward-sample-service-instance2.sh  # For instance2 on port 8082" 