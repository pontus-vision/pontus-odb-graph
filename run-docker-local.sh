#!/bin/bash

docker run --privileged --name odb-local -p 3001:3001 -p 2480:2480 odb-local
