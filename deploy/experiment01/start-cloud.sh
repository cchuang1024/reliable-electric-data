#!/bin/bash
ROOT="/home/schemer/code/deploy/experiment01"
cd $ROOT

cd $ROOT/cloud-context/
docker-compose up -d

cd $ROOT/cloud-zoo/
docker-compose up -d

cd $ROOT/cloud-services/
docker-compose up -d

