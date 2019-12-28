import com.fasterxml.jackson.databind.ObjectMapper
import com.hubspot.jinjava.Jinjava
import com.hubspot.jinjava.lib.fn.ELFunctionDefinition
import com.orientechnologies.common.listener.OProgressListener
import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OProperty
import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.core.record.impl.ODocument
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.apache.tinkerpop.gremlin.orientdb.OrientStandardGraph
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONVersion

import java.util.concurrent.atomic.AtomicInteger

LinkedHashMap globals = [:]
globals << [graph: (graph) as OrientStandardGraph]
globals << [g: (graph).traversal() as GraphTraversalSource]


@CompileStatic
def loadSchema(OrientStandardGraph graph, String... files) {
  StringBuffer sb = new StringBuffer()


  Map<String, OProperty> propsMap = [:]
  for (f in files) {
    try {

      def jsonFile = new File(f);

      if (jsonFile.exists()) {
        def jsonStr = jsonFile.text
        def json = new JsonSlurper().parseText(jsonStr)
        sb?.append("\nLoading File ${f}\n")


        sb?.append("\nAbout to create vertex labels\n")

        Map<String, OClass> classes = addVertexLabels(graph, json, sb);

      } else {
        sb?.append("NOT LOADING FILE ${f}\n")
      }

    } catch (Throwable t) {
      sb?.append('Failed to load schema!\n')?.append(t);
      t.printStackTrace()

    }
  }
  graph.tx().commit();

  sb?.append('Done!\n')
  return sb?.toString()
}


Map<String, OClass> addVertexLabels(OrientStandardGraph graph, def json, StringBuffer sb = null) {

  Map<String, OClass> classMap = new HashMap<>();
  json['vertexLabels'].each {
    final String name = it.name
    final OClass oClass = createVertexLabel(mgmt, name);
    classMap.put(name, oClass);
    sb?.append("Success added vertext label - $name\n")

    json['propertyKeys'].each { prop ->
      if (prop.name && prop.dataType && (prop.name as String).startsWith(name)) {
        OProperty oProperty = createProp(oClass, prop.name as String, Class.forName(prop.dataType as String));

      }
    }

    json['vertexIndexes'].each { idx ->
      if (idx.name && idx.propertyKeys && (idx.name as String).startsWith(name)) {

        if (idx.composite) {
          oClass.createIndex(idx.name as String, OClass.INDEX_TYPE.FULLTEXT.toString(), ids.propertyKeys as String[]);

        }
        if (idx.mixedIndex == "search") {
          oClass.createIndex(idx.name as String, OClass.INDEX_TYPE.FULLTEXT.toString(), null as OProgressListener,
            null as ODocument,
            "LUCENE", ids.propertyKeys as String[]);
        }


      }
    }


  }
  return classMap;
}

Map<String, OClass> addEdgeLabels(OrientStandardGraph graph, def json, StringBuffer sb = null) {

  Map<String, OClass> classMap = new HashMap<>();
  json['edgeLabels'].each {
    final String name = it.name
    final OClass oClass = createEdgeLabel(graph, name);
    classMap.put(name, oClass);
    sb?.append("Success added vertext label - $name\n")

    json['propertyKeys'].each { prop ->
      if (prop.name && prop.dataType && (prop.name as String).startsWith(name)) {
        OProperty oProperty = createProp(oClass, prop.name as String, Class.forName(prop.dataType as String));

      }
    }

    json['edgeIndexes'].each { idx ->
      if (idx.name && idx.propertyKeys && (idx.name as String).startsWith(name)) {

        if (idx.composite) {
          oClass.createIndex(idx.name as String, OClass.INDEX_TYPE.FULLTEXT.toString(), ids.propertyKeys as String[]);

        }
        if (idx.mixedIndex == "search") {
          oClass.createIndex(idx.name as String, OClass.INDEX_TYPE.FULLTEXT.toString(), null as OProgressListener,
            null as ODocument,
            "LUCENE", ids.propertyKeys as String[]);
        }


      }
    }


  }
  return classMap;
}

@CompileStatic
OProperty createProp(OClass oClass, String keyName, Class<?> classType) {

  try {
    OType oType = OType.getTypeByClass(classType);

    OProperty prop = oClass.getProperty(keyName)
    if (prop == null) {
      prop = oClass.createProperty(keyName, oType)

    }
    System.out.println("keyName = ${keyName}, keyID = " + prop.getId())

    return prop

  }
  catch (Throwable t) {
    t.printStackTrace();
  }
  return null
}


OClass createVertexLabel(OrientStandardGraph graph, String labelName) {

  try {
    String className = graph.createVertexClass(labelName);
    OClass oClass = graph.getSchema().getClass(className)



    createProp(oClass, "Metadata.Type." + labelName, String.class);


    return oClass;


  }
  catch (Throwable t) {
    t.printStackTrace();
  }
  return null;
}

OClass createEdgeLabel(OrientStandardGraph graph, String labelName) {

  try {
    String className = graph.createEdgeClass(labelName);
    OClass oClass = graph.getSchema().getClass(className)

    createProp(oClass, "Metadata.Type." + labelName, String.class);

    return oClass;


  }
  catch (Throwable t) {
    t.printStackTrace();
  }
  return null;
}


boolean isASCII(String s) {
  for (int i = 0; i < s.length(); i++)
    if (s.charAt(i) > 127)
      return false;
  return true;
}


def getPropsNonMetadataAsHTMLTableRows(GraphTraversalSource g, Long vid, String origLabel) {
  StringBuilder sb = new StringBuilder();

//    sb.append("{");


//    boolean firstLine = true;

  sb.append(new groovy.json.JsonBuilder(g.V(vid).valueMap().next()).toString());
//    g.V(vid).valueMap().next().forEach { origKey, origVal ->
//
//        if (!firstLine){
//            sb.append (',')
//        }
//        firstLine = false;
//        sb.append('"').append(origKey).append('": "').append(origVal).append('"')
////        String val = origVal.get(0)
////        String key = origKey.replaceAll('[_.]', ' ')
////        if (!key.startsWith('Metadata')) {
////            sb.append("<tr><td class='tg-yw4l'>");
////            if (key.endsWith("b64")) {
////                val = new String(val.decodeBase64())
////                key += ' (Decoded)'
////            }
////            val = val.replaceAll('\\\\"', '"')
////            val = val.replaceAll('\"', '"')
////
////            val = org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(HtmlManipulator.replaceHtmlEntities(val));
////            val = val.replaceAll("(\\r\\n|\\n)", "<br />");
////
////            val = val.replaceAll('\\\\', '');
////
////            if (!isASCII(val)) {
////                val = val.replaceAll("\\p{C}", "?");
////            }
////
////            if (origKey.startsWith(origLabel)) {
////                sb.append(key.substring(origLabel.length() + 1))
////            } else {
////                sb.append(key);
////            }
////            sb.append("</td><td class='tg-yw4l'>") //.append("<![CDATA[");
////                    .append(val)
//////                    .append("]]>")
////                    .append("</td></tr>");
////        }
//    }
//    sb.append("}");

  return sb.toString().bytes.encodeBase64();

//    return sb.toString().replaceAll('["]', '\\\\"').bytes.encodeBase64();
}


def renderReportInTextPt(long pg_id, String reportType = 'DSAR', GraphTraversalSource g = g) {
  def template = g.V().has('Object.Notification_Templates.Types', eq('Person.Natural'))
    .has('Object.Notification_Templates.Label', eq(reportType))
    .values('Object.Notification_Templates.Text').next() as String
  if (template) {

    // def template = g.V().has('Object.Notification_Templates.Types',eq(label)).next() as String
    def context = g.V(pg_id).valueMap()[0].collectEntries { key, val ->
      [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
    };

    def neighbours = g.V(pg_id).both().valueMap().toList().collect { item ->
      item.collectEntries { key, val ->
        [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
    };

    def allData = new HashMap<>();

    allData.put('context', context);
    allData.put('connected_data', neighbours);


    return PontusJ2ReportingFunctions.jinJava.render(new String(template.decodeBase64()), allData).toString();
  }
  return "Failed to render data"
}


def renderReportInText(long pg_id, String reportType = 'SAR Read', GraphTraversalSource g = g) {

  if (new File("conf/i18n_pt_translation.json").exists()) {
    return renderReportInTextPt(pg_id, reportType, g);
  }

  def template = g.V().has('Object.Notification_Templates.Types', eq('Person.Natural'))
    .has('Object.Notification_Templates.Label', eq(reportType))
    .values('Object.Notification_Templates.Text').next() as String
  if (template) {

    // def template = g.V().has('Object.Notification_Templates.Types',eq(label)).next() as String
    def context = g.V(pg_id).valueMap()[0].collectEntries { key, val ->
      [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
    };

    def neighbours = g.V(pg_id).both().valueMap().toList().collect { item ->
      item.collectEntries { key, val ->
        [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
    };

    def allData = new HashMap<>();

    allData.put('context', context);
    allData.put('connected_data', neighbours);

    return PontusJ2ReportingFunctions.jinJava.render(new String(template.decodeBase64()), allData).toString();
  }
  return "Failed to render data"
}


public class PontusJ2ReportingFunctions {

  public static def getProbabilityOfPossibleMatches(Long startVertexId, Map<String, Double> weightsPerVertex) {

    String vertType = g.V(startVertexId).label().next();

    def weightedScores = new HashMap<Long, Double>();
    def labelsForMatch = new HashMap<Long, StringBuffer>();

    Double totalScore = 0;

    g.V(startVertexId).both().label().each { String label ->
      totalScore += weightsPerVertex.get(label, new Double(0));
    }

    g.V(startVertexId)
      .both().bothE()
      .filter(bothV()
        .has("Metadata.Type.${vertType}", eq(vertType))
        .id().not(is(startVertexId))).path()
      .each { path ->

        path.objects().each { obj ->
          if (obj instanceof Edge) {

            int counter = 0;
            obj.bothVertices().each { v ->

              if (vertType == v.label()) {
                Long currVid = v.id() as Long;
                def currScore = weightedScores.get(currVid, new Double(0));
                // def listOfPaths = perUserVertices.computeIfAbsent(v.id(), s -> [] )
                int vertIdx = (counter == 0) ? 1 : 0
                String label = obj.bothVertices().get(vertIdx).label()
                Double scoreForLabel = weightsPerVertex.get(label, new Double(0));

                if (scoreForLabel > 0) {
                  StringBuffer currPath = labelsForMatch.get(currVid, new StringBuffer());
                  if (currPath.length() > 0) {
                    currPath.append(', ')
                  }
                  currPath.append(translate(label.replaceAll("[_|\\.]", " ")));
                  labelsForMatch.put(currVid, currPath);
                }
                currScore += scoreForLabel / totalScore;
                weightedScores.put(currVid, currScore);

              }
              counter++;
            }
          }
          // }
        }
      }


    return [weightedScores, labelsForMatch];

  }

  public static GraphTraversalSource g;
  public static Jinjava jinJava;

  static {
    PontusJ2ReportingFunctions.jinJava = new Jinjava();

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getChart",
      PontusJ2ReportingFunctions.class, "getChart"));

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "possibleMatches",
      PontusJ2ReportingFunctions.class, "possibleMatches", String.class, String.class));

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "context",
      PontusJ2ReportingFunctions.class, "context", String.class));
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "connected_data",
      PontusJ2ReportingFunctions.class, "neighbours", String.class));

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "htmlTableCustomHeader",
      PontusJ2ReportingFunctions.class, "htmlTableCustomHeader", Map.class, String.class, String.class));
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "htmlRows",
      PontusJ2ReportingFunctions.class, "htmlRows", Map.class, String.class));
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "htmlTable",
      PontusJ2ReportingFunctions.class, "htmlTable", Map.class));
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "jsonToHtmlTable",
      PontusJ2ReportingFunctions.class, "jsonToHtmlTable", String.class));
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "jsonToMap",
      PontusJ2ReportingFunctions.class, "jsonToMap", String.class));
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "businessRulesTable",
      PontusJ2ReportingFunctions.class, "businessRulesTable", String.class));

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getDataSourcesForLawfulBasis",
      PontusJ2ReportingFunctions.class, "getDataSourcesForLawfulBasis", String.class));

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getDeptForDataSources",
      PontusJ2ReportingFunctions.class, "getDeptForDataSources", String.class));


    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getNumNaturalPersonForLawfulBasis",
      PontusJ2ReportingFunctions.class, "getNumNaturalPersonForLawfulBasis", String.class));
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getNumNaturalPersonForPIA",
      PontusJ2ReportingFunctions.class, "getNumNaturalPersonForPIA", String.class));
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getNumSensitiveInfoForPIA",
      PontusJ2ReportingFunctions.class, "getNumSensitiveInfoForPIA", String.class));

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "t",
      PontusJ2ReportingFunctions.class, "translate", String.class));


  }

  public static Map<Map<String, String>, Double> possibleMatchesMap(String pg_id, Map<String, Double> weightsPerVertex) {
    def (Map<Long, Double> probs, Map<Long, StringBuffer> labelsForMatch) = getProbabilityOfPossibleMatches(Long.parseLong(pg_id), weightsPerVertex);

    Map<Map<String, String>, Double> retVal = new HashMap<>();
    probs.each { vid, prob ->
      Map<String, String> context = g.V(vid).valueMap()[0].collectEntries { key, val ->
        [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
      };
      context.put('Labels_For_Match', labelsForMatch.get(vid).toString());
      retVal.put(context, prob);
    }

    return retVal;
  }

  public static Map<Map<String, String>, Double> possibleMatches(String pg_id, String weightsPerServer) {


    Map<String, Double> weights =
      new ObjectMapper().readValue(weightsPerServer, Map.class);

    return possibleMatchesMap(pg_id, weights);
  }

  public static Map<String, String> context(String pg_id) {
    def context = g.V(Long.parseLong(pg_id)).valueMap(true)[0].collectEntries { key, val ->
      [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
    };
    return context;
  }

  public static List<Map<String, String>> neighbours(String pg_id) {
    def neighbours = g.V(Long.parseLong(pg_id)).both().valueMap(true).toList().collect { item ->
      item.collectEntries { key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
    };

    return neighbours;

  }

  public static String htmlTableCustomHeader(Map<String, String> map, String tableHeader, String tableFooter) {
    StringBuilder htmlBuilder = new StringBuilder();
    htmlBuilder.append(tableHeader);

    htmlBuilder.append(htmlRows(map))
    htmlBuilder.append(tableFooter);

    return htmlBuilder.toString();
  }

  public static String htmlRows(Map<String, String> map, String rowsCss = "border: 1px solid #dddddd;text-align: left;padding: 8px;") {
    StringBuilder htmlBuilder = new StringBuilder();
    for (Map.Entry<String, String> entry : map.entrySet()) {
      htmlBuilder.append(String.format("<tr style='${rowsCss}'><td style='${rowsCss}'>%s</td><td style='${rowsCss}'>%s</td></tr>\n",
        translate(entry.getKey().replaceAll("_", " ")), entry.getValue()));
    }

    return htmlBuilder.toString();
  }

  public static String htmlTable(Map<String, String> map) {
    htmlTableCustomHeader(map,
      "<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>" +
        translate("Name") +
        "</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>" +
        translate("Value") +
        "</th></tr>",
      "</table>");
  }

  public static String jsonToHtmlTable(String json) {
    Map<String, String> jsonMap =
      new ObjectMapper().readValue(json, Map.class);

    htmlTableCustomHeader(jsonMap,
      "<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Name</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Value</th></tr>",
      "</table>");
  }

  public static Map jsonToMap(String json) {
    return new ObjectMapper().readValue(json, Map.class);

  }


  public static List<Map<String, String>> getDeptForDataSources(String dataSourceId) {
    return g.V(Long.parseLong(dataSourceId))
      .in('Has_Privacy_Impact_Assessment')
      .filter(label().is('Object.Data_Source'))
      .out('Has_Ingestion_Event')
      .out('Has_Ingestion_Event')
      .in('Has_Ingestion_Event')
      .filter(label().is('Person.Natural'))
      .valueMap(true)
      .toList().collect({ item ->
//      .collect { item ->
      item.collectEntries({ key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      })
    } as Closure<Map<String, String>>);
  }

  public static List<Map<String, String>> getDataSourcesForLawfulBasis(String lawfulBasisId) {
    def retVal = g.V(Long.parseLong(lawfulBasisId))
      .in()
      .in()
      .has('Metadata.Type.Object.Privacy_Impact_Assessment', P.eq('Object.Privacy_Impact_Assessment'))
      .in()
      .has('Metadata.Type.Object.Data_Source', P.eq('Object.Data_Source'))
      .valueMap(true).toList().collect { item ->
      item.collectEntries { key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
    };

    return retVal as List<Map<String, String>>;

  }


  public static Long getNumNaturalPersonForLawfulBasis(String lawfulBasisId) {
    return g.V(Long.parseLong(lawfulBasisId))
      .in()
      .in()
      .has('Metadata.Type.Object.Privacy_Impact_Assessment', P.eq('Object.Privacy_Impact_Assessment'))
      .both()
      .has('Metadata.Type.Object.Privacy_Notice', P.eq('Object.Privacy_Notice'))
      .in()
      .has('Metadata.Type.Event.Consent', P.eq('Event.Consent'))
      .in()
      .dedup()
      .count()
      .next();
  }

  public static Long getNumNaturalPersonForPIA(String piaId) {
    return g.V(Long.parseLong(piaId))
      .in('Has_Privacy_Impact_Assessment')
      .filter(has('Metadata.Type.Object.Data_Source', P.eq('Object.Data_Source')))
      .out('Has_Ingestion_Event')
      .out('Has_Ingestion_Event')
      .in('Has_Ingestion_Event')
      .filter(has('Metadata.Type.Person.Natural', P.eq('Person.Natural')))
      .count()
      .next();


  }

  public static Long getNumSensitiveInfoForPIA(String piaId) {
    return g.V(Long.parseLong(piaId))
      .in('Has_Privacy_Impact_Assessment')
      .filter(label().is('Object.Data_Source'))
      .out('Has_Ingestion_Event')
      .out('Has_Ingestion_Event')
      .both('Has_Ingestion_Event')
      .filter(has('Metadata.Type.Object.Sensitive_Data', P.eq('Object.Sensitive_Data')))
      .count()
      .next()

  }

  public static String businessRulesTable(String json) {


    StringBuffer sb = new StringBuffer("<table style='margin: 2px; padding: 5px;'>")
      .append("<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
      .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Name")).append("</th>")
      .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Match Weight")).append("</th>")
      .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Exclude From Search")).append("</th>")
      .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Exclude From Subsequence Search")).append("</th>")
      .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Exclude From Update")).append("</th>")
      .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Operation")).append("</th>")
      .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Value")).append("</th>")
      .append("</tr>");

    Map br = jsonToMap(json);

//    System.out.println('Before loop');

    br.each { key, map ->
      sb.append("<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")

      def innerMap = map[0];

//      System.out.println("key = ${key}, map[0] = ${innerMap} map.size() = ${map.size()}; map[0].size = ${innerMap.size()}; ${map.class} innerMap.class = ${innerMap.class}");

//      innerMap.each { it ->
//        System.out.println("it = ${it}; it.class = ${it.class}");
//        System.out.println("it.key = ${it.key}; it.val = ${it.value}");
//      }
//
      String mainValue = null;
      innerMap.each { entry ->
        if (entry.key != 'matchWeight' && entry.key != 'excludeFromSearch' &&
          entry.key != 'excludeFromSubsequenceSearch' && entry.key != 'excludeFromUpdate' &&
          entry.key != 'operator') {
          sb.append("<td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
            .append(entry.key.toString())
            .append("</td>")
          mainValue = entry.value.toString();

        }
      }

//      System.out.println("innerMap.get('matchWeight' = ${innerMap.get('matchWeight')}");
//      System.out.println("innerMap.get('excludeFromSearch' = ${innerMap.get('excludeFromSearch')}");
//      System.out.println("innerMap.get('excludeFromSubsequenceSearch' = ${innerMap.get('excludeFromSubsequenceSearch')}");
//      System.out.println("innerMap.get('excludeFromUpdate' = ${innerMap.get('excludeFromUpdate')}");
//      System.out.println("innerMap.get('operator' = ${innerMap.get('operator')}");

      sb.append("<td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
        .append(innerMap.get('matchWeight'))
        .append("</td>")

        .append("<td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
        .append(innerMap.get('excludeFromSearch'))
        .append("</td>")

        .append("<td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
        .append(innerMap.get('excludeFromSubsequenceSearch'))
        .append("</td>")

        .append("<td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
        .append(innerMap.get('excludeFromUpdate'))
        .append("</td>")

        .append("<td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
        .append(innerMap.get('operator'))
        .append("</td>")

        .append("<td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
        .append(mainValue)
        .append("</td>")


      sb.append("</tr>")
    }

    sb.append('</table>')

//    {{  "<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td>" | format (c.key , c.value )}}

    return sb.toString();

  }

  public static Long getNumDataSourcesForPIA(String id) {
    return g.V(Long.parseLong(id)).both().has('Metadata.Type.Object.Data_Source', P.eq('Object.Data_Source')).id().count().next()
  }

  public static JsonSlurper ptDictionarySlurper;

  def static ptDictionary;
  static {
    ptDictionarySlurper = new JsonSlurper();
    try {
      def inputFile = new File("conf/i18n_pt_translation.json")

      ptDictionary = ptDictionarySlurper.parse(inputFile.text.toCharArray());

    }
    catch (Throwable t) {
      System.err.println("failed to load conf/i18n_pt_translation.json: " + t.toString());
    }

  }

//  public static String getChart() {
//    try {
//
//      /* Step - 1: Define the data for the bar chart  */
//      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//      dataset.addValue(34, "Q1", "Rome");
//      dataset.addValue(45, "Q1", "Cairo");
//      dataset.addValue(22, "Q2", "Rome");
//      dataset.addValue(12, "Q2", "Cairo");
//      dataset.addValue(56, "Q3", "Rome");
//      dataset.addValue(98, "Q3", "Cairo");
//      dataset.addValue(2, "Q4", "Rome");
//      dataset.addValue(15, "Q4", "Cairo");
//
//      ChartFactory.setChartTheme(StandardChartTheme.createDarknessTheme());
//      /* Step -2:Define the JFreeChart object to create bar chart */
//      JFreeChart barChart = ChartFactory.createBarChart(
//        "CountryVsSales - Bar Chart",
//        "Country",
//        "Sales",
//        dataset,
//        PlotOrientation.VERTICAL, true, true, false);
//
//      /* Step -3: Write the output as PNG file with bar chart information */
//      int width = 640; /* Width of the image */
//      int height = 480; /* Height of the image */
//
//      ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//
//      ChartUtils.writeChartAsPNG(out, barChart, width, height);
//
//
//      out.flush();
//
//      return new String (Base64.mimeEncoder.encode(out.toByteArray()));
//
//    }
//    catch (Exception i)
//    {
//      return i.toString();
//
//    }
//
//  }

  public static String translate(String strToTranslate) {
    if (ptDictionary) {
      String retVal = ptDictionary.get(strToTranslate);
      if (!retVal) {
        System.err.println("failed to find translation conf/i18n_pt_translation.json: " + strToTranslate);
        return strToTranslate;
      } else {
        return retVal;
      }
    }
    return strToTranslate;
  }
}


def renderReportInBase64(long pg_id, String pg_templateTextInBase64, GraphTraversalSource g = g) {

  String vertType = g.V(pg_id).label().next();
  def allData = new HashMap<>();


  def context = g.V(pg_id).valueMap(true)[0].collectEntries { key, val ->
    [key.toString().replaceAll('[.]', '_'), val.toString().startsWith('[') ? val.toString().substring(1, val.toString().length() - 1) : val.toString()]
  };

  def neighbours = g.V(pg_id).both().valueMap(true).toList().collect { item ->
    item.collectEntries { key, val ->
      [key.toString().replaceAll('[.]', '_'), val.toString().startsWith('[') ? val.toString().substring(1, val.toString().length() - 1) : val.toString()]
    }
  };

  allData.put('context', context);
  allData.put('connected_data', neighbours);


  PontusJ2ReportingFunctions.g = g;

  if ('Event.Data_Breach' == vertType) {
    def impactedServers = g.V(pg_id)
      .both()
      .has("Metadata.Type.Object.AWS_Instance", P.eq('Object.AWS_Instance'))
      .valueMap().toList().collect { item ->
      item.collectEntries { key, val ->
        [key.replaceAll('[.]', '_'), val.toString().substring(1, val.toString().length() - 1)]
      }
    };

    GraphTraversal impactedDataSourcesTrav = g.V(pg_id)
      .both().has("Metadata.Type.Object.AWS_Instance", P.eq('Object.AWS_Instance'))
      .bothE('Runs_On').outV().dedup()

    GraphTraversal dsTravClone = impactedDataSourcesTrav.clone()

    def impactedDataSources = impactedDataSourcesTrav.valueMap().toList().collect { item ->
      item.collectEntries { key, val ->
        [key.replaceAll('[.]', '_'), val.toString().substring(1, val.toString().length() - 1)]
      }
    };
    def impactedPeople = dsTravClone
      .out("Has_Ingestion_Event")
      .out("Has_Ingestion_Event")
      .in("Has_Ingestion_Event")
      .has("Metadata.Type.Person.Natural", P.eq('Person.Natural'))
      .dedup()
      .valueMap()
      .toList()
      .collect { item ->
        item.collectEntries { key, val ->
          [key.replaceAll('[.]', '_'), val.toString().substring(1, val.toString().length() - 1)]
        }
      };
    allData.put('impacted_data_sources', impactedDataSources);
    allData.put('impacted_servers', impactedServers);
    allData.put('impacted_people', impactedPeople);

  }


  return PontusJ2ReportingFunctions.jinJava.render(new String(pg_templateTextInBase64.decodeBase64()), allData).bytes.encodeBase64().toString();

}
//def getVisJsGraphImmediateNeighbourNodes(long pg_vid, StringBuffer sb, int counter, Set <Long> nodeIds,AtomicInteger depth) {
//
//    if (depth.intValue() == 0)
//    {
//        return;
//    }
//    depth.decrementAndGet();
//
//    g.V(pg_vid)
//            .repeat(both()).times(depth.intValue()).emit()
//            .each {
//        String groupStr = it.values('Metadata.Type').next();
//        String labelStr = it.label().toString().replaceAll('[_.]', ' ');
//        Long vid = it.id() as Long;
//        if (nodeIds.add(vid)){
//            sb.append(counter == 0 ? '{' : ',{')
//                    .append('"id":').append(vid)
//                    .append(',"level":').append(getLevel(labelStr))
//                    .append(',"group":"').append(groupStr)
//                    .append('","label":"').append(labelStr)
//                    .append('","shape":"').append('image')
//                    .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
//                    .append('"');
//            if (vid.equals(pg_vid)) {
//                sb.append(',"fixed":true');
//            }
//            sb.append('}')
//
//            counter++;
////            getVisJsGraphImmediateNeighbourNodes(vid,sb,counter,nodeIds,depth);
//
//        }
//
//    };
//
//
//
//    if (nodeIds.add(pg_vid)){
//        g.V(pg_vid)  // Also get the original node
//                .each {
//            String groupStr = it.values('Metadata.Type').next();
//            String labelStr = it.label().toString().replaceAll('[_.]', ' ');
//            Long vid = it.id();
//            sb.append(counter == 0 ? '{' : ',{')
//                    .append('"id":').append(vid)
//                    .append(',"level":').append(getLevel(labelStr))
//                    .append(',"group":"').append(groupStr)
//                    .append('","label":"').append(labelStr)
//                    .append('","shape":"').append('image')
//                    .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
//                    .append('"');
//            if (vid.equals(pg_vid)) {
//                sb.append(',"fixed":true');
//            }
//            sb.append('}')
//
//            counter++;
//
//        };
//    }
//
//    return counter;
//}

def getVisJsGraphImmediateNeighbourNodes(long pg_vid, StringBuffer sb, int counter, Set<Long> nodeIds, AtomicInteger depth) {

  def types = getMetadataTypes(depth.intValue());

  types.each { type ->
    g.V().has("Metadata.Type.${type}", eq("${type}")).each {
      String groupStr = it.values('Metadata.Type').next();
      String labelStr = it.label().toString().replaceAll('[_.]', ' ');
      Long vid = it.id() as Long;
      if (nodeIds.add(vid)) {
        sb.append(counter == 0 ? '{' : ',{')
          .append('"id":').append(vid)
          .append(',"level":').append(getLevel(labelStr))
          .append(',"group":"').append(groupStr)
          .append('","label":"').append(labelStr)
          .append('","shape":"').append('image')
          .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
          .append('"');
        if (vid.equals(pg_vid)) {
          sb.append(',"fixed":true');
        }
        sb.append('}')

        counter++;

      }
    }
  };


  return counter;
}


def getEdgeProperties(long fromVertexId, long toVertexId) {
  def mapper = GraphSONMapper.build().version(GraphSONVersion.V1_0).create().createMapper();

  def v = g.V(fromVertexId).bothE().filter(bothV().id().is(toVertexId)).valueMap().next()

  mapper.writeValueAsString(v)

}


def getVisJSGraph(long pg_vid, long pg_depth) {
//g.V().has("Metadata.Type.Person.Natural",eq("Person.Natural")).id()
//g.V(1720320).bothE()
//1720320
//
//  def pg_depth= 1
//  def pg_vid= 1827056

  StringBuffer sb = new StringBuffer()
  StringBuffer sb2 = new StringBuffer()

  Long numEdges = g.V(pg_vid).bothE().count().next();
  String origLabel = g.V(pg_vid).label().next().replaceAll('[_.]', ' ');

  if (numEdges > 15) {

    HashSet nodesSet = new HashSet()
    HashSet edgesSet = new HashSet()


    g.V(pg_vid).as('orig')
      .outE().match(
      __.as('e').inV().label().as('vLabel')
      // ,  __.as('e').outV().label().as('inVLabel')
      , __.as('e').label().as('edgeLabel')
    )
      .select('edgeLabel', 'vLabel')
      .groupCount().each {
      def entry = it;



      entry.each {
        key, val ->


          if (key instanceof Map) {

            String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ');
            String toNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
              ' -> (' + edgeLabel + ')';

            String edgeId = key.get('edgeLabel');
            String toNodeId = key.get('vLabel') +
              ' -> (' + edgeId + ')';

            sb.setLength(0);

            sb.append('{ "id":"').append(toNodeId)
              .append('","label":"').append(toNodeLabel)
              .append('","group":"').append(toNodeLabel)
              .append('","shape":"').append('box')
              .append('"}\n')


            nodesSet.add(sb.toString());


            sb.setLength(0);

            sb.append('{ "from":"')
              .append(pg_vid).append('","to":"')
              .append(toNodeId).append('","label":"')
              .append(edgeLabel).append(' (')
              .append(val).append(')","value":')
              .append(val).append('}\n')

            edgesSet.add(sb.toString());
            sb.setLength(0);

          }


      }


    }


    g.V(pg_vid).as('orig')
      .inE().match(
      __.as('e').outV().label().as('vLabel')
      // ,  __.as('e').outV().label().as('inVLabel')
      , __.as('e').label().as('edgeLabel')
    )
      .select('edgeLabel', 'vLabel')
      .groupCount().each {
      it.each {
        key, val ->
          if (key instanceof Map) {

            String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ');

            String fromNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
              ' <- (' + edgeLabel + ')';
            String edgeId = key.get('edgeLabel');

            String fromNodeId = key.get('vLabel') +
              ' <- (' + edgeId + ')';
            sb.setLength(0);

            sb.append('{ "id":"').append(fromNodeId)
              .append('","label":"').append(fromNodeLabel)
              .append('","group":"').append(fromNodeLabel)
              .append('","shape":"').append('box')
              .append('"}')

            nodesSet.add(sb.toString());


            sb.setLength(0);

            sb.append('{ "from":"')
              .append(fromNodeId).append('","to":"')
              .append(pg_vid).append('","label":"')
              .append(edgeLabel).append(' (')
              .append(val).append(')","value":')
              .append(val).append('}')
            edgesSet.add(sb.toString());
            sb.setLength(0);
          }


      }


    }
    sb.setLength(0)
    sb.append('{ "id":"').append(pg_vid)
      .append('","label":"').append(origLabel)
      .append('","group":"').append(origLabel)
      .append('","fixed":').append(true)
      .append(',"shape":"').append('image')
      .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, pg_vid, origLabel).toString())
      .append('"}')

    nodesSet.add(sb.toString())
    sb.setLength(0)

    sb.append('{ "nodes":')
      .append(nodesSet.toString()).append(', "edges":').append(edgesSet.toString())
  } else {
    int counter = 0;

    try {

      sb.append('{ "nodes":[');

      g.V(pg_vid)
        .both()
        .dedup()
        .each {
          String groupStr = it.values('Metadata.Type').next();
          String labelStr = it.label().toString().replaceAll('[_.]', ' ');
          Long vid = it.id();
          sb.append(counter == 0 ? '{' : ',{')
            .append('"id":').append(vid)
            .append(',"group":"').append(groupStr)
            .append('","label":"').append(labelStr)
            .append('","shape":"').append('image')
            .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
            .append('"');
          if (vid.equals(pg_vid)) {
            sb.append(',"fixed":true');
          }
          sb.append('}')

          counter++;

        };
      g.V(pg_vid)  // Also get the original node
        .each {
          String groupStr = it.values('Metadata.Type').next();
          String labelStr = it.label().toString().replaceAll('[_.]', ' ');
          Long vid = it.id();
          sb.append(counter == 0 ? '{' : ',{')
            .append('"id":').append(vid)
            .append(',"group":"').append(groupStr)
            .append('","label":"').append(labelStr)
            .append('","shape":"').append('image')
            .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
            .append('"');
          if (vid.equals(pg_vid)) {
            sb.append(',"fixed":true');
          }
          sb.append('}')

          counter++;

        };
      sb.append('], "edges":[')


      StringBuffer prob = new StringBuffer();
      try {
        prob.append(' - s - ')
          .append(
            Math.round(
              Math.min(
                it.values('toScorePercent').next(),
                it.values('fromScorePercent').next()
              ) * 100) / 100)
          .append('%')

      } catch (Throwable t) {
        prob.setLength(0)
      }


      counter = 0;
      g.V(pg_vid)
        .bothE()
        .dedup()
        .each {
          sb.append(counter == 0 ? '{' : ',{')
            .append('"from": ').append(it.inVertex().id())
            .append(' ,"to": "').append(it.outVertex().id())
            .append('","label": "').append(it.label().toString().replaceAll('[_.]', ' '))
            .append(prob.toString())
            .append('"}')

          counter++;

        }

      sb.append(']');


    } catch (Throwable t) {
      sb.append(t.toString());
    }

  }
  sb.append(', "origLabel":"').append(origLabel).append('"');
  int counter = 0;
  sb.append(', "reportButtons": [');
  try {
    g.V()
      .has('Object.Notification_Templates.Types'
        , eq(g.V(pg_vid).values('Metadata.Type').next()))
      .valueMap('Object.Notification_Templates.Label', 'Object.Notification_Templates.Text')
      .each {
        sb.append(counter > 0 ? ',{' : '{');
        counter++;
        sb.append('"text":"');
        if (it.get('Object.Notification_Templates.Text') != null)
          sb.append(it.get('Object.Notification_Templates.Text')[0].toString());
        sb.append('","label":"');
        if (it.get('Object.Notification_Templates.Label') != null)
          sb.append(it.get('Object.Notification_Templates.Label')[0]);
        sb.append('", "vid": ').append(pg_vid);

        sb.append("}")

      }
  } catch (e) {
  }
  sb.append('] }');
  sb.toString()
}

def getVisJsGraphImmediateNeighbourEdges(long pg_vid, StringBuffer sb, int counter, Set<String> currEdges) {


  StringBuffer localEntry = new StringBuffer();

  g.V(pg_vid)
    .bothE()
    .each {
      long from = it.inVertex().id() as long;
      long to = it.outVertex().id() as long;
      localEntry.setLength(0);

      localEntry.append(counter == 0 ? '{' : ',{')
        .append('"from": ').append(from)
        .append(' ,"to": "').append(to)
        .append('","label": "').append(it.label().toString().replaceAll('[_.]', ' '))
        .append('"}')
      String localEntryStr = localEntry.toString();

      if (currEdges.add(localEntryStr)) {
        sb.append(localEntryStr);
        counter++;

      }


    }
  return counter;
}
/*
Event.Group_Ingestion
Event.Ingestion

Person

Object.Email_Address
Object.Credential
Event.Form_Ingestion
Object.Identity_Card
Location.Address
Object.Insurance_Policy


Event.Consent
Object.Privacy_Notice
Object.Privacy_Impact_Assessment
Object.Lawful_Basis




Event.Subject_Access_Request
Person.Employee
Object.Awareness_Campaign
Event.Training
Event.Data_Breach

Person.Organisation

Object.Data_Procedures
Object.MoU
Object.Form
Object.Notification_Templates
Object.AWS_Instance
Object.AWS_Security_Group
Object.AWS_Network_Interface

 */

def getMetadataTypes(int level) {
  def metadataTypes = [
    'Event.Group_Ingestion'
    , 'Event.Ingestion'
    , 'Person.Natural'
    , 'Object.Email_Address'
    , 'Object.Credential'
    , 'Event.Form_Ingestion'
    , 'Object.Identity_Card'
    , 'Location.Address'
    , 'Object.Insurance_Policy'
    , 'Event.Consent'
    , 'Object.Privacy_Notice'
    , 'Object.Privacy_Impact_Assessment'
    , 'Object.Lawful_Basis'
    , 'Event.Subject_Access_Request'
    , 'Person.Employee'
    , 'Object.Awareness_Campaign'
    , 'Event.Training'
    , 'Event.Data_Breach'
    , 'Person.Organisation'
    , 'Object.Data_Procedures'
    , 'Object.MoU'
    , 'Object.Form'
    , 'Object.Notification_Templates'
    , 'Object.AWS_Instance'
    , 'Object.AWS_Security_Group'
    , 'Object.AWS_Network_Interface'
  ]
  return metadataTypes.subList(0, level);
}


def getLevel(String label) {
  def levels = [
    'Event  Group Ingestion',
    'Event Ingestion',
    'Person Natural',
    'Object Email Address',
    'Object Credential',
    'Event Form Ingestion',
    'Object Identity Card',
    'Location Address',
    'Object Insurance Policy',
    'Event Consent',
    'Object Privacy Notice',
    'Object Privacy Impact Assessment',
    'Object Lawful Basis',
    'Event Subject Access Request',
    'Person Employee',
    'Object Awareness Campaign',
    'Event Training',
    'Event Data_Breach',
    'Person Organisation',
    'Object Data Procedures',
    'Object MoU',
    'Object Form',
    'Object Notification Templates',
    'Object AWS Instance',
    'Object AWS Security Group',
    'Object AWS Network Interface'
  ];
  int index = levels.findIndexOf {
    (label.equals(it))
  };

  if (index == -1) {
    return label.hashCode() % 10;
  }

  return index;
}


def getVisJsGraph(long pg_vid) {


  StringBuffer sb = new StringBuffer()

  Long numEdges = g.V(pg_vid).bothE().count().next();
  String origLabel = g.V(pg_vid).label().next().replaceAll('[_.]', ' ');

  if (numEdges > 15) {

    HashSet nodesSet = new HashSet()
    HashSet edgesSet = new HashSet()


    g.V(pg_vid).as('orig')
      .outE().match(
      __.as('e').inV().label().as('vLabel')
      // ,  __.as('e').outV().label().as('inVLabel')
      , __.as('e').label().as('edgeLabel')
    )
      .select('edgeLabel', 'vLabel')
      .groupCount().each {
      def entry = it;



      entry.each {
        key, val ->


          if (key instanceof Map) {

            String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ');
            String toNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
              ' -> (' + edgeLabel + ')';

            String edgeId = key.get('edgeLabel');
            String toNodeId = key.get('vLabel') +
              ' -> (' + edgeId + ')';

            sb.setLength(0);

            sb.append('{ "id":"').append(toNodeId)
              .append('","label":"').append(toNodeLabel)
              .append('","group":"').append(toNodeLabel)
              .append('","shape":"').append('box')
              .append('"}\n')


            nodesSet.add(sb.toString());


            sb.setLength(0);

            sb.append('{ "from":"')
              .append(pg_vid).append('","to":"')
              .append(toNodeId).append('","label":"')
              .append(edgeLabel).append(' (')
              .append(val).append(')","value":')
              .append(val).append('}\n')

            edgesSet.add(sb.toString());
            sb.setLength(0);

          }


      }


    }


    g.V(pg_vid).as('orig')
      .inE().match(
      __.as('e').outV().label().as('vLabel')
      // ,  __.as('e').outV().label().as('inVLabel')
      , __.as('e').label().as('edgeLabel')
    )
      .select('edgeLabel', 'vLabel')
      .groupCount().each {
      it.each {
        key, val ->
          if (key instanceof Map) {

            String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ');

            String fromNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
              ' <- (' + edgeLabel + ')';
            String edgeId = key.get('edgeLabel');

            String fromNodeId = key.get('vLabel') +
              ' <- (' + edgeId + ')';
            sb.setLength(0);

            sb.append('{ "id":"').append(fromNodeId)
              .append('","label":"').append(fromNodeLabel)
              .append('","group":"').append(fromNodeLabel)
              .append('","shape":"').append('box')
              .append('"}')

            nodesSet.add(sb.toString());


            sb.setLength(0);

            sb.append('{ "from":"')
              .append(fromNodeId).append('","to":"')
              .append(pg_vid).append('","label":"')
              .append(edgeLabel).append(' (')
              .append(val).append(')","value":')
              .append(val).append('}')
            edgesSet.add(sb.toString());
            sb.setLength(0);
          }


      }


    }
    sb.setLength(0)
    sb.append('{ "id":"').append(pg_vid)
      .append('","label":"').append(origLabel)
      .append('","group":"').append(origLabel)
      .append('","fixed":').append(true)
      .append(',"shape":"').append('image')
      .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, pg_vid, origLabel).toString())
      .append('"}')

    nodesSet.add(sb.toString())
    sb.setLength(0)

    sb.append('{ "nodes":')
      .append(nodesSet.toString()).append(', "edges":').append(edgesSet.toString())
  } else {
    int counter = 0;

    try {

      sb.append('{ "nodes":[');

      g.V(pg_vid)
        .both()
        .dedup()
        .each {
          String groupStr = it.values('Metadata.Type').next();
          String labelStr = it.label().toString().replaceAll('[_.]', ' ');
          Long vid = it.id();
          sb.append(counter == 0 ? '{' : ',{')
            .append('"id":').append(vid)
            .append(',"group":"').append(groupStr)
            .append('","label":"').append(labelStr)
            .append('","shape":"').append('image')
            .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
            .append('"');
          if (vid.equals(pg_vid)) {
            sb.append(',"fixed":true');
          }
          sb.append('}')

          counter++;

        };
      g.V(pg_vid)  // Also get the original node
        .each {
          String groupStr = it.values('Metadata.Type').next();
          String labelStr = it.label().toString().replaceAll('[_.]', ' ');
          Long vid = it.id();
          sb.append(counter == 0 ? '{' : ',{')
            .append('"id":').append(vid)
            .append(',"group":"').append(groupStr)
            .append('","label":"').append(labelStr)
            .append('","shape":"').append('image')
            .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
            .append('"');
          if (vid.equals(pg_vid)) {
            sb.append(',"fixed":true');
          }
          sb.append('}')

          counter++;

        };
      sb.append('], "edges":[')

      StringBuffer prob = new StringBuffer();
      try {
        prob.append(' - s - ')
          .append(
            Math.round(
              Math.min(
                it.values('toScorePercent').next(),
                it.values('fromScorePercent').next()
              ) * 100) / 100)
          .append('%')

      } catch (Throwable t) {
        prob.setLength(0)
      }

      counter = 0;
      g.V(pg_vid)
        .bothE()
        .dedup()
        .each {
          sb.append(counter == 0 ? '{' : ',{')
            .append('"from": ').append(it.inVertex().id())
            .append(' ,"to": "').append(it.outVertex().id())
            .append('","label": "').append(it.label().toString().replaceAll('[_.]', ' '))
            .append(prob.toString())
            .append('"}')

          counter++;

        }

      sb.append(']');


    } catch (Throwable t) {
      sb.append(t.toString());
    }

  }
  sb.append(', "origLabel":"').append(origLabel).append('"');
  int counter = 0;
  sb.append(', "reportButtons": [');
  try {
    g.V()
      .has('Object.Notification_Templates.Types'
        , eq(g.V(pg_vid).values('Metadata.Type').next()))
      .valueMap('Object.Notification_Templates.Label', 'Object.Notification_Templates.Text')
      .each {
        sb.append(counter > 0 ? ',{' : '{');
        counter++;
        sb.append('"text":"');
        if (it.get('Object.Notification_Templates.Text') != null)
          sb.append(it.get('Object.Notification_Templates.Text')[0].toString());
        sb.append('","label":"');
        if (it.get('Object.Notification_Templates.Label') != null)
          sb.append(it.get('Object.Notification_Templates.Label')[0]);
        sb.append('", "vid": ').append(pg_vid);

        sb.append("}")

      }
  } catch (e) {
  }
  sb.append('] }');
  return sb.toString()
}


def getVisJsGraph(long pg_vid, int depth) {
  StringBuffer sb = new StringBuffer()

  Long numEdges = g.V(pg_vid).bothE().count().next();
  String origLabel = g.V(pg_vid).label().next().replaceAll('[_.]', ' ');
  AtomicInteger nodeDepth = new AtomicInteger(depth);

  if (numEdges > 15) {

    HashSet nodesSet = new HashSet()
    HashSet edgesSet = new HashSet()


    g.V(pg_vid).as('orig')
      .outE().match(
      __.as('e').inV().label().as('vLabel')
      // ,  __.as('e').outV().label().as('inVLabel')
      , __.as('e').label().as('edgeLabel')
    )
      .select('edgeLabel', 'vLabel')
      .groupCount().each {
      def entry = it;



      entry.each {
        key, val ->


          if (key instanceof Map) {

            String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ');
            String toNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
              ' -> (' + edgeLabel + ')';

            String edgeId = key.get('edgeLabel');
            String toNodeId = key.get('vLabel') +
              ' -> (' + edgeId + ')';

            sb.setLength(0);

            sb.append('{ "id":"').append(toNodeId)
              .append('","label":"').append(toNodeLabel)
              .append('","group":"').append(toNodeLabel)
              .append('","shape":"').append('box')
              .append('"}\\n')


            nodesSet.add(sb.toString());


            sb.setLength(0);

            sb.append('{ "from":"')
              .append(pg_vid).append('","to":"')
              .append(toNodeId).append('","label":"')
              .append(edgeLabel).append(' (')
              .append(val).append(')","value":')
              .append(val).append('}\\n')

            edgesSet.add(sb.toString());
            sb.setLength(0);

          }


      }


    }


    g.V(pg_vid).as('orig')
      .inE().match(
      __.as('e').outV().label().as('vLabel')
      // ,  __.as('e').outV().label().as('inVLabel')
      , __.as('e').label().as('edgeLabel')
    )
      .select('edgeLabel', 'vLabel')
      .groupCount().each {
      it.each {
        key, val ->
          if (key instanceof Map) {

            String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ');

            String fromNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
              ' <- (' + edgeLabel + ')';
            String edgeId = key.get('edgeLabel');

            String fromNodeId = key.get('vLabel') +
              ' <- (' + edgeId + ')';
            sb.setLength(0);

            sb.append('{ "id":"').append(fromNodeId)
              .append('","label":"').append(fromNodeLabel)
              .append('","group":"').append(fromNodeLabel)
              .append('","shape":"').append('box')
              .append('"}')

            nodesSet.add(sb.toString());


            sb.setLength(0);

            sb.append('{ "from":"')
              .append(fromNodeId).append('","to":"')
              .append(pg_vid).append('","label":"')
              .append(edgeLabel).append(' (')
              .append(val).append(')","value":')
              .append(val).append('}')
            edgesSet.add(sb.toString());
            sb.setLength(0);
          }


      }


    }
    sb.setLength(0)
    sb.append('{ "id":"').append(pg_vid)
      .append('","label":"').append(origLabel)
      .append('","group":"').append(origLabel)
      .append('","fixed":').append(true)
      .append(',"shape":"').append('image')
      .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, pg_vid, origLabel).toString())
      .append('"}')

    nodesSet.add(sb.toString())
    sb.setLength(0)

    sb.append('{ "nodes":')
      .append(nodesSet.toString()).append(', "edges":').append(edgesSet.toString())
      .append('}')
  } else {
    int counter = 0;

    try {
      Set<Long> nodeIds = new HashSet<>();

      sb.append('{ "nodes":[');

      getVisJsGraphImmediateNeighbourNodes(pg_vid, sb, counter, nodeIds, nodeDepth);

      sb.append('], "edges":[')


      counter = 0;

      Set<String> currEdges = new HashSet<>();
      nodeIds.each {
        counter = getVisJsGraphImmediateNeighbourEdges(it, sb, counter, currEdges);

      }

      sb.append(']');


    } catch (Throwable t) {
      sb.append(t.toString());
    }

    sb.toString()
  }
  sb.append(', "origLabel":"').append(origLabel).append('"');
  int counter = 0;
  sb.append(', "reportButtons": [');
  try {
    g.V()
      .has('Object.Notification_Templates.Types'
        , eq(g.V(pg_vid).values('Metadata.Type').next()))
      .valueMap('Object.Notification_Templates.Label', 'Object.Notification_Templates.Text')
      .each {
        sb.append(counter > 0 ? ',{' : '{');
        counter++;
        sb.append('"text":"');
        if (it.get('Object.Notification_Templates.Text') != null)
          sb.append(it.get('Object.Notification_Templates.Text')[0].toString());
        sb.append('","label":"');
        if (it.get('Object.Notification_Templates.Label') != null)
          sb.append(it.get('Object.Notification_Templates.Label')[0]);
        sb.append('", "vid": ').append(pg_vid);

        sb.append("}")

      }
  } catch (e) {
  }

  sb.append('] }');
  return sb.toString();


}



