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
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(1000)
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

      jsonTestUtil("sharepoint/pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
              "sharepoint_data_sources");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST");
      req.setTemplatePOLEType("Object_Data_Source");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(" {% set var1=1 %} {{ var1 }} {{ context.Object_Data_Source_Name }}"
                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Metadata_Type_Object_Data_Source", P.eq("Object_Data_Source"))
              .id().next().toString();

      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);

      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      String expectedStartOfReport = "  1 ";
      assertEquals(expectedStartOfReport, report.substring(0, expectedStartOfReport.length()));

      String dataSourceName = App.g.V(contextId).values("Object_Data_Source_Name").next().toString();

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

      jsonTestUtil("sharepoint/pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields",
              "sharepoint_fontes_de_dados");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json", "$.queryResp[*].fields",
              "sharepoint_mapeamentos");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-risk-mitigations.json", "$.queryResp[*].fields",
              "sharepoint_risk_mitigation");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-risk.json", "$.queryResp[*].fields",
              "sharepoint_risk");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST");
      req.setTemplatePOLEType("Object_Data_Procedures");
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

      String contextId = App.g.V().has("Object_Data_Procedures_ID", P.eq("1"))
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

      jsonTestUtil("sharepoint/pv-extract-sharepoint-policies.json", "$.queryResp[*].fields",
              "sharepoint_policies");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Object_Data_Sources");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString("{{ pv:getPolicyText('abc') }}"
                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Metadata_Type_Object_Data_Source", P.eq("Object_Data_Source"))
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

      jsonTestUtil("sharepoint/pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields",
              "sharepoint_fontes_de_dados");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json", "$.queryResp[*].fields",
              "sharepoint_mapeamentos");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-risk-mitigations.json", "$.queryResp[*].fields",
              "sharepoint_risk_mitigation");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-risk.json", "$.queryResp[*].fields",
              "sharepoint_risk");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST1234");
      req.setTemplatePOLEType("Object_Data_Procedures");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "{% set lia= pv:neighboursByType(context.id,'Has_Legitimate_Interests_Assessment' ) %}" +
                              "{% if lia %}" +

                              "{{ lia[0].Object_Legitimate_Interests_Assessment_Is_Essential | default('Favor Preencher o campo <b>Esse tratamento de dados pessoais é indispensável?</b> no SharePoint') }}" +
                              "{% endif %}")

                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Object_Data_Procedures_ID", P.eq("4"))
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

      assertEquals("Sim, é indispensável - processo 4", report);


//    Testing for LIA's Lawful Basis
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "{% set lia= pv:neighboursByType(context.id,'Has_Legitimate_Interests_Assessment' ) %}" +
                              "{% if lia %}" +

                              "{{ lia[0].Object_Legitimate_Interests_Assessment_Lawful_Basis_Justification | default('Favor Preencher o campo <b>Não há outra base legal possível de se utilizar para alcançar o mesmo propósito?</b> no SharePoint') }}" +
                              "{% endif %}")

                      .getBytes()));

      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      contextId = App.g.V().has("Object_Data_Procedures_ID", P.eq("4"))
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

                              "{{ lia[0].Object_Legitimate_Interests_Assessment_Is_Required | default('Favor Preencher o campo <b>Esse processamento de fato auxilia no propósito almejado?</b> no SharePoint') }}" +
                              "{% endif %}")

                      .getBytes()));

      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      contextId = App.g.V().has("Object_Data_Procedures_ID", P.eq("4"))
              .id().next().toString();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);

      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      assertEquals("Sim", report);


      // test a non-existent  LIA for entry number 5 with an empty reply (same template as before)
      contextId = App.g.V().has("Object_Data_Procedures_ID", P.eq("5"))
              .id().next().toString();

      renderReq.setRefEntryId(contextId);
      renderReply = res.reportTemplateRender(renderReq);

      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      assertEquals("", report);


      // test a non-existent  LIA for entry number 5 with a bad template without bounds checks
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "{% set lia= pv:neighboursByType(context.id,'Has_Legitimate_Interests_Assessment' ) %}" +
                              "{{ lia[0].Object_Legitimate_Interests_Assessment_Is_Required | default('Favor Preencher o campo <b>Esse processamento de fato auxilia no propósito almejado?</b> no SharePoint') }}")

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
                              "{{ lia[20].Object_Legitimate_Interests_Assessment_Is_Required | default('Favor Preencher o campo <b>Esse processamento de fato auxilia no propósito almejado?</b> no SharePoint') }}")

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

      jsonTestUtil("sharepoint/pv-extract-sharepoint-policies.json", "$.queryResp[*].fields",
              "sharepoint_policies");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Object_Data_Sources");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString("{{ pv:getPolicyText('INVALID')|default ('BLAH') }}"
                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Metadata_Type_Object_Data_Source", P.eq("Object_Data_Source"))
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

      jsonTestUtil("sharepoint/pv-extract-sharepoint-policies.json", "$.queryResp[*].fields",
              "sharepoint_policies");

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Object_Data_Sources");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(("{{ pv:getPolicyText(null)|default ('BLAH') }}" +
                      "{{ pv:INVALIDFUNCTION() }}")
                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Metadata_Type_Object_Data_Source", P.eq("Object_Data_Source"))
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

      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
          "$.queryResp[*].fields", "sharepoint_mapeamentos");
      jsonTestUtil("sharepoint/pv-extract-sharepoint-ropa.json",
          "$.queryResp[*].fields", "sharepoint_ropa");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar.json", "$.queryResp[*].fields", "sharepoint_dsar");

      jsonTestUtil("budibase/bb-mapeamentos.json", "$.rows", "bb_mapeamento_de_processo");

      jsonTestUtil("budibase/bb-solicitacoes.json", "$.rows", "bb_controle_de_solicitacoes");

//      def dsarId= App.executor.eval("App.g.V().has('Metadata_Type_Event_Subject_Access_Request', eq('Event_Subject_Access_Request')).id().next()").get().toString()
//
//      def lawfulBasis  = "LEGÍTIMO INTERESSE DO CONTROLADOR"

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Event_Subject_Access_Request");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "{% set ropaContrato = pv:getDsarRopaByLawfulBasis(context.id, 'EXECUÇÃO DE CONTRATO OU DE PROCEDIMENTOS PRELIMINARES A CONTRATO, A PEDIDO DO TITULAR') %}" +
                              "{% set ropaLegitimoInteresse = pv:getDsarRopaByLawfulBasis(context.id, 'LEGÍTIMO INTERESSE DO CONTROLADOR') %}" +
                              "{% if (ropaLegitimoInteresse.size() + ropaContrato.size()) > 0 %}" +
                              "{% for ropa in ropaContrato %}" +
                              "{{ ropa.Object_Data_Procedures_Name }}-" +
                              "{{ pv:removeSquareBrackets(ropa.Object_Data_Procedures_Info_Collected) }}" +
                              "\n" +
                              "{% endfor %}" +
                              "{% for ropa in ropaLegitimoInteresse %}" +
                              "{{ ropa.Object_Data_Procedures_Name }}-" +
                              "{{ pv:removeSquareBrackets(ropa.Object_Data_Procedures_Info_Collected) }}" +
                              "\n" +
                              "{% endfor %}" +
                              "{% endif %}" +
                              "")
                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Event_Subject_Access_Request_Form_Id", P.eq("2"))
              .id().next().toString();

      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);

      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      String expectedReport = "123-Nome do Usuário, E-mail Corporativo\n" +
              "CADASTRAMENTO DE CLIENTES PJ-E-mail Corporativo, Nome da Empresa, Telefone, Dados Bancários\n" +
              "GERENCIAMENTO DE PRESTADORES DE SERVIÇOS EXTERNOS-CPF, Nome, RG, Ocupação, E-mail, Endereço\n" +
              "GERENCIAMENTO DO E-MAIL MARKETING - CLIENTES-Endereço, Nome, Telefone, Ocupação, E-mail\n" +
              "GESTÃO DE ACESSO (RECEPÇÃO)-RG, Nome\n" +
              "GESTÃO DE CURRÍCULOS-Endereço, Nome Completo, Data de Nascimento, Estado Civil, E-mail\n" +
              "GESTÃO DE LEADS - SITE-E-mail, Nome Completo, Telefone, Ocupação, Estado Civil\n" +
              "Gestão de Rede de Distribuidores-Nome, CPF, RG, Endereço, E-mail, Ocupação\n" +
              "Gestão de ferramenta gerencial (PowerBI)-Nome, CPF, RG, Endereço, E-mail, Ocupação\n" +
              "HOMOLOGAÇÃO DE FORNECEDORES-Nome, CPF, RG, Telefone, Endereço, E-mail, Responsáveis Legais da Empresa Fornecedora\n";
      assertEquals(sortLines(expectedReport), sortLines(report), "Expecting ROPA to have a Lawful Basis");


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }


  @Test
  public void test00008TemplateDataBreachReport() throws InterruptedException {
    try {

      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
              "sharepoint_data_sources");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-fontes-de-dados.json",
              "$.queryResp[*].fields",
              "sharepoint_fontes_de_dados");

      jsonTestUtil("totvs/totvs-sa1-real.json", "$.objs", "totvs_protheus_sa1_clientes");

      jsonTestUtil("totvs/totvs-sa1.json", "$.objs", "totvs_protheus_sa1_clientes");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-incidentes-de-seguranca-reportados.json",
              "$.queryResp[*].fields", "sharepoint_data_breaches");

//      def dsarId= App.executor.eval("App.g.V().has('Metadata_Type_Event_Subject_Access_Request', eq('Event_Subject_Access_Request')).id().next()").get().toString()
//
//      def lawfulBasis  = "LEGÍTIMO INTERESSE DO CONTROLADOR"

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Event_Data_Breach");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "People: {{ impacted_people | length }}\n" +
                      "Data Sources: {{ impacted_data_sources | length }}\n" +
                      "Servers: {{ impacted_servers | length }}\n"
              )
                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Event_Data_Breach_Form_Id", P.eq("5"))
              .id().next().toString();

      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);

      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      String expectedReport = "People: 4\nData Sources: 2\nServers: 2\n";
      assertEquals(expectedReport, report, "Data Breache's statistics/numbers");

      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(("{{ context.Event_Data_Breach_Description | default('Favor Preencher o campo <b>Descri&ccedil;&atilde;o</b> na Lista <b>Incidentes de Seguran&ccedil;a Reportados</b> no SharePoint') }}")
                      .getBytes()));
      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      contextId = App.g.V().has("Event_Data_Breach_Form_Id", P.eq("5")).id().next().toString();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);
      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      expectedReport = "EXPOSIÇÃO DA OPINIÃO DOS COLABORADORES QUANTO A COMIDA DA CANTINA";
      assertEquals(expectedReport, report, "Data Breache's Description");


      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(("A ANPD (Autoridade Nacional de Proteção de Dados){% if context.Event_Data_Breach_Authority_Notified == 'false' %} NÃO{% endif %} foi notificada desse evento.")
                      .getBytes()));
      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      contextId = App.g.V().has("Event_Data_Breach_Form_Id", P.eq("5")).id().next().toString();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);
      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      expectedReport = "A ANPD (Autoridade Nacional de Proteção de Dados) foi notificada desse evento.";
      assertEquals(expectedReport, report, "Data Breache's Authority Notification Status");


      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(("Titulares dos dados{% if context.Event_Data_Breach_Natural_Person_Notified == 'false' %} NÃO{% endif %} foram notificados desse evento.")
                      .getBytes()));
      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      contextId = App.g.V().has("Event_Data_Breach_Form_Id", P.eq("4")).id().next().toString();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);
      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      expectedReport = "Titulares dos dados NÃO foram notificados desse evento.";
//    Event_Data_Breach id 4 has Natural_Person_Notified = "Não" ... therefore jinjava condition is always false !!
      assertEquals(expectedReport, report, "Data Breache's Natural Person Notification Status");


      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(("{% if impacted_servers[0] is defined %}\n" +
                      "<hr/>\n" +
                      "<h2> Lista de Módulos </h2>\n" +
                      "<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Nome</th></tr>\n" +
                      "    {% for mainkey in impacted_servers %}\n" +
                      "    {{ \"<td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td></tr>\" | format (mainkey['Object_Module_Name'] | default('n/a')) }}\n" +
                      "    {% endfor %}\n" +
                      "</table>\n" +
                      "{% endif %}")
                      .getBytes()));
      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      contextId = App.g.V().has("Event_Data_Breach_Form_Id", P.eq("1")).id().next().toString();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);
      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
//    expectedReport has changed due to an id change @ resources pbr/fontes-de-dados ADP
      expectedReport = "\n" +
        "<hr/>\n" +
        "<h2> Lista de Módulos </h2>\n" +
        "<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Nome</th></tr>\n" +
        "    \n" +
        "    <td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>leads</td></tr>\n" +
        "    \n" +
        "    <td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>lyyyy</td></tr>\n" +
        "    \n" +
        "</table>\n";
      assertEquals(expectedReport, report, "Lista de Módulos' HTML Table");


      req.setReportTextBase64(
              Base64.getEncoder().encodeToString(("{% if impacted_people[0] is defined %}\n" +
                      "<h2> Lista de Titulares impactados </h2>\n" +
                      "<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Nome</th></tr>\n" +
                      "    {% for mainkey in impacted_people %}\n" +
                      "    {{ \"<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td></tr>\" | format (mainkey['Person_Natural_Full_Name'] | default('n/a')) }}\n" +
                      "    {% endfor %}\n" +
                      "</table>\n" +
                      "{% endif %}")
                      .getBytes()));
      reply = res.reportTemplateUpsert(req);
      templateId = reply.getTemplateId();
      contextId = App.g.V().has("Event_Data_Breach_Form_Id", P.eq("5")).id().next().toString();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);
      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
      expectedReport = "\n" +
              "<h2> Lista de Titulares impactados </h2>\n" +
              "<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Nome</th></tr>\n" +
              "    \n" +
              "    <tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>COMIDAS 1</td></tr>\n" +
              "    \n" +
              "    <tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>JONAS LEO BATISTA</td></tr>\n" +
              "    \n" +
              "    <tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>MATHEUS ROCHA</td></tr>\n" +
              "    \n" +
              "    <tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>GLÓRIA KRACKOVSZI</td></tr>\n" +
              "    \n" +
              "</table>\n";
      assertEquals(expectedReport, report, "Lista de Titulares Impactados' HTML Table");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  @Test
  public void test00009BrazilianDate() throws InterruptedException {
    try {

      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");

      jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar.json", "$.queryResp[*].fields", "sharepoint_dsar");

//      Object date = PVConvMixin.asType("Sat Jan 08 08:00:00 UTC 2022", Date.class);
//
//      String brazilianDate = PontusJ2ReportingFunctions.dateLocaleFormat("Sat Jan 08 08:00:00 UTC 2022", "pt", "BR");
//
//      assertSame("class java.util.Date", date.getClass());
//      assertSame("08 de Janeiro de 2022", brazilianDate);

      Resource res = new Resource();

      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
      req.setTemplateName("TEST2");
      req.setTemplatePOLEType("Event_Subject_Access_Request");
      req.setReportTextBase64(
              Base64.getEncoder().encodeToString((
                      "Conforme solicitado na data de {{ pv:dateLocaleFormat(context.Event_Subject_Access_Request_Metadata_Create_Date, 'pt', 'BR') }} " +
                      "pelo canal: ({{ context.Event_Subject_Access_Request_Request_Channel }}), seus dados foram corrigidos " +
                      "na data de {{ pv:dateLocaleFormat(context.Event_Subject_Access_Request_Metadata_Update_Date, 'pt', 'BR') }}.")
                      .getBytes()));

      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);

      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Event_Subject_Access_Request_Form_Id", P.eq("2"))
              .id().next().toString();

      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);

      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      String expectedReport = "Conforme solicitado na data de 9 de Dezembro de 2021 pelo canal: (Via SMS), seus dados foram corrigidos na data de 10 de Dezembro de 2021.";
      assertEquals(expectedReport, report, "CORREÇÃO DOS DADOS message");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

//
//  @Test
//  public void test00010FormatLocaleDateNow() throws InterruptedException {
//    try {
//
//      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
//              "$.queryResp[*].fields", "sharepoint_mapeamentos");
//
//      jsonTestUtil("sharepoint/pv-extract-sharepoint-dsar.json", "$.queryResp[*].fields", "sharepoint_dsar");
//
//      Resource res = new Resource();
//
//      ReportTemplateUpsertRequest req = new ReportTemplateUpsertRequest();
//      req.setTemplateName("TEST2");
//      req.setTemplatePOLEType("Event_Subject_Access_Request");
//      req.setReportTextBase64(
//              Base64.getEncoder().encodeToString((
//                      "Conforme solicitado na data de {{ pv:dateLocaleFormat(context.Event_Subject_Access_Request_Metadata_Create_Date, 'pt', 'BR') }} " +
//                              "pelo canal: ({{ context.Event_Subject_Access_Request_Request_Channel }}), seus dados foram corrigidos " +
//                              "na data de {{ pv:dateLocaleFormat(context.Event_Subject_Access_Request_Metadata_Update_Date, 'pt', 'BR') }}.")
//                      .getBytes()));
//
//      ReportTemplateUpsertResponse reply = res.reportTemplateUpsert(req);
//
//      String templateId = reply.getTemplateId();
//
//      String contextId = App.g.V().has("Event_Subject_Access_Request_Form_Id", P.eq("2"))
//              .id().next().toString();
//
//      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
//      renderReq.setRefEntryId(contextId);
//      renderReq.setTemplateId(templateId);
//      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);
//
//      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));
//
//      String expectedReport = "Conforme solicitado na data de 9 de Dezembro de 2021 pelo canal: (Via SMS), seus dados foram corrigidos na data de 10 de Dezembro de 2021.";
//      assertEquals(expectedReport, report, "CORREÇÃO DOS DADOS message");
//
//    } catch (Exception e) {
//      e.printStackTrace();
//      assertNull(e);
//
//    }
//
//  }

  @Test
  public void test00010CorrespondeTemplate() throws InterruptedException {
    try {

      jsonTestUtil("totvs/totvs-sa1-real.json", "$.objs", "totvs_protheus_sa1_clientes");
      jsonTestUtil("totvs/totvs-sa1-real-2.json", "$.objs", "totvs_protheus_sa1_clientes");




      Resource res = new Resource();


//      String templateId = reply.getTemplateId();

      String contextId = App.g.V().has("Person_Natural_Full_Name", P.eq("MATHEUS ROCHA"))
          .id().next().toString();

      String templateId = "Person_Natural/Corresponde";
//          App.g.V().has("Object_Notification_Templates_Id", P.eq("Person_Natural/Corresponde"))
//          .id().next().toString();

      ReportTemplateRenderRequest renderReq = new ReportTemplateRenderRequest();
      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      ReportTemplateRenderResponse renderReply = res.reportTemplateRender(renderReq);

      String report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      String expectedReport = "MATHEUS ROCHA é o único registro no sistema";
      assertTrue(report.contains(expectedReport), "Report orig = " + report);

      contextId = App.g.V().has("Person_Natural_Full_Name", P.eq("KRACKOVSZI GLÓRIA 2"))
          .id().next().toString();

//          App.g.V().has("Object_Notification_Templates_Id", P.eq("Person_Natural/Corresponde"))
//          .id().next().toString();

      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);

      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      expectedReport = "KRACKOVSZI GLÓRIA 2 Corresponde potencialmente a 1 registro";
      assertTrue(report.contains(expectedReport), "Contains KRACKOVSZI GLÓRIA 2; orig report = " + report);

      expectedReport = "Location Address, Object Email Address, Object Phone Number";
      assertTrue(report.contains(expectedReport),
          "Contains Location Address, Object Email Address, Object Phone Number Report orig report = " + report);


      contextId = App.g.V().has("Person_Natural_Full_Name", P.eq("GLÓRIA KRACKOVSZI"))
          .id().next().toString();

//          App.g.V().has("Object_Notification_Templates_Id", P.eq("Person_Natural/Corresponde"))
//          .id().next().toString();

      renderReq.setRefEntryId(contextId);
      renderReq.setTemplateId(templateId);
      renderReply = res.reportTemplateRender(renderReq);

      report = new String(Base64.getDecoder().decode(renderReply.getBase64Report().getBytes()));

      expectedReport = "GLÓRIA KRACKOVSZI Corresponde potencialmente a 1 registro";
      assertTrue(report.contains(expectedReport), "Contains GLÓRIA KRACKOVSZI; orig report = " + report);

      expectedReport = "Location Address, Object Email Address, Object Phone Number";
      assertTrue(report.contains(expectedReport),
          "Contains Location Address, Object Email Address, Object Phone Number Report orig report = " + report);




    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

}
