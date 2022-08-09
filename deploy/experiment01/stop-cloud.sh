#!/bin/bash
ROOT="/home/schemer/code/deploy/experiment01"
cd $ROOT

cd $ROOT/cloud-services/
docker-compose down -v

cd $ROOT/cloud-zoo/
docker-compose down -v

cd $ROOT/cloud-context/
docker-compose down -v

