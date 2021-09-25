#!/bin/bash

DIR="$( cd "$(dirname "$0")" ; pwd -P )"
set -e 
export TAG=local
export CURR_TAG=${CURR_TAG:-$TAG}
cd $DIR
#docker build --no-cache --rm . -t pontusvisiongdpr/pontus-graphdb-odb-conf
docker build  --rm --no-cache -f $DIR/Dockerfile.local -t odb-local $DIR


