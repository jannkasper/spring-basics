#!/bin/bash

echo "Setting up port forwarding to access config-service from localhost..."
echo "Access the config-service at http://localhost:8888"
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Port forward from localhost:8888 to the service's port 8888
kubectl port-forward svc/config-service 8888:8888 