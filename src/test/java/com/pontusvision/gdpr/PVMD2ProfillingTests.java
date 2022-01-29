package com.pontusvision.gdpr;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(13)
//@RunWith(JUnitPlatform.class)
public class PVMD2ProfillingTests extends AppTest {

  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  //  Ingestion of 500 records in CSV format in constant batches of 100
  @Test
  public void test00001MD2Profiling() throws InterruptedException {
    try {

      String[] csvFiles =
              {"md2/md2-profiling0.csv", "md2/md2-profiling1.csv", "md2/md2-profiling2.csv", "md2/md2-profiling3.csv", "md2/md2-profiling4.csv"};

      for (String csvFile : csvFiles) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        System.out.println("-----------------------------------------------------------------------------------------");

        csvTestUtil(csvFile, "pv_md2");

        stopwatch.stop(); // optional
        System.out.println("Time elapsed for file " + csvFile + " = " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
      }

      System.out.println("-----------------------------------------------------------------------------------------");

      String aquinoRodrigues =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('9468285804')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("AQUINO RODRIGUES", aquinoRodrigues, "This CPF belongs to Aquino Rodrigues");

      String alfordHiggins =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('96908039994')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("ALFORD HIGGINS", alfordHiggins, "This CPF belongs to Alford Higgins");

      String lennyCooper =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('33666669352')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("LENNY COOPER", lennyCooper, "This CPF belongs to Lenny Cooper");

      String deliaMonteiro =
              App.executor.eval("App.g.V().has('Object.Email_Address.Email',eq('delia.monteiro@example.com')).in('Uses_Email')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("DÃ©LIA MONTEIRO", deliaMonteiro, "This email belongs to DÃ©lia Monteiro");

      String miriamDaLuz =
              App.executor.eval("App.g.V().has('Object.Email_Address.Email',eq('miriam.daluz@example.com')).in('Uses_Email')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("MIRIAM DA LUZ", miriamDaLuz, "This email belongs to Miriam da Luz");

//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling0.csv = 12 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling1.csv = 6 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling2.csv = 7 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling3.csv = 8 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling4.csv = 9 seconds

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  //  Ingestion of 1000 records in CSV format in constant batches of 100
  @Test
  public void test00002MD2Profiling() throws InterruptedException {
    try {

      String[] csvFiles =
              {"md2/md2-profiling0.csv", "md2/md2-profiling1.csv", "md2/md2-profiling2.csv", "md2/md2-profiling3.csv", "md2/md2-profiling4.csv",
                      "md2/md2-profiling5.csv", "md2/md2-profiling6.csv", "md2/md2-profiling7.csv", "md2/md2-profiling8.csv", "md2/md2-profiling9.csv"};

      for (String csvFile : csvFiles) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        System.out.println("-----------------------------------------------------------------------------------------");

        csvTestUtil(csvFile, "pv_md2");

        stopwatch.stop(); // optional
        System.out.println("Time elapsed for file " + csvFile + " = " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
      }

      System.out.println("-----------------------------------------------------------------------------------------");

      String aquinoRodrigues =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('9468285804')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("AQUINO RODRIGUES", aquinoRodrigues, "This CPF belongs to Aquino Rodrigues");

      String alfordHiggins =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('96908039994')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("ALFORD HIGGINS", alfordHiggins, "This CPF belongs to Alford Higgins");

      String lennyCooper =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('33666669352')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("LENNY COOPER", lennyCooper, "This CPF belongs to Lenny Cooper");

      String deliaMonteiro =
              App.executor.eval("App.g.V().has('Object.Email_Address.Email',eq('delia.monteiro@example.com')).in('Uses_Email')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("DÃ©LIA MONTEIRO", deliaMonteiro, "This email belongs to DÃ©lia Monteiro");

      String miriamDaLuz =
              App.executor.eval("App.g.V().has('Object.Email_Address.Email',eq('miriam.daluz@example.com')).in('Uses_Email')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("MIRIAM DA LUZ", miriamDaLuz, "This email belongs to Miriam da Luz");

      String jordinaAraujo =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('72714206140')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("JORDINA ARAÃºJO", jordinaAraujo, "This CPF belongs to Jordina AraÃºjo");

      String janeTeixeira =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('84478523770')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("JÃ³NI TEIXEIRA", janeTeixeira, "This CPF belongs to JÃ³ni Teixeira");

      String guiliaRamones =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('01411383605')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("GUILIA RAMONES", guiliaRamones, "This CPF belongs to Guilia Ramones");

      String safiraCosta =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('26491059847')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("SAFIRA DA COSTA", safiraCosta, "This CPF belongs to Safira da Costa");

      String rodrigoRodrigues =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('90052103625')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("RODRIGO RODRIGUES", rodrigoRodrigues, "This CPF belongs to Rodrigo Rodrigues");

//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling0.csv = 11 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling1.csv = 5 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling2.csv = 6 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling3.csv = 7 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling4.csv = 7 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling5.csv = 9 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling6.csv = 8 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling7.csv = 9 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 101 }
//      Time elapsed for file md2/md2-profiling8.csv = 11 seconds
//      -----------------------------------------------------------------------------------------
//      { "status": "success", "successCount": 100 }
//      Time elapsed for file md2/md2-profiling9.csv = 11 seconds

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

//  TODO: Ingestion of 500 records in CSV format in variable batches

//  TODO: Ingestion of 500 records in JSON format in constant batches of 100

//  TODO: Ingestion of 1000 records in JSON format in constant batches of 100

//  TODO: Ingestion of 500 records in JSON format in variable batches

  //  Ingestion of 20k records in CSV format in constant batches of 5k
  @Test
  public void test00003MD2Profiling() throws InterruptedException {
    try {

      String[] csvFiles = {"random-data-5k(1).csv", "random-data-5k(2).csv", "random-data-5k(3).csv", "random-data-5k(4).csv"};

      for (String csvFile : csvFiles) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        System.out.println("-----------------------------------------------------------------------------------------");

        csvTestUtil(csvFile, "pv_md2");

        stopwatch.stop(); // optional
        System.out.println("Time elapsed for file " + csvFile + " = " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
      }

      System.out.println("-----------------------------------------------------------------------------------------");

      String alirioDaLuz =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('21564809153')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("ALÍRIO DA LUZ", alirioDaLuz, "This CPF belongs to Alírio da Luz");

      String guterreDaMata =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('14519746043')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("GUTERRE DA MATA", guterreDaMata, "This CPF belongs to Guterre da Mata");

      String joceniraJesus =
              App.executor.eval("App.g.V().has('Object.Identity_Card.Id_Value',eq('22875552007')).in('Has_Id_Card')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("JOCENIRA JESUS", joceniraJesus, "This CPF belongs to Jocenira Jesus");

      String azizDaCunha =
              App.executor.eval("App.g.V().has('Object.Email_Address.Email',eq('aziz.dacunha@example.com')).in('Uses_Email')" +
                      ".values('Person.Natural.Full_Name').next().toString()").get().toString();
      assertEquals("AZIZ DA CUNHA", azizDaCunha, "This CPF belongs to Aziz da Cunha");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
