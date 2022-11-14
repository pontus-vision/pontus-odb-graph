package com.pontusvision.gdpr.sv;

import com.pontusvision.gdpr.AnnotationTestsOrderer;
import com.pontusvision.gdpr.AppTest;
import com.pontusvision.gdpr.TestClassesOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static com.pontusvision.graphutils.gdpr.getDataRetentionKpis;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(34)

public class PVKpiTests extends AppTest {

  @Test
  public void test00001DataRetentionKpis() throws InterruptedException {

//    ingesting data_sources, ropas and people
    jsonTestUtil("sharepoint/pv-extract-sharepoint-fontes-de-dados.json", "$.queryResp[*].fields",
            "sharepoint_fontes_de_dados");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-data-sources.json", "$.queryResp[*].fields",
      "sharepoint_data_sources");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
            "$.queryResp[*].fields", "sharepoint_mapeamentos");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo2.json",
      "$.queryResp[*].fields", "sharepoint_mapeamentos");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo3.json",
      "$.queryResp[*].fields", "sharepoint_mapeamentos");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-ropa.json", "$.queryResp[*].fields",
      "sharepoint_ropa");
//    total count Person_Natural ingested = 24
    jsonTestUtil("totvs/totvs-sa1.json", "$.objs", "totvs_protheus_sa1_clientes");
    jsonTestUtil("totvs/totvs-sa1-real.json", "$.objs", "totvs_protheus_sa1_clientes");
    jsonTestUtil("totvs/totvs-sa1-real-2.json", "$.objs", "totvs_protheus_sa1_clientes");
    jsonTestUtil("totvs/totvs-sa2-real.json", "$.objs", "totvs_protheus_sa2_fornecedor");
    jsonTestUtil("totvs/totvs-sra-real.json", "$.objs", "totvs_protheus_sra_funcionario");
    jsonTestUtil("totvs/totvs-srb-real.json", "$.objs", "totvs_protheus_srb_dependente");
    jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos.json",
      "$.queryResp[*].fields", "sharepoint_consents");
    jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos2.json",
      "$.queryResp[*].fields", "sharepoint_consents");
    jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos3.json",
      "$.queryResp[*].fields", "sharepoint_consents");
    jsonTestUtil("ploomes/ploomes1.json", "$.value", "ploomes_clientes");
    jsonTestUtil("ploomes/ploomes1-merge-totvs1-real.json", "$.value", "ploomes_clientes");
    jsonTestUtil("office365/pv-extract-o365-email.json", "$.value", "pv_email", "OFFICE365/EMAIL");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-treinamentos.json", "$.queryResp[*].fields",
      "sharepoint_treinamentos");
    jsonTestUtil("sharepoint/pv-extract-sharepoint-treinamentos2.json", "$.queryResp[*].fields",
      "sharepoint_treinamentos");

    try {

      String output = (String) getDataRetentionKpis();

      assertTrue(output.contains("{ \"metricname\": \"6 Months\", \"metricvalue\": 30, \"metrictype\": \"Data Retention\" }"));
      assertTrue(output.contains("{ \"metricname\": \"1 Year\", \"metricvalue\": 25, \"metrictype\": \"Data Retention\" }"));
      assertTrue(output.contains("{ \"metricname\": \"2 years\", \"metricvalue\": 8, \"metrictype\": \"Data Retention\" }"));
      assertTrue(output.contains("{ \"metricname\": \"3 years\", \"metricvalue\": 7, \"metrictype\": \"Data Retention\" }"));
      assertTrue(output.contains("{ \"metricname\": \"5 years\", \"metricvalue\": 7, \"metrictype\": \"Data Retention\" }"));
      assertTrue(output.contains("{ \"metricname\": \"7 years\", \"metricvalue\": 3, \"metrictype\": \"Data Retention\" }"));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }


}