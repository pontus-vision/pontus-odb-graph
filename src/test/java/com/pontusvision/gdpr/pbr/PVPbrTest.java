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
      assertEquals("4",countFontesDeDados, "number of fontes de dados");

      String countDadosFontes2 =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados'))" +
                      ".as('event-ingestions').out('Has_Ingestion_Event').has('Object_Data_Source_Form_Id', eq('two'))" +
                      ".as('fontes-de-dados-two').out('Has_Sensitive_Data').dedup().count().next().toString()").get().toString();
        assertEquals("5",countDadosFontes2,
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
                        ".out('Has_Module').has('Object_Module_Name', eq('FIXA')).as('tabela')" +
                        ".out('Has_Subsystem').has('Object_Subsystem_Name', eq('ONPREM')).as('onPrem')" +
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
          "WHERE Person_Natural_Form_ID = 83829", Collections.EMPTY_MAP);
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

    jsonTestUtil("sharepoint/pbr/fontes-de-dados.json", "$.queryResp[*].fields","sharepoint_pbr_fontes_de_dados");
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

      resSet = App.graph.executeSql(
        "SELECT Object_Data_Procedures_Business_Area_Responsible as BAR, Object_Data_Procedures_Macro_Process_Name as MPN, " +
          "Object_Data_Procedures_Type_Of_Natural_Person as TNP, Object_Data_Procedures_Why_Is_It_Collected as WIC, " +
          "Object_Data_Procedures_Country_Where_Stored as CWS " +
          "FROM Object_Data_Procedures WHERE Object_Data_Procedures_Form_Id = 9542", Collections.EMPTY_MAP);

      String BAR = resSet.iterator().next().getRawResult().getProperty("BAR");
      String MPN = resSet.iterator().next().getRawResult().getProperty("MPN");
      String TNP = resSet.iterator().next().getRawResult().getProperty("TNP");
      String WIC = resSet.iterator().next().getRawResult().getProperty("WIC");
      String CWS = resSet.iterator().next().getRawResult().getProperty("CWS");

      resSet.close();

      assertEquals("Tecnologia da Informação", BAR, "Business Area Responsible");
      assertEquals("Suporte Técnico", MPN, "Macro Process Name");
      assertEquals("[Funcionário, Terceiro, Cliente]", TNP, "Types of Natural People");
      assertEquals("Atendimento de demandas de suporte técnico", WIC, "Why is it collected");
      assertEquals("[Líbano, Finlândia, Japão, Estados Unidos, Singapura]", CWS, "Countries where stored");

      String incidentsCount = App.executor.eval("App.g.V().where(" +
                                          "has('Metadata_Type_Event_Data_Breach', P.eq('Event_Data_Breach'))" +
                                            ".and().out('Impacted_By_Data_Breach').in('Has_Data_Source')" +
                                            ".has('Object_Data_Procedures_ID', TextP.endingWith(' - PBR'))" +
                                          ").dedup().count().next().toString()").get().toString();
      assertEquals("1", incidentsCount, "Only one of PBR's RoPA was breached");

      resSet = App.graph.executeSql(
        "SELECT Object_Data_Policy_Retention_Period as policy_period, Object_Data_Policy_Retention_Justification as policy_justification " +
          "FROM Object_Data_Policy " +
          "WHERE in('Has_Data_Policy').Object_Data_Procedures_ID = 'RH 43 - PBR'", Collections.EMPTY_MAP);

      String policyPeriod = resSet.iterator().next().getRawResult().getProperty("policy_period");
      String policyJustification = resSet.iterator().next().getRawResult().getProperty("policy_justification");
      resSet.close();
      assertEquals("10 anos", policyPeriod, "This RoPA has a 10 year retention policy");
      assertEquals("Ajuste de acordo com a LGPD", policyJustification, "Policy justification");

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

      resSet = App.graph.executeSql(
        "SELECT Object_Privacy_Notice_Agreements as agreements, Object_Privacy_Notice_Description as description, Object_Privacy_Notice_How_Is_It_Collected as HIC " +
          "FROM Object_Privacy_Notice " +
          "WHERE out('Has_Privacy_Notice').Object_Data_Procedures_Form_Id = '5493'", Collections.EMPTY_MAP);
//    Important note: all ids from Sharepoint come as type String "" (double quotes) and not as type Integer !!!
      String agreements = resSet.iterator().next().getRawResult().getProperty("agreements");
      String description = resSet.iterator().next().getRawResult().getProperty("description");
      String HIC = resSet.iterator().next().getRawResult().getProperty("HIC");

      resSet.close();

      assertEquals("[Correção, Exclusão, Anonimização]", agreements, "This privacy notice has three agreements");
      assertEquals("[Privacy Notice]", description, "Description");
      assertEquals("[Formulário de RH]", HIC, "How is it collected");

    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(null, e, e.getMessage());
    }

  }

  @Test
  public void test00004SharepointJuridico() throws InterruptedException {

    jsonTestUtil("sharepoint/pbr/fontes-de-dados.json", "$.queryResp[*].fields","sharepoint_pbr_fontes_de_dados");
    jsonTestUtil("sharepoint/pbr/users.json", "$.queryResp[*]", "sharepoint_pbr_users");
    jsonTestUtil("sharepoint/pbr/ropa.json", "$.queryResp[*].fields", "sharepoint_pbr_ropa");
    jsonTestUtil("sharepoint/pbr/juridico.json", "$.queryResp[*].fields", "sharepoint_pbr_juridico");

    try {

//    checking if RoPA got the 2 new props Data_Processor and Data_Controller
//    Trying new AGgrid API function "gridWrapper"
      RecordReply reply = gridWrapper("{\n" +
          "  \"searchStr\": \"\",\n" +
          "  \"searchExact\": true,\n" +
          "  \"cols\": [\n" +
          "    {\n" +
          "      \"field\": \"Object_Data_Procedures_Form_Id\",\n" +
          "      \"id\": \"Object_Data_Procedures_Form_Id\",\n" +
          "      \"name\": \"Object_Data_Procedures_Form_Id\",\n" +
          "      \"sortable\": true,\n" +
          "      \"headerName\": \"ID\",\n" +
          "      \"filter\": true\n" +
          "    },\n" +
          "    {\n" +
          "      \"field\": \"Object_Data_Procedures_Data_Processor\",\n" +
          "      \"id\": \"Object_Data_Procedures_Data_Processor\",\n" +
          "      \"name\": \"Object_Data_Procedures_Data_Processor\",\n" +
          "      \"sortable\": true,\n" +
          "      \"headerName\": \"Descrição\",\n" +
          "      \"filter\": true\n" +
          "    },\n" +
          "    {\n" +
          "      \"field\": \"Object_Data_Procedures_Data_Controller\",\n" +
          "      \"id\": \"Object_Data_Procedures_Data_Controller\",\n" +
          "      \"name\": \"Dados Object_Data_Procedures_Data_Controller\",\n" +
          "      \"sortable\": true,\n" +
          "      \"headerName\": \"Dados Coletados\",\n" +
          "      \"filter\": true\n" +
          "    }\n" +
          "  ],\n" +
          "  \"extraSearch\": {\n" +
          "    \"label\": \"Object_Data_Procedures\",\n" +
          "    \"value\": \"Object_Data_Procedures\"\n" +
          "  }\n" +
          "}", "[\n" +
        "  {\n" +
        "    \"colId\": \"Object_Data_Procedures_Form_Id\",\n" +
        "    \"filterType\": \"text\",\n" +
        "    \"type\": \"equals\",\n" +
        "    \"filter\": \"8739\"\n" +
        "  }\n" +
        "]", "Object_Data_Procedures",
        new String[] {"Object_Data_Procedures_Data_Controller", "Object_Data_Procedures_Data_Processor", "Object_Data_Procedures_Form_Id"});

      String replyStr = reply.getRecords()[0];

      assertEquals(1, reply.getTotalAvailable(), "Expecting 1 record to come back");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Data_Processor\":\"PBR\""), "PBR is the data processor");
      assertTrue(replyStr.contains("\"Object_Data_Procedures_Data_Controller\":\"PBR\""), "PBR is the data controller");

      OGremlinResultSet resSet = App.graph.executeSql(
        "SELECT Object_Legal_Actions_Description as legal_action_description, Object_Legal_Actions_Details as legal_action_details " +
          "FROM Object_Legal_Actions " +
          "WHERE Object_Legal_Actions_Title = 'RH 43 - PBR-JUR'", Collections.EMPTY_MAP);

      String legalActionDescription = resSet.iterator().next().getRawResult().getProperty("legal_action_description");
      String legalActionDetails = resSet.iterator().next().getRawResult().getProperty("legal_action_details");
      resSet.close();

      assertEquals("legislações e regulamentos", legalActionDescription, "Legal action description");
      assertEquals("informações adicionais", legalActionDetails, "Legal action details");

      String countLawfulBasis = App.executor.eval("App.g.V().has('Object_Legal_Actions_Title', endingWith('PBR-JUR')).as('pbr-legal-actions')" +
        ".in('Has_Legal_Actions').as('pbr-ropas').out('Has_Lawful_Basis_On').as('pbr-lawful-basis')" +
        ".dedup().count().next().toString()").get().toString();
      assertEquals("4", countLawfulBasis,
        "Four types of lawful basis found: CONSENTIMENTO, LEGÍTIMO INTERESSE,PROTEÇÃO DO CRÉDITO and TUTELA DA SAÚDE");

      resSet = App.graph.executeSql(
        "SELECT Object_Legitimate_Interests_Assessment_Is_Required as LIA_is_required," +
          "Object_Legitimate_Interests_Assessment_Is_Essential as LIA_is_essential," +
          "Object_Legitimate_Interests_Assessment_Lawful_Basis_Justification as LIA_justification, count(*) as ct " +
          "FROM Object_Legitimate_Interests_Assessment " +
          "WHERE Object_Legitimate_Interests_Assessment_Is_Required = 'Sim' AND Object_Legitimate_Interests_Assessment_Is_Essential = 'Sim'", Collections.EMPTY_MAP);

      String LIAisRequired = resSet.iterator().next().getRawResult().getProperty("LIA_is_required");
      String LIAisEssential = resSet.iterator().next().getRawResult().getProperty("LIA_is_essential");
      String LIAjustification = resSet.iterator().next().getRawResult().getProperty("LIA_justification");
      Long countLIA = resSet.iterator().next().getRawResult().getProperty("ct");
      resSet.close();

      assertEquals("Sim", LIAisRequired, "LIA is required");
      assertEquals("Sim", LIAisEssential, "LIA is essential");
      assertEquals("", LIAjustification, "LIA justification is empty when LIA is required and essential");
      assertEquals("1", countLIA, "1 of 3 PBR RoPAs needs LIA");

      resSet = App.graph.executeSql(
        "SELECT Object_Risk_Data_Procedures_Has_Risk_Evaluation as ropa_risk_eval, Object_Risk_Data_Procedures_Justification as ropa_risk_justification " +
          "FROM Object_Risk_Data_Procedures " +
          "WHERE in('Has_Risk').out('Has_Legal_Actions').Object_Legal_Actions_Form_Id = '63720'", Collections.EMPTY_MAP);

      String ropaRiskEval = resSet.iterator().next().getRawResult().getProperty("ropa_risk_eval");
      String ropaRiskJustification = resSet.iterator().next().getRawResult().getProperty("ropa_risk_justification");
      resSet.close();

      assertEquals("Não", ropaRiskEval, "RoPA has risk evaluation");
      assertEquals("Criticidade do RoPA é baixo", ropaRiskJustification, "RoPA risk justification");

    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(null, e, e.getMessage());
    }

  }

}
