package com.pontusvision.gdpr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.tinkerpop.gremlin.server.handler.HttpGremlinEndpointHandler;
import org.janusgraph.diskstorage.hbase.ConnectionMask;
import org.janusgraph.diskstorage.hbase.HBaseCompat1_0;
import org.janusgraph.diskstorage.hbase.HConnection1_0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;

public class HBaseMultiUserCompat1_0 extends HBaseCompat1_0
{
  private static final Logger logger = LoggerFactory.getLogger(HttpGremlinEndpointHandler.class);
  @Override
  public ConnectionMask createConnection(Configuration conf) throws IOException
  {
    String proxyUser = conf.get("hbase.proxy_user");
    String proxyPass = conf.get("hbase.proxy_pass");

    try
    {
      LoginContext lc = JWTToKerberosAuthenticator.kinit(proxyUser, proxyPass);
      lc.login();
      UserProvider provider = UserProvider.instantiate(conf);
      UserGroupInformation ugi = UserGroupInformation.createProxyUser(proxyUser, UserGroupInformation.getLoginUser());

      //      ugi = UserGroupInformation.getUGIFromSubject(lc.getSubject());

      User user = provider.create(ugi);

      Connection conn = ConnectionFactory.createConnection(conf, user);

      return new HBaseMultiUserConnection1_0(conn, ConnectionFactory.createConnection(conf));
    }

    catch (LoginException e)
    {
      //e.printStackTrace();

      logger.info("Failed to authenticate {}  {} (NOTE: this is normal during bootstrapping).",proxyUser,proxyPass  );
    }

    return new HConnection1_0(ConnectionFactory.createConnection(conf));

  }
}
