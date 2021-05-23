#!/usr/bin/env bash
set -e
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
cd $DIR


cat vars/globals.yaml | jinja2 --strict -o ../conf/lgpd-schema.yaml templates/gdpr-schema.yaml.j2
yq . ../conf/lgpd-schema.yaml  >  ../conf/lgpd-schema.json

cat vars/globals.yaml | jinja2 --strict -o ../conf/all-pole templates/all-pole.j2

cat vars/*rules*.yaml | jinja2 --strict -o create-files.sh templates/create-files.sh.j2

chmod 755 create-files.sh

./create-files.sh

rm ./create-files.sh

