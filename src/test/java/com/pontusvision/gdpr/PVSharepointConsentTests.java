package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(15)

public class PVSharepointConsentTests extends AppTest {

  @Test
  public void test0001SharepointConsents() throws InterruptedException {
    try {
//      jsonTestUtil("totvs/totvs-consent-base.json",
//              "$.queryResp[*].fields", "sharepoint_consents");
      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos.json",
        "$.queryResp[*].fields", "sharepoint_consents");

//    POLE Sharepoint Consent doesn't have Person_Natural_Full_Name property anymore, we use Person_Natural_Customer_ID instead
      String lakshmiPrivacyNotices =
        App.executor.eval("App.g.V().has('Person_Natural_Customer_ID', eq('78391192021')).out('Consent')" +
          ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("0", lakshmiPrivacyNotices, "Lakshmi has NO Privacy notices."); // 78391192021 is Lakshmi's ID

      String janosPrivacyNotices =
        App.executor.eval("App.g.V().has('Person_Natural_Customer_ID', eq('12343161718')).out('Consent')" +
          ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("2", janosPrivacyNotices, "Janos has 2 Privacy notices."); // 12343161718 is Janos' ID

      String janosDataProcedures =
        App.executor.eval("App.g.V().has('Person_Natural_Customer_ID', eq('12343161718')).out('Consent')" +
          ".out('Consent').count().next().toString()").get().toString();
      assertEquals("1", janosDataProcedures, "Janos has 1 Data procedure linked to its Consent."); // 12343161718 is Janos' ID

      String petrusDataProcedures =
        App.executor.eval("App.g.V().has('Person_Natural_Customer_ID', eq('67481131415')).out('Consent')" +
          ".out('Consent').count().next().toString()").get().toString();
      assertEquals("0", petrusDataProcedures, "Petrus has NO Data procedures linked to its Consent."); // 67481131415 is Janos' ID

      String laraPrivacyNotices =
        App.executor.eval("App.g.V().has('Person_Natural_Customer_ID', eq('73841101112')).out('Consent')" +
          ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("1", laraPrivacyNotices, "Lara has one Privacy notices.");  // 73841101112 is Lara's ID

      String getObjectDataSourceName =
        App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('4')).in('Has_Privacy_Notice')" +
          ".in('Consent').out('Has_Ingestion_Event').in('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
          ".has('Object_Data_Source_Name', eq('SHAREPOINT/CONSENT'))" +
          ".properties('Object_Data_Source_Name').value().next().toString()").get().toString();
      assertEquals("SHAREPOINT/CONSENT", getObjectDataSourceName, "Data Source Name.");

      String getPersonNaturalCustomerID =
        App.executor.eval("App.g.V().has('Event_Consent_Form_Id', eq('1')).in('Consent')" +
          ".properties('Person_Natural_Customer_ID').value().next().toString()").get().toString();
      assertEquals("12389256473", getPersonNaturalCustomerID, "Karla's Customer Id Number");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using sharepoint_consents POLE
  @Test
  public void test00002UpsertSharepointConsents() throws InterruptedException {
    try {
//    adding people first ...
      jsonTestUtil("totvs/totvs-sa1.json", "$.objs", "totvs_protheus_sa1_clientes");

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos.json",
              "$.queryResp[*].fields", "sharepoint_consents");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Consent'))" +
                      ".count().next().toString()").get().toString();

      String countPersonNatural =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Consent'))" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countConsentEvents =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Consent'))" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
                      ".out('Consent').dedup().count().next().toString()").get().toString();

      String consentEventStatusCount =
          App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Consent'))" +
              ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
               ".out('Consent').dedup().has('Event_Consent_Status', eq('Consent')).count().next().toString()").get().toString();

      assertEquals("7", consentEventStatusCount, "Consent event status count");

//    Test for duplicate data --------------------------------------------------------------------------------------

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos2.json",
              "$.queryResp[*].fields", "sharepoint_consents");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Consent'))" +
                      ".count().next().toString()").get().toString();

      String countPersonNaturalAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Consent'))" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countConsentEventsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Consent'))" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
                      ".out('Consent').dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made the Privacy Notice Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countPersonNatural) == Integer.parseInt(countPersonNaturalAgain));
      assertTrue(Integer.parseInt(countConsentEvents) == Integer.parseInt(countConsentEventsAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // next test might not work if ran isolated
  @Test
  public void test00003SharepointConsentPlusPrivacyNotice() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-aviso-de-privacidade.json",
        "$.queryResp[*].fields", "sharepoint_privacy_notice");

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos.json",
        "$.queryResp[*].fields", "sharepoint_consents");

      String consentDataSourceName = "SHAREPOINT/CONSENT";

      String privacyNoticeDataSourceName =
        App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq(\"" + consentDataSourceName + "\"))" +
          ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event').out('Consent')" +
          ".out('Has_Privacy_Notice').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
          ".in('Has_Ingestion_Event').values('Object_Data_Source_Name').next().toString()").get().toString();
      assertEquals("SHAREPOINT/PRIVACY-NOTICE", privacyNoticeDataSourceName, "Data Source Name for Privacy Notice POLE.");

      String getPersonNaturalByPrivacyNotice =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq(\"" + privacyNoticeDataSourceName + "\"))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".has('Object_Privacy_Notice_Form_Id', eq('4')).in('Has_Privacy_Notice')" +
                      ".has('Event_Consent_Customer_ID', eq('45637846571'))" +
                      ".in('Consent').has('Person_Natural_Customer_ID', eq('45637846571'))" +
                      ".out('Has_Ingestion_Event').in('Has_Ingestion_Event')"+
                      ".has('Event_Group_Ingestion_Type', eq('sharepoint/consent'))" +
                      ".values('Event_Group_Ingestion_Type').next().toString()").get().toString();
      assertEquals("sharepoint/consent", getPersonNaturalByPrivacyNotice,
        "From privacy notice to consent");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}