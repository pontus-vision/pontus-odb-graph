package com.pontusvision.gdpr;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.OVertexDocument;
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static com.pontusvision.graphutils.Utils.mergeVertices;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;

/**
 * Unit test0000 for simple App.
 */

@TestClassOrder(AnnotationTestsOrderer.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassesOrder(35)
//@RunWith(JUnitPlatform.class)
public class PVUtilsTest extends AppTest {

  @Test
  public void test00001MergeVertices() throws InterruptedException {

    jsonTestUtil("utils/merge-vertices.json", "$.value", "merge_vertices");

    try {

      String countPersonOrganisation =
        App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('merge vertices')).out('Has_Ingestion_Event')" +
          ".has('Metadata_Type_Person_Organisation',eq('Person_Organisation')).count().next().toString()").get().toString();

      String countIdentityEdges =
        App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('merge vertices'))" +
          ".out('Has_Ingestion_Event').has('Person_Organisation_Name',TextP.endingWith('[merge vertex]'))" +
          ".out('Has_Id_Card').count().next().toString()").get().toString();

      OGremlinResultSet resSet = App.graph.executeSql(
        "SELECT both('Has_Id_Card') as rid " +
          "FROM Object_Identity_Card " +
          "WHERE both('Has_Id_Card').size() > 1", Collections.EMPTY_MAP);

      resSet.forEach(it -> {
          ORID sourceVRids = ((OVertexDocument) ((ArrayList) (it.getProperty("rid"))).get(0)).getIdentity();
          ORID targetVRids = ((OVertexDocument) ((ArrayList) (it.getProperty("rid"))).get(1)).getIdentity();

          mergeVertices(sourceVRids, targetVRids, true, true);
        }
      );

      String countPersonOrganisationAgain =
        App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('merge vertices')).out('Has_Ingestion_Event')" +
          ".has('Metadata_Type_Person_Organisation',eq('Person_Organisation')).count().next().toString()").get().toString();

      String countIdentityEdgesAgain =
        App.executor.eval("App.g.V().has('Event_Ingestion_Type', eq('merge vertices'))" +
          ".out('Has_Ingestion_Event').has('Person_Organisation_Name',TextP.endingWith('[merge vertex]'))" +
          ".out('Has_Id_Card').count().next().toString()").get().toString();

      assertFalse(countPersonOrganisation.equals(countPersonOrganisationAgain), "Person_Organisation vertices are merged from 6 to 3");
      assertFalse(countIdentityEdges.equals(countIdentityEdgesAgain), "Has_Id_Card edges are merged from 6 to 3");

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

}
