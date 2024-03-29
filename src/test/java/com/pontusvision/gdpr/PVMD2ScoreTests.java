package com.pontusvision.gdpr;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pontusvision.graphutils.gdpr;
import org.junit.jupiter.api.*;

import java.lang.reflect.Type;
import java.util.List;
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
@TestClassesOrder(30)
//@RunWith(JUnitPlatform.class)
public class PVMD2ScoreTests extends AppTest {
  /**
   * Create the test0000 case
   *
   */



  @Test
  public void test00001Md2Stats() throws InterruptedException {
    try {
      jsonTestUtil("md2/md2.json", "$.person", "pv_md2");

      jsonTestUtil("pv-extract-file-ingest-md2.json", "$.value", "pv_file");
      jsonTestUtil("office365/pv-extract-o365-email-md2.json", "$.value", "pv_email");
      jsonTestUtil("office365/pv-extract-o365-email-md2-pt2.json", "$.value", "pv_email");

      String md2Stats = gdpr.getMd2Stats();
      Gson gson = new Gson();
      Type resultType = new TypeToken<List<Map<String, Object>>>(){}.getType();
      List<Map<String, Object>> result = gson.fromJson(md2Stats, resultType);

//      result.size() changed to 140 because we @Disabled Budibase @Test Solicitações, 2 metrics were taken: "BUDIBASE/CONTROLE-DE-SOLICITAÇÕES" and "BB CONTROLE DE SOLICITACOES"
//      assertEquals(138,result.size());
      assertEquals(140,result.size());
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
