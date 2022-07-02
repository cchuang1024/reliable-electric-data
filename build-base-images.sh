#!/usr/bin/env bash

echo "build build-stage image"
docker build -t nccu/red-build -f Dockerfile.build .

echo "build jdk image."
docker build -t nccu/jdk11 -f Dockerfile.jdk .
