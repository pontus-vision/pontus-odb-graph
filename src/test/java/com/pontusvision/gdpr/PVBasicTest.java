package com.pontusvision.gdpr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pontusvision.security.JWTTokenNeededFilter;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
@TestClassesOrder(1)
//@RunWith(JUnitPlatform.class)
public class PVBasicTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */
  public PVBasicTest() throws URISyntaxException {
    super();
    fakeContainerReqContext = new ContainerRequest(new URI("http://localhost:18443"),
            new URI("http://localhost:18443"), "POST", null, new MapPropertiesDelegate(), null);

    fakeContainerReqContext.setProperty("pvDecodedJWT", JWTTokenNeededFilter.createDummyDecodedJWT());
  }

  @Test
  public void test00001Phase1CSVIngestion() throws InterruptedException {
    try {

      csvTestUtil("phase1.csv", "phase1_csv");


      String userId =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('ROZELL COLEMAN')).next().id().toString()")
                      .get().toString();

      String rozellConnectionsQuery = "App.g.V(\"" + userId + "\").bothE().count().next().toString()";
      String rozellConnections = App.executor.eval(rozellConnectionsQuery)
              .get().toString();

      assertEquals(rozellConnections, "3");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00002CsvPolaris() throws InterruptedException {
    try {

      csvTestUtil("SAP/sap-polaris-clientes.csv", "Cliente_SAP_PosVenda_POLARIS");


//    Testing for Person_Natural WITH Tax_Number
      String userId0 =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MATEUS SILVA PINTO'))\n" +
                      ".next().id().toString()").get().toString();
      String pintoConnectionsQuery = "App.g.V(\"" + userId0 + "\").bothE('Has_Phone').count().next().toString()";
      String pintoConnections = App.executor.eval(pintoConnectionsQuery)
              .get().toString();
      assertEquals("2", pintoConnections, "2 Has_Phone");

//    Testing for Person_Natural withOUT Tax_Number
      String munirLocationEdgesCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MUNIR HANDAUS'))\n" +
                      ".bothE('Is_Located').count().next().toString()").get().toString();
      assertEquals("1", munirLocationEdgesCount, "1 Is_Located");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00003SharepointTreinamentos() throws InterruptedException {
    try {

      jsonTestUtil("sharepoint/pv-extract-sharepoint-treinamentos.json", "$.queryResp[*].fields",
              "sharepoint_treinamentos");

//    test0000 for PEDRO Person_Employee NODE
      String userId =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('PEDRO ALVARO CABRAL'))\n" +
                      ".next().id().toString()").get().toString();
      String pedroConnectionsQuery = "App.g.V(\"" + userId + "\").bothE().count().next().toString()";
      String pedroConnections = App.executor.eval(pedroConnectionsQuery).get().toString();
      assertEquals(pedroConnections, "1");

//    test0000 for MARCELA Person_Employee NODE
      String marcelaTraining =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARCELA ALVES'))\n" +
                      ".in('Completed_By').out('Course_Training').properties('Object_Awareness_Campaign_Description')" +
                      ".value().next().toString()").get().toString();
      assertEquals("1º CURSO - LGPD - LEI GERAL DE PROTEÇÃO DE DADOS", marcelaTraining, "Treinamento cursado por Marcela Alves");

//    test0000 for Object_Awareness_Campaign_Description
      String getEmployeeNameByTraining =
              App.executor.eval("App.g.V().has('Object_Awareness_Campaign_Description', " +
                      "eq('COMO CUIDAR DOS SEUS DADOS PESSOAIS DIGITAIS/VIRTUAIS')).in('Course_Training')" +
                      ".out('Completed_By').properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("LEILA BRAGANÇA", getEmployeeNameByTraining, "O curso em questão foi feito por Leila Bragança");

//    test0000 for Object_Awareness_Campaign_Description 3
      String caioTrainingStatus =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('CAIO DA SILVA SAURO'))\n" +
                      ".in('Completed_By').properties('Event_Training_Status')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Passed", caioTrainingStatus, "Caio has PASSED the Training");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @ParameterizedTest(name = "Sharepoint tests ({0}) rule Name {1}, expected Data Source Name = {2} ")
  @CsvSource({
          "sharepoint/pv-extract-sharepoint-data-sources.json,   sharepoint_data_sources,   SHAREPOINT/DATA-SOURCES, 5, 7, 2, 2",
          "sharepoint/pv-extract-sharepoint-fontes-de-dados.json,   sharepoint_fontes_de_dados,   SHAREPOINT/FONTES-DE-DADOS, 9, 8, 2, 4"
//   NOTE: WE expect 7, and not 5 entries in the second run, because the two JSON files have the same data source
//   names (CRM-Leads and CRM-Users), which will end up with 2 data policies each (one from the first run, and one from
//   the second).
  })
  public void test00004SharepointDataSources(String jsonFile, String ruleName, String dataSourceName,
                                             String expectedDataPolicyCount, String expectedModuleCount,
                                             String expectedSubsystemCount, String expectedSystemCount) throws InterruptedException {
    try {
//      jsonTestUtil("pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
//          "sharepoint_data_sources");

      jsonTestUtil(jsonFile, "$.queryResp[*].fields",
              ruleName);

      String queryPrefix = "App.g.V().has('Object_Data_Source_Name', eq('" + dataSourceName + "'))\n";
//    test0000 for COUNT(dataSources)
      String countDataSources =
              App.executor.eval(queryPrefix +
                      ".count().next().toString()").get().toString();
      assertEquals("1", countDataSources);

//    test0000 for COUNT(event Ingestions)
      String countEventGroupIngestions =
              App.executor.eval(queryPrefix +
                      ".both()\n" +
                      ".count().next().toString()").get().toString();
      // expecting 1 less Event_Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("1", countEventGroupIngestions);

      String countEventIngestions =
              App.executor.eval(queryPrefix +
                      ".out().out()\n" +
                      ".count().next().toString()").get().toString();
      // expecting 1 less Event_Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("7", countEventIngestions);


      String countObjectDataSourcesIngested =
              App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
//              ".in('Has_Policy')" +
//              ".has('Metadata_Type_Object_Data_Policy', eq('Object_Data_Policy'))\n" +
                      ".count().next().toString()").get().toString();
      // expecting 1 less Event_Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("7", countObjectDataSourcesIngested);

      String countDataPolicy =
              App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".out('Has_Policy')" +
                      ".has('Metadata_Type_Object_Data_Policy', eq('Object_Data_Policy')).dedup()" +
                      ".count().next().toString()").get().toString();
      // expecting 1 less Event_Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals(expectedDataPolicyCount, countDataPolicy);

      if ("sharepoint_fontes_de_dados".equals(ruleName)) {

        String numSensitiveData = App.executor.eval(queryPrefix +
                ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                ".out('Has_Sensitive_Data')" +
                ".has('Metadata_Type_Object_Sensitive_Data', eq('Object_Sensitive_Data')).id().toSet()" +
                ".size().toString()").get().toString();
        assertEquals("15", numSensitiveData);

        String numTelefones = App.executor.eval("App.g.V().has('Object_Sensitive_Data_Description', eq('TELEFONE'))\n" +
                ".bothE().count().next().toString()").get().toString();
        assertEquals("2", numTelefones);
        String numNomes = App.executor.eval("App.g.V().has('Object_Sensitive_Data_Description', eq('NOME'))\n" +
                ".bothE().count().next().toString()").get().toString();
        assertEquals("5", numNomes);

      }

      String numModules = App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".out('Has_Module').dedup()" +
                      ".count().next().toString()"
              //".values('Object_Module_Name').toList()"
      ).get().toString();

      assertEquals(expectedModuleCount, numModules, "Number of Object_Module Vertices");
      
      String numSubsystem = App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".out('Has_Module').out('Has_Subsystem').dedup()" +
                      ".count().next().toString()"
              //".values('Object_Module_Name').toList()"
      ).get().toString();

      assertEquals(expectedSubsystemCount, numSubsystem, "Number of Object_Subsystem Vertices");

      String numSystem = App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".out('Has_Module').out('Has_Subsystem').out('Has_System').dedup()" +
                      ".count().next().toString()"
              //".values('Object_Module_Name').toList()"
      ).get().toString();

      assertEquals(expectedSystemCount, numSystem, "Number of Object_System Vertices");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }


  //A new version of this Test can be found @ PVTotvsTest.java > test00004TotvsProtheusPlusPloomes
  @Test
  public void test00005TotvsPloomes() throws InterruptedException {
    try {

      jsonTestUtil("ploomes/ploomes1.json", "$.value", "ploomes_clientes");
      jsonTestUtil("totvs/totvs-sa1.json", "$.objs", "totvs_protheus_sa1_clientes");

//    t'est0000 for Object_Data_Source_Name
      String userId2 =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('TOTVS/PROTHEUS/SA1_CLIENTES'))" +
                      ".next().id().toString()").get().toString();
      String rootConnectionsQuery = "App.g.V(\"" + userId2 + "\").bothE().count().next().toString()";
      String rootConnections = App.executor.eval(rootConnectionsQuery).get().toString();

//    test0000 COUNT(Edges) for COMIDAS 2
      String userId3 =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('COMIDAS 2'))" +
                      ".next().id().toString()").get().toString();
      String comidas2ConnectionsQuery = "App.g.V(\"" + userId3 + "\").bothE().count().next().toString()";
      String comidas2Connections = App.executor.eval(comidas2ConnectionsQuery).get().toString();
      assertEquals("4", comidas2Connections);

//    test0000 COUNT(Edges) for Object_Email_Address
      String userId4 =
              App.executor.eval("App.g.V().has('Object_Email_Address_Email',eq('jonas@comida1.com.br'))" +
                      ".next().id().toString()").get().toString();
      String emailConnectionsQuery = "App.g.V(\"" + userId4 + "\").bothE().count().next().toString()";
      String emailConnections = App.executor.eval(emailConnectionsQuery).get().toString();
      assertEquals("1", emailConnections);

//    test0000 COUNT(Edges) for Object_Phone_Number
      String userId5 =
              App.executor.eval("App.g.V().has('Object_Phone_Number_Numbers_Only',eq('111111111'))" +
                      ".next().id().toString()").get().toString();
      String phoneConnectionsQuery = "App.g.V(\"" + userId5 + "\").bothE().count().next().toString()";
      String phoneConnections = App.executor.eval(phoneConnectionsQuery).get().toString();
      assertEquals("1", phoneConnections);


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00006SQL() {
    Gson gson = new Gson();
    RecordRequest req = gson.fromJson(jsonReq, RecordRequest.class);

    req.search.vid = "#-1:-1";
    req.search.relationship = "has_server";
    req.search.direction = "->";

    String sqlCount = req.getSQL(true);

    String expectedSqlCount = "SELECT COUNT(*) FROM (SELECT EXPAND(OUT('has_server')) FROM #-1:-1)";

    assertEquals(expectedSqlCount, sqlCount);

    String sql = req.getSQL(false);
    String expectedSql = "SELECT @rid as id,`Event_Data_Breach_Description`,`Event_Data_Breach_Impact`  FROM (SELECT EXPAND(OUT('has_server')) FROM #-1:-1  WHERE (( `Event_Data_Breach_Impact`  containsText  'OS' ) ) ) ORDER BY `Event_Data_Breach_Impact` DESC SKIP 0 LIMIT 100";
    assertEquals(expectedSql, sql);

    req.search.direction = "<-";
    sqlCount = req.getSQL(true);

    expectedSqlCount = "SELECT COUNT(*) FROM (SELECT EXPAND(IN('has_server')) FROM #-1:-1)";
    assertEquals(expectedSqlCount, sqlCount);

    req.search.vid = null;
    req.search.direction = null;
    req.search.relationship = null;
    sqlCount = req.getSQL(true);
    expectedSqlCount = "SELECT COUNT(*)  FROM `" +
            req.dataType +
            "`  WHERE (( `Event_Data_Breach_Impact`  containsText  'OS' ) ) ";
    assertEquals(expectedSqlCount, sqlCount);


  }

  @Test
  public void test00007SimpleGremlin() throws InterruptedException {


    String res = null;
    try {
      res = App.executor.eval("App.g.V().hasNext();").get().toString();
      System.out.println(res);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

  @Test
  public void test00008Phase1CsvDemoIngestion() throws InterruptedException {


    String res = null;
    try {
      // Ingest twice, and ensure only one JOHN SMITH remains
      csvTestUtil("phase1.csv", "phase1_csv");
      csvTestUtil("phase1.csv", "phase1_csv");

      String numJohnSmiths = App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('JOHN SMITH')).id()" +
                      ".count().next().toString()")
              .get().toString();

      assertEquals("1", numJohnSmiths, "only one John smith after multiple trials");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }
  }


  // A new version of this Test can be found @ PVTotvsTest.java
  @Test
  public void test00009TotvsProtheusSa1Clientes() throws InterruptedException {
    jsonTestUtil("ploomes/ploomes1.json", "$.value", "ploomes_clientes");
    jsonTestUtil("totvs/totvs-sa1.json", "$.objs", "totvs_protheus_sa1_clientes");
//    jsonTestUtil("totvs-sa1.json", "$.objs", "totvs_protheus_sa1_clientes");
//    jsonTestUtil("totvs-sa2-real.json", "$.objs", "totvs_protheus_sa1_clientes");

    try {
      String userId =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('JONAS LEO BATISTA')).next().id().toString()")
                      .get().toString();

      Resource res = new Resource();
      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
              "App.g.V().has('Person_Natural_Full_Name',eq('JONAS LEO BATISTA')).next().id().toString()");
      JsonObject obj = JsonParser.parseString(res.gremlinQuery(fakeContainerReqContext, gson.toJson(gremlinReq))).getAsJsonObject();
      String stringifiedOutput = gson.toJson(obj);
      String res2;
      try {
        HttpClient client = HttpClients.createMinimal();

        HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/gremlin"));

        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        StringEntity data = new StringEntity(gson.toJson(gremlinReq));
//        SerializableEntity data = new SerializableEntity(req);

        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(request).getEntity().getContent());
        System.out.println(res2);


        HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/hello"));

//        request.setHeader("Content-Type", "application/json");
//        request.setHeader("Accept", "application/json");

//        StringEntity getData = new StringEntity(gson.toJson(req));
//        SerializableEntity data = new SerializableEntity(req);

//        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(getRequest).getEntity().getContent());


        System.out.println(res2);

      } catch (IOException e) {
        e.printStackTrace();
        assertNull(e);
      }


      assertEquals(userId, ((obj.get("result").getAsJsonObject())
              .getAsJsonObject().get("data").getAsJsonObject()).get("@value").getAsJsonArray().get(0).getAsString());

      Map<String, Object> bindings = new HashMap() {{
        put("pg_id", userId);
        put("pg_templateText", "eyUgc2V0IHBvc3NpYmxlTWF0Y2hlcyA9IHB2OnBvc3NpYmxlTWF0Y2hlcyhjb250ZXh0LmlkLCd7Ik9iamVjdC5FbWFpbF9BZGRyZXNzIjogMTAuNSwgIk9iamVjdC5JZGVudGl0eV9DYXJkIjogOTAuNSwgIkxvY2F0aW9uLkFkZHJlc3MiOiAxMC4xLCAiT2JqZWN0LlBob25lX051bWJlciI6IDEuMCwgIk9iamVjdC5TZW5zdGl2ZV9EYXRhIjogMTAuMCwgIk9iamVjdC5IZWFsdGgiOiAxLjAsICJPYmplY3QuQmlvbWV0cmljIjogNTAuMCAsICJPYmplY3QuSW5zdXJhbmNlX1BvbGljeSI6IDEuMH0nKSAlfQp7JSBzZXQgbnVtTWF0Y2hlcyA9IHBvc3NpYmxlTWF0Y2hlcy5zaXplKCkgJX0KeyUgaWYgbnVtTWF0Y2hlcyA9PSAwICV9Cnt7IGNvbnRleHQuUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lfX0gw6kgbyDDum5pY28gcmVnaXN0cm8gbm8gc2lzdGVtYS4KCnslIGVsc2UgJX0Ke3sgY29udGV4dC5QZXJzb25fTmF0dXJhbF9GdWxsX05hbWV9fSBDb3JyZXNwb25kZSBwb3RlbmNpYWxtZW50ZSBhIHt7IG51bU1hdGNoZXMgfX0gcmVnaXN0cm97JS0gaWYgbnVtTWF0Y2hlcyAhPSAxIC0lfXN7JSBlbmRpZiAlfS4KCgoKICB7eyAiPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+VGl0dWxhcjwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UGVyY2VudHVhbDwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UHJvcHJpZWRhZGVzIGVtIENvbXVtPC90aD48L3RyPiIgfX0KICB7JSBmb3IgaXRlbSBpbiBwb3NzaWJsZU1hdGNoZXMuZW50cnlTZXQoKSAlfQogIHt7ICAiPHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiUuMmYlJTwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGl0ZW0ua2V5LlBlcnNvbl9OYXR1cmFsX0Z1bGxfTmFtZSAsIGl0ZW0udmFsdWUgKiAxMDAuMCwgaXRlbS5rZXkuTGFiZWxzX0Zvcl9NYXRjaCApIH19CiAgeyUgZW5kZm9yICV9CiAge3sgIjwvdGFibGU+IiB9fQoKeyUgZW5kaWYgJX0=");
      }};

      String report = App.executor.eval("renderReportInBase64(pg_id,pg_templateText)", bindings).get().toString();
      System.out.println(report);

      String getPhoneNumber =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('COMIDAS 1')).out('Has_Phone')" +
                      ".properties('Object_Phone_Number_Raw').value().next().toString()").get().toString();
      assertEquals("111111111", getPhoneNumber, "Número de telefone de Comidas 1");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }


  // A new version of this test can be found @ PVTotvsTest.java
  @Test
  public void test00010PloomesClientes() throws InterruptedException {

    jsonTestUtil("ploomes/ploomes1.json", "$.value", "ploomes_clientes");
//    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");

    try {
      String userId =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('PESSOA NOVA5')).next().id().toString()")
                      .get().toString();

      Resource res = new Resource();
      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
              "App.g.V().has('Person_Organisation_Name',eq('PESSOA NOVA5')).next().id().toString()");
      JsonObject obj = JsonParser.parseString(res.gremlinQuery(fakeContainerReqContext,
              gson.toJson(gremlinReq))).getAsJsonObject();
//      Gson gson = new Gson();
      String stringifiedOutput = gson.toJson(obj);
      String res2;
      try {
        HttpClient client = HttpClients.createMinimal();

        HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/gremlin"));

        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        StringEntity data = new StringEntity(gson.toJson(gremlinReq));
//        SerializableEntity data = new SerializableEntity(req);

        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(request).getEntity().getContent());
        System.out.println(res2);


        HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/hello"));

//        request.setHeader("Content-Type", "application/json");
//        request.setHeader("Accept", "application/json");

//        StringEntity getData = new StringEntity(gson.toJson(req));
//        SerializableEntity data = new SerializableEntity(req);

//        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(getRequest).getEntity().getContent());


        System.out.println(res2);

      } catch (IOException e) {
        e.printStackTrace();
        assertNull(e);
      }


//      String b = "DeBugger Stop!";


      assertEquals(userId, (((obj.get("result").getAsJsonObject())
              .getAsJsonObject().get("data")).getAsJsonObject().get("@value")).getAsJsonArray().get(0).getAsString());

      Map<String, Object> bindings = new HashMap() {{
        put("pg_id", userId);
        put("pg_templateText", "eyUgc2V0IHBvc3NpYmxlTWF0Y2hlcyA9IHB2OnBvc3NpYmxlTWF0Y2hlcyhjb250ZXh0LmlkLCd7Ik9iamVjdC5FbWFpbF9BZGRyZXNzIjogMTAuNSwgIk9iamVjdC5JZGVudGl0eV9DYXJkIjogOTAuNSwgIkxvY2F0aW9uLkFkZHJlc3MiOiAxMC4xLCAiT2JqZWN0LlBob25lX051bWJlciI6IDEuMCwgIk9iamVjdC5TZW5zdGl2ZV9EYXRhIjogMTAuMCwgIk9iamVjdC5IZWFsdGgiOiAxLjAsICJPYmplY3QuQmlvbWV0cmljIjogNTAuMCAsICJPYmplY3QuSW5zdXJhbmNlX1BvbGljeSI6IDEuMH0nKSAlfQp7JSBzZXQgbnVtTWF0Y2hlcyA9IHBvc3NpYmxlTWF0Y2hlcy5zaXplKCkgJX0KeyUgaWYgbnVtTWF0Y2hlcyA9PSAwICV9Cnt7IGNvbnRleHQuUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lfX0gw6kgbyDDum5pY28gcmVnaXN0cm8gbm8gc2lzdGVtYS4KCnslIGVsc2UgJX0Ke3sgY29udGV4dC5QZXJzb25fTmF0dXJhbF9GdWxsX05hbWV9fSBDb3JyZXNwb25kZSBwb3RlbmNpYWxtZW50ZSBhIHt7IG51bU1hdGNoZXMgfX0gcmVnaXN0cm97JS0gaWYgbnVtTWF0Y2hlcyAhPSAxIC0lfXN7JSBlbmRpZiAlfS4KCgoKICB7eyAiPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+VGl0dWxhcjwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UGVyY2VudHVhbDwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UHJvcHJpZWRhZGVzIGVtIENvbXVtPC90aD48L3RyPiIgfX0KICB7JSBmb3IgaXRlbSBpbiBwb3NzaWJsZU1hdGNoZXMuZW50cnlTZXQoKSAlfQogIHt7ICAiPHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiUuMmYlJTwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGl0ZW0ua2V5LlBlcnNvbl9OYXR1cmFsX0Z1bGxfTmFtZSAsIGl0ZW0udmFsdWUgKiAxMDAuMCwgaXRlbS5rZXkuTGFiZWxzX0Zvcl9NYXRjaCApIH19CiAgeyUgZW5kZm9yICV9CiAge3sgIjwvdGFibGU+IiB9fQoKeyUgZW5kaWYgJX0=");
      }};

      String report = App.executor.eval("renderReportInBase64(pg_id,pg_templateText)", bindings).get().toString();
      System.out.println(report);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00011EmailOffice365() throws InterruptedException {
    jsonTestUtil("office365/pv-extract-o365-email.json", "$.value", "pv_email");

    try {
      String numDataSources =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name',eq('OFFICE365/EMAIL')).count().next().toString()")
                      .get().toString();

      assertEquals("1", numDataSources, "Ensure that We only have one data source");

      String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String numDataEventEmailMessageGroups =
              App.executor.eval("App.g.V().has('Event_Email_Msg_Group_Ingestion_Date',eq('" + currDate + "')).count().next().toString()")
                      .get().toString();

      assertEquals("1", numDataEventEmailMessageGroups, "Ensure that We only have one Email Message Group");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00012ADP() throws InterruptedException {
    try {
      csvTestUtil("ADP-real.csv", "ADP");


      String mariaBDay =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('MARIA DA SILVA SANTOS'))" +
                      ".properties('Person_Natural_Date_Of_Birth').value().next().toString()").get().toString();
//      mariaBDay = mariaBDay.replaceAll("... 1980", "GMT 1980");
      assertEquals(dtfmt.parse("Mon Dec 08 01:01:01 GMT 1980"), dtfmt.parse(mariaBDay), "MAria's Birthday");

      String getBossId =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('MARIA DA SILVA SANTOS'))" +
                      ".out('Is_Alias').out('Is_Subordinate').properties('Person_Employee_ID').value().next().toString()").get().toString();
      assertEquals("5", getBossId, "Maria's Boss (José Dorival) has an Id of 5");

      String getBossName =
              App.executor.eval("App.g.V().has('Object_Identity_Card_Id_Value',eq('12345678901'))" +
                      ".in('Has_Id_Card').out('Is_Alias').out('Is_Subordinate').in('Is_Alias')" +
                      ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("JOSÉ DORIVAL", getBossName, "Maria's Boss' Full Name");


    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

  @Test
  public void test00013TotvsProtheusSa2Fornecedor() throws InterruptedException {

    try {

      jsonTestUtil("totvs/totvs-sa2-real.json", "$.objs", "totvs_protheus_sa2_fornecedor");

      String pabloStreetName =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('PABLO MATO ESCOBAR'))" +
                      ".out('Is_Located').properties('Location_Address_parser_road').value().next().toString()").get().toString();
      assertEquals("[rua moreira da silva sauro, ruamoreiradasilvasauro, ruamoreiradonasilvasauro]",
              pabloStreetName, "Pablo's street name");


      String orgCleusaId =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('ARMS MANUTENCAO E R'))" +
                      ".properties('Person_Organisation_Id').value().next().toString()").get().toString();
      assertEquals("01243568000156", orgCleusaId, "Id/CNPJ do Empreendimento de Cleusa");


//      String orgHQ =
//              App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('ARMS MANUTENCAO E R'))" +
//                      ".out('Is_Located').properties('Location_Address_parser_state').value().next().toString()").get().toString();
//      assertEquals("[pr, parana, paraná]", orgHQ, "Company's State HQ");

//      TODO: investigar melhor o location.parser
//      retorno após mudança de "A2_ESTADO": "PARANÁ" para  "A2_EST": "PR" => [parana, pastor, poligonoresidencial, pr, praca, prairie, prarie, principal]

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @ParameterizedTest(name = "Sharepoint tests ({0}) rule Name {1}, expected Data Source Name = {2} ")
  @CsvSource({
          "sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json,   sharepoint_mapeamentos,   SHAREPOINT/MAPEAMENTO-DE-PROCESSOS,  6,  2",
          "sharepoint/pv-extract-sharepoint-ropa.json,   sharepoint_ropa,   SHAREPOINT/ROPA,    125, 19"
  })
  public void test00014SharepointProcessMapping(String jsonFile, String ruleName, String dataSourceName, String numDataSrcs, String numDataPolicies) throws InterruptedException {
    try {
//      jsonTestUtil("pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
//          "sharepoint_data_sources");

      jsonTestUtil(jsonFile, "$.queryResp[*].fields",
              ruleName);

      String queryPrefix = "App.g.V().has('Object_Data_Source_Name', eq('" + dataSourceName + "'))\n";
//    test0000 for COUNT(dataSources)
      String countDataSources =
              App.executor.eval(queryPrefix +
                      ".count().next().toString()").get().toString();
      assertEquals("1", countDataSources);

//    test0000 for COUNT(event Ingestions)
      String countEventGroupIngestions =
              App.executor.eval(queryPrefix +
                      ".both()\n" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", countEventGroupIngestions);

      String countEventIngestions =
              App.executor.eval(queryPrefix +
                      ".out().out()\n" +
                      ".count().next().toString()").get().toString();
      // expecting 1 less Event_Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals(numDataSrcs, countEventIngestions);


      String countObjectDataProcessesIngested =
              App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
//              ".in('Has_Policy')" +
//              ".has('Metadata_Type_Object_Data_Policy', eq('Object_Data_Policy'))\n" +
                      ".count().next().toString()").get().toString();
      // expecting 1 less Event_Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals(numDataSrcs, countObjectDataProcessesIngested);

      String countDataPolicy =
              App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".out('Has_Data_Source')" +
                      ".has('Metadata_Type_Object_Data_Source', eq('Object_Data_Source')).dedup()" +
                      ".count().next().toString()").get().toString();
      assertEquals(numDataPolicies, countDataPolicy);
    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }


  // A new version of this Test can be found @ PVTotvsTest.java
  @Test
  public void test00015TotvsProtheusRaFuncionario() throws InterruptedException {

    try {

      jsonTestUtil("totvs/totvs-sra-real.json", "$.objs", "totvs_protheus_sra_funcionario");

      String martaEmailsCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARTA MARILIA MARCÔNDES'))" +
                      ".bothE('Uses_Email').count().next().toString()").get().toString();
      assertEquals("2", martaEmailsCount, "2 Uses_Email");


      String locationAddressDescription =
              App.executor.eval("App.g.V().has('Location_Address_Full_Address',eq('RUA SAMPAIO CASA, PONTE, JAGUARÃO - RS, 333333, BRASIL'))" +
                      ".properties('Location_Address_parser_city', 'Location_Address_parser_postcode').value().toList()").get().toString();
      assertEquals("[[casa ponte jaguarão, casapontejaguarao], [333333]]", locationAddressDescription, "Address parser City and Post Code");


      String findingTheSonOfAMother =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('DONA SABRINA')).out('Is_Family')" +
                      ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("MARTA MARILIA MARCÔNDES", findingTheSonOfAMother, "A filha de Dona Sabrina é a Marta");
    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }

  ContainerRequest fakeContainerReqContext;


  @Test
  public void test00016FilesNLP() throws InterruptedException {

    csvTestUtil("phase1.csv", "phase1_csv");
    csvTestUtil("phase1.csv", "phase1_csv");

    jsonTestUtil("pv-extract-file-ingest.json", "$.value", "pv_file");

    try {
      String numDataSources =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name',eq('FILE_SERVER_SRV1')).count().next().toString()")
                      .get().toString();

      assertEquals("1", numDataSources, "Ensure that We only have one data source");

      String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String numDataEventFileGroups =
              App.executor.eval("App.g.V().has('Event_File_Group_Ingestion_Ingestion_Date',eq('" + currDate + "')).count().next().toString()")
                      .get().toString();

      assertEquals("1", numDataEventFileGroups, "Ensure that We only have one Group");


      String numIngestionEvents = App.executor.eval("App.g.V().has('Event_File_Group_Ingestion_Ingestion_Date',eq('" + currDate + "'))" +
                      "out('Has_Ingestion_Event').count().next().toString()")
              .get().toString();

      assertEquals("3", numIngestionEvents, "Ensure that We only have 3 File events");

      String numNLPEvents = App.executor.eval("App.g.V().has('Event_File_Group_Ingestion_Ingestion_Date',eq('" + currDate + "'))" +
                      ".out('Has_Ingestion_Event').out('Has_NLP_Events').count().next().toString()")
              .get().toString();

      assertEquals("2", numNLPEvents,
              "Ensure that We only have 2 NLP events (for John Smith and Mickey Cristino)");

      String numNLPEvents2 = App.executor.eval("App.g.V().has('Event_File_Ingestion_Name',eq('c.out'))" +
                      ".out('Has_NLP_Events').out('Has_NLP_Events').count().next().toString()")
              .get().toString();

      assertEquals("2", numNLPEvents2,
              "Ensure that We only have 2 NLP events (for John Smith and Mickey Cristino)");


      String johnSmithMatchCount = App.executor.eval("App.g.V().has('Event_File_Ingestion_Name',eq('c.out'))" +
                      ".out('Has_NLP_Events').out('Has_NLP_Events').has('Person_Natural_Full_Name', eq('JOHN SMITH'))" +
                      ".count().next().toString()")
              .get().toString();

      assertEquals("1", johnSmithMatchCount,
              "Ensure that We only have 1 NLP match (for John Smith)");

      String mickeyCristinoMatchCount = App.executor.eval("App.g.V().has('Event_File_Ingestion_Name',eq('c.out'))" +
                      ".out('Has_NLP_Events').out('Has_NLP_Events').has('Person_Natural_Full_Name', eq('MICKEY CRISTINO'))" +
                      ".count().next().toString()")
              .get().toString();

      assertEquals("1", mickeyCristinoMatchCount,
              "Ensure that We only have 1 NLP match (for Mickey Cristino)");

      Resource res = new Resource();

      Md2Request md2Request = new Md2Request();
      md2Request.settings.limit = 10L;
      md2Request.settings.start = 0L;
      md2Request.query.name = "John Smith";
      md2Request.query.email = "retoh@optonline.net";
      md2Request.query.docCpf = "4736473678";
      md2Request.query.reqId = 4736473678L;


      Md2Reply md2Reply = res.md2Search(fakeContainerReqContext, md2Request);

      assertEquals(1, md2Reply.total);
      assertEquals(md2Request.query.reqId, md2Reply.reqId);
      assertEquals(1, md2Reply.track.length);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }

  @Test
  public void test00017MD2() throws InterruptedException {

    try {
      jsonTestUtil("md2/md2.json", "$.person", "pv_md2");

      jsonTestUtil("pv-extract-file-ingest-md2.json", "$.value", "pv_file");
      jsonTestUtil("office365/pv-extract-o365-email-md2.json", "$.value", "pv_email");
      jsonTestUtil("office365/pv-extract-o365-email-md2-pt2.json", "$.value", "pv_email");

      String getPersonNaturalFullName =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('CARLOS MAURICIO DIRIZ'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", getPersonNaturalFullName, "um registro em nome de CARLOS MAURICIO DIRIZ");


      Resource res = new Resource();

      Md2Request md2Request = new Md2Request();
      md2Request.settings.limit = 2L;
      md2Request.settings.start = 0L;
      md2Request.query.name = "rose marie humenhuk";
      md2Request.query.email = "MADROSE@bol.com.br";
      md2Request.query.docCpf = "22028544953";

      Md2Reply md2Reply = res.md2Search(fakeContainerReqContext, md2Request);

      assertEquals(4, md2Reply.total);
      assertEquals(2, md2Reply.track.length);

      md2Request.settings.start = 2L;

      md2Reply = res.md2Search(fakeContainerReqContext, md2Request);

      assertEquals(4, md2Reply.total);
      assertEquals(2, md2Reply.track.length);

      md2Request.settings.start = 2L;
      md2Request.query.name = "NAME THAT DOES NOT EXIST";


      md2Reply = res.md2Search(fakeContainerReqContext, md2Request);

      assertEquals(404, md2Reply.getStatus());

      md2Request.query.name = "MARIA APARECIDA KEIKO INABA";
      md2Request.query.docCpf = "06451990876";
      md2Request.query.email = "mkeiko@lykeslines.com.br";
      md2Request.settings.limit = 1000L;
      md2Request.settings.start = 0L;

      md2Reply = res.md2Search(fakeContainerReqContext, md2Request);
      assertEquals(200, md2Reply.getStatus());
      assertEquals(0L, md2Reply.total);

      md2Request.query.name = "MARIA APARECIDA KEIKO INABA";
      md2Request.query.docCpf = "06546543431";
      md2Request.query.email = "mkeiko@lykeslines.com.br";
      md2Request.settings.limit = 1000L;
      md2Request.settings.start = 0L;

      md2Reply = res.md2Search(fakeContainerReqContext, md2Request);
      assertEquals(200, md2Reply.getStatus());
      assertEquals(1L, md2Reply.total);

      md2Request.query.name = "MARIA APARECIDA KEIKO INABA";
      md2Request.query.docCpf = null;
      md2Request.query.email = "mkeiko@lykeslines.com.br";
      md2Request.settings.limit = 1000L;
      md2Request.settings.start = 0L;

      md2Reply = res.md2Search(fakeContainerReqContext, md2Request);
      assertEquals(409, md2Reply.getStatus(), "Found more than one person with the same name");

      md2Request.query.name = "HUGO SANCHEZ CORREIA";
      md2Request.query.docCpf = "219.594.684-99";
      md2Request.query.email = null;
      md2Reply = res.md2Search(fakeContainerReqContext, md2Request);

      assertEquals(200, md2Reply.getStatus(), "Found three masked CPF");
      assertEquals(3L, md2Reply.total);

      md2Request.query.name = "HUGO SANCHEZ CORREIA";
      md2Request.query.docCpf = "21959468499";
      md2Request.query.email = null;
      md2Reply = res.md2Search(fakeContainerReqContext, md2Request);
      assertEquals(200, md2Reply.getStatus(), "Found three unmasked CPF");
      assertEquals(3L, md2Reply.total);

      md2Request.settings.limit = 200L;
      md2Request.settings.start = 0L;
      md2Request.query.name = "RAIMUNDO CESAR FERREIRA DA SILVA";
      md2Request.query.email = null;
      md2Request.query.docCpf = "05596491004";

      md2Reply = res.md2Search(fakeContainerReqContext, md2Request);
      assertEquals(200, md2Reply.getStatus(), "Found two masked CPF entries in a PDF file (email + fs)");
      assertEquals(2L, md2Reply.total, "Found two masked CPF entries in a PDF file (email + fs)");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }


}
