package com.pontusvision.gdpr;

import com.pontusvision.graphutils.PontusJ2ReportingFunctions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(1001)

public class PVLiaScoreTests extends AppTest {

  @Test
  public void test00001liaScore() throws InterruptedException {

    jsonTestUtil("webiny/webiny-fontes.json", "$.data.listFontesDeDados.data[*]", "webiny_data_source");
    jsonTestUtil("webiny/webiny-mapeamento-de-processos.json", "$.data.listMapeamentoDeProcessos.data[*]", "webiny_ropa");
    jsonTestUtil("webiny/webiny-contratos.json", "$.data.listContratos.data[*]", "webiny_contracts");
    jsonTestUtil("webiny/webiny-incidentes.json", "$.data.listIncidentesDeSegurancaReportados.data[*]", "webiny_data_breaches");
    jsonTestUtil("webiny/webiny-lia.json", "$.data.listInteressesLegitimos.data[*]", "webiny_lia");

    try {

      // The greater the Score ... THE WORSE !!!!!!!!!

      String liaRid = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Legitimate_Interests_Assessment_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63fe448bf7f21e0008300259#0001\"\n" +
                      "  }\n" +
                      "]", "Object_Legitimate_Interests_Assessment",
              new String[]{"Object_Legitimate_Interests_Assessment_Form_Id"});

      int liaEthicsScore = PontusJ2ReportingFunctions.liaScoreEthics(liaRid);
      assertEquals(120, liaEthicsScore);
      // Object_Legitimate_Interests_Assessment_Ethical_Impact is not empty = "ética"

      int liaContractHasMinorsScore = PontusJ2ReportingFunctions.liaScoreContractHasMinors(liaRid);
      assertEquals(120, liaContractHasMinorsScore);

      int liaStrategicImpactScore = PontusJ2ReportingFunctions.liaScoreStrategicImpact(liaRid);
      assertEquals(5, liaStrategicImpactScore);
      // Object_Legitimate_Interests_Assessment_Strategic_Impact is not empty = "atender lei LGPD"

      int liaScoreEssential = PontusJ2ReportingFunctions.liaScoreEssential(liaRid);
      assertEquals(120, liaScoreEssential);
      // Object_Legitimate_Interests_Assessment_Is_Essential = true

      int liaBreachJustification = PontusJ2ReportingFunctions.liaScoreBreachJustification(liaRid);
      assertEquals(120, liaBreachJustification);
      // Object_Legitimate_Interests_Assessment_Breach_Of_Subject_Rights_Justification is not empty = "liberdade de imprensa"

      int ropaSensitiveData = PontusJ2ReportingFunctions.liaScoreRopaSensitiveData(liaRid);
      assertEquals(120, ropaSensitiveData);
      // Webiny RoPA attached has Object_Sensitive_Data

      PontusJ2ReportingFunctions.liaScoreRopaTypePerson(liaRid);

      int liaDataOrigin = PontusJ2ReportingFunctions.liaScoreDataOrigin(liaRid);
      assertEquals(5, liaDataOrigin);
      // Object_Legitimate_Interests_Assessment_Is_Data_From_Natural_Person = true

//      PontusJ2ReportingFunctions.liaScoreRipdAuthorityNotified(liaRid);

//-------------------------------------------- Testing on NULL values -------------------------------------------------

      liaRid = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Legitimate_Interests_Assessment_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"63fe448bf7f21e0008300259#0356\"\n" +
                      "  }\n" +
                      "]", "Object_Legitimate_Interests_Assessment",
              new String[]{"Object_Legitimate_Interests_Assessment_Form_Id"});

      liaEthicsScore = PontusJ2ReportingFunctions.liaScoreEthics(liaRid);
      assertEquals(5, liaEthicsScore);

      liaStrategicImpactScore = PontusJ2ReportingFunctions.liaScoreStrategicImpact(liaRid);
      assertEquals(10, liaStrategicImpactScore);

      liaScoreEssential = PontusJ2ReportingFunctions.liaScoreEssential(liaRid);
      assertEquals(5, liaScoreEssential);

      liaBreachJustification = PontusJ2ReportingFunctions.liaScoreBreachJustification(liaRid);
      assertEquals(5, liaBreachJustification);

      liaDataOrigin = PontusJ2ReportingFunctions.liaScoreDataOrigin(liaRid);
      assertEquals(20, liaDataOrigin);

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

  @Test
  public void test00002liaScoreSV() throws InterruptedException {

    jsonTestUtil("sharepoint/pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields", "sharepoint_fontes_de_dados");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json", "$.queryResp[*].fields", "sharepoint_mapeamentos");
    jsonTestUtil("sharepoint/devtools-extract-sharepoint-contracts.json", "$.queryResp[*].fields", "sharepoint_contracts");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-incidentes-de-seguranca-reportados.json", "$.queryResp[*].fields", "sharepoint_data_breaches");
    jsonTestUtil("sharepoint/devtools-extract-sharepoint-lia.json", "$.queryResp[*].fields", "sharepoint_lia");

    try {

      // The greater the Score ... THE WORSE !!!!!!!!!

      String liaRid1 = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Legitimate_Interests_Assessment_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"3\"\n" +
                      "  }\n" +
                      "]", "Object_Legitimate_Interests_Assessment",
              new String[]{"Object_Legitimate_Interests_Assessment_Form_Id"});

      int lia1SVScoreRopaSensitiveData = PontusJ2ReportingFunctions.liaSVScoreRopaSensitiveData(liaRid1);
      assertEquals(120, lia1SVScoreRopaSensitiveData, "this LIA is attached to a RoPA WITH sensitive data");
      // Object_Legitimate_Interests_Assessment_Ethical_Impact is not empty = "ética"

      String liaRid2 = gridWrapperGetRid("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Object_Legitimate_Interests_Assessment_Form_Id\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"1\"\n" +
                      "  }\n" +
                      "]", "Object_Legitimate_Interests_Assessment",
              new String[]{"Object_Legitimate_Interests_Assessment_Form_Id"});

      int lia2SVScoreRopaSensitiveData = PontusJ2ReportingFunctions.liaSVScoreRopaSensitiveData(liaRid2);
      assertEquals(5, lia2SVScoreRopaSensitiveData, "this LIA is attached to a RoPA WITHOUT sensitive data");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

}