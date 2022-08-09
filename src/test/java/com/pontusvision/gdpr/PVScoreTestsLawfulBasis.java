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
@TestClassesOrder(27)
//@RunWith(JUnitPlatform.class)
public class PVScoreTestsLawfulBasis extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001LawfulBasisScore() throws InterruptedException {
    try {

      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json", "$.queryResp[*].fields",
              "sharepoint_mapeamentos");



      Map<String, Long> retVals = new HashMap<>();

      long score = (long) gdpr.getLawfulBasisScores(retVals);

      assertEquals(100L, score);


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}