#!/bin/bash

echo "Building gateway-service Docker image..."
cd ../gateway-service
docker build --no-cache -t gateway-service:latest .
cd ..

echo "Deploying ConfigMaps..."
kubectl apply -f k8s/gateway-service-configmap.yaml
kubectl apply -f k8s/gateway-service-k8s-configmap.yaml

echo "Deploying gateway-service..."
kubectl apply -f k8s/gateway-service-deployment.yaml

echo "Waiting for deployment to complete..."
kubectl rollout status deployment/gateway-service

echo "Gateway-service deployment complete!"
echo "Service is available at: gateway-service:8040 within the cluster"
echo ""
echo "To access the gateway-service from localhost, run:"
echo "  ./k8s/port-forward-gateway-service.sh" 