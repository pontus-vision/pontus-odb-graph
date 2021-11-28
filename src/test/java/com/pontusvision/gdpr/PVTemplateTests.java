package com.pontusvision.gdpr;

import com.pontusvision.gdpr.report.ReportTemplateRenderRequest;
import com.pontusvision.gdpr.report.ReportTemplateRenderResponse;
import com.pontusvision.gdpr.report.ReportTemplateUpsertRequest;
import com.pontusvision.gdpr.report.ReportTemplateUpsertResponse;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(4)
//@RunWith(JUnitPlatform.class)
public class PVTemplateTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  /**
   * @return the suite of test0000s being test0000ed
   */

  @Test
  public void test00001TemplateUpsert() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
          "sharepoint_data_sources");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST");
      req.setTemplatePOLEType("Object.Data_Sources");
      req.setReportTextBase64(
          Base64.getEncoder().encodeToString(" {% set var1=1 %} {{ var1 }} {{ context.Object_Data_Source_Name }}"
              .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Metadata.Type.Object.Data_Source", P.eq("Object.Data_Source"))
          .id().next().toString();

      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);

      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      String expectedStartOfReport = "  1 ";
      assertEquals(expectedStartOfReport, report.substring(0, expectedStartOfReport.length()));

      String dataSourceName = App.g.V(contextId).values("Object.Data_Source.Name").next().toString();

      assertEquals(expectedStartOfReport + dataSourceName, report);


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }


  @Test
//  @SetEnvironmentVariable(key = "ENV_VAR1", value = "VALUE FOR ENV_VAR1")
//  @SetEnvironmentVariable(key = "ENV_VAR3", value = "VALNODEFAULT")
  public void test00002TemplateRisksRender() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields",
          "sharepoint_fontes_de_dados");

      jsonTestUtil("pv-extract-sharepoint-mapeamento-de-processo.json", "$.queryResp[*].fields",
          "sharepoint_mapeamentos");

      jsonTestUtil("pv-extract-sharepoint-risk-mitigations.json", "$.queryResp[*].fields",
          "sharepoint_risk_mitigation");

      jsonTestUtil("pv-extract-sharepoint-risk.json", "$.queryResp[*].fields",
          "sharepoint_risk");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST");
      req.setTemplatePOLEType("Object.Data_Procedures");
      req.setReportTextBase64(
          Base64.getEncoder().encodeToString((
              "{% set risks=pv:getRisksForDataProcess(context.id) %}" +
                  "{% set lawfulBasis=pv:neighboursByType(context.id,'Has_Lawful_Basis_On') %}" +
                  "{% set riskMitigations=pv:getRiskMitigationsForRisk(risks[0].id) %}" +
                  "{{ risks[0].Object_Risk_Data_Source_Risk_Id }}-{{ risks[0].Object_Risk_Data_Source_Risk_Level_Num }}" +
                  "({{ pv:getRiskLevelColour(risks[0].Object_Risk_Data_Source_Risk_Level_Num) }})/" +
                  "({{ pv:getRiskLevelColour(risks[0].blah) }})=" +
                  "{{ context.Object_Data_Procedures_ID }}-{{ riskMitigations[0].Object_Risk_Mitigation_Data_Source_Mitigation_Id }}->" +
                  "ENV_VAR1={{ pv:getEnvVar('ENV_VAR1') }};ENV_VAR2={{ pv:getEnvVarDefVal('ENV_VAR2','DEFVAL2') }};" +
                  "ENV_VAR3={{ pv:getEnvVarDefVal('ENV_VAR3','DEFVAL3') }}###{{ pv:formatDateNow('MMM') }}##" +
                  "{{ pv:formatDateNow('dd')}}##{{ pv:formatDateNow('yyyy') }}##{{ pv:formatDateNow('dd/MM/yyyy') }}##" +
                  "{{ lawfulBasis[0].Object_Lawful_Basis_Description }}{% if 'EXECUÇÃO DE CONTRATO' in lawfulBasis.toString() %}" +
                  "##TEM Execução{% endif %}" +
                  "{% if 'BLAH DE CONTRATO' in lawfulBasis.toString()  %}" +
                  "##TEM BLAH{% endif %}" )

              .getBytes()));

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern ( "MMM" );

      String month =  formatter.format ( LocalDate.now());
      DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern ( "dd" );

      String day =  formatterDay.format ( LocalDate.now());


      DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern ( "yyyy" );

      String year =  formatterYear.format ( LocalDate.now());

      DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern ( "dd/MM/yyyy" );

      String fullDate =  formatterFull.format ( LocalDate.now());


          ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Object.Data_Procedures.ID", P.eq("1"))
          .id().next().toString();

//      MockedStatic<PontusJ2ReportingFunctions> mocked = mockStatic(PontusJ2ReportingFunctions.class);
//
//      mocked.when(() -> PontusJ2ReportingFunctions.getEnv(Mockito.eq("ENV_VAR1"))).thenReturn("VALUE FOR ENV_VAR1");
//      mocked.when(() -> PontusJ2ReportingFunctions.getEnv(Mockito.eq("ENV_VAR3"))).thenReturn("VALNODEFAULT");
//      mocked.when(() -> PontusJ2ReportingFunctions.getEnv(Mockito.anyString())).thenReturn(null);


      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);

      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      String lawfulBasis = "Execução de contrato ou de procedimentos preliminares a contrato, a pedido do titular".
          toUpperCase(Locale.ROOT);
      String expectedReport = "R07-150(red)/(blue)=1-M-PHYS-PROT->ENV_VAR1=;ENV_VAR2=DEFVAL2;ENV_VAR3=DEFVAL3###"
          + month +"##" + day + "##" + year + "##"+ fullDate +  "##"+

          lawfulBasis + "##TEM Execução";
      assertEquals(expectedReport, report);





    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}