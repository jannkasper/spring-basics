#!/bin/bash

echo "Setting up port forwarding to access discovery-service from localhost..."
echo "Access the discovery-service at http://localhost:8761"
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Port forward from localhost:8761 to the service's port 8761
kubectl port-forward svc/discovery-service 8761:8761 