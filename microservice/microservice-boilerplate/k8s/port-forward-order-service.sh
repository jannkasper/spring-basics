#!/bin/bash

echo "Setting up port forwarding to access order-service from localhost..."
echo "Access the order-service at http://localhost:8081"
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Port forward from localhost:8081 to the service's port 8081
kubectl port-forward svc/order-service 8081:8081 