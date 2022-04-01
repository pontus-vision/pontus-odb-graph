package com.pontusvision.gdpr.budibase;

import com.pontusvision.gdpr.AnnotationTestsOrderer;
import com.pontusvision.gdpr.App;
import com.pontusvision.gdpr.AppTest;
import com.pontusvision.gdpr.TestClassesOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(25)

public class PVBudibaseUsersTests extends AppTest {

  @Test
  public void test00001BudibaseUsers() throws InterruptedException {

    jsonTestUtil("budibase/bb-users.json", "$.rows","bb_users");

    try {

      String omarUserEmail =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name'" +
                      ",eq('BUDIBASE/USERS')).as('data-source')" +
                      ".out('Has_Ingestion_Event').as('event-group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('OMAR GHOCHE'))" +
                      ".out('Uses_Email').as('email')" +
                      ".values('Object_Email_Address_Email').next().toString()").get().toString();
      assertEquals("oghoche@pontusvision.com", omarUserEmail, "Omar's email.");

      String getUserName3 =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name'" +
                      ",eq('BUDIBASE/USERS')).as('data-source')" +
                      ".out('Has_Ingestion_Event').as('event-group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('person-natural')" +
                      ".out('Uses_Email').as('email')" +
                      ".has('Object_Email_Address_Email', eq('amozena@pontusvision.com'))" +
                      ".in('Uses_Email').as('person-natural-again')" +
                      ".values('Person_Natural_Full_Name').next().toString()").get().toString();
      assertEquals("AMANDA MOZENA",getUserName3,"User 3 is Amanda Mozena.");

      String leoUserEmail =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name'" +
                      ",eq('BUDIBASE/USERS')).as('data-source')" +
                      ".out('Has_Ingestion_Event').as('event-group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('LEONARDO MARTINS'))" +
                      ".out('Uses_Email').as('email')" +
                      ".values('Object_Email_Address_Email').next().toString()").get().toString();
      assertEquals("lmartins@pontusvision.com", leoUserEmail, "Leo's email.");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}