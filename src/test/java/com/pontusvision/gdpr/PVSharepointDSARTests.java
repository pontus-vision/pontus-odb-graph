package com.pontusvision.gdpr;

import com.pontusvision.graphutils.gdpr;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Date;

import static com.pontusvision.graphutils.gdpr.DSARStats.getDSARStatsPerOrganisation;
import static com.pontusvision.graphutils.gdpr.DSARStats.getDSARStatsPerRequestType;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                      ".out('Made_SAR_Request').properties('Event_Subject_Access_Request_Request_Type')" +
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
                      ".out('Made_SAR_Request').properties('Event_Subject_Access_Request_Request_Type')" +
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
    jsonTestUtil("ploomes/ploomes1.json", "$.value", "ploomes_clientes");
    jsonTestUtil("totvs/totvs-sa1.json", "$.objs", "totvs_protheus_sa1_clientes");
    jsonTestUtil("totvs/totvs-sa2-real.json", "$.objs", "totvs_protheus_sa2_fornecedor");
    jsonTestUtil("totvs/totvs-sra-real.json", "$.objs", "totvs_protheus_sra_funcionario");
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
//      String output2 = gdpr.getDSARStatsPerOrganisation().toString();
//      boolean output = getDSARStatsPerRequestType(nowThreshold, oneYearDateThreshold, firstTime, "0-365d", sb);
      assertEquals("[ {\"dsar_source_type\":\"Exclusão de e-mail (Total)\",\"dsar_source_name\":\"TOTAL_TYPE\", \"dsar_count\": 1 }\n" +
              ", {\"dsar_source_type\":\"Bloqueio de Conta (Total)\",\"dsar_source_name\":\"TOTAL_TYPE\", \"dsar_count\": 1 }\n" +
              ", {\"dsar_source_type\":\"Atualização de Endereço (Total)\",\"dsar_source_name\":\"TOTAL_TYPE\", \"dsar_count\": 1 }\n" +
              ", {\"dsar_source_type\":\"Exclusão de Contrato (Total)\",\"dsar_source_name\":\"TOTAL_TYPE\", \"dsar_count\": 1 }\n" +
              ", {\"dsar_source_type\":\"Completed (Total)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 1 }\n" +
              ", {\"dsar_source_type\":\"New (Total)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 1 }\n" +
              ", {\"dsar_source_type\":\"Acknowledged (Total)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 1 }\n" +
              ", {\"dsar_source_type\":\"Denied (Total)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 1 }\n" +
              ", {\"dsar_source_type\":\"Read (0-5d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Update (0-5d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Delete (0-5d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Bloqueio (0-5d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 }\n" +
              ", {\"dsar_source_type\":\"Read (5-10d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Update (5-10d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Delete (5-10d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Bloqueio (5-10d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 }\n" +
              ", {\"dsar_source_type\":\"Read (10-15d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Update (10-15d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Delete (10-15d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Bloqueio (10-15d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 }\n" +
              ", {\"dsar_source_type\":\"Read (15-30d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Update (15-30d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Delete (15-30d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Bloqueio (15-30d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 }\n" +
              ", {\"dsar_source_type\":\"Read (30-365d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Update (30-365d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Delete (30-365d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Bloqueio (30-365d)\",\"dsar_source_name\":\"TOTAL_REQ_TYPE\", \"dsar_count\": 0 }\n" +
              ", {\"dsar_source_type\":\"New (0-5d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Acknowledged (0-5d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Completed (0-5d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 }\n" +
              ", {\"dsar_source_type\":\"New (5-10d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Acknowledged (5-10d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Completed (5-10d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 }\n" +
              ", {\"dsar_source_type\":\"New (10-15d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Acknowledged (10-15d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Completed (10-15d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 }\n" +
              ", {\"dsar_source_type\":\"New (15-30d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Acknowledged (15-30d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Completed (15-30d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 }\n" +
              ", {\"dsar_source_type\":\"New (30-365d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Acknowledged (30-365d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 },{\"dsar_source_type\":\"Completed (30-365d)\",\"dsar_source_name\":\"TOTAL_STATUS\", \"dsar_count\": 0 }]", output, "DSAR Stats per Organisation");
    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}