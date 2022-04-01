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
public class PVScoreTestsMeetings extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001MeetingsScore() throws InterruptedException {
    try {

      jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-p-and-pd-committee-meetings2.json", "$.queryResp[*].fields",
          "sharepoint_meetings");



      Map<String, Long> retVals = new HashMap<>();

      long score = (long) gdpr.getMeetingsScores(retVals);

      assertEquals(0L, score);

      List legalActions = App.g.V().has("Metadata_Type_Event_Meeting",
          P.eq("Event_Meeting")).id().toList();

      Transaction trans = App.graph.tx();
      if (!trans.isOpen()){
        trans.open();
      }
      App.g.V(legalActions.get(0)).property("Event_Meeting_Date", new Date()).iterate();
      App.g.V(legalActions.get(1)).property("Event_Meeting_Date", new Date()).iterate();
      trans.commit();
      trans.close();

      score = (long) gdpr.getMeetingsScores(retVals);

      assertEquals(50L, score);


      trans = App.graph.tx();
      if (!trans.isOpen()){
        trans.open();
      }
      App.g.V(legalActions.get(2)).property("Event_Meeting_Date", new Date()).iterate();
      trans.commit();
      trans.close();

      score = (long) gdpr.getMeetingsScores(retVals);

      assertEquals(100L, score);






    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
