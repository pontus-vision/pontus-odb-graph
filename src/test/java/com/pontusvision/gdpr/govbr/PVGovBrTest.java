package com.pontusvision.gdpr.govbr;

import com.pontusvision.gdpr.AnnotationTestsOrderer;
import com.pontusvision.gdpr.App;
import com.pontusvision.gdpr.AppTest;
import com.pontusvision.gdpr.TestClassesOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(32)

//@RunWith(JUnitPlatform.class)
public class PVGovBrTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001GovBr() throws InterruptedException {
    // updated to real style data lead-reporting.csv

    try {

      csvTestUtil("govbr/APEX_SGP_GCOM.PARTICIPANTE.csv", "govbr_apex_participante");

      String emailMarcela =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SISTEMA DE GESTÃO POR COMPETÊNCIAS-COMPET'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('ingestion-event')" +
                      ".out('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('MARCELA KUKA'))" +
                      ".out('Uses_Email').as('email')" +
                      ".values('Object_Email_Address_Email').next().toString()").get().toString();
      assertEquals("marcelakuka@gov.br", emailMarcela, "Marcela's email");


      String bereniceLocation =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SISTEMA DE GESTÃO POR COMPETÊNCIAS-COMPET'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('ingestion-event')" +
                      ".out('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('BERENICE GONÇALVES CAMARGO'))" +
                      ".out('Is_Located').as('location-address')" +
                      ".values('Location_Address_Full_Address').next().toString()").get().toString();
      assertEquals("PGR, DIVT/SEDEP - DF, 266, BRASIL", bereniceLocation, "Berenice Location Address");


      String jeronimoRole =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SISTEMA DE GESTÃO POR COMPETÊNCIAS-COMPET'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('ingestion-event')" +
                      ".out('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('JERONIMO DE MADERO'))" +
                      ".out('Is_Alias').as('person-employee')" +
                      ".values('Person_Employee_Role').next().toString()").get().toString();
      assertEquals("ASSISTENTE NIVEL II - ANALISTA", jeronimoRole, "Jeronimo's role @ work");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  // Testing for upsert of two similar csv data using govbr_apex_participante POLE
  @Test
  public void test00002UpsertGovBr() throws InterruptedException {
    try {

      csvTestUtil("govbr/APEX_SGP_GCOM.PARTICIPANTE.csv", "govbr_apex_participante");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('SISTEMA DE GESTÃO POR COMPETÊNCIAS-COMPET'))" +
                      ".count().next().toString()").get().toString();

      String countDataSources =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('SISTEMA DE GESTÃO POR COMPETÊNCIAS-COMPET'))" +
                      ".in('Has_Ingestion_Event').as('group-ingestion')" +
                      ".in('Has_Ingestion_Event').as('data-source')" +
                      ".has('Metadata_Type_Object_Data_Source', eq('Object_Data_Source'))" +
                      ".dedup().count().next().toString()").get().toString();

      csvTestUtil("govbr/APEX_SGP_GCOM.PARTICIPANTE-2.csv", "govbr_apex_participante");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('SISTEMA DE GESTÃO POR COMPETÊNCIAS-COMPET'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data
      String countDataSourcesAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('SISTEMA DE GESTÃO POR COMPETÊNCIAS-COMPET'))" +
                      ".in('Has_Ingestion_Event').as('group-ingestion')" +
                      ".in('Has_Ingestion_Event').as('data-source')" +
                      ".has('Metadata_Type_Object_Data_Source', eq('Object_Data_Source'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the SISTEMA DE GESTÃO POR COMPETÊNCIAS-COMPET Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countDataSources) == Integer.parseInt(countDataSourcesAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }
}
