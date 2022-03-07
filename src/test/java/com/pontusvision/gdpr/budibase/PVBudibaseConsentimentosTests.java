package com.pontusvision.gdpr.budibase;

import com.pontusvision.gdpr.AnnotationTestsOrderer;
import com.pontusvision.gdpr.App;
import com.pontusvision.gdpr.AppTest;
import com.pontusvision.gdpr.TestClassesOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(21)

public class PVBudibaseConsentimentosTests extends AppTest {

  @Test
  public void test00001BudibaseConsentimentos() throws InterruptedException {
    try {
      jsonTestUtil("budibase/bb-consentimentos.json", "$.rows", "bb_consentimentos");

      String joaoPrivacyNotices =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/consentimentos')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('JOÃO TEST'))" +
                      ".out('Consent').as('event-consent')" +
                      ".out('Has_Privacy_Notice').as('privacy-notice')" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", joaoPrivacyNotices, "João has one Privacy notice linked.");

      String joaoDataProcedures =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/consentimentos')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('JOÃO TEST'))" +
                      ".out('Consent').as('event-consent')" +
                      ".out('Consent').dedup().as('data-procs')" +
                      ".count().next().toString()").get().toString();
      assertEquals("4", joaoDataProcedures, "João has 3 Data procedures linked to its Consent.");

      String getPrivacyNoticeId =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/consentimentos')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural'" +
                      ",eq('Person_Natural')).as('person-natural')" +
                      ".out('Consent').as('event-consent')" +
                      ".out('Has_Privacy_Notice').as('privacy-notice')" +
                      ".values('Object_Privacy_Notice_Form_Id')" +
                      ".next().toString()").get().toString();
      assertEquals("ro_ta_0714853d6bb241da8bab8e231d12e6e4_4a964bc2d5c54feaac7e889975cc1752",
              getPrivacyNoticeId, "Data Source Name.");

      String getPersonNaturalCustomerID =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/consentimentos')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".values('Person_Natural_Customer_ID').next().toString()").get().toString();
      assertEquals("02143523498", getPersonNaturalCustomerID, "João's Customer Id Number");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using bb_consentimentos POLE
  @Test
  public void test00002UpsertBudibaseConsentimentos() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-consentimentos.json", "$.rows", "bb_consentimentos");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/consentimentos'))" +
                      ".count().next().toString()").get().toString();

      String countDataProcs =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/consentimentos'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Procedures', eq('Object_Data_Procedures'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("budibase/bb-consentimentos-2.json", "$.rows", "bb_consentimentos");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/consentimentos'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countDataProcsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/consentimentos'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Procedures', eq('Object_Data_Procedures'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the budibase/consentimentos Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countDataProcs) == Integer.parseInt(countDataProcsAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}