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
@TestClassesOrder(5)
//@RunWith(JUnitPlatform.class)
public class PVTotvsTest extends AppTest {

  @Test
  public void test00001TotvsProtheusSa1Clientes() throws InterruptedException {

    jsonTestUtil("totvs1-real.json", "$.objs", "totvs_protheus_sa1_clientes");

    try {

      String OrganisationUruguayEdgeCount =
              App.executor.eval("App.g.V().has('Person.Organisation.Name', eq('URUGUAY AUTOMACAO INDL LTDA'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("5", OrganisationUruguayEdgeCount, "1 Has_Ingestion_Event + 1 Uses_Email + 1 Is_Located " +
              "+ 1 Has_Phone + 1 Has_Id_Card");

      String orgIdCard =
              App.executor.eval("App.g.V().has('Person.Organisation.Name',eq('DOMINGOS COML LTDA')).out('Has_Id_Card')" +
                      ".properties('Object.Identity_Card.Id_Value').value().next().toString()").get().toString();
      assertEquals("64765647000156", orgIdCard, "CNPJ da empresa DOMINGOS COML LTDA");

      String PersonMatheusEdgesCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MATHEUS ROCHA'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("5", PersonMatheusEdgesCount, "1 Has_Ingestion_Event + 1 Uses_Email + 1 Is_Located " +
              "+ 1 Has_Phone + 1 Has_Id_Card");

      String matheusPhoneNumber =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('MATHEUS ROCHA')).out('Has_Phone')" +
                      ".properties('Object.Phone_Number.Raw').value().next().toString()").get().toString();
      assertEquals("33238603", matheusPhoneNumber, "Matheus' phone number");

      String domingosOrgLocationAddress =
              App.executor.eval("App.g.V().has('Person.Organisation.Name',eq('DOMINGOS COML LTDA')).out('Is_Located')" +
                      ".properties('Location.Address.Full_Address').value().next().toString()").get().toString();
      assertEquals("RUA PESANHA 433 3 ACESSO MONTE GUADALUPE, JD JORDANA, JABOATÃO DOS GUARARAPES - PE, 54315085, BRASIL",
              domingosOrgLocationAddress, "Localização da loja Domingos Coml LTDA");

      String docesJoinvilleContactPerson =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('85647243000154')).in('Has_Id_Card')" +
                      ".properties('Person.Organisation.Name').value().next().toString()").get().toString();
      assertEquals("DOCES JOINVILLE LTDA", docesJoinvilleContactPerson, "Noma da empresa de CNPJ 85647243000154 é Doces Joinville");

      String totvsPersonNaturalCount =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('85647243000154'))" +
                      ".in('Has_Id_Card')" +
                      ".out('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event')" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".has('Metadata.Type.Person.Natural', eq('Person.Natural')).dedup()" +
                      ".count().next().toString()").get().toString();
      assertEquals("4", totvsPersonNaturalCount, "Count for Person.Natural Vertices from Data_Source TOTVS_SA1_CLIENTES");

      String totvsPersonOrgCount =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('85647243000154'))" +
                      ".in('Has_Id_Card')" +
                      ".out('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event')" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".has('Metadata.Type.Person.Organisation', eq('Person.Organisation')).dedup()" +
                      ".count().next().toString()").get().toString();
      assertEquals("4", totvsPersonOrgCount, "Count for Person.Organisation Vertices from Data_Source TOTVS_SA1_CLIENTES");
    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

  @Test
  public void test00002TotvsProtheusSa2Fornecedores() throws InterruptedException {

    jsonTestUtil("totvs2.json", "$.objs", "totvs_protheus_sa2_fornecedor");

    try {

      String personNaturalEdgesCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('PABLO MATO ESCOBAR'))" +
                      ".bothE('Is_Located').count().next().toString()").get().toString();
      assertEquals("1", personNaturalEdgesCount, "1 Is_Located");

      String orgIdCard =
              App.executor.eval("App.g.V().has('Person.Organisation.Name',eq('ARMS MANUTENCAO E R')).out('Has_Id_Card')" +
                      ".properties('Object.Identity_Card.Id_Value').value().next().toString()").get().toString();
      assertEquals("01243568000156", orgIdCard, "CNPJ da empresa Arms Manutenção e R(eparos)");

      String personOrgEdgesCount =
              App.executor.eval("App.g.V().has('Person.Organisation.Name', eq('ARMS MANUTENCAO E R'))" +
                      ".bothE('Has_Phone').count().next().toString()").get().toString();
      assertEquals("1", personOrgEdgesCount, "1 Has_Phone");

      String yaraPhoneNumber =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('YARA SAMANTHA')).out('Has_Phone')" +
                      ".properties('Object.Phone_Number.Raw').value().next().toString()").get().toString();
      assertEquals("996457874", yaraPhoneNumber, "Yara's phone number");

      String pabloLocationAddress =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('PABLO MATO ESCOBAR')).out('Is_Located')" +
                      ".properties('Location.Address.Full_Address').value().next().toString()").get().toString();
      assertEquals("RUA MOREIRA DA SILVA SAURO , BROOKLYN, RIO DE JANEIRO - RIO DE JANEIRO, 86785908, BRASIL",
              pabloLocationAddress, "Pablo's Address");

      String getOrgNameById =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('37648576000198')).in('Has_Id_Card')" +
                      ".properties('Person.Organisation.Name').value().next().toString()").get().toString();
      assertEquals("GELONESE PLUS", getOrgNameById, "Nome da empresa com CNPJ 37648576000198");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00003TotvsProtheusRaFuncionarios() throws InterruptedException {

    jsonTestUtil("totvs-ra.json", "$.objs", "totvs_protheus_ra_funcionario");

    try {

      String martaParentsCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MARTA MARILIA MARCÔNDES'))" +
                      ".bothE('Is_Family').count().next().toString()").get().toString();
      assertEquals("2", martaParentsCount, "2  Is_Family");

      String getNameByLocationAddress =
              App.executor.eval("App.g.V().has('Location.Address.Full_Address'," +
                      "eq('RUA SAMPAIO CASA 3333 AP 33, PONTE, JAGUARÃO - RS, 333333, BRASIL')).in('Is_Located')" +
                      ".properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("MARTA MARILIA MARCÔNDES", getNameByLocationAddress,
              "Nome da pessoa que mora no endereço especificado");

      String findingTheSonOfAMother =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MARGORE MORGANA')).out('Is_Family')" +
                      ".properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("BRENO DA SILVA", findingTheSonOfAMother, "O filho de Dona Sabrina é Breno");

      String zildaPhoneNumber =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('ZILDA')).out('Has_Phone')" +
                      ".properties('Object.Phone_Number.Raw').value().next().toString()").get().toString();
      // because RA_TELEFON has less than 7 digits number ... it just prints/saves 7 zeros (0000000)
      assertEquals("12345", zildaPhoneNumber, "Zilda's phone registry has less than 7 digits");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }

  @Test
  public void test00004TotvsProtheusPlusPloomes() throws InterruptedException {

    jsonTestUtil("totvs1-real.json", "$.objs", "totvs_protheus_sa1_clientes");
    jsonTestUtil("ploomes1-merge-totvs1-real.json", "$.value", "ploomes_clientes");

    try {

      String cpfGloria =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('GLÓRIA KRACKOVSZI'))" +
                      ".out('Has_Id_Card').properties('Object.Identity_Card.Id_Value').value().next().toString()").get().toString();
      assertEquals("01245367567", cpfGloria, "CPF de Glória Krackovszi");

      String locationAddressCount =
              App.executor.eval("App.g.V().has('Person.Organisation.Name', eq('DOCES JOINVILLE LTDA'))" +
                      ".bothE('Is_Located').count().next().toString()").get().toString();
      assertEquals("1", locationAddressCount, "Both Protheus & Ploomes share the same Location.Address vertex");

      String getCityParser =
              App.executor.eval("App.g.V().has('Person.Organisation.Name', eq('DOMINGOS COML LTDA'))" +
                      ".out('Is_Located').properties('Location.Address.parser.city').value().next().toString()").get().toString();
      assertEquals("[jaboataodosguararapes, jaboatão dos guararapes]", getCityParser, "city of the company DOMINGOS COML LTDA");

      String getPhoneNumberWithParser =
              App.executor.eval("App.g.V().has('Location.Address.parser.postcode', eq('[90420040]'))" +
                      ".in('Is_Located').out('Has_Phone').properties('Object.Phone_Number.Raw').value().next().toString()").get().toString();
      assertEquals("33316400", getPhoneNumberWithParser, "Phone number of the company URUGUAY AUTOMACAO INDL LTDA");

      String fromPhoneVertexToEmailVertex =
              App.executor.eval("App.g.V().has('Object.Phone_Number.Last_7_Digits', eq('3238603'))" +
                      ".in('Has_Phone').out('Uses_Email').properties('Object.Email_Address.Email').value().next().toString()").get().toString();
      assertEquals("matheus@mail.com.br", fromPhoneVertexToEmailVertex, "Matheus' email");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
