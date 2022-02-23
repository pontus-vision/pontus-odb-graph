package com.pontusvision.gdpr;

import com.pontusvision.graphutils.gdpr;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Date;
import java.util.HashMap;
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
@TestClassesOrder(18)
//@RunWith(JUnitPlatform.class)
public class PVScoreTestsPrivacyDocs extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001PrivacyDocsScore() throws InterruptedException {
    try {

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-privacy-docs3.json", "$.queryResp[*].fields",
          "sharepoint_privacy_docs");



      Map<String, Long> retVals = new HashMap<>();

      long score = (long) gdpr.getPrivacyDocsScores(retVals);

      assertEquals(0L, score);

      List legalActions = App.g.V().has("Metadata_Type_Object_Privacy_Docs",
          P.eq("Object_Privacy_Docs")).id().toList();

      Transaction trans = App.graph.tx();
      if (!trans.isOpen()){
        trans.open();
      }
      App.g.V(legalActions.get(0)).property("Object_Privacy_Docs_Date", new Date()).iterate();
      App.g.V(legalActions.get(1)).property("Object_Privacy_Docs_Date", new Date()).iterate();
      App.g.V(legalActions.get(2)).property("Object_Privacy_Docs_Date", new Date()).iterate();
      trans.commit();
      trans.close();

      score = (long) gdpr.getPrivacyDocsScores(retVals);

      assertEquals(50L, score);


      trans = App.graph.tx();
      if (!trans.isOpen()){
        trans.open();
      }
      App.g.V(legalActions.get(3)).property("Object_Privacy_Docs_Date", new Date()).iterate();
      trans.commit();
      trans.close();

      score = (long) gdpr.getPrivacyDocsScores(retVals);

      assertEquals(100L, score);






    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
