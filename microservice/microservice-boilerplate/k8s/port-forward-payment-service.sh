#!/bin/bash

echo "Setting up port forwarding to access payment-service from localhost..."
echo "Access the payment-service at http://localhost:8083"
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Port forward from localhost:8083 to the service's port 8083
kubectl port-forward svc/payment-service 8083:8083 