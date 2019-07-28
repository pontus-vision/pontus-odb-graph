#!/bin/bash

if [[ ! -f $1 ]]; then
  printf "\n\nError: Failed to find file with jar hell output from maven\n\nUsage: \n\t$0 <jar hell output>\n\n"
  exit -1
fi

grep 'Dependency convergence' $1|sed -e 's/Dependency convergence error for //g ; s/ paths.*//g'|sort -u| sed -e 's/^/<exclusion><groupId>/g; s|:[0-9].*|</artifactId></exclusion>|g; s|:|</groupId><artifactId>|g'

