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

//    fixed @ 1667569252000L = 2022-11-04T13:40:52Z [Friday, 4 November 2022 13:40:52] for testing purposes
      String expectedOutput = (String) getDataRetentionKpis(true);

//    metricvalues decreased because nulls where removed !
      assertTrue(expectedOutput.contains("{ \"metricname\": \"6 Months\", \"metricvalue\": 26, \"metrictype\": \"Data Retention\" }"));
      assertTrue(expectedOutput.contains("{ \"metricname\": \"1 Year\", \"metricvalue\": 23, \"metrictype\": \"Data Retention\" }"));
      assertTrue(expectedOutput.contains("{ \"metricname\": \"2 years\", \"metricvalue\": 6, \"metrictype\": \"Data Retention\" }"));
      assertTrue(expectedOutput.contains("{ \"metricname\": \"3 years\", \"metricvalue\": 5, \"metrictype\": \"Data Retention\" }"));
      assertTrue(expectedOutput.contains("{ \"metricname\": \"5 years\", \"metricvalue\": 5, \"metrictype\": \"Data Retention\" }"));
      assertTrue(expectedOutput.contains("{ \"metricname\": \"7 years\", \"metricvalue\": 2, \"metrictype\": \"Data Retention\" }"));

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }


}
