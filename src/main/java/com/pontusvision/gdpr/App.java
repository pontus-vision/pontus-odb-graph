package com.pontusvision.gdpr;

//import com.netflix.astyanax.connectionpool.exceptions.ThrottledException;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.tinkerpop.server.OGremlinServerPlugin;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.orientdb.OrientGraphFactory;
import org.apache.tinkerpop.gremlin.orientdb.OrientStandardGraph;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.server.GraphManager;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.server.util.ServerGremlinExecutor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jhades.JHades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class App // implements RequestStreamHandler
{
  private static final Logger logger = LoggerFactory.getLogger(App.class);
  //    public static JanusGraphManagement graphMgmt;
  public static OrientStandardGraph graph;
  public static GremlinServer gserver;
  public static GraphTraversalSource g;
  public static Settings settings;
  public static OServer oServer;
  public static GremlinExecutor executor;

  public static void main(String[] args) {
    App.mainHandler(args);
  }

  public static void init(String file) throws Exception {

    //      final Settings settings;
    try {
      System.setProperty("distributed","false" );

      settings = Settings.read(file);
    } catch (Exception ex) {
      logger.error("Configuration file at {} could not be found or parsed properly. [{}]", file, ex.getMessage());
      ex.printStackTrace();
      return;
    }

    oServer = OServerMain.create(true);
    oServer.startup();
    logger.info("BEFORE ACTIVATING SERVER *******");
    oServer.activate();
    logger.info("BEFORE GETTING  SERVER PLUGIN *******");

    PontusGremlinPlugin plugin = oServer.getPlugin("gremlin-server");

    executor = plugin.getGremlinExecutor();
    gserver = plugin.getGremlinServer();

//
//    logger.info("Configuring Gremlin Server from {}", file);
//    gserver = new GremlinServer(settings);
//    logger.info("Created new Server; starting it");
//
//    CompletableFuture<ServerGremlinExecutor> c = gserver.start().exceptionally(t -> {
//      logger.error("Gremlin Server was unable to start and will now begin shutdown: {}", t.getMessage());
//      gserver.stop().join();
//      System.exit(-1);
//      return null;
//    });
//    logger.info("Waiting for the server to start");
//
//    ServerGremlinExecutor sge = c.get();
//    logger.info("Getting server executor");
//
////    ServerGremlinExecutor sge = gserver.getServerGremlinExecutor();
//    executor = sge.getGremlinExecutor();
////    sge.getGremlinExecutor().eval("graph = TinkerGraph.open()\n" +
////        "g = graph.traversal()\n");
//    logger.info("warming up with 1+1 query.");
//
//    String obj = (String) executor.eval("1+1").join().toString();
//    logger.info("1+1 query results: " + obj);
//
//    GraphManager graphMgr = sge.getGraphManager();
//    Set<String> graphNames = graphMgr.getGraphNames();
//    for (String graphName : graphNames)
//    {
//      logger.info("Found Graph: " + graphName);
//      graph = (OrientStandardGraph) graphMgr.getGraph(graphName);
//      //                graphMgmt = graph.openManagement();
//      g = graph.traversal();
//    }
//
//    if (graphNames.size() == 0)
//    {
//      for (Map.Entry<String, String> entry : settings.graphs.entrySet())
//      {
//        Configuration conf = (new DefaultConfigurationBuilder(entry.getValue())).getConfiguration();
//        graph =  new OrientStandardGraph(new OrientGraphFactory(),conf);
//        graphMgr.putGraph(entry.getKey(), graph);
//        g = graph.traversal();
//
//
//      }
//
//    }
//    graphMgr.putTraversalSource("g", App.g);

//    oServer.activate();
    logger.info("BEFORE Getting GRAPH  *******");
    graph = (OrientStandardGraph) g.getGraph();

    graph.getRawDatabase().setConflictStrategy(new ORecordConflictResolver() {
      @Override
      public byte[] onUpdate(ORecordId rid, ORecordVersion iRecordVersion, byte[] iRecordContent, ORecordVersion iDatabaseVersion) {
        ODocument storedRecord = rid.getRecord();
        ODocument newRecord = new ODocument().fromStream(iRecordContent);

        storedRecord.merge(newRecord, false, true);

        iDatabaseVersion.setCounter(Math.max(iDatabaseVersion.getCounter(), iRecordVersion.getCounter()));

        return storedRecord.toStream();
      }
    });

  }
  public static Server createJettyServer(){
    String portStr = System.getenv("GRAPHDB_REST_PORT");
    int port = 3001;
    if (portStr != null) {
      port = Integer.parseInt(portStr);
    }
    Server server = new Server(port);
    ResourceConfig config = new ResourceConfig();
    config.packages("com.pontusvision.gdpr", "com.pontusvision.ingestion", "com.pontusvision.security");
    ServletHolder servlet = new ServletHolder(new ServletContainer(config));

    ServletContextHandler context = new ServletContextHandler(server, "/*");
    context.addServlet(servlet, "/*");

    return server;
  }

  public static void mainHandler(String[] args) {
    new JHades().overlappingJarsReport();

    Server server = createJettyServer();


    try {

      server.start();
      String file = args.length == 0 ? "config/gremlin-server.yaml" : args[0];

      //      final Settings settings;
      try {
        App.init(file);
      } catch (Exception ex) {
        logger.error("Configuration file at {} could not be found or parsed properly. [{}]", file, ex.getMessage());
        ex.printStackTrace();
        return;
      }

      server.join();
      //      c.join();
    } catch (Throwable e) {
      e.printStackTrace();
    } finally {
      server.destroy();
    }
  }

  //  @Override public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
  //      throws IOException
  //  {
  //    handler.proxyStream(inputStream, outputStream, context);
  //  }
}
