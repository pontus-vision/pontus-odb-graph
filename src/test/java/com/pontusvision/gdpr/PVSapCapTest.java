package com.pontusvision.gdpr;

import com.pontusvision.ingestion.Ingestion;
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
  public void test00001SapCapLeads() throws InterruptedException {
  // updated to real style data lead.csv

    try {

      String dirtyHeader = "A string. With; loads: of # Chars,{ a.;~^Ç``\"oçã}][";
      String cleanHdr = Ingestion.cleanHeader(dirtyHeader);
      assertEquals(
          "A_string__With__loads__of___Chars___a____C___oca___",
          cleanHdr,
          "check clean headers is OK");

      csvTestUtil("sap-cap/lead.csv", "cap_lead");

      String personNaturalEdgesCount =
           App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MARCEL MOLICA ARAÚJO'))" +
              ".bothE().count().next().toString()").get().toString();
      // Object.Contract has been removed ... so no more Is_Lead EDGE ... total of 6 EDGES
      assertEquals("6", personNaturalEdgesCount, "2 Has_Phone + 1 Event.Ingestion + 1 Works " +
          "+ 1 Is_Located + 1 Uses_Email");


      String fullName =
          App.executor.eval("App.g.V().has('Location.Address.Full_Address'," +
              "eq('Rua Tarcisio Vanderbilt 260 Casa, Araraquara, São Paulo (SP) - BR, 88650024')).in('Is_Located')" +
              ".properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("MARCEL MOLICA ARAÚJO", fullName, "Full Name de Marcel Molica Araújo");


      String gettingEmailAddress =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('MARCEL MOLICA ARAÚJO')).out('Uses_Email')" +
              ".properties('Object.Email_Address.Email').value().next().toString()").get().toString();
      assertEquals("marcelmolica@hotmail.com", gettingEmailAddress, "E-mail de Marcel Molica Araújo");

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

      String gettingEmailAddress =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('NATAN BOLDERI')).out('Uses_Email')" +
                      ".properties('Object.Email_Address.Email').value().next().toString()").get().toString();
      assertEquals("natinhobolderi@gmail.com", gettingEmailAddress, "E-mail de Natan Bolderi");

      String natanBDay =
          App.executor.eval("App.g.V().has('Location.Address.Post_Code'," +
              "eq('14022-000'))" +
              ".in('Is_Located').properties('Person.Natural.Date_Of_Birth').value().next().toString()").get().toString();
      // replace the timezone with GMT; otherwise, if the test is run in Brazil, that appears as BRT 1975
      // ... in regex means any character, so ... 1976 will replace BRT 1976 with GMT 1975
      natanBDay = natanBDay.replaceAll("... 1975", "GMT 1975");
      assertEquals("Thu May 08 01:01:01 GMT 1975", natanBDay, "Data de nascimento de Natan Bolderi");

      String eventConsentCreatedBy =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('NATAN BOLDERI')).out('Has_Consent')" +
                      ".properties('Event.Consent.Created_By').value().next().toString()").get().toString();
      assertEquals("DBR0200WK", eventConsentCreatedBy, "Porsche Employee code");

//      String countEventConsentEdges =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('NATAN BOLDERI')).out('Has_Consent')" +
//                      ".bothE().count().next().toString()").get().toString();
//      assertEquals("8", countEventConsentEdges, "6 Has_Privacy_Notice (e_mail_consent, telefone_consent" +
//              ", cust_pros_consent, fax_sms_consent, social_media_consent, mail_consent) + 1 Has_Consent + 1 Has_Ingestion_Event");

//      String personNaturalEdgesCount =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('NATAN BOLDERI'))" +
//                      ".bothE().otherV().path().toList()").get().toString();
//      assertEquals("[path[v[#234:0], e[#266:0][#234:0-Is_Located->#90:0], v[#90:0]], path[v[#234:0]," +
//                      " e[#254:1][#234:0-Has_Consent->#19:0], v[#19:0]], path[v[#234:0], e[#255:0][#234:0-Has_Consent->#20:0]," +
//                      " v[#20:0]], path[v[#234:0], e[#254:0][#234:0-Has_Consent->#18:0], v[#18:0]], path[v[#234:0]," +
//                      " e[#258:1][#234:0-Has_Phone->#195:0], v[#195:0]], path[v[#234:0], e[#258:0][#234:0-Has_Phone->#194:0]," +
//                      " v[#194:0]], path[v[#234:0], e[#270:0][#234:0-Works->#238:0], v[#238:0]], path[v[#234:0]," +
//                      " e[#262:0][#234:0-Uses_Email->#150:0], v[#150:0]], path[v[#234:0], e[#246:2][#234:0-Has_Ingestion_Event->#70:0]," +
//                      " v[#70:0]], path[v[#234:0], e[#250:0][#234:0-Has_Id_Card->#174:0], v[#174:0]]]",
//              personNaturalEdgesCount, "All EDGES details related to Person.Natural Natan Bolderi");

      String natanBolderiNoConsentCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('NATAN BOLDERI')).out('Has_Consent')" +
                      ".has('Event.Consent.Status', eq('No Consent')).count().next().toString()").get().toString();
      assertEquals("1", natanBolderiNoConsentCount, "Number of No Consents granted by Natan Bolderi");

      String natanBolderiYesConsentCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('NATAN BOLDERI')).out('Has_Consent')" +
                      ".has('Event.Consent.Status', eq('Consent')).count().next().toString()").get().toString();
      assertEquals("2", natanBolderiYesConsentCount, "Number of Consents granted by Natan Bolderi");

      String telephoneConsentStatus =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('NATAN BOLDERI')).out('Has_Consent')" +
                      ".has('Event.Consent.Description', eq('Telephone Consent')).properties('Event.Consent.Status')" +
                      ".value().next().toString()").get().toString();
      assertEquals("No Consent", telephoneConsentStatus, "Status for Natan Bolderi's Telephone Consent");

      // Testing for Lucci Gucci

      String lucciGucciYesConsentCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('LUCCI GUCCI')).out('Has_Consent')" +
                      ".count().next().toString()").get().toString();
      assertEquals("5", lucciGucciYesConsentCount, "Number of Consents granted by Lucci Gucci");

      String lucciGucciBDay =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('LUCCI GUCCI'))" +
                      ".properties('Person.Natural.Date_Of_Birth').value().next().toString()").get().toString();
      lucciGucciBDay = lucciGucciBDay.replaceAll("... 1965", "GMT 1965");
      assertEquals("Sat Jan 23 01:01:01 GMT 1965", lucciGucciBDay, "Lucci Gucci's Birthday");
      
      // testing for Virginia Mars

      String virginiaMarsConsentCount =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('VIRGINIA MARS')).out('Has_Consent')" +
                      ".has('Event.Consent.Status', eq('No Consent')).count().next().toString()").get().toString();
      assertEquals("5", virginiaMarsConsentCount, "Number of No Consents given by Virginia Mars");

      String virginiaMarsCustomerID =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('VIRGINIA MARS')).out('Has_Consent')" +
                      ".properties('Event.Consent.Customer_ID').value().next().toString()").get().toString();
      assertEquals("23453454354", virginiaMarsCustomerID, "Virginia Mars' Tax Number");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  //  @Test
//  public void test00003SapCapMonthlyCockpitsAndReports() throws InterruptedException {
//
//    try {
//
//      csvTestUtil("sap-cap/monthly-cockpits-and-reports.csv", "cap_monthly_cockpits_and_reports");
//
//      String fileIngestionNameEdgesCount =
//              App.executor.eval("App.g.V().has('Event.File_Ingestion.Name', eq('PONTUS.PDF'))" +
//                      ".bothE().count().next().toString()").get().toString();
//      assertEquals("2", fileIngestionNameEdgesCount, "2 Has_Ingestion_Event");
//
//
//      String fileMonthAndYear =
//              App.executor.eval("App.g.V().has('Object.Data_Procedures.ID',eq('578')).out('Has_Ingestion_Event')" +
//                      ".properties('Event.File_Ingestion.Last_Access').value().next().toString()").get().toString();
//      assertEquals("11/2020", fileMonthAndYear, "Mês e Ano do FILE");
//
//    } catch (ExecutionException e) {
//      e.printStackTrace();
//      assertNull(e);
//    }
//  }

  @Test
  public void test00004SapCapMyPChangeReport() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/my-p-change-report.csv", "cap_my_p_change_report");

//      String getCustomerId =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('FELIPE MAGNOLI'))" +
//                      ".properties('Person.Natural.Customer_ID').value().next().toString()").get().toString();
//      assertEquals("65746", getCustomerId, "P ID for Felipe Magnoli");


      String getFullNameById =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('MÔNICA COELHO'))" +
                      ".properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("MÔNICA COELHO", getFullNameById, "Person.Natural.Full_Name = Mônica Coelho");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00005SapCapPConnectMandatoryFields() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/p-connect-mandatory-fields.csv", "cap_p_connect_mandatory_fields");

      String getCustomerId =
              App.executor.eval("App.g.V().has('Object.Email_Address.Email', eq('gandipunjabi@icloud.com')).in('Uses_Email')" +
                      ".properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("GANDI PUNJABI", getCustomerId, "gandipunjabi@icloud.com is used by Gandi Punjabi");


      String getFullNameById =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('CAMILA DIOGINES')).out('Has_Phone')" +
                      ".properties('Object.Phone_Number.Raw').value().next().toString()").get().toString();
      assertEquals("+5541998675898", getFullNameById, "Camila Diogines' mobile number");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00006SapCapOwnershipChange() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/ownership-change.csv", "cap_ownership_change");

      String getResponsibleDealer =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('CAROL SANTANA')).in('Is_Responsible')" +
                      ".properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("FELLIPE XAXIM", getResponsibleDealer, "Felipe Xaxim is Carol's Responsible Dealer");


      String getResponsibleOwner =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('VINICIUS GAMA')).out('Is_Responsible')" +
                      ".properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("ARMANDO ZACHARIA", getResponsibleOwner, "Armando Zacharia is Vinicius' client");

//      String getOwnerType =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('CAROL SANTANA'))" +
//                      ".properties('Person.Natural.Type').value().next().toString()").get().toString();
//      assertEquals("Porsche Ownership Change", getOwnerType, "Person.Natural (owner) Type");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00007SapCapComplaint() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/complaint.csv", "cap_complaint");

      String getComplaintDescription =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('DORIA MARCONDES')).out('Has_Complaint')" +
                      ".properties('Event.Complaint.Description').value().next().toString()").get().toString();
      assertEquals("O carro estava estragado; Resolvido = Sim; Finalizado = Sim.", getComplaintDescription,
              "Descrição da reclamação de Doria");


      String getComplaintStatus =
              App.executor.eval("App.g.V().has('Person.Organisation.Name',eq('MEI KELVIN')).in('Works').out('Has_Complaint')" +
                      ".properties('Event.Complaint.Status').value().next().toString()").get().toString();
      assertEquals("Em analise", getComplaintStatus, "Status do Complaint de Kelvin");

      String countPersonNaturalEdges =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('MARCOS KELVIN'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("7", countPersonNaturalEdges, "1 Has_Phone + 1 Has_Mobile + 1 Has_Ingestion_Event + 1 Works " +
              "+ 1 Is_Located + 1 Uses_Email + 1 Has_Contract");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00008SapCapActivity() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/activity.csv", "cap_activity");

//      String getActivityDescription =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('QUASIMODO PARIS')).out('Has_Campaign')" +
//                      ".properties('Object.Campaign.Description').value().next().toString()").get().toString();
//      assertEquals("Cliente compra muitos carros", getActivityDescription,"Descrição da atividade de Quasimodo");


//      String getActivityStatus =
//              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('ALEX KOVAS')).out('Has_Contract')" +
//                      ".properties('Object.Contract.Status').value().next().toString()").get().toString();
//      assertEquals("Inativo", getActivityStatus, "Status da Activity de Alex");

      String countPersonNaturalEdges =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('QUASIMODO PARIS'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("4", countPersonNaturalEdges, "1 Has_Mobile + 1 Has_Ingestion_Event + 1 Works " +
              "+ 1 Is_Located");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00009SapCapWorkshopCampaignsAndRecalls() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/workshop-campaigns-and-recalls.csv", "cap_workshop_campaigns_and_recalls");

      String getRecallCampaignId =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('CAMILLA FLAVINA')).out('Has_Campaign')" +
                      ".properties('Object.Campaign.Id').value().next().toString()").get().toString();
      assertEquals("7856", getRecallCampaignId,"Id do contrato de Camilla");


      String getCampaignDescription =
              App.executor.eval("App.g.V().has('Location.Address.Full_Address'," +
                      "eq('Rua Germano de Rusky 551 , São Paulo (Região Norte), Marilia - Brasil, 84567-933')).in('Is_Located')" +
                      ".out('Has_Campaign').properties('Object.Campaign.Description').value().next().toString()").get().toString();
      assertEquals("Campanha de Marketing de novo modelo de carro", getCampaignDescription, "Descrição da campanha");

      String countPersonNaturalEdges =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('VALERIA BITTENCOURT'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("3", countPersonNaturalEdges, "1 Has_Ingestion_Event + 1 Is_Located + 1 Has_Contract");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00010SapCapCompetitorVehicles() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/competitor-vehicles.csv", "cap_competitor_vehicles");

      String getVehicleLicensePlate =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('FABIOLA NAKAMURA')).out('Has_Vehicle')" +
                      ".properties('Object.Vehicle.License_Plate').value().next().toString()").get().toString();
      assertEquals("A566-T567", getVehicleLicensePlate,"Fabiola's Car's License plate");


      String getVehicleModel =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('URIEL')).out('Has_Vehicle')" +
                      ".properties('Object.Vehicle.Model').value().next().toString()").get().toString();
      assertEquals("AX45FD", getVehicleModel, "Uriel's Vehicle's Model");

      String getMobileNumber =
              App.executor.eval("App.g.V().has('Object.Phone_Number.Raw',eq('+554130964576')).in('Has_Phone')" +
                      ".out('Has_Mobile').properties('Object.Phone_Number.Raw').value().next().toString()").get().toString();
      assertEquals("+55(45)99165-6544", getMobileNumber, "Fabiola's Mobile number");

      String countPersonNaturalEdges =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('FABIOLA NAKAMURA'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("6", countPersonNaturalEdges, "1 Has_Ingestion_Event + 1 Is_Located + 1 Has_Vehicle " +
              "+ 1 Has_Phone + 1 Has_Mobile + 1 Works");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00011SapCapVehicle() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/vehicle.csv", "cap_vehicle");

      String getVehicleLicensePlate =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name', eq('HELGA BARBOSA')).out('Has_Vehicle')" +
                      ".properties('Object.Vehicle.License_Plate').value().next().toString()").get().toString();
      assertEquals("S987-T098", getVehicleLicensePlate,"Helga's Car's License plate");


      String getVehicleModel =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('JORGE NIAGURA')).out('Has_Vehicle')" +
                      ".properties('Object.Vehicle.Model').value().next().toString()").get().toString();
      assertEquals("ZXR500", getVehicleModel, "Jorge's Vehicle's Model");

      String getEmailAddress =
              App.executor.eval("App.g.V().has('Object.Phone_Number.Last_7_Digits',eq('1657448')).in('Has_Mobile')" +
                      ".out('Uses_Email').properties('Object.Email_Address.Email').value().next().toString()").get().toString();
      assertEquals("jorginho10@icloud.com", getEmailAddress, "Jorge's Email");

      String countPersonNaturalEdges =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('HELGA BARBOSA'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("4", countPersonNaturalEdges, "1 Has_Ingestion_Event + 1 Is_Located + 1 Has_Vehicle + 1 Has_Mobile");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00012SapCapDataQuality() throws InterruptedException {

    try {

      csvTestUtil("sap-cap/data-quality.csv", "cap_data_quality");

      String countPersonNaturalEdges =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('JAMES BONDINHO'))" +
                      ".bothE().count().next().toString()").get().toString();
      assertEquals("8", countPersonNaturalEdges, "2 Has_Id_Card + 1 Is_Located " +
              "+ 1 Has_Ingestion_Event + 1 Works + 1 Uses_Email + 1 Has_Phone + 1 Has_Mobile");

      String onlyLastNameTest =
              App.executor.eval("App.g.V().has('Object.Email_Address.Email',eq('schoemacher_f1@gmail.com'))" +
                      ".in('Uses_Email').properties('Person.Natural.Full_Name').value().next().toString()").get().toString();
      assertEquals("SCHOEMACHER", onlyLastNameTest, "Last Name without white spaces");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }


}
