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
    jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
            "$.queryResp[*].fields", "sharepoint_mapeamentos");
//    total count Person_Natural ingested = 24
    jsonTestUtil("totvs/totvs-sa1.json", "$.objs", "totvs_protheus_sa1_clientes");
    jsonTestUtil("totvs/totvs-sa2-real.json", "$.objs", "totvs_protheus_sa2_fornecedor");
    jsonTestUtil("totvs/totvs-sra-real.json", "$.objs", "totvs_protheus_sra_funcionario");
    jsonTestUtil("sharepoint/non-official-pv-extract-sharepoint-consentimentos.json",
      "$.queryResp[*].fields", "sharepoint_consents");
    jsonTestUtil("ploomes/ploomes1.json", "$.value", "ploomes_clientes");

    try {

      String output = (String) getDataRetentionKpis();

      assertTrue(output.contains("{ \"metricname\": \"6 Months\", \"metricvalue\": 17, \"metrictype\": \"Data Retention\" }"));
      assertTrue(output.contains("{ \"metricname\": \"1 Year\", \"metricvalue\": 17, \"metrictype\": \"Data Retention\" }"));
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