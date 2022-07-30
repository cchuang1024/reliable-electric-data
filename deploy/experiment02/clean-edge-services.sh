#!/bin/bash

ROOT="/home/schemer/code/deploy/experiment02"

cd $ROOT
cd edge-recorder/
docker-compose down -v

cd ..
cd edge-collector/
docker-compose down -v

cd ..
cd edge-context/
docker-compose down -v

cd $ROOT
