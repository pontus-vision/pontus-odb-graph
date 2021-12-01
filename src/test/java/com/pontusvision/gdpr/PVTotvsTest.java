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

<<<<<<< HEAD
    jsonTestUtil("totvs1-real.json", "$.objs", "totvs_protheus_sa1_clientes");

=======
    jsonTestUtil("totvs1.json", "$.objs", "totvs_protheus_sa1_clientes");
    jsonTestUtil("totvs1-real.json", "$.objs", "totvs_protheus_sa1_clientes");
>>>>>>> origin/master
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

<<<<<<< HEAD
      String personNaturalVerticesCount =
              App.executor.eval("App.g.V().has('Metadata.Type.Person.Natural', eq('Person.Natural'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", personNaturalVerticesCount, "The only Person.Natural is Matheus Rocha");

      String personOrganisationVerticesCount =
              App.executor.eval("App.g.V().has('Metadata.Type.Person.Organisation', eq('Person.Organisation'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("3", personOrganisationVerticesCount, "URUGUAY AUTOMACAO INDL LTDA, DOMINGOS COML LTDA e DOCES JOINVILLE LTDA");
=======
//      String personNaturalVerticesCount =
//              App.executor.eval("App.g.V().has('Metadata.Type.Person.Natural', eq('Person.Natural'))" +
//                      ".count().next().toString()").get().toString();
//      assertEquals("1", personNaturalVerticesCount, "The only Person.Natural is Matheus Rocha");
//
//      String personOrganisationVerticesCount =
//              App.executor.eval("App.g.V().has('Metadata.Type.Person.Organisation', eq('Person.Organisation'))" +
//                      ".count().next().toString()").get().toString();
//      assertEquals("3", personOrganisationVerticesCount, "URUGUAY AUTOMACAO INDL LTDA, DOMINGOS COML LTDA e DOCES JOINVILLE LTDA");
>>>>>>> origin/master

      String matheusPhoneNumber =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('MATHEUS ROCHA')).out('Has_Phone')" +
                      ".properties('Object.Phone_Number.Raw').value().next().toString()").get().toString();
      assertEquals("33238603", matheusPhoneNumber, "Matheus' phone number");

      String domingosOrgLocationAddress =
              App.executor.eval("App.g.V().has('Person.Organisation.Name',eq('DOMINGOS COML LTDA')).out('Is_Located')" +
                      ".properties('Location.Address.Full_Address').value().next().toString()").get().toString();
      assertEquals("RUA PESANHA 433 3 ACESSO MONTE GUADALUPE, JD JORDANA, JABOATAO - PE, 54315085",
              domingosOrgLocationAddress, "Localização da loja Domingos Coml LTDA");

      String docesJoinvilleContactPerson =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('85647243000154')).in('Has_Id_Card')" +
<<<<<<< HEAD
                      ".properties('Person.Organisation.Contact').value().next().toString()").get().toString();
      assertEquals("LEOPOLDO MONTES", docesJoinvilleContactPerson, "Contato da empresa Doces Joinville");

=======
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
      assertEquals("3", totvsPersonNaturalCount, "Count for Person.Natural Vertices from Data_Source TOTVS_SA1_CLIENTES");

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
>>>>>>> origin/master
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
<<<<<<< HEAD
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("8", personNaturalEdgesCount, "2 Has_Id_Card + 2 Uses_Email " +
              "+ 2 Has_Phone + 1 Is_Located + 1 Has_Ingestion_Event");
=======
                      ".bothE('Is_Located').count().next().toString()").get().toString();
      assertEquals("1", personNaturalEdgesCount, "1 Is_Located");
>>>>>>> origin/master

      String orgIdCard =
              App.executor.eval("App.g.V().has('Person.Organisation.Name',eq('ARMS MANUTENCAO E R')).out('Has_Id_Card')" +
                      ".properties('Object.Identity_Card.Id_Value').value().next().toString()").get().toString();
      assertEquals("01243568000156", orgIdCard, "CNPJ da empresa Arms Manutenção e R(eparos)");

      String personOrgEdgesCount =
              App.executor.eval("App.g.V().has('Person.Organisation.Name', eq('ARMS MANUTENCAO E R'))" +
<<<<<<< HEAD
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("5", personOrgEdgesCount, "1 Has_Id_Card + 1 Is_Located + 1 Has_Ingestion_Event " +
              "+ 1 Uses_Email + 1 Has_Phone");
=======
                      ".bothE('Has_Phone').count().next().toString()").get().toString();
      assertEquals("1", personOrgEdgesCount, "1 Has_Phone");
>>>>>>> origin/master

      String yaraPhoneNumber =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('YARA SAMANTHA')).out('Has_Phone')" +
                      ".properties('Object.Phone_Number.Raw').value().next().toString()").get().toString();
      assertEquals("996457874", yaraPhoneNumber, "Yara's phone number");

      String pabloLocationAddress =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('PABLO MATO ESCOBAR')).out('Is_Located')" +
                      ".properties('Location.Address.Full_Address').value().next().toString()").get().toString();
      assertEquals("RUA MOREIRA DA SILVA SAURO , BROOKLYN, RIO DE JANEIRO - RIO DE JANEIRO, 86785908",
              pabloLocationAddress, "Pablo's Address");

<<<<<<< HEAD
      String gelonesePlusContactPerson =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('37648576000198')).in('Has_Id_Card')" +
                      ".properties('Person.Organisation.Contact').value().next().toString()").get().toString();
      assertEquals("HERMINIA", gelonesePlusContactPerson, "Contato da empresa Gelonese Plus");
=======
      String getOrgNameById =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('37648576000198')).in('Has_Id_Card')" +
                      ".properties('Person.Organisation.Name').value().next().toString()").get().toString();
      assertEquals("GELONESE PLUS", getOrgNameById, "Nome da empresa com CNPJ 37648576000198");
>>>>>>> origin/master

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00003TotvsProtheusRaFuncionarios() throws InterruptedException {

    jsonTestUtil("totvs-ra.json", "$.objs", "totvs_protheus_ra_funcionario");

    try {

<<<<<<< HEAD
      String personNaturalEdgesCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MARTA MARILIA MARCÔNDES'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("8", personNaturalEdgesCount, "2 Uses_Email + 2 Is_Family + 2 Has_Id_Card +  1 Lives + 1 Has_Ingestion_Event");
=======
      String martaParentsCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MARTA MARILIA MARCÔNDES'))" +
                      ".bothE('Is_Family').count().next().toString()").get().toString();
      assertEquals("2", martaParentsCount, "2  Is_Family");
>>>>>>> origin/master

      String getNameByLocationAddress =
              App.executor.eval("App.g.V().has('Location.Address.Full_Address'," +
                      "eq('RUA SAMPAIO CASA 3333 AP 33, PONTE, JAGUARÃO - RS, 333333')).in('Is_Located')" +
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

//  @Test
//  public void test00004TotvsProtheusPlusPloomes() throws InterruptedException {
//
//    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");
<<<<<<< HEAD
//    jsonTestUtil("totvs1-real.json", "$.objs", "totvs_protheus_sa1_clientes");
//
//    try {
//
////    test0000 for COMIDAS 1 as Person.Natural
//      String userId0 =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('COMIDAS 1'))\n" +
//                      ".next().id().toString()").get().toString();
//
////      test0000 for COMIDAS 1 as Person.Organisation
////      String userId1 =
////              App.executor.eval("App.g.V().has('Person.Organisation.Short_Name', eq('COMIDAS 1'))\n" +
////                      ".next().id().toString()").get().toString();
//
////    test0000 for Object.Data_Source.Name
//      String userId2 =
//              App.executor.eval("App.g.V().has('Object.Data_Source.Name', eq('TOTVS/PROTHEUS/SA1_CLIENTES'))" +
//                      ".next().id().toString()").get().toString();
//      String rootConnectionsQuery = "App.g.V(\"" + userId2 + "\").bothE().count().next().toString()";
//      String rootConnections = App.executor.eval(rootConnectionsQuery).get().toString();
////      assertEquals(rootConnections,"5"); -- vertices podem variar dependendo do timing!
//
////    test0000 COUNT(Edges) for COMIDAS 2
//      String userId3 =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('COMIDAS 2'))" +
//                      ".next().id().toString()").get().toString();
//      String comidas2ConnectionsQuery = "App.g.V(\"" + userId3 + "\").bothE().count().next().toString()";
//      String comidas2Connections = App.executor.eval(comidas2ConnectionsQuery).get().toString();
//      assertEquals("4", comidas2Connections);
//
////    test0000 COUNT(Edges) for Object.Email_Address
//      String userId4 =
//              App.executor.eval("App.g.V().has('Object.Email_Address.Email',eq('jonas@comida1.com.br'))" +
//                      ".next().id().toString()").get().toString();
//      String emailConnectionsQuery = "App.g.V(\"" + userId4 + "\").bothE().count().next().toString()";
//      String emailConnections = App.executor.eval(emailConnectionsQuery).get().toString();
//      assertEquals("1", emailConnections);
//
////    test0000 COUNT(Edges) for Object.Phone_Number
//      String userId5 =
//              App.executor.eval("App.g.V().has('Object.Phone_Number.Numbers_Only',eq('111111111'))" +
//                      ".next().id().toString()").get().toString();
//      String phoneConnectionsQuery = "App.g.V(\"" + userId5 + "\").bothE().count().next().toString()";
//      String phoneConnections = App.executor.eval(phoneConnectionsQuery).get().toString();
//      assertEquals("1", phoneConnections);
//
=======
//
//    try {
//
//      String personNaturalCount =
//              App.executor.eval("App.g.V().has('Metadata.Type.Person.Natural', eq('Person.Natural'))" +
//                      ".count().next().toString()").get().toString();
//      assertEquals("1", personNaturalCount, "only one registry is TypeId = 1 = PF");
//
//      String personOrganisationCount =
//              App.executor.eval("App.g.V().has('Metadata.Type.Person.Organisation', eq('Person.Organisation'))" +
//                      ".count().next().toString()").get().toString();
//      assertEquals("4", personOrganisationCount, "4 registries are of TypeId = 2 = PJ");
>>>>>>> origin/master
//
//    } catch (ExecutionException e) {
//      e.printStackTrace();
//      assertNull(e);
//
//    }
//
//
//  }

}
