package com.pontusvision.gdpr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orientechnologies.apache.commons.csv.CSVFormat;
import com.orientechnologies.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
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
@TestMethodOrder(MethodOrderer.MethodName.class)
//@RunWith(JUnitPlatform.class)
public class PVTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */


  @Test
  public void test00001Phase1CSVIngestion() throws InterruptedException {
    try {

      csvTestUtil("phase1.csv", "phase1_csv");


      String userId =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('ROZELL COLEMAN')).next().id().toString()")
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

      csvTestUtil("sap-polaris-clientes.csv", "Cliente_SAP_PosVenda_POLARIS");


//    Testing for Person.Natural WITH Tax_Number
      String userId0 =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MATEUS SILVA PINTO'))\n" +
              ".next().id().toString()").get().toString();
      String pintoConnectionsQuery = "App.g.V(\"" + userId0 + "\").bothE().count().next().toString()";
      String pintoConnections = App.executor.eval(pintoConnectionsQuery)
          .get().toString();
      assertEquals("5", pintoConnections); // 2 phone numbers + 1 identity card + 1 location + 1 ingestion event

//    Testing for Person.Natural withOUT Tax_Number
      String munirConnections =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MUNIR HANDAUS'))\n" +
              ".bothE().count().next().toString()").get().toString();
//      String munirConnectionsQuery = "App.g.V(\"" + userId1 +"\").bothE().count().next().toString()";
//      String munirConnections = App.executor.eval(munirConnectionsQuery).get().toString();
      assertEquals("3", munirConnections); // 1 phone number + 0 identity card (sem tax) + 1 location + 1 ingestion event

      String personNaturalTypes =
          App.executor.eval("App.g.V().has('Person.Natural.Type', eq('Clientes Sem Tax'))" +
              ".count().next().toString()").get().toString();
      assertEquals("5", personNaturalTypes);

      System.out.println("*******************************************************\n" + personNaturalTypes);


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00003SharepointTreinamentos() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-treinamentos.json", "$.queryResp[*].fields",
          "sharepoint_treinamentos");

//    test0000 for PEDRO Person.Employee NODE
      String userId =
          App.executor.eval("App.g.V().has('Person.Employee.Full_Name', eq('PEDRO ALVARO CABRAL'))\n" +
              ".next().id().toString()").get().toString();
      String pedroConnectionsQuery = "App.g.V(\"" + userId + "\").bothE().count().next().toString()";
      String pedroConnections = App.executor.eval(pedroConnectionsQuery).get().toString();
      assertEquals(pedroConnections, "1");

//    test0000 for MARCELA Person.Employee NODE
      String userId1 =
          App.executor.eval("App.g.V().has('Person.Employee.Full_Name', eq('MARCELA ALVES'))\n" +
              ".next().id().toString()").get().toString();

//    test0000 for JOSE Person.Employee NODE
      String userId2 =
          App.executor.eval("App.g.V().has('Person.Employee.Full_Name', eq('JOSE CARLOS MONSANTO'))\n" +
              ".next().id().toString()").get().toString();

//    test0000 for Object.Data_Source.Name
      String sharepointDataSourceId =
          App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('sharepoint/treinamentos'))" +
              ".next().id().toString()").get().toString();
//      assertEquals(userId3,"#82:0"); -- Id can change!!
      String rootConnectionsQuery = "App.g.V(\"" + sharepointDataSourceId + "\").bothE().count().next().toString()";
      String rootConnections = App.executor.eval(rootConnectionsQuery).get().toString();
      assertEquals("1", rootConnections);

//    test0000 for Object.Awareness_Campaign.Description
      String userId4 =
          App.executor.eval("App.g.V().has('Object.Awareness_Campaign.Description', " +
              "eq('1º CURSO - LGPD - LEI GERAL DE PROTEÇÃO DE DADOS')).next().id().toString()").get().toString();
      String curso1ConnectionsQuery = "App.g.V(\"" + userId4 + "\").bothE().count().next().toString()";
      String curso1Connections = App.executor.eval(curso1ConnectionsQuery).get().toString();
      assertEquals("1",curso1Connections );

//    test0000 for Object.Awareness_Campaign.Description 2
      String curso2Connections =
          App.executor.eval("App.g.V().has('Object.Awareness_Campaign.Description', " +
              "eq('BASES LEGAIS LGPD')).bothE().count().next().toString()").get().toString();
//      String curso2ConnectionsQuery = "App.g.V(\"" + userId5 + "\").bothE().count().next().toString()";
//      String curso2Connections = App.executor.eval(curso2ConnectionsQuery).get().toString();
      assertEquals("2",curso2Connections );

//    test0000 for Object.Awareness_Campaign.Description 3
      String userId6 =
          App.executor.eval("App.g.V().has('Object.Awareness_Campaign.Description', " +
              "eq('LGPD - MULTAS E SANÇÕES')).next().id().toString()").get().toString();
      String curso3ConnectionsQuery = "App.g.V(\"" + userId6 + "\").bothE().count().next().toString()";
      String curso3Connections = App.executor.eval(curso3ConnectionsQuery).get().toString();
      assertEquals("1",curso3Connections);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @ParameterizedTest(name = "Sharepoint tests ({0}) rule Name {1}, expected Data Source Name = {2} ")
  @CsvSource({
      "pv-extract-sharepoint-data-sources.json,   sharepoint_data_sources,   sharepoint/data-sources, 7",
      "pv-extract-sharepoint-fontes-de-dados.json,   sharepoint_fontes_de_dados,   sharepoint/fontes-de-dados, 9"
  })
  public void test00004SharepointDataSources(String jsonFile, String ruleName, String dataSourceName,
                                             String expectedDataPolicyCount) throws InterruptedException {
    try {
//      jsonTestUtil("pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
//          "sharepoint_data_sources");

      jsonTestUtil(jsonFile, "$.queryResp[*].fields",
          ruleName);

      String queryPrefix = "App.g.V().has('Object.Data_Source.Name', eq('" + dataSourceName + "'))\n";
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
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("1", countEventGroupIngestions);

      String countEventIngestions =
          App.executor.eval(queryPrefix +
              ".out().out()\n" +
              ".count().next().toString()").get().toString();
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("7", countEventIngestions);


      String countObjectDataSourcesIngested =
          App.executor.eval(queryPrefix +
              ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
//              ".in('Has_Policy')" +
//              ".has('Metadata.Type.Object.Data_Policy', eq('Object.Data_Policy'))\n" +
              ".count().next().toString()").get().toString();
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("7", countObjectDataSourcesIngested);

      String countDataPolicy =
          App.executor.eval(queryPrefix +
              ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
              ".out('Has_Policy')" +
              ".has('Metadata.Type.Object.Data_Policy', eq('Object.Data_Policy'))" +
              ".count().next().toString()").get().toString();
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals(expectedDataPolicyCount, countDataPolicy);

      if ("sharepoint_fontes_de_dados".equals(ruleName)){

        String numSensitiveData = App.executor.eval(queryPrefix +
            ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
            ".out('Has_Sensitive_Data')" +
            ".has('Metadata.Type.Object.Sensitive_Data', eq('Object.Sensitive_Data')).id().toSet()" +
            ".size().toString()").get().toString();
        assertEquals("15", numSensitiveData);

        String numTelefones = App.executor.eval("App.g.V().has('Object.Sensitive_Data.Description', eq('TELEFONE'))\n" +
            ".bothE().count().next().toString()").get().toString();
        assertEquals("2", numTelefones);
        String numNomes = App.executor.eval("App.g.V().has('Object.Sensitive_Data.Description', eq('NOME'))\n" +
            ".bothE().count().next().toString()").get().toString();
        assertEquals("5", numNomes);


      }

//      String userId =
//              App.executor.eval("App.g.V().has('Object.Sensitive_Data.Description', eq('TELEFONE'))\n" +
//                      ".next().id().toString()").get().toString();
//      String sensitiveDataConnectionsQuery = "App.g.V(\"" + userId + "\").bothE().count().next().toString()";
//      String sensitiveDataConnections = App.executor.eval(sensitiveDataConnectionsQuery).get().toString();
//      assertEquals("1", sensitiveDataConnections);
//
//      String userId1 =
//              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('CRM-Contatos'))\n" +
//                      ".next().id().toString()").get().toString();
//
//      String userId2 =
//              App.executor.eval("App.g.V().has('Object.Data_Source.Domain', eq('Users'))\n" +
//                      ".next().id().toString()").get().toString();


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00005TotvsPloomes() throws InterruptedException {
    try {

      jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");
      jsonTestUtil("totvs1.json", "$.objs", "totvs_protheus_sa1_clientes");

//    test0000 for COMIDAS 1 as Person.Natural
      String userId0 =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('COMIDAS 1'))\n" +
              ".next().id().toString()").get().toString();

//      test0000 for COMIDAS 1 as Person.Organisation
//      String userId1 =
//              App.executor.eval("App.g.V().has('Person.Organisation.Short_Name', eq('COMIDAS 1'))\n" +
//                      ".next().id().toString()").get().toString();

//    test0000 for Object.Data_Source.Name
      String userId2 =
          App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('totvs/protheus/sa1_clientes'))" +
              ".next().id().toString()").get().toString();
      String rootConnectionsQuery = "App.g.V(\"" + userId2 + "\").bothE().count().next().toString()";
      String rootConnections = App.executor.eval(rootConnectionsQuery).get().toString();
//      assertEquals(rootConnections,"5"); -- vertices podem variar dependendo do timing!

//    test0000 COUNT(Edges) for COMIDAS 2
      String userId3 =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('COMIDAS 2'))" +
              ".next().id().toString()").get().toString();
      String comidas2ConnectionsQuery = "App.g.V(\"" + userId3 + "\").bothE().count().next().toString()";
      String comidas2Connections = App.executor.eval(comidas2ConnectionsQuery).get().toString();
      assertEquals("4", comidas2Connections);

//    test0000 COUNT(Edges) for Object.Email_Address
      String userId4 =
          App.executor.eval("App.g.V().has('Object.Email_Address.Email',eq('jonas@comida1.com.br'))" +
              ".next().id().toString()").get().toString();
      String emailConnectionsQuery = "App.g.V(\"" + userId4 + "\").bothE().count().next().toString()";
      String emailConnections = App.executor.eval(emailConnectionsQuery).get().toString();
      assertEquals(emailConnections, "1");

//    test0000 COUNT(Edges) for Object.Phone_Number
      String userId5 =
          App.executor.eval("App.g.V().has('Object.Phone_Number.Numbers_Only',eq('111111111'))" +
              ".next().id().toString()").get().toString();
      String phoneConnectionsQuery = "App.g.V(\"" + userId5 + "\").bothE().count().next().toString()";
      String phoneConnections = App.executor.eval(phoneConnectionsQuery).get().toString();
      assertEquals(phoneConnections, "0");


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
    String expectedSql = "SELECT @rid as id,`Event.Data_Breach.Description`,`Event.Data_Breach.Impact`  FROM (SELECT EXPAND(OUT('has_server')) FROM #-1:-1  WHERE (( `Event.Data_Breach.Impact`  containsText  'OS' ) ) ) ORDER BY `Event.Data_Breach.Impact` DESC SKIP 0 LIMIT 100";
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
        "`  WHERE (( `Event.Data_Breach.Impact`  containsText  'OS' ) ) ";
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

      String numJohnSmiths = App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('JOHN SMITH')).id()" +
              ".count().next().toString()")
          .get().toString();

      assertEquals("1", numJohnSmiths, "only one John smith after multiple trials");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00009TotvsProtheusSa1Clientes() throws InterruptedException {

    jsonTestUtil("totvs1.json", "$.objs", "totvs_protheus_sa1_clientes");
//    jsonTestUtil("totvs2.json", "$.objs", "totvs_protheus_sa1_clientes");

    try {
      String userId =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('JONAS LEO BATISTA')).next().id().toString()")
              .get().toString();

      Resource res = new Resource();
      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
          "App.g.V().has('Person.Natural.Full_Name',eq('JONAS LEO BATISTA')).next().id().toString()");
      JsonObject obj = JsonParser.parseString(res.gremlinQuery(gson.toJson(gremlinReq))).getAsJsonObject();
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

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00010PloomesClientes() throws InterruptedException {

    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");
//    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");

    try {
      String userId =
          App.executor.eval("App.g.V().has('Person.Organisation.Short_Name',eq('Pessoa Nova5')).next().id().toString()")
              .get().toString();

      Resource res = new Resource();
      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
          "App.g.V().has('Person.Organisation.Short_Name',eq('Pessoa Nova5')).next().id().toString()");
      JsonObject obj = JsonParser.parseString(res.gremlinQuery(gson.toJson(gremlinReq))).getAsJsonObject();
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
    jsonTestUtil("pv-extract-o365-email.json", "$.value", "pv_email");

    try {
      String numDataSources =
          App.executor.eval("App.g.V().has('Object.Data_Source.Name',eq('Office365/email')).count().next().toString()")
              .get().toString();

      assertEquals("1", numDataSources, "Ensure that We only have one data source");

      String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String numDataEventEmailMessageGroups =
          App.executor.eval("App.g.V().has('Event.Email_Msg_Group.Ingestion_Date',eq('" + currDate + "')).count().next().toString()")
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
      csvTestUtil("ADP.csv", "ADP");

      String adpDsqueryPrefix = "App.g.V().has('Object.Data_Source.Name', eq('ADP'))\n";

      String countDataSources =
          App.executor.eval(adpDsqueryPrefix +
              ".count().next().toString()").get().toString();
      assertEquals("1", countDataSources, "Expect 1 Data Source (ADP)");


      String adpDsqueryPrefix2 = adpDsqueryPrefix + ".out('Has_Ingestion_Event')";

      String countDataEventGroups =
          App.executor.eval(adpDsqueryPrefix2 +
              ".count().next().toString()").get().toString();
      assertEquals("1", countDataEventGroups, "Expect 1 Ingestion Data Group ");


      String adpDsqueryPrefix3 = adpDsqueryPrefix2 + ".out('Has_Ingestion_Event')";

      String countDataEvents =
          App.executor.eval(adpDsqueryPrefix3 +
              ".count().next().toString()").get().toString();
      assertEquals("7", countDataEvents, "Expect 7 Ingestion Events ");

      String adpDsqueryPrefix4 = adpDsqueryPrefix3 +
          ".in('Has_Ingestion_Event').has('Metadata.Type.Person.Natural', eq('Person.Natural'))";

      String countPersonObjects =
          App.executor.eval(adpDsqueryPrefix4 +
              ".count().next().toString()").get().toString();
      assertEquals("7", countPersonObjects, "Expect 7 Person.Natural entries ");

      String externalAddressCount = App.executor.eval("App.g.V()" +
          ".has('Person.Natural.Full_Name', eq('IAN GAEL FERREIRA')).out('Is_Located')" +
          ".has('Location.Address.Type', eq('Endereço Exterior'))" +
          ".count().next().toString()").get().toString();
      assertEquals("1", externalAddressCount, "Expect IAN GAEL FERREIRA to have 1 Endereço Exterior ");

      String coordenadorCPF = App.executor.eval("App.g.V()" +
          ".has('Person.Natural.Full_Name', eq('JOÃOZINHO')).has('Person.Natural.Customer_ID', eq('43376845409'))" +
          ".out('Is_Subordinate')" +
          ".out('Has_Id_Card')" +
//          ".count().next().toString()").get().toString();

          ".properties('Object.Identity_Card.Id_Value').value()" +
          ".next().toString()").get().toString();
      assertEquals("07856755466", coordenadorCPF, "CPF From Joaozinho's coordinator ");


//
//      String countCommercialPhoneNumbers =
//          App.executor.eval("App.g.V().has('Object.Phone_Number.Type', eq('Telefone Comercial'))" +
//              ".count().next().toString()").get().toString();
//      assertEquals("1", countCommercialPhoneNumbers);
//
//      String countForeignLocationAddress =
//          App.executor.eval("App.g.V().has('Location.Address.Type', eq('Endereço Exterior'))" +
//              ".count().next().toString()").get().toString();
//      assertEquals("1", countForeignLocationAddress);
//
//      App.executor.eval("App.g.V().values('Object.Identity_Card.Id_Value').next().toString()");
//
//      App.executor.eval("App.g.V().values('Person.Employee.Role').count().next().toString()");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00013TotvsProtheusSa2Fornecedores() throws InterruptedException {

    try {

      jsonTestUtil("totvs2.json", "$.objs", "totvs_protheus_sa2_fornecedor");

      String personNaturalEdgesCount =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('PABLO MATO ESCOBAR'))" +
              ".bothE().count().next().toString()").get().toString();
      assertEquals("6", personNaturalEdgesCount, "2 Object.Identity_Card + 1 Object.Email_Address " +
          "+ 1 Object.Phone_Number + 1 Location.Address + 1 Event.Ingestion");


      String orgName =
          App.executor.eval("App.g.V().has('Person.Organisation.Short_Name',eq('CLEUSA PAIVA DIA'))" +
              ".properties('Person.Organisation.Name').value().next().toString()").get().toString();
      assertEquals("ARMS MANUTENCAO E R", orgName, "O Empreendimento de Cleusa se chama Arms Manutenção e R(eparos)");


      String personOrgEdgesCount =
          App.executor.eval("App.g.V().has('Person.Organisation.Name', eq('ARMS MANUTENCAO E R'))" +
              ".bothE().count().next().toString()").get().toString();
      assertEquals("3", personOrgEdgesCount, "1 Object.Identity_Card + 1 Location.Address + 1 Event.Ingestion");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @ParameterizedTest(name = "Sharepoint tests ({0}) rule Name {1}, expected Data Source Name = {2} ")
  @CsvSource({
      "pv-extract-sharepoint-mapeamento-de-processo.json,   sharepoint_mapeamentos,   sharepoint/mapeamento-de-processos",
  })
  public void test00014SharepointProcessMapping(String jsonFile, String ruleName, String dataSourceName) throws InterruptedException {
    try {
//      jsonTestUtil("pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
//          "sharepoint_data_sources");

      jsonTestUtil(jsonFile, "$.queryResp[*].fields",
          ruleName);

      String queryPrefix = "App.g.V().has('Object.Data_Source.Name', eq('" + dataSourceName + "'))\n";
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
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("6", countEventIngestions);


      String countObjectDataProcessesIngested =
          App.executor.eval(queryPrefix +
              ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
//              ".in('Has_Policy')" +
//              ".has('Metadata.Type.Object.Data_Policy', eq('Object.Data_Policy'))\n" +
              ".count().next().toString()").get().toString();
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("6", countObjectDataProcessesIngested);

      String countDataPolicy =
          App.executor.eval(queryPrefix +
              ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
              ".out('Has_Data_Source')" +
              ".has('Metadata.Type.Object.Data_Source', eq('Object.Data_Source'))" +
              ".count().next().toString()").get().toString();
      // expecting 1 less Event.Ingestion because "sharepoint" is the Data Source for the Data Sources
      assertEquals("12", countDataPolicy);
    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }

  @Test
  public void test00015TotvsProtheusRaFuncionarios() throws InterruptedException {

    try {

      jsonTestUtil("totvs-ra.json", "$.objs", "totvs_protheus_ra_funcionario");

      String personNaturalEdgesCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MARTA MARILIA MARCÔNDES'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("6", personNaturalEdgesCount, "2 Uses_Email + 1 Lives + 2 Is_Family + 1 Has_Ingestion_Event");


      String locationAddressDescription =
              App.executor.eval("App.g.V().has('Location.Address.Full_Address',eq('RUA SAMPAIO CASA Ponte, Jaguarão - RS, 333333'))" +
                      ".properties('Location.Address.Description').value().next().toString()").get().toString();
      assertEquals("moradia principal", locationAddressDescription, "Descrição do Endereço");


      String findingTheSonOfAMother =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('DONA SABRINA')).out('Is_Family')" +
                      ".properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("MARTA MARILIA MARCÔNDES", findingTheSonOfAMother, "A filha de Dona Sabrina é a Marta" +
              "+ 1 Object.Phone_Number + 1 Location.Address + 1 Event.Ingestion");
    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }

  @Test
  public void test00016SapCapLeads() throws InterruptedException {

    try {

      csvTestUtil("/sap-cap/leads.csv", "cap_leads");

      String personNaturalEdgesCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('IGOR FERREIRA'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("7", personNaturalEdgesCount, "2 Has_Phone + 1 Event.Ingestion + 1 Works " +
              "+ 1 Is_Located + 1 Uses_Email + 1 Is_Lead");


      String leadId =
              App.executor.eval("App.g.V().has('Location.Address.Full_Address'," +
                      "eq('av. marcio gomes 333 , AA3, Belo Horizonte - Brasil, 6758090')).in('Is_Located')" +
                      ".properties('Person.Natural.Customer_ID').value().next().toString()").get().toString();
      assertEquals("1", leadId, "Lead ID de Igor Ferreira");


      String gettingEmailAddress =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('LARA MAGALHAES')).out('Uses_Email')" +
                      ".properties('Object.Email_Address.Email').value().next().toString()").get().toString();
      assertEquals("lara@yahoo.com", gettingEmailAddress, "E-mail de Lara Magalhaes");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00017FilesNLP() throws InterruptedException {

    csvTestUtil("phase1.csv", "phase1_csv");
    csvTestUtil("phase1.csv", "phase1_csv");

    jsonTestUtil("pv-extract-file-ingest.json", "$.value", "pv_file");

    try {
      String numDataSources =
          App.executor.eval("App.g.V().has('Object.Data_Source.Name',eq('file_server_srv1')).count().next().toString()")
              .get().toString();

      assertEquals("1", numDataSources, "Ensure that We only have one data source");

      String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String numDataEventFileGroups =
          App.executor.eval("App.g.V().has('Event.File_Group_Ingestion.Ingestion_Date',eq('" + currDate + "')).count().next().toString()")
              .get().toString();

      assertEquals("1", numDataEventFileGroups, "Ensure that We only have one Group");


      String numIngestionEvents = App.executor.eval("App.g.V().has('Event.File_Group_Ingestion.Ingestion_Date',eq('" + currDate + "'))" +
              "out('Has_Ingestion_Event').count().next().toString()")
          .get().toString();

      assertEquals("3", numIngestionEvents, "Ensure that We only have 3 File events");

      String numNLPEvents = App.executor.eval("App.g.V().has('Event.File_Group_Ingestion.Ingestion_Date',eq('" + currDate + "'))" +
              ".out('Has_Ingestion_Event').out('Has_NLP_Events').count().next().toString()")
          .get().toString();

      assertEquals("2", numNLPEvents,
          "Ensure that We only have 2 NLP events (for John Smith and Mickey Cristino)");

      String numNLPEvents2 = App.executor.eval("App.g.V().has('Event.File_Ingestion.Name',eq('c.out'))" +
              ".out('Has_NLP_Events').out('Has_NLP_Events').count().next().toString()")
          .get().toString();

      assertEquals("2", numNLPEvents2,
          "Ensure that We only have 2 NLP events (for John Smith and Mickey Cristino)");


      String johnSmithMatchCount = App.executor.eval("App.g.V().has('Event.File_Ingestion.Name',eq('c.out'))" +
              ".out('Has_NLP_Events').out('Has_NLP_Events').has('Person.Natural.Full_Name', eq('JOHN SMITH'))" +
              ".count().next().toString()")
          .get().toString();

      assertEquals("1", johnSmithMatchCount,
          "Ensure that We only have 1 NLP match (for John Smith)");

      String mickeyCristinoMatchCount = App.executor.eval("App.g.V().has('Event.File_Ingestion.Name',eq('c.out'))" +
              ".out('Has_NLP_Events').out('Has_NLP_Events').has('Person.Natural.Full_Name', eq('MICKEY CRISTINO'))" +
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

      Md2Reply md2Reply = res.md2Search(md2Request);

      assertEquals(1, md2Reply.total);
      assertEquals(1, md2Reply.track.length);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }

  @Test
  public void test00018MD2() throws InterruptedException {

    try {
      jsonTestUtil("md2.json", "$.person", "pv_md2");

      jsonTestUtil("pv-extract-file-ingest-md2.json", "$.value", "pv_file");
      jsonTestUtil("pv-extract-o365-email-md2.json", "$.value", "pv_email");

      String getPersonNaturalFullName =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('CARLOS MAURICIO DIRIZ'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", getPersonNaturalFullName, "um registro em nome de CARLOS MAURICIO DIRIZ");


      Resource res = new Resource();

      Md2Request md2Request = new Md2Request();
      md2Request.settings.limit = 2L;
      md2Request.settings.start = 0L;
      md2Request.query.name = "rose marie humenhuk";
      md2Request.query.email = "MADROSE@bol.com.br";
      md2Request.query.docCpf = "22028544953";

      Md2Reply md2Reply = res.md2Search(md2Request);

      assertEquals(3, md2Reply.total);
      assertEquals(2, md2Reply.track.length);

      md2Request.settings.start = 2L;

      md2Reply = res.md2Search(md2Request);

      assertEquals(3, md2Reply.total);
      assertEquals(1, md2Reply.track.length);




    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }



  }

}
