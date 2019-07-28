package com.pontusvision.gdpr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.hadoop.security.UserGroupInformation;
import org.janusgraph.diskstorage.hbase.*;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;

public class HBaseMultiUserConnection1_0 extends HConnection1_0
{
  Connection admCnx;

  public HBaseMultiUserConnection1_0(Connection cnx, Connection admCnx)
  {
    super(cnx);
    this.admCnx = admCnx;
  }
  public AdminMask getAdmin() throws IOException {
    return new HBaseAdmin1_0(this.admCnx.getAdmin());
  }


}
