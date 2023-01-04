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
        App.executor.eval("App.g.V().has('Metadata_Type_Person_Organisation',eq('Person_Organisation'))" +
          ".has('Person_Organisation_Name', TextP.endingWith('[MERGE VERTEX]')).count().next().toString()").get().toString();

      String countMatinhosOutEdges =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('MATINHOS EMP AL LTDA [MERGE VERTEX]')).out()" +
          ".count().next().toString()").get().toString();

      String countMonstersOutEdges =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('MONSTROS ELEC SA [MERGE VERTEX]')).out()" +
          ".count().next().toString()").get().toString();

      String countPearOutEdges =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('PEAR INCORPORATED [MERGE VERTEX]')).out()" +
          ".count().next().toString()").get().toString();

      assertEquals(3, Integer.parseInt(countMatinhosOutEdges));
      assertEquals(3, Integer.parseInt(countMonstersOutEdges));
      assertEquals(3, Integer.parseInt(countPearOutEdges));

      OGremlinResultSet resSet = App.graph.executeSql(
        "SELECT both('Has_Id_Card') as rid " +
          "FROM Object_Identity_Card " +
          "WHERE both('Has_Id_Card').size() > 1", Collections.EMPTY_MAP);

      resSet.forEach(it -> {
          ORID sourceVRids = ((OVertexDocument) ((ArrayList) (it.getProperty("rid"))).get(0)).getIdentity();
          ORID targetVRids = ((OVertexDocument) ((ArrayList) (it.getProperty("rid"))).get(1)).getIdentity();

          long edgesCreated = mergeVertices(sourceVRids, targetVRids, true, true);
          assertEquals(9, edgesCreated);
        }
      );

      String countPersonOrganisationAgain =
        App.executor.eval("App.g.V().has('Metadata_Type_Person_Organisation',eq('Person_Organisation'))" +
          ".has('Person_Organisation_Name', TextP.endingWith('[MERGE VERTEX]')).count().next().toString()").get().toString();

      String countMatinhosOutEdgesAgain =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('MATINHOS EMP AL LTDA [MERGE VERTEX]')).out()" +
          ".count().next().toString()").get().toString();

      String countMonstersOutEdgesAgain =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('MONSTROS ELEC SA [MERGE VERTEX]')).out()" +
          ".count().next().toString()").get().toString();

      String countPearOutEdgesAgain =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('PEAR INCORPORATED [MERGE VERTEX]')).out()" +
          ".count().next().toString()").get().toString();

      assertTrue( Integer.parseInt(countPersonOrganisation) > Integer.parseInt(countPersonOrganisationAgain), "Person_Organisation vertices are merged from 6 to 3");
      assertTrue( Integer.parseInt(countMatinhosOutEdgesAgain) == 5, "Matinhos edges are 5");
      assertTrue( Integer.parseInt(countMonstersOutEdgesAgain) == 4, "Monsters edges are 4");
      assertTrue( Integer.parseInt(countPearOutEdgesAgain) == 3, "Pear edges are 3");

    } catch (ExecutionException e) {
      e.printStackTrace();

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

}
