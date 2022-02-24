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
@TestClassesOrder(17)

public class PVSharepointPrivacyNoticeTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  @Test
  public void test00001SharepointPrivacyNotice() throws InterruptedException {
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
  public void test00002UpsertSharepointPrivacyNotice() throws InterruptedException {
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

}