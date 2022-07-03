#!/bin/bash

REGISTRY=dispatch-cloud:54320
MODULE_NAME="cloud-view"

docker rmi $REGISTRY/nccu/$MODULE_NAME

docker tag nccu/$MODULE_NAME $REGISTRY/nccu/$MODULE_NAME

docker push $REGISTRY/nccu/$MODULE_NAME

