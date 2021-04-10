#!/bin/bash
export TAG=${TAG:-latest}
export DOLLAR='$'
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
set -e 
#cd $DIR/docker-lib
#docker build --no-cache  --rm . -t pontusvisiongdpr/pontus-graphdb-odb-lib
cat $DIR/docker-lib/Dockerfile.template | envsubst > $DIR/docker-lib/Dockerfile
docker build --rm  -f $DIR/docker-lib/Dockerfile -t pontusvisiongdpr/pontus-graphdb-odb-lib:${TAG} $DIR


docker push pontusvisiongdpr/pontus-graphdb-odb-lib:${TAG}

