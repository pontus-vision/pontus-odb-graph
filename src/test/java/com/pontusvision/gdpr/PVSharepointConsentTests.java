package com.pontusvision.gdpr;

import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;
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
  public void test00001SharepointConsents() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos.json",
              "$.queryResp[*].fields", "sharepoint_consents");

      String lakshmiPrivacyNotices =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('LAKSHMI')).out('Consent')" +
                      ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("0", lakshmiPrivacyNotices, "Lakshmi has NO Privacy notices.");

      String janosPrivacyNotices =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('JANOS GABOR')).out('Consent')" +
                      ".out('Has_Privacy_Notice').count().next().toString()").get().toString();
      assertEquals("2", janosPrivacyNotices, "Janos has 2 Privacy notices.");

      String janosDataProcedures =
          App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('JANOS GABOR'))" +
              ".out('Consent').as('eventconsent')" +
              ".out('Consent').dedup().as('dataprocs')" +
              ".count().next().toString()").get().toString();
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

      // Testing new prop Event_Consent_Description [pontus-odb-graph v.1.15.90]
        String getLakshmiEventConsentDescription =
                App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Consent')).as('event-ingestion')" +
                        ".in('Has_Ingestion_Event').has('Person_Natural_Full_Name', eq('LAKSHMI')).as('person')" +
                        ".out('Consent').has('Event_Consent_Form_Id', eq('7')).as('consent')" +
                        ".properties('Event_Consent_Description').value().next().toString()").get().toString();
        assertEquals("stuvw", getLakshmiEventConsentDescription, "Consent Description");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using sharepoint_consents POLE
  @Test
  public void test00002UpsertSharepointConsents() throws InterruptedException {
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

//  @Test
//  public void test00003SharepointConsentPlusPrivacyNotice() throws InterruptedException {
//    try {
//      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-aviso-de-privacidade.json",
//              "$.queryResp[*].fields", "sharepoint_privacy_notice");
//
//      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos.json",
//              "$.queryResp[*].fields", "sharepoint_consents");
//
//      String consentDataSourceName = "SHAREPOINT/CONSENT";
//
//      String privacyNoticeDataSourceName =
//              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq(\"" + consentDataSourceName + "\"))" +
//                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event').out('Consent')" +
//                      ".out('Has_Privacy_Notice').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
//                      ".in('Has_Ingestion_Event').values('Object_Data_Source_Name').next().toString()").get().toString();
//      assertEquals("SHAREPOINT/PRIVACY-NOTICE", privacyNoticeDataSourceName, "Data Source Name for Privacy Notice POLE.");
//
//      String getPersonNaturalByPrivacyNotice =
//              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq(\"" + privacyNoticeDataSourceName + "\"))" +
//                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
//                      ".has('Object_Privacy_Notice_Form_Id', eq('4')).in('Has_Privacy_Notice')" +
//                      ".has('Event_Consent_Customer_ID', eq('456'))" +
//                      ".in('Consent').has('Person_Natural_Customer_ID', eq('456'))" +
//                      ".values('Person_Natural_Full_Name').next().toString()").get().toString();
//      assertEquals("BOB NAKAMURA", getPersonNaturalByPrivacyNotice,
//              "This Privacy Notice POLE Graph path leads to Bob Nakamura");
//
//    } catch (ExecutionException e) {
//      e.printStackTrace();
//      assertNull(e);
//    }
//
//  }

  @Test
  public void test00004ConsentsSameNum() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/pv-extract-sharepoint-consentimentos-same-num.json",
              "$.queryResp[*].fields", "sharepoint_consents");

      OResultSet resultSet =
              App.graph.executeSql("SELECT count(*) as ct FROM Person_Natural WHERE Person_Natural_Customer_ID = '12345'", Collections.EMPTY_MAP).getRawResultSet();
      String countPersonNatural  = resultSet.next().getProperty("ct").toString();
      resultSet.close();
      assertEquals("1", countPersonNatural,
              "There should be only one Person Natural in the graph, because Customer_ID, which is the main key, is same number.");

//      TODO: check excludeFromUpdate, it doesn't seem to be working.
      String getPersonNaturalCustomerID =
              App.executor.eval("App.g.V().has('Event_Group_Ingestion_Type', eq('sharepoint/consent')).as('group_ingestion')" +
                      ".out('Has_Ingestion_Event').has('Event_Ingestion_Type', eq('Consent')).as('event_ingestion')" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
//          Person_Natural_Full_Name = MODIFIED because it's the last registry and excludeFromUpdate seems to not be working.
                      ".has('Person_Natural_Full_Name', eq('MODIFIED'))" +
                      ".values('Person_Natural_Customer_ID').next().toString()").get().toString();
      assertEquals("12345", getPersonNaturalCustomerID, "MODIFIED's Customer ID");

      // Count Event_Consent SQL
        resultSet =
                App.graph.executeSql("SELECT count(*) as ct FROM Event_Consent WHERE Event_Consent_Customer_ID = '12345'", Collections.EMPTY_MAP).getRawResultSet();
        String countEventConsent  = resultSet.next().getProperty("ct").toString();
        resultSet.close();
        assertEquals("8", countEventConsent, "counting Event_Consent");

      // test empty Event_Consent_Customer_ID with SQL
        resultSet =
                App.graph.executeSql("SELECT count(*) as ct " +
                                "FROM Event_Consent " +
                                "WHERE Event_Consent_Customer_ID = ''",
                        Collections.EMPTY_MAP).getRawResultSet();

        String countEventConsentCustomerIDEmpty  = resultSet.next().getProperty("ct").toString();
        resultSet.close();
        assertEquals("0", countEventConsentCustomerIDEmpty,"Customer_ID is not empty");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00005ConsentsLetters() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/pv-extract-sharepoint-consentimentos-letters.json",
        "$.queryResp[*].fields", "sharepoint_consents");

      OResultSet resultSet =
        App.graph.executeSql("SELECT count(*) as ct FROM Person_Natural WHERE Person_Natural_Customer_ID = ''", Collections.EMPTY_MAP).getRawResultSet();
      String countPersonNatural  = resultSet.next().getProperty("ct").toString();
      resultSet.close();
      assertEquals("0", countPersonNatural,
        "There should no Person_Natural in the graph, because Customer_ID, which is the main key, is empty. It doesn't satisfy the condition.");

      String getEventIngestionType =
        App.executor.eval("App.g.V().has('Event_Group_Ingestion_Type', eq('sharepoint/consent')).as('group_ingestion')" +
          ".out('Has_Ingestion_Event').has('Event_Ingestion_Type', eq('Consent')).as('event_ingestion')" +
          ".values('Event_Ingestion_Type').next().toString()").get().toString();
      assertEquals("Consent", getEventIngestionType,"Event_Ingestion_Type should be Consent");

      // Count Event_Consent SQL
        resultSet =
                App.graph.executeSql("SELECT count(*) as ct FROM Event_Consent WHERE Event_Consent_Customer_ID = ''", Collections.EMPTY_MAP).getRawResultSet();
        String countEventConsent  = resultSet.next().getProperty("ct").toString();
        resultSet.close();
        assertEquals("8", countEventConsent, "counting Event_Consent");
//        Even though there's only one Person_Natural, there are 8 Event_Consent because Event_Consents are keyed by Form_Id,
//      not by Customer_ID. In this example, there are 8 different Form_Id's, but only one Customer_ID.


      // test empty Event_Consent_Customer_ID with SQL
        resultSet =
                App.graph.executeSql("SELECT count(*) as ct " +
                                "FROM Event_Consent " +
                                "WHERE Event_Consent_Customer_ID = ''",
                        Collections.EMPTY_MAP).getRawResultSet();

        String countEventConsentCustomerIDEmpty  = resultSet.next().getProperty("ct").toString();
        resultSet.close();
        assertEquals("8", countEventConsentCustomerIDEmpty,
                "All 8 Event_Consents have empty Customer_ID");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00006NormalizeNames() throws InterruptedException {
    try {
//      jsonTestUtil("totvs/totvs-sa1-real.json", "$.objs", "totvs_protheus_sa1_clientes");
      jsonTestUtil("sharepoint/pv-extract-sharepoint-consentimentos-names.json",
              "$.queryResp[*].fields", "sharepoint_consents");
//
//      String getEventIngestionType =
//        App.executor.eval("App.g.V().has('Event_Group_Ingestion_Type', eq('sharepoint/consent')).as('group_ingestion')" +
//          ".out('Has_Ingestion_Event').has('Event_Ingestion_Type', eq('Consent')).as('event_ingestion')" +
//          ".values('Event_Ingestion_Type').next().toString()").get().toString();
//      assertEquals("Consent", getEventIngestionType,"Event_Ingestion_Type should be Consent");
//
//      String getJanosGaborWithouAccents =
//        App.executor.eval("App.g.V().has('Event_Group_Ingestion_Type', eq('sharepoint/consent')).as('group_ingestion')" +
//          ".out('Has_Ingestion_Event').has('Event_Ingestion_Type', eq('Consent')).as('event_ingestion')" +
//          ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
//          ".has('Person_Natural_Customer_ID', eq('6'))" +
//          ".values('Person_Natural_Full_Name').next().toString()").get().toString();
//      assertEquals("JANOS GABOR", getJanosGaborWithouAccents,
//        "JANOS GABOR should be normalized. Uppercased and without accents.");

      // nao consegue retornar (JavaNullPointerException)
      StringBuffer sb = new StringBuffer();
      OResultSet resultSet =
        App.graph.executeSql("SELECT Person_Natural_Full_Name as name " +
          "FROM Person_Natural WHERE Person_Natural_Full_Name = 'SZSZYAAAAAACEEEEIIIINOOOOOUUUUYAAAAAACEEEEIIIINOOOOOUUUUYY'", Collections.EMPTY_MAP).getRawResultSet();
      while (resultSet.hasNext()) {
        sb.append(resultSet.next().getProperty("name").toString());
      }
      resultSet.close();
      assertEquals("VITORIA JULIA\n" +
        "MARCIA ELIS\n" +
        "SZSZYAAAAAACEEEEIIIINOOOOOUUUUYAAAAAACEEEEIIIINOOOOOUUUUYY\n" +
        "GLORIA FERNANDES\n" +
        "JANOS GABOR\n" +
        "FATIMA BERNARDES" +
        "", sb, "Person_Natural_Full_Name should be normalized");

      //Funciona, porem temos que converter para Java
//      Groovy easier and better version of SQL query

//      StringBuffer sb = new StringBuffer();
//      def results = App.graph.executeSql(
//        """
//        SELECT Person_Natural_Full_Name as name
//        FROM Person_Natural
//        """, [:]);
//      for (def result : results) {
//        String name = result.getProperty('name');
//        sb.append(name).append('\n')
//      }
//      sb.toString();


      String normalizeTest =
        com.pontusvision.utils.NLPCleaner.normalizeName("ŠŽšžŸÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖÙÚÛÜÝàáâãäåçèéêëìíîïðñòóôõöùúûüýÿ");
      assertEquals("SZSZYAAAAAACEEEEIIIINOOOOOUUUUYAAAAAACEEEEIIIINOOOOOUUUUYY", normalizeTest,
        "The resulting string is 2 chars shorter than the original string, because the letters Ð and ð could not be normalized.");

//      String returnEmptyString =
//        com.pontusvision.utils.NLPCleaner.normalizeName("`~^´¨àãâ"); // doesn't work for ` , ~ and ^
//      assertEquals("", returnEmptyString, "Should return empty string");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }


}