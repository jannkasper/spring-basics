#!/bin/bash

# Exit on any error
set -e

echo "Starting build and deployment process..."

# Execute build script
echo "Executing build script..."
./build.sh

# Execute deploy script
echo "Executing deploy script..."
./deploy.sh

echo "Build and deployment process completed successfully."