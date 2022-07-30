#!/bin/bash

cd /home/schemer/code/deploy/experiment02
cd edge-context/
docker-compose up -d

cd ..
cd edge-collector/
docker-compose up -d

cd ..
cd edge-recorder/
docker-compose up -d

