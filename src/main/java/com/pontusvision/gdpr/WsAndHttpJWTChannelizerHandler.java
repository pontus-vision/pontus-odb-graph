package com.pontusvision.gdpr;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpMessage;
import org.apache.tinkerpop.gremlin.server.channel.HttpChannelizer;
import org.apache.tinkerpop.gremlin.server.channel.WebSocketChannelizer;
import org.apache.tinkerpop.gremlin.server.handler.AbstractAuthenticationHandler;
import org.apache.tinkerpop.gremlin.server.handler.HttpGremlinEndpointHandler;
import org.apache.tinkerpop.gremlin.server.handler.WsAndHttpChannelizerHandler;
import org.apache.tinkerpop.gremlin.server.util.ServerGremlinExecutor;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.UPGRADE;
import static org.apache.tinkerpop.gremlin.server.AbstractChannelizer.PIPELINE_AUTHENTICATOR;
import static org.apache.tinkerpop.gremlin.server.AbstractChannelizer.PIPELINE_HTTP_RESPONSE_ENCODER;
import static org.apache.tinkerpop.gremlin.server.AbstractChannelizer.PIPELINE_REQUEST_HANDLER;

public class WsAndHttpJWTChannelizerHandler  extends WsAndHttpChannelizerHandler
{

//  private final HttpChannelizer wsChannelizer = new HttpChannelizer();
//  private HttpGremlinEndpointHandler httpGremlinEndpointHandler;
//  AbstractAuthenticationHandler authenticationHandler;

  public WsAndHttpJWTChannelizerHandler()
  {
//    this.authenticationHandler =  authenticationHandler;
  }

  public void init(final ServerGremlinExecutor serverGremlinExecutor) {
    //WebSocketChannelizer has everything needed for the http endpoint to work
//    wsChannelizer.init(serverGremlinExecutor);
//    this.httpGremlinEndpointHandler = httpGremlinEndpointHandler;
  }

  public void configure(final ChannelPipeline pipeline) {
//    wsChannelizer.configure(pipeline);
//
//    if (null != pipeline.get(PIPELINE_AUTHENTICATOR))
//    {
//      pipeline.remove(PIPELINE_AUTHENTICATOR);
//    }
//    pipeline.addLast(PIPELINE_AUTHENTICATOR, authenticationHandler);

  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object obj) {
//    final ChannelPipeline pipeline = ctx.pipeline();
//    if (obj instanceof HttpMessage && !isWebSocket((HttpMessage)obj)) {
//      if (null != pipeline.get(PIPELINE_AUTHENTICATOR)) {
//        pipeline.remove(PIPELINE_REQUEST_HANDLER);
//        final ChannelHandler authenticator = pipeline.get(PIPELINE_AUTHENTICATOR);
//        pipeline.remove(PIPELINE_AUTHENTICATOR);
//        pipeline.addAfter(PIPELINE_HTTP_RESPONSE_ENCODER, PIPELINE_AUTHENTICATOR, authenticator);
//        pipeline.addAfter(PIPELINE_AUTHENTICATOR, PIPELINE_REQUEST_HANDLER, this.httpGremlinEndpointHandler);
//      } else {
//        pipeline.remove(PIPELINE_REQUEST_HANDLER);
//        pipeline.addAfter(PIPELINE_HTTP_RESPONSE_ENCODER, PIPELINE_REQUEST_HANDLER, this.httpGremlinEndpointHandler);
//      }
//    }
    ctx.fireChannelRead(obj);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
    ctx.close();
  }

  static boolean isWebSocket(final HttpMessage msg) {
    final String connectionHeader = msg.headers().get(CONNECTION);
    final String upgradeHeader = msg.headers().get(UPGRADE);
    return (null != connectionHeader && connectionHeader.equalsIgnoreCase("Upgrade")) ||
        (null != upgradeHeader && upgradeHeader.equalsIgnoreCase("WebSocket"));
  }


}
