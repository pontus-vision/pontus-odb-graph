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
@TestClassesOrder(16)

public class PVSharepointPrivacyDocsTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  @Test
  public void test00001SharepointPrivacyDocs() throws InterruptedException {

    jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-privacy-docs.json", "$.queryResp[*].fields",
            "sharepoint_privacy_docs");

    try {

      String privacyDoc1 =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SHAREPOINT/PRIVACY-DOCS'))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".has('Metadata_Type_Object_Privacy_Docs',eq('Object_Privacy_Docs'))" +
                      ".values('Object_Privacy_Docs_Description').next().toString()").get().toString();
      assertEquals("o que muda com as novas leis de segurança dos dados pessoais dos brasileiros.", privacyDoc1,
              "Privacy Doc 1's Description.");

      String privacyDoc2 =
              App.executor.eval("App.g.V().has('Object_Privacy_Docs_Form_Id',eq('2'))" +
                      ".values('Object_Privacy_Docs_Title').next().toString()").get().toString();
      assertEquals("Cartaz - cuide de sua identidade virtual!", privacyDoc2,"Privacy Doc 2's Title");

      String privacyDoc3 =
              App.executor.eval("App.g.V().has('Object_Privacy_Docs_Description',eq('Introdção a LGPD'))" +
                      ".values('Object_Privacy_Docs_Date').next().toString()").get().toString();
      assertEquals(dtfmt.parse("Mon Jan 10 01:01:01 GMT 2022"), dtfmt.parse(privacyDoc3),
              "Privacy Doc 3's Date");

      String privacyDoc4 =
              App.executor.eval("App.g.V().has('Object_Privacy_Docs_Description',eq('Introdção a LGPD'))" +
                      ".values('Object_Privacy_Docs_URL').next().toString()").get().toString();
      assertEquals("www.wikipedia.com.br",privacyDoc4,"Privacy Doc 4's URL");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using sharepoint_privacy_docs POLE
  @Test
  public void test00002UpsertSharepointPrivacyDocs() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-privacy-docs.json",
              "$.queryResp[*].fields", "sharepoint_privacy_docs");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Privacy Docs'))" +
                      ".count().next().toString()").get().toString();

      String countPrivacyDocs =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Privacy Docs'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Privacy_Docs', eq('Object_Privacy_Docs'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-privacy-docs.json",
              "$.queryResp[*].fields", "sharepoint_privacy_docs");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Privacy Docs'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countPrivacyDocsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('Privacy Docs'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Privacy_Docs', eq('Object_Privacy_Docs'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made the Privacy Docs Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countPrivacyDocs) == Integer.parseInt(countPrivacyDocsAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}