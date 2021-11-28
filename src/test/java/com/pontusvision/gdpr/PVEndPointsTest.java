package com.pontusvision.gdpr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pontusvision.gdpr.report.ReportTemplateRenderRequest;
import com.pontusvision.gdpr.report.ReportTemplateUpsertRequest;
import com.pontusvision.gdpr.report.ReportTemplateUpsertResponse;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestClassOrder(AnnotationTestsOrderer.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassesOrder(6)
public class PVEndPointsTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

//   what about "InterruptedException" that are grayed out ?!?!
  @Test
  public void test00001Hello() throws InterruptedException {

    try {
      HttpClient client = HttpClients.createMinimal();

      HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/hello"));

//      getRequest.setHeader("Content-Type", "application/json");
      getRequest.setHeader("Accept", "*/*");

      String res = IOUtils.toString(client.execute(getRequest).getEntity().getContent());
      assertEquals("Hello, world!", res, "Response from endpoint ~/home/hello");

    } catch (IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00002Param() throws InterruptedException {

    try {
      HttpClient client = HttpClients.createMinimal();

      HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/param?name=Omar"));

//      getRequest.setHeader("Content-Type", "application/json");
      getRequest.setHeader("AUTHORIZATION", ": granted");

      String res = IOUtils.toString(client.execute(getRequest).getEntity().getContent());
      assertEquals("Hello, Omar AUTHORIZATION: granted", res, "message from ~/home/param endpoint");

    } catch (IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00003GraphanaBackend() throws InterruptedException {

    try {
      HttpClient client = HttpClients.createMinimal();

      HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/grafana_backend"));

      getRequest.setHeader("Accept", "*/*");

      String res = IOUtils.toString(client.execute(getRequest).getEntity().getContent());
      assertEquals("{\"message\":\"Data source is working\",\"status\":\"success\",\"title\":\"success\"}", res,
              "json message from ~/home/grafana_backend endpoint");

    } catch (IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00004Gremlin() throws InterruptedException {

    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");

    try {

      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
              "App.g.V().has('Person.Organisation.Short_Name',eq('Pessoa Nova5')).next().id().toString()");
      String query1, query2, query3;

      HttpClient client = HttpClients.createMinimal();
      HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/gremlin"));
      request.setHeader("Content-Type", "application/json");
      request.setHeader("Accept", "application/json");
      StringEntity data = new StringEntity(gson.toJson(gremlinReq));
      request.setEntity(data);

      query1 = IOUtils.toString(client.execute(request).getEntity().getContent());
      System.out.println("print 1: " + query1);

//      JSONObject json = new JSONObject(query1);
//      String value = json.getJSONObject("@value").toString();
//      System.out.println(value);

      gremlinReq.setGremlin("App.g.V().has('Person.Natural.Full_Name',eq('Pessoa Nova5')).next().id().toString()");
      data = new StringEntity(gson.toJson(gremlinReq));
      request.setEntity(data);
      query2 = IOUtils.toString(client.execute(request).getEntity().getContent());
      System.out.println("print 2: " +query2);

      gremlinReq.setGremlin(null);
      data = new StringEntity(gson.toJson(gremlinReq));
      request.setEntity(data);
      query3 = IOUtils.toString(client.execute(request).getEntity().getContent());
      System.out.println("print 3: " +query3);


    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      assertNull(e);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

//  TODO: try to make a true reply from the Function, because till now, only error messages are being returned !!
  @Test
  public void test00005TemplateRender() throws InterruptedException {

    jsonTestUtil("pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
            "sharepoint_data_sources");

    try {

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST");
      req.setTemplatePOLEType("Object.Data_Sources");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(" {% set var1=1 %} {{ var1 }} {{ context.Object_Data_Source_Name }}"
                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Metadata.Type.Object.Data_Source", P.eq("Object.Data_Source"))
              .id().next().toString();

      ReportTemplateRenderRequest reportReq = new ReportTemplateRenderRequest();
      reportReq.setTemplateId(null);
      reportReq.setRefEntryId(templateId);

      HttpClient client = HttpClients.createMinimal();
      HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/report/template/render"));
      request.setHeader("Content-Type", "application/json");
      request.setHeader("Accept", "application/json");
      StringEntity data = new StringEntity(gson.toJson(reportReq));
      request.setEntity(data);

      String output = IOUtils.toString(client.execute(request).getEntity().getContent());
      assertEquals("{\"type\":\"reportTemplateRenderResponse\",\"errorStr\":\"Missing ReportId\"}",
              output, "Error because TemplateId is NULL");

      reportReq.setTemplateId(contextId);
      reportReq.setRefEntryId(null);

      data = new StringEntity(gson.toJson(reportReq));
      request.setEntity(data);

      output = IOUtils.toString(client.execute(request).getEntity().getContent());
      assertEquals("{\"type\":\"reportTemplateRenderResponse\",\"errorStr\":\"Missing RefId\"}",
              output, "Error because RefEntryId is NULL");

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

//  TODO: Work on KPIs Endpoints Tests
  @Test
  public void test00006calPOLECounts() throws InterruptedException {

    try {
      HttpClient client = HttpClients.createMinimal();

      HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/kpi/calculatePOLECounts"));

//      getRequest.setHeader("Content-Type", "application/json");
      getRequest.setHeader("Accept", "*/*");

      String res = IOUtils.toString(client.execute(getRequest).getEntity().getContent());
      assertEquals("????", res, "???? ~/home/kpi/calculatePOLECounts");

    } catch (IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

}
