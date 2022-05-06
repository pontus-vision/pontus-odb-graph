package com.pontusvision.gdpr.govbr;

import com.pontusvision.gdpr.AnnotationTestsOrderer;
import com.pontusvision.gdpr.App;
import com.pontusvision.gdpr.AppTest;
import com.pontusvision.gdpr.TestClassesOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(34)

public class PVGovBrMapeamentoDeProcessoTests extends AppTest {

  @Test
  public void test00001GovBrMapeamentoDeProcesso() throws InterruptedException {
    try {
      jsonTestUtil("govbr/govbr-mapeamentos.json", "$.rows", "govbr_mapeamento_de_processo");

      String govIsInterested =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".out('Has_Policy').as('data_policy')" +
                      ".has('Object_Data_Policy_Type', eq('Seguranca e Tratamento'))" +
                      ".in('Has_Policy').as('data_procs_again').has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_6f48fd8a585b4f18af44ee8633269f2d'))" +
                      ".properties('Object_Data_Procedures_Interested_Parties_Consulted').value().next().toString()").get().toString();
      assertEquals("pv@gov.br", govIsInterested, "The Government is interested in this Process");

      String numParties =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('GOV.BR/MAPEAMENTO-DE-PROCESSO')).as('data_source')" +
                      ".out('Has_Ingestion_Event').as('event_group')" +
                      ".out('Has_Ingestion_Event').as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Interested_Parties_Consulted', " +
                      "eq('pv@gov.br')).dedup().count().next().toString()").get().toString();
      assertEquals("3", numParties, "3 registries with same parties interested");

      String legInterestCount=
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('GOV.BR/MAPEAMENTO-DE-PROCESSO')).as('data_source')" +
                      ".out('Has_Ingestion_Event').as('event_group')" +
                      ".out('Has_Ingestion_Event').as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".out('Has_Lawful_Basis_On').as('lawful_basis')" +
                      ".has('Object_Lawful_Basis_Description', eq('PARA A PROTEÇÃO DA VIDA OU DA INCOLUMIDADE FÍSICA DO TITULAR OU DE TERCEIRO'))" +
//                      ".valueMap().toList()"
                      ".count().next().toString()").get().toString();
      assertEquals("4", legInterestCount, "This Lawful Basis is present in 4 registries");

      String LIALawfulBasis =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_5e673a2d7c6e4a1ba4ccd433d8ab1d94'))" +
                      ".out('Has_Legitimate_Interests_Assessment').as('lia')" +
                      ".properties('Object_Legitimate_Interests_Assessment_Lawful_Basis_Justification')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Justificativa jkl", LIALawfulBasis, "Lawful Basis Justification for this LIA");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using govbr_mapeamento_de_processo POLE
  @Test
  public void test00002UpsertGovBrMapeamentoDeProcesso() throws InterruptedException {
    try {

      jsonTestUtil("govbr/govbr-mapeamentos.json", "$.rows", "govbr_mapeamento_de_processo");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo'))" +
                      ".count().next().toString()").get().toString();

      String countDataProcs =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Procedures', eq('Object_Data_Procedures'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countPersonalData =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo'))" +
                      ".out('Has_Ingestion_Event').out('Has_Personal_Data')" +
                      ".has('Metadata_Type_Object_Personal_Data', eq('Object_Personal_Data'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("govbr/govbr-mapeamentos-2.json", "$.rows", "govbr_mapeamento_de_processo");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countDataProcsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Procedures', eq('Object_Data_Procedures'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countPersonalDataAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo'))" +
                      ".out('Has_Ingestion_Event').out('Has_Personal_Data')" +
                      ".has('Metadata_Type_Object_Personal_Data', eq('Object_Personal_Data'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the gov.br/mapeamento-de-processo Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data (Object_Data_Procedures is primary key) is still the same
      assertTrue(Integer.parseInt(countDataProcs) == Integer.parseInt(countDataProcsAgain));

//    There is a new Object_Personal_Data = MODIFIED
     assertTrue(Integer.parseInt(countPersonalData) < Integer.parseInt(countPersonalDataAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }


  @Test
  public void test00003GovBrMapeamentoAndFontesAndPoliciesAndUsers() throws InterruptedException {
    try {

      jsonTestUtil("govbr/govbr-users.json", "$.rows", "govbr_users");
      jsonTestUtil("govbr/govbr-politicas.json", "$.rows", "govbr_politicas");
      jsonTestUtil("govbr/govbr-fontes.json", "$.rows", "govbr_fontes_de_dados");
      jsonTestUtil("govbr/govbr-mapeamentos.json", "$.rows", "govbr_mapeamento_de_processo");

      String getUser =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_7e2db45b5cd24722a7ea04f83b8c7b2c'))" +
                      ".in('Is_Responsible').as('email')" +
                      ".values('Object_Email_Address_Email').next().toString()").get().toString();
      assertEquals("gov-br@pontusvision.com", getUser, "User email for this Procedure");

      String countUser =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_7e2db45b5cd24722a7ea04f83b8c7b2c'))" +
                      ".in('Is_Responsible').as('email')" +
                      ".dedup().count().next().toString()").get().toString();
      assertEquals("1", countUser, "Number of Users for this Procedure");

      String getPolicy =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_6f48fd8a585b4f18af44ee8633269f2d'))" +
                      ".out('Has_Policy').as('policy')" +
                      ".has('Object_Data_Policy_Form_Id', eq('ro_ta_d40a2a1b6e6840e59214e15a3bd487cd_357035b395cc4d02920534a6976b1ead'))" +
                      ".values('Object_Data_Policy_Name').next().toString()").get().toString();
      assertEquals("TESTE", getPolicy, "One of the policies' name for this Procedure");

      String countPolicy =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_6f48fd8a585b4f18af44ee8633269f2d'))" +
                      ".out('Has_Policy').as('policy')" +
                      ".dedup().count().next().toString()").get().toString();
      assertEquals("2", countPolicy, "Number of Policies for this Procedure");

      String getSource = //Fontes de Dados
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_6f48fd8a585b4f18af44ee8633269f2d'))" +
                      ".out('Has_Data_Source').as('source')" +
                      ".has('Object_Data_Source_Form_Id', eq('ro_ta_0bbb856ebdaf400fb5f8abbc6bff4c08_f623bcbc04c646d2943385bd83345fe1'))" +
                      ".values('Object_Data_Source_Name').next().toString()").get().toString();
      assertEquals("BLU-RAY", getSource, "One of the sources' name for this Procedure");

      String countSource = //Fontes de Dados
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/mapeamento-de-processo')).as('event_ingestion')" +
                      ".out('Has_Ingestion_Event').as('data_procs')" +
                      ".has('Object_Data_Procedures_Form_Id', eq('ro_ta_fd0adee5c35840ce9da93a237784885d_6f48fd8a585b4f18af44ee8633269f2d'))" +
                      ".out('Has_Data_Source').as('source')" +
                      ".dedup().count().next().toString()").get().toString();
      assertEquals("4", countSource, "Number of Sources for this Procedure");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}