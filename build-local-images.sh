#!/usr/bin/env bash

echo "local build"
~/gradle/bin/gradle clean build -x test

echo "build local image"
docker build -t nccu/red-build -f Dockerfile.local .

echo "build jdk image."
docker build -t nccu/jdk11 -f Dockerfile.jdk .
