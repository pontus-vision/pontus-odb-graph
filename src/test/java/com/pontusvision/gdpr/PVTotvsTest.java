package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

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

    jsonTestUtil("totvs/totvs-sa1-real.json", "$.objs", "totvs_protheus_sa1_clientes");

    try {

      String orgIdCard =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('DOMINGOS COML LTDA')).out('Has_Id_Card')" +
                      ".properties('Object_Identity_Card_Id_Value').value().next().toString()").get().toString();
      assertEquals("64765647000156", orgIdCard, "CNPJ da empresa DOMINGOS COML LTDA");

      String PersonMatheusEdgesCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MATHEUS ROCHA'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("5", PersonMatheusEdgesCount, "1 Has_Ingestion_Event + 1 Uses_Email + 1 Is_Located " +
              "+ 1 Has_Phone + 1 Has_Id_Card");

      String matheusPhoneNumber =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('MATHEUS ROCHA')).out('Has_Phone')" +
                      ".properties('Object_Phone_Number_Raw').value().next().toString()").get().toString();
      assertEquals("33238603", matheusPhoneNumber, "Matheus' phone number");

      String domingosOrgLocationAddress =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('DOMINGOS COML LTDA')).out('Is_Located')" +
                      ".properties('Location_Address_Full_Address').value().next().toString()").get().toString();
      assertEquals("RUA PESANHA 433 3 ACESSO MONTE GUADALUPE, JD JORDANA, JABOATÃO DOS GUARARAPES - PE, 54315085, BRASIL",
              domingosOrgLocationAddress, "Localização da loja Domingos Coml LTDA");

      String docesJoinvilleContactPerson =
              App.executor.eval("App.g.V().has('Object_Identity_Card_Id_Value',eq('85647243000154')).in('Has_Id_Card')" +
                      ".properties('Person_Organisation_Name').value().next().toString()").get().toString();
      assertEquals("DOCES JOINVILLE LTDA", docesJoinvilleContactPerson, "Noma da empresa de CNPJ 85647243000154 é Doces Joinville");

      String totvsPersonNaturalCount =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name',eq('TOTVS/PROTHEUS/SA1_CLIENTES'))" +
                      ".out('Has_Ingestion_Event').has('Event_Group_Ingestion_Type', eq('totvs/protheus/sa1_clientes'))" +
                      ".out('Has_Ingestion_Event').has('Event_Ingestion_Type', eq('totvs/protheus/sa1_clientes'))" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural')).dedup()" +
                      ".count().next().toString()").get().toString();
      assertEquals("4", totvsPersonNaturalCount,
              "Count for Person_Natural Vertices from Data_Source TOTVS/PROTHEUS/SA1_CLIENTES, 2 Person_Natural" +
                      "come from totvs-sa1.json and 2 from totvs-sa1-real.json");

      String totvsPersonOrgCount =
              App.executor.eval("App.g.V().has('Object_Identity_Card_Id_Value',eq('85647243000154'))" +
                      ".in('Has_Id_Card')" +
                      ".out('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event')" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".has('Metadata_Type_Person_Organisation', eq('Person_Organisation')).dedup()" +
                      ".count().next().toString()").get().toString();
      assertEquals("5", totvsPersonOrgCount, "Count for Person_Organisation Vertices from Data_Source TOTVS_SA1_CLIENTES");

      // Test Person_Natural_Last_Update_Date Matheus Rocha
      String matheusRochaLastUpdateDate =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('totvs/protheus/sa1_clientes'))" +
                      ".in('Has_Ingestion_Event').has('Person_Natural_Full_Name', eq('MATHEUS ROCHA'))" +
                      ".properties('Person_Natural_Last_Update_Date').value().next().toString()").get().toString();
      assertEquals(dtfmt.parse("Fri Nov 19 07:59:59 UTC 2021"), dtfmt.parse(matheusRochaLastUpdateDate),
              "Matheus Rocha Last Update Date");

      // Test Person_Organisation_Last_Update_Date Doces Joinville
        String docesJoinvilleLastUpdateDate =
                App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('totvs/protheus/sa1_clientes'))" +
                        ".in('Has_Ingestion_Event').has('Person_Organisation_Name', eq('DOCES JOINVILLE LTDA'))" +
                        ".properties('Person_Organisation_Last_Update_Date').value().next().toString()").get().toString();
        assertEquals(dtfmt.parse("Fri Nov 19 07:59:59 UTC 2021"), dtfmt.parse(docesJoinvilleLastUpdateDate),
                "Doces Joinville Last Update Date");

      //    new AGgrid test style ------------------------------------------------------------------------------------------------------------
      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Person_Organisation_Registration_Number\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"29072\"\n" +
                      "  }\n" +
                      "]", "Person_Organisation",
              new String[]{"Person_Organisation_Type", "Person_Organisation_Name"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Person_Organisation_Name\":\"YOUSSEF MOTORS\""));
      assertTrue(replyStr.contains("\"Person_Organisation_Type\":\"INTERNATIONAL\""));

    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

  @Test
  public void test00002TotvsProtheusSa2Fornecedor() throws InterruptedException {

    jsonTestUtil("totvs/totvs-sa2-real.json", "$.objs", "totvs_protheus_sa2_fornecedor");

    try {

      String orgIdCard =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('ARMS MANUTENCAO E R')).out('Has_Id_Card')" +
                      ".properties('Object_Identity_Card_Id_Value').value().next().toString()").get().toString();
      assertEquals("01243568000156", orgIdCard, "CNPJ da empresa Arms Manutenção e R(eparos)");

      String yaraPhoneNumber =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('YARA SAMANTHA')).out('Has_Phone')" +
                      ".properties('Object_Phone_Number_Raw').value().next().toString()").get().toString();
      assertEquals("996457874", yaraPhoneNumber, "Yara's phone number");

      String pabloLocationAddress =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('PABLO MATO ESCOBAR')).out('Is_Located')" +
                      ".properties('Location_Address_Full_Address').value().next().toString()").get().toString();
      assertEquals("RUA MOREIRA DA SILVA SAURO, BROOKLYN, RIO DE JANEIRO - RJ, 86785908, BRASIL",
              pabloLocationAddress, "Pablo's Address");

      String personNaturalEdgesCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('PABLO MATO ESCOBAR'))" +
                      ".bothE('Is_Located').count().next().toString()").get().toString();
      assertEquals("1", personNaturalEdgesCount, "1 Is_Located");

      String getOrgNameById =
              App.executor.eval("App.g.V().has('Object_Identity_Card_Id_Value',eq('37648576000198')).in('Has_Id_Card')" +
                      ".properties('Person_Organisation_Name').value().next().toString()").get().toString();
      assertEquals("GELONESE PLUS", getOrgNameById, "Nome da empresa com CNPJ 37648576000198");

//    new AGgrid test style ------------------------------------------------------------------------------------------------------------
      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Person_Organisation_Registration_Number\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"937012\"\n" +
                      "  }\n" +
                      "]", "Person_Organisation",
              new String[]{"Person_Organisation_Type", "Person_Organisation_Name"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Person_Organisation_Name\":\"GRAPE INC.\""));
      assertTrue(replyStr.contains("\"Person_Organisation_Type\":\"INTERNATIONAL\""));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00003TotvsProtheusRaFuncionario() throws InterruptedException {

    jsonTestUtil("totvs/totvs-sra-real.json", "$.objs", "totvs_protheus_sra_funcionario");

    try {

      String martaParentsCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARTA MARILIA MARCÔNDES'))" +
                      ".bothE('Is_Family').count().next().toString()").get().toString();
      assertEquals("2", martaParentsCount, "2  Is_Family");

      String getNameByLocationAddress =
              App.executor.eval("App.g.V().has('Location_Address_Full_Address'," +
                      "eq('RUA SAMPAIO CASA, PONTE, JAGUARÃO - RS, 333333, BRASIL')).in('Is_Located')" +
                      ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("MARTA MARILIA MARCÔNDES", getNameByLocationAddress,
              "Nome da pessoa que mora no endereço especificado");

      String findingTheSonOfAMother =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARGORE MORGANA')).out('Is_Family')" +
                      ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("BRENO DA SILVA", findingTheSonOfAMother, "O filho de Dona Sabrina é Breno");

      String zildaPhoneNumber =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('ZILDA')).out('Has_Phone')" +
                      ".properties('Object_Phone_Number_Raw').value().next().toString()").get().toString();
      // because RA_TELEFON has less than 7 digits number ... it just prints/saves 7 zeros (0000000)
      assertEquals("00550154530251495", zildaPhoneNumber, "Zilda's phone registry has less than 7 digits");

//    new AGgrid test style ------------------------------------------------------------------------------------------------------------
      RecordReply reply = gridWrapper("[\n" +
                      "  {\n" +
                      "    \"colId\": \"Person_Organisation_Registration_Number\",\n" +
                      "    \"filterType\": \"text\",\n" +
                      "    \"type\": \"equals\",\n" +
                      "    \"filter\": \"825309\"\n" +
                      "  }\n" +
                      "]", "Person_Organisation",
              new String[]{"Person_Organisation_Type", "Person_Organisation_Name"});
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Person_Organisation_Name\":\"ZAMOLODCHIKOVA PERIODICHESKIY KHIMIYA\""));
      assertTrue(replyStr.contains("\"Person_Organisation_Type\":\"INTERNATIONAL\""));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }

  @Test
  public void test00004TotvsProtheusPlusPloomes() throws InterruptedException {

    jsonTestUtil("totvs/totvs-sa1-real.json", "$.objs", "totvs_protheus_sa1_clientes");
    jsonTestUtil("ploomes/ploomes1-merge-totvs1-real.json", "$.value", "ploomes_clientes");

    try {

      String cpfGloria =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('GLÓRIA KRACKOVSZI'))" +
                      ".out('Has_Id_Card').properties('Object_Identity_Card_Id_Value').value().next().toString()").get().toString();
      assertEquals("01245367567", cpfGloria, "CPF de Glória Krackovszi");

      String locationAddressCount =
              App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('DOCES JOINVILLE LTDA'))" +
                      ".bothE('Is_Located').dedup().count().next().toString()").get().toString();
      assertEquals("1", locationAddressCount, "Both Protheus & Ploomes share the same Location_Address vertex");

      String getCityParser =
              App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('DOMINGOS COML LTDA'))" +
                      ".out('Is_Located').properties('Location_Address_parser_city').value().next().toString()").get().toString();
      assertEquals("[jaboataodosguararapes, jaboatão dos guararapes]", getCityParser, "city of the company DOMINGOS COML LTDA");

      String getPhoneNumberWithParser =
              App.executor.eval("App.g.V().has('Location_Address_parser_postcode', eq('[90420040]'))" +
                      ".in('Is_Located').out('Has_Phone').properties('Object_Phone_Number_Raw').value().next().toString()").get().toString();
      assertEquals("33316400", getPhoneNumberWithParser, "Phone number of the company URUGUAY AUTOMACAO INDL LTDA");

      String fromPhoneVertexToEmailVertex =
              App.executor.eval("App.g.V().has('Object_Phone_Number_Last_7_Digits', eq('3238603'))" +
                      ".in('Has_Phone').out('Uses_Email').properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("matheus@mail.com.br", fromPhoneVertexToEmailVertex, "Matheus' email");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
