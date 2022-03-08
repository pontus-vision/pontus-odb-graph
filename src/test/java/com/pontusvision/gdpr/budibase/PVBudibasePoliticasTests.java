package com.pontusvision.gdpr.budibase;

import com.pontusvision.gdpr.AnnotationTestsOrderer;
import com.pontusvision.gdpr.App;
import com.pontusvision.gdpr.AppTest;
import com.pontusvision.gdpr.TestClassesOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(23)

public class PVBudibasePoliticasTests extends AppTest {

  @Test
  public void test00001BudibasePoliticas() throws InterruptedException {

    jsonTestUtil("budibase/bb-politicas.json", "$.rows", "bb_politicas");

    try {

      String policyText =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/políticas')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('policies')" +
                      ".has('Object_Policies_Form_Id'" +
                      ",eq('ro_ta_d40a2a1b6e6840e59214e15a3bd487cd_b369526f21b94395a0d98b7b79961639'))" +
                      ".values('Object_Policies_Text').next().toString()").get().toString();
      assertEquals("A partir do momento que um Dado Pessoal é necessário para realização de determinado processo ...",
              policyText, "This policy's text.");

      String policyType =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type'" +
                      ",eq('budibase/políticas')).as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('policies')" +
                      ".has('Object_Policies_Form_Id'" +
                      ",eq('ro_ta_d40a2a1b6e6840e59214e15a3bd487cd_6be56a3bbd294dc88f9367fb7734d961'))" +
                      ".values('Object_Policies_Type').next().toString()").get().toString();
      assertEquals("Privacidade e Proteção de Dados Pessoais - Site",
              policyType, "This policy's type.");

      String dataSourceName =
              App.executor.eval("App.g.V().has('Object_Policies_Form_Id'" +
                      ",eq('ro_ta_d40a2a1b6e6840e59214e15a3bd487cd_6be56a3bbd294dc88f9367fb7734d961'))" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('group-ingestion')" +
                      ".in('Has_Ingestion_Event').as('data-source')" +
                      ".values('Object_Data_Source_Name').next().toString()").get().toString();
      assertEquals("BUDIBASE/POLÍTICAS",dataSourceName, "This policy's type.");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using bb_politicas POLE
  @Test
  public void test00002UpsertBudibasePoliticas() throws InterruptedException {
    try {

      jsonTestUtil("budibase/bb-politicas.json", "$.rows", "bb_politicas");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/políticas'))" +
                      ".count().next().toString()").get().toString();

      String countDataBreach =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/políticas'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Policies', eq('Object_Policies'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("budibase/bb-politicas-2.json", "$.rows", "bb_politicas");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/políticas'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countDataBreachAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/políticas'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Policies', eq('Object_Policies'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the budibase/políticas Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countDataBreach) == Integer.parseInt(countDataBreachAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}