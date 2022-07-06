package com.pontusvision.gdpr;

import com.pontusvision.ingestion.Ingestion;
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
@TestClassesOrder(2)

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
  public void test00001SapCapLead() throws InterruptedException {
    // updated to real style data lead.csv

    try {

      String dirtyHeader = "A string. With; loads: of # Chars,{ a.;~^Ç``\"oçã}][";
      String cleanHdr = Ingestion.cleanHeader(dirtyHeader);
      assertEquals(
              "A_string__With__loads__of___Chars___a____C___oca___",
              cleanHdr,
              "check clean headers is OK");

      csvTestUtil("SAP/sap-cap/lead.csv", "cap_lead");

      String marcelPhoneCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARCEL MOLICA ARAÚJO'))" +
                      ".bothE('Has_Phone').count().next().toString()").get().toString();
      // Object_Contract has been removed ... so no more Is_Lead EDGE ... total of 6 EDGES
      assertEquals("2", marcelPhoneCount, "Marcel has 2 phone numbers registered");


      String fullName =
              App.executor.eval("App.g.V().has('Location_Address_Full_Address'," +
                      "eq('Rua Tarcisio Vanderbilt 260 Casa, Araraquara, São Paulo (SP) - BR, 88650024')).in('Is_Located')" +
                      ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("MARCEL MOLICA ARAÚJO", fullName, "Full Name de Marcel Molica Araújo");


      String gettingEmailAddress =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MARCEL MOLICA ARAÚJO')).out('Uses_Email')" +
                      ".properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("marcelmolica@hotmail.com", gettingEmailAddress, "E-mail de Marcel Molica Araújo");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00002SapCapCustomerProspect() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/customer-prospect.csv", "cap_customer_prospect");

      String gettingEmailAddress =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('NATAN BOLDERI')).out('Uses_Email')" +
                      ".properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("natinhobolderi@gmail.com", gettingEmailAddress, "E-mail de Natan Bolderi");

      String natanBDay =
              App.executor.eval("App.g.V().has('Location_Address_Post_Code'," +
                      "eq('14022-000'))" +
                      ".in('Is_Located').properties('Person_Natural_Date_Of_Birth').value().next().toString()").get().toString();
      // replace the timezone with GMT; otherwise, if the test is run in Brazil, that appears as BRT 1975
      // ... in regex means any character, so ... 1976 will replace BRT 1976 with GMT 1975
//      natanBDay = natanBDay.replaceAll("... 1975", "GMT 1975");
      assertEquals(dtfmt.parse("Thu May 08 01:01:01 GMT 1975"), dtfmt.parse(natanBDay), "Data de nascimento de Natan Bolderi");

      String eventConsentCreatedBy =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('NATAN BOLDERI')).out('Has_Consent')" +
                      ".properties('Event_Consent_Created_By').value().next().toString()").get().toString();
      assertEquals("DBR0200WK", eventConsentCreatedBy, "Porsche Employee code");

      String natanBolderiNoConsentCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('NATAN BOLDERI')).out('Has_Consent')" +
                      ".has('Event_Consent_Status', eq('No Consent')).count().next().toString()").get().toString();
      assertEquals("1", natanBolderiNoConsentCount, "Number of No Consents granted by Natan Bolderi");

      String natanBolderiYesConsentCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('NATAN BOLDERI')).out('Has_Consent')" +
                      ".has('Event_Consent_Status', eq('Consent')).count().next().toString()").get().toString();
      assertEquals("2", natanBolderiYesConsentCount, "Number of Consents granted by Natan Bolderi");

      String telephoneConsentStatus =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('NATAN BOLDERI')).out('Has_Consent')" +
                      ".has('Event_Consent_Description', eq('Telephone Consent')).properties('Event_Consent_Status')" +
                      ".value().next().toString()").get().toString();
      assertEquals("No Consent", telephoneConsentStatus, "Status for Natan Bolderi's Telephone Consent");

      // Testing for Lucci Gucci

      String lucciGucciYesConsentCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('LUCCI GUCCI')).out('Has_Consent')" +
                      ".count().next().toString()").get().toString();
      assertEquals("5", lucciGucciYesConsentCount, "Number of Consents granted by Lucci Gucci");

      String lucciGucciBDay =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('LUCCI GUCCI'))" +
                      ".properties('Person_Natural_Date_Of_Birth').value().next().toString()").get().toString();
//      lucciGucciBDay = lucciGucciBDay.replaceAll("... 1965", "GMT 1965");
      assertEquals(dtfmt.parse("Sat Jan 23 01:01:01 GMT 1965"), dtfmt.parse(lucciGucciBDay), "Lucci Gucci's Birthday");

      // testing for Virginia Mars

      String virginiaMarsConsentCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('VIRGINIA MARS')).out('Has_Consent')" +
                      ".has('Event_Consent_Status', eq('No Consent')).count().next().toString()").get().toString();
      assertEquals("5", virginiaMarsConsentCount, "Number of No Consents given by Virginia Mars");

      String virginiaMarsCustomerID =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('VIRGINIA MARS')).out('Has_Consent')" +
                      ".properties('Event_Consent_Customer_ID').value().next().toString()").get().toString();
      assertEquals("23453454354", virginiaMarsCustomerID, "Virginia Mars' Tax Number");

    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00003SapCapMyPChangeReport() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/my-p-change-report.csv", "cap_my_p_change_report");

      String ingestionOperationType =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('FELIPE MAGNOLI')).out('Has_Ingestion_Event')" +
                      ".properties('Event_Ingestion_Operation').value().next().toString()").get().toString();
      assertEquals("Structured Data Insertion", ingestionOperationType, "Type of operation for Felipe's data");

      String DataSourceName =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MÔNICA COELHO'))" +
                      ".out('Has_Ingestion_Event').in('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".properties('Object_Data_Source_Name').value().next().toString()").get().toString();
      assertEquals("SAP/C@P MY PORSCHE CHANGE REPORT", DataSourceName, "The source for Mônica's data");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00004SapCapPConnectMandatoryFields() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/p-connect-mandatory-fields.csv", "cap_p_connect_mandatory_fields");

      String jamesEmail =
              App.executor.eval("App.g.V().has('Object_Email_Address_Email', eq('hollywood@movies.com')).in('Uses_Email')" +
                      ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("FORTUNI JAMES", jamesEmail, "James' Email");


      String jamesPhoneNumber =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('FORTUNI JAMES')).out('Has_Phone')" +
                      ".properties('Object_Phone_Number_Raw').value().next().toString()").get().toString();
      assertEquals("302543787", jamesPhoneNumber, "James' mobile number");

      String responsibleDealer =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('FORTUNI JAMES')).in('Is_Responsible')" +
                      ".properties('Person_Organisation_Form_Id').value().next().toString()").get().toString();
      assertEquals("784384", responsibleDealer, "James' responsible dealer");

      String respSalesDealer =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('FORTUNI JAMES')).in('Is_Responsible')" +
                      ".properties('Person_Organisation_Name').value().next().toString()").get().toString();
      assertEquals("P CENTER CURITIBA", respSalesDealer, "James' responsible dealer");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00005SapCapOwnershipChange() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/ownership-change.csv", "cap_ownership_change");

      String getResponsibleDealer =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('AHOII BRAUSE')).in('Is_Responsible')" +
                      ".properties('Person_Organisation_Name').value().next().toString()").get().toString();
      assertEquals("PORSCHE CENTER SÃO PAULO", getResponsibleDealer, "Responsible Dealer");


      String getResponsibleOwner =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('PORSCHE CENTER SÃO PAULO')).out('Is_Responsible')" +
                      ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("AHOII BRAUSE", getResponsibleOwner, "client");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00006SapCapComplaint() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/complaint.csv", "cap_complaint");

      String getComplaintDescription =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('DORIA MARCONDES')).out('Has_Complaint')" +
                      ".properties('Event_Complaint_Description').value().next().toString()").get().toString();
      assertEquals("O carro estava estragado; Resolvido = Sim; Finalizado = Sim.", getComplaintDescription,
              "Descrição da reclamação de Doria");

      String getComplaintStatus =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('MEI KELVIN')).in('Works').out('Has_Complaint')" +
                      ".properties('Event_Complaint_Status').value().next().toString()").get().toString();
      assertEquals("Em analise", getComplaintStatus, "Status do Complaint de Kelvin");

      String marcosCountry =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('MARCOS KELVIN')).out('Is_Located')" +
                      ".properties('Location_Address_parser_country').value().next().toString()").get().toString();
      assertEquals("[brasil]", marcosCountry, "Marcos is located in Brasil");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00007SapCapActivity() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/activity.csv", "cap_activity");

      String getQuasimodoMobile =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('QUASIMODO PARIS')).out('Has_Mobile')" +
                      ".properties('Object_Phone_Number_Raw').value().next().toString()").get().toString();
      assertEquals("+5549987776575", getQuasimodoMobile, "Quasimodo's mobile");


      String alexEmail =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('ALEX KOVAS')).out('Uses_Email')" +
                      ".properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("alex_kovas_10@gmail.com", alexEmail, "Email de Alex Kovas");

      String locationParser =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('QUASIMODO PARIS')).out('Is_Located')" +
                      ".properties('Location_Address_parser_city').value().next().toString()").get().toString();
      assertEquals("[fortaleza]", locationParser, "Quasimodo is located in Fortaleza - Brasil");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00008SapCapWorkshopCampaignsAndRecalls() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/workshop-campaigns-and-recalls.csv", "cap_workshop_campaigns_and_recalls");

      String getRecallCampaignId =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('CAMILLA FLAVINA')).out('Has_Campaign')" +
                      ".properties('Object_Campaign_Id').value().next().toString()").get().toString();
      assertEquals("7856", getRecallCampaignId, "Id do contrato de Camilla");

      String getCampaignDescription =
              App.executor.eval("App.g.V().has('Location_Address_Full_Address'," +
                      "eq('Rua Germano de Rusky 551 , São Paulo (Região Norte), Marilia - Brasil, 84567-933')).in('Is_Located')" +
                      ".out('Has_Campaign').properties('Object_Campaign_Description').value().next().toString()").get().toString();
      assertEquals("Campanha de Marketing de novo modelo de carro", getCampaignDescription, "Descrição da campanha");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00009SapCapCompetitorVehicles() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/competitor-vehicles.csv", "cap_competitor_vehicles");

      String getVehicleLicensePlate =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('FABIOLA NAKAMURA')).out('Has_Vehicle')" +
                      ".properties('Object_Vehicle_License_Plate').value().next().toString()").get().toString();
      assertEquals("A566-T567", getVehicleLicensePlate, "Fabiola's Car's License plate");


      String getVehicleModel =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('URIEL')).out('Has_Vehicle')" +
                      ".properties('Object_Vehicle_Model').value().next().toString()").get().toString();
      assertEquals("AX45FD", getVehicleModel, "Uriel's Vehicle's Model");

      String getMobileNumber =
              App.executor.eval("App.g.V().has('Object_Phone_Number_Raw',eq('+554130964576')).in('Has_Phone')" +
                      ".out('Has_Mobile').properties('Object_Phone_Number_Raw').value().next().toString()").get().toString();
      assertEquals("+55(45)99165-6544", getMobileNumber, "Fabiola's Mobile number");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00010SapCapVehicle() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/vehicle.csv", "cap_vehicle");

      String getVehicleLicensePlate =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('HELGA BARBOSA')).out('Has_Vehicle')" +
                      ".properties('Object_Vehicle_License_Plate').value().next().toString()").get().toString();
      assertEquals("S987-T098", getVehicleLicensePlate, "Helga's Car's License plate");

      String getVehicleModel =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('JORGE NIAGURA')).out('Has_Vehicle')" +
                      ".properties('Object_Vehicle_Model').value().next().toString()").get().toString();
      assertEquals("ZXR500", getVehicleModel, "Jorge's Vehicle's Model");

      String getEmailAddress =
              App.executor.eval("App.g.V().has('Object_Phone_Number_Last_7_Digits',eq('1657448')).in('Has_Mobile')" +
                      ".out('Uses_Email').properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("jorginho10@icloud.com", getEmailAddress, "Jorge's Email");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00011SapCapDataQuality() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/data-quality.csv", "cap_data_quality");

      String mariaCompany =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('MARIA DOLORES'))" +
                      ".out('Works').properties('Person_Organisation_Name').value().next().toString()").get().toString();
      assertEquals("CIMED INDÚSTRIA DE MEDICAMENTOS LTDA.", mariaCompany, "Maria' company");

      String locationAddress =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('MARIA DOLORES'))" +
                      ".out('Is_Located').properties('Location_Address_parser_city').value().next().toString()").get().toString();
      assertEquals("[sao paulo, saopaulo]", locationAddress, "Maria' city");

      String cimedTaxNumber =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('CIMED INDÚSTRIA DE MEDICAMENTOS LTDA.')).in('Works')" +
                      ".out('Has_Id_Card').has('Object_Identity_Card_Id_Type', eq('CPF')).properties('Object_Identity_Card_Id_Value').value().next().toString()").get().toString();
      assertEquals("18586336807", cimedTaxNumber, "CIMED's Tax Number");

      String emailNameTest =
              App.executor.eval("App.g.V().has('Object_Email_Address_Email',eq('nelquio@hotmail.com'))" +
                      ".in('Uses_Email').properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("MARIA DOLORES", emailNameTest);

      String mariaPostalCode =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('MARIA DOLORES')).out('Is_Located')" +
                      ".properties('Location_Address_parser_postcode').value().next().toString()").get().toString();
      assertEquals("[04514-050]", mariaPostalCode, "Maria's post code");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }


}
