# Pontus NiFi Processors

NOTE 1: This project is a derivative of other Apache projects; as such, the Apache License was chosen.
NOTE 2: This project has dummy self-signed certificates and keystores used for testing purposes only!


This is the Pontus NiFi processor project.  It creates various processors to put data into ElasticSearch and HBASE, including handling JWT authentication.

The project uses Apache NiFi to handle the business logic, with custom Processors
to do any high-performance tasks.

## Installing NiFi

Here are the rough procedures:

```
# login as root into the sandbox
cd /opt
wget http://mirrors.ukfast.co.uk/sites/ftp.apache.org/nifi/1.4.0/nifi-1.4.0-bin.tar.gz

tar xvfz nifi-1.4.0-bin.tar.gz
useradd nifi -G hadoop
ln -s  nifi-1.4.0  nifi
chown -R nifi:nifi /opt/nifi-1.4.0
su - nifi
cd /opt/nifi
```
## Adding a new kerberos principal
```
login to the sandbox as root, and run the following:
kinit
kadmin.local
kadmin.local:  addprinc -pw pa55word nifi
WARNING: no policy specified for nifi@EU-WEST-1.COMPUTE.AMAZONAWS.COM; defaulting to no policy
Principal "nifi@EU-WEST-1.COMPUTE.AMAZONAWS.COM" created.
kadmin.local:  xst -k /etc/security/keytabs/nifi.service.keytab nifi
Entry for principal nifi with kvno 2, encryption type aes256-cts-hmac-sha1-96 added to keytab WRFILE:/etc/security/keytabs/nifi.service.keytab.
Entry for principal nifi with kvno 2, encryption type aes128-cts-hmac-sha1-96 added to keytab WRFILE:/etc/security/keytabs/nifi.service.keytab.
Entry for principal nifi with kvno 2, encryption type des3-cbc-sha1 added to keytab WRFILE:/etc/security/keytabs/nifi.service.keytab.
Entry for principal nifi with kvno 2, encryption type arcfour-hmac added to keytab WRFILE:/etc/security/keytabs/nifi.service.keytab.

```
## Configuring properties
```
# Change the port from 8080 to 8090
sed -i 's/8080/8090/g' /opt/nifi/conf/nifi.properties  
KERBEROS_REALM=EU-WEST-1.COMPUTE.AMAZONAWS.COM

sed -i 's|nifi.kerberos.krb5.file.*|nifi.kerberos.krb5.file=/etc/krb5.conf|g' /opt/nifi/conf/nifi.properties  
sed -i "s|nifi.kerberos.service.principal.*|nifi.kerberos.service.principal=nifi@${KERBEROS_REALM}|g" /opt/nifi/conf/nifi.properties  
sed -i 's|nifi.kerberos.service.keytab.location.*|nifi.kerberos.service.keytab.location=/etc/security/keytabs/nifi.service.keytab|g' /opt/nifi/conf/nifi.properties

HOSTNAME=`hostname -f`
sed -i "s|nifi.kerberos.spnego.principal.*|nifi.kerberos.spnego.principal=HTTP/${HOSTNAME}@${KERBEROS_REALM}|g" /opt/nifi/conf/nifi.properties
sed -i "s|nifi.kerberos.spnego.keytab.location.*|nifi.kerberos.spnego.keytab.location=/etc/security/keytabs/spnego.service.keytab|g" /opt/nifi/conf/nifi.properties

# Create the following file (/opt/nifi/conf/jaas_policy_store.conf) :

tee /opt/nifi/conf/jaas_policy_store.conf <<'EOF'
Client {
   com.sun.security.auth.module.Krb5LoginModule required
   useKeyTab=true
   useTicketCache=true
   renewTicket=true
   keyTab="/etc/security/keytabs/hbase.service.keytab"
   serviceName="zookeeper"
   principal="hbase/sandbox.hortonworks.com@EU-WEST-1.COMPUTE.AMAZONAWS.COM";
};
KafkaServer {
   com.sun.security.auth.module.Krb5LoginModule required
   useKeyTab=true
   useTicketCache=true
   renewTicket=true
   storeKey=true
   keyTab="/etc/security/keytabs/kafka.service.keytab"
   principal="kafka/sandbox.hortonworks.com@EU-WEST-1.COMPUTE.AMAZONAWS.COM";
};
hdfs {
   com.sun.security.auth.module.Krb5LoginModule required
   useKeyTab=true
   storeKey=true
   renewTicket=true
   useTicketCache=true
   keyTab="/etc/security/keytabs/hdfs.headless.keytab"
   principal="hdfs-sandbox@EU-WEST-1.COMPUTE.AMAZONAWS.COM";

};
KafkaClient {
   com.sun.security.auth.module.Krb5LoginModule required
   useTicketCache=true
   useKeyTab=true
   renewTicket=true
   serviceName="kafka"
   keyTab="/etc/security/keytabs/hbase.service.keytab"
   principal="hbase/sandbox.hortonworks.com@EU-WEST-1.COMPUTE.AMAZONAWS.COM";
};
EOF


# Add the following to the bootstrap.conf file:

printf "java.arg.15=-Djava.security.auth.login.config=/opt/nifi/conf/jaas_policy_store.conf\n" >> /opt/nifi/conf/bootstrap.conf

# Fix the classpath a bit to get phoenix/hbase working...

cd /opt/nifi/lib/bootstrap
ln -s /usr/hdp/2.5.0.0-1245/phoenix/phoenix-4.7.0.2.5.0.0-1245-client.jar


cd /opt/nifi/lib

ln -s /opt/pontus/nifi-pontus-hbase-processor-nar-1.0.nar
ln -s /opt/pontus/nifi-pontus-elastic-2.x-processor-nar-1.0.nar
ln -s /opt/pontus/nifi-pontus-service-api-nar-1.0.nar
ln -s /opt/pontus/nifi-pontus-service-nar-1.0.nar


```

