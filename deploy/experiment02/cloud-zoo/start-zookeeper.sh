#!/usr/bin/env bash

TICK_TIME=$1
export ZOO_TICK_TIME=$TICK_TIME
docker-compose up -d

