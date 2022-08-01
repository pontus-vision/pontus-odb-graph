package com.pontusvision.gdpr;

import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.util.JacksonFeature;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import org.apache.tinkerpop.gremlin.orientdb.OrientStandardGraph;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jhades.JHades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    protected static final Logger logger = LoggerFactory.getLogger(LambdaHandler.class);

    static {
        try {
            App.setLambdaProps();
            System.out.println("IN LAMBDA HANDLER");
            App.init(System.getenv("PV_ODB_CONF")!=null?
                    System.getenv("PV_ODB_CONF"):
                    "/orientdb/config/gremlin-server.yaml");


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected static final ResourceConfig jerseyApplication = new ResourceConfig()
            .packages("com.pontusvision.gdpr", "com.pontusvision.ingestion", "com.pontusvision.security",
                "com.pontusvision.web",
                "com.pontusvision.kpis")
            .register(JacksonFeature.class);
    protected static final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler
            = JerseyLambdaContainerHandler
            .getAwsProxyHandler(jerseyApplication);

//    private JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> container =
//        JerseyLambdaContainerHandler.getAwsProxyHandler(jerseyApplication);


    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
        return LambdaHandler.handler.proxy(awsProxyRequest, context);
    }


}
