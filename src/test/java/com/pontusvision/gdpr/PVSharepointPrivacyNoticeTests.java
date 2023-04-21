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
      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");
      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-aviso-de-privacidade.json",
              "$.queryResp[*].fields", "sharepoint_privacy_notice");

//      String getPrivacyNoticeDescription =
//              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('1'))" +
//                      ".properties('Object_Privacy_Notice_Description').value()" +
//                      ".next().toString()").get().toString();

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Privacy_Notice_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"1\"\n" +
                      "  }\n" +
                      "]", "Object_Privacy_Notice",
              new String[]{"Object_Privacy_Notice_Description"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Privacy_Notice_Description\":\"O Titular foi avisado e concordou com os termos\""));

//      String getPrivacyNoticeCreateDate =
//              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('5'))" +
//                      ".properties('Object_Privacy_Notice_Metadata_Create_Date').value()" +
//                      ".next().toString()").get().toString();
//      getPrivacyNoticeCreateDate = getPrivacyNoticeCreateDate.replaceAll("... 2022", "GMT 2022");

      reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Privacy_Notice_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"5\"\n" +
                      "  }\n" +
                      "]", "Object_Privacy_Notice",
              new String[]{"Object_Privacy_Notice_Metadata_Create_Date"});
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Privacy_Notice_Metadata_Create_Date\":\"Wed Jan 05 19:18:05 UTC 2022\""));

//      String getObjectDataSourceName =
//              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id', eq('4')).out('Has_Ingestion_Event')" +
//                      ".in('Has_Ingestion_Event').in('Has_Ingestion_Event').properties('Object_Data_Source_Name').value()" +
//                      ".next().toString()").get().toString();

      String svFormId = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Privacy_Notice_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"4\"\n" +
                      "  }\n" +
                      "]", "Object_Privacy_Notice",
              new String[]{"Object_Privacy_Notice_Form_Id'"});

//    from Object_Privacy_Notice to Event_Ingestion
      String svFormId2 = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Event_Ingestion_Type\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"Privacy Notice\"\n" +
                      "  }\n" +
                      "]", "Event_Ingestion",
              new String[]{"Event_Ingestion_Type'"}, "hasNeighbourId:" + svFormId);

//    from Event_Ingestion to Event_Group_Ingestion
      String svFormId3 = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Event_Group_Ingestion_Type\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"sharepoint/privacy-notice\"\n" +
                      "  }\n" +
                      "]", "Event_Group_Ingestion",
              new String[]{"Event_Group_Ingestion_Type'"}, "hasNeighbourId:" + svFormId2);

//    and finally from Event_Group_Ingestion to Object_Data_Source
      reply = gridWrapper(null, "Object_Data_Source", new String[]{"Object_Data_Source_Name"}, "hasNeighbourId:" + svFormId3);
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Data_Source_Name\":\"SHAREPOINT/PRIVACY-NOTICE\""));

//    testing new connection from Object_Data_Procedures ------ Has_Privacy_Notice -------> Object_Privacy_Notice

      String privNoticeRid = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Privacy_Notice_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"5\"\n" +
                      "  }\n" +
                      "]", "Object_Privacy_Notice",
              new String[]{"Object_Privacy_Notice_Form_Id'"});

      reply = gridWrapper(null, "Object_Data_Procedures", new String[]{"Object_Data_Procedures_Name"}, "hasNeighbourId:" + privNoticeRid, 0L, 10L, "Object_Data_Procedures_Name", "+asc");

      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Procedures_Name\":\"Contratos Estratégicos\""));
      assertTrue(reply.getRecords()[1].contains("\"Object_Data_Procedures_Name\":\"Gestão de Rede de Distribuidores\""));
      assertTrue(reply.getRecords()[2].contains("\"Object_Data_Procedures_Name\":\"Gestão de ferramenta gerencial (PowerBI)\""));
      assertTrue(reply.getRecords()[3].contains("\"Object_Data_Procedures_Name\":\"Prestadores de Serviços e Fornecedores\""));
      assertTrue(reply.getRecords()[4].contains("\"Object_Data_Procedures_Name\":\"Prontuário do colaborador\""));
      assertTrue(reply.getRecords()[5].contains("\"Object_Data_Procedures_Name\":\"Termos de Confidencialidade \""));
      assertEquals(6, reply.getTotalAvailable(), "This Privacy Notice has 6 RoPAs attaches to it.");

    } catch (Exception e) {
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