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
        App.executor.eval("App.g.V().has('Metadata_Type_Person_Organisation',eq('Person_Organisation'))" +
          ".has('Person_Organisation_Name', TextP.endingWith('[MERGE VERTEX]')).count().next().toString()").get().toString();

      String countMatinhosOutEdgesAgain =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('MATINHOS EMP AL LTDA [MERGE VERTEX]')).out()" +
          ".count().next().toString()").get().toString();

      String countMatinhosOutEdgesDedupedAgain =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('MATINHOS EMP AL LTDA [MERGE VERTEX]')).out()" +
          ".dedup().count().next().toString()").get().toString();

      String countMonstersOutEdgesAgain =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('MONSTROS ELEC SA [MERGE VERTEX]')).out()" +
          ".count().next().toString()").get().toString();

      String countMonstersOutEdgesDedupedAgain =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('MONSTROS ELEC SA [MERGE VERTEX]')).out()" +
          ".dedup().count().next().toString()").get().toString();

      String countPearOutEdgesAgain =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('PEAR INCORPORATED [MERGE VERTEX]')).out()" +
          ".count().next().toString()").get().toString();

      String countPearOutEdgesDedupedAgain =
        App.executor.eval("App.g.V().has('Person_Organisation_Name', eq('PEAR INCORPORATED [MERGE VERTEX]')).out()" +
          ".dedup().count().next().toString()").get().toString();

      assertTrue( Integer.parseInt(countPersonOrganisation) > Integer.parseInt(countPersonOrganisationAgain), "Person_Organisation vertices are merged from 6 to 3");
      assertTrue( Integer.parseInt(countMatinhosOutEdges) > Integer.parseInt(countMatinhosOutEdgesAgain), "Matinhos edges are merged from 3 to 6");
      assertTrue( Integer.parseInt(countMonstersOutEdges) > Integer.parseInt(countMonstersOutEdgesAgain), "Monsters edges are merged from 3 to 6");
      assertTrue( Integer.parseInt(countPearOutEdges) > Integer.parseInt(countPearOutEdgesAgain), "Pear edges are merged from 3 to 6");
//    asserting dedupped edges
      assertTrue( Integer.parseInt(countMatinhosOutEdgesDedupedAgain) == 5, "Matinhos edges are dedupped to 5 because 2 edges point to Object_Identity_Card");
      assertTrue( Integer.parseInt(countMonstersOutEdgesDedupedAgain) == 4,
        "Monsters edges are dedupped to 4 because 4 edges of it's edges are duplicated, 2 point to Object_Identity_Card and 2 point to Object_Email_Address");
      assertTrue( Integer.parseInt(countPearOutEdgesDedupedAgain) == 3,
        "Pear edges are dedupped to 3 because all of it's edges are duplicated, 2 point to Object_Identity_Card, 2 point to Object_Email_Address and the last 2 are pointing to Location_Address");

    } catch (ExecutionException e) {
      e.printStackTrace();

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

}
