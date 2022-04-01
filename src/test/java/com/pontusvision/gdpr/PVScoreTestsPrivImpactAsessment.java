package com.pontusvision.gdpr;

import com.pontusvision.graphutils.gdpr;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(28)
//@RunWith(JUnitPlatform.class)
public class PVScoreTestsPrivImpactAsessment extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001PrivacyImpactAssessmentScores() throws InterruptedException {
    try {

      jsonTestUtil("sharepoint/pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
          "sharepoint_data_sources");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-fontes-de-dados.json",
          "$.queryResp[*].fields",
          "sharepoint_fontes_de_dados");

      jsonTestUtil("budibase/bb-fontes-dados.json", "$.rows", "bb_fontes_de_dados");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json", "$.queryResp[*].fields",
          "sharepoint_mapeamentos");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-ropa.json",
          "$.queryResp[*].fields", "sharepoint_ropa");

      jsonTestUtil("budibase/bb-mapeamentos.json", "$.rows", "bb_mapeamento_de_processo");

      jsonTestUtil("budibase/bb-mitigacao-de-riscos.json", "$.rows", "bb_mitigacao_de_riscos");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-risk-mitigations.json", "$.queryResp[*].fields",
          "sharepoint_risk_mitigation");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-risk.json", "$.queryResp[*].fields",
          "sharepoint_risk");

      jsonTestUtil("budibase/bb-riscos-fontes-dados.json", "$.rows", "bb_riscos");



      Map<String, Long> retVals = new HashMap<>();

      long score = (long) gdpr.getPrivacyImpactAssessmentScores(retVals);

      // given:
      //   numDataProceduresWithoutDataSources = 0
      //   numDataProcedures = 141
      //   numDataSourcesWithoutRisks = 31
      //   numDataSources = 36
      //   numRisksWithoutMitigations = 17
      //   numRisks = 24
      //   scoreValue = 100L
      //
      //    scoreValue -= (numDataProceduresWithoutDataSources > 0) ? (long) (15L + 10L * numDataProceduresWithoutDataSources / numDataProcedures) : 0
      //    100L
      //    scoreValue -= (numDataSourcesWithoutRisks > 0) ? (long) (40L + 5L * numDataSourcesWithoutRisks / numDataSources) : 0
      //    100 - (40 + 5 * 31 /36) = 56
      //    scoreValue -= (numRisksWithoutMitigations > 0) ? (long) (20L + 10L * numRisksWithoutMitigations / numRisks) : 0
      //    56 - (20 + 10 * 17/24) = 29L
      assertEquals(29L, score);
      

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
