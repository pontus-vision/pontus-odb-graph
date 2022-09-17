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
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(19)
//@RunWith(JUnitPlatform.class)
public class PVTotvsDependenteTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001TotvsProtheusSRaFuncionarioSRbDependente() throws InterruptedException {

    jsonTestUtil("totvs/totvs-sra-real.json", "$.objs", "totvs_protheus_sra_funcionario");
    jsonTestUtil("totvs/totvs-srb-real.json", "$.objs", "totvs_protheus_srb_dependente");

    try {

//    testing for SRA_FUNCIONARIO

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
      assertEquals("12345", zildaPhoneNumber, "Zilda's phone registry has less than 7 digits");

//    testing for SRB_DEPENDENTE

      String anneWithAnEId =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('totvs/protheus/srb_dependente'))" +
                      ".in('Has_Ingestion_Event').has('Person_Natural_Full_Name', eq('ANNE WITH AN E'))" +
                      ".out('Is_Dependant').values('Person_Natural_Id').next().toString()").get().toString();
      assertEquals("6490009832", anneWithAnEId, "Anne's Dependant Id");

      String xavierHasNoId =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('totvs/protheus/srb_dependente'))" +
                      ".in('Has_Ingestion_Event').has('Person_Natural_Full_Name', eq('XAVIER DO PORTO'))" +
                      ".bothE().dedup().count().next().toString()").get().toString();
      assertEquals("0", xavierHasNoId, "No vertices were created for Xavier, because there is no Id");

      // Teste para Person_Natural_Last_Update_Date Jorge Schimelpfeng
        String jorgeSchimelpfengLastUpdateDate =
                App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('totvs/protheus/srb_dependente'))" +
                        ".in('Has_Ingestion_Event').has('Person_Natural_Full_Name', eq('JORGE SCHIMELPFENG'))" +
                        ".properties('Person_Natural_Last_Update_Date').value().next().toString()").get().toString();
        assertEquals(dtfmt.parse("Sat Jan 01 01:00:00 UTC 2022"), dtfmt.parse(jorgeSchimelpfengLastUpdateDate), "Jorge Schimelpfeng Last Update Date");

//    Testing the link between SRA and SRB

      String fromSRBtoSRA =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('totvs/protheus/srb_dependente'))" +
                      ".as('srb-ingestion').in('Has_Ingestion_Event').as('dependente').out('Is_Dependant')" +
                      ".as('colaborador').out('Has_Ingestion_Event').as('sra-ingestion').values('Event_Ingestion_Type')" +
                      ".next().toString()").get().toString();
      assertEquals("totvs/protheus/sra_funcionario", fromSRBtoSRA,
              "Going from srb_dependente and getting to sra_funcionario");


    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);

    }
  }

}
