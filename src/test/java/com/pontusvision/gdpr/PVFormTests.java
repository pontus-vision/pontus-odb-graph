package com.pontusvision.gdpr;

import com.pontusvision.gdpr.form.FormDataRequest;
import com.pontusvision.gdpr.form.FormDataResponse;
import com.pontusvision.gdpr.form.PVFormData;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.Normalizer;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(31)

public class PVFormTests extends AppTest {
  /**
   * Create the test0000 case
   *
   * @param test0000Name name of the test0000 case
   */

  @Test
  public void test00001FormTests() throws InterruptedException {
    try {
      jsonTestUtil("sharepoint/pv-extract-sharepoint-mapeamento-de-processo.json",
              "$.queryResp[*].fields", "sharepoint_mapeamentos");

      String rid =
              App.executor.eval("App.g.V().has('Object_Data_Policy_Type', eq('6')).in('Has_Policy')" +
                      ".id().next().toString()").get().toString();

      FormDataRequest req = new FormDataRequest();
      PVFormData formData = new PVFormData();
      formData.setName("Object_Data_Procedures_Interested_Parties_Consulted");
      PVFormData[] components = new PVFormData[1];
      components[0] = formData;
      req.setComponents(components);
      req.setDataType("Object_Data_Procedures");
      req.setRid(rid);
      req.setOperation("read");
//      formData.setUserData(new String[1] );
      FormDataResponse resp =  Resource.getFormDataImpl(req);
      assertEquals("Object_Data_Procedures",resp.dataType);
      assertEquals("EPG Advogados",resp.getComponents()[0].getUserData()[0]);

      formData.setName("#Object_Data_Procedures_Interested_Parties_Consulted");
      components[0] = formData;
      components[0].setUserData(null);
      req.setComponents(components);

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


    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);
    }

  }


}