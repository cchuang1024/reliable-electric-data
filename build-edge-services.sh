#!/usr/bin/env bash

SERVICES=('sim-meter')
PORTS=(8080)

for i in "${SERVICES[@]}"; do
  bash ./build-service-image.sh ${SERVICES[$i]} ${PORTS[$i]}
  bash ./push-service-image.sh ${SERVICES[$i]}
done
