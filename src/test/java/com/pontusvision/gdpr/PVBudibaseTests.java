package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

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

      String zeroInterest =
              App.executor.eval("App.g.V().has('Object_Data_Policy_Type', eq('Contratos-Fornecedores')).in('Has_Policy')" +
                      ".properties('Object_Data_Procedures_Interested_Parties_Consulted').value().next().toString()").get().toString();
      assertEquals("", zeroInterest, "No one is interested in this Process");

      String numParties =
              App.executor.eval("App.g.V().has('Object_Data_Procedures_Interested_Parties_Consulted', " +
                      "eq('teste@email.com, teste2@mail.com, teste3@mail.com')).count().next().toString()").get().toString();
      assertEquals("3", numParties, "3 registries with same parties interested");

      String juridicoInterest =
              App.executor.eval("App.g.V().has('Object_Lawful_Basis_Description', eq('LEGÍTIMO INTERESSE DO CONTROLADOR'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("7", juridicoInterest, "Jurídico Snowymountain is a Party Interested in these Data Procedures");

//      String numJuridicoParties =
//              App.executor.eval("App.g.V().has('Object_Data_Procedures_Interested_Parties_Consulted', " +
//                      "eq('Jurídico Snowymountain')).count().next().toString()").get().toString();
//      assertEquals("4", numJuridicoParties, "4 registries have Jurídico Snowymountain as an Interested Party");
//
//      String LIALawfulBasis =
//              App.executor.eval("App.g.V().has('Object_Data_Procedures_Form_Id', eq('404'))" +
//                      ".out('Has_Legitimate_Interests_Assessment')" +
//                      ".properties('Object_Legitimate_Interests_Assessment_Lawful_Basis_Justification')" +
//                      ".value().next().toString()").get().toString();
//      assertEquals("Legitimo interesse", LIALawfulBasis, "Lawful Basis Justification for this LIA is => Legitimo interesse");
//
//      String LIAProcessingPurpose =
//              App.executor.eval("App.g.V().has('Object_Data_Procedures_Form_Id', eq('406'))" +
//                      ".out('Has_Legitimate_Interests_Assessment')" +
//                      ".properties('Object_Legitimate_Interests_Assessment_Processing_Purpose').value()" +
//                      ".next().toString()").get().toString();
//      assertEquals("Sim", LIAProcessingPurpose, "Processing Purpose for this LIA is => SIM");
//
//      String LIAPersonalDataTreatment =
//              App.executor.eval("App.g.V().has('Object_Data_Source_Name', " +
//                      "eq('SHAREPOINT/MAPEAMENTO-DE-PROCESSOS')).out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
//                      ".out('Has_Ingestion_Event').out('Has_Lawful_Basis_On').has('Object_Lawful_Basis_Description', " +
//                      "eq('EXECUÇÃO DE CONTRATO OU DE PROCEDIMENTOS PRELIMINARES A CONTRATO, A PEDIDO DO TITULAR'))" +
//                      ".in('Has_Lawful_Basis_On').out('Has_Legitimate_Interests_Assessment')" +
//                      ".properties('Object_Legitimate_Interests_Assessment_Personal_Data_Treatment')" +
//                      ".value().next().toString()").get().toString();
//      assertEquals("Sim, é indispensável - processo 1", LIAPersonalDataTreatment, "Personal Data Treatment for this LIA is => Sim, é indispensável");

    } catch (ExecutionException e) {
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

}