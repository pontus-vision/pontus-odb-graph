package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(20)

public class PVBudibaseTests extends AppTest {

  @Test
  public void test00001BudibaseMapeamentoDeProcesso() throws InterruptedException {
    try {
      jsonTestUtil("budibase/bb-mapeamentos.json", "$.rows", "bb_mapeamento_de_processo");

      String joaoIsInterested =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
//                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_5ab07dbd961c41cf90bd55ddfff7869b'))" +
                      ".out('Has_Policy').as('data_policy')" +
                      ".has('Object_Data_Policy_Type', eq('Acesso-Recepção'))" +
                      ".in('Has_Policy').as('data_procs_again')" +
                      ".properties('Object_Data_Procedures_Interested_Parties_Consulted').value().next().toString()").get().toString();
      assertEquals("joao@mail.com", joaoIsInterested, "João id interested in this Process");

      String numParties =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('BUDIBASE/MAPEAMENTO-DE-PROCESSO')).as('data_source')" +
                      ".out('Has_Ingestion_Event').as('event_group')" +
                      ".out('Has_Ingestion_Event').as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Interested_Parties_Consulted', " +
                      "eq('teste@email.com, teste2@mail.com, teste3@mail.com')).dedup().count().next().toString()").get().toString();
      assertEquals("3", numParties, "3 registries with same parties interested");

      String legInterestCount=
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('BUDIBASE/MAPEAMENTO-DE-PROCESSO')).as('data_source')" +
                      ".out('Has_Ingestion_Event').as('event_group')" +
                      ".out('Has_Ingestion_Event').as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".out('Has_Lawful_Basis_On').as('lawful_basis')" +
                      ".has('Object_Lawful_Basis_Description', eq('LEGÍTIMO INTERESSE DO CONTROLADOR'))" +
//                      ".valueMap().toList()"
                      ".count().next().toString()").get().toString();
      assertEquals("7", legInterestCount, "Jurídico Snowymountain is a Party Interested in these Data Procedures");

      String LIALawfulBasis =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_5ab07dbd961c41cf90bd55ddfff7869b'))" +
                      ".out('Has_Legitimate_Interests_Assessment').as('lia')" +
                      ".properties('Object_Legitimate_Interests_Assessment_Lawful_Basis_Justification')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Justificativa azerty", LIALawfulBasis, "Lawful Basis Justification for this LIA");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using bb_mapeamento_de_processo POLE
  @Test
  public void test00002UpsertBudibaseMapeamentoDeProcesso() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-mapeamentos.json", "$.rows", "bb_mapeamento_de_processo");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/mapeamento-de-processo'))" +
                      ".count().next().toString()").get().toString();

      String countDataProcs =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/mapeamento-de-processo'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Procedures', eq('Object_Data_Procedures'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("budibase/bb-mapeamentos-2.json", "$.rows", "bb_mapeamento_de_processo");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/mapeamento-de-processo'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countDataProcsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/mapeamento-de-processo'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Procedures', eq('Object_Data_Procedures'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the budibase/mapeamento-de-processo Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countDataProcs) == Integer.parseInt(countDataProcsAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00003BudibaseFontesDeDados() throws InterruptedException {
    try {
      jsonTestUtil("budibase/bb-fontes-dados.json", "$.rows", "bb_fontes_de_dados");

      String queryPrefix = "App.g.V().has('Object_Data_Source_Name', eq('BUDIBASE/FONTES-DE-DADOS'))";

      String countDataSources =
              App.executor.eval(queryPrefix +
                      ".count().next().toString()").get().toString();
      assertEquals("1", countDataSources);

      String countEventGroupIngestions =
              App.executor.eval(queryPrefix +
                      ".both()" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", countEventGroupIngestions);

      String countEventIngestions =
              App.executor.eval(queryPrefix +
                      ".out().out()" +
                      ".count().next().toString()").get().toString();
      assertEquals("3", countEventIngestions);


      String countObjectDataSourcesIngested =
              App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".count().next().toString()").get().toString();
      assertEquals("3", countObjectDataSourcesIngested);

      String countDataPolicy =
              App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".out('Has_Policy')" +
                      ".has('Metadata_Type_Object_Data_Policy', eq('Object_Data_Policy')).dedup()" +
                      ".count().next().toString()").get().toString();
      assertEquals("3", countDataPolicy);

        String numSensitiveData = App.executor.eval(queryPrefix +
                ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                ".out('Has_Sensitive_Data')" +
                ".has('Metadata_Type_Object_Sensitive_Data', eq('Object_Sensitive_Data')).id().toSet()" +
                ".size().toString()").get().toString();
        assertEquals("11", numSensitiveData,
                "Telefone, Senha, Usuário, Ocupação, E-mail, Endereço, Nome Completo, CPF, CTPS, Certificado de Escolaridade, Certidão de Nascimento");

//        String numTelefones = App.executor.eval(queryPrefix +
//                ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
//                ".out('Has_Sensitive_Data')" +
//                ".has('Object_Sensitive_Data_Description', eq('TELEFONE'))" +
//                ".bothE().count().next().toString()").get().toString();
//        assertEquals("1", numTelefones);
//
//        String numNomes = App.executor.eval(queryPrefix +
//                ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
//                ".out('Has_Sensitive_Data')" +
//                ".has('Object_Sensitive_Data_Description', eq('NOME'))" +
//                ".bothE().count().next().toString()").get().toString();
//        assertEquals("0", numNomes);

      String dataSourceType = App.executor.eval(queryPrefix + ".as('data_source')" +
              ".out('Has_Ingestion_Event').as('event_group')" +
              ".out('Has_Ingestion_Event').as('event_ingestion')" +
              ".out('Has_Ingestion_Event').as('fontes_de_dados').has('Object_Data_Source_Form_Id', eq('ro_ta_0bbb856ebdaf400fb5f8abbc6bff4c08_cd02aa12d9ca4cc696bb2fb1e882e13d'))" +
              ".values('Object_Data_Source_Type').next().toString()").get().toString();
      assertEquals("Bare-metal server", dataSourceType, "SubSystem for this Data Source");

      String dataRetentionPeriod = App.executor.eval(queryPrefix + ".as('data_source')" +
              ".out('Has_Ingestion_Event').as('event_group')" +
              ".out('Has_Ingestion_Event').as('event_ingestion')" +
              ".out('Has_Ingestion_Event').as('fontes_de_dados').has('Object_Data_Source_Form_Id', eq('ro_ta_0bbb856ebdaf400fb5f8abbc6bff4c08_f623bcbc04c646d2943385bd83345fe1'))" +
              ".out('Has_Policy').as('data_policy')" +
              ".values('Object_Data_Policy_Retention_Period').next().toString()").get().toString();
      assertEquals("15 anos", dataRetentionPeriod, "Maximum Retention Period for this Data Source (in years)");

      String dataSourceEngine = App.executor.eval(queryPrefix + ".as('data_source')" +
              ".out('Has_Ingestion_Event').as('event_group')" +
              ".out('Has_Ingestion_Event').as('event_ingestion')" +
              ".out('Has_Ingestion_Event').as('fontes_de_dados').has('Object_Data_Source_Form_Id', eq('ro_ta_0bbb856ebdaf400fb5f8abbc6bff4c08_0d04ac78f65f4b789fa930d294995f35'))" +
              ".values('Object_Data_Source_Engine').next().toString()").get().toString();
      assertEquals("Windows", dataSourceEngine, "SubSystem for this Data Source");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using bb_fontes_de_dados POLE
  @Test
  public void test00004UpsertBudibaseFontesDeDados() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-fontes-dados.json", "$.rows", "bb_fontes_de_dados");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".count().next().toString()").get().toString();

      String countDataSources =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Source', eq('Object_Data_Source'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("budibase/bb-fontes-dados-2.json", "$.rows", "bb_fontes_de_dados");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countDataSourcesAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Source', eq('Object_Data_Source'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the budibase/fontes-de-dados Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countDataSources) == Integer.parseInt(countDataSourcesAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00005BudibaseAvisoPrivacidade() throws InterruptedException {
    try {
      jsonTestUtil("budibase/bb-aviso-privacidade.json", "$.rows", "bb_aviso_privacidade");

      String getPrivacyNoticeDescription =
              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id'" +
                      ",eq('ro_ta_0714853d6bb241da8bab8e231d12e6e4_4a964bc2d5c54feaac7e889975cc1752'))" +
                      ".properties('Object_Privacy_Notice_Description').value()" +
                      ".next().toString()").get().toString();
      assertEquals("Test privacy", getPrivacyNoticeDescription,"Privacy Notice's Description.");

      String getPrivacyNoticeCreateDate =
              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id'" +
                      ",eq('ro_ta_0714853d6bb241da8bab8e231d12e6e4_4a964bc2d5c54feaac7e889975cc1752'))" +
                      ".properties('Object_Privacy_Notice_Metadata_Create_Date').value()" +
                      ".next().toString()").get().toString();
//      getPrivacyNoticeCreateDate = getPrivacyNoticeCreateDate.replaceAll("... 2022", "GMT 2022");
      assertEquals(dtfmt.parse("Tue Jan 25 14:10:42 UTC 2022"), dtfmt.parse(getPrivacyNoticeCreateDate),
              "Privacy Notice's creation date.");

      String getObjectDataSourceName =
              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id'" +
                      ",eq('ro_ta_0714853d6bb241da8bab8e231d12e6e4_4a964bc2d5c54feaac7e889975cc1752')).out('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event').in('Has_Ingestion_Event').properties('Object_Data_Source_Name').value()" +
                      ".next().toString()").get().toString();
      assertEquals("BUDIBASE/AVISO-DE-PRIVACIDADE", getObjectDataSourceName, "Data Source Name.");

      String getPrivacyNoticeDeliveryDate =
              App.executor.eval("App.g.V().has('Object_Privacy_Notice_Form_Id'" +
                      ",eq('ro_ta_0714853d6bb241da8bab8e231d12e6e4_4a964bc2d5c54feaac7e889975cc1752'))" +
                      ".properties('Object_Privacy_Notice_Delivery_Date').value()" +
                      ".next().toString()").get().toString();
      assertEquals(dtfmt.parse("Tue Jun 22 15:00:00 UTC 2021"), dtfmt.parse(getPrivacyNoticeDeliveryDate),
              "Privacy Notice's delivery date for id 'stuvw'.");

    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }
  // TODO: test00006UpsertBudibaseAvisoPrivacidade & bb-aviso-privacidade-2.json

  @Test
  public void test00007BudibaseRiscosDeFontesDeDados() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-riscos-fontes-dados.json", "$.rows", "bb_riscos");

      String numRiskMitigations =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/risk'))" +
                      ".out('Has_Ingestion_Event').as('risk-data-source')" +
                      ".has('Metadata_Type_Object_Risk_Data_Source'" +
                      ",eq('Object_Risk_Data_Source'))" +
                      ".dedup().count().next().toString()").get().toString();
      assertEquals("10",numRiskMitigations, "10 Risks");

      String numDataSourcesRo_ta_285c8a =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/risk'))" +
                      ".out('Has_Ingestion_Event').as('risk-data-source')" +
                      ".has('Object_Risk_Data_Source_Form_Id'" +
                      ",eq('ro_ta_285c8a55b293461e9c2c50eefa082d96_fcd98e1ad156470da005bf0e77d47a4c'))" +
                      ".in('Has_Risk')" +
                      ".count().next().toString()"
              ).get().toString();
      assertEquals("2",numDataSourcesRo_ta_285c8a, "2 Data sources associated with ro_ta_285c8a");

      String descriptionR02 =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/risk'))" +
                      ".out('Has_Ingestion_Event').as('risk-data-source')" +
                      ".has('Object_Risk_Data_Source_Risk_Id', eq('R02'))" +
                      ".values('Object_Risk_Data_Source_Description')" +
                      ".next().toString()").get().toString();
      assertEquals("Modificação não autorizada.",descriptionR02, "R02 description is correct");


      String numMitigationsR02 =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/risk'))" +
                      ".out('Has_Ingestion_Event').as('risk-data-source')" +
                      ".has('Object_Risk_Data_Source_Risk_Id', eq('R02')).in('Mitigates_Risk')\n" +
                      ".count().next().toString()").get().toString();
      assertEquals("0",numMitigationsR02, "No Risk Mitigation is associated with R02");

      String approvedBySecurity =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/risk'))" +
                      ".out('Has_Ingestion_Event').as('risk-data-source')" +
                      ".has('Object_Risk_Data_Source_Risk_Id', eq('R13'))" +
                      ".values('Object_Risk_Data_Source_Approved_By_Security').next().toString()").get().toString();
      assertEquals("true",approvedBySecurity, "bool status for Approved_By_Security");

      String approvedByDPO =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/risk'))" +
                      ".out('Has_Ingestion_Event').as('risk-data-source')" +
                      ".has('Object_Risk_Data_Source_Risk_Id', eq('R06'))" +
                      ".values('Object_Risk_Data_Source_Approved_By_DPO').next().toString()").get().toString();
      assertEquals("false",approvedByDPO, "bool status for Approved_By_DPO");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }
  // TODO: test00008UpsertBudibaseRiscosDeFontesDeDados & bb-riscos-fontes-dados-2.json

  @Test
  public void test00009BudibaseAcoesJudiciais() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-acoes-judiciais.json", "$.rows", "bb_acoes_judiciais_ppd");

      String legalActionsDescription =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/ações-judiciais-p&pd'))" +
                      ".out('Has_Ingestion_Event').as('legal-actions')" +
                      ".has('Object_Legal_Actions_Form_Id'" +
                      ",eq('ro_ta_98e11b0486e4491eb84a317e758e5493_1c18938bf60b447da319f824629e76f7'))" +
                      ".values('Object_Legal_Actions_Description').next().toString()").get().toString();
      assertEquals("teste",legalActionsDescription, "descrição da ação judicial");

      String legalActionsDetails =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/ações-judiciais-p&pd'))" +
                      ".out('Has_Ingestion_Event').as('legal-actions')" +
                      ".has('Object_Legal_Actions_Form_Id'" +
                      ",eq('ro_ta_98e11b0486e4491eb84a317e758e5493_1c18938bf60b447da319f824629e76f7'))" +
                      ".values('Object_Legal_Actions_Details').next().toString()").get().toString();
      assertEquals("teste",legalActionsDetails, "detalhe da ação judicial");

      String legalActionsDate =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/ações-judiciais-p&pd'))" +
                      ".out('Has_Ingestion_Event').as('legal-actions')" +
                      ".has('Object_Legal_Actions_Form_Id'" +
                      ",eq('ro_ta_98e11b0486e4491eb84a317e758e5493_1c18938bf60b447da319f824629e76f7'))" +
                      ".values('Object_Legal_Actions_Date').next().toString()").get().toString();
      assertEquals(dtfmt.parse("Tue Feb 08 15:00:00 UTC 2022"),dtfmt.parse(legalActionsDate), "data da ação judicial");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }
  // TODO: test00010UpsertBudibaseAcoesJudiciais & bb-acoes-judiciais-2.json

  @Test
  public void test00011BudibaseMitigacaoDeRiscos() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-mitigacao-de-riscos.json", "$.rows", "bb_mitigacao_de_riscos");

      String riskMitigationCount =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/mitigação-de-riscos')).as('event-ingestion')" +
                      ".outE('Has_Ingestion_Event').as('risk-mitigation')" +
                      ".dedup().count().next().toString()").get().toString();
      assertEquals("9",riskMitigationCount, "9 Risk Mitigations");

      String riskMitigationDescription =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/mitigação-de-riscos')).as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('risk-mitigation')" +
                      ".has('Object_Risk_Mitigation_Data_Source_Form_Id', eq('ro_ta_f756e115964544a48c5fb113231d48a7_dd168a0999374477b6808d37e3c0543c'))" +
                      ".values('Object_Risk_Mitigation_Data_Source_Description')" +
                      ".next().toString()").get().toString();
      assertEquals("POLITICAS PARA ELIMINAREM O ACESSO LOCAL DE DISCOS",riskMitigationDescription, "This Risk Mitigation's description");

      String riskMitigationId =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/mitigação-de-riscos')).as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('risk-mitigation')" +
                      ".has('Object_Risk_Mitigation_Data_Source_Form_Id', eq('ro_ta_f756e115964544a48c5fb113231d48a7_950623b8115f4f31be6c6e4dc7f075c2'))" +
                      ".values('Object_Risk_Mitigation_Data_Source_Mitigation_Id')" +
                      ".next().toString()").get().toString();
      assertEquals("M-DATA-ENCR-REST",riskMitigationId, "This risk mitigation's ID");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }
  // TODO: test00012UpsertBudibaseMitigacaoDeRiscos & bb-mitigacao-de-riscos-2.json

  @Test
  public void test00013BudibaseTreinamento() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-treinamento.json", "$.rows", "bb_treinamento");

      String trainingCompletedBy =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/treinamento')).as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-training')" +
                      ".has('Event_Training_Form_Id', eq('ro_ta_aad5934f39b3412ba8a8a3d59f4a4edc_0432eb6e47d04029b602e1ca7046bd23'))" +
                      ".out('Completed_By').as('person-natural')" +
                      ".values('Person_Natural_Full_Name')" +
                      ".next().toString()").get().toString();
      assertEquals("JULIO RIBEIRO", trainingCompletedBy, "This training was done by Julio.");

      String trainingStatus =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/treinamento')).as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-training')" +
                      ".has('Event_Training_Form_Id', eq('ro_ta_aad5934f39b3412ba8a8a3d59f4a4edc_0432eb6e47d04029b602e1ca7046bd23'))" +
                      ".values('Event_Training_Status')" +
                      ".next().toString()").get().toString();
      assertEquals("Passed", trainingStatus, "Julio PASSED this training.");

      String trainingDescription =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/treinamento')).as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-training')" +
                      ".has('Event_Training_Form_Id', eq('ro_ta_aad5934f39b3412ba8a8a3d59f4a4edc_0432eb6e47d04029b602e1ca7046bd23'))" +
                      ".out('Course_Training').as('aware-campaign')" +
                      ".values('Object_Awareness_Campaign_Description')" +
                      ".next().toString()").get().toString();
      assertEquals("TREINAMENTO PARA TESTE 1", trainingDescription, "This training's description");

      String trainingDate =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/treinamento')).as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-training')" +
                      ".has('Event_Training_Form_Id', eq('ro_ta_aad5934f39b3412ba8a8a3d59f4a4edc_0432eb6e47d04029b602e1ca7046bd23'))" +
                      ".out('Course_Training').as('aware-campaign')" +
                      ".values('Object_Awareness_Campaign_Start_Date')" +
                      ".next().toString()").get().toString();
      assertEquals(dtfmt.parse("Tue Feb 08 15:00:00 UTC 2022"), dtfmt.parse(trainingDate), "This training's description");

    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }
  // TODO: test00014UpsertBudibaseBudibaseTreinamento & bb-treinamento-2.json

  @Test
  public void test00014BudibaseControleDeSolicitacoes() throws InterruptedException {

    jsonTestUtil("budibase/bb-mapeamentos.json", "$.rows", "bb_mapeamento_de_processo");

    jsonTestUtil("budibase/bb-solicitacoes.json", "$.rows", "bb_controle_de_solicitacoes");

    try {

      String dsarRequesterName =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/controle-de-solicitações')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('dsar')" +
                      ".has('Event_Subject_Access_Request_Form_Id', eq('ro_ta_c638105c80d04cd2a8a73740e1a1d240_33aefc7448af413ea21d09963058c75d'))" +
                      ".in('Made_SAR_Request').as('person-natural')" +
                      ".values('Person_Natural_Full_Name').next().toString()").get().toString();
      assertEquals("AMANDA MOZENA", dsarRequesterName, "Person that requested this DSAR");

      String omarRequesterID =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/controle-de-solicitações')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('dsar')" +
                      ".in('Made_SAR_Request').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('OMAR MOHAMAD ABOU GHOCHE'))" +
                      ".values('Person_Natural_Customer_ID')" +
                      ".next().toString()").get().toString();
      assertEquals("01222554389", omarRequesterID,"Omar's ID");

      String completedDSARCount =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/controle-de-solicitações')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('dsar')" +
                      ".has('Event_Subject_Access_Request_Status', eq('Completed'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("2", completedDSARCount, "Two DSARs were Completed!");

      String fromDsarToRopa =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/controle-de-solicitações')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('event-access-req')" +
                      ".in('Made_SAR_Request').as('person-natural')" +
                      ".has('Person_Natural_Full_Name'" +
                      ",eq('OMAR MOHAMAD ABOU GHOCHE')).as('person-natural')" +
                      ".out('Made_SAR_Request').as('dsar')" +
                      ".out('Has_DSAR').as('dsar-group')" +
                      ".out('Has_DSAR').as('ropa')" +
                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_5ab07dbd961c41cf90bd55ddfff7869b'))" +
                      ".values('Object_Data_Procedures_Description')" +
                      ".next().toString()").get().toString();
      assertEquals("Necessário para entrada e saída de candidatos a sede da empresa",fromDsarToRopa,
              "RoPA's Data Procedures' Description");

      String fromRopaToDsar =
              App.executor.eval("App.g.V().has('Object_Data_Procedures_Interested_Parties_Consulted'" +
                      ",eq('vitor@gmail.com, omar@mail.com')).as('ropa-data-procs')" +
                      ".in('Has_DSAR').as('dsar-group')" +
                      ".in('Has_DSAR').as('dsar')" +
                      ".in('Made_SAR_Request').as('person-natural')" +
                      ".has('Person_Natural_Customer_ID', eq('80927892892'))" +
                      ".values('Person_Natural_Full_Name')" +
                      ".next().toString()").get().toString();
      assertEquals("LEO MARTINS", fromRopaToDsar,"RoPA's Interested Parties");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}