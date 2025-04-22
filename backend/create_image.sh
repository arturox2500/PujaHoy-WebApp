#!/bin/bash

set -e

# Image Name
IMAGE_NAME="pujahoy/pujahoy"
IMAGE_TAG="latest"

echo "🚀 Bulding image"

docker build -f Dockerfile -t $IMAGE_NAME:$IMAGE_TAG ..

echo "✅ Image built succesfully: $IMAGE_NAME:$IMAGE_TAG"
