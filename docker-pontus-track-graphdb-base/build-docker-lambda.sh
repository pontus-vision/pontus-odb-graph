#!/bin/bash

export TAG=${TAG:-1.13.2}
set -e 
DIR="$( cd "$(dirname "$0")" ; pwd -P )"

export DOLLAR='$'
cat $DIR/base/Dockerfile.template | envsubst > $DIR/base/Dockerfile
cd $DIR/base
docker build --rm . -t pontusvisiongdpr/pontus-track-graphdb-base:${TAG}

#cd $DIR/orientdb
#cat $DIR/orientdb/Dockerfile.template | envsubst > $DIR/orientdb/Dockerfile
#docker build  --rm  . -t pontusvisiongdpr/pontus-track-graphdb-odb:${TAG}
#
#cd $DIR/orientdb-pt
#cat $DIR/orientdb-pt/Dockerfile.template | envsubst > $DIR/orientdb-pt/Dockerfile
#docker build  --rm . -t pontusvisiongdpr/pontus-track-graphdb-odb-pt:${TAG}

cd $DIR/orientdb-lambda
cat $DIR/orientdb-lambda/Dockerfile.template | envsubst > $DIR/orientdb-lambda/Dockerfile
#cat $DIR/orientdb-lambda/Dockerfile.template | envsubst > $DIR/orientdb-lambda/Dockerfile
export ORIENTDB_PASSWORD=admin

if [[ -z $FORMITI_DEV_ACCOUNT ]]; then
  docker build  --rm . -t pontusvisiongdpr/pontus-track-graphdb-odb-lambda:${TAG}
  docker push pontusvisiongdpr/pontus-track-graphdb-odb-lambda:${TAG}
else 
  if [[ $(aws --version 2>&1 ) == "aws-cli/1"* ]] ; then
    $(aws ecr get-login --no-include-email --region eu-west-2)
  else
    aws ecr get-login-password --region eu-west-2 | docker login --username AWS --password-stdin ${FORMITI_DEV_ACCOUNT}.dkr.ecr.eu-west-2.amazonaws.com
  fi

  docker build  --rm . -t pontus-track-graphdb-odb-lambda:${TAG}
  TIMESTAMP=$(date +%y%m%d_%H%M%S)
  docker tag pontus-track-graphdb-odb-lambda:${TAG} ${FORMITI_DEV_ACCOUNT}.dkr.ecr.eu-west-2.amazonaws.com/pontus-track-graphdb-odb-lambda:${TAG}
  docker push ${FORMITI_DEV_ACCOUNT}.dkr.ecr.eu-west-2.amazonaws.com/pontus-track-graphdb-odb-lambda:${TAG}
  IMAGE_SHA=$(aws ecr describe-images --repository-name pontus-track-graphdb-odb-lambda --image-ids imageTag=${TAG} | jq -r '.imageDetails[0].imageDigest')
  aws lambda update-function-code --function-name graphdb  --image-uri 558029316991.dkr.ecr.eu-west-2.amazonaws.com/pontus-track-graphdb-odb-lambda@${IMAGE_SHA}

fi


