#!/bin/bash

# Start services in the correct order
echo "Starting config service..."
cd config-service
./mvnw spring-boot:run &
DISCOVERY_PID=$!
cd ..

# Wait for Discovery Service to start
echo "Waiting for Config Service to start..."
sleep 30

# Start services in the correct order
echo "Starting discovery service..."
cd discovery-service
./mvnw spring-boot:run &
DISCOVERY_PID=$!
cd ..

# Wait for Discovery Service to start
echo "Waiting for Discovery Service to start..."
sleep 30

# Start config service (if needed)
echo "Starting gateway service..."
cd gateway-service
./mvnw spring-boot:run &
GATEWAY_PID=$!
cd ..

# Wait for Gateway Service to start
echo "Waiting for Gateway Service to start..."
sleep 30

# Start first sample service instance
echo "Starting sample service instance 1..."
cd sample-service
export SPRING_PROFILES_ACTIVE=instance1
./mvnw spring-boot:run &
SAMPLE1_PID=$!

# Start second sample service instance
echo "Starting sample service instance 2..."
export SPRING_PROFILES_ACTIVE=instance2
./mvnw spring-boot:run &
SAMPLE2_PID=$!
cd ..

echo "All services started!"
echo "Discovery service running on port 8761"
echo "Gateway service running on port 8080"
echo "Sample service instance 1 running on port 8081"
echo "Sample service instance 2 running on port 8082"
echo "Try: curl http://localhost:8080/api/sample/instance/info"

echo "Press Ctrl+C to stop all services"
trap "kill $DISCOVERY_PID $GATEWAY_PID $SAMPLE1_PID $SAMPLE2_PID; exit" INT
wait