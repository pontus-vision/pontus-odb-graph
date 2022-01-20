package com.pontusvision.gdpr;

import com.pontusvision.gdpr.report.ReportTemplateRenderRequest;
import com.pontusvision.gdpr.report.ReportTemplateRenderResponse;
import com.pontusvision.gdpr.report.ReportTemplateUpsertRequest;
import com.pontusvision.gdpr.report.ReportTemplateUpsertResponse;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
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
      jsonTestUtil("pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");

      String EPGInterest =
              App.executor.eval("App.g.V().has('Object.Data_Policy.Type', eq('6')).in('Has_Policy')" +
                      ".properties('Object.Data_Procedures.Interested_Parties_Consulted').value().next().toString()").get().toString();
      assertEquals("EPG Advogados", EPGInterest, "EPG Advogados is a Party Interested in these Data Procedures");

      String numEPGParties =
              App.executor.eval("App.g.V().has('Object.Data_Procedures.Interested_Parties_Consulted', " +
                      "eq('EPG Advogados')).count().next().toString()").get().toString();
      assertEquals("2", numEPGParties, "2 registries have EPG Advogados as an Interested Party");

      String juridicoInterest =
              App.executor.eval("App.g.V().has('Object.Lawful_Basis.Description', eq('LEGÍTIMO INTERESSE DO CONTROLADOR'))" +
                      ".in('Has_Lawful_Basis_On').properties('Object.Data_Procedures.Interested_Parties_Consulted').value().next().toString()").get().toString();
      assertEquals("Jurídico Snowymountain", juridicoInterest, "Jurídico Snowymountain is a Party Interested in these Data Procedures");

      String numJuridicoParties =
              App.executor.eval("App.g.V().has('Object.Data_Procedures.Interested_Parties_Consulted', " +
                      "eq('Jurídico Snowymountain')).count().next().toString()").get().toString();
      assertEquals("4", numJuridicoParties, "4 registries have Jurídico Snowymountain as an Interested Party");

      String LIALawfulBasis =
              App.executor.eval("App.g.V().has('Object.Data_Procedures.Form_Id', eq('404'))" +
                      ".out('Has_Legitimate_Interests_Assessment')" +
                      ".properties('Object.Legitimate_Interests_Assessment" +
                      ".Lawful_Basis_Justification').value().next().toString()").get().toString();
      assertEquals("Legitimo interesse", LIALawfulBasis, "Lawful Basis Justification for this LIA is => Legitimo interesse");

      String LIAProcessingPurpose =
              App.executor.eval("App.g.V().has('Object.Data_Procedures.Form_Id', eq('406'))" +
                      ".out('Has_Legitimate_Interests_Assessment')" +
                      ".properties('Object.Legitimate_Interests_Assessment.Processing_Purpose').value()" +
                      ".next().toString()").get().toString();
      assertEquals("Sim", LIAProcessingPurpose, "Processing Purpose for this LIA is => SIM");

      String LIAPersonalDataTreatment =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', " +
                      "eq('SHAREPOINT/MAPEAMENTO-DE-PROCESSOS')).out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".out('Has_Ingestion_Event').out('Has_Lawful_Basis_On').has('Object.Lawful_Basis.Description', " +
                      "eq('EXECUÇÃO DE CONTRATO OU DE PROCEDIMENTOS PRELIMINARES A CONTRATO, A PEDIDO DO TITULAR'))" +
                      ".in('Has_Lawful_Basis_On').out('Has_Legitimate_Interests_Assessment')" +
                      ".properties('Object.Legitimate_Interests_Assessment.Personal_Data_Treatment')" +
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
      jsonTestUtil("non-official-pv-extract-sharepoint-aviso-de-privacidade.json",
              "$.queryResp[*].fields", "sharepoint_privacy_notice");

      String getPrivacyNoticeDescription =
              App.executor.eval("App.g.V().has('Object.Privacy_Notice.Form_Id', eq('1'))" +
                      ".properties('Object.Privacy_Notice.Description').value()" +
                      ".next().toString()").get().toString();
      assertEquals("O Titular foi avisado e concordou com os termos", getPrivacyNoticeDescription,
              "Privacy Notice's Description for Title 'abc'.");

      String getPrivacyNoticeCreateDate =
              App.executor.eval("App.g.V().has('Object.Privacy_Notice.Form_Id', eq('5'))" +
                      ".properties('Object.Privacy_Notice.Metadata.Create_Date').value()" +
                      ".next().toString()").get().toString();
//      getPrivacyNoticeCreateDate = getPrivacyNoticeCreateDate.replaceAll("... 2022", "GMT 2022");
      assertEquals(dtfmt.parse("Wed Jan 05 19:18:05 GMT 2022"), dtfmt.parse(getPrivacyNoticeCreateDate),
              "Privacy Notice's creation date.");

//      String getPrivacyNoticeCountryWhereStored =
//              App.executor.eval("App.g.V().has('Object.Privacy_Notice.Form_Id', eq('2'))" +
//                      ".properties('Object.Privacy_Notice.Country_Where_Stored').value()" +
//                      ".next().toString()").get().toString();
//      assertEquals("Brazil", getPrivacyNoticeCountryWhereStored,
//              "Privacy Notice's Data's Country Storage.");

//      String getPrivacyNoticeWhoIsCollecting =
//              App.executor.eval("App.g.V().has('Object.Privacy_Notice.Form_Id', eq('4'))" +
//                      ".properties('Object.Privacy_Notice.Who_Is_Collecting').value()" +
//                      ".next().toString()").get().toString();
//      assertEquals("Sxxxxxxxe", getPrivacyNoticeWhoIsCollecting,
//              "Privacy Notice's Data's Company Collector.");

      String getObjectDataSourceName =
              App.executor.eval("App.g.V().has('Object.Privacy_Notice.Form_Id', eq('4')).out('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event').in('Has_Ingestion_Event').properties('Object.Data_Source.Name').value()" +
                      ".next().toString()").get().toString();
      assertEquals("SHAREPOINT/PRIVACY-NOTICE", getObjectDataSourceName, "Data Source Name.");

      String getPrivacyNoticeDeliveryDate =
              App.executor.eval("App.g.V().has('Object.Privacy_Notice.Form_Id', eq('7'))" +
                      ".properties('Object.Privacy_Notice.Delivery_Date').value()" +
                      ".next().toString()").get().toString();
      assertEquals(dtfmt.parse("Fri Dec 10 01:01:01 GMT 2021"), dtfmt.parse(getPrivacyNoticeDeliveryDate), "Privacy Notice's delivery date for id 'stuvw'.");

    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using sharepoint_privacy_notice POLE
  @Test
  public void test00003UpsertSharepointPrivacyNotice() throws InterruptedException {
    try {
      jsonTestUtil("non-official-pv-extract-sharepoint-aviso-de-privacidade.json",
              "$.queryResp[*].fields", "sharepoint_privacy_notice");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Privacy Notice'))" +
                      ".count().next().toString()").get().toString();

      String countPrivacyNotices =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Privacy Notice'))" +
                      ".in('Has_Ingestion_Event').has('Metadata.Type.Object.Privacy_Notice', eq('Object.Privacy_Notice'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("non-official-pv-extract-sharepoint-aviso-de-privacidade2.json",
              "$.queryResp[*].fields", "sharepoint_privacy_notice");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Privacy Notice'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countPrivacyNoticesAgain =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Privacy Notice'))" +
                      ".in('Has_Ingestion_Event').has('Metadata.Type.Object.Privacy_Notice', eq('Object.Privacy_Notice'))" +
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
      jsonTestUtil("non-official-pv-extract-sharepoint-consentimentos.json",
              "$.queryResp[*].fields", "sharepoint_consents");

      String lakshmiPrivacyNotices =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('LAKSHMI')).out('Consent')" +
                      ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("0", lakshmiPrivacyNotices, "Lakshmi has NO Privacy notices.");

      String janosPrivacyNotices =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('JANOS GÁBOR')).out('Consent')" +
                      ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("2", janosPrivacyNotices, "Janos has 2 Privacy notices.");

      String laraPrivacyNotices =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('LARA CROFT')).out('Consent')" +
                      ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("1", laraPrivacyNotices, "Lara has one Privacy notices.");

      String getObjectDataSourceName =
              App.executor.eval("App.g.V().has('Object.Privacy_Notice.Form_Id', eq('4')).in('Has_Privacy_Notice')" +
                      ".in('Consent').out('Has_Ingestion_Event').in('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".properties('Object.Data_Source.Name').value().next().toString()").get().toString();
      assertEquals("SHAREPOINT/CONSENT", getObjectDataSourceName, "Data Source Name.");

      String getPersonNaturalCustomerID =
              App.executor.eval("App.g.V().has('Event.Consent.Form_Id', eq('1')).in('Consent')" +
                      ".properties('Person.Natural.Customer_ID').value().next().toString()").get().toString();
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
      jsonTestUtil("non-official-pv-extract-sharepoint-consentimentos.json",
              "$.queryResp[*].fields", "sharepoint_consents");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Consent'))" +
                      ".count().next().toString()").get().toString();

      String countPersonNatural =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Consent'))" +
                      ".in('Has_Ingestion_Event').has('Metadata.Type.Person.Natural', eq('Person.Natural'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countConsentEvents =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Consent'))" +
                      ".in('Has_Ingestion_Event').has('Metadata.Type.Person.Natural', eq('Person.Natural'))" +
                      ".out('Consent').dedup().count().next().toString()").get().toString();

//    Test for duplicate data --------------------------------------------------------------------------------------

      jsonTestUtil("non-official-pv-extract-sharepoint-consentimentos2.json",
              "$.queryResp[*].fields", "sharepoint_consents");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Consent'))" +
                      ".count().next().toString()").get().toString();

      String countPersonNaturalAgain =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Consent'))" +
                      ".in('Has_Ingestion_Event').has('Metadata.Type.Person.Natural', eq('Person.Natural'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countConsentEventsAgain =
              App.executor.eval("App.g.V().has('Event.Ingestion.Type', eq('Consent'))" +
                      ".in('Has_Ingestion_Event').has('Metadata.Type.Person.Natural', eq('Person.Natural'))" +
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
      jsonTestUtil("non-official-pv-extract-sharepoint-aviso-de-privacidade.json",
              "$.queryResp[*].fields", "sharepoint_privacy_notice");

      jsonTestUtil("non-official-pv-extract-sharepoint-consentimentos.json",
              "$.queryResp[*].fields", "sharepoint_consents");

      String consentDataSourceName = "SHAREPOINT/CONSENT";

      String privacyNoticeDataSourceName =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq(\"" + consentDataSourceName + "\"))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event').out('Consent')" +
                      ".out('Has_Privacy_Notice').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event').values('Object.Data_Source.Name').next().toString()").get().toString();
      assertEquals("SHAREPOINT/PRIVACY-NOTICE", privacyNoticeDataSourceName, "Data Source Name for Privacy Notice POLE.");

      String getPersonNaturalByPrivacyNotice =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq(\"" + privacyNoticeDataSourceName + "\"))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".has('Object.Privacy_Notice.Form_Id', eq('4')).in('Has_Privacy_Notice')" +
                      ".has('Event.Consent.Customer_ID', eq('456'))" +
                      ".in('Consent').has('Person.Natural.Customer_ID', eq('456'))" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("BOB NAKAMURA", getPersonNaturalByPrivacyNotice,
              "This Privacy Notice POLE Graph path leads to Bob Nakamura");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}