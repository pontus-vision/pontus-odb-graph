package com.pontusvision.gdpr;

import com.pontusvision.ingestion.Ingestion;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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
public class PVSharepointRiskTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001RiskMitigation() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-risk-mitigations.json", "$.queryResp[*].fields",
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
      assertEquals("1", curso1Connections);

//    test0000 for Object.Awareness_Campaign.Description 2
      String curso2Connections =
          App.executor.eval("App.g.V().has('Object.Awareness_Campaign.Description', " +
              "eq('BASES LEGAIS LGPD')).bothE().count().next().toString()").get().toString();
//      String curso2ConnectionsQuery = "App.g.V(\"" + userId5 + "\").bothE().count().next().toString()";
//      String curso2Connections = App.executor.eval(curso2ConnectionsQuery).get().toString();
      assertEquals("2", curso2Connections);

//    test0000 for Object.Awareness_Campaign.Description 3
      String userId6 =
          App.executor.eval("App.g.V().has('Object.Awareness_Campaign.Description', " +
              "eq('LGPD - MULTAS E SANÇÕES')).next().id().toString()").get().toString();
      String curso3ConnectionsQuery = "App.g.V(\"" + userId6 + "\").bothE().count().next().toString()";
      String curso3Connections = App.executor.eval(curso3ConnectionsQuery).get().toString();
      assertEquals("1", curso3Connections);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }


}
