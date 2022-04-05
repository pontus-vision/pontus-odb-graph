package com.pontusvision.gdpr;

import com.pontusvision.graphutils.gdpr;
import org.junit.jupiter.api.*;

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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassesOrder(11)
//@RunWith(JUnitPlatform.class)
public class PVMD2ScoreTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */
  @BeforeAll
  public void prepareData() throws InterruptedException {
    jsonTestUtil("md2/md2.json", "$.person", "pv_md2");

    jsonTestUtil("pv-extract-file-ingest-md2.json", "$.value", "pv_file");
    jsonTestUtil("office365/pv-extract-o365-email-md2.json", "$.value", "pv_email");
    jsonTestUtil("office365/pv-extract-o365-email-md2-pt2.json", "$.value", "pv_email");


  }

  @Test
  public void test00001TemplateUpsert() throws InterruptedException {
    try {

      String md2Stats = gdpr.getMd2Stats();

//      Map<String, Long> retVals = new HashMap<>();
//
//      long score =  gdpr.getAwarenessScores(retVals);
//
//      assertEquals(54L, score);


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }



}
