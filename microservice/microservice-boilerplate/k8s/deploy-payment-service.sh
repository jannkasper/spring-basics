#!/bin/bash

echo "Building payment-service Docker image..."
cd ../payment-service
docker build --no-cache -t payment-service:latest .
cd ..

echo "Deploying ConfigMap..."
kubectl apply -f k8s/payment-service-configmap.yaml

echo "Deploying payment-service..."
kubectl apply -f k8s/payment-service-deployment.yaml

echo "Waiting for deployment to complete..."
kubectl rollout status deployment/payment-service

echo "Payment-service deployment complete!"
echo "Service is available at: payment-service:8083 within the cluster"
echo ""
echo "To access the payment-service from localhost, run:"
echo "  ./k8s/port-forward-payment-service.sh" 