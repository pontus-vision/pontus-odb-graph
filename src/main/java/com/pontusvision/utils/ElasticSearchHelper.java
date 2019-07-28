package com.pontusvision.utils;

import com.pontusvision.gdpr.App;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.janusgraph.diskstorage.configuration.BasicConfiguration;
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration;
import org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration;
import org.janusgraph.diskstorage.es.ElasticSearchIndex;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ElasticSearchHelper
{

  public static class RestClientSetup extends org.janusgraph.diskstorage.es.rest.RestClientSetup
  {
    public RestClient getRestClient(org.janusgraph.diskstorage.configuration.Configuration config) throws IOException
    {
      List<HttpHost> hosts = new ArrayList<>();
      int defaultPort = config.has(GraphDatabaseConfiguration.INDEX_PORT) ?
          config.get(GraphDatabaseConfiguration.INDEX_PORT) :
          9200;
      String httpScheme = config.get(ElasticSearchIndex.SSL_ENABLED) ? "https" : "http";
      String[] hostsArray = config.get(GraphDatabaseConfiguration.INDEX_HOSTS);
      int scrollKeepAlive = hostsArray.length;

      for (int idx = 0; idx < scrollKeepAlive; ++idx)
      {
        String host = hostsArray[idx];
        String[] hostStringParts = host.split(":");
        String hostname = hostStringParts[0];
        int hostPort = defaultPort;
        if (hostStringParts.length == 2)
        {
          hostPort = Integer.parseInt(hostStringParts[1]);
        }

        hosts.add(new HttpHost(hostname, hostPort, httpScheme));
      }

      RestClient rc = this.getRestClient(hosts.toArray(new HttpHost[hosts.size()]), config);

      return rc;
    }

  }

  public static String INDEX_NAME = "search";
  public static RestClientSetup rcs = new RestClientSetup();

  public static String createTemplate(String templateName, String templateStr) throws IOException, ConfigurationException
  {
    StringBuilder sb = new StringBuilder();
    for (String graphConfFile : App.settings.graphs.values())
    {
      Configuration baseConf = new PropertiesConfiguration(graphConfFile);
      final CommonsConfiguration cc = new CommonsConfiguration(baseConf);
      final ModifiableConfiguration config = new ModifiableConfiguration(GraphDatabaseConfiguration.ROOT_NS, cc,
          BasicConfiguration.Restriction.NONE);
      final org.janusgraph.diskstorage.configuration.Configuration indexConfig = config.restrictTo(INDEX_NAME);
      RestClient rc = rcs.getRestClient(indexConfig);
      String endPoint = "/_template/" + templateName;
      String method = "PUT";
      final HttpEntity entity = new ByteArrayEntity(templateStr.getBytes(), ContentType.APPLICATION_JSON);
      final Response response = rc.performRequest(method, endPoint, Collections.emptyMap(), entity);
      if (response.getStatusLine().getStatusCode() >= 400)
      {
        throw new IOException("Error executing request: " + response.getStatusLine().getReasonPhrase());
      }
      sb.append(response.toString()).append("\n");
    }
    return sb.toString();
  }

}
