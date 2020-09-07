#!/usr/bin/env bash
set -e
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
cd $DIR

cat vars/globals.yaml | jinja2 --strict -o create-files.sh templates/create-files.sh.j2

chmod 755 create-files.sh

./create-files.sh

rm ./create-files.sh

