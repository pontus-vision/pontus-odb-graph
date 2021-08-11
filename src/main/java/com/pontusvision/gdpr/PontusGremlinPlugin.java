package com.pontusvision.gdpr;

import com.orientechnologies.tinkerpop.server.OGremlinServerPlugin;
import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.server.GremlinServer;

public class PontusGremlinPlugin extends OGremlinServerPlugin {
  public PontusGremlinPlugin() {
    super();

  }

  public GremlinServer getGremlinServer() {
    return this.gremlinServer;
  }

  public GremlinExecutor getGremlinExecutor() {
    return this.gremlinServer.getServerGremlinExecutor().getGremlinExecutor();
  }
}
