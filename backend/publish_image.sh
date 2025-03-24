#!/bin/bash

IMAGE_NAME="pujahoy"
IMAGE_TAG="latest"

read -p "Docker Hub username: " DOCKER_USER

docker login -u $DOCKER_USER

docker tag $IMAGE_NAME:$IMAGE_TAG $DOCKER_USER/$IMAGE_NAME:$IMAGE_TAG

docker push $DOCKER_USER/$IMAGE_NAME:$IMAGE_TAG