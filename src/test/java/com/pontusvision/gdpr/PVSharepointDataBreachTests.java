package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(10)

public class PVSharepointDataBreachTests extends AppTest {

  @Test
  public void test00001SharepointDataBreach() throws InterruptedException {

    jsonTestUtil("pv-extract-sharepoint-incidentes-de-seguranca-reportados.json",
            "$.queryResp[*].fields", "sharepoint_data_breaches");

    try {

      String dataBreachStatus =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('CRM-LEADS')).in('Impacted_By_Data_Breach')" +
                      ".properties('Event.Data_Breach.Status').value().next().toString()").get().toString();
      assertEquals("Open", dataBreachStatus, "Status for Vazamento de E-mails de Clientes");

      String dataBreachDate =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('CRM-LEADS')).in('Impacted_By_Data_Breach')" +
              ".values('Event.Data_Breach.Metadata.Create_Date').next().toString()").get().toString();

      Date dateObj = dtfmt.parse(dataBreachDate);
      Date expDateObj = dtfmt.parse("Thu Aug 12 15:17:48 GMT 2021");
      assertEquals(expDateObj, dateObj, "Time of the Data Breach");

      String dataBreachSource =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('CRM-LEADS')).in('Impacted_By_Data_Breach')" +
                      ".properties('Event.Data_Breach.Source').value().next().toString()").get().toString();
      assertEquals("OUTLOOK, GMAIL, YAHOO MAIL", dataBreachSource, "Source for the Data Breach on Documents");

      String dataSourceArray =
              App.executor.eval("App.g.V().has('Event.Data_Breach.Description', " +
                      "eq('VAZAMENTO DO HISTÓRICO DE NAVEGAÇÃO DOS COLABORADORES')).out('Impacted_By_Data_Breach')" +
                      ".has('Metadata.Type.Object.Data_Source', eq('Object.Data_Source')).dedup().count().next().toString()").get().toString();
      assertEquals("3", dataSourceArray,"This Data_Breach event has 4 data_sources: " +
              "Histórico navegador Google Chrome / Mozilla Firefox / Microsoft Edge / Apple Safari");

      String opinionsBreach =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('SHAREPOINT/DATA-BREACHES'))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".out('Impacted_By_Data_Breach').has('Object.Data_Source.Name', eq('ERP-HR'))" +
                      ".in('Impacted_By_Data_Breach').values('Event.Data_Breach.Impact').next().toString()").get().toString();
      assertEquals("No Impact", opinionsBreach,"Impact for breaching employees opinions");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00002SharepointLegalActions() throws InterruptedException {

    jsonTestUtil("non-official-pv-extract-sharepoint-legal-actions.json",
            "$.queryResp[*].fields", "sharepoint_legal_actions");

    try {

      String legalAction1 =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('SHAREPOINT/LEGAL-ACTIONS'))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".has('Metadata.Type.Object.Legal_Actions',eq('Object.Legal_Actions'))" +
                      ".values('Object.Legal_Actions.Details').next().toString()").get().toString();
      assertEquals("Processo por vazamento de dados pessoais dos clientes.", legalAction1,
              "Detalhes da Ação Legal 1.");

      String legalAction2 =
              App.executor.eval("App.g.V().has('Object.Legal_Actions.Form_Id',eq('2'))" +
                      ".values('Object.Legal_Actions.Description').next().toString()").get().toString();
      assertEquals("Ação judicial 768", legalAction2,"Descrição da Ação Legal 2.");

      String legalAction3 =
              App.executor.eval("App.g.V().has('Object.Legal_Actions.Description',eq('Ação judicial 333'))" +
                      ".values('Object.Legal_Actions.Date').next().toString()").get().toString();
      assertEquals(dtfmt.parse("Wed Jan 19 01:01:01 GMT 2022"), dtfmt.parse(legalAction3),
              "Data da Ação Legal 3.");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using sharepoint_legal_actions POLE
  @Test
  public void test00003UpsertSharepointLegalActions() throws InterruptedException {
    try {
      jsonTestUtil("non-official-pv-extract-sharepoint-legal-actions.json",
              "$.queryResp[*].fields", "sharepoint_legal_actions");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Legal Actions'))" +
                      ".count().next().toString()").get().toString();

      String countLegalActions =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Legal Actions'))" +
                      ".out('Has_Ingestion_Event').has('Metadata.Type.Object.Legal_Actions', eq('Object.Legal_Actions'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("non-official-pv-extract-sharepoint-legal-actions2.json",
              "$.queryResp[*].fields", "sharepoint_privacy_notice");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Legal Actions'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countLegalActionsAgain =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Legal Actions'))" +
                      ".out('Has_Ingestion_Event').has('Metadata.Type.Object.Legal_Actions', eq('Object.Legal_Actions'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made the Legal Actions Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countLegalActions) == Integer.parseInt(countLegalActionsAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}