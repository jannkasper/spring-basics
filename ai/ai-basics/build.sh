#!/bin/bash

echo "Building frontend..."
cd frontend && npm install && npm run build

echo "Building backend..."
cd ..
./mvnw clean package -DskipTests

echo "Build complete! Run the application with: java -jar target/ai-basics-0.0.1-SNAPSHOT.jar" 