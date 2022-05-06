#!/usr/bin/env bash

SERVICES=('dispatch-cloud')
PORTS=(8080)

for i in "${SERVICES[@]}"; do
  bash ./build-service-image.sh ${$SERVICES[$i]}
  bash ./push-service-image.sh ${$PORTS[$i]}
done
