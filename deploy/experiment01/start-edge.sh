#!/bin/bash
ROOT="/home/schemer/code/deploy/experiment01"
cd $ROOT

cd $ROOT/edge-context/
docker-compose up -d

cd $ROOT/edge-services/
docker-compose up -d

