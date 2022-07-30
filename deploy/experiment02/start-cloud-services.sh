#!/bin/bash

ROOT="/home/schemer/code/deploy/experiment02"
TICK_TIME=$1
DEFAULT_TICK_TIME=2000

cd $ROOT
cd cloud-context/
docker-compose up -d

cd ..
cd cloud-zoo/

if [ -z ${1+x} ];
then
	echo 'zookeeper tick time set to default'
	sh ./start-zookeeper.sh $DEFAULT_TICK_TIME
else
	echo "zookeeper tick time set to $TICK_TIME"
	sh ./start-zookeeper.sh $TICK_TIME
fi

cd ..
cd cloud-services/
docker-compose up -d

cd $ROOT
