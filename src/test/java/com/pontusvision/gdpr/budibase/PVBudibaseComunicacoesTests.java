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
@TestClassesOrder(24)

public class PVBudibaseComunicacoesTests extends AppTest {

  @Test
  public void test00001BudibaseComunicacoesPPD() throws InterruptedException {

    jsonTestUtil("budibase/bb-comunicacoes-ppd.json", "$.rows","bb_comunicacoes_ppd");

    try {

      String privacyDoc1 =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SHAREPOINT/PRIVACY-DOCS'))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".has('Metadata_Type_Object_Privacy_Docs',eq('Object_Privacy_Docs'))" +
                      ".values('Object_Privacy_Docs_Description').next().toString()").get().toString();
      assertEquals("o que muda com as novas leis de segurança dos dados pessoais dos brasileiros.", privacyDoc1,
              "Privacy Doc 1's Description.");

      String privacyDoc2 =
              App.executor.eval("App.g.V().has('Object_Privacy_Docs_Form_Id',eq('2'))" +
                      ".values('Object_Privacy_Docs_Title').next().toString()").get().toString();
      assertEquals("Cartaz - cuide de sua identidade virtual!", privacyDoc2,"Privacy Doc 2's Title");

      String privacyDoc3 =
              App.executor.eval("App.g.V().has('Object_Privacy_Docs_Description',eq('Introdção a LGPD'))" +
                      ".values('Object_Privacy_Docs_Date').next().toString()").get().toString();
      assertEquals(dtfmt.parse("Mon Jan 10 01:01:01 GMT 2022"), dtfmt.parse(privacyDoc3),
              "Privacy Doc 3's Date");

      String privacyDoc4 =
              App.executor.eval("App.g.V().has('Object_Privacy_Docs_Description',eq('Introdção a LGPD'))" +
                      ".values('Object_Privacy_Docs_URL').next().toString()").get().toString();
      assertEquals("www.wikipedia.com.br",privacyDoc4,"Privacy Doc 4's URL");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

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