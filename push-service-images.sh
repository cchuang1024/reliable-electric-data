#!/bin/bash

REGISTRY=dispatch-cloud:54320
MODULE_NAME=$1

docker rmi $REGISTRY/nccu/$MODULE_NAME

docker tag esd-java/esd-dnp3 $REGISTRY/esd-java/esd-dnp3

docker push $REGISTRY/esd-java/esd-dnp3

