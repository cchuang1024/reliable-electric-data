#!/bin/bash

declare -a SERVICES=("sim-meter" "meter-recorder" "data-sender")
declare -a PORTS=(8080 8080)

LENGTH=${#SERVICES[@]}

for (( i=0; i < ${LENGTH}; i++ ))
do
  echo "build ${SERVICES[$i]}..."
  bash ./build-service-image.sh ${SERVICES[$i]} ${PORTS[$i]}

  echo "push ${SERVICES[$i]}..."
  bash ./push-service-image.sh ${SERVICES[$i]}
done
