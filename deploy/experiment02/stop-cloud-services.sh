#!/bin/bash

ROOT="/home/schemer/code/deploy/experiment02"

cd $ROOT
cd cloud-services/
docker-compose down

cd ..
cd cloud-zoo/
sh ./stop-zookeeper.sh

cd ..
cd cloud-context/
docker-compose down

cd $ROOT
