#!/bin/bash

echo "Setting up port forwarding to access product-service from localhost..."
echo "Access the product-service at http://localhost:8082"
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Port forward from localhost:8082 to the service's port 8082
kubectl port-forward svc/product-service 8082:8082 