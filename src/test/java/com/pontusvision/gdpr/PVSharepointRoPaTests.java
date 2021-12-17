package com.pontusvision.gdpr;

import com.pontusvision.gdpr.report.ReportTemplateRenderRequest;
import com.pontusvision.gdpr.report.ReportTemplateRenderResponse;
import com.pontusvision.gdpr.report.ReportTemplateUpsertRequest;
import com.pontusvision.gdpr.report.ReportTemplateUpsertResponse;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(8)

public class PVSharepointRoPaTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  @Test
  public void test00001SharepointMapeamentoDeProcesso() throws InterruptedException {
    try {
      jsonTestUtil("pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");

      String EPGInterest =
              App.executor.eval("App.g.V().has('Object.Data_Policy.Type', eq('6')).in('Has_Policy')" +
                      ".properties('Object.Data_Procedures.Interested_Parties_Consulted').value().next().toString()").get().toString();
      assertEquals("EPG Advogados", EPGInterest, "EPG Advogados is a Party Interested in these Data Procedures");

      String numEPGParties =
              App.executor.eval("App.g.V().has('Object.Data_Procedures.Interested_Parties_Consulted', " +
                      "eq('EPG Advogados')).count().next().toString()").get().toString();
      assertEquals("2", numEPGParties, "2 registries have EPG Advogados as an Interested Party");

      String juridicoInterest =
              App.executor.eval("App.g.V().has('Object.Lawful_Basis.Description', eq('LEGÍTIMO INTERESSE DO CONTROLADOR'))" +
                      ".in('Has_Lawful_Basis_On').properties('Object.Data_Procedures.Interested_Parties_Consulted').value().next().toString()").get().toString();
      assertEquals("Jurídico Snowymountain", juridicoInterest, "Jurídico Snowymountain is a Party Interested in these Data Procedures");

      String numJuridicoParties =
              App.executor.eval("App.g.V().has('Object.Data_Procedures.Interested_Parties_Consulted', " +
                      "eq('Jurídico Snowymountain')).count().next().toString()").get().toString();
      assertEquals("4", numJuridicoParties, "4 registries have Jurídico Snowymountain as an Interested Party");

      String LIALawfulBasis =
              App.executor.eval("App.g.V().has('Object.Data_Procedures.Form_Id', eq('404'))" +
                      ".out('Has_Legitimate_Interests_Assessment')" +
                      ".properties('Object.Legitimate_Interests_Assessment" +
                      ".Lawful_Basis_Justification').value().next().toString()").get().toString();
      assertEquals("Legitimo interesse", LIALawfulBasis, "Lawful Basis Justification for this LIA is => Legitimo interesse");

      String LIAProcessingPurpose =
              App.executor.eval("App.g.V().has('Object.Data_Procedures.Form_Id', eq('406'))" +
                      ".out('Has_Legitimate_Interests_Assessment')" +
                      ".properties('Object.Legitimate_Interests_Assessment.Processing_Purpose').value()" +
                      ".next().toString()").get().toString();
      assertEquals("Sim", LIAProcessingPurpose, "Processing Purpose for this LIA is => SIM");

      String LIAPersonalDataTreatment =
              App.executor.eval("App.g.V().has('Object.Data_Source.Name', " +
                      "eq('SHAREPOINT/MAPEAMENTO-DE-PROCESSOS')).out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".out('Has_Ingestion_Event').out('Has_Legitimate_Interests_Assessment')" +
                      ".properties('Object.Legitimate_Interests_Assessment.Personal_Data_Treatment')" +
                      ".value().next().toString()").get().toString();
      assertEquals("Sim, é indispensável", LIAPersonalDataTreatment, "Personal Data Treatment for this LIA is => Sim, é indispensável");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }


}