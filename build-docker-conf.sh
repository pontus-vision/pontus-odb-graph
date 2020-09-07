#!/bin/bash

DIR="$( cd "$(dirname "$0")" ; pwd -P )"
set -e 

cd $DIR/docker-conf
#docker build --no-cache --rm . -t pontusvisiongdpr/pontus-graphdb-odb-conf
docker build  --rm --no-cache -f $DIR/docker-conf/Dockerfile -t pontusvisiongdpr/pontus-graphdb-odb-conf $DIR

docker push pontusvisiongdpr/pontus-graphdb-odb-conf

