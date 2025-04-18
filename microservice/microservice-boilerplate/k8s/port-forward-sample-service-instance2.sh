#!/bin/bash

echo "Setting up port forwarding to access sample-service instance2 from localhost..."
echo "Access the sample-service instance2 at http://localhost:8082/api/sample"
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Get the first pod with the instance2 profile
INSTANCE2_POD=$(kubectl get pods -l app=sample-service,profile=instance2 -o name | head -1)

# Port forward directly to the pod instead of through the service
kubectl port-forward $INSTANCE2_POD 8092:8082