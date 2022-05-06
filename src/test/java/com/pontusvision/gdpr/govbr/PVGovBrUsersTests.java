package com.pontusvision.gdpr.govbr;

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
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(31)

public class PVGovBrUsersTests extends AppTest {

  @Test
  public void test00001GovBrUsers() throws InterruptedException {

    jsonTestUtil("govbr/govbr-users.json", "$.rows","govbr_users");

    try {

      String omarUserEmail =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name'" +
                      ",eq('GOV.BR/USERS')).as('data-source')" +
                      ".out('Has_Ingestion_Event').as('event-group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('GOV BR'))" +
                      ".out('Uses_Email').as('email')" +
                      ".values('Object_Email_Address_Email').next().toString()").get().toString();
      assertEquals("gov-br@pontusvision.com", omarUserEmail, "GOV's email.");

      String getUserName =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name'" +
                      ",eq('GOV.BR/USERS')).as('data-source')" +
                      ".out('Has_Ingestion_Event').as('event-group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('person-natural')" +
                      ".out('Uses_Email').as('email')" +
                      ".has('Object_Email_Address_Email', eq('gov-br@pontusvision.com'))" +
                      ".in('Uses_Email').as('person-natural-again')" +
                      ".values('Person_Natural_Full_Name').next().toString()").get().toString();
      assertEquals("GOV BR",getUserName,"User is Gov Br.");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using govbr_users POLE
  @Test
  public void test00002UpsertGovBrUsers() throws InterruptedException {
    try {

      jsonTestUtil("govbr/govbr-users.json", "$.rows", "govbr_users");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/users'))" +
                      ".count().next().toString()").get().toString();

      String countEmails =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/users'))" +
                      ".out('Has_Ingestion_Event').out('Uses_Email')" +
                      ".has('Metadata_Type_Object_Email_Address', eq('Object_Email_Address'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countPerson =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/users'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
                      ".dedup().count().next().toString()").get().toString();


      jsonTestUtil("govbr/govbr-users-2.json", "$.rows", "govbr_users");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/users'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countEmailsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/users'))" +
                      ".out('Has_Ingestion_Event').out('Uses_Email')" +
                      ".has('Metadata_Type_Object_Email_Address', eq('Object_Email_Address'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countPersonAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('gov.br/users'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the gov.br/users Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data (Object_Email_Address is primary key) is still the same
      assertTrue(Integer.parseInt(countEmails) == Integer.parseInt(countEmailsAgain));

//    New Person_Natural (Full_Name = MODIFIED MODIFIED) was added
      assertTrue(Integer.parseInt(countPerson) < Integer.parseInt(countPersonAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}