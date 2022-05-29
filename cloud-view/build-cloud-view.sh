#!/bin/bash

MODULE_NAME="cloud-view"
PORT=80

echo "build $MODULE_NAME"
docker build -t nccu/$MODULE_NAME --build-arg MODULE_NAME=$MODULE_NAME --build-arg PORT=$2 .