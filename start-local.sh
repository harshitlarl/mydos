#!/bin/bash
# Script to start the MyDOS application in local development mode

echo "Starting MyDOS in local development mode..."

# Create logs directory if it doesn't exist
mkdir -p backend/logs

# Build and start all services using Docker Compose
echo "Starting all services with Docker Compose..."
docker-compose down
docker-compose up -d

echo "Services are starting..."
echo "Backend will be available at: http://localhost:8080/api"
echo "Frontend will be available at: http://localhost:3000"
echo "Admin interface will be available at: http://localhost:8081"
echo ""
echo "To view logs, run: docker-compose logs -f"
echo "To stop all services, run: docker-compose down"