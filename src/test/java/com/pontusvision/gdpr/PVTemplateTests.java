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
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.pontusvision.graphutils.PontusJ2ReportingFunctions;

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
      req.setTemplatePOLEType("Object.Data_Source");
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
                              "##TEM BLAH{% endif %}")

                      .getBytes()));

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");

      String month = formatter.format(LocalDate.now());
      DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd");

      String day = formatterDay.format(LocalDate.now());


      DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");

      String year = formatterYear.format(LocalDate.now());

      DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      String fullDate = formatterFull.format(LocalDate.now());


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
      String expectedReport = "R02-150(red)/(blue)=1-M-DATA-ENCR-FLIGHT->ENV_VAR1=;ENV_VAR2=DEFVAL2;ENV_VAR3=DEFVAL3###"
//      String expectedReport = "R07-150(red)/(blue)=1-M-PHYS-PROT->ENV_VAR1=;ENV_VAR2=DEFVAL2;ENV_VAR3=DEFVAL3###"
              + month + "##" + day + "##" + year + "##" + fullDate + "##" +

              lawfulBasis + "##TEM Execução";
      assertEquals(expectedReport, report);


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00003TemplateGetPoliciesText() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-policies.json", "$.queryResp[*].fields",
              "sharepoint_policies");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Object.Data_Sources");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString("{{ pv:getPolicyText('abc') }}"
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

      String expectedReport = "Test ABC abc AbC";
      assertEquals(expectedReport, report, "Test the abc policy text");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
//  @SetEnvironmentVariable(key = "ENV_VAR1", value = "VALUE FOR ENV_VAR1")
//  @SetEnvironmentVariable(key = "ENV_VAR3", value = "VALNODEFAULT")
  public void test00004TemplateLiaRender() throws InterruptedException {
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
      req.setTemplateName("TEST1234");
      req.setTemplatePOLEType("Object.Data_Procedures");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "{% set lia= pv:neighboursByType(context.id,'Has_Legitimate_Interests_Assessment' ) %}" +
                              "{% if lia %}" +

                              "{{ lia[0].Object_Legitimate_Interests_Assessment_Personal_Data_Treatment | " +
                              "default('Favor Preencher o campo <b>Esse tratamento de dados pessoais é indispensável?</b> " +
                              "no SharePoint') }}" +
                              "{% endif %}")

                      .getBytes()));

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

      assertEquals("Sim, é indispensável - processo 1", report);


//    Testing for LIA's Lawful Basis
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "{% set lia= pv:neighboursByType(context.id,'Has_Legitimate_Interests_Assessment' ) %}" +
                              "{% if lia %}" +

                              "{{ lia[0].Object_Legitimate_Interests_Assessment_Lawful_Basis_Justification | " +
                              "default('Favor Preencher o campo <b>Não há outra base legal possível de se utilizar " +
                              "para alcançar o mesmo propósito?</b> no SharePoint') }}" +
                              "{% endif %}")

                      .getBytes()));

      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      contextId = App.g.V().has("Object.Data_Procedures.ID", P.eq("6"))
              .id().next().toString();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);

      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      assertEquals("Consentimento", report);


//    Testing for LIA's Processing Purpose
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "{% set lia= pv:neighboursByType(context.id,'Has_Legitimate_Interests_Assessment' ) %}" +
                              "{% if lia %}" +

                              "{{ lia[0].Object_Legitimate_Interests_Assessment_Processing_Purpose | " +
                              "default('Favor Preencher o campo <b>Esse processamento de fato auxilia no propósito " +
                              "almejado?</b> no SharePoint') }}" +
                              "{% endif %}")

                      .getBytes()));

      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      contextId = App.g.V().has("Object.Data_Procedures.ID", P.eq("4"))
              .id().next().toString();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);

      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      assertEquals("Sim", report);


      // test a non-existent  LIA for entry number 5 with an empty reply (same template as before)
      contextId = App.g.V().has("Object.Data_Procedures.ID", P.eq("5"))
              .id().next().toString();

      renderReq.setRefEntryId(contextId);
      renderReply = res.reportTemplateRender(renderReq);

      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      assertEquals("", report);


      // test a non-existent  LIA for entry number 5 with a bad template without bounds checks
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "{% set lia= pv:neighboursByType(context.id,'Has_Legitimate_Interests_Assessment' ) %}" +
                              "{{ lia[0].Object_Legitimate_Interests_Assessment_Processing_Purpose | " +
                              "default('Favor Preencher o campo <b>Esse processamento de fato auxilia no propósito " +
                              "almejado?</b> no SharePoint') }}")

                      .getBytes()));

      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      renderReq.setTemplateId(templateId);
      renderReq.setRefEntryId(contextId);
      renderReply = res.reportTemplateRender(renderReq);

      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      assertEquals("Favor Preencher o campo <b>Esse processamento de fato auxilia no propósito " +
              "almejado?</b> no SharePoint", report);


      // try an out of bounds array
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "{% set lia= pv:neighboursByType(context.id,'Has_Legitimate_Interests_Assessment' ) %}" +
                              "{{ lia[20].Object_Legitimate_Interests_Assessment_Processing_Purpose | " +
                              "default('Favor Preencher o campo <b>Esse processamento de fato auxilia no propósito " +
                              "almejado?</b> no SharePoint') }}")

                      .getBytes()));

      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      renderReq.setTemplateId(templateId);
      renderReq.setRefEntryId(contextId);
      renderReply = res.reportTemplateRender(renderReq);

      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      assertEquals("Favor Preencher o campo <b>Esse processamento de fato auxilia no propósito " +
              "almejado?</b> no SharePoint", report);


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00005TemplateGetPoliciesTextInvalid() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-policies.json", "$.queryResp[*].fields",
              "sharepoint_policies");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Object.Data_Sources");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString("{{ pv:getPolicyText('INVALID')|default ('BLAH') }}"
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

      String expectedReport = "BLAH";
      assertEquals(expectedReport, report, "Ensure that an invalid test still works");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00006TemplateGetPoliciesTextInvalidTemplate() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-policies.json", "$.queryResp[*].fields",
              "sharepoint_policies");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Object.Data_Sources");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(("{{ pv:getPolicyText(null)|default ('BLAH') }}" +
                      "{{ pv:INVALIDFUNCTION() }}")
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

      String expectedReport = "Error resolving template:  Could not resolve function 'pv:INVALIDFUNCTION'";
      assertEquals(expectedReport, report, "Ensure that an invalid template shows an error");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00007TemplateGetDsarRopaByLawfulBasis() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");

      jsonTestUtil("pv-extract-sharepoint-dsar.json", "$.queryResp[*].fields", "sharepoint_dsar");

//      def dsarId= App.executor.eval("App.g.V().has('Metadata.Type.Event.Subject_Access_Request', eq('Event.Subject_Access_Request')).id().next()").get().toString()
//
//      def lawfulBasis  = "LEGÍTIMO INTERESSE DO CONTROLADOR"

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Event.Subject_Access_Request");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(("{% set ropaLegitimoInteresse=" +
                      "pv:getDsarRopaByLawfulBasis(context.id, 'EXECUÇÃO DE CONTRATO OU DE PROCEDIMENTOS PRELIMINARES A CONTRATO, A PEDIDO DO TITULAR') %}" +
                      "{% for ropa in ropaLegitimoInteresse %}" +
                      "{{ ropa.Object_Data_Procedures_Name }}-" +
                      "{{ pv:removeSquareBrackets(ropa.Object_Data_Procedures_Info_Collected) }}" +
                      "{% endfor %}")
                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Event.Subject_Access_Request.Form_Id", P.eq("2"))
              .id().next().toString();

      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);

      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      String expectedReport = "Gestão de Rede de Distribuidores-Nome, CPF, RG, Endereço, E-mail, Ocupação";
      assertEquals(expectedReport, report, "Expecting ROPA to have a Lawful Basis");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00008SimpleJsonToHtmlTable() throws InterruptedException {
    try {

//      jsonTestUtil("pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields",
//              "sharepoint_fontes_de_dados");

      String jsonString = "{\"Nome do Processo\": \"nomeDoProcesso\", \"Descrição\": \"descricao\", \"Dados Coletados\": \"dadosColetados\", \"Dados Sensíveis\": \"dadosSensiveis\"}";
      String htmlTable = PontusJ2ReportingFunctions.jsonToHtmlTable(jsonString);
      assertEquals("<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;" +
              "padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Name</th>" +
              "<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Value</th></tr>" +
              "<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;" +
              "text-align: left;padding: 8px;'>Nome do Processo</td><td style='border: 1px solid #dddddd;" +
              "text-align: left;padding: 8px;'>nomeDoProcesso</td></tr>\n" +
              "<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;" +
              "text-align: left;padding: 8px;'>Descrição</td><td style='border: 1px solid #dddddd;text-align: left;" +
              "padding: 8px;'>descricao</td></tr>\n" +
              "<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;" +
              "text-align: left;padding: 8px;'>Dados Coletados</td><td style='border: 1px solid #dddddd;text-align: left;" +
              "padding: 8px;'>dadosColetados</td></tr>\n" +
              "<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;" +
              "text-align: left;padding: 8px;'>Dados Sensíveis</td><td style='border: 1px solid #dddddd;text-align: left;" +
              "padding: 8px;'>dadosSensiveis</td></tr>\n" +
              "</table>", htmlTable, "html table from json string");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }

  @Test
  public void test00009TemplateJsonToHtmlTable() throws InterruptedException {
    try {

      jsonTestUtil("pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields",
              "sharepoint_fontes_de_dados");

      jsonTestUtil("pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");

      jsonTestUtil("pv-extract-sharepoint-dsar.json", "$.queryResp[*].fields", "sharepoint_dsar");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Event.Subject_Access_Request");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(("{% set ropaLegitimoInteresse=" +
                      "pv:getDsarRopaByLawfulBasis(context.id, 'EXECUÇÃO DE CONTRATO OU DE PROCEDIMENTOS PRELIMINARES A CONTRATO, A PEDIDO DO TITULAR') %}" +
                      "{% for ropa in ropaLegitimoInteresse %}" +
                      "{% set nomeDoProcesso=ropa.Object_Data_Procedures_Name %}" +
                      "{{ pv:removeSquareBrackets(ropa.Object_Data_Procedures_Name) }}" +
                      "{% set descricao=ropa.Object_Data_Procedures_Description %}" +
                      "{{ pv:removeSquareBrackets(ropa.Object_Data_Procedures_Description) }}" +
                      "{% set dadosColetados=ropa.Object_Data_Procedures_Info_Collected %}" +
                      "{{ pv:removeSquareBrackets(ropa.Object_Data_Procedures_Info_Collected) }}" +
                      "{% set htmlTable=pv:jsonToHtmlTable('{\"Nome do Processo\": nomeDoProcesso, \"Descrição\": descricao, \"Dados Coletados\": dadosColetados}') %}" +
                      "{{ htmlTable }}" +
                      "{% endfor %}").getBytes()));

//      String = "{\"Nome do Processo\": " + {{ nomeDoProcesso }} + ", \"Descrição\": " + {{ descricao }} + ", \"Dados Coletados\": " + {{ dadosColetados }} + "}";

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Event.Subject_Access_Request.Form_Id", P.eq("2"))
              .id().next().toString();

      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);

      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      String expectedReport = "Gestão de Rede de Distribuidores-Nome, CPF, RG, Endereço, E-mail, Ocupação";
      assertEquals(expectedReport, report, "Expecting ROPA to have a Lawful Basis");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

}