package com.pontusvision.gdpr;

import com.pontusvision.graphutils.VisJSGraph;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.*;
import java.util.stream.Collectors;

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

//      infraGraphJson =
//        {
//          "nodes": [
//            {
//              "id":"#190:0",
//                    "group":"Object.Module",
//                    "label":"lyyyy",
//                    "shape":"image",
//                    "image":
//              "eyJNZXRhZGF0YS5UeXBlIjpbIk9iamVjdC5Nb2R1bGUiXSwiTWV0YWRhdGEuVHlwZS5PYmplY3QuTW9kdWxlIjpbIk9iamVjdC5Nb2R1bGUiXSwiT2JqZWN0Lk1vZHVsZS5OYW1lIjpbImx5eXl5Il19"
//            },
//            {
//              "id":"#142:0",
//                    "group":"Object.Data_Source",
//                    "label":"SISTEMA DE CRM PARA CAPTURA DE LYYYY",
//                    "shape":"image",
//                    "image":
//              "eyJPYmplY3QuRGF0YV9Tb3VyY2UuVHlwZSI6WyJTYWFTIl0sIk9iamVjdC5EYXRhX1NvdXJjZS5VUklfUmVhZCI6WyJodHRwczovL2FwcC54eHh4eC5jb20vIl0sIk9iamVjdC5EYXRhX1NvdXJjZS5EZXNjcmlwdGlvbiI6WyJTSVNURU1BIERFIENSTSBQQVJBIENBUFRVUkEgREUgTFlZWVkiXSwiT2JqZWN0LkRhdGFfU291cmNlLkZvcm1fSWQiOlsiMSJdLCJPYmplY3QuRGF0YV9Tb3VyY2UuVVJJX1VwZGF0ZSI6WyJodHRwczovL2FwcC54eHh4eC5jb20vIl0sIk1ldGFkYXRhLlR5cGUuT2JqZWN0LkRhdGFfU291cmNlIjpbIk9iamVjdC5EYXRhX1NvdXJjZSJdLCJNZXRhZGF0YS5UeXBlIjpbIk9iamVjdC5EYXRhX1NvdXJjZSJdLCJPYmplY3QuRGF0YV9Tb3VyY2UuVVJJX0RlbGV0ZSI6WyJodHRwczovL2FwcC54eHh4eC5jb20vIl0sIk9iamVjdC5EYXRhX1NvdXJjZS5OYW1lIjpbIlNJU1RFTUEgREUgQ1JNIFBBUkEgQ0FQVFVSQSBERSBMWVlZWSJdLCJPYmplY3QuRGF0YV9Tb3VyY2UuRG9tYWluIjpbIkx5eXl5Il0sIk9iamVjdC5EYXRhX1NvdXJjZS5FbmdpbmUiOlsieHh4eHgiXX0="
//            },
//            {
//              "id":"#238:0",
//                    "group":"Object.System",
//                    "label":"XXXXX",
//                    "shape":"image",
//                    "image":
//              "eyJNZXRhZGF0YS5UeXBlIjpbIk9iamVjdC5TeXN0ZW0iXSwiT2JqZWN0LlN5c3RlbS5OYW1lIjpbIlhYWFhYIl0sIk1ldGFkYXRhLlR5cGUuT2JqZWN0LlN5c3RlbSI6WyJPYmplY3QuU3lzdGVtIl19"
//            },
//            {
//              "id":"#234:0",
//                    "group":"Object.Subsystem",
//                    "label":"SAAS",
//                    "shape":"image",
//                    "image":
//              "eyJNZXRhZGF0YS5UeXBlIjpbIk9iamVjdC5TdWJzeXN0ZW0iXSwiTWV0YWRhdGEuVHlwZS5PYmplY3QuU3Vic3lzdGVtIjpbIk9iamVjdC5TdWJzeXN0ZW0iXSwiT2JqZWN0LlN1YnN5c3RlbS5OYW1lIjpbIlNBQVMiXX0="
//            }
//            ],
//          "edges": [
//            {
//              "from":"#234:0",
//                    "to":"#190:0",
//                    "label":"Has Subsystem"
//            },
//            {
//              "from":"#190:0",
//                    "to":"#142:0",
//                    "label":"Has Module"
//            },
//            {
//              "from":"#238:0",
//                    "to":"#234:0",
//                    "label":"Has System"
//            }
//            ]
//        }

      // Object.Module--->Has Module--->Object.DataSource
      // Object.Subsystem--->Has Subsystem--->Object.Module
      // Object.System--->Has System--->Object.Subsystem

      JSONObject obj = new JSONObject(infraGraphJson);
      Set<String> resp = new HashSet<>();
      JSONArray array = obj.getJSONArray("nodes");

      Map<String, String> vertMap = new HashMap<>();
      for(int i = 0 ; i < array.length() ; i++){
        JSONObject entry = array.getJSONObject(i);
        String group = entry.getString("group");
        resp.add(group);
        vertMap.put(entry.getString("id"),group);
      }

      String vertices = resp.stream().sorted().collect(Collectors.toList()).toString();

      assertEquals("[Object.Data_Source, Object.Module, Object.Subsystem, Object.System]",
              vertices, "Vertices to be drawn on the Dashboard");

      array = obj.getJSONArray("edges");
//      resp = new HashSet<>();
      for(int i = 0 ; i < array.length() ; i++){
        JSONObject edge = array.getJSONObject(i);
        String label = edge.getString("label");
//        resp.add(label);
        StringBuilder sb  = new StringBuilder();
        String from = edge.getString("from");
        String to = edge.getString("to");
        sb.append(from).append("--->").append(label).append("--->").append(to);
        resp.add(sb.toString());
      }

      String edges = resp.stream().sorted().collect(Collectors.toList()).toString();

      assertEquals("[Object.Module--->Has Module--->Object.DataSource, \n" +
              "Object.Subsystem--->Has Subsystem--->Object.Module, \n" +
              "Object.System--->Has System--->Object.Subsystem]",
              edges, "Edges to be drawn on the Dashboard");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
