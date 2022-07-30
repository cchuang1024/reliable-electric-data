#!/bin/bash

cd /home/schemer/code/deploy/experiment02
cd edge-recorder/
docker-compose down

cd ..
cd edge-collector/
docker-compose down

cd ..
cd edge-context/
docker-compose down

