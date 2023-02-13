package com.pontusvision.gdpr;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(14)

public class PVSharepointTests extends AppTest {
  /**
   * Create the test0000 case
   *
   */

  @Test
  public void test00001SharepointPAndPDCommitteeMeetings() throws InterruptedException {
    try {

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-p-and-pd-committee-meetings.json",
              "$.queryResp[*].fields", "sharepoint_meetings");

      String topicsDiscussedMeeting1 =
              App.executor.eval("App.g.V().has('Event_Meeting_Form_Id', eq('1'))" +
                      ".properties('Event_Meeting_Discussed_Topics').value()" +
                      ".next().toString()").get().toString();
      assertEquals("LGPD NA EMPRESA; O IMPACTO DA NOVA LEI LGPD", topicsDiscussedMeeting1,
              "Topics discussed at this meeting");

      String eventMetingDate =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SHAREPOINT/P-AND-PD-COMMITTEE-MEETINGS'))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event').has('Event_Meeting_Form_Id', eq('2'))" +
                      ".values('Event_Meeting_Date').next().toString()").get().toString();
      assertEquals(dtfmt.parse("Tue Dec 07 01:01:01 GMT 2021"), dtfmt.parse(eventMetingDate),
              "P&PD Meeting #2 date.");

      String getObjectDataSourceName =
              App.executor.eval("App.g.V().has('Event_Meeting_Form_Id', eq('3')).in('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event').in('Has_Ingestion_Event').values('Object_Data_Source_Name')" +
                      ".next().toString()").get().toString();
      assertEquals("SHAREPOINT/P-AND-PD-COMMITTEE-MEETINGS", getObjectDataSourceName, "Data Source Name.");

//      String meeting3Participants =
//              App.executor.eval("App.g.V().has('Event_Meeting_Form_Id', eq('3')).values('Event_Meeting_Participants')" +
//                      ".next().toString()").get().toString();
//      assertEquals("MÔNICA; CEBOLINHA; MAGALI; CASCÃO", meeting3Participants, "The participants of Meeting #3.");

//    testing new edge between Object_Email ------- Is_Participant ------- > Event_Meeting
      String svMeetingRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Event_Meeting_Form_Id\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"3\"\n" +
          "  }\n" +
          "]", "Event_Meeting",
        new String[]{"Event_Meeting_Form_Id"});

      RecordReply reply = gridWrapper(null, "Object_Email_Address", new String[]{"Object_Email_Address_Email"},
        "hasNeighbourId:" + svMeetingRid, 0L, 10L, "Object_Email_Address_Email", "+asc");

//    The number of participants should be:
      assertEquals(4, reply.getTotalAvailable());

//    The participant's email's are:
      assertTrue(reply.getRecords()[0].contains("\"Object_Email_Address_Email\":\"cascao@gibi.com\""));
      assertTrue(reply.getRecords()[1].contains("\"Object_Email_Address_Email\":\"cebolinha@gibi.com\""));
      assertTrue(reply.getRecords()[2].contains("\"Object_Email_Address_Email\":\"magali@gibi.com\""));
      assertTrue(reply.getRecords()[3].contains("\"Object_Email_Address_Email\":\"monica@gibi.com\""));

// ---------------------------------------------------------------------------------------------------------------------

      String meetingSubject =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings')).out('Has_Ingestion_Event')" +
                      ".has('Event_Meeting_Form_Id', eq('2')).values('Event_Meeting_Title').next().toString()").get().toString();
      assertEquals("COMO SALVAR O UNIVERSO?", meetingSubject, "Meeting's #3 Title/Subject.");

    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using sharepoint_meetings POLE
  @Test
  public void test00002UpsertSharepointPAndPDCommitteeMeetings() throws InterruptedException {
    try {

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-p-and-pd-committee-meetings.json",
              "$.queryResp[*].fields", "sharepoint_meetings");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings'))" +
                      ".count().next().toString()").get().toString();

      String countEventMeetings =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Event_Meeting', eq('Event_Meeting'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-p-and-pd-committee-meetings2.json",
              "$.queryResp[*].fields", "sharepoint_meetings");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countEventMeetingsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Event_Meeting', eq('Event_Meeting'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the P&PD Committee Meetings Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countEventMeetings) == Integer.parseInt(countEventMeetingsAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00003SharepointContratos() throws InterruptedException {

    jsonTestUtil("sharepoint/pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields", "sharepoint_fontes_de_dados");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json", "$.queryResp[*].fields", "sharepoint_mapeamentos");
    jsonTestUtil("totvs/totvs-sa2-real.json", "$.objs", "totvs_protheus_sa2_fornecedor");
    jsonTestUtil("sharepoint/devtools-extract-sharepoint-contracts.json", "$.queryResp[*].fields", "sharepoint_contracts");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Contract_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"1\"\n" +
                      "  }\n" +
                      "]", "Object_Contract",
              new String[]{"Object_Contract_Short_Description", "Object_Contract_Tranfer_Intl", "Object_Contract_Has_Minors_Data", "Object_Contract_Expiry"});
      String replyStr = reply.getRecords()[0];

      String contractRid = JsonParser.parseString(replyStr).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Object_Contract_Short_Description\":\"POLE-CONTRATOS-2\""));
      assertTrue(replyStr.contains("\"Object_Contract_Has_Minors_Data\":\"True\""));
      assertTrue(replyStr.contains("\"Object_Contract_Tranfer_Intl\":\"[II - QUANDO O CONTROLADOR OFERECER E COMPROVAR GARANTIAS DE CUMPRIMENTO DOS PRINCÍPIOS, DOS DIREITOS DO TITULAR E DO REGIME DE PROTEÇÃO DE DADOS PREVISTOS NA LGPD, IV - QUANDO A TRANSFERÊNCIA FOR NECESSÁRIA PARA A PROTEÇÃO DA VIDA OU DA INCOLUMIDADE FÍSICA DO TITULAR OU DE TERCEIRO]\""));
      assertTrue(replyStr.contains("\"Object_Contract_Expiry\":\"Mon Feb 02 08:00:00 UTC 2026\""));

      reply = gridWrapper(null, "Object_Data_Source", new String[]{"Object_Data_Source_Name", "Object_Data_Source_Engine", "Object_Data_Source_Description"},
              "hasNeighbourId:" + contractRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Source_Engine\":\"PBR\""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Source_Name\":\"ERP-FUNCIONÁRIO\""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Source_Description\":\"SISTEMA DE ERP PARA CADASTRO DOS COLABORADORES\"}"));

      reply = gridWrapper(null, "Object_Data_Procedures",new String[]{"Object_Data_Procedures_Business_Area_Responsible",
              "Object_Data_Procedures_Why_Is_It_Collected", "Object_Data_Procedures_Name", "Object_Data_Procedures_Info_Collected"},
              "hasNeighbourId:" + contractRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Procedures_Why_Is_It_Collected\":\"Necessário para gestão de termo de confidencialidade formalizados com clientes/fornecedores\""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Procedures_Name\":\"Termos de Confidencialidade \""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Procedures_Business_Area_Responsible\":\"Administrativo-Financeiro - 32\""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Procedures_Info_Collected\":\"[Nome, CPF, RG, Endereço, E-mail]\""));

      reply = gridWrapper(null, "Person_Organisation", new String[]{"Person_Organisation_Registration_Number"},
              "hasNeighbourId:" + contractRid, 0L, 4L, "Person_Organisation_Registration_Number", "+asc");

      assertEquals(4, reply.getTotalAvailable(), "This contract has 4 Person_Orgs attached");
      assertTrue(reply.getRecords()[0].contains("\"Person_Organisation_Registration_Number\":\"19854875000145\""));
      assertTrue(reply.getRecords()[1].contains("\"Person_Organisation_Registration_Number\":\"49034782000123\""));
      assertTrue(reply.getRecords()[2].contains("\"Person_Organisation_Registration_Number\":\"78675984000165\""));
      assertTrue(reply.getRecords()[3].contains("\"Person_Organisation_Registration_Number\":\"89894673000152\""));

      reply = gridWrapper(null, "Person_Natural", new String[]{"Person_Natural_Customer_ID"},
              "hasNeighbourId:" + contractRid);

      assertTrue(reply.getRecords()[0].contains("\"Person_Natural_Customer_ID\":\"01201405628\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

}