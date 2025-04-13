#!/bin/bash

# Exit on any error
set -e

echo "Starting build process..."

# Build all Docker images
echo "Building Docker images..."

# Set the registry prefix (if using a container registry)
# REGISTRY="yourregistry.com/"
# Remove or set as needed
REGISTRY=""

# Set version tag - using current date/time as version
VERSION=$(date +"%Y%m%d%H%M")

# Build Config Service
echo "Building Config Service..."
cd config-service
docker build -t ${REGISTRY}config-service:${VERSION} -t ${REGISTRY}config-service:latest .
cd ..

# Build Discovery Service
echo "Building Discovery Service..."
cd discovery-service
docker build -t ${REGISTRY}discovery-service:${VERSION} -t ${REGISTRY}discovery-service:latest .
cd ..

# Build Sample Service
echo "Building Sample Service..."
cd sample-service
docker build -t ${REGISTRY}sample-service:${VERSION} -t ${REGISTRY}sample-service:latest .
cd ..

# Build Gateway Service
echo "Building Gateway Service..."
cd gateway-service
docker build -t ${REGISTRY}gateway-service:${VERSION} -t ${REGISTRY}gateway-service:latest .
cd ..

echo "Build process complete. Images built with tags: ${VERSION} and latest"

# Create a version file that deploy.sh can use
echo "${VERSION}" > .version

echo "Version ${VERSION} saved to .version file" 