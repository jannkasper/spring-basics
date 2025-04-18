#!/bin/bash

echo "Setting up port forwarding to access sample-service instance1 from localhost..."
echo "Access the sample-service instance1 at http://localhost:8081/api/sample"
echo "Press Ctrl+C to stop port forwarding"
echo ""

# Get the first pod with the instance1 profile
INSTANCE1_POD=$(kubectl get pods -l app=sample-service,profile=instance1 -o name | head -1)

# Port forward directly to the pod instead of through the service
kubectl port-forward $INSTANCE1_POD 8091:8081