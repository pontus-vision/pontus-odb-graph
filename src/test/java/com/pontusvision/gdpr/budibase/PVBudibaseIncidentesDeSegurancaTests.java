package com.pontusvision.gdpr.budibase;

import com.pontusvision.gdpr.AnnotationTestsOrderer;
import com.pontusvision.gdpr.App;
import com.pontusvision.gdpr.AppTest;
import com.pontusvision.gdpr.TestClassesOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(22)

public class PVBudibaseIncidentesDeSegurancaTests extends AppTest {

  @Test
  public void test00001BudibaseIncidentesDeSegurancaReportados() throws InterruptedException {

    jsonTestUtil("budibase/bb-incidentes-de-seguranca.json", "$.rows", "bb_incidentes_de_seguranca_reportados");

    try {

      String dataBreachStatus =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/incidentes-de-segurança-reportados')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('event-data-breach')" +
                      ".has('Event_Data_Breach_Form_Id'" +
                      ",eq('ro_ta_dc72039093fd48368c19faf546feb5d0_500a95bf92304930acec7005c9d3f9d9'))" +
                      ".values('Event_Data_Breach_Status').next().toString()").get().toString();
      assertEquals("Resolved", dataBreachStatus, "Status for incidente 667");

      String dataBreachCreateDate =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/incidentes-de-segurança-reportados')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('event-data-breach')" +
                      ".has('Event_Data_Breach_Form_Id'" +
                      ",eq('ro_ta_dc72039093fd48368c19faf546feb5d0_bd9367dc9bd3469caaab1e400841c473'))" +
                      ".values('Event_Data_Breach_Metadata_Create_Date').next().toString()").get().toString();
      Date dateObj = dtfmt.parse(dataBreachCreateDate);
      Date expDateObj = dtfmt.parse("Tue Feb 08 21:14:00 UTC 2022");
      assertEquals(expDateObj, dateObj, "Time of this Data Breach event");

      String dataBreachSource =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/incidentes-de-segurança-reportados')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('event-data-breach')" +
                      ".has('Event_Data_Breach_Form_Id'" +
                      ",eq('ro_ta_dc72039093fd48368c19faf546feb5d0_fda530b0eace44f29ddb6231a8b3667e'))" +
                      ".properties('Event_Data_Breach_Source').value().next().toString()").get().toString();
      assertEquals("ABC", dataBreachSource, "Source for the Data Breach incidente 8");

      String dataSourceArray =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/incidentes-de-segurança-reportados')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('event-data-breach')" +
                      ".has('Event_Data_Breach_Form_Id'" +
                      ",eq('ro_ta_dc72039093fd48368c19faf546feb5d0_bd9367dc9bd3469caaab1e400841c473'))" +
                      ".out('Impacted_By_Data_Breach')" +
                      ".has('Metadata_Type_Object_Data_Source', eq('Object_Data_Source'))" +
                      ".dedup().count().next().toString()").get().toString();
      assertEquals("3", dataSourceArray,
              "This Data_Breach event has 3 data_sources: On-Premises / SaaS / test123");

      String opinionsBreach =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('BUDIBASE/INCIDENTES-DE-SEGURANÇA-REPORTADOS'))" +
                      ".out('Has_Ingestion_Event').as('event-group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('data-breach')" +
                      ".has('Event_Data_Breach_Form_Id'" +
                      ",eq('ro_ta_dc72039093fd48368c19faf546feb5d0_fda530b0eace44f29ddb6231a8b3667e'))" +
//                      ".out('Impacted_By_Data_Breach').as('data-sources')" +
//                      ".has('Object_Data_Source_Name', eq('SaaS'))" +
//                      ".in('Impacted_By_Data_Breach').as('data-breach-again')" +
                      ".values('Event_Data_Breach_Impact').next().toString()").get().toString();
      assertEquals("High", opinionsBreach,"Impact for incidente 8");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using bb_incidentes_de_seguranca_reportados POLE
  @Test
  public void test00002UpsertBudibaseIncidentesDeSegurancaReportados() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-incidentes-de-seguranca.json", "$.rows", "bb_incidentes_de_seguranca_reportados");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/incidentes-de-segurança-reportados'))" +
                      ".count().next().toString()").get().toString();

      String countDataBreach =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/incidentes-de-segurança-reportados'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Event_Data_Breach', eq('Event_Data_Breach'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("budibase/bb-incidentes-de-seguranca-2.json", "$.rows", "bb_incidentes_de_seguranca_reportados");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/incidentes-de-segurança-reportados'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countDataBreachAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/incidentes-de-segurança-reportados'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Event_Data_Breach', eq('Event_Data_Breach'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the budibase/incidentes-de-segurança-reportados Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countDataBreach) == Integer.parseInt(countDataBreachAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}