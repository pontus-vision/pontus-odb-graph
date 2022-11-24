package com.pontusvision.gdpr;

import com.pontusvision.ingestion.Ingestion;
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
    // updated to real style data lead-reporting.csv

    try {

      String dirtyHeader = "A string. With; loads: of # Chars,{ a.;~^Ç``\"oçã}][";
      String cleanHdr = Ingestion.cleanHeader(dirtyHeader);
      assertEquals(
              "A_string__With__loads__of___Chars___a____C___oca___",
              cleanHdr,
              "check clean headers is OK");

      csvTestUtil("SAP/sap-cap/lead-reporting.csv", "cap_lead_reporting");

      String marcelPhoneCount =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('EMILY VARGAS'))" +
                      ".bothE('Has_Phone').count().next().toString()").get().toString();
      // Object_Contract has been removed ... so no more Is_Lead EDGE ... total of 6 EDGES
      assertEquals("2", marcelPhoneCount, "Marcel has 2 phone numbers registered");


      String fullName =
              App.executor.eval("App.g.V().has('Location_Address_Full_Address'," +
                      "eq('test street 1 casa 110, Rio, Rio de Janeiro RJ - BR, 12345')).in('Is_Located')" +
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

      csvTestUtil("SAP/sap-cap/my-pbr-change-report.csv", "cap_my_pbr_change_report");

      String ingestionOperationType =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('FELIPE MAGNOLI')).out('Has_Ingestion_Event')" +
                      ".properties('Event_Ingestion_Operation').value().next().toString()").get().toString();
      assertEquals("Structured Data Insertion", ingestionOperationType, "Type of operation for Felipe's data");

      String DataSourceName =
              App.executor.eval("App.g.V().has('Person_Natural_Full_Name', eq('MÔNICA COELHO'))" +
                      ".out('Has_Ingestion_Event').in('Has_Ingestion_Event').in('Has_Ingestion_Event')" +
                      ".properties('Object_Data_Source_Name').value().next().toString()").get().toString();
      assertEquals("SAP/C@P MY PBR CHANGE REPORT", DataSourceName, "The source for Mônica's data");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00004SapCapPConnectMandatoryFields() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/pbr-connect-mandatory-fields.csv", "cap_pbr_connect_mandatory_fields");

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

      csvTestUtil("SAP/sap-cap/ownership-reporting.csv", "cap_ownership_reporting");

      String getResponsibleOwner =
        App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P PBR OWNERSHIP REPORTING'))" +
          ".out('Has_Ingestion_Event').as('group-ingestion')" +
          ".out('Has_Ingestion_Event').as('event-ingestion')" +
          ".in('Has_Ingestion_Event').as('person-natural')" +
          ".out('Is_Client').as('person-org')" +
          ".has('Person_Organisation_Name',eq('P CENTER SÃO PAULO')).in('Is_Client')" +
          ".properties('Person_Natural_Full_Name').value().next().toString()").get().toString();
      assertEquals("LARA CRAFT", getResponsibleOwner, "client");

      String capPersonRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Person_Natural_Full_Name\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"" + getResponsibleOwner + "\"\n" +
          "  }\n" +
          "]", "Person_Natural",
        new String[]{"Person_Natural_Full_Name"});

      RecordReply reply = gridWrapper(null, "Person_Organisation", new String[]{"Person_Organisation_Name"},
        "hasNeighbourId:" + capPersonRid);
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Person_Organisation_Name\":\"P CENTER SÃO PAULO\""), "Responsible Dealer");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00006SapCapComplaint() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/complaint-reporting.csv", "cap_complaint_reporting");

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
      assertEquals("[bairro, br, brace, brae, branch, bridge, brother]", mustafaCountry);


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00007SapCapActivity() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/activity-reporting.csv", "cap_activity_reporting");

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
                      ".properties('Object_Campaign_Id').value().next().toString()").get().toString();
      assertEquals("938987", getRecallCampaignId, "Id do contrato");

      String getCampaignDescription =
              App.executor.eval("App.g.V().has('Location_Address_Full_Address'," +
                      "eq('av Maria Mar 99 Apt 61, SC Rebouças, Taio - BR, 18958-980')).in('Is_Located')" +
                      ".out('Has_Campaign').properties('Object_Campaign_Description').value().next().toString()").get().toString();
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
                      ".out('Has_Ingestion_Event').in('Has_Vehicle').has('Metadata_Type_Person_Natural', eq('Person_Natural'))" +
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

      csvTestUtil("SAP/sap-cap/vehicle-reporting.csv", "cap_vehicle_reporting");

      String getVehicleLicensePlate = App.executor.eval(
        "App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE REPORTING'))" +
          ".out('Has_Ingestion_Event').as('group-ingestion')" +
          ".out('Has_Ingestion_Event').as('event-ingestion')" +
          ".out('Has_Ingestion_Event').as('vehicle')" +
          ".in('Has_Vehicle')" +
          ".has('Person_Natural_Full_Name', eq('THOMAS THAMES')).out('Has_Vehicle')" +
          ".values('Object_Vehicle_License_Plate').next().toString()").get().toString();
      assertEquals("JHFU7857", getVehicleLicensePlate, "Thomas' Car's License plate");

      String getVehicleModel =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE REPORTING'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('vehicle')" +
                      ".in('Has_Vehicle').as('person-natural')" +
                      ".has('Person_Natural_Full_Name', eq('THOMAS THAMES')).out('Has_Vehicle')" +
                      ".properties('Object_Vehicle_Model').value().next().toString()").get().toString();
      assertEquals("766", getVehicleModel, "Thomas' Vehicle's Model");

      String getEmailAddress =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE REPORTING'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('vehicle')" +
                      ".in('Has_Vehicle').as('person-natural')" +
                      ".out('Has_Mobile').as('mobile-phone')" +
                      ".has('Object_Phone_Number_Last_7_Digits',eq('6453545'))" +
                      ".in('Has_Mobile')" +
                      ".out('Uses_Email').properties('Object_Email_Address_Email').value().next().toString()").get().toString();
      assertEquals("colchoes.thames@hotmail.com", getEmailAddress, "Thomas' job Email");

      String thomasIsClient =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE REPORTING'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('vehicle')" +
                      ".in('Has_Vehicle').as('person-natural')" +
                      ".out('Is_Client').as('p-company')" +
                      ".properties('Person_Organisation_Name').value().next().toString()").get().toString();
      assertEquals("P CENTER SÃO PAULO", thomasIsClient, "Thomas is P's client");


      String thomasId =
              App.executor.eval("App.g.V().has('Object_Data_Source_Name', eq('SAP/C@P VEHICLE REPORTING'))" +
                      ".out('Has_Ingestion_Event').as('group-ingestion')" +
                      ".out('Has_Ingestion_Event').as('event-ingestion')" +
                      ".out('Has_Ingestion_Event').as('vehicle')" +
                      ".in('Has_Vehicle').as('person-natural')" +
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

      csvTestUtil("SAP/sap-cap/data-quality-reporting.csv", "cap_data_quality_reporting");

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

  @Test
  public void test00012SapCapComplaintsWorklist() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/complaints-worklist.csv", "cap_complaints_worklist");

      String capGroupEventRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Event_Group_Ingestion_Type\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"sap/c@p Complaints Worklist\"\n" +
          "  }\n" +
          "]", "Event_Group_Ingestion",
        new String[]{"Event_Group_Ingestion_Type"});

      RecordReply reply = gridWrapper(null, "Object_Data_Source", new String[]{"Object_Data_Source_Name"},
        "hasNeighbourId:" + capGroupEventRid);
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Data_Source_Name\":\"SAP/C@P COMPLAINTS WORKLIST\""),
        "Data source name");

      reply = gridWrapper("[\n" +
          "  {\n" +
          "    \"colId\": \"Event_Complaint_Type\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"SAP/C@P Complaints Worklist\"\n" +
          "  }\n" +
          "]", "Event_Complaint",
        new String[]{"Event_Complaint_Type", "Event_Complaint_Description", "Event_Complaint_Status"});
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Event_Complaint_Description\":\"Test PBR\""), "Complaint description");
      assertTrue(replyStr.contains("\"Event_Complaint_Status\":\"Work in Progress\""), "Complaint status");

      String capComplaintRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Event_Complaint_Type\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"SAP/C@P Complaints Worklist\"\n" +
          "  }\n" +
          "]", "Event_Complaint",
        new String[]{"Event_Complaint_Type"});

      reply = gridWrapper(null, "Person_Natural", new String[]{"Person_Natural_Full_Name", "Person_Natural_Customer_ID"},
        "hasNeighbourId:" + capComplaintRid);
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Person_Natural_Full_Name\":\"MORGANA ADDAMS\""), "Person name");
      assertTrue(replyStr.contains("\"Person_Natural_Customer_ID\":\"94562\""), "Morgana's customer ID");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00013SapCapMarketingCampaignReporting() throws InterruptedException {

    try {

      csvTestUtil("SAP/sap-cap/marketing-campaign-reporting.csv", "cap_marketing_campaign_reporting");

      String capGroupEventRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Event_Group_Ingestion_Type\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"sap/c@p Marketing Campaign Reporting\"\n" +
          "  }\n" +
          "]", "Event_Group_Ingestion",
        new String[]{"Event_Group_Ingestion_Type"});

      RecordReply reply = gridWrapper(null, "Object_Data_Source", new String[]{"Object_Data_Source_Name"},
        "hasNeighbourId:" + capGroupEventRid);
      String replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Data_Source_Name\":\"SAP/C@P MARKETING CAMPAIGN REPORTING\""),
        "Data source name");

      reply = gridWrapper("[\n" +
          "  {\n" +
          "    \"colId\": \"Object_Campaign_Type\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"SAP/C@P Marketing Campaign Reporting\"\n" +
          "  }\n" +
          "]", "Object_Campaign",
        new String[]{"Object_Campaign_Type", "Object_Campaign_Status", "Object_Campaign_Date", "Object_Campaign_Id"});
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Campaign_Status\":\"P\""), "Campaign status");
      assertTrue(replyStr.contains("\"Object_Campaign_Date\":\"Tue May 11 01:01:01 UTC 2021\""), "Campaign date");
      assertTrue(replyStr.contains("\"Object_Campaign_Id\":\"QS99 PBR MM1\""), "Campaign ID");

      String capCampaignRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Object_Campaign_Type\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"SAP/C@P Marketing Campaign Reporting\"\n" +
          "  }\n" +
          "]", "Object_Campaign",
        new String[]{"Object_Campaign_Type"});

      reply = gridWrapper(null, "Person_Natural", new String[]{"Person_Natural_Full_Name",
          "Person_Natural_Customer_ID", "Person_Natural_Title"}, "hasNeighbourId:" + capCampaignRid);
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Person_Natural_Full_Name\":\"ROCHELLE DE LA ROCHEFOUCAULD\""), "Person name");
      assertTrue(replyStr.contains("\"Person_Natural_Customer_ID\":\"782297\""), "Rochelle's customer ID");
      assertTrue(replyStr.contains("\"Person_Natural_Title\":\"Mme.\""), "Rochelle's title");


      String capPersonRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Person_Natural_Customer_ID\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"782297\"\n" +
          "  }\n" +
          "]", "Person_Natural",
        new String[]{"Person_Natural_Customer_ID"});

      reply = gridWrapper(null, "Person_Organisation", new String[]{"Person_Organisation_Name"},
        "hasNeighbourId:" + capPersonRid, 0L, 2L, "Person_Organisation_Name", "+asc");

      assertTrue(reply.getRecords()[0].contains("\"Person_Organisation_Name\":\"PBR CENTER SÃO PAULO\""), "other_company [Is_Client] edge");
      assertTrue(reply.getRecords()[1].contains("\"Person_Organisation_Name\":\"ROCHEFOUCAULD FONDUE\""), "own_client [Works] edge");

      reply = gridWrapper(null, "Object_Email_Address", new String[]{"Object_Email_Address_Email"},"hasNeighbourId:" + capPersonRid);
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Object_Email_Address_Email\":\"rochelle@test.com\""), "Email address");

      reply = gridWrapper(null, "Object_Phone_Number", new String[]{"Object_Phone_Number_Last_7_Digits"},
        "hasNeighbourId:" + capPersonRid, 0L, 2L, "Object_Phone_Number_Last_7_Digits", "+asc");

      assertTrue(reply.getRecords()[0].contains("\"Object_Phone_Number_Numbers_Only\":\"00000000\""), "Phone number");
      assertTrue(reply.getRecords()[1].contains("\"Object_Phone_Number_Numbers_Only\":\"551545982435653\""), "Mobile number");

      reply = gridWrapper(null, "Location_Address", new String[]{"Location_Address_Full_Address"},"hasNeighbourId:" + capPersonRid);
      replyStr = reply.getRecords()[0];

      assertTrue(replyStr.contains("\"Location_Address_Full_Address\":\"Rua Paris 287, São Paulo, Centro SP - BR, 87862-943\""), "Location Address");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

}
