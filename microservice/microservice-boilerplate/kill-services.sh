#!/bin/bash

echo "Stopping all microservices..."

# Function to kill process running on specific port
kill_service_on_port() {
  local port=$1
  local service_name=$2
  
  # Find PID using the port
  local pid=$(lsof -ti:$port)
  
  if [ -n "$pid" ]; then
    echo "Stopping $service_name on port $port (PID: $pid)..."
    kill -15 $pid
    sleep 2
    
    # Check if process still exists and force kill if necessary
    if ps -p $pid > /dev/null; then
      echo "$service_name still running, force killing..."
      kill -9 $pid
    fi
    
    echo "$service_name stopped successfully"
  else
    echo "No process found running on port $port for $service_name"
  fi
}

# Kill services in reverse order of startup
kill_service_on_port 8081 "Sample Service Instance 1"
kill_service_on_port 8082 "Sample Service Instance 2"
kill_service_on_port 8080 "Gateway Service"
kill_service_on_port 8761 "Discovery Service"
kill_service_on_port 8762 "Discovery Service"
kill_service_on_port 8763 "Discovery Service"
kill_service_on_port 8888 "Config Service"

echo "All services have been stopped!" 