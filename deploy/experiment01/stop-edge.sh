#!/bin/bash
ROOT="/home/schemer/code/deploy/experiment01"
cd $ROOT

cd $ROOT/edge-services/
docker-compose down -v

cd $ROOT/edge-context/
docker-compose down -v

