#!/bin/bash
export TAG=${TAG:-1.13.2}
export DOLLAR='$'
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
set -e 
#cd $DIR/docker-lib
#docker build --no-cache  --rm . -t pontusvisiongdpr/pontus-graphdb-odb-lib
export CURR_TAG=${CURR_TAG:-$TAG}
cat $DIR/docker-lib/Dockerfile.template | envsubst > $DIR/docker-lib/Dockerfile
docker build --progress=plain --rm  -f $DIR/docker-lib/Dockerfile -t pontusvisiongdpr/pontus-graphdb-odb-lib:${CURR_TAG} $DIR
