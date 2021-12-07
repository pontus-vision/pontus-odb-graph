package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

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

    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");

    try {

      String getCityParser =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('COMIDAS 1'))" +
                      ".out('Is_Located').properties('Location.Address.parser.city').value().next().toString()").get().toString();
      assertEquals("[angra dos reis, angradosreis]", getCityParser, "A cidade de COMIDAS 1 é Angra dos Reis");

      String getStateParser =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('COMIDAS 2'))" +
                      ".out('Is_Located').properties('Location.Address.parser.state').value().next().toString()").get().toString();
      assertEquals("[laspezia, santopadre, saopaulo, sp, stradaprovinciale]", getStateParser, "O estado de COMIDAS 2 é São Paulo");

//      String getNullAddress =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('PESSOA NOVA3'))" +
//                      ".bothE().otherV().path().toList()").get().toString();
//      getNullAddress = getNullAddress.replaceAll("[0-9]", "");
//      assertEquals("[path[v[#:], e[#:][#:-Has_Ingestion_Event->#:], v[#:]], path[v[#:], e[#:][#:-Has_Id_Card->#:], v[#:]]]",
//              getNullAddress, "Pessoa Nova 3 não tem EDGE Is_Located, porque todos os valores de Location.Address são null");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

  @Test
  public void test00002PloomesMergedExpanded() throws InterruptedException {

    jsonTestUtil("ploomes1-merge-totvs1-real.json", "$.value", "ploomes_clientes");

    try {

      String OrganisationUruguayFullAddress =
              App.executor.eval("App.g.V().has('Person.Organisation.Name', eq('URUGUAY AUTOMACAO INDL LTDA'))" +
                      ".out('Is_Located').properties('Location.Address.Full_Address').value().next().toString()").get().toString();
      assertEquals("RUA STA BARBARA, 456, STA BARBARA, PORTO ALEGRE - RS, 90420040, BRASIL",
              OrganisationUruguayFullAddress, "Full ADdress for Uruguay Automação LTDA");

      String getAddressParserSubUrb =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MATHEUS ROCHA'))" +
                      ".out('Is_Located').properties('Location.Address.parser.suburb').value().next().toString()").get().toString();
      assertEquals("[jardimlaranjeiras, jd laranjeiras]", getAddressParserSubUrb, "Matheus Rocha's neighborhood");

      String locationAddressPostCode =
              App.executor.eval("App.g.V().has('Object.Email_Address.Email', eq('primeiro@mail.com.br;segundo@mail.com.br'))" +
                      ".in('Uses_Email').out('Is_Located').properties('Location.Address.Post_Code').value().next().toString()").get().toString();
      assertEquals("54315085", locationAddressPostCode, "Post Code for Domingos Coml LTDA");

      String valueMapJoinville =
              App.executor.eval("App.g.V().has('Location.Address.parser.city', eq('[joinville]'))" +
                      ".in('Is_Located').valueMap('Person.Organisation.Name').next().toString()").get().toString();
      assertEquals("[Person.Organisation.Name:[DOCES JOINVILLE LTDA]]", valueMapJoinville,
              "valueMap for Person.Org.Name for Doces Joinville LTDA @ city of Joinville");

      String roadToGloria =
              App.executor.eval("App.g.V().has('Location.Address.parser.road', eq('[avenida das almondegas, avenidadasalmondegas]'))" +
                      ".in('Is_Located').properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("GLÓRIA KRACKOVSZI", roadToGloria, "Avenida das Almondegas is where Glória Krackovszi lives");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }
}
