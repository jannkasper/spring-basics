#!/bin/bash

# Exit on any error
set -e

# This script builds and pushes Docker images to Google Container Registry

if [ -z "$1" ]; then
  echo "Error: No project ID provided."
  echo "Usage: $0 <project-id>"
  exit 1
fi

PROJECT_ID=$1
VERSION=$(date +"%Y%m%d%H%M")

echo "Building and pushing Docker images to gcr.io/${PROJECT_ID}..."

# Configure Docker to use gcloud as a credential helper
gcloud auth configure-docker

# Build Config Service
echo "Building Config Service..."
cd ../config-service
docker build --no-cache --platform=linux/amd64 -t gcr.io/${PROJECT_ID}/config-service:${VERSION} -t gcr.io/${PROJECT_ID}/config-service:latest .
docker push gcr.io/${PROJECT_ID}/config-service:${VERSION}
docker push gcr.io/${PROJECT_ID}/config-service:latest
cd ..

# Build Discovery Service
echo "Building Discovery Service..."
cd discovery-service
docker build --no-cache --platform=linux/amd64 -t gcr.io/${PROJECT_ID}/discovery-service:${VERSION} -t gcr.io/${PROJECT_ID}/discovery-service:latest .
docker push gcr.io/${PROJECT_ID}/discovery-service:${VERSION}
docker push gcr.io/${PROJECT_ID}/discovery-service:latest
cd ..

# Build Sample Service
echo "Building Sample Service..."
cd sample-service
docker build --no-cache --platform=linux/amd64 -t gcr.io/${PROJECT_ID}/sample-service:${VERSION} -t gcr.io/${PROJECT_ID}/sample-service:latest .
docker push gcr.io/${PROJECT_ID}/sample-service:${VERSION}
docker push gcr.io/${PROJECT_ID}/sample-service:latest
cd ..

# Build Gateway Service
echo "Building Gateway Service..."
cd gateway-service
docker build --no-cache --platform=linux/amd64 -t gcr.io/${PROJECT_ID}/gateway-service:${VERSION} -t gcr.io/${PROJECT_ID}/gateway-service:latest .
docker push gcr.io/${PROJECT_ID}/gateway-service:${VERSION}
docker push gcr.io/${PROJECT_ID}/gateway-service:latest
cd ..

echo "All images built and pushed to gcr.io/${PROJECT_ID}" 