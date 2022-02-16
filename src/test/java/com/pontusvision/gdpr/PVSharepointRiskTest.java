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
@TestClassesOrder(3)
//@RunWith(JUnitPlatform.class)
public class PVSharepointRiskTest extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001RiskMitigation() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-risk-mitigations.json", "$.queryResp[*].fields",
          "sharepoint_risk_mitigation");

//    test0000 for PEDRO Person_Employee NODE
      String numRiskMitigations =
          App.executor.eval("App.g.V().has('Metadata_Type_Object_Risk_Mitigation_Data_Source', eq('Object_Risk_Mitigation_Data_Source')).count()\n" +
              ".next().toString()").get().toString();

      assertEquals("9",numRiskMitigations, "9 Risk Mitigations");

      String mSystmPasswd =
          App.executor.eval("App.g.V().has('Object_Risk_Mitigation_Data_Source_Mitigation_Id', eq('M-SYSTM-PASSWD')).count()\n" +
              ".next().toString()").get().toString();


      assertEquals("1",mSystmPasswd, "1 Risk Mitigation m-systm-passwd");


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00002Risk() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-risk.json", "$.queryResp[*].fields",
          "sharepoint_risk");

//    test0000 for PEDRO Person_Employee NODE
      String numRiskMitigations =
          App.executor.eval("App.g.V().has('Metadata_Type_Object_Risk_Data_Source', eq('Object_Risk_Data_Source')).count()\n" +
              ".next().toString()").get().toString();

      assertEquals("14",numRiskMitigations, "14 Risks");

      String numDataSourcesR02 =
          App.executor.eval("App.g.V().has('Object_Risk_Data_Source_Risk_Id', eq('R02')).in('Has_Risk')\n" +
              ".count().next().toString()").get().toString();


      assertEquals("2",numDataSourcesR02, "2 Data sources associated with R02");

      String descriptionR02 =
          App.executor.eval("App.g.V().has('Object_Risk_Data_Source_Risk_Id', eq('R02'))" +
              ".values('Object_Risk_Data_Source_Description')\n" +
              ".next().toString()").get().toString();


      assertEquals("Modificação não autorizada.",descriptionR02, "R02 description is correct");


      String numMitigationsR02 =
          App.executor.eval("App.g.V().has('Object_Risk_Data_Source_Risk_Id', eq('R02')).in('Mitigates_Risk')\n" +
              ".count().next().toString()").get().toString();


      assertEquals("1",numMitigationsR02, "1 Risk Mitigation associated with R02");


      String mitigationsR03Count =
          App.executor.eval("App.g.V().has('Object_Risk_Data_Source_Risk_Id', eq('R03')).in('Mitigates_Risk')\n" +
              ".count().next().toString()").get().toString();

      assertEquals("0",mitigationsR03Count, "0 Risk Mitigations for R03");


      String approvedBySecurity =
              App.executor.eval("App.g.V().has('Object_Risk_Data_Source_Risk_Id', eq('R05'))" +
                      ".values('Object_Risk_Data_Source_Approved_By_Security').next().toString()").get().toString();

      assertEquals("false",approvedBySecurity, "bool status for Approved_By_Security");


      String approvedByDPO =
              App.executor.eval("App.g.V().has('Object_Risk_Data_Source_Risk_Id', eq('R11'))" +
                      ".values('Object_Risk_Data_Source_Approved_By_DPO').next().toString()").get().toString();

      assertEquals("true",approvedByDPO, "bool status for Approved_By_DPO");

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
