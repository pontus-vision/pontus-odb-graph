package com.pontusvision.gdpr;

import com.pontusvision.gdpr.report.ReportTemplateRenderRequest;
import com.pontusvision.gdpr.report.ReportTemplateUpsertRequest;
import com.pontusvision.gdpr.report.ReportTemplateUpsertResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
      getRequest.setHeader("X-PV-NAME", ": granted");

      String res = IOUtils.toString(client.execute(getRequest).getEntity().getContent());
      assertEquals("Hello, Omar X-PV-NAME: granted", res, "message from ~/home/param endpoint");

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

      String res = IOUtils.toString(client.execute(getRequest).getEntity().getContent(), Charset.defaultCharset());
      assertEquals("{\"message\":\"Data source is working\",\"status\":\"success\",\"title\":\"success\"}", res,
          "json message from ~/home/grafana_backend endpoint");

    } catch (IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00004Gremlin() throws InterruptedException {

    jsonTestUtil("ploomes/ploomes1.json", "$.value", "ploomes_clientes");

    try {

      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
          "App.g.V().has('Person_Organisation_Name',eq('PESSOA NOVA5')).next().id().toString()");
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

      gremlinReq.setGremlin("App.g.V().has('Person_Organisation_Name',eq('PESSOA NOVA5')).next().id().toString()");
      data = new StringEntity(gson.toJson(gremlinReq));
      request.setEntity(data);
      query2 = IOUtils.toString(client.execute(request).getEntity().getContent());
      System.out.println("print 2: " + query2);

      gremlinReq.setGremlin(null);
      data = new StringEntity(gson.toJson(gremlinReq));
      request.setEntity(data);
      query3 = IOUtils.toString(client.execute(request).getEntity().getContent());
      System.out.println("print 3: " + query3);


    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      assertNull(e);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void test00005TemplateRender() throws InterruptedException {

    jsonTestUtil("sharepoint/pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
        "sharepoint_data_sources");

    try {

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST");
      req.setTemplatePOLEType("Object_Data_Sources");
      req.setReportTextBase64(
          Base64.getEncoder().encodeToString(" {% set var1=1 %} {{ var1 }} {{ context.Object_Data_Source_Name }}"
              .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Metadata_Type_Object_Data_Source", P.eq("Object_Data_Source"))
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

      HttpResponse httpResponse = client.execute(request);
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      String output = IOUtils.toString(httpResponse.getEntity().getContent(),Charset.defaultCharset());
      assertEquals("{\"type\":\"reportTemplateRenderResponse\",\"errorStr\":\"Missing ReportId\"}",
          output, "Error because ReportId is NULL");
      assertEquals(400, statusCode, "Expected to return 400 because ReportId is NULL");

      reportReq.setTemplateId(contextId);
      reportReq.setRefEntryId(null);

      data = new StringEntity(gson.toJson(reportReq));
      request.setEntity(data);
      httpResponse = client.execute(request);
      statusCode = httpResponse.getStatusLine().getStatusCode();

      output = IOUtils.toString(client.execute(request).getEntity().getContent());
      assertEquals("{\"type\":\"reportTemplateRenderResponse\",\"errorStr\":\"Missing RefId\"}",
          output, "Error because RefEntryId is NULL");
      assertEquals(400, statusCode, "Expected to return 400 because RefEntryId is NULL");

    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  @Test
  public void test00006WebStatic() throws InterruptedException {


    try {


      HttpClient client = HttpClients.createMinimal();
      HttpGet request = new HttpGet(URI.create("http://localhost:3001/pv"));
      request.setHeader("Accept", "*/*");

      HttpResponse resp = client.execute(request);

      String output = IOUtils.toString(resp.getEntity().getContent());
      assertEquals(200,
          resp.getStatusLine().getStatusCode(), "200 status code");


    } catch (IOException e) {
      e.printStackTrace();
    }

  }


//  TODO: Work on KPIs Endpoints Tests
//  @Test
//  public void test00006calPOLECounts() throws InterruptedException {
//
//    try {
//      HttpClient client = HttpClients.createMinimal();
//
//      HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/kpi/calculatePOLECounts"));
//
////      getRequest.setHeader("Content-Type", "application/json");
//      getRequest.setHeader("Accept", "*/*");
//
//      String res = IOUtils.toString(client.execute(getRequest).getEntity().getContent());
//      assertEquals("????", res, "???? ~/home/kpi/calculatePOLECounts");
//
//    } catch (IOException e) {
//      e.printStackTrace();
//      assertNull(e);
//    }
//  }


//  @Test
//  public void test00007getDSARStatsPerOrganisation() throws InterruptedException {
//
//    try {
//
//      HttpClient client = HttpClients.createMinimal();
//      HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/kpi/getDSARStatsPerOrganisation"));
//      getRequest.setHeader("Accept", "*/*");
//
//      HttpResponse resp = client.execute(getRequest);
//
//      String output = IOUtils.toString(resp.getEntity().getContent());
//      assertEquals("????", output, "????");
//
//    } catch (IOException e) {
//      e.printStackTrace();
//      assertNull(e);
//    }
//  }

}
