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


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(32)

public class PVGovBrPoliticasTests extends AppTest {

  @Test
  public void test00001GovBrPoliticas() throws InterruptedException {

    jsonTestUtil("govbr/govbr-politicas.json", "$.rows", "bb_politicas");

    try {

      String policiesText =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/políticas')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('policies')" +
                      ".has('Object_Policies_Form_Id'" +
                      ",eq('ro_ta_d40a2a1b6e6840e59214e15a3bd487cd_b369526f21b94395a0d98b7dea536457'))" +
                      ".values('Object_Policies_Text').next().toString()").get().toString();
      assertEquals("A partir do momento que um Dado Pessoal é necessário para realização_de_determinado processo ...",
              policiesText, "This polices' text.");

      String policiesType =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/políticas')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('policies')" +
                      ".has('Object_Policies_Form_Id'" +
                      ",eq('ro_ta_d40a2a1b6e6840e59214e15a3bd487cd_6be56a3bbd294dc88f9367f3456fade'))" +
                      ".values('Object_Policies_Type').next().toString()").get().toString();
      assertEquals("Privacidade e Proteção_de_Dados Pessoais - Site",
              policiesType, "This policies' type.");

//      String dataSourceName =
//              App.executor.eval("App.g.V().has('Object_Policies_Form_Id'" +
//                      ",eq('ro_ta_d40a2a1b6e6840e59214e15a3bd487cd_cb354fa4aa4443eeb9a2e170ae9d5b3d'))" +
//                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
//                      ".in('Has_Ingestion_Event').as('group-ingestion')" +
//                      ".in('Has_Ingestion_Event').as('data-source')" +
//                      ".values('Object_Data_Source_Name').next().toString()").get().toString();
//      assertEquals("BUDIBASE/POLÍTICAS",dataSourceName, "This data source's name.");

      String policiesName =
              App.executor.eval("App.g.V().has('Object_Policies_Form_Id'" +
                      ",eq('ro_ta_d40a2a1b6e6840e59214e15a3bd487cd_cb354fa4aa4443eeb9daedc544326'))" +
                      ".values('Object_Policies_Name').next().toString()").get().toString();
      assertEquals("TEST3",policiesName, "This policies' name.");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using bb_politicas POLE
  @Test
  public void test00002UpsertGovBrPoliticas() throws InterruptedException {
    try {

      jsonTestUtil("govbr/govbr-politicas.json", "$.rows", "bb_politicas");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/políticas'))" +
                      ".count().next().toString()").get().toString();

      String countPolicies =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/políticas'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Policies', eq('Object_Policies'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("govbr/govbr-politicas-2.json", "$.rows", "bb_politicas");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/políticas'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countPoliciesAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/políticas'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Policies', eq('Object_Policies'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the gov.br/políticas Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data (Object_Policies is primary key) is still the same
      assertTrue(Integer.parseInt(countPolicies) == Integer.parseInt(countPoliciesAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}