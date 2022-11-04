package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */

@TestClassOrder(AnnotationTestsOrderer.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassesOrder(7)
//@RunWith(JUnitPlatform.class)
public class PVPloomesTest extends AppTest {

  @Test
  public void test00001PloomesOneExpanded() throws InterruptedException {

    jsonTestUtil("ploomes/ploomes1.json", "$.value", "ploomes_clientes");

    try {

      String getCityParser =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('COMIDAS 1'))" +
                      ".out('Is_Located').properties('Location_Address_parser_city').value().next().toString()").get().toString();
      assertEquals("[angra dos reis, angradosreis]", getCityParser, "A cidade de COMIDAS 1 é Angra dos Reis");

      String getStateParser =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('COMIDAS 2'))" +
                      ".out('Is_Located').properties('Location_Address_parser_state').value().next().toString()").get().toString();
      assertEquals("[laspezia, santopadre, saopaulo, sp, stradaprovinciale]", getStateParser, "O estado de COMIDAS 2 é São Paulo");

      // test Person_Natural_Last_Update_Date
        String getPersonNaturalLastUpdateDate =
                App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('ploomes/clientes'))" +
                        ".in('Has_Ingestion_Event').has('Person_Natural_Full_Name', eq('COMIDAS 2'))" +
                        ".properties('Person_Natural_Last_Update_Date').value().next().toString()").get().toString();
        assertEquals(dtfmt.parse("Thu Feb 27 11:12:12 UTC 2020"), dtfmt.parse(getPersonNaturalLastUpdateDate),
                "A data de atualização de COMIDAS 2 é Sat May 29 11:47:29 UTC 2021");

      // test Person_Organisation_Last_Update_Date for Pessoa Nova5
        String getPersonOrganisationLastUpdateDate =
                App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('ploomes/clientes'))" +
                        ".in('Has_Ingestion_Event').has('Person_Organisation_Name', eq('PESSOA NOVA5'))" +
                        ".properties('Person_Organisation_Last_Update_Date').value().next().toString()").get().toString();
        assertEquals(dtfmt.parse("Sat May 29 11:47:46 UTC 2021"), dtfmt.parse(getPersonOrganisationLastUpdateDate),
                "A data de atualização de Pessoa Nova5 é Sat May 29 11:47:46 UTC 2021");

//      String getNullAddress =
//              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('PESSOA NOVA3'))" +
//                      ".bothE().otherV().path().toList()").get().toString();
//      getNullAddress = getNullAddress.replaceAll("[0-9]", "");
//      assertEquals("[path[v[#:], e[#:][#:-Has_Ingestion_Event->#:], v[#:]], path[v[#:], e[#:][#:-Has_Id_Card->#:], v[#:]]]",
//              getNullAddress, "Pessoa Nova 3 não tem EDGE Is_Located, porque todos os valores de Location_Address são null");

    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

  @Test
  public void test00002PloomesMergedExpanded() throws InterruptedException {

    jsonTestUtil("ploomes/ploomes1-merge-totvs1-real.json", "$.value", "ploomes_clientes");

    try {

      String OrganisationUruguayFullAddress =
              App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('URUGUAY AUTOMACAO INDL LTDA'))" +
                      ".out('Is_Located').properties('Location_Address_Full_Address').value().next().toString()").get().toString();
      assertEquals("RUA STA BARBARA, 456, STA BARBARA, PORTO ALEGRE - RS, 90420040, BRASIL",
              OrganisationUruguayFullAddress, "Full ADdress for Uruguay Automação LTDA");

      String getAddressParserSubUrb =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MATHEUS ROCHA'))" +
                      ".out('Is_Located').properties('Location_Address_parser_suburb').value().next().toString()").get().toString();
      assertEquals("[jardimlaranjeiras, jd laranjeiras]", getAddressParserSubUrb, "Matheus Rocha's neighborhood");

      String locationAddressPostCode =
              App.executor.eval("App.g.V().has('Object_Email_Address_Email', eq('primeiro@mail.com.br;segundo@mail.com.br'))" +
                      ".in('Uses_Email').out('Is_Located').properties('Location_Address_Post_Code').value().next().toString()").get().toString();
      assertEquals("54315085", locationAddressPostCode, "Post Code for Domingos Coml LTDA");

      String valueMapJoinville =
              App.executor.eval("App.g.V().has('Location_Address_parser_city', eq('[joinville]'))" +
                      ".in('Is_Located').valueMap('Person_Organisation_Name').next().toString()").get().toString();
      assertEquals("[Person_Organisation_Name:[DOCES JOINVILLE LTDA]]", valueMapJoinville,
              "valueMap for Person.Org.Name for Doces Joinville LTDA @ city of Joinville");

      String roadToGloria =
              App.executor.eval("App.g.V().has('Location_Address_parser_road', eq('[avenida das almondegas, avenidadasalmondegas]'))" +
                      ".in('Is_Located').properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("GLÓRIA KRACKOVSZI", roadToGloria, "Avenida das Almondegas is where Glória Krackovszi lives");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }
}
