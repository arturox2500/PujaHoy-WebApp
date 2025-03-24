#!/bin/bash

set -e

# Image name
IMAGE_NAME="pujahoy/pujahoy"
IMAGE_TAG="latest"

echo "🔑 Login in Docker Hub..."
docker login

echo "📤 Publishing image in Docker Hub..."
docker push $IMAGE_NAME:$IMAGE_TAG

echo "✅ Image Published: $IMAGE_NAME:$IMAGE_TAG"
