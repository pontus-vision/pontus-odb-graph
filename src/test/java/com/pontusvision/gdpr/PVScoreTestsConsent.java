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
@TestClassesOrder(18)
//@RunWith(JUnitPlatform.class)
public class PVScoreTestsConsent extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001ConsentScore() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-mapeamento-de-processo2.json", "$.queryResp[*].fields",
          "sharepoint_mapeamentos");

      jsonTestUtil("non-official-pv-extract-sharepoint-consentimentos2.json", "$.queryResp[*].fields",
          "sharepoint_consents");



      Map<String, Long> retVals = new HashMap<>();

      long score = (long) gdpr.getConsentScores(retVals);

      assertEquals(54L, score);


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00002DataBreachesScores() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-incidentes-de-seguranca-reportados.json", "$.queryResp[*].fields",
              "sharepoint_data_breaches");


      Map<String, Long> retVals = new HashMap<>();

      long score = (long) gdpr.getDataBreachesScores(retVals);

      assertEquals(37L, score, "Expected to lose 63 points because there is one Customer Data Stolen (External)");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00003DataBreachesScores() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-incidentes-de-seguranca-reportados.json", "$.queryResp[*].fields",
          "sharepoint_data_breaches");


      Map<String, Long> retVals = new HashMap<>();

      long score = (long) gdpr.getDataBreachesScores(retVals);

      assertEquals(37L, score, "Expected to lose 63 points because there is one Customer Data Stolen (External)");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
