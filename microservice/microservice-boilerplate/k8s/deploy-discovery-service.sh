#!/bin/bash

echo "Building discovery-service Docker image..."
cd ../discovery-service
docker build --no-cache -t discovery-service:latest .
cd ..

echo "Deploying ConfigMap..."
kubectl apply -f k8s/discovery-service-configmap.yaml

echo "Deploying discovery-service..."
kubectl apply -f k8s/discovery-service-deployment.yaml

echo "Waiting for deployment to complete..."
kubectl rollout status deployment/discovery-service

echo "Discovery-service deployment complete!"
echo "Service is available at: discovery-service:8761 within the cluster"
echo ""
echo "To access the discovery-service from localhost, run:"
echo "  ./k8s/port-forward-discovery-service.sh" 