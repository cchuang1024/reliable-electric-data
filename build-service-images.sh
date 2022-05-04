#!/usr/bin/env bash

echo "build sim-meter"
docker build -t nccu/sim-meter --build-arg MODULE_NAME=sim-meter --build-arg PORT=8080 .