package com.pontusvision.gdpr;

//import com.netflix.astyanax.connectionpool.exceptions.ThrottledException;

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
import org.janusgraph.core.JanusGraph;
import org.jhades.JHades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class App
{
  private static final Logger logger = LoggerFactory.getLogger(App.class);
  public static JanusGraph graph;
  //    public static JanusGraphManagement graphMgmt;
  public static GremlinServer gserver;
  public static GraphTraversalSource g;
  public static Settings settings;



  public static void main(String[] args)
  {
    new JHades().overlappingJarsReport();

    String portStr = System.getenv("GRAPHDB_REST_PORT");
    int port = 3001;
    if (portStr != null){
      port = Integer.parseInt(portStr);
    }
    Server server = new Server(port);

    try
    {
      ResourceConfig config = new ResourceConfig();
      config.packages("com.pontusvision.gdpr");
      ServletHolder servlet = new ServletHolder(new ServletContainer(config));

      ServletContextHandler context = new ServletContextHandler(server, "/*");
      context.addServlet(servlet, "/*");

      server.start();

      String file = args.length == 0 ? "conf/gremlin-server.yml" : args[0];

//      final Settings settings;
      try
      {
        settings = Settings.read(file);
      }
      catch (Exception ex)
      {
        logger.error("Configuration file at {} could not be found or parsed properly. [{}]", file, ex.getMessage());
        ex.printStackTrace();
        return;
      }

      logger.info("Configuring Gremlin Server from {}", file);
      gserver = new GremlinServer(settings);
      CompletableFuture<ServerGremlinExecutor> c = gserver.start().exceptionally(t -> {
        logger.error("Gremlin Server was unable to start and will now begin shutdown: {}", t.getMessage());
        gserver.stop().join();
        return null;
      });

      ServerGremlinExecutor sge = c.get();
      ServerGremlinExecutor executor = gserver.getServerGremlinExecutor();
      executor.getGremlinExecutor().eval("graph = TinkerGraph.open()\n" +
              "g = graph.traversal()\n");


      GraphManager graphMgr = sge.getGraphManager();
      Set<String> graphNames = graphMgr.getGraphNames();

      for (String graphName : graphNames)
      {
        logger.debug("Found Graph: " + graphName);
        graph = (JanusGraph) graphMgr.getGraph(graphName);
        //                graphMgmt = graph.openManagement();
        g = graph.traversal();
      }

      //            graph = graphMgr.getGraph("graph");

      //            initGraph(graphConfFile);

      //            Graph graph = initGraph(graphConfFile);
      //            ServerGremlinExecutor executor = gserver.getServerGremlinExecutor();
      //            executor.getGremlinExecutor().eval("graph = TinkerGraph.open()\n" +
      //                    "g = graph.traversal()\n");

      //            gserver.getServerGremlinExecutor().getGraphManager().putGraph("graph", graph);
      //            gserver.getServerGremlinExecutor().getGraphManager().putTraversalSource("g", graph.traversal());
      //
      //            gserver.

      //            Configuration conf = getDefaultConfigs();
      //            Cluster cluster = Cluster.open(conf);
      //
      //            Client unaliasedClient = cluster.connect();
      //
      //            Client client = unaliasedClient; //.alias("g1");
      //
      //
      //            ResultSet res = client.submit("[1,2,3,4]");
      //
      //            logger.debug(res.toString());
      //

      server.join();
      c.join();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    finally
    {
      server.destroy();
    }
  }
}
