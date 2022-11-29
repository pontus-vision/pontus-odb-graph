package com.pontusvision.gdpr;

import com.pontusvision.gdpr.form.FormDataRequest;
import com.pontusvision.gdpr.form.FormDataResponse;
import com.pontusvision.gdpr.form.PVFormData;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(2033)

public class PVFormTests extends AppTest {
  /**
   * Create the test0000 case
   */

  @Test
  public void test00001FormTests() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");

//      String rid =
//              App.executor.eval("App.g.V().has('Object_Data_Policy_Type', eq('4')).in('Has_Policy')" +
//                      ".id().next().toString()").get().toString();

//    new AGgrid test style ------------------------------------------------------------------------------------------------------------
      String dataPolicyRid = gridWrapperGetRid("[\n" +
          "  {\n" +
          "    \"colId\": \"Object_Data_Policy_Type\",\n" +
          "    \"filterType\": \"text\",\n" +
          "    \"type\": \"equals\",\n" +
          "    \"filter\": \"4\"\n" +
          "  }\n" +
          "]", "Object_Data_Policy",
        new String[]{"Object_Data_Policy_Type"});

      String rid = gridWrapperGetRid(null, "Object_Data_Procedures", new String[]{"Object_Data_Procedures_ID"}, "hasNeighbourId:" + dataPolicyRid);

// -------------------------------------------------------------------------------------------------------------------------------------

      FormDataRequest req = new FormDataRequest();
      int idx = -1;
      PVFormData[] components = new PVFormData[7];
      components[++idx] = new PVFormData().setName("Object_Data_Procedures_Interested_Parties_Consulted");
      components[++idx] = new PVFormData().setName(">out_Has_Lawful_Basis_On");
      components[++idx] = new PVFormData().setName(">out_Has_Sensitive_Data");
      components[++idx] = new PVFormData().setName("<in_Has_Ingestion_Event");
      components[++idx] = new PVFormData().setName(">out_Has_Legitimate_Interests_Assessment");
      components[++idx] = new PVFormData().setName(">out_Has_Policy");
      components[++idx] = new PVFormData().setName(">out_Has_Data_Source");
//      components[++idx] = new PVFormData().setName("");
//      components[++idx] = new PVFormData().setName("");
//      components[++idx] = new PVFormData().setName("");


      req.setComponents(components);
      req.setDataType("Object_Data_Procedures");
      req.setRid(rid);
      req.setOperation("read");
//      formData.setUserData(new String[1] );
      FormDataResponse resp =  Resource.getFormDataImpl(req);

      FormDataResponse orig = resp.clone();

      assertEquals("Object_Data_Procedures",resp.dataType);
      assertEquals("EPG Advogados",resp.getComponents()[0].getUserData()[0]);

      req.getComponents()[0].setName("#Object_Data_Procedures_Interested_Parties_Consulted").setUserData(null);
      resp =  Resource.getFormDataImpl(req);
      assertEquals("Object_Data_Procedures",resp.dataType);
      assertEquals("EPG Advogados",resp.getComponents()[0].getUserData()[0]);


      req.getComponents()[0].setUserData(new String[]{"EPG Advogados2"});
      req.setOperation("update");
      resp =  Resource.getFormDataImpl(req);
      assertEquals("Object_Data_Procedures",resp.dataType);


      req.getComponents()[0].setUserData(null);
      req.setOperation("read");
      resp =  Resource.getFormDataImpl(req);
      assertEquals("Object_Data_Procedures",resp.dataType);
      assertEquals("EPG Advogados2",resp.getComponents()[0].getUserData()[0]);


      req.getComponents()[0].setUserData(new String[]{"EPG Advogados"});
      req.setOperation("update");
      resp =  Resource.getFormDataImpl(req);
      assertEquals("Object_Data_Procedures",resp.dataType);

      req.getComponents()[0].setUserData(null);
      req.setOperation("read");
      resp =  Resource.getFormDataImpl(req);
      assertEquals("Object_Data_Procedures",resp.dataType);
      assertEquals("EPG Advogados",resp.getComponents()[0].getUserData()[0]);



      req.getComponents()[0].setUserData(null);
      req.setOperation("delete");
      Resource.getFormDataImpl(req);


      req.getComponents()[0].setUserData(null);
      req.setOperation("read");
      resp =  Resource.getFormDataImpl(req);
      assertEquals("Object_Data_Procedures",resp.dataType);
      assertEquals(null,resp.getComponents()[0].getUserData());

//      assertEquals("Object_Data_Procedures",resp.dataType);
//      assertEquals("EPG Advogados",resp.getComponents()[0].getUserData()[0]);

      /*
            "Object_Data_Procedures_Business_Area_Responsible" -> "Compras - 19"
        "out_Has_Policy" -> {ORidBag@14463} "[#1102:0]"
        "out_Has_Data_Source" -> {ORidBag@14465} "[#1122:0, #1123:0]"
        "Object_Data_Procedures_Type" -> "Recebimento / Transferência"
        "Object_Data_Procedures_Form_Id" -> "406"
        "Object_Data_Procedures_Why_Is_It_Collected" -> "Necessário para contratação de prestadores de serviços e/ou fornecedores pessoas físicas"
        "Object_Data_Procedures_Country_Where_Stored" -> "Bxxxx"
       */
      PVFormData[] components2 = new PVFormData[20];
      idx = -1;
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Interested_Parties_Consulted").setUserData(new String[]{"EPG Advogados"});
      components2[++idx] = new PVFormData().setName(">out_Has_Lawful_Basis_On").setUserData(orig.getComponents()[idx].getUserData());
      components2[++idx] = new PVFormData().setName(">out_Has_Sensitive_Data").setUserData(orig.getComponents()[idx].getUserData());
      components2[++idx] = new PVFormData().setName("<in_Has_Ingestion_Event").setUserData(orig.getComponents()[idx].getUserData());
      components2[++idx] = new PVFormData().setName(">out_Has_Legitimate_Interests_Assessment").setUserData(orig.getComponents()[idx].getUserData());
      components2[++idx] = new PVFormData().setName(">out_Has_Policy").setUserData(orig.getComponents()[idx].getUserData());
      components2[++idx] = new PVFormData().setName(">out_Has_Data_Source").setUserData(orig.getComponents()[idx].getUserData());
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Business_Area_Responsible").setUserData(new String[]{"Compras - 19"});
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Type").setUserData(new String[]{"Recebimento / Transferência"});
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Type").setUserData(new String[]{"Recebimento / Transferência"});
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Form_Id").setUserData(new String[]{"406"});
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Why_Is_It_Collected").setUserData(new String[]{"Necessário para contratação de prestadores de serviços e/ou fornecedores pessoas físicas"});
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Country_Where_Stored").setUserData(new String[]{"Bxxxx"});

      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_ID").setUserData(new String[]{"4"});
//      components2[++idx] = new PVFormData().setName("Metadata_Type_Object_Data_Procedures").setUserData(new String[]{"Object_Data_Procedures"});
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Name").setUserData(new String[]{"Prestadores de Serviços e Fornecedores"});
//      components2[++idx] = new PVFormData().setName("Metadata_Type").setUserData(new String[]{"Object_Data_Procedures"});
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Description").setUserData(new String[]{"Recebimento de dados via e-mail (exemplo: dados de motoristas das transportadoras) e informação repassada à fornecedores e/ou clientes onde será feita a coleta/entrega das mercadorias."});
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Type_Of_Natural_Person").setUserData(new String[]{"TXXXXXXX"});
      components2[++idx] = new PVFormData().setName("Object_Data_Procedures_Info_Collected").setUserData(new String[]{"[Nome, Data de Nascimento, Local de Nascimento, RG, CPF, Endereço, Telefone, E-mail, Dados Bancários, Ocupação]"});
      req.setComponents(components2);
      req.setOperation("create");
      resp =  Resource.getFormDataImpl(req);

      assertEquals(orig.getComponents()[0].getUserData()[0],resp.getComponents()[0].getUserData()[0]);
      assertEquals(orig.getComponents()[1].getUserData()[0],resp.getComponents()[1].getUserData()[0]);

      FormDataRequest req2 = new FormDataRequest();
      req2.setComponents(components);
      req2.setOperation("read");
      req2.setRid(resp.getRid());
      req2.setDataType("Object_Data_Procedures");

      resp =  Resource.getFormDataImpl(req2);

      assertEquals("Object_Data_Procedures",resp.dataType);
      assertEquals("EPG Advogados",resp.getComponents()[0].getUserData()[0]);


    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);
    }

  }


}