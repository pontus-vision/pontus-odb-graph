#!/usr/bin/env bash
set -e
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
cd "$DIR"


cat vars/globals.yaml | jinja2 --strict -o ../conf/lgpd-schema.yaml templates/gdpr-schema.yaml.j2
yq -o json . ../conf/lgpd-schema.yaml  >  ../conf/lgpd-schema.json
cp ../conf/lgpd-schema.json ../conf/gdpr-schema.json

cat vars/globals.yaml | jinja2 --strict -o ../conf/all-pole templates/all-pole.j2

for i in  vars/*rules*.yaml; do
  jinja2 --strict -o create-files.sh templates/create-files.sh.j2 < "$i"
  chmod 755 create-files.sh
  ./create-files.sh "$i"
  rm ./create-files.sh
done
