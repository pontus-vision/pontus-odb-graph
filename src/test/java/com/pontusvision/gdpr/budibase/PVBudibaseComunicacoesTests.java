package com.pontusvision.gdpr.budibase;

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


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(24)

public class PVBudibaseComunicacoesTests extends AppTest {

  @Test
  public void test00001BudibaseComunicacoesPPD() throws InterruptedException {

    jsonTestUtil("budibase/bb-comunicacoes-ppd.json", "$.rows","bb_comunicacoes_ppd");

    try {

      String privacyDoc1 =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name'" +
                      ",eq('BUDIBASE/COMUNICAÇÕES-P&PD')).as('data-source')" +
                      ".out('Has_Ingestion_Event').as('event-group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('privacy-docs').has('Object_Privacy_Docs_Form_Id'" +
                      ",eq('ro_ta_d396b9320fca45b68aa935379ca0bcd9_5794c115f8624d0cb69bd15356a120cd'))" +
                      ".values('Object_Privacy_Docs_Description').next().toString()").get().toString();
      assertEquals("teste", privacyDoc1, "Privacy Doc 1's Description.");

      String privacyDocDate =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name'" +
                      ",eq('BUDIBASE/COMUNICAÇÕES-P&PD')).as('data-source')" +
                      ".out('Has_Ingestion_Event').as('event-group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('privacy-docs').has('Object_Privacy_Docs_Form_Id'" +
                      ",eq('ro_ta_d396b9320fca45b68aa935379ca0bcd9_5794c115f8624d0cb69bd15356a120cd'))" +
                      ".values('Object_Privacy_Docs_Date').next().toString()").get().toString();
      assertEquals(dtfmt.parse("Tue Feb 08 15:00:00 UTC 2022"), dtfmt.parse(privacyDocDate),"Privacy Doc 2's Title");

      String privacyDocTitle =
                      App.executor.eval("App.g.V().has('Object_Data_Source_Name'" +
                              ",eq('BUDIBASE/COMUNICAÇÕES-P&PD')).as('data-source')" +
                              ".out('Has_Ingestion_Event').as('event-group-ingestion')" +
                              ".out('Has_Ingestion_Event').as('event-ingestion')" +
                              ".out('Has_Ingestion_Event').as('privacy-docs').has('Object_Privacy_Docs_Form_Id'" +
                              ",eq('ro_ta_d396b9320fca45b68aa935379ca0bcd9_5794c115f8624d0cb69bd15356a120cd'))" +
                              ".values('Object_Privacy_Docs_Title').next().toString()").get().toString();
      assertEquals("folheto LGPD",privacyDocTitle,"Privacy Docs' Title");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using bb_comunicacoes_ppd POLE
  @Test
  public void test00002UpsertBudibasePoliticas() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-comunicacoes-ppd.json", "$.rows", "bb_comunicacoes_ppd");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/comunicações-p&pd'))" +
                      ".count().next().toString()").get().toString();

      String countPrivacyDocs =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/comunicações-p&pd'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Policies', eq('Object_Policies'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("budibase/bb-comunicacoes-ppd-2.json", "$.rows", "bb_comunicacoes_ppd");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/comunicações-p&pd'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countPrivacyDocsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/comunicações-p&pd'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Policies', eq('Object_Policies'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the budibase/comunicações-p&pd Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countPrivacyDocs) == Integer.parseInt(countPrivacyDocsAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}