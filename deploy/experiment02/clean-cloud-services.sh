#!/bin/bash

ROOT="/home/schemer/code/deploy/experiment02"

cd $ROOT
cd cloud-services/
docker-compose down -v

cd ..
cd cloud-zoo/
# sh ./stop-zookeeper.sh
docker-compose down -v

cd ..
cd cloud-context/
docker-compose down -v

cd $ROOT
