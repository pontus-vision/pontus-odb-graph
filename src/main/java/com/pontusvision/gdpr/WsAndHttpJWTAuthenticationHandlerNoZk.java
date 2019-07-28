package com.pontusvision.gdpr;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SystemUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import uk.gov.homeoffice.pontus.JWTClaim;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

@ChannelHandler.Sharable

public class WsAndHttpJWTAuthenticationHandlerNoZk extends WsAndHttpJWTAuthenticationHandler
{

  public WsAndHttpJWTAuthenticationHandlerNoZk(final Authenticator authenticator,
                                               final Settings.AuthenticationSettings authenticationSettings)
  {
    super(authenticator, authenticationSettings);

  }

  // Method to disconnect from zookeeper server
  @Override
  public void close() throws InterruptedException
  {
    // noop

  }

  // Method to connect zookeeper ensemble.

  @Override
  protected void handleZookeeper (JWSObject jwsObject, JWTClaim sampleClaim )
      throws KeeperException, InterruptedException, IOException
  {
     // noop

  }


}
