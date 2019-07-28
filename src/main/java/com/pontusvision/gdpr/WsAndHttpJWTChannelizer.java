package com.pontusvision.gdpr;

import io.netty.channel.ChannelPipeline;
import org.apache.tinkerpop.gremlin.server.AbstractChannelizer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.server.auth.AllowAllAuthenticator;
import org.apache.tinkerpop.gremlin.server.channel.WsAndHttpChannelizer;
import org.apache.tinkerpop.gremlin.server.handler.AbstractAuthenticationHandler;
import org.apache.tinkerpop.gremlin.server.handler.HttpGremlinEndpointHandler;
import org.apache.tinkerpop.gremlin.server.handler.WsAndHttpChannelizerHandler;
import org.apache.tinkerpop.gremlin.server.util.ServerGremlinExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsAndHttpJWTChannelizer extends AbstractChannelizer
{

//  private static final Logger logger = LoggerFactory.getLogger(WsAndHttpJWTChannelizer.class);

  private WsAndHttpChannelizerHandler handler;
  private WsAndHttpJWTChannelizerHandler dummyHandler;
//  private AbstractAuthenticationHandler authenticationHandler;

  private HttpGremlinEndpointHandler endpointHandler;

  @Override
  public void init(final ServerGremlinExecutor serverGremlinExecutor) {
    super.init(serverGremlinExecutor);
//    authenticator = new JWTToKerberosAuthenticator();

//    if (authenticator != null)
//      authenticationHandler = authenticator.getClass() == AllowAllAuthenticator.class ?
//          null : instantiateAuthenticationHandler(settings.authentication);

    endpointHandler = new HttpJWTGremlinEndpointHandler(serializers, gremlinExecutor, graphManager, settings);

    handler = new WsAndHttpChannelizerHandler();
    handler.init(serverGremlinExecutor,endpointHandler);

    dummyHandler = new WsAndHttpJWTChannelizerHandler();


  }

  public WsAndHttpChannelizerHandler getHandler(){
    return handler;
  }

  public HttpGremlinEndpointHandler getEndpointHandler(){
    return endpointHandler;
  }
  @Override
  public void configure(final ChannelPipeline pipeline) {
    handler.configure(pipeline);
    pipeline.addAfter(PIPELINE_HTTP_REQUEST_DECODER, "WsAndHttpJWTChannelizerHandler", dummyHandler);
  }

//  private AbstractAuthenticationHandler instantiateAuthenticationHandler(final Settings.AuthenticationSettings authSettings) {
//    final String authenticationHandler = authSettings.authenticationHandler;
//    if (authenticationHandler == null) {
//      //Keep things backwards compatible
//      return new WsAndHttpJWTAuthenticationHandler(authenticator, authSettings);
//    } else {
//      return createAuthenticationHandler(authSettings);
//    }
//  }

}
