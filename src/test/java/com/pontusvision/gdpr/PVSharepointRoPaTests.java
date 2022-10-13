package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(8)

public class PVSharepointRoPaTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  @Test
  public void test00001SharepointMapeamentoDeProcesso() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");

      String EPGInterest =
              App.executor.eval("App.g.V().has('Object_Data_Policy_Type', eq('6')).in('Has_Policy')" +
                      ".properties('Object_Data_Procedures_Interested_Parties_Consulted').value().next().toString()").get().toString();
      assertEquals("EPG Advogados", EPGInterest, "EPG Advogados is a Party Interested in these Data Procedures");

      String numEPGParties =
              App.executor.eval("App.g.V().has('Object_Data_Procedures_Interested_Parties_Consulted', " +
                      "eq('EPG Advogados')).count().next().toString()").get().toString();
      assertEquals("2", numEPGParties, "2 registries have EPG Advogados as an Interested Party");

      String juridicoInterest =
              App.executor.eval("App.g.V().has('Object_Lawful_Basis_Description', eq('LEGÍTIMO INTERESSE DO CONTROLADOR'))" +
                      ".in('Has_Lawful_Basis_On').properties('Object_Data_Procedures_Interested_Parties_Consulted').value().next().toString()").get().toString();
      assertEquals("Jurídico Snowymountain", juridicoInterest, "Jurídico Snowymountain is a Party Interested in these Data Procedures");

      String numJuridicoParties =
              App.executor.eval("App.g.V().has('Object_Data_Procedures_Interested_Parties_Consulted', " +
                      "eq('Jurídico Snowymountain')).count().next().toString()").get().toString();
      assertEquals("4", numJuridicoParties, "4 registries have Jurídico Snowymountain as an Interested Party");

      String LIALawfulBasis =
              App.executor.eval("App.g.V().has('Object_Data_Procedures_Form_Id', eq('404'))" +
                      ".out('Has_Legitimate_Interests_Assessment')" +
                      ".properties('Object_Legitimate_Interests_Assessment_Lawful_Basis_Justification')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Legitimo interesse", LIALawfulBasis, "Lawful Basis Justification for this LIA is => Legitimo interesse");

      String LIAProcessingPurpose =
              App.executor.eval("App.g.V().has('Object_Data_Procedures_Form_Id', eq('406'))" +
                      ".out('Has_Legitimate_Interests_Assessment')" +
                      ".properties('Object_Legitimate_Interests_Assessment_Is_Required').value()" +
                      ".next().toString()").get().toString();
      assertEquals("Sim", LIAProcessingPurpose, "Processing Purpose for this LIA is => SIM");

      String LIAPersonalDataTreatment =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', " +
                      "eq('SHAREPOINT/MAPEAMENTO-DE-PROCESSOS')).out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".out('Has_Ingestion_Event').out('Has_Lawful_Basis_On').has('Object_Lawful_Basis_Description', " +
                      "eq('EXECUÇÃO DE CONTRATO OU DE PROCEDIMENTOS PRELIMINARES A CONTRATO, A PEDIDO DO TITULAR'))" +
                      ".in('Has_Lawful_Basis_On').out('Has_Legitimate_Interests_Assessment')" +
                      ".properties('Object_Legitimate_Interests_Assessment_Is_Essential')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Sim, é indispensável - processo 1", LIAPersonalDataTreatment, "Personal Data Treatment for this LIA is => Sim, é indispensável");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00002SharepointPrivacyNotice() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-aviso-de-privacidade.json",
              "$.queryResp[*].fields", "sharepoint_privacy_notice");

      String getPrivacyNoticeDescription =
              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('1'))" +
                      ".properties('Object_Privacy_Notice_Description').value()" +
                      ".next().toString()").get().toString();
      assertEquals("O Titular foi avisado e concordou com os termos", getPrivacyNoticeDescription,
              "Privacy Notice's Description for Title 'abc'.");

      String getPrivacyNoticeCreateDate =
              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('5'))" +
                      ".properties('Object_Privacy_Notice_Metadata_Create_Date').value()" +
                      ".next().toString()").get().toString();
//      getPrivacyNoticeCreateDate = getPrivacyNoticeCreateDate.replaceAll("... 2022", "GMT 2022");
      assertEquals(dtfmt.parse("Wed Jan 05 19:18:05 GMT 2022"), dtfmt.parse(getPrivacyNoticeCreateDate),
              "Privacy Notice's creation date.");

//      String getPrivacyNoticeCountryWhereStored =
//              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('2'))" +
//                      ".properties('Object_Privacy_Notice_Country_Where_Stored').value()" +
//                      ".next().toString()").get().toString();
//      assertEquals("Brazil", getPrivacyNoticeCountryWhereStored,
//              "Privacy Notice's Data's Country Storage.");

//      String getPrivacyNoticeWhoIsCollecting =
//              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('4'))" +
//                      ".properties('Object_Privacy_Notice_Who_Is_Collecting').value()" +
//                      ".next().toString()").get().toString();
//      assertEquals("Sxxxxxxxe", getPrivacyNoticeWhoIsCollecting,
//              "Privacy Notice's Data's Company Collector.");

      String getObjectDataSourceName =
              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('4')).out('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event').in('Has_Ingestion_Event').properties('Object_Data_Source_Name').value()" +
                      ".next().toString()").get().toString();
      assertEquals("SHAREPOINT/PRIVACY-NOTICE", getObjectDataSourceName, "Data Source Name.");

//      String getPrivacyNoticeDeliveryDate =
//              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('7'))" +
//                      ".properties('Object_Privacy_Notice_Delivery_Date').value()" +
//                      ".next().toString()").get().toString();
//      assertEquals(dtfmt.parse("Fri Dec 10 01:01:01 GMT 2021"), dtfmt.parse(getPrivacyNoticeDeliveryDate), "Privacy Notice's delivery date for id 'stuvw'.");

    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using sharepoint_privacy_notice POLE
  @Test
  public void test00003UpsertSharepointPrivacyNotice() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-aviso-de-privacidade.json",
              "$.queryResp[*].fields", "sharepoint_privacy_notice");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Privacy Notice'))" +
                      ".count().next().toString()").get().toString();

      String countPrivacyNotices =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Privacy Notice'))" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Object_Privacy_Notice', eq('Object_Privacy_Notice'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-aviso-de-privacidade2.json",
              "$.queryResp[*].fields", "sharepoint_privacy_notice");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Privacy Notice'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countPrivacyNoticesAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Privacy Notice'))" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Object_Privacy_Notice', eq('Object_Privacy_Notice'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made the Privacy Notice Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countPrivacyNotices) == Integer.parseInt(countPrivacyNoticesAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00004SharepointConsents() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos.json",
              "$.queryResp[*].fields", "sharepoint_consents");
//      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos2.json",
//              "$.queryResp[*].fields", "sharepoint_consents");

      String lakshmiPrivacyNotices =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('LAKSHMI')).out('Consent')" +
                      ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("0", lakshmiPrivacyNotices, "Lakshmi has NO Privacy notices.");

      String janosPrivacyNotices =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('JANOS GABOR')).out('Consent')" +
                      ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("2", janosPrivacyNotices, "Janos has 2 Privacy notices.");

      String janosDataProcedures =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('JANOS GABOR')).out('Consent')" +
                      ".out('Consent').count().next().toString()").get().toString();
      assertEquals("1", janosDataProcedures, "Janos has 1 Data procedure linked to its Consent.");

      String petrusDataProcedures =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('PETRUS PAPASTATHOPOULOS')).out('Consent')" +
                      ".out('Consent').count().next().toString()").get().toString();
      assertEquals("0", petrusDataProcedures, "Petrus has NO Data procedures linked to its Consent.");

      String laraPrivacyNotices =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('LARA CROFT')).out('Consent')" +
                      ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("1", laraPrivacyNotices, "Lara has one Privacy notices.");

      String getObjectDataSourceName =
              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('4')).in('Has_Privacy_Notice')" +
                      ".in('Consent').out('Has_Ingestion_Event').in('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".properties('Object_Data_Source_Name').value().next().toString()").get().toString();
      assertEquals("SHAREPOINT/CONSENT", getObjectDataSourceName, "Data Source Name.");

      String getPersonNaturalCustomerID =
              App.executor.eval("App.g.V().has('Event_Consent_Form_Id', eq('1')).in('Consent')" +
                      ".properties('Person_Natural_Customer_ID').value().next().toString()").get().toString();
      assertEquals("123", getPersonNaturalCustomerID, "Karla's Customer Id Number");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using sharepoint_consents POLE
  @Test
  public void test00005UpsertSharepointConsents() throws InterruptedException {
    try {
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

  @Test
  public void test00006SharepointConsentPlusPrivacyNotice() throws InterruptedException {
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
                      ".has('Event_Consent_Customer_ID', eq('456'))" +
                      ".in('Consent').has('Person_Natural_Customer_ID', eq('456'))" +
                      ".values('Person_Natural_Full_Name').next().toString()").get().toString();
      assertEquals("BOB NAKAMURA", getPersonNaturalByPrivacyNotice,
              "This Privacy Notice POLE Graph path leads to Bob Nakamura");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}