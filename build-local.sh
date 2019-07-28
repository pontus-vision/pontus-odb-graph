#!/bin/bash
git pull
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
VERSION=1.2.0
echo DIR is $DIR
export DISTDIR="$DIR/../pontus-dist/opt/pontus/pontus-graph/pv-gdpr-$VERSION";

CURDIR=`pwd`
cd $DIR
mvn -DskipTests clean install

if [[ ! -d $DISTDIR ]]; then
  mkdir -p $DISTDIR
fi

cd $DISTDIR

rm -rf *


cp -r $DIR/bin $DIR/conf $DISTDIR
mkdir -p $DISTDIR/lib

cp $DIR/target/pontus*.jar $DISTDIR/lib
cp $DIR/log4j.properties $DISTDIR/lib

cd ..

unlink current
ln -s pv-gdpr-$VERSION current

cd current
cp $DIR/datadir.tar.gz-* .

cd $CURDIR

echo docker cp $DISTDIR/lib/pontus-gdpr-graph-${VERSION}.jar d:/opt/pontus/pontus-graph/current/lib


