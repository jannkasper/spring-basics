#!/bin/bash

echo "Building order-service Docker image..."
cd ../order-service
docker build --no-cache -t order-service:latest .
cd ..

echo "Deploying ConfigMap..."
kubectl apply -f k8s/order-service-configmap.yaml

echo "Deploying order-service..."
kubectl apply -f k8s/order-service-deployment.yaml

echo "Waiting for deployment to complete..."
kubectl rollout status deployment/order-service

echo "Order-service deployment complete!"
echo "Service is available at: order-service:8081 within the cluster"
echo ""
echo "To access the order-service from localhost, run:"
echo "  ./k8s/port-forward-order-service.sh" 