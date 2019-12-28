#!/bin/bash

DIR="$( cd "$(dirname "$0")" ; pwd -P )"
#cd $DIR/docker-lib
#docker build --no-cache  --rm . -t pontusvisiongdpr/pontus-graphdb-odb-lib
docker build  -f $DIR/docker-lib/Dockerfile -t pontusvisiongdpr/pontus-graphdb-odb-lib $DIR


docker push pontusvisiongdpr/pontus-graphdb-odb-lib

