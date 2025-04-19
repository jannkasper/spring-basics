#!/bin/bash

echo "Deleting all Kubernetes resources for the microservice project..."

# Delete all deployments
echo "Deleting deployments..."
kubectl delete deployment gateway-service
kubectl delete deployment order-service
kubectl delete deployment payment-service
kubectl delete deployment product-service
kubectl delete deployment sample-service-instance1
kubectl delete deployment sample-service-instance2
kubectl delete deployment config-service

# Delete statefulsets
echo "Deleting statefulsets..."
kubectl delete statefulset discovery-service

# Delete services
echo "Deleting services..."
kubectl delete service gateway-service
kubectl delete service order-service
kubectl delete service payment-service
kubectl delete service product-service
kubectl delete service sample-service
kubectl delete service discovery-service
kubectl delete service config-service

# Delete configmaps
echo "Deleting configmaps..."
kubectl delete configmap gateway-service-routes-config
kubectl delete configmap gateway-service-configmap
kubectl delete configmap --all

# Wait for resources to be deleted
echo "Waiting for resources to be deleted..."
sleep 5

# Verify resources are deleted
echo "Verifying resources are deleted..."
kubectl get pods
kubectl get services
kubectl get configmaps

echo "Cleanup complete!" 