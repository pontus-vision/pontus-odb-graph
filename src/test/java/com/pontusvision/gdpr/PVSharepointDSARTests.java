package com.pontusvision.gdpr;

import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;
import java.util.Date;

import static com.pontusvision.graphutils.gdpr.DSARStats.getDSARStatsPerOrganisation;
import static com.pontusvision.graphutils.gdpr.DSARStats.getDSARStatsPerRequestType;
import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(9)

public class PVSharepointDSARTests extends AppTest {

  @Test
  public void test00001SharepointDSAR() throws InterruptedException {

    jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
            "$.queryResp[*].fields", "sharepoint_mapeamentos");

    jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar.json", "$.queryResp[*].fields", "sharepoint_dsar");

    try {

      String palmitosSARType =
              App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('PALMITOS SA'))" +
                      ".out('Made_SAR_Request').properties('Event_Subject_Access_Request_Description')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Exclusão de e-mail", palmitosSARType, "DSAR Request type made by Palmitos SA");

      String palmitosSARCreateDate =
              App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('PALMITOS SA'))" +
                      ".out('Made_SAR_Request').properties('Event_Subject_Access_Request_Metadata_Create_Date')" +
                      ".value().next().toString()").get().toString();
//      palmitosSARCreateDate = palmitosSARCreateDate.replaceAll("... 2021", "GMT 2021");
      assertEquals(dtfmt.parse("Tue May 18 12:01:00 GMT 2021"), dtfmt.parse(palmitosSARCreateDate),
              "DSAR Request date - Palmitos SA");

      String completedDSARCount =
              App.executor.eval("App.g.V().has('Event_Subject_Access_Request_Status', eq('Completed'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", completedDSARCount, "Only 1 DSAR has being Completed!");

      String emailChannelDSARCount =
              App.executor.eval("App.g.V().has('Event_Subject_Access_Request_Request_Channel', " +
                      "eq('Via e-mail')).count().next().toString()").get().toString();
      assertEquals("2", emailChannelDSARCount, "Two DSARs where reaceived via e-mail");

      String angeleBxlDSARType =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('ANGÈLE BRUXÈLE'))" +
                      ".out('Made_SAR_Request').properties('Event_Subject_Access_Request_Description')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Atualização de Endereço", angeleBxlDSARType,
              "Angèle Bruxèle wants to update her Address");

      String miltonOrgDSARStatus =
              App.executor.eval("App.g.V().has('Person_Organisation_Registration_Number', " +
                      "eq('45232190000112')).out('Made_SAR_Request')" +
                      ".properties('Event_Subject_Access_Request_Status').value().next().toString()").get().toString();
      assertEquals("Denied", miltonOrgDSARStatus, "Milton's Company's DSAR Request was Denied!");


//      String DSARTotalCount =
//              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARGORE PROXANO'))" +
//                      ".out('Made_SAR_Request').as('DSAR').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
//                      ".in('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event').dedup()" +
//                      ".count().next().toString()").get().toString();
//      assertEquals("4", DSARTotalCount, "Total count of DSARs: 4");

      String fromDsarToRopa =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARGORE PROXANO'))" +
                      ".out('Made_SAR_Request').as('DSAR').out('Has_DSAR').as('DSAR_Group').out('Has_DSAR')" +
                      ".as('RoPA').has('Object_Data_Procedures_Form_Id', eq('401'))" +
                      ".values('Object_Data_Procedures_Description')" +
                      ".next().toString()").get().toString();
      assertEquals("Recebimento dos dados via e-mail e envio ao departamentos: Jxxxyyy e Fzzzzzzzzzz.",
              fromDsarToRopa, "RoPA's Data Procedures' Description");

      String fromDsarToRopa2 =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('ANGÈLE BRUXÈLE'))" +
                              ".out('Made_SAR_Request').as('DSAR').out('Has_DSAR').as('DSAR_Group').out('Has_DSAR')" +
                              ".as('RoPA').has('Object_Data_Procedures_Form_Id', eq('402'))" +
                              ".values('Object_Data_Procedures_Interested_Parties_Consulted').next().toString()")
                      .get().toString();
      assertEquals("Jurídico Snowymountain", fromDsarToRopa2,"RoPA's Interested Parties");

      // testing for DSAR Request_Type in SQL
      OGremlinResultSet resSet = App.graph.executeSql(
        "SELECT Event_Subject_Access_Request_Request_Type as type " +
          "FROM Event_Subject_Access_Request " +
          "WHERE Event_Subject_Access_Request_Form_Id = 4", Collections.EMPTY_MAP);

      String dsarType = resSet.iterator().next().getRawResult().getProperty("type");
      resSet.close();
      assertEquals("Read", dsarType, "DSAR Type is of Read");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }


  @Test
  public void test00002DSARStats() throws InterruptedException {

    jsonTestUtil("sharepoint/pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields",
            "sharepoint_fontes_de_dados");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
            "$.queryResp[*].fields", "sharepoint_mapeamentos");
    jsonTestUtil("totvs/totvs-sa1.json", "$.objs", "totvs_protheus_sa1_clientes");
    jsonTestUtil("totvs/totvs-sa2-real.json", "$.objs", "totvs_protheus_sa2_fornecedor");
    jsonTestUtil("totvs/totvs-sra-real.json", "$.objs", "totvs_protheus_sra_funcionario");
    jsonTestUtil("ploomes/ploomes1.json", "$.value", "ploomes_clientes");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar.json", "$.queryResp[*].fields", "sharepoint_dsar");
    jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos.json",
            "$.queryResp[*].fields", "sharepoint_consents");
    jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-aviso-de-privacidade.json",
            "$.queryResp[*].fields", "sharepoint_privacy_notice");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-risk.json", "$.queryResp[*].fields",
            "sharepoint_risk");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-risk-mitigations.json", "$.queryResp[*].fields",
            "sharepoint_risk_mitigation");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-treinamentos.json", "$.queryResp[*].fields",
            "sharepoint_treinamentos");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-incidentes-de-seguranca-reportados.json",
            "$.queryResp[*].fields", "sharepoint_data_breaches");


    try {

      StringBuffer sb = new StringBuffer("[");
      boolean firstTime = true;

      long nowMs = System.currentTimeMillis();
      Date nowThreshold = new Date(nowMs);

      long oneYearThresholdMs = (long) (nowMs - (3600000L * 24L * 365L));
      Date oneYearDateThreshold = new Date(oneYearThresholdMs);

      String output = getDSARStatsPerOrganisation(App.g);

      assertTrue(output.contains("{\"dsar_source_type\":\"Delete (Total)\",\"dsar_source_name\":\"TOTAL_TYPE\", \"dsar_count\": 1 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Bloqueio (Total)\",\"dsar_source_name\":\"TOTAL_TYPE\", \"dsar_count\": 1 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Update (Total)\",\"dsar_source_name\":\"TOTAL_TYPE\", \"dsar_count\": 1 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Read (Total)\",\"dsar_source_name\":\"TOTAL_TYPE\", \"dsar_count\": 1 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Completed (Total)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 1 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"New (Total)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 1 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Acknowledged (Total)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 1 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Denied (Total)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 1 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Read (0-5d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Update (0-5d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Delete (0-5d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Bloqueio (0-5d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Read (5-10d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Update (5-10d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Delete (5-10d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Bloqueio (5-10d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Read (10-15d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Update (10-15d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Delete (10-15d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Bloqueio (10-15d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"Read (15-30d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Update (15-30d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Delete (15-30d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Bloqueio (15-30d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 }"));
//      assertTrue(output.contains("{\"dsar_source_type\":\"Bloqueio (30-365d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 1 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"New (0-5d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Acknowledged (0-5d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Completed (0-5d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"New (5-10d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Acknowledged (5-10d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Completed (5-10d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"New (10-15d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Acknowledged (10-15d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Completed (10-15d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 }"));
      assertTrue(output.contains("{\"dsar_source_type\":\"New (15-30d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Acknowledged (15-30d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Completed (15-30d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 }"));
//      assertTrue(output.contains("{\"dsar_source_type\":\"New (30-365d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 1 }"));

      sb.setLength(0);

      boolean output2 = getDSARStatsPerRequestType(nowThreshold, oneYearDateThreshold, firstTime, "0-365d", sb);
//      assertTrue(sb.toString().contains("{\"dsar_source_type\":\"Bloqueio (0-365d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 1 }"));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00003SARUpdates() throws InterruptedException {

    //simple unit test, focusing only on the DSAR part of the Graph (no Peron_Natural, no RoPA, no Data_Source, etc)
    try {
      // first ingestion, DSAR 0
      jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar-update-test-0.json","$.queryResp[*].fields", "sharepoint_dsar");

        OGremlinResultSet resSet = App.graph.executeSql(
        "SELECT Event_Subject_Access_Request_Request_Type as type " +
          "FROM Event_Subject_Access_Request " +
          "WHERE Event_Subject_Access_Request_Form_Id = 19874373249732", Collections.EMPTY_MAP);

      String dsarType = resSet.iterator().next().getRawResult().getProperty("type");
      resSet.close();
      assertEquals("Read", dsarType, "DSAR Type is of Update at DSAR 0");

      // second ingestion [first update], DSAR 1, with a different request type = MODIFIED
      jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar-update-test-1.json","$.queryResp[*].fields", "sharepoint_dsar");

      resSet = App.graph.executeSql(
        "SELECT Event_Subject_Access_Request_Request_Type as type " +
          "FROM Event_Subject_Access_Request " +
          "WHERE Event_Subject_Access_Request_Form_Id = 19874373249732", Collections.EMPTY_MAP);

      dsarType = resSet.iterator().next().getRawResult().getProperty("type");
      resSet.close();
      assertEquals("MODIFIED", dsarType, "DSAR Type has been updated to MODIFIED at DSAR 1");

      // third ingestion [second update], DSAR 2, with a different status = MODIFIED
      jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar-update-test-2.json","$.queryResp[*].fields", "sharepoint_dsar");

      resSet = App.graph.executeSql(
        "SELECT Event_Subject_Access_Request_Status as status " +
          "FROM Event_Subject_Access_Request " +
          "WHERE Event_Subject_Access_Request_Form_Id = 19874373249732", Collections.EMPTY_MAP);

      String dsarStatus = resSet.iterator().next().getRawResult().getProperty("status");
      resSet.close();
      assertEquals("MODIFIED", dsarStatus, "DSAR Status has been updated to MODIFIED at DSAR 2");

      // fourth ingestion [third update], DSAR 3, with a different Natural_Person_Type = MODIFIED
      jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar-update-test-3.json","$.queryResp[*].fields", "sharepoint_dsar");

      resSet = App.graph.executeSql(
        "SELECT Event_Subject_Access_Request_Natural_Person_Type as person " +
          "FROM Event_Subject_Access_Request " +
          "WHERE Event_Subject_Access_Request_Form_Id = 19874373249732", Collections.EMPTY_MAP);

      String dsarUpdateDate = resSet.iterator().next().getRawResult().getProperty("person");
      resSet.close();
      assertEquals("MODIFIED", dsarUpdateDate, "DSAR Natural_Person_Type has been updated to MODIFIED at DSAR 3");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}