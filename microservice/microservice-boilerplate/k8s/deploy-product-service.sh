#!/bin/bash

echo "Building product-service Docker image..."
cd ../product-service
docker build --no-cache -t product-service:latest .
cd ..

echo "Deploying ConfigMap..."
kubectl apply -f k8s/product-service-configmap.yaml

echo "Deploying product-service..."
kubectl apply -f k8s/product-service-deployment.yaml

echo "Waiting for deployment to complete..."
kubectl rollout status deployment/product-service

echo "Product-service deployment complete!"
echo "Service is available at: product-service:8082 within the cluster"
echo ""
echo "To access the product-service from localhost, run:"
echo "  ./k8s/port-forward-product-service.sh" 