#!/bin/bash  -x

#
#/**
# * Licensed to the Apache Software Foundation (ASF) under one
# * or more contributor license agreements.  See the NOTICE file
# * distributed with this work for additional information
# * regarding copyright ownership.  The ASF licenses this file
# * to you under the Apache License, Version 2.0 (the
# * "License"); you may not use this file except in compliance
# * with the License.  You may obtain a copy of the License at
# *
# *     http://www.apache.org/licenses/LICENSE-2.0
# *
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.
# */
# 
#
# Environment Variables:
#
#   JAVA_HOME        The java implementation to use.  Overrides JAVA_HOME.
#
#   PVGDPR_CLASSPATH  Extra Java CLASSPATH entries.
#
#   PVGDPR_CLASSPATH_PREFIX Extra Java CLASSPATH entries that should be
#                    prefixed to the system classpath.
#
#   PVGDPR_HEAPSIZE   The maximum amount of heap to use.
#                    Default is unset and uses the JVMs default setting
#                    (usually 1/4th of the available memory).
#
#   PVGDPR_LIBRARY_PATH  HBase additions to JAVA_LIBRARY_PATH for adding
#                    native libraries.
#
#   PVGDPR_OPTS       Extra Java runtime options.
#
#   PVGDPR_CONF_DIR   Alternate conf dir. Default is ${PVGDPR_HOME}/conf.
#
#
bin=`dirname "$0"`
bin=`cd "$bin">/dev/null; pwd`

# This will set PVGDPR_HOME, etc.
. "$bin"/pvgdpr-config.sh

if [[ -z "$PVGDPR_HOME" ]]; then
  PVGDPR_HOME="$bin/.."
fi

if [[ -z "$CLASS" ]]; then
  CLASS=com.pontusvision.gdpr.App
fi

if [[ -z "$PVGDPR_CONF_DIR" ]]; then
   PVGDPR_CONF_DIR=${PVGDPR_HOME}/conf
fi

if [[ ! -d "$PVGDPR_HOME/datadir" ]]; then
   cd $PVGDPR_HOME/
   cat datadir.tar.gz-* | tar xvzf -
fi

cygwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
esac

# Detect if we are in hbase sources dir
in_dev_env=false
if [ -d "${PVGDPR_HOME}/target" ]; then
  in_dev_env=true
fi

read -d '' options_string << EOF
Options:
  --config DIR     Configuration direction to use. Default: ./conf
  --auth-as-server Authenticate to ZooKeeper using servers configuration
EOF
# if no args specified, show usage
#if [ $# = 0 ]; then
  #echo "Usage: pvgdpr-graphdb [<args>]"
  #echo "$options_string"
  #echo ""
  #exit 1
#fi

# get arguments
COMMAND=graph

JAVA=$JAVA_HOME/bin/java

# override default settings for this command, if applicable
if [ -f "$PVGDPR_HOME/conf/pvgdpr-env-$COMMAND.sh" ]; then
  . "$PVGDPR_HOME/conf/pvgdpr-env-$COMMAND.sh"
fi

add_size_suffix() {
    # add an 'm' suffix if the argument is missing one, otherwise use whats there
    local val="$1"
    local lastchar=${val: -1}
    if [[ "mMgG" == *$lastchar* ]]; then
        echo $val
    else
        echo ${val}m
    fi
}

if [[ -n "$PVGDPR_HEAPSIZE" ]]; then
    JAVA_HEAP_MAX="-Xmx$(add_size_suffix $PVGDPR_HEAPSIZE)"
fi

if [[ -n "$PVGDPR_OFFHEAPSIZE" ]]; then
    JAVA_OFFHEAP_MAX="-XX:MaxDirectMemorySize=$(add_size_suffix $PVGDPR_OFFHEAPSIZE)"
fi

# so that filenames w/ spaces are handled correctly in loops below
ORIG_IFS=$IFS
IFS=

# CLASSPATH initially contains $PVGDPR_CONF_DIR
CLASSPATH="${PVGDPR_CONF_DIR}"
#CLASSPATH=${CLASSPATH}:$JAVA_HOME/lib/tools.jar

add_to_cp_if_exists() {
  if [ -d "$@" ]; then
    CLASSPATH=${CLASSPATH}:"$@"
  fi
}
add_maven_deps_to_classpath() {
  f="${PVGDPR_HOME}/target/cached_classpath.txt"
  if [ ! -f "${f}" ]
  then
      echo "As this is a development environment, we need ${f} to be generated from maven (command: mvn install -DskipTests)"
      exit 1
  fi
  CLASSPATH=${CLASSPATH}:`cat "${f}"`
}


#Add the development env class path stuff
#if $in_dev_env; then
  #add_maven_deps_to_classpath
#fi

# Add libs to CLASSPATH
for f in $PVGDPR_HOME/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

for f in $PVGDPR_HOME/lib/*.properties; do
  CLASSPATH=${CLASSPATH}:$f;
done

# default log directory & file
if [ "$PVGDPR_LOG_DIR" = "" ]; then
  PVGDPR_LOG_DIR="$PVGDPR_HOME/logs"
fi
if [ "$PVGDPR_LOGFILE" = "" ]; then
  PVGDPR_LOGFILE='pvgdpr.log'
fi

function append_path() {
  if [ -z "$1" ]; then
    echo $2
  else
    echo $1:$2
  fi
}

JAVA_PLATFORM=""

# if PVGDPR_LIBRARY_PATH is defined lets use it as first or second option
if [ "$PVGDPR_LIBRARY_PATH" != "" ]; then
  JAVA_LIBRARY_PATH=$(append_path "$JAVA_LIBRARY_PATH" "$PVGDPR_LIBRARY_PATH")
fi

#If avail, add Hadoop to the CLASSPATH and to the JAVA_LIBRARY_PATH
# Allow this functionality to be disabled
if [ "$PVGDPR_DISABLE_HADOOP_CLASSPATH_LOOKUP" != "true" ] ; then
  HADOOP_IN_PATH=$(PATH="${HADOOP_HOME:-${HADOOP_PREFIX}}/bin:$PATH" which hadoop 2>/dev/null)
  if [ -f ${HADOOP_IN_PATH} ]; then
    HADOOP_JAVA_LIBRARY_PATH=$(HADOOP_CLASSPATH="$CLASSPATH" ${HADOOP_IN_PATH} \
                               org.apache.hadoop.hbase.util.GetJavaProperty java.library.path 2>/dev/null)
    if [ -n "$HADOOP_JAVA_LIBRARY_PATH" ]; then
      JAVA_LIBRARY_PATH=$(append_path "${JAVA_LIBRARY_PATH}" "$HADOOP_JAVA_LIBRARY_PATH")
    fi
    CLASSPATH=$(append_path "${CLASSPATH}" `${HADOOP_IN_PATH} classpath 2>/dev/null`)
  fi
fi

# Add user-specified CLASSPATH last
if [ "$PVGDPR_CLASSPATH" != "" ]; then
  CLASSPATH=${CLASSPATH}:${PVGDPR_CLASSPATH}
fi

# Add user-specified CLASSPATH prefix first
if [ "$PVGDPR_CLASSPATH_PREFIX" != "" ]; then
  CLASSPATH=${PVGDPR_CLASSPATH_PREFIX}:${CLASSPATH}
fi

# cygwin path translation
if $cygwin; then
  CLASSPATH=`cygpath -p -w "$CLASSPATH"`
  PVGDPR_HOME=`cygpath -d "$PVGDPR_HOME"`
  PVGDPR_LOG_DIR=`cygpath -d "$PVGDPR_LOG_DIR"`
fi

if [ -d "${PVGDPR_HOME}/build/native" -o -d "${PVGDPR_HOME}/lib/native" ]; then
  if [ -z $JAVA_PLATFORM ]; then
    JAVA_PLATFORM=`CLASSPATH=${CLASSPATH} ${JAVA} org.apache.hadoop.util.PlatformName | sed -e "s/ /_/g"`
  fi
  if [ -d "$PVGDPR_HOME/build/native" ]; then
    JAVA_LIBRARY_PATH=$(append_path "$JAVA_LIBRARY_PATH" ${PVGDPR_HOME}/build/native/${JAVA_PLATFORM}/lib)
  fi

  if [ -d "${PVGDPR_HOME}/lib/native" ]; then
    JAVA_LIBRARY_PATH=$(append_path "$JAVA_LIBRARY_PATH" ${PVGDPR_HOME}/lib/native/${JAVA_PLATFORM})
  fi
fi

# cygwin path translation
if $cygwin; then
  JAVA_LIBRARY_PATH=`cygpath -p "$JAVA_LIBRARY_PATH"`
fi
 
# restore ordinary behaviour
unset IFS

PVGDPR_OPTS="$PVGDPR_OPTS $SERVER_GC_OPTS"

if [ "$AUTH_AS_SERVER" == "true" ] || [ "$COMMAND" = "hbck" ]; then
   if [ -n "$PVGDPR_SERVER_JAAS_OPTS" ]; then
     PVGDPR_OPTS="$PVGDPR_OPTS $PVGDPR_SERVER_JAAS_OPTS"
   else
     PVGDPR_OPTS="$PVGDPR_OPTS $PVGDPR_REGIONSERVER_OPTS"
   fi
fi

if [ "x$JAVA_LIBRARY_PATH" != "x" ]; then
  PVGDPR_OPTS="$PVGDPR_OPTS -Djava.library.path=$JAVA_LIBRARY_PATH"
  export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$JAVA_LIBRARY_PATH"
fi
HEAP_SETTINGS="$JAVA_HEAP_MAX $JAVA_OFFHEAP_MAX"
# Exec unless PVGDPR_NOEXEC is set.
export CLASSPATH
cd ${PVGDPR_HOME}
if [ "${PVGDPR_NOEXEC}" != "" ]; then
  "$JAVA" -Dproc_$COMMAND -XX:OnOutOfMemoryError="kill -9 %p" -cp $CLASSPATH $HEAP_SETTINGS $PVGDPR_OPTS $CLASS "$@"
else
    #-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 \
  exec "$JAVA" \
    -Dcom.pontusvision.gdpr.log.class=org.eclipse.jetty.util.log.StrErrLog \
    -Dorg.eclipse.jetty.LEVEL=INFO \
    -Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.StrErrLog \
    -Dorg.eclipse.jetty.websocket.LEVEL=INFO \
    -Dlog4j.debug \
    -Dlog4j.configuration=file://${PVGDPR_HOME}/lib/log4j.properties \
    -Dproc_$COMMAND \
    -XX:OnOutOfMemoryError="kill -9 %p" \
    -cp $CLASSPATH $HEAP_SETTINGS $PVGDPR_OPTS $CLASS "$@"
fi
