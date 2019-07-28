#!/bin/bash

DIR="$( cd "$(dirname "$0")" ; pwd -P )"

cd $DIR/docker-conf
docker build --no-cache --rm . -t pontusvisiongdpr/pontus-graphdb-odb-conf

docker push pontusvisiongdpr/pontus-graphdb-odb-conf

