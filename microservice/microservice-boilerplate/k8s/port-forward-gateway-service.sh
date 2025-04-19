#!/bin/bash

echo "Setting up port forwarding to access gateway-service from localhost..."
echo "Access the gateway-service at http://localhost:8040"
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Get the first pod with the gateway-service label
GATEWAY_POD=$(kubectl get pods -l app=gateway-service -o name | head -1)

# Port forward directly to the pod instead of through the service
kubectl port-forward $GATEWAY_POD 8040:8040 