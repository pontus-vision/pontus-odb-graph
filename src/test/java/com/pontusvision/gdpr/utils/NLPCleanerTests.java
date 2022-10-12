package com.pontusvision.gdpr.utils;

import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.pontusvision.gdpr.AnnotationTestsOrderer;
import com.pontusvision.gdpr.App;
import com.pontusvision.gdpr.AppTest;
import com.pontusvision.gdpr.TestClassesOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(15)

public class NLPCleanerTests extends AppTest {

  @Test
  public void test00001NormalizeNames() throws InterruptedException {
    try {

      String normalizeTest =
        com.pontusvision.utils.NLPCleaner.normalizeName("ŠŽšžŸÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖÙÚÛÜÝàáâãäåçèéêëìíîïðñòóôõöùúûüýÿ");
      assertEquals("SZSZYAAAAAACEEEEIIIINOOOOOUUUUYAAAAAACEEEEIIIINOOOOOUUUUYY", normalizeTest,
        "The resulting string is 2 chars shorter than the original string, because the letters Ð and ð could not be normalized.");

//      String returnEmptyString =
//        com.pontusvision.utils.NLPCleaner.normalizeName("`~^´¨àãâ"); // doesn't work for ` , ~ and ^
//      assertEquals("", returnEmptyString, "Should return empty string");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }


}