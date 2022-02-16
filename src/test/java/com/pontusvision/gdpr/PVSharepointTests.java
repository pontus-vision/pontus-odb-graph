package com.pontusvision.gdpr;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(14)

public class PVSharepointTests extends AppTest {
  /**
   * Create the test0000 case
   *
   */

  @Test
  public void test00001SharepointPAndPDCommitteeMeetings() throws InterruptedException {
    try {

      jsonTestUtil("non-official-pv-extract-sharepoint-p-and-pd-committee-meetings.json",
              "$.queryResp[*].fields", "sharepoint_meetings");

      String topicsDiscussedMeeting1 =
              App.executor.eval("App.g.V().has('Event_Meeting_Form_Id', eq('1'))" +
                      ".properties('Event_Meeting_Discussed_Topics').value()" +
                      ".next().toString()").get().toString();
      assertEquals("LGPD NA EMPRESA; O IMPACTO DA NOVA LEI LGPD", topicsDiscussedMeeting1,
              "Topics discussed at this meeting");

      String eventMetingDate =
              App.executor.eval("App.g.V().has('Object_Data_Source.Name', eq('SHAREPOINT/P-AND-PD-COMMITTEE-MEETINGS'))" +
                      ".out('Has_Ingestion_Event').out('Has_Ingestion_Event').out('Has_Ingestion_Event').has('Event_Meeting_Form_Id', eq('2'))" +
                      ".values('Event_Meeting_Date').next().toString()").get().toString();
      assertEquals(dtfmt.parse("Tue Dec 07 01:01:01 GMT 2021"), dtfmt.parse(eventMetingDate),
              "P&PD Meeting #2 date.");

      String getObjectDataSourceName =
              App.executor.eval("App.g.V().has('Event_Meeting_Form_Id', eq('3')).in('Has_Ingestion_Event')" +
                      ".in('Has_Ingestion_Event').in('Has_Ingestion_Event').values('Object_Data_Source.Name')" +
                      ".next().toString()").get().toString();
      assertEquals("SHAREPOINT/P-AND-PD-COMMITTEE-MEETINGS", getObjectDataSourceName, "Data Source Name.");

      String meeting3Participants =
              App.executor.eval("App.g.V().has('Event_Meeting_Form_Id', eq('3')).values('Event_Meeting_Participants')" +
                      ".next().toString()").get().toString();
      assertEquals("MÔNICA; CEBOLINHA; MAGALI; CASCÃO", meeting3Participants, "The participants of Meeting #3.");

      String meetingSubject =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings')).out('Has_Ingestion_Event')" +
                      ".has('Event_Meeting_Form_Id', eq('2')).values('Event_Meeting_Title').next().toString()").get().toString();
      assertEquals("COMO SALVAR O UNIVERSO?", meetingSubject, "Meeting's #3 Title/Subject.");

    } catch (ExecutionException | ParseException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  // Testing for upsert of two similar json data using sharepoint_meetings POLE
  @Test
  public void test00002UpsertSharepointPAndPDCommitteeMeetings() throws InterruptedException {
    try {

      jsonTestUtil("non-official-pv-extract-sharepoint-p-and-pd-committee-meetings.json",
              "$.queryResp[*].fields", "sharepoint_meetings");

      String countEventIngestions =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings'))" +
                      ".count().next().toString()").get().toString();

      String countEventMeetings =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Event_Meeting', eq('Event_Meeting'))" +
                      ".dedup().count().next().toString()").get().toString();

      jsonTestUtil("non-official-pv-extract-sharepoint-p-and-pd-committee-meetings2.json",
              "$.queryResp[*].fields", "sharepoint_meetings");

      String countEventIngestionsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings'))" +
                      ".count().next().toString()").get().toString();

//      Test for duplicate data

      String countEventMeetingsAgain =
              App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('P & PD Committee Meetings'))" +
                      ".out('Has_Ingestion_Event').has('Metadata_Type_Event_Meeting', eq('Event_Meeting'))" +
                      ".dedup().count().next().toString()").get().toString();

//    This proves that new insertions were made to the P&PD Committee Meetings Graph part
      assertTrue(Integer.parseInt(countEventIngestionsAgain) > Integer.parseInt(countEventIngestions));

//    This proves that data is still the same
      assertTrue(Integer.parseInt(countEventMeetings) == Integer.parseInt(countEventMeetingsAgain));

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

}