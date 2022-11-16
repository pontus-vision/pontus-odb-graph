package com.pontusvision.gdpr.pbr;

import com.pontusvision.gdpr.*;
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */

@TestClassOrder(AnnotationTestsOrderer.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassesOrder(33)
//@RunWith(JUnitPlatform.class)
public class PVPbrTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001FontesDeDados() throws InterruptedException {

    try {

      jsonTestUtil("sharepoint/pbr/fontes-de-dados.json", "$.queryResp[*].fields", "sharepoint_pbr_fontes_de_dados");

      String countFontesDeDados =
        App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados'))" +
          ".as('event-ingestions').dedup().count().next().toString()").get().toString();
      assertEquals("4", countFontesDeDados, "number of fontes de dados");

      String countDadosFontes2 =
        App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados'))" +
          ".as('event-ingestions').out('Has_Ingestion_Event').has('Object_Data_Source_Form_Id', eq('two'))" +
          ".as('fontes-de-dados-two').out('Has_Sensitive_Data').dedup().count().next().toString()").get().toString();
      assertEquals("5", countDadosFontes2,
        "Apelido, Data da Aposentadoria, Situação no CIPA, Classificação do visto, PIS/PASEP/NIT");

      String getFontesEquipamentosDescription =
        App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados'))" +
          ".as('event-ingestions').out('Has_Ingestion_Event').has('Object_Data_Source_Name', eq('EQUIPAMENTOS'))" +
          ".as('fontes-de-dados').values('Object_Data_Source_Description').next().toString()").get().toString();
      assertEquals("EQUIPAMENTOS DE PROTEÇÃO INDIVIDUAL", getFontesEquipamentosDescription, "Description of EQUIPAMENTOS");

      String getFontes4System =
        App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SHAREPOINT/PBR/FONTES-DE-DADOS')).as('root')" +
          ".out('Has_Ingestion_Event').has('Event_Group_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados')).as('group-ingestion')" +
          ".out('Has_Ingestion_Event').has('Event_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados')).as('event-ingestion')" +
          ".out('Has_Ingestion_Event').has('Object_Data_Source_Form_Id', eq('four')).as('fontes-de-dados')" +
          ".out('Has_Module').has('Object_Module_Name', eq('fixa')).as('tabela')" +
          ".out('Has_Subsystem').has('Object_Subsystem_Name', eq('ONPREM-PBR')).as('onPrem')" +
          ".out('Has_System').as('system')" +
          ".values('Object_System_Name').next().toString()").get().toString();
      assertEquals("POLARIS", getFontes4System, "System of fontes-de-dados 4");


    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(null, e, e.getMessage());
    }


  }

  // Testing new method/function SharepointUserRef
  @Test
  public void test00002SharepointUserRef() throws InterruptedException {

    try {

      jsonTestUtil("sharepoint/pbr/users.json", "$.queryResp[*]", "sharepoint_pbr_users");

      OGremlinResultSet resSet = App.graph.executeSql(
        "SELECT Person_Natural_Email as email " +
          "FROM Person_Natural " +
          "WHERE Person_Natural_Full_Name = 'GHOCHE, OMAR (LBN)'", Collections.EMPTY_MAP);

      String responsibleEmail = resSet.iterator().next().getRawResult().getProperty("email");
      resSet.close();
      assertEquals("oghoche@pontusvision.com", responsibleEmail, "Responsible email");

      resSet = App.graph.executeSql(
        "SELECT Person_Natural_Form_Id as form_id " +
          "FROM Person_Natural " +
          "WHERE Person_Natural_Full_Name = 'VITOR, PAULO (BRL)'", Collections.EMPTY_MAP);

      String alternativeResponsibleFormId = resSet.iterator().next().getRawResult().getProperty("form_id");
      resSet.close();
      assertEquals("32192", alternativeResponsibleFormId, "Alternative Responsible form id");

      resSet = App.graph.executeSql(
        "SELECT Object_Data_Source_Name as data_source " +
          "FROM Object_Data_Source " +
          "WHERE out('Has_Ingestion_Event').Event_Group_Ingestion_Type = 'sharepoint/pbr/users'", Collections.EMPTY_MAP);
      String dataSourceName = resSet.iterator().next().getRawResult().getProperty("data_source");
      resSet.close();
      assertEquals("SHAREPOINT/PBR/USERS", dataSourceName, "Data source name");

      resSet = App.graph.executeSql(
        "SELECT Person_Natural_Full_Name as name " +
          "FROM Person_Natural " +
          "WHERE Person_Natural_Form_Id = 83829", Collections.EMPTY_MAP);
      String personNaturalName = resSet.iterator().next().getRawResult().getProperty("name");
      resSet.close();
      assertEquals("GHOCHE, OMAR (LBN)", personNaturalName, "Person natural name");

    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(null, e, e.getMessage());
    }

  }

  @Test
  public void test00003SharepointRoPA() throws InterruptedException {

    jsonTestUtil("sharepoint/pbr/fontes-de-dados.json", "$.queryResp[*].fields", "sharepoint_pbr_fontes_de_dados");
    jsonTestUtil("sharepoint/pbr/users.json", "$.queryResp[*]", "sharepoint_pbr_users");
    jsonTestUtil("sharepoint/pbr/ropa.json", "$.queryResp[*].fields", "sharepoint_pbr_ropa");

    try {

      OGremlinResultSet resSet = App.graph.executeSql(
        "SELECT count(*) as ropa " +
          "FROM Object_Data_Procedures " +
          "WHERE Object_Data_Procedures_ID LIKE '%- PBR'", Collections.EMPTY_MAP);

      String ropaCount = resSet.iterator().next().getRawResult().getProperty("ropa").toString();
      resSet.close();
      assertEquals("3", ropaCount, "Three RoPA procedures found in PBR");

      // test linked users to the RoPAs
      resSet = App.graph.executeSql(
        "SELECT Person_Natural_Full_Name as responsible " +
          "FROM Person_Natural " +
          "WHERE Person_Natural_Form_Id = 83847", Collections.EMPTY_MAP);
//    Important note: all ids from Sharepoint come as type String "" (double quotes) and not as type Integer !!!
      String responsibleName = resSet.iterator().next().getRawResult().getProperty("responsible");
      resSet.close();
      assertEquals("MENDES, ALINE (US)", responsibleName, "Mendes is responsible for this RoPA");

      String dataSourceCount = App.executor.eval("App.g.V().has('Object_Data_Procedures_Form_Id', eq('9542'))" +
        ".as('ropa-9542').out('Has_Data_Source').as('attached_data_sources').dedup().count().next().toString()").get().toString();
      assertEquals("2", dataSourceCount, "Two Data_Sources found attached to this RoPA - ADP and Marketing");

      String dsName = App.executor.eval("App.g.V().has('Object_Data_Procedures_ID', endingWith(' - PBR'))" +
        ".in('Has_Ingestion_Event').as('event_ingestion').in('Has_Ingestion_Event').as('event_group')" +
        ".in('Has_Ingestion_Event').as('data_source').values('Object_Data_Source_Name').next().toString()").get().toString();
      assertEquals("SHAREPOINT/PBR/ROPA", dsName, "Data source name");

//    new AGgrid test style ------------------------------------------------------------------------------------------------------------
      RecordReply reply = gridWrapper("[\n" +
          "  {\n" +
          "    \"colId\": \"Object_Data_Procedures_Form_Id\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"9542\"\n" +
          "  }\n" +
          "]", "Object_Data_Procedures",
        new String[]{"Object_Data_Procedures_Business_Area_Responsible", "Object_Data_Procedures_Macro_Process_Name",
          "Object_Data_Procedures_Type_Of_Natural_Person", "Object_Data_Procedures_Why_Is_It_Collected","Object_Data_Procedures_Country_Where_Stored"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Data_Procedures_Business_Area_Responsible\":\"Tecnologia da Informação\""), "Business Area Responsible");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Macro_Process_Name\":\"Suporte Técnico\""), "Macro Process Name");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Type_Of_Natural_Person\":\"[Funcionário, Terceiro, Cliente]\""), "Types of Natural People");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Why_Is_It_Collected\":\"Atendimento de demandas de suporte técnico\""), "Why is it collected");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Country_Where_Stored\":\"[Líbano, Finlândia, Japão, Estados Unidos, Singapura]\""), "Countries where stored");

      String pbrRopaRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Object_Data_Procedures_ID\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"RH 43 - PBR\"\n" +
          "  }\n" +
          "]", "Object_Data_Procedures",
        new String[]{"Object_Data_Procedures_ID"});

      reply = gridWrapper(null, "Object_Data_Policy", new String[]{"Object_Data_Policy_Retention_Period",
        "Object_Data_Policy_Retention_Justification"}, "hasNeighbourId:" + pbrRopaRid);
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Data_Policy_Retention_Period\":\"10 anos\""), "This RoPA has a 10 year retention policy");
      assertTrue(replyStr.contains("\"Object_Data_Policy_Retention_Justification\":\"Ajuste de acordo com a LGPD\""), "Policy justification");

      pbrRopaRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Object_Data_Procedures_Form_Id\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"5493\"\n" +
          "  }\n" +
          "]", "Object_Data_Procedures",
        new String[]{"Object_Data_Procedures_ID"});

      reply = gridWrapper(null, "Object_Privacy_Notice",
        new String[]{"Object_Privacy_Notice_Agreements", "Object_Privacy_Notice_Description", "Object_Privacy_Notice_How_Is_It_Collected"},
        "hasNeighbourId:" + pbrRopaRid);
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Privacy_Notice_Agreements\":\"[Correção, Exclusão, Anonimização]\""), "This privacy notice has three agreements");
      assertTrue(replyStr.contains("\"Object_Privacy_Notice_Description\":\"[Privacy Notice]\""), "Description");
      assertTrue(replyStr.contains("\"Object_Privacy_Notice_How_Is_It_Collected\":\"[Formulário de RH]\""), "How is it collected");

// -------------------------------------------------------------------------------------------------------------------------------------

      String incidentsCount = App.executor.eval("App.g.V().where(" +
        "has('Metadata_Type_Event_Data_Breach', P.eq('Event_Data_Breach'))" +
        ".and().out('Impacted_By_Data_Breach').in('Has_Data_Source')" +
        ".has('Object_Data_Procedures_ID', TextP.endingWith(' - PBR'))" +
        ").dedup().count().next().toString()").get().toString();
      assertEquals("1", incidentsCount, "Only one of PBR's RoPA was breached");

      resSet = App.graph.executeSql(
        "SELECT Object_Data_Procedures_Info_Collected as info " +
          "FROM Object_Data_Procedures " +
          "WHERE Object_Data_Procedures_ID = 'FINANCEIRO 01 - PBR'", Collections.EMPTY_MAP);

      String info = resSet.iterator().next().getRawResult().getProperty("info");
      resSet.close();
      assertEquals("[Cargo/Posição, Data de Nascimento, E-mail Comercial, RG, E-mail Particular, CPF, Endereço, " +
        "Telefone Comercial, Telefone Particular, Dados Bancários, Nome Social, Dados Biométricos, Raça, Religião, " +
        "Nome da Mãe, Nome do Pai, Nacionalidade]", info, "This RoPA collects a lot of information in one string block");

      String countSensitiveData = App.executor.eval("App.g.V().where(" +
        "has('Object_Data_Procedures_ID', TextP.endingWith(' - PBR'))" +
        ").as('ropa_pbr').out('Has_Sensitive_Data').as('sensitive_data')" +
        ".dedup().count().next().toString()").get().toString();
      assertEquals("4", countSensitiveData, "Four types of sensitive data found in the PBR RoPA: Dados Biométricos, Raça, Religião and Opinião Política");

      String countPersonalData = App.executor.eval("App.g.V().where(" +
        "has('Object_Data_Procedures_ID', TextP.endingWith(' - PBR'))" +
        ").as('ropa_pbr').out('Has_Personal_Data').as('personal_data')" +
        ".dedup().count().next().toString()").get().toString();
      assertEquals("17", countPersonalData, "17 types of personal data found within three of PBR's RoPA");



    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(null, e, e.getMessage());
    }

  }

  @Test
  public void test00004SharepointJuridico() throws InterruptedException {

    jsonTestUtil("sharepoint/pbr/fontes-de-dados.json", "$.queryResp[*].fields", "sharepoint_pbr_fontes_de_dados");
    jsonTestUtil("sharepoint/pbr/users.json", "$.queryResp[*]", "sharepoint_pbr_users");
    jsonTestUtil("sharepoint/pbr/ropa.json", "$.queryResp[*].fields", "sharepoint_pbr_ropa");
    jsonTestUtil("sharepoint/pbr/juridico.json", "$.queryResp[*].fields", "sharepoint_pbr_juridico");

    try {

//    checking if RoPA got the 2 new props Data_Processor and Data_Controller
//    Trying new AGgrid API function "gridWrapper"
      RecordReply reply = gridWrapper("[\n" +
          "  {\n" +
          "    \"colId\": \"Object_Data_Procedures_Form_Id\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"8739\"\n" +
          "  }\n" +
          "]", "Object_Data_Procedures",
        new String[]{"Object_Data_Procedures_Data_Controller", "Object_Data_Procedures_Data_Processor", "Object_Data_Procedures_Form_Id"});
      String replyStr = reply.getRecords()[0];

      assertEquals(1, reply.getTotalAvailable(), "Expecting 1 record to come back");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Data_Processor\":\"PBR\""), "PBR is the data processor");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Data_Controller\":\"PBR\""), "PBR is the data controller");

      String pbrRopaRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Object_Data_Procedures_ID\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"FINANCEIRO 01 - PBR\"\n" +
          "  }\n" +
          "]", "Object_Data_Procedures",
        new String[]{"Object_Data_Procedures_ID"});

      reply = gridWrapper(null, "Object_Lawful_Basis", new String[]{"Object_Lawful_Basis_Description"}, "hasNeighbourId:" + pbrRopaRid);
      assertEquals(2, reply.getTotalAvailable(),
        "Two types of lawful basis found: CONSENTIMENTO and LEGÍTIMO INTERESSE");

    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(null, e, e.getMessage());
    }

  }

  @Test
  public void test00005SharepointLIA() throws InterruptedException {

    jsonTestUtil("sharepoint/pbr/fontes-de-dados.json", "$.queryResp[*].fields", "sharepoint_pbr_fontes_de_dados");
    jsonTestUtil("sharepoint/pbr/users.json", "$.queryResp[*]", "sharepoint_pbr_users");
    jsonTestUtil("sharepoint/pbr/ropa.json", "$.queryResp[*].fields", "sharepoint_pbr_ropa");
    jsonTestUtil("sharepoint/pbr/lia.json", "$.queryResp[*].fields", "sharepoint_pbr_lia");

    try {

      String pbrGroupEventRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Event_Group_Ingestion_Type\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"sharepoint/pbr/lia\"\n" +
          "  }\n" +
          "]", "Event_Group_Ingestion",
        new String[]{"Event_Group_Ingestion_Type"});

      RecordReply reply = gridWrapper(null, "Object_Data_Source", new String[]{"Object_Data_Source_Name"}, "hasNeighbourId:" + pbrGroupEventRid);

      String replyStr = reply.getRecords()[0];
      assertTrue(replyStr.contains("\"Object_Data_Source_Name\":\"SHAREPOINT/PBR/LIA\""),
        "PBR is the data source");

      String pbrRopaRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Object_Data_Procedures_ID\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"RH 43 - PBR\"\n" +
          "  }\n" +
          "]", "Object_Data_Procedures",
        new String[]{"Object_Data_Procedures_ID"});

      reply = gridWrapper(null, "Object_Legitimate_Interests_Assessment",
        new String[]{"Object_Legitimate_Interests_Assessment_Strategic_Impact"}, "hasNeighbourId:" + pbrRopaRid);

      replyStr = reply.getRecords()[0];
      assertTrue(replyStr.contains("\"Object_Legitimate_Interests_Assessment_Strategic_Impact\":\"Sim, pois é estratégico para a empresa; Consequências legais; \""),
        "text for strategic impact");

      reply = gridWrapper(null, "Object_Legitimate_Interests_Assessment",
        new String[]{"Object_Legitimate_Interests_Assessment_Is_Essential", "Object_Legitimate_Interests_Assessment_Is_Required"},
        "hasNeighbourId:" + pbrRopaRid);

      replyStr = reply.getRecords()[0];
      assertTrue(replyStr.contains("\"Object_Legitimate_Interests_Assessment_Is_Essential\":\"Sim\""), "LIA is essential");
      assertTrue(replyStr.contains("\"Object_Legitimate_Interests_Assessment_Is_Required\":\"Sim\""), "LIA is required");

    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(null, e, e.getMessage());
    }

  }

}
