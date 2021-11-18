package com.pontusvision.gdpr;

import com.pontusvision.ingestion.Ingestion;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
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

//    test0000 for PEDRO Person.Employee NODE
      String numRiskMitigations =
          App.executor.eval("App.g.V().has('Metadata.Type.Object.Risk_Mitigation_Data_Source', eq('Object.Risk_Mitigation_Data_Source')).count()\n" +
              ".next().toString()").get().toString();

      assertEquals("9",numRiskMitigations, "9 Risk Mitigations");

      String mSystmPasswd =
          App.executor.eval("App.g.V().has('Object.Risk_Mitigation_Data_Source.Mitigation_Id', eq('M-SYSTM-PASSWD')).count()\n" +
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

//    test0000 for PEDRO Person.Employee NODE
      String numRiskMitigations =
          App.executor.eval("App.g.V().has('Metadata.Type.Object.Risk_Data_Source', eq('Object.Risk_Data_Source')).count()\n" +
              ".next().toString()").get().toString();

      assertEquals("14",numRiskMitigations, "14 Risks");

      String numDataSourcesR02 =
          App.executor.eval("App.g.V().has('Object.Risk_Data_Source.Risk_Id', eq('R02')).in('Has_Risk')\n" +
              ".count().next().toString()").get().toString();


      assertEquals("5",numDataSourcesR02, "5 Data sources associated with R02");

      String descriptionR02 =
          App.executor.eval("App.g.V().has('Object.Risk_Data_Source.Risk_Id', eq('R02'))" +
              ".values('Object.Risk_Data_Source.Description')\n" +
              ".next().toString()").get().toString();


      assertEquals("Modificação não autorizada.",descriptionR02, "R02 description is correct");


      String numMitigationsR02 =
          App.executor.eval("App.g.V().has('Object.Risk_Data_Source.Risk_Id', eq('R02')).in('Mitigates_Risk')\n" +
              ".count().next().toString()").get().toString();


      assertEquals("1",numMitigationsR02, "1 Risk Mitigation associated with R02");

      String mitigationsR02 =
          App.executor.eval("App.g.V().has('Object.Risk_Data_Source.Risk_Id', eq('R02')).in('Mitigates_Risk')\n" +
              ".values('Object.Risk_Mitigation_Data_Source.Mitigation_Id').next().toString()").get().toString();


      assertEquals("M-DATA-ENCR-FLIGHT",mitigationsR02, "1 Risk Mitigation associated with R02");

      String mitigationsR02Description =
          App.executor.eval("App.g.V().has('Object.Risk_Data_Source.Risk_Id', eq('R02')).in('Mitigates_Risk')\n" +
              ".values('Object.Risk_Mitigation_Data_Source.Description').next().toString()").get().toString();

      assertEquals("CONTROLES CRIPTOGRÁFICOS QUANDO OS DADOS ESTÃO NA REDE",mitigationsR02Description, "Risk Mitigation description with R02");


      String mitigationsR03Count =
          App.executor.eval("App.g.V().has('Object.Risk_Data_Source.Risk_Id', eq('R03')).in('Mitigates_Risk')\n" +
              ".count().next().toString()").get().toString();

      assertEquals("0",mitigationsR03Count, "0 Risk Mitigations to R03");




    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}
