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
@TestClassesOrder(33)

public class PVGovBrFontesDeDadosTests extends AppTest {

  @Test
  public void test00001GovBrFontesDeDados() throws InterruptedException {
    try {
      jsonTestUtil("govbr/govbr-fontes.json", "$.rows", "govbr_fontes_de_dados");

      String queryPrefix = "App.g.V().has('Object_Data_Source_Name', eq('BUDIBASE/FONTES-DE-DADOS'))";

      String countDataSources =
              App.executor.eval(queryPrefix +
                      ".count().next().toString()").get().toString();
      assertEquals("2", countDataSources, "One DataSource comes from Budibase, the other from Gov.br");

      String countEventGroupIngestions =
              App.executor.eval(queryPrefix +
                      ".both()" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", countEventGroupIngestions);

      String countEventIngestions =
              App.executor.eval(queryPrefix +
                      ".out().out()" +
                      ".count().next().toString()").get().toString();
      assertEquals("10", countEventIngestions);

      String countObjectDataSourcesIngested =
              App.executor.eval(queryPrefix +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".count().next().toString()").get().toString();
      assertEquals("10", countObjectDataSourcesIngested, "Data Sources Ingested");

      String numPersonalData =
              App.executor.eval(queryPrefix +
              ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
              ".out('Has_Sensitive_Data')" +
              ".has('Metadata_Type_Object_Sensitive_Data', eq('Object_Sensitive_Data')).id().toSet()" +
              ".size().toString()").get().toString();
      assertEquals("27", numPersonalData, "Number of personal data");

      String dataSourceType = App.executor.eval(queryPrefix + ".as('data_source')" +
              ".out('Has_Ingestion_Event').as('event_group')" +
              ".out('Has_Ingestion_Event').as('event_ingestion')" +
              ".out('Has_Ingestion_Event').as('fontes_de_dados')" +
              ".has('Object_Data_Source_Form_Id', eq('ro_ta_0bbb856ebdaf400fb5f8abbc6bff4c08_ca627d7dfb974178a6c2ca7f3cf74dc4'))" +
              ".values('Object_Data_Source_Type').next().toString()").get().toString();
      assertEquals("OneDrive", dataSourceType, "SubSystem for this Data Source");

      String dataSourceEngine = App.executor.eval(queryPrefix + ".as('data_source')" +
              ".out('Has_Ingestion_Event').as('event_group')" +
              ".out('Has_Ingestion_Event').as('event_ingestion')" +
              ".out('Has_Ingestion_Event').as('fontes_de_dados')" +
              ".has('Object_Data_Source_Form_Id', eq('ro_ta_0bbb856ebdaf400fb5f8abbc6bff4c08_f9063db6f854438ca08a4997e385c269'))" +
              ".values('Object_Data_Source_Engine').next().toString()").get().toString();
      assertEquals("Salesforce", dataSourceEngine, "SubSystem for this Data Source");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using govbr_fontes_de_dados POLE
  @Test
  public void test00002UpsertGovBrFontesDeDados() throws InterruptedException {
    try {

      boolean hasModuleModified = true;

      jsonTestUtil("govbr/govbr-fontes.json", "$.rows", "govbr_fontes_de_dados");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".count().next().toString()").get().toString();

      String countDataSources =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Source', eq('Object_Data_Source'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countModules =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').out('Has_Module').has('Metadata_Type_Object_Module', eq('Object_Module'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countSubsystems =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').out('Has_Module').out('Has_Subsystem').has('Metadata_Type_Object_Subsystem', eq('Object_Subsystem'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countSystems =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').out('Has_Module').out('Has_Subsystem').out('Has_System').has('Metadata_Type_Object_System', eq('Object_System'))" +
                      ".dedup().count().next().toString()").get().toString();

      try {
        App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                ".out('Has_Ingestion_Event').out('Has_Module').has('Metadata_Type_Object_Module', eq('Object_Module'))" +
                ".has('Object_Module_Name', eq('modified')).next().toString()").get().toString();
      }
      catch (Exception e) {
        hasModuleModified = false;
      }

      jsonTestUtil("govbr/govbr-fontes-2.json", "$.rows", "govbr_fontes_de_dados");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countDataSourcesAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Object_Data_Source', eq('Object_Data_Source'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countModulesAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').out('Has_Module').has('Metadata_Type_Object_Module', eq('Object_Module'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countSubsystemsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').out('Has_Module').out('Has_Subsystem').has('Metadata_Type_Object_Subsystem', eq('Object_Subsystem'))" +
                      ".dedup().count().next().toString()").get().toString();

      String countSystemsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('budibase/fontes-de-dados'))" +
                      ".out('Has_Ingestion_Event').out('Has_Module').out('Has_Subsystem').out('Has_System').has('Metadata_Type_Object_System', eq('Object_System'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the budibase/fontes-de-dados Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data  (Object_Data_Source is primary key) is still the same
      assertTrue(Integer.parseInt(countDataSources) == Integer.parseInt(countDataSourcesAgain));

      if (!hasModuleModified) {
//    This shows that one new Module, Subsystem, and System (named MODIFIED) were added
        assertTrue(Integer.parseInt(countModules) < Integer.parseInt(countModulesAgain));
        assertTrue(Integer.parseInt(countSubsystems) < Integer.parseInt(countSubsystemsAgain));
        assertTrue(Integer.parseInt(countSystems) < Integer.parseInt(countSystemsAgain));
      }

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00003GovBrFontesAndUsersAndPolicies() throws InterruptedException {
    try {

      jsonTestUtil("govbr/govbr-users.json", "$.rows", "govbr_users");
      jsonTestUtil("govbr/govbr-politicas.json", "$.rows", "govbr_politicas");
      jsonTestUtil("govbr/govbr-fontes.json", "$.rows", "govbr_fontes_de_dados");

      String responsible =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('BUDIBASE/FONTES-DE-DADOS'))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event')" +
                      ".has('Object_Data_Source_Form_Id', eq('ro_ta_0bbb856ebdaf400fb5f8abbc6bff4c08_0d04ac78f65f4b789fa930d294995f35'))" +
                      ".in('Is_Responsible').has('Metadata_Type_Object_Email_Address', eq('Object_Email_Address')).in('Uses_Email')" +
                      ".values('Person_Natural_Full_Name').next().toString()").get().toString();
      assertEquals("GOV BR", responsible);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}