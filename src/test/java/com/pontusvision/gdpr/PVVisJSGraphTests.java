package com.pontusvision.gdpr;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pontusvision.graphutils.VisJSGraph;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(12)
//@RunWith(JUnitPlatform.class)
public class PVVisJSGraphTests extends AppTest {

  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001InfraGraph() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields",
              "sharepoint_fontes_de_dados");

      jsonTestUtil("pv-extract-sharepoint-incidentes-de-seguranca-reportados.json",
              "$.queryResp[*].fields", "sharepoint_data_breaches");

      String vId =
              App.executor.eval("App.g.V().has('Event.Data_Breach.Form_Id', eq('1'))" +
                      ".id().next().toString()").get().toString();

      String infraGraphJson = (String) VisJSGraph.getInfraGraph(vId);
      JSONObject obj = new JSONObject(infraGraphJson);
      String resp = "| ";
      JSONArray array = obj.getJSONArray("nodes");

      for(int i = 0 ; i < array.length() ; i++){
        resp += array.getJSONObject(i).getString("group");
        resp += " | ";
      }

      assertEquals("| Object.Module | Object.Data_Source | Object.System | Object.Subsystem |",
              resp.trim(), "non-parsed string escaped json");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
