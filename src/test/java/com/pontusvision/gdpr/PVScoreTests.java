package com.pontusvision.gdpr;

import com.pontusvision.gdpr.report.ReportTemplateRenderRequest;
import com.pontusvision.gdpr.report.ReportTemplateRenderResponse;
import com.pontusvision.gdpr.report.ReportTemplateUpsertRequest;
import com.pontusvision.gdpr.report.ReportTemplateUpsertResponse;
import com.pontusvision.graphutils.gdpr;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
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
@TestClassesOrder(11)
//@RunWith(JUnitPlatform.class)
public class PVScoreTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001TemplateUpsert() throws InterruptedException {
    try {

      // TODO: the file pv-extract-treinamentos.json has static dates; we need to update them
      // periodically; otherwise, the tests will start failing.


      jsonTestUtil("pv-extract-sharepoint-treinamentos2.json", "$.queryResp[*].fields",
              "sharepoint_treinamentos");


      Map<String, Long> retVals = new HashMap<>();

      long score =  gdpr.getAwarenessScores(retVals);

      assertEquals(54L, score);


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}