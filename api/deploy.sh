#!/bin/bash
# This script builds and pushes the api docker images and then deploys
# them to the kubernetes cluster. It requires the following environment
# variables to work:
#   DOCKER_USER
#   DOCKER_PASSWORD
#
# usage: deploy.sh <image name> <k8s deployment name> <k8s container name>
#
# The image name should not contain a tag. That value will be lifted from
# the version field of package.json.
set -e

IMAGE_NAME=$1
# Get the semantic version from the package file.
IMAGE_TAG=$(sed -rn 's/^.*"version": "(.*)",$/\1/p' package.json)
DOCKER_IMAGE=$IMAGE_NAME:$IMAGE_TAG

docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}

# This should already be deployed.
if (docker pull $DOCKER_USER/$DOCKER_IMAGE); then
    echo "An image with this tag already exists. Skipping deployment..."
    exit 0
fi

docker build -t $DOCKER_USER/$DOCKER_IMAGE .
docker push $DOCKER_USER/$DOCKER_IMAGE

KUBE_DEPLOYMENT=$2
CONTAINER_NAME=$3

kubectl set image deployment/$KUBE_DEPLOYMENT $CONTAINER_NAME=$DOCKER_USER/$DOCKER_IMAGE
kubectl rollout status deployment $KUBE_DEPLOYMENT
