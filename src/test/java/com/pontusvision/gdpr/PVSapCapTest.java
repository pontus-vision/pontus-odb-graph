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
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('EMILY VARGAS'))" +
                      ".bothE('Has_Phone').count().next().toString()").get().toString();
      // Object_Contract has been removed ... so no more Is_Lead EDGE ... total of 6 EDGES
      assertEquals("2", marcelPhoneCount, "Marcel has 2 phone numbers registered");


      String fullName =
              App.executor.eval("App.g.V().has('Location_Address_Full_Address'," +
                      "eq('test street 1 casa 110, Rio, Rio de Janeiro RJ - Brasil, 12345')).in('Is_Located')" +
                      ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("EMILY VARGAS", fullName, "Full Name de Marcel Molica Araújo");


      String gettingEmailAddress =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('EMILY VARGAS')).out('Uses_Email')" +
                      ".properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("test@test.com", gettingEmailAddress, "E-mail de Marcel Molica Araújo");

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
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('BORGES KALL')).out('Uses_Email')" +
                      ".properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("test@test.com", gettingEmailAddress, "E-mail");

      String bDay =
              App.executor.eval("App.g.V().has('Location_Address_Post_Code'," +
                      "eq('90982-090'))" +
                      ".in('Is_Located').properties('Person_Natural_Date_Of_Birth').value().next().toString()").get().toString();
      assertEquals(dtfmt.parse("Tue Mar 08 01:01:01 UTC 2022"), dtfmt.parse(bDay), "Data de nascimento");

      String eventConsentCreatedBy =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('BORGES KALL')).out('Has_Consent')" +
                      ".properties('Event_Consent_Created_By').value().next().toString()").get().toString();
      assertEquals("87NJI239", eventConsentCreatedBy, "Employee code");

      String ConsentCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('BORGES KALL')).out('Has_Consent')" +
                      ".has('Event_Consent_Status', eq('Consent')).count().next().toString()").get().toString();
      assertEquals("5", ConsentCount, "Number of Consents granted");

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
      assertEquals("SAP/C@P MY PXXXXXX CHANGE REPORT", DataSourceName, "The source for Mônica's data");

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
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('FORTUNI JAMES')).out('Is_Client')" +
                      ".properties('Person_Organisation_Form_Id').value().next().toString()").get().toString();
      assertEquals("784384", responsibleDealer, "James' responsible dealer");

      String respSalesDealer =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('FORTUNI JAMES')).out('Is_Client')" +
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
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P PXXXXXX OWNERSHIP CHANGE'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('LARA CRAFT')).out('Is_Client')" +
                      ".properties('Person_Organisation_Name').value().next().toString()").get().toString();
      assertEquals("P CENTER SÃO PAULO", getResponsibleDealer, "Responsible Dealer");


      String getResponsibleOwner =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P PXXXXXX OWNERSHIP CHANGE'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".out('Is_Client').as('person-org')" +
                      ".has('Person_Organisation_Name',eq('P CENTER SÃO PAULO')).in('Is_Client')" +
                      ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("LARA CRAFT", getResponsibleOwner, "client");

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
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MUSTAFA GHADAS')).out('Has_Complaint')" +
                      ".properties('Event_Complaint_Description').value().next().toString()").get().toString();
      assertEquals("COMPLAINT MANAGEMENT LTDA.", getComplaintDescription, "Descrição da reclamação");

      String getComplaintStatus =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('GHADAS DOCES')).in('Works').out('Has_Complaint')" +
                      ".properties('Event_Complaint_Status').value().next().toString()").get().toString();
      assertEquals("M", getComplaintStatus, "Status do Complaint");

      String mustafaCountry =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('MUSTAFA GHADAS')).out('Is_Located')" +
                      ".properties('Location_Address_parser_country').value().next().toString()").get().toString();
      assertEquals("[brasil]", mustafaCountry);


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
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('GABRIEL MOSHI')).out('Has_Mobile')" +
                      ".properties('Object_Phone_Number_Raw').value().next().toString()").get().toString();
      assertEquals("94327596090", getQuasimodoMobile, "Mobile");


      String alexEmail =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('GABRIEL MOSHI')).out('Uses_Email')" +
                      ".properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("foster@mail.com", alexEmail, "Email");

      String locationParser =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('GABRIEL MOSHI')).out('Is_Located')" +
                      ".properties('Location_Address_parser_city').value().next().toString()").get().toString();
      assertEquals("[salvador]", locationParser);

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
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('RODOLFO FIGUEIREDO')).out('Has_Campaign')" +
                      ".properties('Event_Campaign_Id').value().next().toString()").get().toString();
      assertEquals("938987", getRecallCampaignId, "Id do contrato");

      String getCampaignDescription =
              App.executor.eval("App.g.V().has('Location_Address_Full_Address'," +
                      "eq('av Maria Mar 99 Apt 61, SC Rebouças, Taio - Brasil, 18958-980')).in('Is_Located')" +
                      ".out('Has_Campaign').properties('Event_Campaign_Description').value().next().toString()").get().toString();
      assertEquals("Test", getCampaignDescription, "Descrição da campanha");

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
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('EDSON THOMAS')).out('Has_Vehicle')" +
                      ".properties('Object_Vehicle_License_Plate').value().next().toString()").get().toString();
      assertEquals("AJN93248", getVehicleLicensePlate, "License plate");


      String getVehicleModel =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('EDSON THOMAS')).out('Has_Vehicle')" +
                      ".properties('Object_Vehicle_Model').value().next().toString()").get().toString();
      assertEquals("Super Sport", getVehicleModel, "Vehicle's Model");

      String getMobileNumber =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('sap/c@p Competitor Vehicles'))" +
                      ".in('Has_Ingestion_Event').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
                      ".out('Has_Mobile').properties('Object_Phone_Number_Raw').value().next()").get().toString();
      assertEquals("098098704", getMobileNumber, "Mobile number");

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
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('THOMAS THAMES')).out('Has_Vehicle')" +
                      ".properties('Object_Vehicle_License_Plate').value().next().toString()").get().toString();
      assertEquals("JHFU7857", getVehicleLicensePlate, "Thomas' Car's License plate");

      String getVehicleModel =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('THOMAS THAMES')).out('Has_Vehicle')" +
                      ".properties('Object_Vehicle_Model').value().next().toString()").get().toString();
      assertEquals("766", getVehicleModel, "Thomas' Vehicle's Model");

      String getEmailAddress =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".out('Has_Mobile').as('mobile-phone')" +
                      ".has('Object_Phone_Number_Last_7_Digits',eq('6453545'))" +
                      ".in('Has_Mobile')" +
                      ".out('Uses_Email').properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("colchoes.thames@hotmail.com", getEmailAddress, "Thomas' job Email");

      String thomasIsClient =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".out('Is_Client').as('p-company')" +
                      ".properties('Person_Organisation_Name').value().next().toString()").get().toString();
      assertEquals("P CENTER SÃO PAULO", thomasIsClient, "Thomas is P's client");


      String thomasId =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".in('Has_Ingestion_Event').as('person-natural')" +
                      ".out('Has_Id_Card').as('cnpj')" +
                      ".properties('Object_Identity_Card_Id_Value').value().next().toString()").get().toString();
      assertEquals("28938923809747", thomasId, "Thomas' company Tax ID");
    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00011SapCapDataQuality() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/data-quality.csv", "cap_data_quality");

      String kumaCompany =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('KUMA CASTRO'))" +
                      ".out('Works').properties('Person_Organisation_Name').value().next().toString()").get().toString();
      assertEquals("PHARMA", kumaCompany, "Kuma's company");

      String locationAddress =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('KUMA CASTRO'))" +
                      ".out('Is_Located').properties('Location_Address_parser_city').value().next().toString()").get().toString();
      assertEquals("[sao paulo, saopaulo]", locationAddress, "Kuma's city");

      String pharmaTaxNumber =
              App.executor.eval("App.g.V().has('Person_Organisation_Name',eq('PHARMA')).in('Works')" +
                      ".out('Has_Id_Card').has('Object_Identity_Card_Id_Type', eq('CPF')).properties('Object_Identity_Card_Id_Value').value().next().toString()").get().toString();
      assertEquals("21398213986", pharmaTaxNumber, "Pharma's Tax Number");

      String emailNameTest =
              App.executor.eval("App.g.V().has('Object_Email_Address_Email',eq('castro.kuma@hotmail.com'))" +
                      ".in('Uses_Email').properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("KUMA CASTRO", emailNameTest);

      String kumaPostalCode =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name',eq('KUMA CASTRO')).out('Is_Located')" +
                      ".properties('Location_Address_parser_postcode').value().next().toString()").get().toString();
      assertEquals("[98767-090]", kumaPostalCode, "Kuma's postal code");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }


}
