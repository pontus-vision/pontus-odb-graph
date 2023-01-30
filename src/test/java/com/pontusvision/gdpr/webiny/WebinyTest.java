package com.pontusvision.gdpr.webiny;

import com.pontusvision.gdpr.*;
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */

@TestClassOrder(AnnotationTestsOrderer.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassesOrder(36)
//@RunWith(JUnitPlatform.class)
public class WebinyTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001WebinyMapeamento() throws InterruptedException {

    jsonTestUtil("webiny/webiny-mapeamento-de-processos.json", "$.data.listMapeamentoDeProcessos.data[*]", "webiny_ropa");

    try {

      String mapeamentoRid = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Data_Procedures_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63c6f874320a910008b4e5b5#0002\"\n" +
                      "  }\n" +
                      "]", "Object_Data_Procedures",
              new String[]{"Object_Data_Procedures_Form_Id"});

      RecordReply reply = gridWrapper(null, "Object_Lawful_Basis", new String[]{"Object_Lawful_Basis_Description"},
              "hasNeighbourId:" + mapeamentoRid, 0L, 2L, "Object_Lawful_Basis_Description", "+asc");

      assertTrue(reply.getRecords()[0].contains("\"Object_Lawful_Basis_Description\":\"CONSENTIMENTO\""));
      assertTrue(reply.getRecords()[1].contains("\"Object_Lawful_Basis_Description\":\"LEGÍTIMO INTERESSE\""));

//      TODO: test data_source and mapeamentos
//      {"id":"#571:0","Object_Data_Source_Form_Id":"d=63c978dfa85ed20008efe81b#0001"}

      reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Data_Procedures_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63c6f874320a910008b4e5b5#0002\"\n" +
                      "  }\n" +
                      "]", "Object_Data_Procedures",
              new String[]{"Object_Data_Procedures_ID", "Object_Data_Procedures_Business_Area_Responsible",
                      "Object_Data_Procedures_Macro_Process_Name", "Object_Data_Procedures_Description",
                      "Object_Data_Procedures_Products_And_Services", "Object_Data_Procedures_Info_Collected",
                      "Object_Data_Procedures_Type_Of_Natural_Person", "Object_Data_Procedures_Lawful_Basis_Justification"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Data_Procedures_Products_And_Services\":\"carro de luxo\""), "Product attached to this RoPA");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_ID\":\"ROPA 1\""), "Name (or ID) of this process");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Lawful_Basis_Justification\":\"justificativa 1\""), "Justification for this process");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Business_Area_Responsible\":\"[TI]\""), "Department responsible for this process");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Description\":\"finalidade 1\""));
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Type_Of_Natural_Person\":\"[Cliente]\""), "Type of People at this process");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Macro_Process_Name\":\"processo 1\""));
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Info_Collected\":\"[Nome Completo, CNH, Local de nascimento" +
              ", Despesas, Detalhes Pessoais, Hábitos, Caráter, Familiares ou membros da família, Nome de usuário, Clubes" +
              ", Parcerias, Condenações, Bens ou serviços emprestados, Custos, Tempo de habitação, Histórico escolar" +
              ", Grau de formação, Matrícula, Data de admissão, Nome da empresa, INSS, Fotografia, Impressão digital" +
              ", Dados que revelam origem racial ou ética, outros dados 1]\""), "Type of Data/Information that where collected in this RoPA process");

      reply = gridWrapper(null, "Object_Sensitive_Data", new String[]{"Object_Sensitive_Data_Description"},
              "hasNeighbourId:" + mapeamentoRid, 0L, 25L, "Object_Sensitive_Data_Description", "+asc");

      assertEquals(25,reply.getTotalAvailable(), "");
//    some of the personal/sensitive data linked to the mapeamento:
      assertTrue(reply.getRecords()[1].contains("\"Object_Sensitive_Data_Description\":\"BENS OU SERVIÇOS EMPRESTADOS\""));
      assertTrue(reply.getRecords()[7].contains("\"Object_Sensitive_Data_Description\":\"DADOS QUE REVELAM ORIGEM RACIAL OU ÉTICA\""));
      assertTrue(reply.getRecords()[14].contains("\"Object_Sensitive_Data_Description\":\"HISTÓRICO ESCOLAR\""));
      assertTrue(reply.getRecords()[18].contains("\"Object_Sensitive_Data_Description\":\"LOCAL DE NASCIMENTO\""));
      assertTrue(reply.getRecords()[24].contains("\"Object_Sensitive_Data_Description\":\"TEMPO DE HABITAÇÃO\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00002WebinyAcoesJudiciais() throws InterruptedException {

//    jsonTestUtil("webiny/webiny-mapeamento-de-processos.json", "$.data.listMapeamentoDeProcessos.data[*]", "webiny_ropa");
    jsonTestUtil("webiny/webiny-acoes-judiciais.json", "$.data.listAcoesJudiciaisPpds.data[*]", "webiny_legal_actions");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Legal_Actions_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63c9480f9ac28f000901f8eb#0001\"\n" +
                      "  }\n" +
                      "]", "Object_Legal_Actions",
              new String[]{"Object_Legal_Actions_Name", "Object_Legal_Actions_Description", "Object_Legal_Actions_Date", "Object_Legal_Actions_Details"});
      String replyStr = reply.getRecords()[0];

      assertEquals(1, reply.getTotalAvailable(), "Expecting 1 record to come back");
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Date\":\"Thu Dec 01 01:01:01 UTC 2022\""), "");
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Description\":\"Ação judicial contra o desmatamento da Amazônia.\""), "");
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Details\":\"nada bom\""), "");
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Name\":\"JUR T33DF\""), "");

      reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Legal_Actions_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63c947d79ac28f000901f8ea#0001\"\n" +
                      "  }\n" +
                      "]", "Object_Legal_Actions",
              new String[]{"Object_Legal_Actions_Name", "Object_Legal_Actions_Description", "Object_Legal_Actions_Date", "Object_Legal_Actions_Details"});
      replyStr = reply.getRecords()[0];

      assertEquals(1, reply.getTotalAvailable(), "Expecting 1 record to come back");
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Date\":\"Sun Mar 21 01:01:01 UTC 2021\""), "");
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Description\":\"ato 123\""), "");
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Details\":\"muito bom\""), "");
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Name\":\"ATO 123\""), "");

      // #TODO: do it in gridWrapper !!!
      OGremlinResultSet resSet = App.graph.executeSql(
              "SELECT count(Object_Legal_Actions_Form_Id) as ct " +
                      "FROM Object_Legal_Actions " +
                      "WHERE Object_Legal_Actions_Form_Id LIKE '%#0001'", Collections.EMPTY_MAP);

      Long countObjectLegalActions = resSet.iterator().next().getRawResult().getProperty("ct");

      assertEquals(2, countObjectLegalActions, "There are 2 Webiny Object_Legal_Actions");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00003WebinyComunicacoesPpd() throws InterruptedException {

    jsonTestUtil("webiny/webiny-comunicacoes.json", "$.data.listComunicacoesPpds.data[*]", "webiny_privacy_docs");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Privacy_Docs_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63cac3f1b788960008872ee4#0001\"\n" +
                      "  }\n" +
                      "]", "Object_Privacy_Docs",
              new String[]{"Object_Privacy_Docs_Title", "Object_Privacy_Docs_Date", "Object_Privacy_Docs_Description"});
      String replyStr = reply.getRecords()[0];


      assertTrue(replyStr.contains("\"Object_Privacy_Docs_Title\":\"COMUNICAÇÃO 2\""), "Title for this Privacy document");
      assertTrue(replyStr.contains("\"Object_Privacy_Docs_Description\":\"Reunião com DPO\""), "Description for this priv doc");
       assertTrue(replyStr.contains("\"Object_Privacy_Docs_Date\":\"Tue Jul 12 01:01:01 UTC 2022\""), "Date this event took place");

      reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Privacy_Docs_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63cac3ccb788960008872ee3#0001\"\n" +
                      "  }\n" +
                      "]", "Object_Privacy_Docs",
              new String[]{"Object_Privacy_Docs_Title", "Object_Privacy_Docs_Date", "Object_Privacy_Docs_Description"});
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Privacy_Docs_Title\":\"COMUNICAÇÃO 1\""), "Title for this Privacy document");
      assertTrue(replyStr.contains("\"Object_Privacy_Docs_Description\":\"palestra introdução LGPD\""), "Description for this priv doc");
      assertTrue(replyStr.contains("\"Object_Privacy_Docs_Date\":\"Fri Oct 09 01:01:01 UTC 2020\""), "Date this event took place");

      // #TODO: do it in gridWrapper !!!
      OGremlinResultSet resSet = App.graph.executeSql(
              "SELECT count(Object_Privacy_Docs_Form_Id) as ct " +
                      "FROM Object_Privacy_Docs " +
                      "WHERE Object_Privacy_Docs_Form_Id LIKE '%#0001'", Collections.EMPTY_MAP);

      Long countObjectPrivacyDocs = resSet.iterator().next().getRawResult().getProperty("ct");

      assertEquals(2, countObjectPrivacyDocs, "There are 2 registries for Object_Privacy_Docs Webiny");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00004WebinyReunioesPpd() throws InterruptedException {

//  jsonTestUtil("webiny/webiny-titulares.json", "$.data.listTitulares.data[*]", "webiny_titulares");
    jsonTestUtil("webiny/webiny-reunioes.json", "$.data.listReunioesPpds.data[*]", "webiny_meetings");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Event_Meeting_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63ce85b5df67ec0008f29876#0001\"\n" +
                      "  }\n" +
                      "]", "Event_Meeting",
              new String[]{"Event_Meeting_Name", "Event_Meeting_Title", "Event_Meeting_Date", "Event_Meeting_Discussed_Topics"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Event_Meeting_Name\":\"REUNIÃO 1\""));
      assertTrue(replyStr.contains("\"Event_Meeting_Date\":\"Mon Jan 16 01:01:01 UTC 2023\""));
      assertTrue(replyStr.contains("\"Event_Meeting_Discussed_Topics\":\"O QUE SERÁ DA LGPD NO ANO DE 2023\""));

      String eventMeeting = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Event_Meeting_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63ce85b5df67ec0008f29876#0001\"\n" +
                      "  }\n" +
                      "]", "Event_Meeting",
              new String[]{"Event_Meeting_Form_Id"});

      reply = gridWrapper(null, "Person_Natural", new String[]{"Person_Natural_Customer_ID"},
              "hasNeighbourId:" + eventMeeting, 0L, 3L, "Person_Natural_Customer_ID", "+asc");

      assertTrue(reply.getRecords()[0].contains("\"Person_Natural_Customer_ID\":\"12309732112\""));
      assertTrue(reply.getRecords()[1].contains("\"Person_Natural_Customer_ID\":\"13245623451\""));
      assertTrue(reply.getRecords()[2].contains("\"Person_Natural_Customer_ID\":\"19439809467\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00005WebinyAvisoDePrivacidade() throws InterruptedException {

//    jsonTestUtil("webiny/webiny-mapeamento-de-processos.json", "$.data.listMapeamentoDeProcessos.data[*]", "webiny_ropa");
//    jsonTestUtil("webiny/webiny-consentimento.json", "$.data.listConsentimento.data[*]", "webiny_consentimento");
    jsonTestUtil("webiny/webiny-avisos.json", "$.data.listAvisoDePrivacidades.data[*]", "webiny_privacy_notice");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Privacy_Notice_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63cedccce37f880008086531#0002\"\n" +
                      "  }\n" +
                      "]", "Object_Privacy_Notice",
              new String[]{"Object_Privacy_Notice_Name", "Object_Privacy_Notice_Description", "Object_Privacy_Notice_Delivery_Date"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Privacy_Notice_Name\":\"AVISO 1\""));
      assertTrue(replyStr.contains("\"Object_Privacy_Notice_Description\":\"aviso 1\""));
      assertTrue(replyStr.contains("\"Object_Privacy_Notice_Delivery_Date\":\"Sat Oct 10 01:01:01 UTC 2020\""));

      String privacyNotice = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Privacy_Notice_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63cedccce37f880008086531#0002\"\n" +
                      "  }\n" +
                      "]", "Object_Privacy_Notice",
              new String[]{"Object_Privacy_Notice_Form_Id"});

      reply = gridWrapper(null, "Object_Data_Procedures", new String[]{"Object_Data_Procedures_Form_Id"},
              "hasNeighbourId:" + privacyNotice);
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Data_Procedures_Form_Id\":\"=63c6f874320a910008b4e5b5#0005\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }


}
