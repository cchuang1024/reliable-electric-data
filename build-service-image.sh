#!/usr/bin/env bash

MODULE_NAME=$1
PORT=$2

echo "build $MODULE_NAME"
docker build -t nccu/$MODULE_NAME --build-arg MODULE_NAME=$MODULE_NAME --build-arg PORT=$2 .