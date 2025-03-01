#!/bin/bash

# Start Keycloak
echo "Starting Keycloak..."
docker-compose up -d

# Wait for Keycloak to start
echo "Waiting for Keycloak to start..."
sleep 10

echo "======================"
echo "Keycloak is running at http://localhost:8090"
echo "Admin console: http://localhost:8090/admin"
echo "Login with admin/admin"
echo "======================"
echo "Please configure Keycloak as described in the README.md"
echo "======================"
echo ""
echo "Starting Spring Boot application..."
echo "Running: mvn spring-boot:run"
echo ""

# Start Spring Boot application
mvn spring-boot:run 