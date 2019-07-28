package com.pontusvision.gdpr;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.util.ReferenceCountUtil;
import net.minidev.json.JSONObject;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.hadoop.hbase.security.AccessDeniedException;
import org.apache.hadoop.hbase.shaded.org.apache.commons.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.server.auth.Authenticator;
import org.apache.tinkerpop.gremlin.server.handler.AbstractAuthenticationHandler;
import org.apache.tinkerpop.gremlin.server.handler.HttpGremlinEndpointHandler;
import org.apache.tinkerpop.gremlin.server.util.ServerGremlinExecutor;
import org.apache.tinkerpop.gremlin.server.util.ThreadFactoryUtil;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration;
import org.keycloak.jose.jwk.JWKParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cdp.ldap.LdapService;
import uk.gov.cdp.ldap.LdapServiceImpl;
import uk.gov.cdp.ldap.LdapServiceSambaImpl;
import uk.gov.cdp.shadow.user.auth.AuthenticationService;
import uk.gov.cdp.shadow.user.auth.AuthenticationServiceImpl;
import uk.gov.cdp.shadow.user.auth.CDPShadowUserPasswordGeneratorImpl;
//import uk.gov.homeoffice.pontus.JWTClaim;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration.*;
import static uk.gov.cdp.ldap.LdapService.LDAP_USER_FREE_IPA_MODE;
import static uk.gov.cdp.ldap.LdapService.LDAP_USER_FREE_IPA_MODE_DEFAULT;

@ChannelHandler.Sharable

public class WsAndHttpJWTAuthenticationHandler extends AbstractAuthenticationHandler
{
  private static final Logger logger = LoggerFactory.getLogger(WsAndHttpJWTAuthenticationHandler.class);
  private static final Logger auditLogger = LoggerFactory.getLogger(GremlinServer.AUDIT_LOGGER_NAME);
  private final Settings.AuthenticationSettings authenticationSettings;

  private final Base64.Decoder decoder = Base64.getUrlDecoder();
  private SSLContextService sslContextService = new SSLContextService();
  public static final String JWT_ZK_PATH = "jwt_store.zk.path";
  public static final String JWT_ZK_PATH_DEFVAL = "/jwt/users";
  public static final String JWT_ZK_SESSION_TIMEOUT = "jwt_store.zk.sessionTimeoutMs";
  public static final String JWT_ZK_CONNECT_STRING = "jwt_store.zk.connectString";
  public static final String JWT_ZK_CONNECT_STRING_DEFVAL = "sandbox.hortonworks.com";
  public static final String JWT_SECURITY_CLAIM_CACHE_INIT_SIZE = "jwt_store.security_claim_cache.initSizeBytes";
  public static final String JWT_SECURITY_CLAIM_CACHE_LOAD_FACTOR = "jwt_store.security_claim_cache.loadFactor";
  public static final String JWT_SECURITY_CLAIM_CACHE_MAX_SIZE = "jwt_store.security_claim_cache.maxSizeBytes";
  public static final String GRAPHDB_ENABLE_QUERY_AUDIT = "graphdb.enable.query.audit";
  public static final String GRAPHDB_ENABLE_QUERY_AUDIT_DEFAULT = "true";

  public static final int JWT_SECURITY_CLAIM_CACHE_INIT_SIZE_DEFVAL = 20000;
  public static final float JWT_SECURITY_CLAIM_CACHE_LOAD_FACTOR_DEFVAL = 0.75F;
  public static final long JWT_SECURITY_CLAIM_CACHE_MAX_SIZE_DEFVAL = 50000000L;

  boolean ipaMode = Boolean.parseBoolean(System.getProperty(LDAP_USER_FREE_IPA_MODE, LDAP_USER_FREE_IPA_MODE_DEFAULT));

  boolean enableUserAudit = Boolean
      .parseBoolean(System.getProperty(GRAPHDB_ENABLE_QUERY_AUDIT, GRAPHDB_ENABLE_QUERY_AUDIT_DEFAULT));

  LdapService ldapSvc = ipaMode ? new LdapServiceImpl() : new LdapServiceSambaImpl();

  protected AuthenticationService authenticationService = new AuthenticationServiceImpl(ldapSvc,
      new CDPShadowUserPasswordGeneratorImpl());
  public String zookeeperConnStr = "localhost";
  public String zookeeperPrincipal = "";
  public String zookeeperKeytab = "";

  String zkPath = JWT_ZK_PATH_DEFVAL;
  ZooKeeper zoo = null;

  public String keyAlias = "localhost";

  public static Map<Object, Object> graphDbMap;

  static
  {
    graphDbMap = Collections.synchronizedMap(new LRUMap(20));
  }

  public WsAndHttpJWTAuthenticationHandler(final Authenticator authenticator,
                                           final Settings.AuthenticationSettings authenticationSettings)
  {
    super(authenticator);
    this.authenticationSettings = authenticationSettings;

    if (this.authenticationSettings != null && this.authenticationSettings.config != null)
    {
      this.zookeeperConnStr = (String) this.authenticationSettings.config.get("zookeeperConnStr");

      this.zookeeperPrincipal = (String) this.authenticationSettings.config.get("zookeeperPrincipal");
      this.zookeeperKeytab = (String) this.authenticationSettings.config.get("zookeeperKeytab");

      this.keyAlias = (String) this.authenticationSettings.config.getOrDefault("jwtKeyAlias", "jwt");
      this.sslContextService.keyPassword = (String) this.authenticationSettings.config
          .getOrDefault("jwtKeyPassword", "pa55word");
      this.sslContextService.keyStoreFile = (String) this.authenticationSettings.config
          .getOrDefault("jwtKeyStoreFile", "/etc/pki/java/jwt.jks");
      this.sslContextService.keyStorePassword = (String) this.authenticationSettings.config
          .getOrDefault("jwtKeyStorePassword", "pa55word");
      this.sslContextService.keyStoreType = (String) this.authenticationSettings.config
          .getOrDefault("jwtKeyStoreType", "jks");

      //zookeeperPrincipal
      //zookeeperKeytab
    }

  }

  public static Key[] getPublicKey(SSLContextService sslService, String alias)
      throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException
  {
    FileInputStream is = new FileInputStream(sslService.getKeyStoreFile());

    if ("JWK".equals(sslService.getKeyStoreType()))
    {

      JWKParser parser = JWKParser.create();

      String theString = IOUtils.toString(is);

      Gson gson = new Gson();
      JsonObject obj = gson.fromJson(theString, JsonObject.class);
      //{"keys":[{"kid":"fy8EMWnzLcgzvR6gil6tgAf5bNPiut6f4DO3XNf7rmQ","kty":"RSA","alg":"RS256","use":"sig","n":"oqeIQJ7Rp-duwY0gjjgqO7qwCs6Wf8TEATowAw9oJ2Rv2R4CK-7iAlJeB23uGtLkpWAtUkOgLX-U47tsScnBOoKeHGNPIfSH6ZFIY7QoKBcLIy1eQ0nBOaYqU1LbMlJBS8lChyt4hgiNStHFgHXuGtx6Q6MLUViTHvTeSzO876jWyXexzyh9ffmsLjekuxc53xQ216kTIRMUzrx6WiuM-5oBTJ5DpbkZFOs_bH2Hrpnmy-JB3TxToi-V0AAQYfXuAfwu0DvIjBRHHZg_iNN5CrhcsIvAmqRKGl1sXSXtXs-BSzrBwzlPzf1U2Fq1Ot9bD5HivZHx5Y1Z3hS-98nI1w","e":"AQAB"}]}

      JsonElement keysElement = obj.get("keys");

      if (keysElement.isJsonArray())
      {
        JsonArray array = obj.get("keys").getAsJsonArray();

        int numKeys = array.size();
        Key[] retVals = new Key[numKeys];

        for (int i = 0; i < numKeys; i++)
        {
          JsonElement keyJson = array.get(i);
          String keyString = keyJson.toString();
          parser.parse(keyString);
          retVals[i] = parser.toPublicKey();
        }

        //      parser.parse(theString);
        return retVals;
      }
      else
      {
        Key[] retVals = new Key[1];

        String keyString = keysElement.toString();
        parser.parse(keyString);

        retVals[0] = parser.toPublicKey();

        return retVals;

      }

    }
    else
    {
      KeyStore keystore = KeyStore.getInstance(sslService.getKeyStoreType());
      keystore.load(is, sslService.getKeyStorePassword().toCharArray());

      //    String alias = sslService.getIdentifier(); //"myalias";

      Key key = keystore.getKey(alias, sslService.getKeyPassword().toCharArray());
      if (key instanceof PrivateKey)
      {
        // Get certificate of public key
        java.security.cert.Certificate cert = keystore.getCertificate(alias);

        // Get public key
        PublicKey publicKey = cert.getPublicKey();

        return new PublicKey[] { publicKey };
        // Return a key pair
        //      new KeyPair(publicKey, (PrivateKey) key);
      }
    }

    return null;

  }

  public static JWSVerifier getVerifier(JWSAlgorithm algo, Key key) throws JOSEException
  {

    if (JWSAlgorithm.Family.EC.contains(algo))
    {

      if (key instanceof ECPublicKey)
      {
        return new ECDSAVerifier((ECPublicKey) key);

      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the ECDSASigner; note that DSA Keys are not currently supported by this processor.");
      }

    }

    else if (JWSAlgorithm.Family.RSA.contains(algo))
    {
      if (key instanceof RSAPublicKey)
      {
        return new RSASSAVerifier((RSAPublicKey) key);
      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the RSASSASigner; note that DSA Keys are not currently supported by this processor.\n\n"
            + "  Use the following to create an RSA Key:\n\n"
            + "     keytool -genkeypair -alias jwtkey -keyalg RSA -dname \"CN=Server,OU=Unit,O=Organization,L=City,S=State,C=US\" -keypass pa55word -keystore kafka.client.keystore.jks -storepass pa55word\n");
      }

    }
    else if (JWSAlgorithm.Family.HMAC_SHA.contains(algo))
    {
      if (key instanceof SecretKey)
      {
        return new MACVerifier((SecretKey) key);
      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the MACSigner; note that DSA Keys are not currently supported by this processor.");
      }
    }

    return null;
  }

  public static JWSSigner getSigner(JWSAlgorithm algo, Key key) throws JOSEException
  {

    if (JWSAlgorithm.Family.EC.contains(algo))
    {

      if (key instanceof ECPrivateKey)
      {
        return new ECDSASigner((ECPrivateKey) key);

      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the ECDSASigner; note that DSA Keys are not currently supported by this processor.");
      }

    }

    else if (JWSAlgorithm.Family.RSA.contains(algo))
    {
      if (key instanceof PrivateKey)
      {
        return new RSASSASigner((PrivateKey) key);
      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the RSASSASigner; note that DSA Keys are not currently supported by this processor.\n\n"
            + "  Use the following to create an RSA Key:\n\n"
            + "     keytool -genkeypair -alias jwtkey -keyalg RSA -dname \"CN=Server,OU=Unit,O=Organization,L=City,S=State,C=US\" -keypass pa55word -keystore kafka.client.keystore.jks -storepass pa55word\n");
      }

    }
    else if (JWSAlgorithm.Family.HMAC_SHA.contains(algo))
    {
      if (key instanceof SecretKey)
      {
        return new MACSigner((SecretKey) key);
      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the MACSigner; note that DSA Keys are not currently supported by this processor.");
      }
    }

    return null;
  }

  // Method to disconnect from zookeeper server
  public void close() throws InterruptedException
  {
    zoo.close();
  }

  // Method to connect zookeeper ensemble.
  public static ZooKeeper connect(String host) throws IOException, InterruptedException
  {

    final CountDownLatch connectedSignal = new CountDownLatch(1);
    ZooKeeper zoo = null;

    zoo = new ZooKeeper(host, 3600000*24 * 7, we -> {
      if (we.getState() == Watcher.Event.KeeperState.SyncConnected)
      {
        connectedSignal.countDown();
      }
    });

    connectedSignal.await();
    return zoo;
  }

  public void create(String path, byte[] data) throws KeeperException, InterruptedException
  {

    String[] parts = path.split("/");
    StringBuffer strBuf = new StringBuffer();
    // LPPM - the first level is empty, as we start with a slash.
    // skip it by setting i and j = 1.
    for (int i = 1, ilen = parts.length, lastItem = parts.length - 1; i < ilen; i++)
    {
      strBuf.setLength(0);
      for (int j = 1; j <= i; j++)
      {
        strBuf.append("/").append(parts[j]);
      }
      String partialPath = strBuf.toString();
      if (exists(partialPath) == null)
      {
        CreateMode mode;
        if (i == lastItem)
        {
          mode = CreateMode.PERSISTENT;
        }
        else
        {
          mode = CreateMode.PERSISTENT;
        }
        List<ACL> perms = new ArrayList<>();
        if (UserGroupInformation.isSecurityEnabled())
        {
          perms.add(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.AUTH_IDS));
          perms.add(new ACL(ZooDefs.Perms.READ, ZooDefs.Ids.ANYONE_ID_UNSAFE));
        }
        else
        {
          perms.add(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE));
        }

        zoo.create(partialPath, data, perms, mode);

      }
    }
  }

  public Stat exists(String path) throws KeeperException, InterruptedException
  {
    return zoo.exists(path, true);
  }

  public void update(String path, byte[] data) throws KeeperException, InterruptedException
  {
    zoo.setData(path, data, zoo.exists(path, true).getVersion());
  }

  private static String getAbsolutePath(final File configParent, final String file)
  {
    final File storeDirectory = new File(file);
    if (!storeDirectory.isAbsolute())
    {
      String newFile = configParent.getAbsolutePath() + File.separator + file;
      return newFile;
    }
    else
    {
      return file;
    }
  }

  private static CommonsConfiguration getLocalConfiguration(File file, String user, String pass)
  {
    Preconditions.checkArgument(file != null && file.exists() && file.isFile() && file.canRead(),
        "Need to specify a readable configuration file, but was given: %s", file.toString());

    try
    {
      PropertiesConfiguration configuration = new PropertiesConfiguration(file);

      final File tmpParent = file.getParentFile();
      final File configParent;

      if (null == tmpParent)
      {
        /*
         * null usually means we were given a JanusGraph config file path
         * string like "foo.properties" that refers to the current
         * working directory of the process.
         */
        configParent = new File(System.getProperty("user.dir"));
      }
      else
      {
        configParent = tmpParent;
      }

      Preconditions.checkNotNull(configParent);
      Preconditions.checkArgument(configParent.isDirectory());

      // TODO this mangling logic is a relic from the hardcoded string days; it should be deleted and rewritten as a setting on ConfigOption
      final Pattern p = Pattern.compile(
          "(" + Pattern.quote(STORAGE_NS.getName()) + "\\..*" + "(" + Pattern.quote(STORAGE_DIRECTORY.getName()) + "|"
              + Pattern.quote(STORAGE_CONF_FILE.getName()) + ")" + "|" + Pattern.quote(INDEX_NS.getName()) + "\\..*"
              + "(" + Pattern.quote(INDEX_DIRECTORY.getName()) + "|" + Pattern.quote(INDEX_CONF_FILE.getName()) + ")"
              + ")");

      final Iterator<String> keysToMangle = Iterators
          .filter(configuration.getKeys(), new Predicate<String>()
              {
                @Override public boolean apply(@Nullable String key)
                {
                  return ((null != key) && p.matcher((CharSequence) key).matches());
                }
              });


      while (keysToMangle.hasNext())
      {
        String k = keysToMangle.next();
        Preconditions.checkNotNull(k);
        final String s = configuration.getString(k);
        Preconditions.checkArgument(StringUtils.isNotBlank(s), "Invalid Configuration: key %s has null empty value", k);
        configuration.setProperty(k, getAbsolutePath(configParent, s));
      }
      return new CommonsConfiguration(configuration);
    }
    catch (ConfigurationException e)
    {
      throw new IllegalArgumentException("Could not load configuration at: " + file, e);
    }
  }

  public static HttpGremlinEndpointHandler getGremlinEndpointHandler(Settings settings, String userName,
                                                                     String password) throws ConfigurationException
  {
    HttpGremlinEndpointHandler retVal = (HttpGremlinEndpointHandler) graphDbMap.get(userName);
    if (retVal == null)
    {

      String gconfFileStr = (String) settings.graphs.getOrDefault("graph", "conf/janusgraph-hbase-es.properties");

      File gconfFile = new File(gconfFileStr);
      CommonsConfiguration conf = getLocalConfiguration(gconfFile, userName, password);

      conf.set("storage.hbase.ext.hbase.proxy_user", userName);
      conf.set("storage.hbase.ext.hbase.proxy_pass", password);
      JanusGraph graph = JanusGraphFactory.open(conf);

      final ThreadFactory threadFactoryWorker = ThreadFactoryUtil.create("worker-%d");
      boolean isEpollEnabled = settings.useEpollEventLoop && SystemUtils.IS_OS_LINUX;

      ScheduledExecutorService workerGroup;
      if (isEpollEnabled)
      {
        workerGroup = new EpollEventLoopGroup(settings.threadPoolWorker, threadFactoryWorker);
      }
      else
      {
        workerGroup = new NioEventLoopGroup(settings.threadPoolWorker, threadFactoryWorker);
      }

      ServerGremlinExecutor serverGremlinExecutor = new ServerGremlinExecutor(settings, null, workerGroup);
      //      ServerGremlinExecutorService gremlinExecutorService = serverGremlinExecutor.getGremlinExecutorService();

      GremlinExecutor exec = serverGremlinExecutor.getGremlinExecutor();

      WsAndHttpJWTChannelizer channelizer = new WsAndHttpJWTChannelizer();
      serverGremlinExecutor.getGraphManager().putTraversalSource("g", graph.traversal());

      serverGremlinExecutor.getGraphManager().putGraph("graph", graph);
      channelizer.init(serverGremlinExecutor);

      retVal = channelizer.getEndpointHandler();

      graphDbMap.put(userName, retVal);

    }
    return retVal;
  }

  @Override public void channelRead(final ChannelHandlerContext ctx, final Object msg)
  {
    if (msg instanceof FullHttpMessage)
    {
      final FullHttpMessage request = (FullHttpMessage) msg;
      if (!request.headers().contains("Authorization"))
      {
        logger.error("Missing Authorization Header");
        sendError(ctx, msg);
        return;
      }

      // strip off "Basic " from the Authorization header (RFC 2617)
      String basic = "Bearer ";
      final String jwt = "JWT ";
      final String authorizationHeader = request.headers().get("Authorization");
      if (!authorizationHeader.startsWith(basic))
      {
        if (!authorizationHeader.startsWith(jwt))
        {
          logger.error("Missing Bearer or JWT in authorization header");

          sendError(ctx, msg);
          return;
        }
        basic = jwt;
      }

      final String jwtStr = authorizationHeader.substring(basic.length());

      //      JWSSigner signer = getSigner(keyAlgo,key);

      //To parse the JWS and verify it, e.g. on client-side
      JWSObject jwsObject = null;
      try
      {
        Key[] keys = getPublicKey(sslContextService, keyAlias);

        jwsObject = JWSObject.parse(jwtStr);

        JWSAlgorithm keyAlgo = JWSAlgorithm.parse(JWSAlgorithm.RS512.toString());
        JWSVerifier verifier;
        boolean passedVerification = false;
        for (int i = 0, ilen = keys.length; i < ilen; i++)
        {
          verifier = getVerifier(keyAlgo, keys[i]);
          boolean passedSignatureVerification = jwsObject.verify(verifier);

          if (passedSignatureVerification)
          {
            Payload payload = jwsObject.getPayload();
            JSONObject obj = payload.toJSONObject();

            String expStr = obj.getAsString("exp");
            if (expStr != null)
            {
              int expStrLen = expStr.length();
              long targetTime = System.currentTimeMillis();
              boolean expIsinMillis = (expStrLen >= 13);

              if (!expIsinMillis)
              {
                targetTime /= 1000;
              }

              long exp = obj.getAsNumber("exp").longValue();
              if (exp == 0 || targetTime <= exp)
              {
                passedVerification = true;
              }
              else
              {
                passedVerification = false;
                auditLogger.error("The JWT Token  has expired: {}", obj.toJSONString());
              }
            }
            else
            {
              passedVerification = false;
              auditLogger.error("The JWT Token expiration is not present: {}", obj.toJSONString());
            }

            if (passedVerification)
            {
              break;
            }

          }
        }

        if (!passedVerification)
        {
          sendError(ctx, msg);
          logger.error("Failed to verify the JWT with the supplied key");

          auditLogger.error("Failed to verify the JWT with the supplied key.");
          return;

        }

        JWTClaim sampleClaim = JWTClaim.fromJson(jwsObject.getPayload().toString());

        //        final Map<String, String> credentials = new HashMap<>();
        //        credentials.put(PROPERTY_USERNAME, sampleClaim.getSub());
        //        credentials.put(PROPERTY_PASSWORD, sampleClaim.getSub());
        //        credentials.put(PROPERTY_PASSWORD, jwtStr);

        String user = sampleClaim.getSub();

        //TODO - Update pontus-redaction-common to add a method in JWTClaim to return user group list
        List<String> groups = sampleClaim.getGroups();
        if (groups == null)
        {
          groups = Collections.emptyList();
        }

        String pass = authenticationService.authenticate(user, user, sampleClaim.getBizctx(), groups);

        //        LoginContext lc =

        //        JWTToKerberosAuthenticator.kinit(user,pass);

        //        authenticator.authenticate(credentials);
        //        this.close();

        // TODO: add a doAs
        //        UserGroupInformation ugi = UserGroupInformation.getUGIFromSubject(lc.getSubject());
        //        PrivilegedExceptionAction pea = ()-> ctx.fireChannelRead(request);
        //
        //        ugi.doAs(pea);

        //        exec.

        handleZookeeper(jwsObject, sampleClaim);

        // User name logged with the remote socket address and authenticator classname for audit logging
        if (enableUserAudit)
        {
          String address = ctx.channel().remoteAddress().toString();
          if (address.startsWith("/") && address.length() > 1)
            address = address.substring(1);
          final String[] authClassParts = authenticator.getClass().toString().split("[.]");

          logger.info("Successfully authenticated user {}", user);

          String contentStr = "<EMPTY_REQUEST>";

          ByteBuf content = ((FullHttpMessage) msg).content();
          if (content != null)
          {
            contentStr = content.toString(Charset.defaultCharset());
          }
          auditLogger.info("User {} with address {} authenticated by {}; request body = {}", user, address,
              authClassParts[authClassParts.length - 1], contentStr);
        }

        try
        {
          HttpGremlinEndpointHandler geh = getGremlinEndpointHandler(App.settings, user, pass);
          geh.channelRead(ctx, msg);

          ctx.fireChannelRead(msg);

        }
        catch (Throwable t)
        {
          logger.info("Got Exception", t);

          boolean isServerError = true;

          Throwable cause = t.getCause();
          while (cause != null)
          {
            if (cause instanceof AccessDeniedException)
            {
              isServerError = false;
            }
            cause = cause.getCause();
          }
          if (isServerError)
          {
            sendServerError(ctx, msg);
          }
          else
          {
            sendError(ctx, msg);
          }
        }
      }
      catch (Exception ae)
      {
        this.zoo = null;
        logger.info("Got Exception creating zookeeper conn", ae);

        sendError(ctx, msg);
      }
    }
  }

  protected void handleZookeeper(JWSObject jwsObject, JWTClaim sampleClaim)
      throws KeeperException, InterruptedException, IOException
  {
    StringBuffer strBuf = new StringBuffer(JWT_ZK_PATH_DEFVAL).append("/").append(sampleClaim.getSub());

    if (this.zoo == null)
    {
      UserGroupInformation ugi = UserGroupInformation
          .loginUserFromKeytabAndReturnUGI(zookeeperPrincipal, zookeeperKeytab);
      ugi.checkTGTAndReloginFromKeytab();
      PrivilegedExceptionAction<ZooKeeper> action = () -> connect(zookeeperConnStr);
      this.zoo = ugi.doAs(action);

    }

    //        UserGroupInformation ugi = UserGroupInformation.
    if (this.exists(strBuf.toString()) == null)
    {
      this.create(strBuf.toString(), jwsObject.getPayload().toBytes());
    }
    else
    {
      this.update(strBuf.toString(), jwsObject.getPayload().toBytes());
    }

  }

  private void sendServerError(final ChannelHandlerContext ctx, final Object msg)
  {
    // Close the connection as soon as the error message is sent.
    ctx.writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR))
        .addListener(ChannelFutureListener.CLOSE);
    ReferenceCountUtil.release(msg);
  }

  private void sendError(final ChannelHandlerContext ctx, final Object msg)
  {
    // Close the connection as soon as the error message is sent.
    ctx.writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED)).addListener(ChannelFutureListener.CLOSE);
    ReferenceCountUtil.release(msg);
  }

  public static SSLContextService getSSLContextServiceInstance(Settings.AuthenticationSettings authSettings)
  {

    SSLContextService svc = new SSLContextService();
    svc.keyPassword = (String) authSettings.config.getOrDefault("jwtKeyPassword", "pa55word");
    svc.keyStoreFile = (String) authSettings.config.getOrDefault("jwtKeyStoreFile", "/etc/pki/java/jwt.jks");
    svc.keyStorePassword = (String) authSettings.config.getOrDefault("jwtKeyStorePassword", "pa55word");
    svc.keyStoreType = (String) authSettings.config.getOrDefault("jwtKeyStoreType", "jks");

    return svc;

  }

  public static class SSLContextService
  {

    String keyStoreFile = "/etc/pki/java/keystore.jks";
    String keyStoreType = "JKS";
    String keyStorePassword = "pa55word";
    String keyPassword = "pa55word";

    public String getKeyStoreFile()
    {
      return keyStoreFile;
    }

    public String getKeyStoreType()
    {
      return keyStoreType;
    }

    public String getKeyStorePassword()
    {
      return keyStorePassword;
    }

    public String getKeyPassword()
    {
      return keyPassword;
    }

    public void setKeyStoreFile(String keyStoreFile)
    {
      this.keyStoreFile = keyStoreFile;
    }

    public void setKeyStoreType(String keyStoreType)
    {
      this.keyStoreType = keyStoreType;
    }

    public void setKeyStorePassword(String keyStorePassword)
    {
      this.keyStorePassword = keyStorePassword;
    }

    public void setKeyPassword(String keyPassword)
    {
      this.keyPassword = keyPassword;
    }

  }
}
