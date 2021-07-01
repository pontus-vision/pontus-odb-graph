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
            System.setProperty("distributed","false" );
            System.setProperty("ORIENTDB_HOME","/orientdb" );
            System.setProperty("orientdb_home","/orientdb" );
            System.setProperty("java.util.logging.manager","com.orientechnologies.common.log.ShutdownLogManager" );
            System.setProperty("java.util.logging.config.file","/orientdb/config/orientdb-server-log.properties" );
            System.setProperty("tx.commit.synch","true" );
            System.setProperty("tx.log.synch","true" );
            System.setProperty("tx.log.fileType","mmap" );
            System.setProperty("index.txMode","FULL" );
            System.setProperty("nonTX.recordUpdate.synch","true" );
            System.setProperty("environment.dumpCfgAtStartup","false" );
            System.setProperty("orientdb.config.file","/orientdb/config/orientdb-server-config.xml" );
            System.setProperty("orientdb.www.path","/orientdb/www" );
            System.setProperty("orientdb.build.number",
                    "develop@re3b3314d5494363f823331471c31461678d1b734; 2019-10-28 11:33:25+0000");
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
            .packages("com.pontusvision.gdpr", "com.pontusvision.ingestion", "com.pontusvision.security")
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
