package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
            "$.queryResp[*].fields", "sharepoint_incidentes_de_seguranca");

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
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('SHAREPOINT/INCIDENTES-DE-SEGURANÇA-REPORTADOS'))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".out('Impacted_By_Data_Breach').has('Object.Data_Source.Name', eq('ERP-HR'))" +
                      ".in('Impacted_By_Data_Breach').values('Event.Data_Breach.Impact').next().toString()").get().toString();
      assertEquals("No Impact", opinionsBreach,"Impact for breaching employees opinions");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}