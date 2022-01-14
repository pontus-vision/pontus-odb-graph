#!/bin/bash

export TAG=${TAG:-1.13.2}
set -e 
DIR="$( cd "$(dirname "$0")" ; pwd -P )"

export DOLLAR='$'
cat $DIR/base/Dockerfile.template | envsubst > $DIR/base/Dockerfile
cd $DIR/base
docker build --rm . -t pontusvisiongdpr/pontus-track-graphdb-base:${TAG}

cd $DIR/orientdb
cat $DIR/orientdb/Dockerfile.template | envsubst > $DIR/orientdb/Dockerfile
docker build  --rm  . -t pontusvisiongdpr/pontus-track-graphdb-odb:${TAG}

cd $DIR/orientdb-pt
cat $DIR/orientdb-pt/Dockerfile.template | envsubst > $DIR/orientdb-pt/Dockerfile
docker build  --rm . -t pontusvisiongdpr/pontus-track-graphdb-odb-pt:${TAG}

#cd $DIR/orientdb-lambda
#cat $DIR/orientdb-lambda/Dockerfile.template | envsubst > $DIR/orientdb-lambda/Dockerfile
#docker build  --rm . -t pontusvisiongdpr/pontus-track-graphdb-odb-lambda:${TAG}

#cd $DIR/full-graphdb-nifi
#docker build  --rm . -t pontusvisiongdpr/pontus-track-graphdb-nifi:${TAG}

#cd $DIR/full-graphdb-nifi-pt
#docker build  --rm . -t pontusvisiongdpr/pontus-track-graphdb-nifi-pt:${TAG}

docker push pontusvisiongdpr/pontus-track-graphdb-base:${TAG}
docker push pontusvisiongdpr/pontus-track-graphdb-odb:${TAG}
docker push pontusvisiongdpr/pontus-track-graphdb-odb-pt:${TAG}
#docker push pontusvisiongdpr/pontus-track-graphdb-odb-lambda:${TAG}

#docker push pontusvisiongdpr/pontus-track-graphdb-nifi-pt:${TAG}
#docker push pontusvisiongdpr/pontus-track-graphdb-nifi:${TAG}

#cd $DIR/full-graphdb-gui
#docker build --rm . -t pontusvisiongdpr/pontus-track-graphdb-gui
#docker push pontusvisiongdpr/pontus-track-graphdb-gui


