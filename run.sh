#!/bin/zsh
# Start Kafka Demo Application
# Requires: Docker Desktop, Java 21+, Maven 3.9+

set -e

cd "$(dirname "$0")"

echo "=== Kafka Demo Application ==="

# Check prerequisites
if ! command -v docker &> /dev/null; then
    echo "❌ Docker not found. Please install Docker Desktop."
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "❌ Docker is not running. Please start Docker Desktop."
    exit 1
fi

echo "✅ Docker is running"

# Build and run
echo "Building application..."
mvn clean package -DskipTests -q

echo "Starting application (Docker Compose will start automatically)..."
mvn spring-boot:run

