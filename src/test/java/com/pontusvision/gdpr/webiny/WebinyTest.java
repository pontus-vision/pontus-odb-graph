package com.pontusvision.gdpr.webiny;

import com.google.gson.JsonParser;
import com.pontusvision.gdpr.*;
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;

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
                      "Object_Data_Procedures_Name", "Object_Data_Procedures_Description",
                      "Object_Data_Procedures_Products_And_Services", "Object_Data_Procedures_Info_Collected",
                      "Object_Data_Procedures_Type_Of_Natural_Person", "Object_Data_Procedures_Lawful_Basis_Justification"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Data_Procedures_Products_And_Services\":\"CARRO DE LUXO\""), "Product attached to this RoPA");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_ID\":\"ROPA 1\""), "Name (or ID) of this process");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Lawful_Basis_Justification\":\"JUSTIFICATIVA 1\""), "Justification for this process");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Business_Area_Responsible\":\"[TI]\""), "Department responsible for this process");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Description\":\"FINALIDADE 1\""));
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Type_Of_Natural_Person\":\"[Cliente]\""), "Type of People at this process");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Name\":\"PROCESSO 1\""));
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Info_Collected\":\"[Nome Completo, CNH, Local de nascimento" +
              ", Despesas, Detalhes Pessoais, Hábitos, Caráter, Familiares ou membros da família, Nome de usuário, Clubes" +
              ", Parcerias, Condenações, Bens ou serviços emprestados, Custos, Tempo de habitação, Histórico escolar" +
              ", Grau de formação, Matrícula, Data de admissão, Nome da empresa, INSS, Fotografia, Impressão digital" +
              ", Dados que revelam origem racial ou ética, outros dados 1]\""), "Type of Data/Information that where collected in this RoPA process");

      reply = gridWrapper(null, "Object_Sensitive_Data", new String[]{"Object_Sensitive_Data_Description"},
              "hasNeighbourId:" + mapeamentoRid, 0L, 25L, "Object_Sensitive_Data_Description", "+asc");

      assertEquals(25,reply.getTotalAvailable());
//    some of the personal/sensitive data linked to the mapeamento:
      assertTrue(reply.getRecords()[0].contains("\"Object_Sensitive_Data_Description\":\"BENS OU SERVIÇOS EMPRESTADOS\""));
      assertTrue(reply.getRecords()[6].contains("\"Object_Sensitive_Data_Description\":\"DADOS QUE REVELAM ORIGEM RACIAL OU ÉTICA\""));
      assertTrue(reply.getRecords()[13].contains("\"Object_Sensitive_Data_Description\":\"HISTÓRICO ESCOLAR\""));
      assertTrue(reply.getRecords()[17].contains("\"Object_Sensitive_Data_Description\":\"LOCAL DE NASCIMENTO\""));
      assertTrue(reply.getRecords()[22].contains("\"Object_Sensitive_Data_Description\":\"OUTROS DADOS 1\""));

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
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Date\":\"Thu Dec 01 01:01:01 UTC 2022\""));
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Description\":\"JUR T33DF\""));
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Details\":\"AÇÃO JUDICIAL CONTRA O DESMATAMENTO DA AMAZÔNIA.\""));

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
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Date\":\"Sun Mar 21 01:01:01 UTC 2021\""));
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Description\":\"ATO 123\""));
      assertTrue(replyStr.contains("\"Object_Legal_Actions_Details\":\"ATO 123\""));

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
      assertTrue(replyStr.contains("\"Object_Privacy_Docs_Description\":\"REUNIÃO COM DPO\""), "Description for this priv doc");
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
      assertTrue(replyStr.contains("\"Object_Privacy_Docs_Description\":\"PALESTRA INTRODUÇÃO LGPD\""), "Description for this priv doc");
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
      assertTrue(replyStr.contains("\"Object_Privacy_Notice_Description\":\"AVISO 1\""));
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

      assertTrue(replyStr.contains("\"Object_Data_Procedures_Form_Id\":\"63c6f874320a910008b4e5b5#0005\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00006WebinyFontesDeDados() throws InterruptedException {

    jsonTestUtil("webiny/webiny-fontes.json", "$.data.listFontesDeDados.data[*]", "webiny_data_source");

    try {

//      ----------------------- Object_Data_Source fontes_de_dados ----------------------------------------

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Data_Source_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63c978dfa85ed20008efe81b#0002\"\n" +
                      "  }\n" +
                      "]", "Object_Data_Source",
              new String[]{"Object_Data_Source_Name", "Object_Data_Source_Description", "Object_Data_Source_Engine", "Object_Data_Source_Type", "Object_Data_Source_Domain"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Data_Source_Type\":\"SUBSISTEMA 1\""));
      assertTrue(replyStr.contains("\"Object_Data_Source_Engine\":\"SISTEMA 1\""));
      assertTrue(replyStr.contains("\"Object_Data_Source_Domain\":\"MODULO 1\""));
      assertTrue(replyStr.contains("\"Object_Data_Source_Name\":\"FONTE 1\""));
      assertTrue(replyStr.contains("\"Object_Data_Source_Description\":\"FONTE 1\""));

//      ----------------------- Object_Module  ----------------------------------------

      String dataSourceRid = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Data_Source_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63c978dfa85ed20008efe81b#0002\"\n" +
                      "  }\n" +
                      "]", "Object_Data_Source",
              new String[]{"Object_Data_Source_Form_Id"}); // returned val ex: #102:43

      reply = gridWrapper(null, "Object_Module", new String[]{"Object_Module_Name"},
              "hasNeighbourId:" + dataSourceRid);
      replyStr = reply.getRecords()[0];
      assertTrue(replyStr.contains("\"Object_Module_Name\":\"modulo 1\""));
      String moduleRid = JsonParser.parseString(reply.getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

//      ----------------------- Object_Subsystem  ----------------------------------------

      reply = gridWrapper(null, "Object_Subsystem", new String[]{"Object_Subsystem_Name"},
              "hasNeighbourId:" + moduleRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Subsystem_Name\":\"SUBSISTEMA 1\""));
      String subsystemRid = JsonParser.parseString(reply.getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

//      ----------------------- Object_System  ----------------------------------------

      reply = gridWrapper(null, "Object_System", new String[]{"Object_System_Name"},
              "hasNeighbourId:" + subsystemRid);
      assertTrue(reply.getRecords()[0].contains("\"Object_System_Name\":\"SISTEMA 1\""));

//      ----------------------- from Event_Ingestion to Object_Data_Source root  ----------------------

      String eventRid = JsonParser.parseString(gridWrapper(null, "Event_Ingestion", new String[]{"Event_Ingestion_Type"},
              "hasNeighbourId:" + dataSourceRid).getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      String eventGroupRid = JsonParser.parseString(gridWrapper(null, "Event_Group_Ingestion", new String[]{"Event_Group_Ingestion_Type"},
              "hasNeighbourId:" + eventRid).getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      reply = gridWrapper(null, "Object_Data_Source", new String[]{"Object_Data_Source_Name"},
              "hasNeighbourId:" + eventGroupRid);
      replyStr = reply.getRecords()[0];
      assertTrue(replyStr.contains("\"Object_Data_Source_Name\":\"WEBINY/FONTES-DE-DADOS\""));

//      ----------------------- Object_Data_Policy  ----------------------

      reply = gridWrapper(null, "Object_Data_Policy", new String[]{"Object_Data_Policy_Update_Frequency", "Object_Data_Policy_Retention_Period"},
              "hasNeighbourId:" + dataSourceRid);
      replyStr = reply.getRecords()[0];
      assertTrue(replyStr.contains("\"Object_Data_Policy_Update_Frequency\":\"10 meses\""));
      assertTrue(replyStr.contains("\"Object_Data_Policy_Retention_Period\":\"10 meses\""));

//      ----------------------- counting Object_Sensitive_Data  ----------------------

      reply = gridWrapper(null, "Object_Sensitive_Data", new String[]{"Object_Sensitive_Data_Description"},
              "hasNeighbourId:" + dataSourceRid, 0L, 25L, "Object_Sensitive_Data_Description", "+asc");
      assertEquals(20,reply.getTotalAvailable(), "20 personal/sensitive data are attached to this Data Source: ");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00007WebinyConsentimentos() throws InterruptedException {

    jsonTestUtil("webiny/webiny-consentimentos.json", "$.data.listConsentimentos.data[*]", "webiny_consents");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Person_Natural_Customer_ID\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63ce9ae5c064470008b72f83#0002\"\n" +
                      "  }\n" +
                      "]", "Person_Natural",
              new String[]{"Person_Natural_Last_Update_Date"});
      String replyStr = reply.getRecords()[0];

      String titularRid = JsonParser.parseString(replyStr).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Person_Natural_Last_Update_Date\":\"Mon Jan 23 19:16:02 UTC 2023\""));

      reply = gridWrapper(null, "Person_Natural", new String[]{"Person_Natural_Customer_ID"},
              "hasNeighbourId:" + titularRid);
      assertTrue(reply.getRecords()[0].contains("\"Person_Natural_Customer_ID\":\"63ce9ae5c064470008b72f83#0002\""), "The Guardian!");

      reply = gridWrapper(null, "Event_Consent", new String[]{"Event_Consent_Customer_ID", "Event_Consent_Status",
                      "Event_Consent_Metadata_Create_Date", "Event_Consent_Metadata_Update_Date", "Event_Consent_Description"},
              "hasNeighbourId:" + titularRid);

      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Event_Consent_Status\":\"Consent\""));
      assertTrue(replyStr.contains("\"Event_Consent_Metadata_Update_Date\":\"Mon Jan 23 19:16:02 UTC 2023\""));
      assertTrue(replyStr.contains("\"Event_Consent_Description\":\"CONSENTIMENTO 1\""));
      assertTrue(replyStr.contains("\"Event_Consent_Metadata_Create_Date\":\"Tue Mar 29 01:01:01 UTC 2022\""));
      assertTrue(replyStr.contains("\"Event_Consent_Customer_ID\":\"90129578321\""));

      String consentRid = JsonParser.parseString(reply.getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      reply = gridWrapper(null, "Object_Privacy_Notice", new String[]{"Object_Privacy_Notice_Form_Id"},
              "hasNeighbourId:" + consentRid);
      assertTrue(reply.getRecords()[0].contains("\"Object_Privacy_Notice_Form_Id\":\"63cedccce37f880008086531#0002\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00008WebinyContratos() throws InterruptedException {

    jsonTestUtil("webiny/webiny-fontes.json", "$.data.listFontesDeDados.data[*]", "webiny_data_source");
    jsonTestUtil("webiny/webiny-mapeamento-de-processos.json", "$.data.listMapeamentoDeProcessos.data[*]", "webiny_ropa");
    jsonTestUtil("webiny/webiny-contratos.json", "$.data.listContratos.data[*]", "webiny_contracts");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Contract_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63daa413efaf5f0008f6e296#0001\"\n" +
                      "  }\n" +
                      "]", "Object_Contract",
              new String[]{"Object_Contract_Short_Description", "Object_Contract_Tranfer_Intl", "Object_Contract_Has_Minors_Data", "Object_Contract_Expiry"});
      String replyStr = reply.getRecords()[0];

      String contractRid = JsonParser.parseString(replyStr).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Object_Contract_Short_Description\":\"CONTRATO 3\""));
      assertTrue(replyStr.contains("\"Object_Contract_Has_Minors_Data\":\"True\""));
      assertTrue(replyStr.contains("\"Object_Contract_Tranfer_Intl\":\"IX - QUANDO NECESSÁRIO PARA ATENDER AS HIPÓTESES PREVISTAS NOS INCISOS II\""));
      assertTrue(replyStr.contains("\"Object_Contract_Expiry\":\"Tue Jan 03 01:01:01 UTC 2023\""));

      reply = gridWrapper(null, "Object_Data_Source", new String[]{"Object_Data_Source_Name", "Object_Data_Source_Engine", "Object_Data_Source_Description"},
              "hasNeighbourId:" + contractRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Source_Engine\":\"SISTEMA 1\""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Source_Name\":\"FONTE 1\""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Source_Description\":\"FONTE 1\""));

      reply = gridWrapper(null, "Object_Data_Procedures", new String[]{"Object_Data_Procedures_Business_Area_Responsible",
                      "Object_Data_Procedures_Products_And_Services", "Object_Data_Procedures_Lawful_Basis_Justification"},
              "hasNeighbourId:" + contractRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Procedures_Products_And_Services\":\"CARRO DE LUXO\""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Procedures_Lawful_Basis_Justification\":\"JUSTIFICATIVA 1\""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Procedures_Business_Area_Responsible\":\"[TI]\""));

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

  @Test
  public void test00009WebinyOrganizacoes() throws InterruptedException {

    jsonTestUtil("webiny/webiny-organizacoes.json", "$.data.listOrganizacoes.data[*]", "webiny_organisation");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Person_Organisation_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"89894673000152\"\n" +
                      "  }\n" +
                      "]", "Person_Organisation",
              new String[]{"Person_Organisation_Name","Person_Organisation_URL"});
      String replyStr = reply.getRecords()[0];

      String NatOrgRid = JsonParser.parseString(reply.getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Person_Organisation_Name\":\"PONTUS VISION BRASIL\""));
      assertTrue(replyStr.contains("\"Person_Organisation_URL\":\"pontusvision.com.br\""));

      reply = gridWrapper(null, "Location_Address", new String[]{"Location_Address_Full_Address",
                      "Location_Address_parser_city", "Location_Address_parser_country",
                      "Location_Address_parser_road", "Location_Address_parser_postcode"},
              "hasNeighbourId:" + NatOrgRid);

      assertTrue(reply.getRecords()[0].contains("\"Location_Address_parser_country\":\"[brasil]\""));
      assertTrue(reply.getRecords()[0].contains("\"Location_Address_parser_postcode\":\"[87981061]\""));
      assertTrue(reply.getRecords()[0].contains("\"Location_Address_parser_city\":\"[saopaulo, são paulo]\""));
      assertTrue(reply.getRecords()[0].contains("\"Location_Address_parser_road\":\"[rua almirante barroso, ruaalmirantebarroso]\""));
      assertTrue(reply.getRecords()[0].contains("\"Location_Address_Full_Address\":\"RUA ALMIRANTE BARROSO 957 APARTAMENTO 23, ABC - SÃO PAULO (SP), 87981061, BRASIL\""));

      reply = gridWrapper(null, "Object_Email_Address", new String[]{"Object_Email_Address_Email"},
              "hasNeighbourId:" + NatOrgRid);
      assertTrue(reply.getRecords()[0].contains("\"Object_Email_Address_Email\":\"lmartins@pontusnetworks.com\""));

      reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Person_Organisation_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"34907\"\n" +
                      "  }\n" +
                      "]", "Person_Organisation",
              new String[]{"Person_Organisation_Name", "Person_Organisation_Type"});
      replyStr = reply.getRecords()[0];

      String IntlOrgRid = JsonParser.parseString(reply.getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Person_Organisation_Name\":\"PONTUS VISION UK\""));
      assertTrue(replyStr.contains("\"Person_Organisation_Type\":\"INTERNATIONAL\""));

      reply = gridWrapper(null, "Location_Address", new String[]{"Location_Address_Full_Address",
                      "Location_Address_parser_city", "Location_Address_parser_country",
                      "Location_Address_parser_road", "Location_Address_parser_postcode"},
              "hasNeighbourId:" + IntlOrgRid);

      assertTrue(reply.getRecords()[0].contains("\"Location_Address_parser_city\":\"[london]\""));
      assertTrue(reply.getRecords()[0].contains("\"Location_Address_parser_country\":\"[exterior]\""));
      assertTrue(reply.getRecords()[0].contains("\"Location_Address_parser_road\":\"[thatcher street 345, thatcherstreet345]\""));
      assertTrue(reply.getRecords()[0].contains("\"Location_Address_parser_postcode\":\"[e 1 0 aa, e 1 0aa, e1 0 aa, e1 0aa]\""));
      assertTrue(reply.getRecords()[0].contains("\"Location_Address_Full_Address\":\"THATCHER STREET 345 APT 1, WESTMINSTER - LONDON, E1 0AA, EXTERIOR\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00010WebinyTreinamentos() throws InterruptedException {

    jsonTestUtil("webiny/webiny-treinamentos.json", "$.data.listTreinamentos.data[*]", "webiny_awareness_campaign");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Awareness_Campaign_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63e149001cb96c000893f5c1#0001\"\n" +
                      "  }\n" +
                      "]", "Object_Awareness_Campaign",
              new String[]{"Object_Awareness_Campaign_Description", "Object_Awareness_Campaign_Start_Date"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Awareness_Campaign_Description\":\"TESTE\""));
      assertTrue(replyStr.contains("\"Object_Awareness_Campaign_Start_Date\":\"Mon Nov 13 01:01:01 UTC 2023\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00011WebinyTitulares() throws InterruptedException {

    jsonTestUtil("webiny/webiny-titulares.json", "$.data.listTitulares.data[*]", "webiny_owner");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Person_Natural_Customer_ID\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"01201405628\"\n" +
                      "  }\n" +
                      "]", "Person_Natural",
              new String[]{"Person_Natural_Full_Name", "Person_Natural_Type", "Person_Natural_Last_Update_Date"});
      String replyStr = reply.getRecords()[0];

      String titularRid = JsonParser.parseString(replyStr).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Person_Natural_Full_Name\":\"MARIA SANTOS\""), "Owner's name is Maria Santos");
      assertTrue(replyStr.contains("\"Person_Natural_Type\":\"[Colaborador]\""), "Person Natural Type");
      assertTrue(replyStr.contains("\"Person_Natural_Last_Update_Date\":\"Wed Jan 25 18:22:14 UTC 2023\""), "Last Update");

      reply = gridWrapper(null, "Person_Employee", new String[]{"Person_Employee_Role"}, "hasNeighbourId:" + titularRid);

      assertTrue(reply.getRecords()[0].contains("\"Person_Employee_Role\":\"MARKETING\""));

      reply = gridWrapper(null, "Object_Identity_Card", new String[]{"Object_Identity_Card_Id_Value"}, "hasNeighbourId:" + titularRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Identity_Card_Id_Value\":\"01201405628\""));

      reply = gridWrapper(null, "Object_Email_Address", new String[]{"Object_Email_Address_Email"}, "hasNeighbourId:" + titularRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Email_Address_Email\":\"maria@pontusvision.com\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

  @Test
  public void test00012WebinyDSAR() throws InterruptedException {

//    jsonTestUtil("webiny/webiny-titulares.json", "$.data.listTitulares.data[*]", "webiny_owner"); assuming that titulares are already ingested when the tests run integrated
    jsonTestUtil("webiny/webiny-requisicoes.json", "$.data.listRequisicaoTitulares.data[*]", "webiny_dsar");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Event_Subject_Access_Request_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63efb10fa4e4cf0008eae52c#0002\"\n" +
                      "  }\n" +
                      "]", "Event_Subject_Access_Request",
              new String[]{"Event_Subject_Access_Request_Request_Type", "Event_Subject_Access_Request_Natural_Person_Type", "Event_Subject_Access_Request_Id"});
      String replyStr = reply.getRecords()[0];

      String sarRid = JsonParser.parseString(replyStr).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Event_Subject_Access_Request_Natural_Person_Type\":\"Colaborador\""));
      assertTrue(replyStr.contains("\"Event_Subject_Access_Request_Id\":\"admin@pontus.com\""));
      assertTrue(replyStr.contains("\"Event_Subject_Access_Request_Request_Type\":\"[Read, Update, Bloqueio, Delete]\""));

      reply = gridWrapper(null, "Event_Group_Subject_Access_Request", new String[]{"Event_Group_Subject_Access_Request_Ingestion_Date"}, "hasNeighbourId:" + sarRid);

      assertTrue(reply.getRecords()[0].contains("\"Event_Group_Subject_Access_Request_Ingestion_Date\":\"" + LocalDate.now() + "\""));

      reply = gridWrapper(null, "Person_Natural", new String[]{"Person_Natural_Full_Name"}, "hasNeighbourId:" + sarRid);

      assertTrue(reply.getRecords()[0].contains("\"Person_Natural_Full_Name\":\"MARIA SANTOS\""));

      String pjRid = JsonParser.parseString(reply.getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      reply = gridWrapper(null, "Person_Employee", new String[]{"Person_Employee_Role"}, "hasNeighbourId:" + pjRid);

      assertTrue(reply.getRecords()[0].contains("\"Person_Employee_Role\":\"MARKETING\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e, e.getMessage());
    }

  }

    @Test
  public void test00013WebinyIncidentes() throws InterruptedException {

    jsonTestUtil("webiny/webiny-fontes.json", "$.data.listFontesDeDados.data[*]", "webiny_data_source");
    jsonTestUtil("webiny/webiny-incidentes.json", "$.data.listIncidentesDeSegurancaReportados.data[*]", "webiny_data_breaches");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Event_Data_Breach_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63ebe782ff8b690008ad5d40#0002\"\n" +
                      "  }\n" +
                      "]", "Event_Data_Breach",
              new String[]{"Event_Data_Breach_Description", "Event_Data_Breach_Status", "Event_Data_Breach_Impact",
                      "Event_Data_Breach_Source", "Event_Data_Breach_Authority_Notified", "Event_Data_Breach_Metadata_Create_Date"});
      String replyStr = reply.getRecords()[0];

      String breachRid = JsonParser.parseString(reply.getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Event_Data_Breach_Status\":\"Suspect External Theft\""));
      assertTrue(replyStr.contains("\"Event_Data_Breach_Metadata_Create_Date\":\"Tue Feb 14 20:01:59 UTC 2023\""));
      assertTrue(replyStr.contains("\"Event_Data_Breach_Authority_Notified\":\"True\""));
      assertTrue(replyStr.contains("\"Event_Data_Breach_Impact\":\"Data Lost\""));
      assertTrue(replyStr.contains("\"Event_Data_Breach_Description\":\"CIBER ATAQUE\""));
      assertTrue(replyStr.contains("\"Event_Data_Breach_Source\":\"CHINA\""));

      reply = gridWrapper(null, "Object_Data_Source", new String[]{"Object_Data_Source_Name",
                      "Object_Data_Source_Description"}, "hasNeighbourId:" + breachRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Source_Name\":\"FONTE 1\""));
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Source_Description\":\"FONTE 1\""));

      String ingestionRid = JsonParser.parseString(gridWrapper(null, "Event_Ingestion", new String[]{"Event_Ingestion_Type"},
              "hasNeighbourId:" + breachRid).getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      String groupIngestionRid = JsonParser.parseString(gridWrapper(null, "Event_Group_Ingestion", new String[]{"Event_Group_Ingestion_Type"},
              "hasNeighbourId:" + ingestionRid).getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      reply = gridWrapper(null, "Object_Data_Source", new String[]{"Object_Data_Source_Name"}, "hasNeighbourId:" + groupIngestionRid);
      assertTrue(reply.getRecords()[0].contains("\"Object_Data_Source_Name\":\"WEBINY/INCIDENTES-DE-SEGURANÇA-REPORTADOS\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00014WebinyRiscos() throws InterruptedException {

    jsonTestUtil("webiny/webiny-fontes.json", "$.data.listFontesDeDados.data[*]", "webiny_data_source");
    jsonTestUtil("webiny/webiny-mitigacoes.json", "$.data.listMitigacoesDeRiscos.data[*]", "webiny_risk_mitigation");
    jsonTestUtil("webiny/webiny-riscos.json", "$.data.listRiscosDeFontesDeDados.data[*]", "webiny_risk");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Risk_Data_Source_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63ecec700e3fcd00083e8545#0002\"\n" +
                      "  }\n" +
                      "]", "Object_Risk_Data_Source",
              new String[]{"Object_Risk_Data_Source_Description", "Object_Risk_Data_Source_Probability",
                      "Object_Risk_Data_Source_Impact", "Object_Risk_Data_Source_Approved_By_DPO"});
      String replyStr = reply.getRecords()[0];

      String riskRid = JsonParser.parseString(reply.getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Object_Risk_Data_Source_Probability\":\"Medium\""));
      assertTrue(replyStr.contains("\"Object_Risk_Data_Source_Impact\":\"High\""));
      assertTrue(replyStr.contains("\"Object_Risk_Data_Source_Description\":\"ROUBO DE DADOS PESSOAIS\""));
      assertTrue(replyStr.contains("\"Object_Risk_Data_Source_Approved_By_DPO\":\"False\""));

      reply = gridWrapper(null, "Object_Risk_Mitigation_Data_Source",
              new String[]{"Object_Risk_Mitigation_Data_Source_Mitigation_Id"}, "hasNeighbourId:" + riskRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Risk_Mitigation_Data_Source_Mitigation_Id\":\"MITIGAÇÃO PARA ROUBO DE CELULAR\""));

      reply = gridWrapper(null, "Event_Ingestion", new String[]{"Event_Ingestion_Type"}, "hasNeighbourId:" + riskRid);

      assertTrue(reply.getRecords()[0].contains("\"Event_Ingestion_Type\":\"webiny/riscos-de-fontes-de-dados\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

  @Test
  public void test00015WebinyMitigacaoDeRiscos() throws InterruptedException {

    jsonTestUtil("webiny/webiny-riscos.json", "$.data.listRiscosDeFontesDeDados.data[*]", "webiny_risk");
    jsonTestUtil("webiny/webiny-mitigacoes.json", "$.data.listMitigacoesDeRiscos.data[*]", "webiny_risk_mitigation");

    try {

      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Risk_Mitigation_Data_Source_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63ececa50e3fcd00083e8546#0001\"\n" +
                      "  }\n" +
                      "]", "Object_Risk_Mitigation_Data_Source",
              new String[]{"Object_Risk_Mitigation_Data_Source_Mitigation_Id", "Object_Risk_Mitigation_Data_Source_Description",
                      "Object_Risk_Mitigation_Data_Source_Is_Implemented", "Object_Risk_Mitigation_Data_Source_Is_Approved"});
      String replyStr = reply.getRecords()[0];

      String riskMitigationRid = JsonParser.parseString(reply.getRecords()[0]).getAsJsonObject().get("id").toString().replaceAll("^\"|\"$", "");

      assertTrue(replyStr.contains("\"Object_Risk_Mitigation_Data_Source_Is_Approved\":\"True\""));
      assertTrue(replyStr.contains("\"Object_Risk_Mitigation_Data_Source_Is_Implemented\":\"True\""));
      assertTrue(replyStr.contains("\"Object_Risk_Mitigation_Data_Source_Mitigation_Id\":\"MITIGAÇÃO PARA ROUBO DE CELULAR\""));
      assertTrue(replyStr.contains("\"Object_Risk_Mitigation_Data_Source_Description\":\"SE ROUBAREM CELULAR, DEVE-SE RELATAR O MAIS RÁPIDO POSSÍVEL E FAZER BO.\""));

      reply = gridWrapper(null, "Object_Risk_Data_Source",
              new String[]{"Object_Risk_Data_Source_Risk_Id"}, "hasNeighbourId:" + riskMitigationRid);

      assertTrue(reply.getRecords()[0].contains("\"Object_Risk_Data_Source_Risk_Id\":\"RISCO DE ROUBO\""));

      reply = gridWrapper(null, "Event_Ingestion", new String[]{"Event_Ingestion_Type"}, "hasNeighbourId:" + riskMitigationRid);

      assertTrue(reply.getRecords()[0].contains("\"Event_Ingestion_Type\":\"webiny/mitigações-de-riscos\""));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

}
