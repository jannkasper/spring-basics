#!/bin/bash

echo "Building config-service Docker image..."
cd ../config-service
docker build --no-cache -t config-service:latest .
cd ..

echo "Creating config-repo ConfigMap..."
# First create the config-repo ConfigMap
kubectl create configmap config-repo --from-file=config-repo/ || kubectl create configmap config-repo --from-file=config-repo/ -o yaml --dry-run=client | kubectl apply -f -

echo "Deploying ConfigMap..."
kubectl apply -f k8s/config-service-configmap.yaml

echo "Deploying config-service..."
kubectl apply -f k8s/config-service-deployment.yaml

echo "Waiting for deployment to complete..."
kubectl rollout status deployment/config-service

echo "Config-service deployment complete!"
echo "Service is available at: config-service:8888 within the cluster"
echo ""
echo "To access the config-service from localhost, run:"
echo "  ./k8s/port-forward-config-service.sh" 