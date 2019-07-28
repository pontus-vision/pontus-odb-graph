#!/bin/bash
export JAVA_HOME=/etc/alternatives/jre

curl -XDELETE 'http://localhost:9200/janusgraph*'

kinit -kt /etc/security/keytabs/hbase.service.keytab hbase/`hostname -f`
/opt/pontus/pontus-hbase/current/bin/hbase shell -n /opt/pontus/pontus-graph/current/conf/hbase-drop.cmd
