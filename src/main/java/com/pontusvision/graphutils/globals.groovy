package com.pontusvision.graphutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.hubspot.jinjava.Jinjava
import com.hubspot.jinjava.lib.fn.ELFunctionDefinition
import com.orientechnologies.common.listener.OProgressListener
import com.orientechnologies.orient.core.id.ORID
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OProperty
import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.executor.OResultSet
import com.pontusvision.gdpr.App
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.StringEscapeUtils
import org.apache.tinkerpop.gremlin.orientdb.OrientStandardGraph
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet
import org.apache.tinkerpop.gremlin.process.traversal.Order
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONVersion

import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.bothV

class ODBSchemaManager {
  static def loadSchema(OrientStandardGraph graph, String... files) {
    StringBuffer sb = new StringBuffer()

    def dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    graph.executeSql('ALTER DATABASE DATETIMEFORMAT "' + dateFormat + '"', [:])
    graph.executeSql('ALTER DATABASE CONFLICTSTRATEGY "content" ', [:])
//    graph.executeSql("ALTER DATABASE demodb plocal users ( admin identified by '${System.getenv('ORIENTDB_ROOT_PASSWORD')?:'admin'}' role admin)");


    Map<String, OProperty> propsMap = [:]
    for (f in files) {
      try {

        def jsonFile = new File(f)

        if (jsonFile.exists()) {
          def jsonStr = jsonFile.text
          def json = new JsonSlurper().parseText(jsonStr)
          sb?.append("\nLoading File ${f}\n")


          sb?.append("\nAbout to create vertex labels\n")

          Map<String, OClass> classes = ODBSchemaManager.addVertexLabels(graph, json, sb)

        } else {
          sb?.append("NOT LOADING FILE ${f}\n")
        }

      } catch (Throwable t) {
        sb?.append('Failed to load schema!\n')?.append(t)
        t.printStackTrace()

      }
    }
    graph.tx().commit()

    sb?.append('Done!\n')
    return sb?.toString()
  }

  static Map<String, OClass> addVertexLabels(OrientStandardGraph graph, def json, StringBuffer sb = null) {

    Map<String, OClass> classMap = new HashMap<>()
    json['vertexLabels'].each {
      final String name = it.name
      final OClass oClass = createVertexLabel(graph, name)
      classMap.put(name, oClass)
      sb?.append("Success added vertext label - $name\n")

      json['propertyKeys'].each { prop ->
        if (prop.name && prop.dataType && (prop.name as String).startsWith(name)) {
          Class<?> cls = null

          try {
            cls = Class.forName(prop.dataType as String)
          }
          catch (Throwable t) {
            String dataTypeLowerCase = prop?.dataType?.toLowerCase()
            if (dataTypeLowerCase == 'date') {
              cls = Date.class
            } else if (dataTypeLowerCase == 'string') {
              cls = String.class

            } else {
              cls = Class.forName("java.lang.${prop.dataType}")
            }
          }
          OProperty oProperty = createProp(oClass, prop.name as String, cls)
        }

      }

      json['vertexIndexes'].each { idx ->
        if (idx.name && idx.propertyKeys && (idx.name as String).startsWith(name)) {

          if (idx.composite) {
            System.out.println("COMPOSITE Index ${idx.name} already exists.")

            if (!oClass.getClassIndex(idx.name)) {
              oClass.createIndex(idx.name as String, OClass.INDEX_TYPE.FULLTEXT.toString(), idx.propertyKeys as String[])
            } else {
              System.out.println("Index ${idx.name} already exists.")
            }


          }
          if (idx.mixedIndex == "search") {
            ODocument metadata = new ODocument();
            metadata.field("indexRadix", true);
            metadata.field("stopWords", [""]);
            metadata.field("separatorChars", " :;?!.,");
            metadata.field("ignoreChars", "");
            metadata.field("minWordLength", 1);

            if (!oClass.getClassIndex(idx.name)) {
//              oClass.createIndex(idx.name as String, OClass.INDEX_TYPE.FULLTEXT.toString(), null as OProgressListener,
//                      metadata as ODocument,
//                      "LUCENE", idx.propertyKeys as String[])
              OClass.INDEX_TYPE idxType = Enum.valueOf(OClass.INDEX_TYPE.class, idx.indexType)
              oClass.createIndex(idx.name as String, idxType.toString(), null as OProgressListener,
                      metadata as ODocument,
                      null, idx.propertyKeys as String[])


            } else {
              System.out.println("Index ${idx.name} already exists.")
            }

          }


        }
      }


    }

    return classMap
  }

  static Map<String, OClass> addEdgeLabels(OrientStandardGraph graph, def json, StringBuffer sb = null) {

    Map<String, OClass> classMap = new HashMap<>()
    json['edgeLabels'].each {
      final String name = it.name
      final OClass oClass = createEdgeLabel(graph, name)
      classMap.put(name, oClass)
      sb?.append("Success added vertext label - $name\n")

      json['propertyKeys'].each { prop ->
        if (prop.name && prop.dataType && (prop.name as String).startsWith(name)) {
          OProperty oProperty = createProp(oClass, prop.name as String, Class.forName(prop.dataType as String))

        }
      }

      json['edgeIndexes'].each { idx ->
        if (idx.name && idx.propertyKeys && (idx.name as String).startsWith(name)) {

          if (idx.composite) {
            oClass.createIndex(idx.name as String, OClass.INDEX_TYPE.FULLTEXT.toString(), ids.propertyKeys as String[])

          }
          if (idx.mixedIndex == "search") {
            oClass.createIndex(idx.name as String, OClass.INDEX_TYPE.FULLTEXT.toString(), null as OProgressListener,
                    null as ODocument,
                    "LUCENE", ids.propertyKeys as String[])
          }


        }
      }


    }
    return classMap
  }

  static OProperty createProp(OClass oClass, String keyName, Class<?> classType) {


    try {
      OType oType = OType.getTypeByClass(classType)
      System.out.println("keyName = ${keyName}, classType = ${classType?.toString()} oType = ${oType?.toString()}")

      OProperty prop = oClass.getProperty(keyName)
      if (prop == null) {
        prop = oClass.createProperty(keyName, oType)

      }

      return prop

    }
    catch (Throwable t) {
      t.printStackTrace()
    }
    return null
  }


  static OClass createVertexLabel(OrientStandardGraph graph, String labelName) {

    try {
      String className = graph.createVertexClass(labelName)
      OClass oClass = graph.getRawDatabase().getClass(className)


      createProp(oClass, "Metadata_Type_" + labelName, String.class)


      return oClass


    }
    catch (Throwable t) {
      t.printStackTrace()
    }
    return null
  }

  static OClass createEdgeLabel(OrientStandardGraph graph, String labelName) {

    try {
      String className = graph.createEdgeClass(labelName)
      OClass oClass = graph.getRawDatabase().getClass(className)

      createProp(oClass, "Metadata_Type_" + labelName, String.class)

      return oClass


    }
    catch (Throwable t) {
      t.printStackTrace()
    }
    return null
  }
}


class PontusJ2ReportingFunctions {

  static boolean isASCII(String s) {
    for (int i = 0; i < s.length(); i++)
      if (s.charAt(i) > 127)
        return false
    return true
  }

  static def getSelfDiscoveryGrid(def pg_vid, String queryDir, String pg_edgeType, String pg_type, String pg_orderCol,
                                  Integer pg_orderDir, Long pg_from, Long pg_to) {
    HashSet<String> headers = new HashSet<>()
    StringBuffer sb = new StringBuffer()

    long topCounter = 0
    sb.append('{ "data":[')


    def gridData = App.g.V(pg_vid)
    gridData = (queryDir == '<-' ?
            gridData.inE(pg_edgeType).outV() :
            gridData.outE(pg_edgeType).inV())

    gridData = gridData
            .has('Metadata_Type_' + pg_type, P.eq(pg_type))
            .order()

    gridData = (pg_orderCol == null ?
            gridData.by('id') :
            gridData.by(pg_orderCol.toString(), pg_orderDir == (1) ? Order.asc : Order.desc))

    gridData.range(pg_from, pg_to)
            .match(
                    __.as('data').id().as('id')
                    , __.as('data').valueMap().as('valueMap')
            )
            .select('id', 'valueMap')
            .each { it ->
              if (topCounter > 0) {
                sb.append(',')
              }
              topCounter++
              def tmpId = it.get('id').toString()
              tmpId = tmpId.startsWith('v') ? tmpId.substring(2, tmpId.size() - 1) : tmpId
              sb.append('{ "index":"').append(tmpId).append('"')

              it.get('valueMap').each { String key, val ->
                if ("Event_Ingestion_Business_Rules" != key) {
                  sb.append(', "').append(key).append('":')
                  headers.add(key)
                  if (val.size() == 1) {
                    def rawVal = val[0]
                    if (rawVal instanceof String || rawVal instanceof Date) {
                      sb.append('"').append(rawVal.toString().replaceAll('["]', "'")).append('"')
                    } else {
                      sb.append(rawVal)
                    }

                  } else {
                    sb.append('[')
                    int counter = 0
                    val.each { rawVal ->
                      if (counter > 0) {
                        sb.append(',')
                      }
                      counter++
                      if (rawVal instanceof String || rawVal instanceof Date) {
                        sb.append('"').append(rawVal.toString().replaceAll('["]', "'")).append('"')
                      } else {
                        sb.append(rawVal)

                      }


                    }
                    sb.append(']')

                  }


                }
              }
              sb.append('}')

            }


    topCounter = 0
    sb.append('],  "cols":[')
    headers.each { it ->
      if (topCounter > 0) {
        sb.append(',')
      }
      topCounter++
      sb.append('{ "id":"').append(it).append('"')
              .append(', "name":"')
              .append(it.replace('Metadata_', '').replace(pg_type + '_', '')
                      .replaceAll('[_.]', ' '))
              .append('"')
              .append(', "field":"').append(it).append('"}')
    }
    sb.append('] }')

    sb.toString()
  }

  static def renderReportInTextPt(String pg_id, String reportType = 'DSAR', GraphTraversalSource g = App.g) {
    return renderReportInTextPt(new ORecordId(pg_id), reportType, g)
  }

  static def renderReportInTextPt(ORID pg_id, String reportType = 'DSAR', GraphTraversalSource g = App.g) {
    def template = g.V().has('Object_Notification_Templates_Types', P.eq('Person_Natural'))
            .has('Object_Notification_Templates_Label', P.eq(reportType))
            .values('Object_Notification_Templates_Text').next() as String
    if (template) {

      // def template = g.V().has('Object_Notification_Templates_Types',eq(label)).next() as String
      def context = g.V(pg_id).valueMap()[0].collectEntries { key, val ->
        [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }

      def neighbours = g.V(pg_id).both().valueMap().toList().collect { item ->
        item.collectEntries { key, val ->
          [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
        }
      }

      def allData = new HashMap<>()

      allData.put('context', context)
      allData.put('connected_data', neighbours)


      return PontusJ2ReportingFunctions.jinJava.render(new String(template.decodeBase64()), allData).toString()
    }
    return "Failed to render data"
  }

//  static String translateValue (def val){
//    if (typeof (val) == Date.class){
//      return dateLocaleFormat(val);
//    }
//    return val.toString() - '[' - ']'
//  }

  static def renderReportInText(ORID pg_id, String reportType = 'SAR Read', GraphTraversalSource g = App.g) {

    if (new File("/orientdb/conf/i18n_pt_translation.json").exists()) {
      return renderReportInTextPt(pg_id, reportType, g)
    }

    def template = g.V().has('Object_Notification_Templates_Types', P.eq('Person_Natural'))
            .has('Object_Notification_Templates_Label', P.eq(reportType))
            .values('Object_Notification_Templates_Text').next() as String
    if (template) {

      // def template = g.V().has('Object_Notification_Templates_Types',eq(label)).next() as String
      def context = g.V(pg_id).valueMap()[0].collectEntries { key, val ->
        [key.replaceAll('[.]', '_'), val.toString() - '[' - ']'] // translateValue
      }

      def neighbours = g.V(pg_id).both().valueMap().toList().collect { item ->
        item.collectEntries { key, val ->
          [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
        }
      }

      def allData = new HashMap<>()

      allData.put('context', context)
      allData.put('connected_data', neighbours)

      return PontusJ2ReportingFunctions.jinJava.render(new String(template.decodeBase64()), allData).toString()
    }
    return "Failed to render data"
  }

  static def getProbabilityOfPossibleMatches(String startVertexId, Map<String, Double> weightsPerVertex) {
    return getProbabilityOfPossibleMatches(new ORecordId(startVertexId), weightsPerVertex)
  }

  static def getProbabilityOfPossibleMatches(ORID startVertexId, Map<String, Double> weightsPerVertex) {

    String vertType = App.g.V(startVertexId).label().next()

    def weightedScores = new HashMap<ORID, Double>()
    def labelsForMatch = new HashMap<ORID, StringBuffer>()

    Double totalScore = 0

    App.g.V(startVertexId).both().label().each { String label ->
      totalScore += weightsPerVertex.get(label, new Double(0))
    }

    App.g.V(startVertexId)
            .both().bothE()
            .filter(bothV()
                    .has("Metadata_Type_${vertType}", P.eq(vertType))
                    .id().not(__.is(startVertexId))).path()
            .each { path ->

              path.objects().each { obj ->
                if (obj instanceof Edge) {

                  int counter = 0
//            final def bothVertices = obj.bothVertices();

                  def vertices = []

                  obj.bothVertices().each { v ->
                    vertices.push(v)

                  }


                  vertices.each { v ->

                    if (vertType == v.label()) {
                      ORID currVid = v.id() as ORID
                      def currScore = weightedScores.get(currVid, new Double(0))
                      // def listOfPaths = perUserVertices.computeIfAbsent(v.id(), s -> [] )
                      int vertIdx = (counter == 0) ? 1 : 0
                      String label = vertices.get(vertIdx).label()
                      Double scoreForLabel = weightsPerVertex.get(label, new Double(0))

                      if (scoreForLabel > 0) {
                        StringBuffer currPath = labelsForMatch.get(currVid, new StringBuffer())
                        if (currPath.length() > 0) {
                          currPath.append(', ')
                        }
                        currPath.append(translate(label.replaceAll("[_|\\.]", " ")))
                        labelsForMatch.put(currVid, currPath)
                      }
                      currScore += scoreForLabel / totalScore
                      weightedScores.put(currVid, currScore)

                    }
                    counter++
                  }
                }
                // }
              }
            }


    return [weightedScores, labelsForMatch]

  }


  static Map<Map<String, String>, Double> possibleMatchesMap(String pg_id, Map<String, Double> weightsPerVertex) {
    def (Map<ORID, Double> probs, Map<ORID, StringBuffer> labelsForMatch) =
    getProbabilityOfPossibleMatches(new ORecordId(pg_id), weightsPerVertex)

    Map<Map<String, String>, Double> retVal = new HashMap<>()
    probs.each { vid, prob ->
      Map<String, String> context = App.g.V(vid).valueMap()[0].collectEntries { key, val ->
        [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
      context.put('Labels_For_Match', labelsForMatch.get(vid).toString())
      retVal.put(context, prob)
    }

    return retVal
  }

  static Map<Map<String, String>, Double> possibleMatches(String pg_id, String weightsPerServer) {


    Map<String, Double> weights =
            new ObjectMapper().readValue(weightsPerServer, Map.class)

    return possibleMatchesMap(pg_id, weights)
  }

  static Map<String, String> context(String pg_id) {
    def context = App.g.V(new ORecordId(pg_id)).elementMap()[0].collectEntries { key, val ->
      [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
    }
    return context
  }

  static List<Map<String, String>> neighbours(String pg_id) {
    def neighbours = App.g.V(new ORecordId(pg_id)).both().elementMap().toList().collect { item ->
      item.collectEntries { key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
    }

    return neighbours

  }

  static String liaScore(String lia_id) {
    
//    pg_id starting point is always LIA

    def lia = App.g.V(new ORecordId(lia_id)).elementMap()[0]

    //      RoPA.each {

//    String queryStrLIAStrategy = "SELECT * FROM :pg_id WHERE `Object_Legitimate_Interests_Assessment_Strategic_Impact.length()` > 0"
    def liaStrategicImpactScore = (lia?.get("Object_Legitimate_Interests_Assessment_Strategic_Impact")?.toString()?.length() > 0)?5:10

    String queryStrLIAEthics = "SELECT COUNT(*) FROM `Object_Legitimate_Interests_Assessment` WHERE `Object_Legitimate_Interests_Assessment_Ethical_Impact.length()` > 0"

    String queryStrLIAEssential = "SELECT COUNT(*) FROM `Object_Legitimate_Interests_Assessment` WHERE `Object_Legitimate_Interests_Assessment_Is_Essential` = true"

    String queryStrLIABreachJustification = "SELECT COUNT(*) FROM `Object_Legitimate_Interests_Assessment` WHERE `Object_Legitimate_Interests_Assessment_Breach_Of_Subject_Rights_Justification.length()` > 0"

    String queryStrRopaSensitiveData = "SELECT COUNT (*) FROM `Object_Data_Procedures` WHERE `out('Has_Sensitive_Data').size()` > 0"

    String queryStrRopaTypePerson = "SELECT COUNT (*) FROM `Object_Data_Procedures` WHERE `Object_Data_Procedures_Type_Of_Natural_Person` = 'COLABORADOR' OR `Object_Data_Procedures_Type_Of_Natural_Person` = 'FORNECEDOR'"

    String queryStrLIADataOrigin = "SELECT COUNT(*) FROM `Object_Legitimate_Interests_Assessment` WHERE `Object_Legitimate_Interests_Assessment_Is_Data_From_Natural_Person` = true"

//      }

    return ""

  }

  static Pattern sqlPattern =
          Pattern.compile(".*(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|UPDATE|UNION( +ALL){0,1})(\\s).*")

  static Boolean testQuery(String sqlQuery){

    return sqlPattern.matcher(sqlQuery).matches()
    // def matcher = (sqlQuery.toUpperCase() ==~ /(?:INSERT|ALTER|CREATE|EXEC|UPDATE|WITH|DELETE)(?:[^;']|(?:'[^']+'))/)

    // return matcher;
  }

  static List<Map<String, Object>> runSql(String sqlQuery, Map params) {
    List<Map<String, Object>> retVal = new LinkedList<>()

    def matcher = testQuery(sqlQuery.toUpperCase())

    if (!matcher) {

      App.graph.executeSql(sqlQuery,
              params).getRawResultSet().each {


        Map<String, Object> item = new HashMap<>()
        it.propertyNames.each {propName->
           item.put(propName, it.getProperty(propName))
        }
        retVal.add(item);
      }
    }
    return retVal
  }
  static List<Map<String,Object>> getConsentDataProceduresWithoutEvents(String consentDataCsv){
    return runSql(
            """
        SELECT *  FROM  Object_Data_Procedures where 
         (out('Has_Lawful_Basis_On').Object_Lawful_Basis_Description.removeAll(:con).size() ) != (out('Has_Lawful_Basis_On').Object_Lawful_Basis_Description.size() )    
         AND (in('Consent').Metadata_Type_Object_Data_Procedures.removeAll(null).size() ) = 0    
     """,
            ['con':consentDataCsv.split(',')]
    )
  }
  static String getPolicyText(String policyType) {
    try {
      OGremlinResultSet resultSet =
              App.graph.executeSql(
                      """
        SELECT Object_Policies_Text  as ct  FROM  Object_Policies where
        Object_Policies_Type = :pt
        """,
                      ['pt': policyType])

      String text = resultSet.getRawResultSet().next().getProperty('ct')

      resultSet.close()
//              App.g.V().has("Object_Policies_Type", P.eq(policyType))
//              .values("Object_Policies_Text").next().toString()
      return text;
    } catch (Throwable t) {
      return null;
    }

  }

  static List<Map<String, String>> getDsarRopaByLawfulBasis(String dsarId, String lawfulBasis) {

    try {


      def ropaList = App.g.V(dsarId).out('Has_DSAR')
              .out('Has_DSAR').as('RoPA').out('Has_Lawful_Basis_On')
              .has('Object_Lawful_Basis_Description', P.eq(lawfulBasis))
              .select('RoPA').valueMap().toList().collect { item ->
        item.collectEntries { key, val ->
          [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
        }
      } as List<Map<String, String>>;


      return ropaList
    } catch (Throwable t) {
      return [];
    }

  }

//  Formats string Date to local country/language using ISO639-2 Country Code and Language Code
//  Check the table at src/test/resources/country_date_formats.csv
  static String dateLocaleFormat(String date, String lang, String country) {

    //  If date is today, then it will be formatted to present
    if (date == 'today') {
      date = new Date().toString();
    }

    Date d = PVConvMixin.asType(date, Date.class) as Date
//  TODO: new parameter to option the DateFormat = LONG, SHORT, MEDIUM
    DateFormat dtf = DateFormat.getDateInstance(DateFormat.LONG, new Locale(lang, country))
    return dtf.format(d) as String

  }

  static String removeSquareBrackets(String orig) {

    try {
      return orig - '[' - ']'
    } catch (Throwable t) {
      return orig;
    }

  }

  static List<Map<String, String>> neighboursByType(String pg_id, String edgeType) {
    def neighbours = App.g.V(new ORecordId(pg_id)).both(edgeType).elementMap().toList().collect { item ->
      item.collectEntries { key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
    }

    return neighbours

  }

  static String htmlTableCustomHeader(Map<String, String> map, String tableHeader, String tableFooter) {
    StringBuilder htmlBuilder = new StringBuilder()
    htmlBuilder.append(tableHeader)

    htmlBuilder.append(htmlRows(map))
    htmlBuilder.append(tableFooter)

    return htmlBuilder.toString()
  }

  static String htmlRows(Map<String, String> map, String rowsCss = "border: 1px solid #dddddd;text-align: left;padding: 8px;") {
    StringBuilder htmlBuilder = new StringBuilder()
    for (Map.Entry<String, String> entry : map.entrySet()) {
      htmlBuilder.append(String.format("<tr style='${rowsCss}'><td style='${rowsCss}'>%s</td><td style='${rowsCss}'>%s</td></tr>\n",
              translate(entry.getKey().replaceAll("_", " ")), entry.getValue()))
    }

    return htmlBuilder.toString()
  }

  static String htmlTable(Map<String, String> map) {
    htmlTableCustomHeader(map,
            "<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>" +
                    translate("Name") +
                    "</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>" +
                    translate("Value") +
                    "</th></tr>",
            "</table>")
  }

  static String jsonToHtmlTable(String json) {
    Map<String, String> jsonMap =
            new ObjectMapper().readValue(json, Map.class)

    htmlTableCustomHeader(jsonMap,
            "<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Name</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Value</th></tr>",
            "</table>")
  }

  static Map jsonToMap(String json) {
    return new ObjectMapper().readValue(json, Map.class)

  }


  static List<Map<String, String>> getDeptForDataSources(String dataSourceId) {
    return App.g.V(new ORecordId(dataSourceId))
            .in('Has_Privacy_Impact_Assessment')
            .filter(__.label().is('Object_Data_Source'))
            .out('Has_Ingestion_Event')
            .out('Has_Ingestion_Event')
            .in('Has_Ingestion_Event')
            .filter(__.label().is('Person_Natural'))
            .valueMap(true)
            .toList().collect({ item ->
//      .collect { item ->
      item.collectEntries({ key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      })
    } as Closure<Map<String, String>>)
  }

  static List<Map<String, String>> getDataSourcesForLawfulBasis(String lawfulBasisId) {
    def retVal = App.g.V(new ORecordId(lawfulBasisId))
            .in()
            .in()
            .has('Metadata_Type_Object_Privacy_Impact_Assessment', P.eq('Object_Privacy_Impact_Assessment'))
            .in()
            .has('Metadata_Type_Object_Data_Source', P.eq('Object_Data_Source'))
            .elementMap().toList().collect { item ->
      item.collectEntries { key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
    }

    return retVal as List<Map<String, String>>

  }


  static Long getNumNaturalPersonForLawfulBasis(String lawfulBasisId) {
    return App.g.V(new ORecordId(lawfulBasisId))
            .in()
            .in()
            .has('Metadata_Type_Object_Privacy_Impact_Assessment', P.eq('Object_Privacy_Impact_Assessment'))
            .both()
            .has('Metadata_Type_Object_Privacy_Notice', P.eq('Object_Privacy_Notice'))
            .in()
            .has('Metadata_Type_Event_Consent', P.eq('Event_Consent'))
            .in()
            .dedup()
            .count()
            .next()
  }

  static Long getNumNaturalPersonForPIA(String piaId) {
    return App.g.V(new ORecordId(piaId))
            .in('Has_Privacy_Impact_Assessment')
            .filter(__.has('Metadata_Type_Object_Data_Source', P.eq('Object_Data_Source')))
            .out('Has_Ingestion_Event')
            .out('Has_Ingestion_Event')
            .in('Has_Ingestion_Event')
            .filter(__.has('Metadata_Type_Person_Natural', P.eq('Person_Natural')))
            .count()
            .next()


  }


  static Long getNumNaturalPersonForDataProcess(String processId) {
    return App.g.V(new ORecordId(processId))
            .out('Has_Data_Source') // Data Source
            .out('Has_Ingestion_Event')  // Event Group
            .out('Has_Ingestion_Event') // Event Ingestion
            .in('Has_Ingestion_Event')
            .filter(__.has('Metadata_Type_Person_Natural', P.eq('Person_Natural')))
            .dedup()
            .count()
            .next()


  }


//  | Data Policy Type | Data Policy Frequency | Data Policy Retention Period|

  static List<Map<String, String>> getDataPoliciesForDataProcess(String processId) {
    def ret = App.g.V(new com.orientechnologies.orient.core.id.ORecordId(processId))
            .out('Has_Data_Source') // Data Source
            .out('Has_Policy')  // Object_Data_Policy
            .dedup()
            .elementMap().toList().collect { item ->
      item.collectEntries { key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
    }

    return ret as List<Map<String, String>>

  }

  static Integer getHiMedLowNumber(String hml) {
    String lcHML = hml.toLowerCase()

    if (lcHML.startsWith("h") || lcHML.startsWith("a")) {
      return 15
    } else if (lcHML.startsWith("m")) {
      return 10
    }
    return 5
  }

  static String getRiskLevelColour(String riskLevelNumStr) {

    if (!riskLevelNumStr) {
      return 'blue'
    }
    Integer riskLevelNum = Integer.parseInt(riskLevelNumStr)
    if (riskLevelNum >= 150) {
      return 'red'
    }
    if (riskLevelNum <= 50) {
      return 'green'
    }
    return 'orange'
  }

  static List<Map<String, String>> getRisksForDataProcess(String processId) {
    def ret = App.g.V(new ORecordId(processId))
            ?.out('Has_Data_Source')
            ?.out('Has_Risk')
            ?.dedup()
            ?.elementMap()?.toList()?.collect { item ->
      item.collectEntries { key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }
    }


    for (Map<String, String> entry : ret as List<Map<String, String>>) {
      String prob = entry.get('Object_Risk_Data_Source_Probability')
      String imp = entry.get('Object_Risk_Data_Source_Impact')
      if (prob && imp) {
        Integer probNum = PontusJ2ReportingFunctions.getHiMedLowNumber(prob)
        Integer impNum = PontusJ2ReportingFunctions.getHiMedLowNumber(imp)
        Integer riskNum = probNum * impNum
        entry.put('Object_Risk_Data_Source_Probability_Num', probNum.toString())
        entry.put('Object_Risk_Data_Source_Impact_Num', impNum.toString())
        entry.put('Object_Risk_Data_Source_Risk_Level_Num', riskNum.toString())
      }
      String probRes = entry.get('Object_Risk_Data_Source_Residual_Probability')
      String impRes = entry.get('Object_Risk_Data_Source_Residual_Impact')
      if (probRes && impRes) {
        Integer probNum = PontusJ2ReportingFunctions.getHiMedLowNumber(probRes)
        Integer impNum = PontusJ2ReportingFunctions.getHiMedLowNumber(impRes)
        Integer riskNum = probNum * impNum
        entry.put('Object_Risk_Data_Source_Residual_Probability_Num', probNum.toString())
        entry.put('Object_Risk_Data_Source_Residual_Impact_Num', impNum.toString())
        entry.put('Object_Risk_Data_Source_Residual_Risk_Level_Num', riskNum.toString())
      }


    }

    return ret as List<Map<String, String>>

  }

  static List<Map<String, String>> getRiskMitigationsForRisk(String riskId) {
    def ret = App.g.V(new ORecordId(riskId))
            ?.in('Mitigates_Risk')
            ?.dedup()
            ?.elementMap()?.toList()?.collect { item ->
      item?.collectEntries { key, val ->
        [key?.toString()?.replaceAll('[.]', '_'), val?.toString() - '[' - ']']
      }
    }


    return ret as List<Map<String, String>>

  }

  static String getRiskMitigationsForRiskAsHTMLTable(String riskId, String style) {
    List<Map<String, String>> riskMigitations = getRiskMitigationsForRisk(riskId)
    StringBuilder sb = new StringBuilder("<table style='${style ?: 'border=1px;'}'>")
    for (Map<String, String> riskMitigation : riskMigitations) {
//      sb.append("<tr><td>${riskMitigation.get('Object_Risk_Mitigation_Data_Source_Mitigation_Id')}</td>")
      sb.append("<tr><td>${riskMitigation.get('Object_Risk_Mitigation_Data_Source_Description')}</td></tr>")
    }

    sb.append("</table>")


    return sb.toString()

  }

  static String getEnv(String envVar) {
    return System.getenv(envVar)
  }

  static String formatDateNow(String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)

    return formatter.format(LocalDate.now())

  }

//  Formats string Date to local country/language using ISO639-2 Country Code and Language Code
//  Check the table at src/test/resources/country_date_formats.csv
  static String formatLocaleDateNow(String pattern, String lang, String country) {

    Locale locale = new Locale.Builder().setLanguage(lang).setRegion(country).build()
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale)

    return formatter.format(LocalDate.now())

  }

  static String getEnvVarDefVal(String envVar, String defVal) {
    return getEnv(envVar) ?: defVal
  }

  static String getEnvVar(String envVar) {
    return getEnv(envVar)
  }

  static String getDataProceduresPerPerson(String userId) {
    return App.g.V(new ORecordId(userId))

            .out('Has_Ingestion_Event')
            .in('Has_Ingestion_Event')
            .filter(__.has('Metadata_Type_Event_Group_Ingestion', P.eq('Event_Group_Ingestion')))
            .in('Has_Ingestion_Event')
            .filter(__.has('Metadata_Type_Object_Data_Source', P.eq('Object_Data_Source')))
            .in('Has_Data_Source')
            .dedup()
            .elementMap().toList().collect { item ->
      item.collectEntries { key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString() - '[' - ']']
      }


    }
  }

  static Long getNumSensitiveInfoForPIA(String piaId) {
    return App.g.V(new ORecordId(piaId))
            .in('Has_Privacy_Impact_Assessment')
            .filter(__.label().is('Object_Data_Source'))
            .out('Has_Ingestion_Event')
            .out('Has_Ingestion_Event')
            .both('Has_Ingestion_Event')
            .filter(__.has('Metadata_Type_Object_Sensitive_Data', P.eq('Object_Sensitive_Data')))
            .count()
            .next()

  }

  static String businessRulesTable(String json) {


    StringBuffer sb = new StringBuffer("<table style='margin: 2px; padding: 5px;'>")
            .append("<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
            .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Name")).append("</th>")
            .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Match Weight")).append("</th>")
            .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Exclude From Search")).append("</th>")
            .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Exclude From Subsequence Search")).append("</th>")
            .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Exclude From Update")).append("</th>")
            .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Operation")).append("</th>")
            .append("<th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>").append(translate("Value")).append("</th>")
            .append("</tr>")

    Map br = jsonToMap(json)

//    System.out.println('Before loop');

    br.each { key, listEntries ->

      if (listEntries instanceof List && listEntries.size() > 0) {

        listEntries.each { innerMap ->
          sb.append("<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")

//        def innerMap = map[0]

//      System.out.println("key = ${key}, map[0] = ${innerMap} map.size() = ${map.size()}; map[0].size = ${innerMap.size()}; ${map.class} innerMap.class = ${innerMap.class}");

//      innerMap.each { it ->
//        System.out.println("it = ${it}; it.class = ${it.class}");
//        System.out.println("it.key = ${it.key}; it.val = ${it.value}");
//      }
//
          String mainValue = null
          innerMap.each { entry ->
            if (entry.key != 'matchWeight' && entry.key != 'excludeFromSearch' &&
                    entry.key != 'excludeFromSubsequenceSearch' && entry.key != 'excludeFromUpdate' &&
                    entry.key != 'operator') {
              sb.append("<td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>")
                      .append(entry.key.toString())
                      .append("</td>")
              mainValue = entry.value.toString()

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
      }
    }

    sb.append('</table>')

//    {{  "<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td>" | format (c.key , c.value )}}

    return sb.toString()

  }

  static Long getNumDataSourcesForPIA(String id) {
    return App.g.V(new ORecordId(id)).both().has('Metadata_Type_Object_Data_Source', P.eq('Object_Data_Source')).count().next()
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

  static String translate(String strToTranslate) {
    if (ptDictionary) {
      String retVal = ptDictionary.get(strToTranslate)
      if (!retVal) {
        System.err.println("failed to find translation conf/i18n_pt_translation.json: " + strToTranslate)
        return strToTranslate
      } else {
        return retVal
      }
    }
    return strToTranslate
  }

  static String renderReportInBase64(String pg_id, String pg_templateTextInBase64, GraphTraversalSource g = App.g) {
    return pg_id ? renderReportInBase64(new ORecordId(pg_id), pg_templateTextInBase64, g) :
            renderReportInBase64(null, pg_templateTextInBase64, g);
  }

  static String renderReportInBase64(ORID pg_id, String pg_templateTextInBase64, GraphTraversalSource g = App.g) {

    def allData = new HashMap<>()

    if (pg_id) {
      String vertType = App.g.V(pg_id).label().next()

      def context = App.g.V(pg_id).elementMap()[0].collectEntries { key, val ->
        [key.toString().replaceAll('[.]', '_'), val.toString().startsWith('[') ? val.toString().substring(1, val.toString().length() - 1) : val.toString()]
      }

      def neighbours = App.g.V(pg_id).both().elementMap().toList().collect { item ->
        item.collectEntries { key, val ->
          [key.toString().replaceAll('[.]', '_'), val.toString().startsWith('[') ? val.toString().substring(1, val.toString().length() - 1) : val.toString()]
        }
      }

      allData.put('context', context)
      allData.put('connected_data', neighbours)

      if ('Event_Data_Breach' == vertType) {
        GraphTraversal impactedDataSourcesTrav = App.g.V(pg_id).out('Impacted_By_Data_Breach').dedup()
//              .both().has("Metadata_Type_Object_AWS_Instance", P.eq('Object_AWS_Instance'))
//              .bothE('Runs_On').outV().dedup()

        GraphTraversal dsTravClone = impactedDataSourcesTrav.clone()
        GraphTraversal dsTravClone2 = impactedDataSourcesTrav.clone()
        GraphTraversal dsTravClone3 = impactedDataSourcesTrav.clone()


        def impactedServers = dsTravClone2
                .out('Has_Module').dedup()
//              .has("Metadata_Type_Object_AWS_Instance", P.eq('Object_AWS_Instance'))
                .valueMap().toList().collect { item ->
          item.collectEntries { key, val ->
            [key.replaceAll('[.]', '_'), val.toString().substring(1, val.toString().length() - 1)]
          }
        }
        def impactedDataSources = impactedDataSourcesTrav.valueMap().toList().collect { item ->
          item.collectEntries { key, val ->
            [key.replaceAll('[.]', '_'), val.toString().substring(1, val.toString().length() - 1)]
          }
        }
        def impactedPeople = dsTravClone
                .out("Has_Ingestion_Event")
                .out("Has_Ingestion_Event")
                .in("Has_Ingestion_Event")
                .has("Metadata_Type_Person_Natural", P.eq('Person_Natural'))
                .dedup()
                .valueMap()
                .toList()
                .collect { item ->
                  item.collectEntries { key, val ->
                    [key.replaceAll('[.]', '_'), val.toString().substring(1, val.toString().length() - 1)]
                  }
                }
        def impactedPeople2 = dsTravClone3
                .out("Has_Ingestion_Event")
                .out("Has_Ingestion_Event")
                .out("Has_Ingestion_Event")
                .has("Metadata_Type_Person_Natural", P.eq('Person_Natural'))
                .dedup()
                .valueMap()
                .toList()
                .collect { item ->
                  item.collectEntries { key, val ->
                    [key.replaceAll('[.]', '_'), val.toString().substring(1, val.toString().length() - 1)]
                  }
                }

        def impactedPeopleAll  = [];
        impactedPeopleAll.addAll(impactedPeople)
        impactedPeopleAll.addAll(impactedPeople2)
        allData.put('impacted_data_sources', impactedDataSources)
        allData.put('impacted_servers', impactedServers)
        allData.put('impacted_people', impactedPeopleAll)

      }
    }


    return PontusJ2ReportingFunctions.jinJava.render(new String(pg_templateTextInBase64.decodeBase64()), allData)
            .bytes.encodeBase64().toString()

  }
  public static Jinjava jinJava
  public static JsonSlurper ptDictionarySlurper

  static def ptDictionary
  static {
    ptDictionarySlurper = new JsonSlurper()
    try {
      def inputFile = new File("/orientdb/conf/i18n_pt_translation.json")

      ptDictionary = ptDictionarySlurper.parse(inputFile.text.toCharArray())

    }
    catch (Throwable t) {
      System.err.println("failed to load conf/i18n_pt_translation.json: " + t.toString())
    }

    jinJava = new Jinjava()

//    com.pontusvision.graphutils.PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getChart",
//      com.pontusvision.graphutils.PontusJ2ReportingFunctions.class, "getChart"))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "possibleMatches",
            PontusJ2ReportingFunctions.class, "possibleMatches", String.class, String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "context",
            PontusJ2ReportingFunctions.class, "context", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "connected_data",
            PontusJ2ReportingFunctions.class, "neighbours", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "neighboursByType",
            PontusJ2ReportingFunctions.class, "neighboursByType", String.class, String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "removeSquareBrackets",
            PontusJ2ReportingFunctions.class, "removeSquareBrackets", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getDsarRopaByLawfulBasis",
            PontusJ2ReportingFunctions.class, "getDsarRopaByLawfulBasis", String.class, String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getPolicyText",
            PontusJ2ReportingFunctions.class, "getPolicyText", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "htmlTableCustomHeader",
            PontusJ2ReportingFunctions.class, "htmlTableCustomHeader", Map.class, String.class, String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "htmlRows",
            PontusJ2ReportingFunctions.class, "htmlRows", Map.class, String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "htmlTable",
            PontusJ2ReportingFunctions.class, "htmlTable", Map.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "jsonToHtmlTable",
            PontusJ2ReportingFunctions.class, "jsonToHtmlTable", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "jsonToMap",
            PontusJ2ReportingFunctions.class, "jsonToMap", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "businessRulesTable",
            PontusJ2ReportingFunctions.class, "businessRulesTable", String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getDataSourcesForLawfulBasis",
            PontusJ2ReportingFunctions.class, "getDataSourcesForLawfulBasis", String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getDeptForDataSources",
            PontusJ2ReportingFunctions.class, "getDeptForDataSources", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "dateLocaleFormat",
            PontusJ2ReportingFunctions.class, "dateLocaleFormat", String.class, String.class, String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getNumNaturalPersonForLawfulBasis",
            PontusJ2ReportingFunctions.class, "getNumNaturalPersonForLawfulBasis", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getNumNaturalPersonForPIA",
            PontusJ2ReportingFunctions.class, "getNumNaturalPersonForPIA", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getNumSensitiveInfoForPIA",
            PontusJ2ReportingFunctions.class, "getNumSensitiveInfoForPIA", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getDataProceduresPerPerson",
            PontusJ2ReportingFunctions.class, "getDataProceduresPerPerson", String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getEnvVarDefVal",
            PontusJ2ReportingFunctions.class, "getEnvVarDefVal", String.class, String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "formatDateNow",
            PontusJ2ReportingFunctions.class, "formatDateNow", String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "formatLocaleDateNow",
            PontusJ2ReportingFunctions.class, "formatLocaleDateNow", String.class, String.class, String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getEnvVar",
            PontusJ2ReportingFunctions.class, "getEnvVar", String.class))


    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getNumNaturalPersonForDataProcess",
            PontusJ2ReportingFunctions.class, "getNumNaturalPersonForDataProcess", String.class))


    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getDataPoliciesForDataProcess",
            PontusJ2ReportingFunctions.class, "getDataPoliciesForDataProcess", String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getRisksForDataProcess",
            PontusJ2ReportingFunctions.class, "getRisksForDataProcess", String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getRiskLevelColour",
            PontusJ2ReportingFunctions.class, "getRiskLevelColour", String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getRiskMitigationsForRisk",
            PontusJ2ReportingFunctions.class, "getRiskMitigationsForRisk", String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getRiskMitigationsForRiskAsHTMLTable",
            PontusJ2ReportingFunctions.class, "getRiskMitigationsForRiskAsHTMLTable", String.class, String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "t",
            PontusJ2ReportingFunctions.class, "translate", String.class))

    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "getConsentDataProceduresWithoutEvents",
            PontusJ2ReportingFunctions.class, "getConsentDataProceduresWithoutEvents", String.class))
    PontusJ2ReportingFunctions.jinJava.getGlobalContext().registerFunction(new ELFunctionDefinition("pv", "runSql",
            PontusJ2ReportingFunctions.class, "runSql", String.class, Map.class))

  }
}


class VisJSGraph {
  static getPropsNonMetadataAsHTMLTableRows(GraphTraversalSource g, ORID vid, String origLabel) {
    StringBuilder sb = new StringBuilder()
    sb.append(new JsonBuilder(g.V(vid).valueMap().next()).toString())
    return sb.toString().bytes.encodeBase64()
  }

  static getVisJsGraph(ORID pg_vid) {


    StringBuffer sb = new StringBuffer()

    Long numEdges = App.g.V(pg_vid).bothE().count().next()
    String origLabel = App.g.V(pg_vid).label().next().replaceAll('[_.]', ' ')

    if (numEdges > 15) {

      HashSet nodesSet = new HashSet()
      HashSet edgesSet = new HashSet()


      App.g.V(pg_vid).as('orig')
              .outE().match(
              __.as('e').inV().label().as('vLabel')
              // ,  __.as('e').outV().label().as('inVLabel')
              , __.as('e').label().as('edgeLabel')
      )
              .select('edgeLabel', 'vLabel')
              .groupCount().each {
        def entry = it


        entry.each {
          key, val ->


            if (key instanceof Map) {

              String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ')
              String toNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
                      ' -> (' + edgeLabel + ')'

              String edgeId = key.get('edgeLabel')
              String toNodeId = key.get('vLabel') +
                      ' -> (' + edgeId + ')'

              sb.setLength(0)

              sb.append('{ "id":"').append(toNodeId)
                      .append('","label":"').append(toNodeLabel)
                      .append('","group":"').append(toNodeLabel)
                      .append('","shape":"').append('box')
                      .append('"}\n')


              nodesSet.add(sb.toString())


              sb.setLength(0)

              sb.append('{ "from":"')
                      .append(pg_vid).append('","to":"')
                      .append(toNodeId).append('","label":"')
                      .append(edgeLabel).append(' (')
                      .append(val).append(')","value":')
                      .append(val).append('}\n')

              edgesSet.add(sb.toString())
              sb.setLength(0)

            }


        }


      }


      App.g.V(pg_vid).as('orig')
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

              String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ')

              String fromNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
                      ' <- (' + edgeLabel + ')'
              String edgeId = key.get('edgeLabel')

              String fromNodeId = key.get('vLabel') +
                      ' <- (' + edgeId + ')'
              sb.setLength(0)

              sb.append('{ "id":"').append(fromNodeId)
                      .append('","label":"').append(fromNodeLabel)
                      .append('","group":"').append(fromNodeLabel)
                      .append('","shape":"').append('box')
                      .append('"}')

              nodesSet.add(sb.toString())


              sb.setLength(0)

              sb.append('{ "from":"')
                      .append(fromNodeId).append('","to":"')
                      .append(pg_vid).append('","label":"')
                      .append(edgeLabel).append(' (')
                      .append(val).append(')","value":')
                      .append(val).append('}')
              edgesSet.add(sb.toString())
              sb.setLength(0)
            }


        }


      }
      sb.setLength(0)
      sb.append('{ "id":"').append(pg_vid)
              .append('","label":"').append(origLabel)
              .append('","group":"').append(origLabel)
//        .append('","fixed":').append(true)
              .append('","shape":"').append('image')
              .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(App.g, pg_vid, origLabel).toString())
              .append('"}')

      nodesSet.add(sb.toString())
      sb.setLength(0)

      sb.append('{ "nodes":')
              .append(nodesSet.toString()).append(', "edges":').append(edgesSet.toString())
    } else {
      int counter = 0

      try {

        sb.append('{ "nodes":[')

        App.g.V(pg_vid)
                .both()
                .dedup()
                .each {
                  String groupStr = it.label().toString()
                  String labelStr = groupStr.replaceAll('[_.]', ' ')
                  ORID vid = it.id()
                  sb.append(counter == 0 ? '{' : ',{')
                          .append('"id":"').append(vid.toString())
                          .append('","group":"').append(groupStr)
                          .append('","label":"').append(labelStr)
                          .append('","shape":"').append('image')
                          .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(App.g, vid, labelStr).toString())
                          .append('"')
                  if (pg_vid.toString() == (vid.toString())) {
//              sb.append(',"fixed":true')
                  }
                  sb.append('}')

                  counter++

                }
        App.g.V(pg_vid)  // Also get the original node
                .each {
                  String groupStr = it.label().toString()
                  String labelStr = groupStr.replaceAll('[_.]', ' ')
                  ORID vid = it.id()

                  sb.append(counter == 0 ? '{' : ',{')
                          .append('"id":"').append(vid.toString())
                          .append('","group":"').append(groupStr)
                          .append('","label":"').append(labelStr)
                          .append('","shape":"').append('image')
                          .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(App.g, vid, labelStr).toString())
                          .append('"')
                  if (pg_vid.toString() == (vid.toString())) {
//              sb.append(',"fixed":true')
                  }
                  sb.append('}')

                  counter++

                }
        sb.append('], "edges":[')


        counter = 0
        App.g.V(pg_vid)
                .bothE()
                .dedup()
                .each {
                  StringBuffer prob = new StringBuffer()
                  try {
                    prob.append(' - s - ')
                            .append(
                                    Math.round(
                                            Math.min(
                                                    it.values('toScorePercent').next() as Double,
                                                    it.values('fromScorePercent').next() as Double
                                            ) * 100) / 100)
                            .append('%')

                  } catch (Throwable t) {
                    prob.setLength(0)
                  }

                  sb.append(counter == 0 ? '{' : ',{')
                          .append('"from": "').append(it.inVertex().id())
                          .append('" ,"to": "').append(it.outVertex().id())
                          .append('","label": "').append(PontusJ2ReportingFunctions.translate(it.label().toString().replaceAll('[_.]', ' ')))
                          .append(prob.toString())
                          .append('"}')

                  counter++

                }

        sb.append(']')


      } catch (Throwable t) {
        sb.append(t.toString())
      }

    }
    sb.append(', "origLabel":"').append(origLabel).append('"')
    int counter = 0
    sb.append(', "reportButtons": [')
    try {
      App.g.V()
              .has('Object_Notification_Templates_Types'
                      , eq(App.g.V(pg_vid).values('Metadata_Type').next()))
              .valueMap('Object_Notification_Templates_Label', 'Object_Notification_Templates_Text')
              .each {
                sb.append(counter > 0 ? ',{' : '{')
                counter++
                sb.append('"text":"')
                if (it.get('Object_Notification_Templates_Text') != null)
                  sb.append(it.get('Object_Notification_Templates_Text')[0].toString())
                sb.append('","label":"')
                if (it.get('Object_Notification_Templates_Label') != null)
                  sb.append(it.get('Object_Notification_Templates_Label')[0])
                sb.append('", "vid": "').append(pg_vid)

                sb.append('"}')

              }
    } catch (e) {
    }
    sb.append('] }')
    return sb.toString()
  }

  static getVisJsGraphImmediateNeighbourNodes(ORID pg_vid, StringBuffer sb, int counter, Set<ORID> nodeIds, AtomicInteger depth) {

    def types = getMetadataTypes(depth.intValue())

    types.each { type ->
      App.g.V().has("Metadata_Type_${type}", P.eq("${type}")).each {
        String groupStr = it.values('Metadata_Type').next()
        String labelStr = it.label().toString().replaceAll('[_.]', ' ')
        ORID vid = it.id() as ORID
        if (nodeIds.add(vid)) {
          sb.append(counter == 0 ? '{' : ',{')
                  .append('"id":"').append(vid)
                  .append('","level":').append(getLevel(labelStr))
                  .append(',"group":"').append(groupStr)
                  .append('","label":"').append(labelStr)
                  .append('","shape":"').append('image')
                  .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
                  .append('"')
          if (vid.equals(pg_vid)) {
//            sb.append(',"fixed":true')
          }
          sb.append('}')

          counter++

        }
      }
    }


    return counter
  }

  static getEdgeProperties(ORID fromVertexId, ORID toVertexId) {
    def mapper = GraphSONMapper.build().version(GraphSONVersion.V1_0).create().createMapper()

    def v = App.g.V(fromVertexId).bothE().filter(bothV().id().is(toVertexId)).valueMap().next()

    mapper.writeValueAsString(v)

  }

  static getVisJsGraph(ORID pg_vid, long pg_depth) {

    StringBuffer sb = new StringBuffer()

    Long numEdges = App.g.V(pg_vid).bothE().count().next()
    String origLabel = App.g.V(pg_vid).label().next().replaceAll('[_.]', ' ')

    if (numEdges > 15) {

      HashSet nodesSet = new HashSet()
      HashSet edgesSet = new HashSet()


      App.g.V(pg_vid).as('orig')
              .outE().match(
              __.as('e').inV().label().as('vLabel')
              // ,  __.as('e').outV().label().as('inVLabel')
              , __.as('e').label().as('edgeLabel')
      )
              .select('edgeLabel', 'vLabel')
              .groupCount().each {
        def entry = it


        entry.each {
          key, val ->


            if (key instanceof Map) {

              String edgeLabel = key.get('edgeLabel')?.replaceAll('[_.]', ' ')
              String toNodeLabel = key.get('vLabel')?.replaceAll('[_.]', ' ') +
                      ' -> (' + edgeLabel + ')'

              String edgeId = key.get('edgeLabel')
              String toNodeId = key.get('vLabel') +
                      ' -> (' + edgeId + ')'

              sb.setLength(0)

              sb.append('{ "id":"').append(toNodeId)
                      .append('","label":"').append(toNodeLabel)
                      .append('","group":"').append(toNodeLabel)
                      .append('","shape":"').append('box')
                      .append('"}\n')


              nodesSet.add(sb.toString())


              sb.setLength(0)

              sb.append('{ "from":"')
                      .append(pg_vid).append('","to":"')
                      .append(toNodeId).append('","label":"')
                      .append(edgeLabel).append(' (')
                      .append(val).append(')","value":')
                      .append(val).append('}\n')

              edgesSet.add(sb.toString())
              sb.setLength(0)

            }


        }


      }


      App.g.V(pg_vid).as('orig')
              .inE().match(
              __.as('e').outV().label().as('vLabel')
              // ,  __.as('e').outV().label().as('inVLabel')
              , __.as('e').label().as('edgeLabel')
      )
              .select('edgeLabel', 'vLabel')
              .groupCount().each { it ->
        it.each {
          key, val ->
            if (key instanceof Map) {

              String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ')

              String fromNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
                      ' <- (' + edgeLabel + ')'
              String edgeId = key.get('edgeLabel')

              String fromNodeId = key.get('vLabel') +
                      ' <- (' + edgeId + ')'
              sb.setLength(0)

              sb.append('{ "id":"').append(fromNodeId)
                      .append('","label":"').append(fromNodeLabel)
                      .append('","group":"').append(fromNodeLabel)
                      .append('","shape":"').append('box')
                      .append('"}')

              nodesSet.add(sb.toString())


              sb.setLength(0)

              sb.append('{ "from":"')
                      .append(fromNodeId).append('","to":"')
                      .append(pg_vid).append('","label":"')
                      .append(edgeLabel).append(' (')
                      .append(val).append(')","value":')
                      .append(val).append('}')
              edgesSet.add(sb.toString())
              sb.setLength(0)
            }


        }


      }
      sb.setLength(0)
      sb.append('{ "id":"').append(pg_vid)
              .append('","label":"').append(origLabel)
              .append('","group":"').append(origLabel)
//        .append('","fixed":').append(true)
              .append(',"shape":"').append('image')
              .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(App.g, pg_vid, origLabel).toString())
              .append('"}')

      nodesSet.add(sb.toString())
      sb.setLength(0)

      sb.append('{ "nodes":')
              .append(nodesSet.toString()).append(', "edges":').append(edgesSet.toString())
    } else {
      int counter = 0

      try {

        sb.append('{ "nodes":[')

        App.g.V(pg_vid)
                .both()
                .dedup()
                .each { it ->
                  String groupStr = it.values('Metadata_Type').next()
                  String labelStr = it.label().toString().replaceAll('[_.]', ' ')
                  ORID vid = it.id()
                  sb.append(counter == 0 ? '{' : ',{')
                          .append('"id":"').append(vid)
                          .append('","group":"').append(groupStr)
                          .append('","label":"').append(labelStr)
                          .append('","shape":"').append('image')
                          .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
                          .append('"')
                  if (vid.equals(pg_vid)) {
//              sb.append(',"fixed":true')
                  }
                  sb.append('}')

                  counter++

                }
        App.g.V(pg_vid)  // Also get the original node
                .each {
                  String groupStr = it.values('Metadata_Type').next()
                  String labelStr = it.label().toString().replaceAll('[_.]', ' ')
                  ORID vid = it.id()
                  sb.append(counter == 0 ? '{' : ',{')
                          .append('"id":"').append(vid)
                          .append('","group":"').append(groupStr)
                          .append('","label":"').append(labelStr)
                          .append('","shape":"').append('image')
                          .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(g, vid, labelStr).toString())
                          .append('"')
                  if (vid.equals(pg_vid)) {
//              sb.append(',"fixed":true')
                  }
                  sb.append('}')

                  counter++

                }
        sb.append('], "edges":[')


        counter = 0
        App.g.V(pg_vid)
                .bothE()
                .dedup()
                .each {
                  StringBuffer prob = new StringBuffer()
                  try {
                    prob.append(' - s - ')
                            .append(
                                    Math.round(
                                            Math.min(
                                                    it.values('toScorePercent').next() as Double,
                                                    it.values('fromScorePercent').next() as Double
                                            ) * 100) / 100)
                            .append('%')

                  } catch (Throwable t) {
                    prob.setLength(0)
                  }


                  sb.append(counter == 0 ? '{' : ',{')
                          .append('"from": "').append(it.inVertex().id())
                          .append('" ,"to": "').append(it.outVertex().id())
                          .append('","label": "').append(it.label().toString().replaceAll('[_.]', ' '))
                          .append(prob.toString())
                          .append('"}')

                  counter++

                }

        sb.append(']')


      } catch (Throwable t) {
        sb.append(t.toString())
      }

    }
    sb.append(', "origLabel":"').append(origLabel).append('"')
    int counter = 0
    sb.append(', "reportButtons": [')
    try {
      App.g.V()
              .has('Object_Notification_Templates_Types'
                      , P.eq(App.g.V(pg_vid).values('Metadata_Type').next()))
              .valueMap('Object_Notification_Templates_Label', 'Object_Notification_Templates_Text')
              .each {
                sb.append(counter > 0 ? ',{' : '{')
                counter++
                sb.append('"text":"')
                if (it.get('Object_Notification_Templates_Text') != null)
                  sb.append(it.get('Object_Notification_Templates_Text')[0].toString())
                sb.append('","label":"')
                if (it.get('Object_Notification_Templates_Label') != null)
                  sb.append(it.get('Object_Notification_Templates_Label')[0])
                sb.append('", "vid": "').append(pg_vid)

                sb.append('"}')

              }
    } catch (e) {
    }
    sb.append('] }')
    sb.toString()
  }

  static getVisJsGraphImmediateNeighbourEdges(ORID pg_vid, StringBuffer sb, int counter, Set<String> currEdges) {


    StringBuffer localEntry = new StringBuffer()

    App.g.V(pg_vid)
            .bothE()
            .each {
              ORID from = it.inVertex().id() as ORID
              ORID to = it.outVertex().id() as ORID
              localEntry.setLength(0)

              localEntry.append(counter == 0 ? '{' : ',{')
                      .append('"from": "').append(from)
                      .append('" ,"to": "').append(to)
                      .append('","label": "').append(it.label().toString().replaceAll('[_.]', ' '))
                      .append('"}')
              String localEntryStr = localEntry.toString()

              if (currEdges.add(localEntryStr)) {
                sb.append(localEntryStr)
                counter++

              }


            }
    return counter
  }


  static getMetadataTypes(int level) {
    def metadataTypes = [
            'Event_Consent',
            'Event_Data_Breach',
            'Event_Form_Ingestion',
            'Event_Email_Msg_Group',
            'Event_Email_To_Group',
            'Event_Email_From_Group',
            'Event_Email_CC_Group',
            'Event_Email_BCC_Group',
            'Event_NLP_Group',
            'Event_File_Group_Ingestion',
            'Event_File_Ingestion',
            'Event_Email_Message',
            'Event_Group_Ingestion',
            'Event_Group_Subject_Access_Request',
            'Event_Ingestion',
            'Event_Meeting',
            'Event_Subject_Access_Request',
            'Event_Training',
            'Event_Transaction',
            'Event_Complaint',
            'Location_Address',
            'Object_AWS_Instance',
            'Object_AWS_Network_Interface',
            'Object_AWS_Security_Group',
            'Object_AWS_VPC',
            'Object_Application',
            'Object_Awareness_Campaign',
            'Object_Biometric',
            'Object_Contract',
            'Object_Campaign',
            'Object_Credential',
            'Object_Data_Policy',
            'Object_Data_Procedures',
            'Object_Data_Source',
            'Object_Data_Src_Mapping_Rule',
            'Object_Email_Address',
            'Object_Email_Message_Attachment',
            'Object_Email_Message_Body',
            'Object_Form',
            'Object_Genetic',
            'Object_Health',
            'Object_Identity_Card',
            'Object_Insurance_Policy',
            'Object_Lawful_Basis',
            'Object_Metadata_Source',
            'Object_Module',
            'Object_Notification_Templates',
            'Object_Policies',
            'Object_Phone_Number',
            'Object_Privacy_Impact_Assessment',
            'Object_Legitimate_Interests_Assessment',
            'Object_Risk_Data_Source',
            'Object_Risk_Mitigation_Data_Source',
            'Object_Privacy_Docs',
            'Object_Legal_Actions',
            'Object_Privacy_Notice',
            'Object_Salary',
            'Object_Sensitive_Data',
            'Object_Subsystem',
            'Object_System',
            'Object_Vehicle',
            'Person_Employee',
            'Person_Identity',
            'Person_Natural',
            'Person_Organisation'
    ]
    return metadataTypes.subList(0, level)
  }

  static getVisJsGraph(ORID pg_vid, int depth) {
    StringBuffer sb = new StringBuffer()

    Long numEdges = App.g.V(pg_vid).bothE().count().next()
    String origLabel = App.g.V(pg_vid).label().next().replaceAll('[_.]', ' ')
    AtomicInteger nodeDepth = new AtomicInteger(depth)

    if (numEdges > 15) {

      HashSet nodesSet = new HashSet()
      HashSet edgesSet = new HashSet()


      App.g.V(pg_vid).as('orig')
              .outE().match(
              __.as('e').inV().label().as('vLabel')
              // ,  __.as('e').outV().label().as('inVLabel')
              , __.as('e').label().as('edgeLabel')
      )
              .select('edgeLabel', 'vLabel')
              .groupCount().each {
        def entry = it


        entry.each {
          key, val ->


            if (key instanceof Map) {

              String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ')
              String toNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
                      ' -> (' + edgeLabel + ')'

              String edgeId = key.get('edgeLabel')
              String toNodeId = key.get('vLabel') +
                      ' -> (' + edgeId + ')'

              sb.setLength(0)

              sb.append('{ "id":"').append(toNodeId)
                      .append('","label":"').append(toNodeLabel)
                      .append('","group":"').append(toNodeLabel)
                      .append('","shape":"').append('box')
                      .append('"}\\n')


              nodesSet.add(sb.toString())


              sb.setLength(0)

              sb.append('{ "from":"')
                      .append(pg_vid).append('","to":"')
                      .append(toNodeId).append('","label":"')
                      .append(edgeLabel).append(' (')
                      .append(val).append(')","value":')
                      .append(val).append('}\\n')

              edgesSet.add(sb.toString())
              sb.setLength(0)

            }


        }


      }


      App.g.V(pg_vid).as('orig')
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

              String edgeLabel = key.get('edgeLabel').replaceAll('[_.]', ' ')

              String fromNodeLabel = key.get('vLabel').replaceAll('[_.]', ' ') +
                      ' <- (' + edgeLabel + ')'
              String edgeId = key.get('edgeLabel')

              String fromNodeId = key.get('vLabel') +
                      ' <- (' + edgeId + ')'
              sb.setLength(0)

              sb.append('{ "id":"').append(fromNodeId)
                      .append('","label":"').append(fromNodeLabel)
                      .append('","group":"').append(fromNodeLabel)
                      .append('","shape":"').append('box')
                      .append('"}')

              nodesSet.add(sb.toString())


              sb.setLength(0)

              sb.append('{ "from":"')
                      .append(fromNodeId).append('","to":"')
                      .append(pg_vid).append('","label":"')
                      .append(edgeLabel).append(' (')
                      .append(val).append(')","value":')
                      .append(val).append('}')
              edgesSet.add(sb.toString())
              sb.setLength(0)
            }


        }


      }
      sb.setLength(0)
      sb.append('{ "id":"').append(pg_vid)
              .append('","label":"').append(origLabel)
              .append('","group":"').append(origLabel)
//        .append('","fixed":').append(true)
              .append(',"shape":"').append('image')
              .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(App.g, pg_vid, origLabel).toString())
              .append('"}')

      nodesSet.add(sb.toString())
      sb.setLength(0)

      sb.append('{ "nodes":')
              .append(nodesSet.toString()).append(', "edges":').append(edgesSet.toString())
              .append('}')
    } else {
      int counter = 0

      try {
        Set<ORID> nodeIds = new HashSet<>()

        sb.append('{ "nodes":[')

        getVisJsGraphImmediateNeighbourNodes(pg_vid, sb, counter, nodeIds, nodeDepth)

        sb.append('], "edges":[')


        counter = 0

        Set<String> currEdges = new HashSet<>()
        nodeIds.each {
          counter = getVisJsGraphImmediateNeighbourEdges(it, sb, counter, currEdges)

        }

        sb.append(']')


      } catch (Throwable t) {
        sb.append(t.toString())
      }

      sb.toString()
    }
    sb.append(', "origLabel":"').append(origLabel).append('"')
    int counter = 0
    sb.append(', "reportButtons": [')
    try {
      App.g.V()
              .has('Object_Notification_Templates_Types'
                      , P.eq(App.g.V(pg_vid).values('Metadata_Type').next()))
              .valueMap('Object_Notification_Templates_Label', 'Object_Notification_Templates_Text')
              .each {
                sb.append(counter > 0 ? ',{' : '{')
                counter++
                sb.append('"text":"')
                if (it.get('Object_Notification_Templates_Text') != null)
                  sb.append(it.get('Object_Notification_Templates_Text')[0].toString())
                sb.append('","label":"')
                if (it.get('Object_Notification_Templates_Label') != null)
                  sb.append(it.get('Object_Notification_Templates_Label')[0])
                sb.append('", "vid": "').append(pg_vid)

                sb.append('"}')

              }
    } catch (e) {
    }

    sb.append('] }')
    return sb.toString()


  }

  static getInfraGraph(String pg_vid) {
    StringBuffer sb = new StringBuffer()
    int counter = 0

    try {

      GraphTraversal gtrav = (pg_vid == "-1") ?
              App.g.V() :
              App.g.V(new ORecordId(pg_vid)).repeat(__.outE().subgraph('subGraph').bothV())
                      .times(4).cap('subGraph').next().traversal().V()


      sb.append('{ "nodes":[')

      gtrav
              .or(
                      __.has('Metadata_Type_Object_System', P.eq('Object_System'))
                      , __.has('Metadata_Type_Object_Subsystem', P.eq('Object_Subsystem'))
                      , __.has('Metadata_Type_Object_Module', P.eq('Object_Module'))
                      , __.has('Metadata_Type_Object_Data_Source', P.eq('Object_Data_Source'))
              ).dedup()
              .each {
                String groupStr = it.values('Metadata_Type').next()
                String labelStr = it.values(groupStr + '_Name').next()
                ORID vid = (ORID) it.id()
                sb.append(counter == 0 ? '{' : ',{')
                        .append('"id":"').append(vid)
                        .append('","group":"').append(groupStr)
                        .append('","label":"').append(StringEscapeUtils.escapeJavaScript(labelStr))
                        .append('","shape":"').append('image')
                        .append('","image":"').append(getPropsNonMetadataAsHTMLTableRows(App.g, vid, labelStr).toString())
                        .append('"}')

                counter++

              }

      sb.append('], "edges":[')


      counter = 0
      gtrav = (pg_vid == "-1") ?
              App.g.V() :
              App.g.V(pg_vid).repeat(__.outE().subgraph('subGraph').bothV())
                      .times(4).cap('subGraph').next().traversal().V()


      gtrav
              .or(
                      __.has('Metadata_Type_Object_System', P.eq('Object_System'))
                      , __.has('Metadata_Type_Object_Subsystem', P.eq('Object_Subsystem'))
                      , __.has('Metadata_Type_Object_Module', P.eq('Object_Module'))
                      , __.has('Metadata_Type_Object_Data_Source', P.eq('Object_Data_Source'))
              )
              .bothE('Has_Module', 'Has_Subsystem', 'Has_System')
              .dedup().each {
        sb.append(counter == 0 ? '{' : ',{')
                .append('"from": "').append(it.inVertex().id())
                .append('" ,"to": "').append(it.outVertex().id())
                .append('","label": "').append(it.label().toString().replaceAll('[_.]', ' '))
                .append('"}')

        counter++

      }

      sb.append(']}')


    } catch (Throwable t) {
      sb.append(t.toString())
    }

    sb.toString()
  }

  static getLevel(String label) {
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
            'Object AWS Network Interface',
            'Object System',
            'Object Subsystem',
            'Object Module'
    ]
    int index = levels.findIndexOf {
      (label.equals(it))
    }

    if (index == -1) {
      return label.hashCode() % 10
    }

    return index
  }

  static getVisJsGraph(String pg_vid) {

    return VisJSGraph.getVisJsGraph(new ORecordId(pg_vid))
  }


  static getVisJsGraph(String pg_vid, int depth) {
    return VisJSGraph.getVisJsGraph(new ORecordId(pg_vid), depth)
  }

  static getVisJsGraphImmediateNeighbourNodes(String pg_vid, StringBuffer sb, int counter, Set<ORID> nodeIds, AtomicInteger depth) {

    return VisJSGraph.getVisJsGraphImmediateNeighbourNodes(new ORecordId(pg_vid), sb, counter, nodeIds, depth)


  }


  static getEdgeProperties(String fromVertexId, String toVertexId) {
    return VisJSGraph.getEdgeProperties(new ORecordId(fromVertexId), new ORecordId(toVertexId))
  }

  static getVisJsGraph(String pg_vid, long pg_depth) {

    return VisJSGraph.getVisJsGraph(new ORecordId(pg_vid), pg_depth)
  }

  static def getPropsNonMetadataAsHTMLTableRows(GraphTraversalSource g, String vid, String origLabel) {
    return VisJSGraph.getPropsNonMetadataAsHTMLTableRows(g, new ORecordId(vid), origLabel)
  }

}

class Utils {

  // signature with Strings as params
  static Long mergeVertices(String srcVid1, String targetVid2,  boolean removeRid1Vertex, boolean removeRid1Edge) {
    return mergeVertices(new ORecordId(srcVid1), new ORecordId(targetVid2), removeRid1Vertex, removeRid1Edge);

  }

  // main method
  static Long mergeVertices(ORID srcVid1, ORID targetVid2,  boolean removeRid1Vertex, boolean removeRid1Edge) {
    //Inputs:
    //  rid1(source); rid2(target); removeRid1Edge(boolean)
    /*
          A1            B1====B2
          |\____A2
          |
          A3

          A1, B1, remove=true

          B1=====B2
          |\______A2
          |
          A3

   */

    //Algorithm:
    //  for each edge in bothrids (to/from) rid1:
    //    get the edge label + the neighbour id to / from
    //    if (to is the same as Rid1):
    //      create a new edge from rid2 to the neighbour
    //    else
    //      create a new edge from neighbour to rid2
    //    endif
    //    if (removeRid1Edge):
    //      delete edge
    //    endif
    //  endfor
    //output:
    //  numberOfEdges moved/copied

    def tx = App.g.tx();

    if (!tx.isOpen()) {
      tx.open()
    }

    Long numberOfEdges = 0;

    try {

      def delEdges = [];

      App.g.V(srcVid1).bothE().each { Edge e ->
//      from the Edge's perspective, the OUT is IN and the IN turns to be OUT (just go with it) ...
        Vertex toOutV = e.inVertex()
        Vertex fromInV = e.outVertex()
        String label = e.label()

        numberOfEdges++;

        ORID fromInVRid = fromInV.id();
        ORID toOutVRid = toOutV.id();

        if (toOutVRid.equals(srcVid1)) {
          FileNLPRequest.upsertEdge(fromInVRid, targetVid2, label);
        } else {
          FileNLPRequest.upsertEdge(targetVid2, toOutVRid, label);
        }

//        TODO: maybe we'll need to add 2 else if() statements to account for the edge's direction

        if (removeRid1Edge) {
          delEdges.add(e);
        }
      }

      for (def e : delEdges) {
        App.g.E(e).drop().iterate();
      }

      if (removeRid1Vertex) {
        App.g.V(srcVid1).drop().iterate();
      }

      tx.commit()
    }
    catch (Throwable e){
      tx.rollback()
    }finally {
      tx.close()
    }

    return numberOfEdges;

  }


}

/*
Event_Group_Ingestion
Event_Ingestion

Person

Object_Email_Address
Object_Credential
Event_Form_Ingestion
Object_Identity_Card
Location_Address
Object_Insurance_Policy


Event_Consent
Object_Privacy_Notice
Object_Privacy_Impact_Assessment
Object_Lawful_Basis




Event_Subject_Access_Request
Person_Employee
Object_Awareness_Campaign
Event_Training
Event_Data_Breach

Person_Organisation

Object_Data_Procedures
Object_Form
Object_Notification_Templates
Object_AWS_Instance
Object_AWS_Security_Group
Object_AWS_Network_Interface

 */




