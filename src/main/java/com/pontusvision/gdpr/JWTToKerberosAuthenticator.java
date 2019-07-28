package com.pontusvision.gdpr;

import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.tinkerpop.gremlin.groovy.jsr223.dsl.credential.CredentialGraph;
import org.apache.tinkerpop.gremlin.server.auth.AuthenticatedUser;
import org.apache.tinkerpop.gremlin.server.auth.AuthenticationException;
import org.apache.tinkerpop.gremlin.server.auth.Authenticator;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.hadoop.security.SaslRpcServer.AuthMethod.KERBEROS;
import static org.apache.tinkerpop.gremlin.groovy.jsr223.dsl.credential.CredentialGraphTokens.PROPERTY_PASSWORD;
import static org.apache.tinkerpop.gremlin.groovy.jsr223.dsl.credential.CredentialGraphTokens.PROPERTY_USERNAME;

/**
 * A simple implementation of an {@link Authenticator} that uses a {@link Graph} instance as a credential store.
 * Management of the credential store can be handled through the {@link CredentialGraph} DSL.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class JWTToKerberosAuthenticator implements Authenticator
{
  private static final Logger logger = LoggerFactory
      .getLogger(JWTToKerberosAuthenticator.class);
  private static final byte NUL = 0;
  private CredentialGraph credentialStore;

  private  Map<String, Object> config;

  @Override public boolean requireAuthentication()
  {
    return true;
  }

  @Override public void setup(final Map<String, Object> config)
  {
    logger.info("Initializing authentication with the {}",
        JWTToKerberosAuthenticator.class.getName());

    if (null == config)
    {
      throw new IllegalArgumentException(String
          .format("Could not configure a %s - provide a 'config' in the 'authentication' settings",
              org.apache.tinkerpop.gremlin.server.auth.SimpleAuthenticator.class.getName()));
    }
    this.config = config;

  }

  @Override public SaslNegotiator newSaslNegotiator(final InetAddress remoteAddress)
  {
    return null;
  }

  public static LoginContext kinit(String username, String password) throws LoginException
  {
    LoginContext lc = new LoginContext("HBaseClient", new CallbackHandler() {
      public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
      {
        for(Callback c : callbacks){
          if(c instanceof NameCallback)
            ((NameCallback) c).setName(username);
          if(c instanceof PasswordCallback && password != null)
            ((PasswordCallback) c).setPassword(password.toCharArray());
        }
      }});
    lc.login();
    return lc;
  }

  public AuthenticatedUser authenticate(final Map<String, String> credentials) throws AuthenticationException
  {
    if (!credentials.containsKey(PROPERTY_USERNAME))
      throw new IllegalArgumentException(String.format("Credentials must contain a %s", PROPERTY_USERNAME));
    if (!credentials.containsKey(PROPERTY_PASSWORD))
      throw new IllegalArgumentException(String.format("Credentials must contain a %s", PROPERTY_PASSWORD));

    final String username = credentials.get(PROPERTY_USERNAME);

    final String password = credentials.get(PROPERTY_PASSWORD);

    try
    {
      LoginContext lc = kinit(username,password);
//      UserGroupInformation ugi =    UserGroupInformation.createRemoteUser(username,KERBEROS);
//      UserGroupInformation.loginUserFromSubject(lc.getSubject());

//      AuthenticatedUser au = new AuthenticatedUser(username)

    }
    catch (LoginException e)
    {
      throw(new AuthenticationException(e));
    }
//    catch (IOException e)
//    {
//      throw(new AuthenticationException(e));
//    }
    
    return new AuthenticatedUser(username);
  }

}
