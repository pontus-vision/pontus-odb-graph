package com.pontusvision.gdpr.pbr;

import com.pontusvision.gdpr.AnnotationTestsOrderer;
import com.pontusvision.gdpr.App;
import com.pontusvision.gdpr.AppTest;
import com.pontusvision.gdpr.TestClassesOrder;
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */

@TestClassOrder(AnnotationTestsOrderer.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassesOrder(33)
//@RunWith(JUnitPlatform.class)
public class PVPbrTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001FontesDeDados() throws InterruptedException {

    try {

      jsonTestUtil("sharepoint/pbr/fontes-de-dados.json", "$.queryResp[*].fields", "sharepoint_pbr_fontes_de_dados");

      String countFontesDeDados =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados'))" +
                      ".as('event-ingestions').dedup().count().next().toString()").get().toString();
      assertEquals("4",countFontesDeDados, "number of fontes de dados");

      String countDadosFontes2 =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados'))" +
                      ".as('event-ingestions').out('Has_Ingestion_Event').has('Object_Data_Source_Form_Id', eq('two'))" +
                      ".as('fontes-de-dados-two').out('Has_Sensitive_Data').dedup().count().next().toString()").get().toString();
        assertEquals("5",countDadosFontes2,
                "Apelido, Data da Aposentadoria, Situação no CIPA, Classificação do visto, PIS/PASEP/NIT");

        String getFontesEquipamentosDescription =
                App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados'))" +
                        ".as('event-ingestions').out('Has_Ingestion_Event').has('Object_Data_Source_Name', eq('EQUIPAMENTOS'))" +
                        ".as('fontes-de-dados').values('Object_Data_Source_Description').next().toString()").get().toString();
        assertEquals("EQUIPAMENTOS DE PROTEÇÃO INDIVIDUAL", getFontesEquipamentosDescription, "Description of EQUIPAMENTOS");

        String getFontes4System =
                App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SHAREPOINT/PBR/FONTES-DE-DADOS')).as('root')" +
                        ".out('Has_Ingestion_Event').has('Event_Group_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados')).as('group-ingestion')" +
                        ".out('Has_Ingestion_Event').has('Event_Ingestion_Type', eq('sharepoint/pbr/fontes-de-dados')).as('event-ingestion')" +
                        ".out('Has_Ingestion_Event').has('Object_Data_Source_Form_Id', eq('four')).as('fontes-de-dados')" +
                        ".out('Has_Module').has('Object_Module_Name', eq('FIXA')).as('tabela')" +
                        ".out('Has_Subsystem').has('Object_Subsystem_Name', eq('ONPREM')).as('onPrem')" +
                        ".out('Has_System').as('system')" +
                        ".values('Object_System_Name').next().toString()").get().toString();
        assertEquals("POLARIS", getFontes4System, "System of fontes-de-dados 4");


    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  // Testing new method/function SharepointUserRef
  @Test
  public void test00002SharepointUserRef() throws InterruptedException {

    try {

      jsonTestUtil("sharepoint/pbr/ropa.json", "$.queryResp[*].fields", "sharepoint_pbr_ropa");

      OGremlinResultSet resSet = App.graph.executeSql(
        "SELECT Person_Natural_Email as email " +
          "FROM Person_Natural " +
          "WHERE Person_Natural_Full_Name = 'GHOCHE, OMAR (LBN)'", Collections.EMPTY_MAP);

      String responsibleEmail = resSet.iterator().next().getRawResult().getProperty("email");
      resSet.close();
      assertEquals("oghoche@pontusvision.com", responsibleEmail, "Responsible email");

      resSet = App.graph.executeSql(
        "SELECT Person_Natural_Form_ID as form_id " +
          "FROM Person_Natural " +
          "WHERE Person_Natural_Full_Name = 'VITOR, PAULO (BRL)'", Collections.EMPTY_MAP);

      String alternativeResponsibleFormId = resSet.iterator().next().getRawResult().getProperty("form_id");
      resSet.close();
      assertEquals("324324324532", alternativeResponsibleFormId, "Alternative Responsible form id");

      resSet = App.graph.executeSql(
        "SELECT Object_Data_Source_Name as data_source " +
          "FROM Object_Data_Source", Collections.EMPTY_MAP);
      String dataSourceName = resSet.iterator().next().getRawResult().getProperty("data_source");
      resSet.close();
      assertEquals("SHAREPOINT/PBR/USERS", dataSourceName, "Data source name");

      resSet = App.graph.executeSql(
        "SELECT Person_Natural_Full_Name as name " +
          "FROM Person_Natural " +
          "WHERE Person_Natural_Form_ID = 838473834324", Collections.EMPTY_MAP);
      String personNaturalName = resSet.iterator().next().getRawResult().getProperty("name");
      resSet.close();
      assertEquals("GHOCHE, OMAR (LBN)", personNaturalName, "Person natural name");

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
