PVGDPR_DISABLE_HADOOP_CLASSPATH_LOOKUP=true
PVGDPR_CLASSPATH=/opt/pontus/pontus-hbase/current/conf
#PVGDPR_OPTS="-Djava.security.auth.login.config=/opt/graphdb/current/conf/jaas.conf -Dsun.security.krb5.debug=true"
#PVGDPR_OPTS="-Djava.security.auth.login.config=/opt/pontus-graphdb/graphdb-current//conf/jaas.conf"
PVGDPR_OPTS=" \
 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5009 \
 -Dldap.user.creation.freeipa.mode=false \
 -Dindex.elastic.use_params=true \
 -Dshadow.user.keystore.location=/etc/pki/java/shadow.jks \
 -Dshadow.user.keystore.pwd=pa55word \
 -Dshadow.user.key.pwd=pa55word \
 -Dshadow.user.key.alias=shadow \
 -Dshadow.user.key.algo=HmacSHA512 \
 -Dshadow.user.key.store.type=JCEKS \
 -Djava.security.auth.login.config=/opt/pontus-graphdb/graphdb-current/conf/jaas.conf \
 -Dpg.jpostal.datadir=/opt/pontus-graphdb/graphdb-current/datadir/libpostal \
 -Dldap.create.user=true  \
 -Dkerberos.authentication=false  \
 -Dldap.protocol=ldaps  \
 -Dldap.server.hostname=localhost  \
 -Dldap.port=636  \
 -Dldap.domain.name=pontusvision.com  \
 -Dldap.domain.root=CN=Users,DC=pontusvision,DC=com  \
 -Dldap.user.group=CN=Administrators,CN=Builtin,DC=pontusvision,DC=com  \
 -Dldap.admin.user=Administrator  \
 -Dldap.admin.user.pwd=pa55wordpa55wordPASSWD999 \
 -Dcom.sun.management.jmxremote=true \
 -Dcom.sun.management.jmxremote.local.only=false \
 -Dcom.sun.management.jmxremote.authenticate=false \
 -Dcom.sun.management.jmxremote.ssl=false \
 -Dcom.sun.management.jmxremote.port=9999 \
-Dcom.sun.management.jmxremote.rmi.port=9999 \
-Dcom.sun.management.jmxremote=true \
-Dcom.sun.management.jmxremote.port=9999 \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.local.only=false \
-Djava.rmi.server.hostname=localhost \
 -Dcom.sun.management.jmxremote.rmi.port=9999"

CLASS=com.pontusvision.gdpr.App
#CLASS=uk.gov.cdp.pole.bootstrap.Bootstrap
