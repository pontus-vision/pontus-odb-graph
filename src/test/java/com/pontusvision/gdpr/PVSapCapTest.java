package com.pontusvision.gdpr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pontusvision.ingestion.Ingestion;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
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
@TestMethodOrder(MethodOrderer.MethodName.class)
//@RunWith(JUnitPlatform.class)
public class PVSapCapTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001SapCapLeads() throws InterruptedException {

    try {

      String dirtyHeader = "A string. With; loads: of # Chars,{ a.;~^Ç``\"oçã}][";
      String cleanHdr = Ingestion.cleanHeader(dirtyHeader);
      assertEquals(
          "A_string__With__loads__of___Chars___a____C___oca___",
          cleanHdr,
          "check clean headers is OK");

      csvTestUtil("sap-cap/leads.csv", "cap_leads");

      String personNaturalEdgesCount =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('IGOR FERREIRA'))" +
              ".bothE().count().next().toString()").get().toString();
      assertEquals("7", personNaturalEdgesCount, "2 Has_Phone + 1 Event.Ingestion + 1 Works " +
          "+ 1 Is_Located + 1 Uses_Email + 1 Is_Lead");


      String leadId =
          App.executor.eval("App.g.V().has('Location.Address.Full_Address'," +
              "eq('av. marcio gomes 333 , AA3, Belo Horizonte - Brasil, 6758090')).in('Is_Located')" +
              ".properties('Person.Natural.Customer_ID').value().next().toString()").get().toString();
      assertEquals("1", leadId, "Lead ID de Igor Ferreira");


      String gettingEmailAddress =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('LARA MAGALHAES')).out('Uses_Email')" +
              ".properties('Object.Email_Address.Email').value().next().toString()").get().toString();
      assertEquals("lara@yahoo.com", gettingEmailAddress, "E-mail de Lara Magalhaes");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00002SapCapCustomerProspect() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/customer-prospect.csv", "cap_customer_prospect");

//      String personNaturalEdgesCount =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('JAMIL GUPTA'))" +
//                      ".bothE().count().next().toString()").get().toString();
//      assertEquals("7", personNaturalEdgesCount, "2 Has_Phone + 1 Event.Ingestion + 1 Works " +
//              "+ 1 Is_Located + 1 Uses_Email + 1 Is_Lead");


      String dateOfBirth =
          App.executor.eval("App.g.V().has('Location.Address.Full_Address'," +
              "eq('Rua Porto Alegre 12 , Bairro da Lama (Vila Loubos), Gralhas do Sul - Brasil, 85867-909'))" +
              ".in('Is_Located').properties('Person.Natural.Date_Of_Birth').value().next().toString()").get().toString();
      // replace the timezone with GMT; otherwise, if the test is run in Brazil, that appears as BRT 1976
      // ... in regex means any character, so ... 1976 will replace BRT 1976 with GMT 1976
      dateOfBirth = dateOfBirth.replaceAll("... 1976", "GMT 1976");
      assertEquals("Fri Feb 13 00:00:00 GMT 1976", dateOfBirth, "Data de nascimento de Jamil Gupta");


      String gettingEmailAddress =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('FERNANDA CASTELO')).out('Uses_Email')" +
              ".properties('Object.Email_Address.Email').value().next().toString()").get().toString();
      assertEquals("fernanda_castelo@icloud.com", gettingEmailAddress, "E-mail de Fernanda Castelo");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }


}
