package com.pontusvision.gdpr;

import com.pontusvision.graphutils.VisJSGraph;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(13)
//@RunWith(JUnitPlatform.class)
public class PVMD2ProfillingTests extends AppTest {

  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001MD2Profilling() throws InterruptedException {
    try {

      csvTestUtil("md2-profilling.csv", "pv_md2");

      String adaAlexander =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('63152378866')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("ADA ALEXANDER", adaAlexander, "This CPF belongs to Ada Alexander");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
