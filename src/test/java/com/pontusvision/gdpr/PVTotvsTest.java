package com.pontusvision.gdpr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    jsonTestUtil("totvs_sa1.json", "$.objs", "totvs_protheus_sa1_clientes");

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

      String personNaturalVerticesCount =
              App.executor.eval("App.g.V().has('Metadata.Type.Person.Natural', eq('Person.Natural'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("1", personNaturalVerticesCount, "The only Person.Natural is Matheus Rocha");

      String personOrganisationVerticesCount =
              App.executor.eval("App.g.V().has('Metadata.Type.Person.Organisation', eq('Person.Organisation'))" +
                      ".count().next().toString()").get().toString();
      assertEquals("3", personOrganisationVerticesCount, "URUGUAY AUTOMACAO INDL LTDA, DOMINGOS COML LTDA e DOCES JOINVILLE LTDA");

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
                      ".properties('Person.Organisation.Contact').value().next().toString()").get().toString();
      assertEquals("LEOPOLDO MONTES", docesJoinvilleContactPerson, "Contato da empresa Doces Joinville");

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
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("8", personNaturalEdgesCount, "2 Has_Id_Card + 2 Uses_Email " +
              "+ 2 Has_Phone + 1 Is_Located + 1 Has_Ingestion_Event");

      String orgIdCard =
              App.executor.eval("App.g.V().has('Person.Organisation.Name',eq('ARMS MANUTENCAO E R')).out('Has_Id_Card')" +
                      ".properties('Object.Identity_Card.Id_Value').value().next().toString()").get().toString();
      assertEquals("01243568000156", orgIdCard, "CNPJ da empresa Arms Manutenção e R(eparos)");

      String personOrgEdgesCount =
              App.executor.eval("App.g.V().has('Person.Organisation.Name', eq('ARMS MANUTENCAO E R'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("5", personOrgEdgesCount, "1 Has_Id_Card + 1 Is_Located + 1 Has_Ingestion_Event " +
              "+ 1 Uses_Email + 1 Has_Phone");

      String yaraPhoneNumber =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('YARA SAMANTHA')).out('Has_Phone')" +
                      ".properties('Object.Phone_Number.Raw').value().next().toString()").get().toString();
      assertEquals("996457874", yaraPhoneNumber, "Yara's phone number");

      String pabloLocationAddress =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('PABLO MATO ESCOBAR')).out('Is_Located')" +
                      ".properties('Location.Address.Full_Address').value().next().toString()").get().toString();
      assertEquals("RUA MOREIRA DA SILVA SAURO , BROOKLYN, RIO DE JANEIRO - RIO DE JANEIRO, 86785908",
              pabloLocationAddress, "Pablo's Address");

      String gelonesePlusContactPerson =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('37648576000198')).in('Has_Id_Card')" +
                      ".properties('Person.Organisation.Contact').value().next().toString()").get().toString();
      assertEquals("HERMINIA", gelonesePlusContactPerson, "Contato da empresa Gelonese Plus");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00003TotvsProtheusRaFuncionarios() throws InterruptedException {

    jsonTestUtil("totvs-ra.json", "$.objs", "totvs_protheus_ra_funcionario");

    try {

      String personNaturalEdgesCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MARTA MARILIA MARCÔNDES'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("8", personNaturalEdgesCount, "2 Uses_Email + 2 Is_Family + 2 Has_Id_Card +  1 Lives + 1 Has_Ingestion_Event");

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
//    jsonTestUtil("totvs_sa1.json", "$.objs", "totvs_protheus_sa1_clientes");
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