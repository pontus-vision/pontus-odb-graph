package com.pontusvision.graphutils

import com.google.common.util.concurrent.AtomicDouble
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.jayway.jsonpath.JsonPath
import com.joestelmach.natty.DateGroup
import com.joestelmach.natty.Parser
import com.orientechnologies.orient.core.id.ORID
import com.pontusvision.gdpr.App
import com.pontusvision.gdpr.mapping.Rules
import com.pontusvision.gdpr.mapping.VertexProps
import com.pontusvision.utils.LocationAddress
import com.pontusvision.utils.PostCode
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import groovy.text.Template
import org.apache.lucene.document.DateTools
import org.apache.tinkerpop.gremlin.orientdb.OrientStandardGraph
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResult
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.Text
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Transaction
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiPredicate
import java.util.regex.Pattern

/*
def benchmark = { closure ->
    start = System.nanoTime()
    closure.call()
    now = System.nanoTime()
    now - start
}
*/

//class PVVertex {
//    VertProp[] props
//    String label
//    String name
//    Double percentageThreshold
//
//}
//class VertProp extends com.pontusvision.com.pontusvision.graphutils.gdpr.mapping.VertexProps {
//    Class nativeType
//}

class Convert<T> {
  private from
  private to


  Convert(clazz) {
    from = clazz


  }

  static def from(clazz) {
    new Convert(clazz)
  }


  def to(clazz) {
    to = clazz
    return this
  }

  def using(closure) {
    def originalAsType = from.metaClass.getMetaMethod('asType', [] as Class[])
    from.metaClass.asType = { Class clazz ->
      if (clazz == to) {
        closure.setProperty('value', delegate)
        closure(delegate)
      } else {
        originalAsType.doMethodInvoke(delegate, clazz)
      }
    }
  }


  T fromString(String data, Class<T> requiredType, StringBuffer sb = null) {

    if (requiredType == Date.class) {
      return data as Date

    } else if (requiredType == String.class) {
      return data as T
    } else if (requiredType == Boolean.class) {
      return Boolean.valueOf(data) as T
    } else if (requiredType == Integer.class) {
      return Integer.valueOf(data) as T
    } else if (requiredType == Long.class) {
      return Long.valueOf(data) as T
    } else if (requiredType == Float.class) {
      return Float.valueOf(data) as T
    } else if (requiredType == Double.class) {
      return Double.valueOf(data) as T
    } else if (requiredType == Short.class) {
      return Short.valueOf(data) as T
    } else {
      return data as T
    }


  }
}

class PVConvMixin {


  static final def convert = StringGroovyMethods.&asType

  static Date invalidDate = new Date("01/01/1666")
  static Parser parser = new Parser()
  static {

    String.mixin(PVConvMixin)
  }

  static def asType(String self, Class cls, StringBuffer sb = null) {
    if (cls == Date) {

      List<DateGroup> dateGroup = parser.parse(self as String)
      Date retVal = null

      if (!dateGroup.isEmpty()) {
        DateGroup dg = dateGroup.get(0)

        boolean isTimeInferred = dg.isTimeInferred()

        List<Date> dates = dg.getDates()

        sb?.append("\n\nConverting data $self; found $dates")
        dates.each {

          retVal = it
          if (isTimeInferred) {
            Calendar calendar = Calendar.getInstance()
            calendar.setTime(retVal)
            calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("GMT")))
            calendar.set(Calendar.HOUR_OF_DAY, 1)
            calendar.set(Calendar.MINUTE, 1)
            calendar.set(Calendar.SECOND, 1)
            calendar.set(Calendar.MILLISECOND, 0)
            retVal = calendar.getTime()

          }
        }
        if (retVal == null) {
          sb?.append("\nCould not find a conversion for this date $self")

          return null
        }
      }
      return retVal


    } else if (cls == PostCode) {
      return new PostCode(self)
    } else return convert(self, cls)
  }


}


class EdgeRequest {

  String label
  String fromVertexName
  String toVertexName

  EdgeRequest(String label, String fromVertexName, String toVertexName) {
    this.label = label
    this.fromVertexName = fromVertexName
    this.toVertexName = toVertexName
  }

  String getLabel() {
    return label
  }

  void setLabel(String label) {
    this.label = label
  }

  String getFromVertexName() {
    return fromVertexName
  }

  void setFromVertexName(String fromVertexName) {
    this.fromVertexName = fromVertexName
  }

  String getToVertexName() {
    return toVertexName
  }

  void setToVertexName(String toVertexName) {
    this.toVertexName = toVertexName
  }

  boolean equals(o) {
    if (this.is(o)) return true
    if (!(o instanceof EdgeRequest)) return false

    EdgeRequest that = (EdgeRequest) o

    if (fromVertexName != that.fromVertexName) return false
    if (label != that.label) return false
    if (toVertexName != that.toVertexName) return false

    return true
  }

  int hashCode() {
    int result
    result = (label != null ? label.hashCode() : 0)
    result = 31 * result + (fromVertexName != null ? fromVertexName.hashCode() : 0)
    result = 31 * result + (toVertexName != null ? toVertexName.hashCode() : 0)
    return result
  }

  String toString() {
    return "${label} = ($fromVertexName)->($toVertexName)"
  }
}

def class PVValTemplate {
  private static GStringTemplateEngine engine = new GStringTemplateEngine(PVValTemplate.class.getClassLoader())

  private static Map<String, Template> templateMap = new ConcurrentHashMap<>()

  static Template getTemplate(String templateName) {
    return templateMap.computeIfAbsent(templateName, { key -> engine.createTemplate(key) })
  }

}

class JsonSerializer {
  public static GsonBuilder builder = new GsonBuilder()
  public static Gson gson = null

  static {
    builder.registerTypeAdapter(MatchReq.class, new MatchReqAdapter())
    builder.registerTypeAdapter(Date.class, new TypeAdapter<Date>() {
      @Override
      void write(JsonWriter jsonWriter, Date o) throws IOException {
        jsonWriter.beginObject()
        jsonWriter.value(o.toString())
        jsonWriter.endObject()


      }

      @Override
      Date read(JsonReader jsonReader) throws IOException {
        return null
      }
    })

    builder.setPrettyPrinting()
    gson = builder.create()
  }

}

class MatchReqAdapter extends TypeAdapter<MatchReq> {

  @Override
  MatchReq read(JsonReader reader) throws IOException {
    return null
  }

  @Override
  void write(JsonWriter writer, MatchReq obj) throws IOException {

//    if (!obj.excludeFromSearch) {

    writer.beginObject()
            .name(obj.propName).value(obj.attribVal)
            .name("matchWeight").value(obj.matchWeight)
            .name("excludeFromSearch").value(obj.excludeFromSearch)
            .name("excludeFromSubsequenceSearch").value(obj.excludeFromSubsequenceSearch)
            .name("excludeFromUpdate").value(obj.excludeFromUpdate)
            .name("operator").value(obj.predicateStr)
            .endObject()

//    }

  }
}

class PText<V> extends P<V> {

  PText(final BiPredicate<V, V> biPredicate, final V value) {
    super(biPredicate, value)

  }

  static <V> P<V> textContains(final V value) {
    return new P(Text.containing, value)
  }

  static <V> P<V> textContainsPrefix(final V value) {
    return new P(Text.startingWith, value)
  }


}

class MatchReq<T> {


  private double matchWeight
  private T attribNativeVal
  private String attribVal
  private Class attribType
  private String propName
  private String vertexName
  private String vertexLabel
  private Boolean processAll


  private Double percentageThreshold
  private String predicateStr
  private Closure predicate


  private String sqlPredicateStr
  private Convert<T> conv
  private StringBuffer sb = null

  private boolean mandatoryInSearch
  private boolean excludeFromSearch
  private boolean excludeFromSubsequenceSearch
  private boolean excludeFromUpdate

  String getSqlPredicateStr() {
    return sqlPredicateStr
  }

  static Closure convertPredicateFromStr(String predicateStr) {
    if ("eq".equals(predicateStr)) {
      return P.&eq
    } else if ("neq".equals(predicateStr)) {
      return P.&neq
    } else if ("gt".equals(predicateStr)) {
      return P.&gt
    } else if ("lt".equals(predicateStr)) {
      return P.&lt
    } else if ("gte".equals(predicateStr)) {
      return P.&gte
    } else if ("lte".equals(predicateStr)) {
      return P.&lte
    } else if ("textContains".equals(predicateStr)) {
      return PText.&textContains
    } else if ("textContainsPrefix".equals(predicateStr)) {
      return PText.&textContainsPrefix
//    } else if ("textContainsRegex".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textContainsRegex
//    } else if ("textContainsFuzzy".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textContainsFuzzy
//    } else if ("textPrefix".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textPrefix
//    } else if ("textRegex".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textRegex
//    } else if ("textFuzzy".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textFuzzy
    } else return P.&eq

  }

  static String convertPredicateToSqlStr(String predicateStr) {
    if ("eq".equals(predicateStr)) {
      return "="
    } else if ("neq".equals(predicateStr)) {
      return "<>"
    } else if ("gt".equals(predicateStr)) {
      return ">"
    } else if ("lt".equals(predicateStr)) {
      return "<"
    } else if ("gte".equals(predicateStr)) {
      return ">="
    } else if ("lte".equals(predicateStr)) {
      return "<"
    } else if ("textContains".equals(predicateStr)) {
      return "CONTAINSTEXT"
    } else if ("textContainsPrefix".equals(predicateStr)) {
      return "CONTAINSTEXT"
//    } else if ("textContainsRegex".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textContainsRegex
//    } else if ("textContainsFuzzy".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textContainsFuzzy
//    } else if ("textPrefix".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textPrefix
//    } else if ("textRegex".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textRegex
//    } else if ("textFuzzy".equals(predicateStr)) {
//      return org.janusgraph.core.attribute.Text.&textFuzzy
    } else return "="

  }


  MatchReq(String attribVals, Class<T> attribType, String propName, String vertexName, String vertexLabel, String predicateStr, boolean excludeFromSearch = false, boolean excludeFromSubsequenceSearch = false, boolean excludeFromUpdate = false, boolean mandatoryInSearch = false, boolean processAll = false, double matchWeight, StringBuffer sb = null) {
    this.attribVal = attribVals
    this.attribType = attribType
    this.processAll = processAll
    this.propName = propName
    this.vertexName = vertexName
    this.vertexLabel = vertexLabel
    this.conv = new Convert<>(attribType)
    this.predicateStr = predicateStr
    this.predicate = convertPredicateFromStr(predicateStr)
    this.sqlPredicateStr = convertPredicateToSqlStr(predicateStr)
    this.sb = sb

    this.excludeFromSearch = excludeFromSearch
    this.excludeFromSubsequenceSearch = excludeFromSubsequenceSearch
    this.excludeFromUpdate = excludeFromUpdate
    this.mandatoryInSearch = mandatoryInSearch
    this.matchWeight = matchWeight

    sb?.append("\n In com.pontusvision.graphutils.MatchReq($attribVals, $attribType, $propName, $vertexName, $predicateStr)")
    convertToNativeFormat()
  }

  // int compareTo(Object other) {
  //     this.propName <=> other.propName
  // }

  protected void convertToNativeFormat() {

//        com.pontusvision.graphutils.Convert.fromString("asdf", this.attribType);

    if (this.attribType == String) {
      this.attribNativeVal = (T) this.attribVal
    } else {
      this.attribNativeVal = conv.fromString(this.attribVal, this.attribType, this.sb)


    }
  }

  Boolean getProcessAll() {
    return processAll
  }

  void setProcessAll(Boolean processAll) {
    this.processAll = processAll
  }

  Double getPercentageThreshold() {
    return percentageThreshold
  }

  void setPercentageThreshold(Double percentageThreshold) {
    this.percentageThreshold = percentageThreshold
  }

  boolean getMandatoryInSearch() {
    return mandatoryInSearch
  }

  void setMandatoryInSearch(boolean mandatoryInSearch) {
    this.mandatoryInSearch = mandatoryInSearch
  }

  boolean getExcludeFromSubsequenceSearch() {
    return excludeFromSubsequenceSearch
  }

  void setExcludeFromSubsequenceSearch(boolean excludeFromSubsequenceSearch) {
    this.excludeFromSubsequenceSearch = excludeFromSubsequenceSearch
  }

  boolean getExcludeFromSearch() {
    return excludeFromSearch
  }

  void setExcludeFromSearch(boolean excludeFromSearch) {
    this.excludeFromSearch = excludeFromSearch
  }


  boolean getExcludeFromUpdate() {
    return excludeFromUpdate
  }

  void setExcludeFromUpdate(boolean excludeFromUpdate) {
    this.excludeFromUpdate = excludeFromUpdate
  }

  T getAttribNativeVal() {
    return attribNativeVal
  }

  void setAttribNativeVal(T attribNativeVal) {
    this.attribNativeVal = attribNativeVal
  }

  String getAttribVal() {
    return attribVal
  }

  void setAttribVal(String attribVal) {
    this.attribVal = attribVal
  }

  Class getAttribType() {
    return attribType
  }

  void setAttribType(Class attribType) {
    this.attribType = attribType
  }

  String getPropName() {
    return propName
  }

  void setPropName(String propName) {
    this.propName = propName
  }

  String getVertexName() {
    return vertexName
  }

  void setVertexName(String vertexName) {
    this.vertexName = vertexName
  }

  String getVertexLabel() {
    return vertexLabel
  }

  void setVertexLabel(String vertexLabel) {
    this.vertexLabel = vertexLabel
  }

  String getPredicateStr() {
    return predicateStr
  }

  Closure getPredicate() {
    return predicate
  }

  void setPredicate(Closure predicate) {
    this.predicate = predicate
  }

  boolean hasGraphEntries(OrientStandardGraph graph, GraphTraversal gtrav, StringBuffer sb) {

    if (!this.excludeFromSearch && this.excludeFromUpdate) {
      List<Long> indexQueryResults = new ArrayList<>(1)
      int maxHitsPerType = 1

      GraphTraversal localTrav = getGraphEntries(graph, gtrav, indexQueryResults, maxHitsPerType, sb)

      List<Long> travResults = localTrav.range(0, maxHitsPerType).id().toList()

      return indexQueryResults.size() > 0 || travResults.size() > 0

    }

    return true
  }

  GraphTraversal getGraphEntries(OrientStandardGraph graph, GraphTraversal gtrav, List<ORID> indexQueryResults, int maxHitsPerType, StringBuffer sb) {

    String predicateStr = this.predicateStr as String
    if (predicateStr.startsWith("idx")) {
      String[] idxQuery = (predicateStr).split(':')
      String idx = idxQuery[1]

//      String value = "v.\"${this.propName}\":${this.attribNativeVal}"

      OGremlinResultSet results = graph.executeSql("SELECT @id FROM `${this.vertexLabel}` WHERE SEARCH_CLASS('${this.attribNativeVal}') = true", Collections.EMPTY_MAP)

      for (OGremlinResult result : results) {
        result.getVertex().ifPresent({ res -> indexQueryResults.add(res.id()) })
      }

      results.close()
//
//      for (JanusGraphIndexQuery.Result<JanusGraphVertex> result : (graph as OrientStandardGraph).indexQuery(idx, value).limit(maxHitsPerType).vertexStream().collect(Collectors.toList())) {
//        indexQueryResults.add(result.getElement().id());
//      }
//    }
//    if (predicateStr.startsWith("idxRaw:")) {
//      String[] idxQuery = (predicateStr).split(':');
//      String idx = idxQuery[1];
//
//      String value = this.attribNativeVal.toString();
//      value = value?.replaceAll(Pattern.quote("v.'"), 'v."');
//      value = value?.replaceAll(Pattern.quote("':"), '":');
//
//      sb?.append("\n in getGraphEntries()  idx=$idx; value=$value; predicateStr = ${predicateStr}, maxHitsPerType=$maxHitsPerType ")
//
//      for (JanusGraphIndexQuery.Result<JanusGraphVertex> result : (graph as OrientStandardGraph).indexQuery(idx, value).limit(maxHitsPerType).vertexStream().collect(Collectors.toList())) {
//        indexQueryResults.add(result.getElement().longId());
//      }
    } else {
      GraphTraversal retVal = gtrav.has(this.propName, this.predicate(this.attribNativeVal))
      sb?.append("\n     .has('")?.append(this.propName)?.append("',")
              ?.append(this.predicate)?.append(",'")?.append(this.attribNativeVal)?.append("')")

      return retVal
    }

    return gtrav

  }

  double getMatchWeight() {
    return matchWeight
  }

  void setMatchWeight(double matchWeight) {
    this.matchWeight = matchWeight
  }

  @Override
  String toString() {
    StringBuffer sb = new StringBuffer()

    if (!excludeFromSearch) {
      sb.append('\n{\n"')
              .append(propName).append('":"').append(attribVal)
              .append('"\n,"matchWeight":').append(matchWeight)
              .append('\n,"operator":"').append(predicateStr)
              .append('"\n}\n')
      return sb.toString()

    }
    return null
  }
}


class Matcher {

  static {
    PVConvMixin dummy = new PVConvMixin()

    String.mixin(PVConvMixin)

  }

  static def loadDataMappingFiles(OrientStandardGraph graph, String... dirs) {

    StringBuffer sb = new StringBuffer()


    def trans = graph.tx()
    try {
      if (!trans.isOpen()) {
        trans.open()
      }
      def jsonSuffix = ".json"
      for (d in dirs) {
        sb.append("\nLoading rules from folder ${d}")
        def jsonDir = new File(d)
        if (jsonDir.exists()) {
          jsonDir.traverse {

            def fileName = it.name

            if (fileName.endsWith(jsonSuffix)) {

              def ruleName = fileName.substring(0, fileName.length() - jsonSuffix.length())
              sb.append("\nLoading rule ${ruleName}")

              def ruleStr = it.text
              Matcher.upsertRule(App.g, ruleName, ruleStr, sb)
            }
          }
        }
      }
      graph.tx().commit()
    }
    catch (Throwable t) {
      graph.tx().rollback()
      sb?.append('\nFailed to load Mapping Files!\n')?.append(t)
      t.printStackTrace()
    }
    return sb.toString()
  }


  static Set<List<MatchReq>> subsequencesUniqueTypes(List<MatchReq> items) {
    Set<List<MatchReq>> ans = new HashSet<>()

    HashSet next
    for (Iterator iter = items.iterator(); iter.hasNext(); ans = next) {
      MatchReq h = iter.next()
      next = new HashSet()
      Iterator answerIterator = ans.iterator()

      while (answerIterator.hasNext()) {
        List<MatchReq> it = (List) answerIterator.next()
        List<MatchReq> sublist = new ArrayList<>(it)
        sublist.add(h)

        Set<String> types = new HashSet<>()
        boolean hasDups = false
        int ilen = sublist.size()
        for (int i = 0; i < ilen; i++) {
          MatchReq req = sublist.get(i)
          if (!types.add(req.propName)) {
            hasDups = true
            break
          }
        }
        if (!hasDups) {
          next.add(sublist)
        }
      }

      next.addAll(ans)
      List<MatchReq> hlist = new ArrayList<>()
      hlist.add(h)
      next.add(hlist)
    }

    return ans
  }

  static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

  static String createSearchClassAttribs(List<MatchReq> mandatoryFields) {
    StringBuilder sb = new StringBuilder()
    long counter = 0
    sb.append("(")
    mandatoryFields.each { field ->
      if (field.attribNativeVal) {
        if (counter > 0) {
          sb.append(" AND ")
        }
        counter++
        String attribVal = field.attribType == Date.class ?
                "${DateTools.dateToString(field.attribNativeVal as Date, DateTools.Resolution.SECOND)}" :
                "\"${escapeLucene(field.attribNativeVal.toString())}\""

        sb.append(field.propName).append(":").append(attribVal)
      }
    }
    sb.append(")")

    return sb.toString()
  }

  static String createWhereClauseAttribs(List<MatchReq> mandatoryFields) {
    StringBuilder sb = new StringBuilder()
    long counter = 0
    sb.append("(")
    mandatoryFields.each { field ->
      if (field.attribNativeVal) {
        if (counter > 0) {
          sb.append(" AND ")
        }
        counter++
        Object attribVal = " :${field.propName}"
//                (field.attribType == String.class) ?
//                " '${field.attribNativeVal}'" :
//                   (field.attribType == Date.class)?
//                   "'${sdf.format(field.attribNativeVal as Date)}'"
//                           :
//        " ${field.attribNativeVal}"

        String predicate = field.sqlPredicateStr
        sb.append(field.propName).append(predicate).append(attribVal)
      }
    }
    sb.append(")")

    return sb.toString()
  }

  static createJsonMergeParam(List<MatchReq> updateFields, String vertexLabel) {
//    JsonObject jb = new JsonObject()
    StringBuilder sb = new StringBuilder()
    sb.append("{")
    long counter = 0
    Map<String, Object> sqlParams = [:]
    updateFields.each { field ->
      if (counter > 0) {
        sb.append(",")
      }
      counter++
      sb.append('"').append(field.propName).append('":').append(" :${field.propName} ")
//      jb.addProperty(field.propName, ":${field.propName}")
      if (field.attribType == Date.class){

        sqlParams.put(field.propName, ((Date) field.attribNativeVal).toInstant().toString())

      }
      else{
        sqlParams.put(field.propName, field.attribNativeVal)

      }

    }
    if (counter > 0) {
      String metadataTypeVertexLabel = "Metadata_Type_${vertexLabel}"
      sb.append(',"').append(metadataTypeVertexLabel).append('":').append(" :${metadataTypeVertexLabel} ")
      sqlParams.put(metadataTypeVertexLabel, vertexLabel)

      sb.append(', "Metadata_Type": :Metadata_Type')
      sqlParams.put("Metadata_Type", vertexLabel)

    }
    sb.append("}")


    return [sb.toString(), sqlParams]
  }


  static matchVertices(OrientStandardGraph graph, GraphTraversalSource gTravSource, List<MatchReq> matchReqs, int maxHitsPerType, StringBuffer sb = null) {


    Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName = new HashMap<>()
    Map<String, Map<ORID, List<MatchReq>>> matchReqListByOridByVertexName = new HashMap<>()

    Map<String, List<MatchReq>> matchReqByVertexName = new HashMap<>()

    matchReqs.each {
      List<MatchReq> matchReqList = matchReqByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() })

      // LPPM - 25 June 2019 - reduce the number of combinations below by pruning out any records that don't have
      // any hits in the graph as early as possible.  The logic here is that if a match request does not have any matches
      // on its own, what hope do we have to use it as a filter combined with other entries???  This is especially true
      // when the NLP engines give us false positives (e.g. erroneous matches for names, dates, etc).
      if (it.hasGraphEntries(graph, gTravSource.V(), sb)) {
        matchReqList.push(it)
        vertexScoreMapByVertexName.computeIfAbsent(it.vertexName, { k -> new HashMap<>() })
        matchReqListByOridByVertexName.computeIfAbsent(it.vertexName, { k -> new HashMap<>() })
      } else {
        sb?.append("\nremoved match Request $it from the list as it was not in the graph, and marked as searchable without updates\n")

      }
    }


    matchReqByVertexName.each { vertexName, v ->


      // LPPM - must do a deep copy here, because unique is a bit nasty and actually changes the
      // original record.
      List<MatchReq> vFiltered = []

      vFiltered.addAll(v.findAll { it2 -> !(it2.excludeFromSearch) })
      boolean processAll = vFiltered.size() > 0 && vFiltered.get(0).processAll

      if (processAll) {
        vFiltered.each { matchReq ->
          GraphTraversal exists =
                  gTravSource.V().has("Metadata_Type_" + matchReq.vertexLabel, P.eq(matchReq.vertexLabel))
                          .has(matchReq.propName, matchReq.predicate(matchReq.attribNativeVal)).id()
          ORID id
          if (exists.hasNext()) {
            id = exists.next() as ORID
          } else {
            id = gTravSource.addV(matchReq.vertexLabel)
                    .property("Metadata_Type_" + matchReq.vertexLabel, matchReq.vertexLabel)
                    .property("Metadata_Type", matchReq.vertexLabel)
                    .property(matchReq.propName, matchReq.attribNativeVal)
                    .id()
                    .next() as ORID
          }

          vertexScoreMapByVertexName.get(matchReq.vertexName).put(id, new AtomicDouble(matchReq.matchWeight))
          matchReqListByOridByVertexName.get(matchReq.vertexName).put(id, [matchReq])

        }

      } else {
        List<MatchReq> vCopy2 = []
        vCopy2.addAll(vFiltered)


        List<MatchReq> uniqueProps = vCopy2.unique { a, b -> a.propName <=> b.propName }


        int maxExpectedSizeOfQueries = uniqueProps.size()


        List<MatchReq> mandatoryFields = uniqueProps.findAll { it2 -> it2.mandatoryInSearch }

        List<String> mandatoryFieldPropNames = []
        mandatoryFields.each { it2 ->
          mandatoryFieldPropNames << it2.propName
        }


        Set<List<MatchReq>> subs = Matcher.subsequencesUniqueTypes(vFiltered)
        // LPPM - 08/02/2019 - subsequences was creating way too many entries;
        // the alternative above only gives subsequences with unique types.
        // here is the original:
        // vFiltered.subsequences()

//        expectedSizeOfQueries = expectedSizeOfQueries > 2? expectedSizeOfQueries - 1: expectedSizeOfQueries

        // LPPM - 23/08/2018
        // NOTICE: To get an accurate answer, we need to get this to only do a  match across properties with
        // relatively few hits; otherwise, with large datasets, the other subsequences of smaller
        // sizes may return loads of false positives, and many false negatives (e.g. if a person lives
        // in London, we may end up with loads of hits for London, and with the cap, we may exclude the real
        // match).  To achieve this, we get all the match requests that have a excludeFromSearch set to false,
        // for the sequences with the full number of props, and then, for subsequences with smaller values, we
        // only include entries that have excludeFromSubsequenceSearch set to false.

//    double maxScore = 0;

        subs.each { it ->

          // WARNING: it.unique changes the list; DONT call it as the first arg in the if statement below, as it
          // will taint the list !
          // Also, we should always use this lambda
          // comparator here rather than at the class so
          // the subsequences can do its job without repetition
          int currSize = it.size()

          if (currSize == it.unique { entry -> entry.propName }.size()) {


            boolean checkForMandatoryFields = mandatoryFieldPropNames.size() > 0

            boolean mandatoryFieldChecksOK = true
            if (checkForMandatoryFields) {
              def currFieldPropNames = []
              it.each { it2 ->
                currFieldPropNames << it2.propName
              }

              mandatoryFieldChecksOK = currFieldPropNames.containsAll(mandatoryFieldPropNames)

            }
            if (mandatoryFieldChecksOK) {

              def searchableItems = it.findAll { it2 -> (!(currSize < maxExpectedSizeOfQueries && it2.excludeFromSubsequenceSearch)) }

              Map<ORID, AtomicDouble> indexQueryResults = new HashMap<>()
              if (searchableItems.size() > 0) {
                boolean atLeastOneTraversal = false
                double standardScore = 0
                GraphTraversal graphTraversal = gTravSource.V()

                searchableItems.each { matchReq ->

                  String predicateStr = matchReq.predicateStr as String
                  if (predicateStr.startsWith("idx")) {
                    String[] idxQuery = (predicateStr).split(':')
                    String idx = idxQuery[1]

                    String value = "v.\"${matchReq.propName}\":${matchReq.attribNativeVal}"

                    runIndexQuery(graph, idx, value, maxHitsPerType, matchReq, indexQueryResults, sb)


//                }
//                else if (predicateStr.startsWith("idxRaw:")) {
//
//                  String[] idxQuery = (predicateStr).split(':');
//                  String idx = idxQuery[1];
//
//                  String value = matchReq.attribNativeVal.toString();
//                  value = value?.replaceAll(Pattern.quote("v.'"), 'v."');
//                  value = value?.replaceAll(Pattern.quote("':"), '":');
//
//                  runIndexQuery(idx, value, maxHitsPerType, matchReq, indexQueryResults, sb);
//

                  } else {
                    if (!atLeastOneTraversal) {

                      sb?.append("\ng.V().has('Metadata_Type_")?.append(matchReq.vertexLabel)?.append("',eq('")?.append(matchReq.vertexLabel)?.append("')")
                      graphTraversal = gTravSource.V().has("Metadata_Type_" + matchReq.vertexLabel, P.eq(matchReq.vertexLabel))
                      atLeastOneTraversal = true
                    }
                    standardScore += matchReq.matchWeight

                    graphTraversal = graphTraversal.has(matchReq.propName, matchReq.predicate(matchReq.attribNativeVal))
                    sb?.append("\n     .has('")
                            ?.append(matchReq.propName)?.append("',")
                            ?.append(matchReq.predicate)?.append(",'")?.append(matchReq.attribNativeVal)?.append("')")

                  }


                }
                // Vertex ID vs Score
                Map<ORID, AtomicDouble> vertexScoreMap = vertexScoreMapByVertexName.get(vertexName)
                Map<ORID, List<MatchReq>> matchReqListByOridMap = matchReqListByOridByVertexName.get(vertexName)

                if (atLeastOneTraversal) {

                  (graphTraversal.range(0, maxHitsPerType).id().toList() as ORID[]).each { vId ->
                    AtomicDouble totalScore = vertexScoreMap.computeIfAbsent(vId, { key -> new AtomicDouble(0) })
                    List<MatchReq> matchReqList = matchReqListByOridMap.computeIfAbsent(vId, { key -> new ArrayList<>() })
                    matchReqList.addAll(searchableItems)
                    // Get rid of any index scores here in case we have any mixed entries;
                    AtomicDouble idxScores = indexQueryResults.remove(vId)
                    double idxScore = idxScores == null ? 0 : idxScores.get()
                    totalScore.set(Math.max(totalScore.get(), standardScore + idxScore))
                  }
                }
                // Look for any entries where the only matches were index scores;

                if (indexQueryResults.size() > 0) {

                  indexQueryResults.each { vId, score ->
                    AtomicDouble totalScore = vertexScoreMap.computeIfAbsent(vId, { key -> new AtomicDouble(0) })
                    totalScore.set(Math.max(totalScore.get(), score.get()))
                  }

                }

                sb?.append("\n $it")

//            maxScore += standardScore;
//            maxScore += idxQueryScore;
              }
            }


          }

        }

//    maxScoresByVertexName.get(k).addAndGet(maxScore)

        sb?.append('\n')?.append(vertexScoreMapByVertexName)?.append(" ")
      }


    }


    return [vertexScoreMapByVertexName, matchReqByVertexName, matchReqListByOridByVertexName]

  }


  static getTopHits(Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName, String targetType, double scoreThreshold, StringBuffer sb = null) {
    def ids = vertexScoreMapByVertexName.get(targetType) as Map<ORID, AtomicDouble>

    return getTopHits(ids, scoreThreshold, sb)

  }


  static getTopHits(Map<ORID, AtomicDouble> counts, final double scoreThreshold, StringBuffer sb = null) {

//    Map<Long, Integer> counts = ids.countBy { it }
    Map<ORID, AtomicDouble> countList = counts.findAll { entry -> entry.value.get() >= scoreThreshold }
//
//    List<Long> retVal = new ArrayList<>()
//    counts.each { k, v ->
//        if (v >= scoreThreshold) {
//            retVal.add(k)
//        }
//
//    }


    return countList

  }


  static Map<ORID, AtomicDouble> getOtherTopHits(Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName, String targetType, double scoreThreshold, StringBuffer sb = null) {

    Map<ORID, AtomicDouble> otherIds = new HashMap<>()

    vertexScoreMapByVertexName.each { k, v ->
      if (k != targetType) {
        otherIds.putAll(getTopHits(v, scoreThreshold, sb))
      }
    }

    return otherIds

  }


  static findMatchingNeighboursFromSingleRequired(gTrav, ORID requiredTypeId, Map<ORID, AtomicDouble> otherIds, StringBuffer sb = null) {


    def foundIds = gTrav.V(otherIds.keySet())
            .both()
            .hasId(requiredTypeId).id()
            .toSet() as ORID[]

    sb?.append("\n in findMatchingNeighboursFromSingleRequired() - foundIds = $foundIds")

    return foundIds

  }


/*

*/

  static void addNewMatchRequest(Map<String, String> binding, List<MatchReq> matchReqs, String propValItem, Class nativeType, String propName, String vertexName, String vertexLabel, String predicate, boolean excludeFromSearch, boolean excludeFromSubsequenceSearch, boolean excludeFromUpdate, boolean mandatoryInSearch, String postProcessor, String postProcessorVar, Boolean processAll, double matchWeight, StringBuffer sb = null) {

    MatchReq mreq = null

    if (nativeType == LocationAddress) {

      LocationAddress addr = LocationAddress.fromString(propValItem as String)

      Class nativeTypeAddrParts = String.class

      addr.tokens.each { key, val ->

//        val.each { it ->
        String it = val

        binding.put(postProcessorVar ?: "it", it)


        String processedVal = (postProcessor != null) ?
                PVValTemplate.getTemplate((String) postProcessor).make(binding) :
                it

        if (processedVal != null) {

          mreq = new MatchReq(
                  (String) processedVal as String
                  , nativeTypeAddrParts
                  , (String) "${propName}_${key}" as String
                  , (String) vertexName
                  , (String) vertexLabel
                  , (String) predicate
                  , (boolean) excludeFromSearch
                  , (boolean) excludeFromSubsequenceSearch
                  , (boolean) excludeFromUpdate
                  , (boolean) mandatoryInSearch
                  , (boolean) processAll
                  , (double) matchWeight
                  , sb
          )

          if (mreq?.attribNativeVal != null) {
            matchReqs.add(mreq)

          }
//          }
        }

      }


    } else {

      binding.put(postProcessorVar ?: "it", propValItem)

      String processedVal = (postProcessor != null) ?
              PVValTemplate.getTemplate((String) postProcessor).make(binding) :
              propValItem
      if (processedVal != null) {

        mreq = new MatchReq(
                (String) processedVal as String
                , nativeType
                , (String) propName
                , (String) vertexName
                , (String) vertexLabel
                , (String) predicate
                , (boolean) excludeFromSearch
                , (boolean) excludeFromSubsequenceSearch
                , (boolean) excludeFromUpdate
                , (boolean) mandatoryInSearch
                , processAll
                , (double) matchWeight
                , sb

        )

        if (mreq?.attribNativeVal != null) {
          matchReqs.add(mreq)

        }
      }
    }


  }

  static com.pontusvision.gdpr.mapping.Rules getRule(String rulesName) {
    def hasEntry =
            (App.g.V()
                    .has("Metadata_Type", "Object_Data_Src_Mapping_Rule")
                    .has("Metadata_Type_Object_Data_Src_Mapping_Rule",
                            P.eq("Object_Data_Src_Mapping_Rule"))
                    .has("Object_Data_Src_Mapping_Rule_Name", P.eq(rulesName))
                    .next()
                    .property("Object_Data_Src_Mapping_Rule_Business_Rules_JSON")
                    .value()

            )
    def jsonSlurper = new JsonSlurper()
    def rules = jsonSlurper.parseText(hasEntry as String) as com.pontusvision.gdpr.mapping.Rules

    return rules

  }

  /*
      - label: Event_Ingestion
      props:
        - name: Event_Ingestion_Type
          val: MD2
          mandatoryInSearch: False
          excludeFromSearch: True
        - name: Event_Ingestion_Operation
          val: Structured Data Insertion
          mandatoryInSearch: False
          excludeFromSearch: True
        - name: Event_Ingestion_Metadata_Create_Date
          val: ${new Date()}
          mandatoryInSearch: False
          type: "java.util.Date"
          excludeFromSearch: True

   */

  static Vertex addEventIngestion(String dataSourceName, Vertex groupIngestionVertex) {
    Vertex eventIngestionVertex = App.g.addV('Event_Ingestion')
            .property('Metadata_Type_Event_Ingestion', 'Event_Ingestion')
            .property('Event_Ingestion_Type', dataSourceName)
            .property('Event_Ingestion_Operation', 'Structured Data Insertion')
            .property('Event_Ingestion_Metadata_Create_Date', new Date())
            .next()

    App.g.addE('Has_Ingestion_Event').from(groupIngestionVertex).to(eventIngestionVertex).iterate()

    return eventIngestionVertex
  }
/*
    - label: Object_Data_Source
      props:
        - name: Object_Data_Source_Name
          val: MD2
          mandatoryInSearch: True
        - name: Object_Data_Source_Description
          val: Dados RH Colaboradores MD2
          mandatoryInSearch: True
        - name: Object_Data_Source_Type
          val: Structured
          mandatoryInSearch: True

 */

  static Vertex getObjectDataSource(String dataSourceName) {
    if (App.g.V().has('Object_Data_Source_Name', P.eq(dataSourceName)).hasNext()) {
      return App.g.V().has('Object_Data_Source_Name', P.eq(dataSourceName)).next()
    }
    Vertex dataSourceVertex = App.g.addV('Object_Data_Source')
            .property('Metadata_Type_Object_Data_Source', 'Object_Data_Source')
            .property('Object_Data_Source_Name', dataSourceName)
            .property('Object_Data_Source_Description', dataSourceName)
            .property('Object_Data_Source_Type', 'Structured')
            .next()

    return dataSourceVertex

  }

  static Vertex getEventGroupIngestion(String dataSourceName) {
    /*
          - label: Event_Group_Ingestion
      props:
        - name: Event_Group_Ingestion_Type
          val: MD2
          mandatoryInSearch: True
        - name: Event_Group_Ingestion_Operation
          val: Structured Data Insertion
          mandatoryInSearch: True
        - name: Event_Group_Ingestion_Ingestion_Date
          val: ${new java.text.SimpleDateFormat('yyyy-MM-dd').format(new Date())}
          mandatoryInSearch: True
        - name: Event_Group_Ingestion_Metadata_Start_Date
          val: ${new Date()}
          mandatoryInSearch: False
          type: "java.util.Date"
          excludeFromSearch: True
        - name: Event_Group_Ingestion_Metadata_End_Date
          val: ${new Date()}
          mandatoryInSearch: False
          type: "java.util.Date"
          excludeFromSearch: True

     */
    String ingestionDate = "${new SimpleDateFormat('yyyy-MM-dd').format(new Date())}"
    def groupIngestionVertex
    if (App.g.V().has('Event_Group_Ingestion_Type', P.eq(dataSourceName))
            .has('Event_Group_Ingestion_Ingestion_Date', P.eq(ingestionDate)).hasNext()) {
      return App.g.V().has('Event_Group_Ingestion_Type', P.eq(dataSourceName))
              .has('Event_Group_Ingestion_Ingestion_Date', P.eq(ingestionDate)).next()
    }
    groupIngestionVertex = App.g.addV('Event_Group_Ingestion')
            .property('Metadata_Type_Event_Group_Ingestion', 'Event_Group_Ingestion')
            .property('Event_Group_Ingestion_Type', dataSourceName)
            .property('Event_Group_Ingestion_Ingestion_Date', ingestionDate)
            .property('Event_Group_Ingestion_Operation', 'Structured Data Insertion')
            .property('Event_Group_Ingestion_Metadata_Start_Date', new Date())
            .property('Event_Group_Ingestion_Metadata_End_Date', new Date())
            .next()

    Vertex dataSource = getObjectDataSource(dataSourceName)

    App.g.addE('Has_Ingestion_Event').from(dataSource).to(groupIngestionVertex)

    return groupIngestionVertex

  }

  static Vertex addPersonVertex(String name, String document) {
    Vertex retVal = null
//    Transaction trans = App.graph.tx()
//    if (!trans.isOpen()) {
//      trans.open()
//    }
//    try {
    try {
      retVal = App.g.addV('Person_Natural')
              .property('Metadata_Type_Person_Natural', 'Person_Natural')
              .property('Person_Natural_Full_Name', name?.toUpperCase()?.trim())
              .property('Person_Natural_Customer_ID', "${document?.replaceAll('[^0-9]', '')}")
              .next()
    }
    catch (Throwable t) {
      retVal = App.g.V()
              .has('Metadata_Type_Person_Natural', P.eq('Person_Natural'))
              .has('Person_Natural_Full_Name', P.eq(name?.toUpperCase()?.trim()))
              .has('Person_Natural_Customer_ID', P.eq("${document?.replaceAll('[^0-9]', '')}"))
              .next()
    }
//      trans.commit()
//    } catch (Throwable t) {
//      trans.rollback()
//    }
//    finally {
//      trans.close()
//    }
    return retVal
  }

  static Vertex addIdentityVertex(String document) {

    Vertex retVal = null
//    Transaction trans = App.graph.tx()
//    if (!trans.isOpen()) {
//      trans.open()
//    }
//    try {
    try {
      retVal = App.g.addV('Object_Identity_Card')
              .property('Metadata_Type_Object_Identity_Card', 'Object_Identity_Card')
              .property('Object_Identity_Card_Type', 'CPF')
              .property('Object_Identity_Card_Id_Value', "${document?.replaceAll('[^0-9]', '')}")
              .next()
    }
    catch (Throwable t) {
      retVal = App.g.V()
              .has('Metadata_Type_Object_Identity_Card', P.eq('Object_Identity_Card'))
              .has('Object_Identity_Card_Type', P.eq('CPF'))
              .has('Object_Identity_Card_Id_Value', P.eq("${document?.replaceAll('[^0-9]', '')}"))
              .next()
    }
//
//    trans.commit()
//  }
//
//  catch (  Throwable t ) {
//    trans.rollback()
//  }
//  finally {
//    trans.close()
//  }
    return retVal
  }

  static Vertex addEmailAddressVertex(String email) {
//  Transaction trans = App.graph.tx()
//  if (!trans.isOpen()) {
//    trans.open()
//  }
//
//  try {
    try {
      return App.g.addV('Object_Email_Address')
              .property('Metadata_Type_Object_Email_Address', 'Object_Email_Address')
              .property('Object_Email_Address_Email', "${email?.trim()?.toLowerCase()}")
              .next()
    } catch (Throwable t) {
      return App.g.V()
              .has('Metadata_Type_Object_Email_Address', P.eq('Object_Email_Address'))
              .has('Object_Email_Address_Email', P.eq("${email?.trim()?.toLowerCase()}"))
              .next()
    }
////  }
////  trans.commit()
//}
//
//catch (Throwable t ) {
//  trans.rollback()
//}
//finally {
//  trans.close()
//}

  }


  static String ingestMD2BulkData(String jsonString, String jsonPath, String ruleName) {

    def recordList = JsonPath.read(jsonString, jsonPath)
    String dataSourceName = 'MD2'


    Vertex groupIngestionVertex = null

    Transaction trans = App.graph.tx()
    long successCount = 0

    if (!trans.isOpen()) {
      trans.open()
    }
    try {
      groupIngestionVertex = getEventGroupIngestion(dataSourceName)


      for (def item in recordList) {
        String name = item.name
        String email = item.email
        String document = item.document
        Vertex personNaturalVertex = addPersonVertex(name, document)
        Vertex documentVertex = addIdentityVertex(document)
        Vertex emailVertex = addEmailAddressVertex(email)

        Vertex eventIngestionVertex = addEventIngestion(dataSourceName, groupIngestionVertex)

        App.g.addE('Has_Ingestion').from(eventIngestionVertex).to(personNaturalVertex).iterate()
        App.g.addE('Uses_Email').from(personNaturalVertex).to(emailVertex).iterate()
        App.g.addE('Has_Id_Card').from(personNaturalVertex).to(documentVertex).iterate()

        successCount++
      }
      trans.commit()

    } catch (Throwable t) {
      trans.rollback()
      throw t
    } finally {
      trans.close()
    }

//    return sb?.toString()
    return "{ \"status\": \"success\"," +
            " \"successCount\": ${successCount} }"


  }

  static String ingestRecordListUsingRules(String jsonString,
                                           String jsonPath,
                                           String ruleName) {
    return ingestRecordListUsingRules(App.graph, App.g, jsonString, jsonPath, ruleName)
  }

  static String ingestEmail(String jsonString, String jsonPath, String ruleName) {
    EmailNLPRequest[] recordList = JsonPath.read(jsonString, jsonPath) // as EmailNLPRequest[]

    return EmailNLPRequest.upsertEmailNLPRequestArray(App.graph, App.g, recordList, ruleName).toString()
  }

  static String ingestFile(String jsonString, String jsonPath, String ruleName) {
    FileNLPRequest[] recordList = JsonPath.read(jsonString, jsonPath) // as EmailNLPRequest[]

    return FileNLPRequest.upsertFileNLPRequestArray(App.graph, App.g, recordList, ruleName).toString()
  }

  static String ingestRecordListUsingRules(OrientStandardGraph graph, GraphTraversalSource g,
                                           String jsonString,
                                           String jsonPath,
                                           String ruleName) {

    StringBuffer sb = null //  new StringBuffer()


    def recordList = JsonPath.read(jsonString, jsonPath)
//        def  recordList =  mapper.readValue(jsonString,
//                new TypeReference<List<Map<String, String>>>(){} as TypeReference<List<Map<String, String>>>);


    def rules = getRule(ruleName)
    //jsonSlurper.parseText(jsonRules) as com.pontusvision.com.pontusvision.graphutils.gdpr.mapping.Rules;

    int successCount = 0

    Double percentageThreshold = (rules.percentageThreshold == null) ? 10.0d
            : (double) (rules.percentageThreshold)
    int maxHitsPerType = (rules.maxHitsPerType == null) ? 1000 : (int) rules.maxHitsPerType


    for (def item in recordList) {
      Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = new HashMap<>()

      def (Map<String, List<EdgeRequest>> edgeReqsByVertexName, Set<EdgeRequest> edgeReqs) = parseEdges(rules.updatereq)

      Transaction trans = graph.tx()
      try {
        def (List<MatchReq> matchReqs, Map<String, AtomicDouble> maxScoresByVertexName,
        Map<String, Double> percentageThresholdByVertexName) =
        getMatchRequests(item as Map<String, String>, rules.updatereq, percentageThreshold, sb)

        if (rules.useSlim) {
          processMatchRequestsSlim(graph, g,
                  matchReqs,
                  edgeReqs)
        } else {
          processMatchRequests(graph, g,
                  matchReqs,
                  maxHitsPerType,
                  percentageThresholdByVertexName,
                  maxScoresByVertexName,
                  finalVertexIdByVertexName,
                  edgeReqsByVertexName,
                  edgeReqs,
                  sb)
        }


        trans.commit()
        successCount++
      } catch (Throwable t) {
        trans.rollback()
        sb?.append(t)
        throw t
      } finally {
        trans.close()
      }
    }


//    return sb?.toString()
    return "{ \"status\": \"success\"," +
            " \"successCount\": ${successCount} }"
  }

  static String upsertRule(GraphTraversalSource g, String ruleName, String ruleStr,
                           StringBuffer sb) {
    def ruleJson = new JsonSlurper().parseText(ruleStr) as Rules

    if (ruleJson.updatereq) {
      String id = null
      try {
        id = (g.V()
                .has("Metadata_Type", "Object_Data_Src_Mapping_Rule")
                .has("Metadata_Type_Object_Data_Src_Mapping_Rule",
                        P.eq("Object_Data_Src_Mapping_Rule"))
                .has("Object_Data_Src_Mapping_Rule_Name", P.eq(ruleName))
                .next()
                .id().toString())
      }
      catch (Throwable t) {
      }

      if (!id) {
        def localG = g.addV("Object_Data_Src_Mapping_Rule")
                .property("Metadata_Type", "Object_Data_Src_Mapping_Rule")
                .property("Metadata_Type_Object_Data_Src_Mapping_Rule", "Object_Data_Src_Mapping_Rule")
                .property("Object_Data_Src_Mapping_Rule_Name", ruleName)
                .property("Object_Data_Src_Mapping_Rule_Business_Rules_JSON", ruleStr)
                .property("Object_Data_Src_Mapping_Rule_Update_Date", new Date())


        sb.append("\nAdded a new Entry for ${ruleName}")
        localG = localG.property("Object_Data_Src_Mapping_Rule_Create_Date", new Date())
        id = localG.next().id().toString()

      } else {
        sb.append("\nUpdating exiting Entry for ${ruleName}; id = ${id}")

        g.V(id)
                .property("Object_Data_Src_Mapping_Rule_Business_Rules_JSON", ruleStr)
                .next()

      }

      sb.append("\nId for ${ruleName} is ${id}")
    }
  }

  static Map<String, Map<ORID, AtomicDouble>> ingestDataUsingRules(
          Map<String, String> bindings,
          String rulesName,
          StringBuffer sb = null) {
    Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = new HashMap<>()


    Transaction trans = App.graph.tx()
    try {
      if (!trans.isOpen()) {
        trans.open()
      }
      def rules = getRule(rulesName)

      double percentageThreshold = (rules.percentageThreshold == null) ? 10.0 : (double) (rules.percentageThreshold)
      int maxHitsPerType = (rules.maxHitsPerType == null) ? 1000 : (int) rules.maxHitsPerType

      def (Map<String, List<EdgeRequest>> edgeReqsByVertexName, Set<EdgeRequest> edgeReqs) =
      parseEdges(rules.updatereq)

      def (List<MatchReq> matchReqs, Map<String, AtomicDouble> maxScoresByVertexName, Map<String, Double> percentageThresholdByVertexName) =
      getMatchRequests(bindings, rules.updatereq, percentageThreshold, sb)

      processMatchRequests(App.graph,
              App.g,
              matchReqs,
              maxHitsPerType,
              percentageThresholdByVertexName,
              maxScoresByVertexName,
              finalVertexIdByVertexName,
              edgeReqsByVertexName,
              edgeReqs,
              sb)


      trans.commit()
    } catch (Throwable t) {
      trans.rollback()
      throw t
    } finally {
      trans.close()
    }

    return finalVertexIdByVertexName
  }

  static Map<String, String> cleanupKeys(Map<String, String> orgMap) {
    Map<String, String> resultMap = new HashMap<>()
    if (orgMap == null || orgMap.isEmpty()) {
      return resultMap
    }
    Set<String> keySet = orgMap.keySet()
    for (String key : keySet) {
      String newKey = key.trim()
      newKey = newKey.replaceAll(Pattern.quote("."), "_")
      newKey = newKey.replaceAll(" ", "_")

      // remove accents, etc.
      newKey = java.text.Normalizer.normalize(newKey, java.text.Normalizer.Form.NFD)
      newKey = newKey.replaceAll("\\p{M}", "")

      resultMap.put(newKey, orgMap.get(key))
    }

    resultMap.putIfAbsent("currDate", new Date().toString())
    return resultMap
  }


  static getMatchRequests(Map<String, String> currRecord, Object parsedRules, Double percentageThreshold, StringBuffer sb = null) {
    def binding = cleanupKeys(currRecord)

    binding.put("original_request", JsonOutput.prettyPrint(JsonOutput.toJson(currRecord)))

    def rules = parsedRules as com.pontusvision.gdpr.mapping.UpdateReq
    Map<String, AtomicDouble> maxScoresByVertexName = new HashMap<>()
    Map<String, Double> percentageThresholdByVertexName = new HashMap<>()

    List<MatchReq> matchReqs = new ArrayList<>(rules.vertices.size() as int)

    JsonSlurper slurper = new JsonSlurper()

    rules.vertices.each { vtx ->

      String vertexName = vtx.name ?: vtx.label
      String vertexLabel = vtx.label

      Boolean passedCondition = true


      try {
        if (vtx.condition) {
          passedCondition = Boolean
                  .parseBoolean(PVValTemplate.getTemplate((String) vtx.condition).make(binding).toString())
        }
      }
      catch (Throwable t) {
        passedCondition = false
      }

      if (passedCondition) {
        if (vtx.createMany) {
          com.pontusvision.gdpr.mapping.VertexProps createManyProp = vtx.createMany
          String propSplitChar = createManyProp?.splitChar ?: ","
          String[] propVal = ((String) createManyProp?.val).split(Pattern.quote(propSplitChar))
          def origProps = new ArrayList<com.pontusvision.gdpr.mapping.VertexProps>(vtx.props)

          propVal.each { it ->
            VertexProps newProp = new VertexProps()
            newProp.name = createManyProp.name
            newProp.excludeFromSearch = (boolean) createManyProp.excludeFromSearch
            newProp.excludeFromSubsequenceSearch = (boolean) createManyProp.excludeFromSubsequenceSearch
            newProp.excludeFromUpdate = (boolean) createManyProp.excludeFromUpdate
            newProp.mandatoryInSearch = (boolean) createManyProp.mandatoryInSearch
            newProp.val = it

            vtx.props = origProps
            vtx.props.add(newProp)

            Matcher.processVertices(slurper,
                    matchReqs,
                    maxScoresByVertexName,
                    percentageThresholdByVertexName,
                    vertexName + "[${it}]",
                    vertexLabel,
                    binding,
                    vtx,
                    percentageThreshold,
                    sb)

          }
        } else {
          Matcher.processVertices(slurper, matchReqs, maxScoresByVertexName, percentageThresholdByVertexName,
                  vertexName, vertexLabel, binding, vtx, percentageThreshold, sb)
        }
      }
    }


    return [matchReqs, maxScoresByVertexName, percentageThresholdByVertexName]

  }

  static processVertices(JsonSlurper slurper,
                         List<MatchReq> matchReqs,
                         Map<String, AtomicDouble> maxScoresByVertexName,
                         Map<String, Double> percentageThresholdByVertexName,
                         String vertexName,
                         String vertexLabel,
                         Map<String, String> binding,
                         def vtx,
                         Double percentageThreshold,
                         StringBuffer sb) {


    AtomicDouble maxScore = maxScoresByVertexName.computeIfAbsent(vertexName, { k -> new AtomicDouble(0) })
    percentageThresholdByVertexName.computeIfAbsent(vertexName, { k -> new Double((double) (vtx?.percentageThreshold == null ? percentageThreshold : vtx.percentageThreshold)) })
//        int minSizeSubsequences = vtx.minSizeSubsequences ?: -1;


    (vtx as com.pontusvision.gdpr.mapping.Vertex).props.each { prop ->

      Class nativeType = String.class

      if (prop.type != null) {
        nativeType = Class.forName((String) prop.type.toString())
      }

      String propName = prop.name

      double weight = ((prop.matchWeight == null) ? 1.0 : prop.matchWeight)
      if (!prop.excludeFromSearch) {
        maxScore.addAndGet(weight)

      }


      String propVal = null

      try {
        propVal = PVValTemplate.getTemplate((String) prop.val).make(binding)

      }
      catch (Exception e) {
        System.err.println("Got an exception ${e.getMessage()} processing ${prop.name} (${prop.val}); ignoring error for now")
//        e.printStackTrace()
      }


      if (propVal != null && !"null".equals(propVal)) {
        String predicate = prop.predicate ?: "eq"


        if (nativeType.isArray()) {

          nativeType = nativeType.getComponentType()

          def propVals

          try {
            propVals = slurper.parseText(propVal)

          }
          catch (Throwable t) {
            propVals = null
          }


          if (propVals != null) {

            propVals.each { propValItem ->

              addNewMatchRequest(
                      binding
                      , matchReqs
                      , (String) propValItem as String
                      , nativeType
                      , (String) propName
                      , (String) vertexName
                      , (String) vertexLabel
                      , (String) predicate
                      , (boolean) prop.excludeFromSearch
                      , (boolean) prop.excludeFromSubsequenceSearch
                      , (boolean) prop.excludeFromUpdate
                      , (boolean) prop.mandatoryInSearch
                      , (String) prop.postProcessor ?: null
                      , (String) prop.postProcessorVar ?: null
                      , prop.processAll ?: false
                      , (double) weight
                      , sb
              )


            }
          }

        } else {

          sb?.append("\n in getMatchRequests() - single processing $propName")

          addNewMatchRequest(
                  binding
                  , matchReqs
                  , (String) propVal
                  , nativeType
                  , (String) propName
                  , (String) vertexName
                  , (String) vertexLabel
                  , (String) predicate
                  , (boolean) prop.excludeFromSearch
                  , (boolean) prop.excludeFromSubsequenceSearch
                  , (boolean) prop.excludeFromUpdate
                  , (boolean) prop.mandatoryInSearch
                  , (String) prop.postProcessor ?: null
                  , (String) prop.postProcessorVar ?: null
                  , (Boolean) prop.processAll ?: false
                  , (double) ((prop.matchWeight == null) ? 1.0d : prop.matchWeight)
                  , sb
          )
        }
      }
    }


  }

  static Map<ORID, AtomicDouble> getTopHitsWithEdgeCheck(GraphTraversalSource g,
                                                         Map<ORID, AtomicDouble> potentialHitIDs,
                                                         double scoreThreshold,
                                                         Map<String, Map<ORID, AtomicDouble>> matchIdsByVertexType,
                                                         String vertexTypeStr,
                                                         Map<String, List<EdgeRequest>> edgeReqsByVertexType,
                                                         StringBuffer sb = null) {

    sb?.append("\nIn getTopHitsWithEdgeCheck() -- vertType = ${vertexTypeStr} ; potentialHitIDs = ${potentialHitIDs};" +
            " scoreThreshold = ${scoreThreshold}, edgeReqsByVertexType = ${edgeReqsByVertexType}")
    Map<ORID, AtomicDouble> topHits = getTopHits(potentialHitIDs, scoreThreshold, sb)

    sb?.append("\nIn getTopHitsWithEdgeCheck() -- vertType = ${vertexTypeStr} ; topHits = ${topHits} ")

    Integer numEdgesRequired = edgeReqsByVertexType.get(vertexTypeStr)?.size()

    if (numEdgesRequired != null && numEdgesRequired > 0 && topHits.size() > 1) {
      // Sanity check: we now have one or more candidates, so let's check
      // if this has conns to other vertices in our little world
      Map<ORID, AtomicDouble> otherTopHits = getOtherTopHits(matchIdsByVertexType, vertexTypeStr, scoreThreshold, sb)


      Map<ORID, AtomicDouble> topHitsFiltered = new HashMap<ORID, AtomicDouble>()

      topHitsFiltered.putAll(topHits.findAll { topHitVid, topHitScore ->
        ORID[] tempTopHits = findMatchingNeighboursFromSingleRequired(g, topHitVid as ORID, otherTopHits, sb)
        return (tempTopHits?.size() > 0)
      })
      sb?.append("\nIn getTopHitsWithEdgeCheck() -- vertType = ${vertexTypeStr} ; topHits  = ${topHitsFiltered} ")

      if (topHitsFiltered.size() == 0) {
        sb?.append("\nFiltered too much; removing filter")

        topHitsFiltered.putAll(topHits)
      }
      sb?.append("\nAfter Filter : In getTopHitsWithEdgeCheck() -- vertType = ${vertexTypeStr} ; topHits  = ${topHitsFiltered} ")

      return topHitsFiltered

    }


    sb?.append("\nIn getTopHitsWithEdgeCheck() -- vertType = ${vertexTypeStr} ; topHits  = ${topHits} ")

    return topHits

  }

  static List<ORID> addNewVertexFromMatchReqs(GraphTraversalSource g, String vertexTypeStr, List<MatchReq> matchReqsForThisVertexType,
                                              StringBuffer sb = null) {


    List<MatchReq> matchesForUpdate = []

    matchesForUpdate.addAll(matchReqsForThisVertexType.findAll { it2 -> !(it2.excludeFromUpdate) })

    Set<String> updateTypes = new HashSet<>()

    matchesForUpdate.forEach {
      updateTypes.add(it.propName)
    }

    boolean moreThanOneVertex = (matchesForUpdate.size() != updateTypes.size()) && updateTypes.size() == 1


    boolean atLeastOneUpdate = matchesForUpdate.size() > 0

    List<ORID> retVal = new LinkedList<>()
    GraphTraversal localTrav

    if (atLeastOneUpdate) {

      String vertexLabel = matchesForUpdate.get(0).vertexLabel

      if (!moreThanOneVertex) {
        localTrav = g.addV(vertexLabel)
                .property('Metadata_Type_' + vertexLabel, vertexLabel)
                .property('Metadata_Type', vertexLabel)

      }
      matchesForUpdate.each { it ->
        if (moreThanOneVertex) {
          localTrav = g.addV(vertexLabel)
                  .property('Metadata_Type_' + vertexLabel, vertexLabel)
                  .property('Metadata_Type', vertexLabel)

        }

        if (!it.excludeFromUpdate && it.attribNativeVal != null) {
          localTrav = localTrav.property(it.getPropName(), it.attribNativeVal)
        }
        if (moreThanOneVertex) {
          retVal.add(localTrav.id().next() as ORID)
        }
      }

      if (moreThanOneVertex) {
        return retVal
      } else {
        ORID orid = localTrav.next().id() as ORID
        sb?.append("\n in addNewVertexFromMatchReqs() - added new vertex of type ${vertexTypeStr}; id = ${retVal}")
        retVal.add(orid)

        return retVal

      }
    }
    sb?.append("\n in addNewVertexFromMatchReqs() - could not add ${vertexTypeStr}; no match requests marked for update")

    return retVal


  }


  static updateExistingVertexWithMatchReqs(GraphTraversalSource g, Map<ORID, AtomicDouble> vertices,
                                           List<MatchReq> matchReqsForThisVertexType, double scoreThreshold,
                                           StringBuffer sb = null) {

    GraphTraversal localTrav
    def deletionTrav = g
    sb?.append("\n in updateExistingVertexWithMatchReqs() - about to start Updating vertex of id ${vertices}; ${matchReqsForThisVertexType}")

    vertices.each { vertexId, score ->

      if (score.get() >= scoreThreshold) {
        localTrav = g.V(vertexId)

        boolean atLeastOneUpdate = false

        matchReqsForThisVertexType.each { it ->
          if (!it.excludeFromUpdate && it.attribNativeVal != null) {

            String propName = it.getPropName()
            sb?.append("\n in updateExistingVertexWithMatchReqs() - updating new vertex of id = ${vertexId} prop=${propName} val = ${it.attribNativeVal}")

//            try {
//              deletionTrav.V(vertexId).properties(it.getPropName()).drop().iterate()
//
//            }
//            catch (Throwable t) {
//              sb?.append("\n in updateExistingVertexWithMatchReqs() - FAILED TO DELETE  = ${vertexId} prop=${propName} val = ${it.attribNativeVal}; err = $t")
//            }
            if (!g.V(vertexId).has(propName, P.eq(it.attribNativeVal)).hasNext()) {
              localTrav = localTrav.property(propName, it.attribNativeVal)
              atLeastOneUpdate = true
            }

          } else {
            sb?.append("\n in updateExistingVertexWithMatchReqs() - SKIPPING UPDATE either due to null value or excludeFromUpdate == ${it.excludeFromUpdate} ; vertexId = ${vertexId} prop=${it.propName} val = ${it.attribNativeVal} ")

          }
        }

        if (atLeastOneUpdate) {
          localTrav.next()
          sb?.append("\n in updateExistingVertexWithMatchReqs() - updated vertex with  id ${vertexId}")

        } else {
          sb?.append("\n in updateExistingVertexWithMatchReqs() - SKIPPED UPDATES for  vertex with id ${vertexId}")

        }
      }
    }
    // Long retVal = localTrav.next().id() as Long

    // return retVal


  }


  static parseEdges(com.pontusvision.gdpr.mapping.UpdateReq updateReq) {

    Map<String, List<EdgeRequest>> edgeReqsByVertexName = new HashMap<>()
    Set<EdgeRequest> edgeReqs = new HashSet<>()

    (updateReq as com.pontusvision.gdpr.mapping.UpdateReq).edges.each { it ->
      String fromVertexName = it.fromVertexName ?: it.fromVertexLabel
      String toVertexName = it.toVertexName ?: it.toVertexLabel
      String label = it.label

      EdgeRequest req = new EdgeRequest(label, fromVertexName, toVertexName)

      edgeReqs.add(req)
      List<EdgeRequest> fromEdgeList = edgeReqsByVertexName.computeIfAbsent(fromVertexName, { k -> new ArrayList<EdgeRequest>() })
      fromEdgeList.add(req)
      List<EdgeRequest> toEdgeList = edgeReqsByVertexName.computeIfAbsent(toVertexName, { k -> new ArrayList<EdgeRequest>() })
      toEdgeList.add(req)

    }

    return [edgeReqsByVertexName, edgeReqs]
  }

  static createEdgesSlim(GraphTraversalSource gTrav, Set<EdgeRequest> edgeReqs, Map<String, Map<ORID, List<MatchReq>>> finalVertexIdByVertexName) {

    edgeReqs.each { it ->

      Map<ORID, List<MatchReq>> fromIds = finalVertexIdByVertexName?.get(it.fromVertexName)
      Map<ORID, List<MatchReq>> toIds = finalVertexIdByVertexName?.get(it.toVertexName)

      fromIds?.forEach { fromId, fromScore ->

        toIds?.forEach { toId, toScore ->

          if (fromId != null && toId != null) {

            ORID[] foundIds = gTrav.V(toId)
                    .both()
                    .hasId(P.within(fromId)).id()
                    .toSet() as ORID[]

            if (foundIds.size() == 0) {

              def fromV = gTrav.V(fromId)
              def toV = gTrav.V(toId)

              gTrav.addE(it.label)
                      .from(fromV).to(toV)
                      .next()


            }
          }
        }

      }


    }
  }

  static String escapeLucene(String s) {
//    return s;
    StringBuilder sb = new StringBuilder()

    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i)
      if (c == '\\'
//              c == '+' || c == '-' ||
              || c == '!'
//              || c == '('
//          c == ')' || c == ':' || c == '^' || c == '[' || c == ']' ||
              || c == '"'
//          || c == '{' || c == '}' || c == '~' || c == '*' ||
//          c == '?' || c == '|' || c == '&' || c == '/' || c == "'"
      ) {
        sb.append('\\')
      }

      if (c != '\n' && c != '\'') {
        sb.append(c)
      } else {
        sb.append(' ')
      }
    }

    return sb.toString()
  }

  static processVerticesSlim(List<MatchReq> uniqueProps,
                             String vertexLabel,
                             String vertexName,
                             Map<String, Map<ORID, List<MatchReq>>> matchReqListByOridByVertexName) {

    List<MatchReq> mandatoryFields = uniqueProps.findAll { it2 -> it2.mandatoryInSearch }
    List<MatchReq> updateFields = uniqueProps.findAll { it2 -> !it2.excludeFromUpdate }

    def (String jsonToMerge, Map<String, Object> sqlParams) = createJsonMergeParam(updateFields, vertexLabel)


    String searchClassAttribs = createSearchClassAttribs(mandatoryFields)
    String whereClauseAttribs = createWhereClauseAttribs(mandatoryFields)


    final boolean isPureInsert = "()".equals(searchClassAttribs)
    String whereClause = "WHERE ${whereClauseAttribs}"
//              "WHERE SEARCH_CLASS ('${searchClassAttribs}') = true"

    final String sqlStr = isPureInsert ?
            "INSERT INTO `${vertexLabel}` CONTENT ${jsonToMerge}" :
            "UPDATE `${vertexLabel}` MERGE ${jsonToMerge}  UPSERT  RETURN AFTER ${whereClause} LOCK record LIMIT 1 "
    final def retVals = App.graph.executeSql(sqlStr, sqlParams)
    retVals.each { OGremlinResult result ->

      result.getVertex().ifPresent({ res ->
        Map<ORID, List<MatchReq>> mrl = matchReqListByOridByVertexName.get(vertexName)
        mrl.computeIfAbsent(res.id(), { k -> uniqueProps })
      })

    }
  }

  static matchVerticesSlim(OrientStandardGraph graph, GraphTraversalSource gTravSource, List<MatchReq> matchReqs) {


    Map<String, Map<ORID, List<MatchReq>>> matchReqListByOridByVertexName = new HashMap<>()

    Map<String, List<MatchReq>> matchReqByVertexName = new HashMap<>()

    matchReqs.each {
      List<MatchReq> matchReqList = matchReqByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() })

      // LPPM - 25 June 2019 - reduce the number of combinations below by pruning out any records that don't have
      // any hits in the graph as early as possible.  The logic here is that if a match request does not have any matches
      // on its own, what hope do we have to use it as a filter combined with other entries???  This is especially true
      // when the NLP engines give us false positives (e.g. erroneous matches for names, dates, etc).
//      if (it.hasGraphEntries(graph, gTravSource.V(), sb)) {
      matchReqList.push(it)
      matchReqListByOridByVertexName.computeIfAbsent(it.vertexName, { k -> new HashMap<>() })
//      } else {
//        sb?.append("\nremoved match Request $it from the list as it was not in the graph, and marked as searchable without updates\n")
//
//      }
    }


    matchReqByVertexName.each { vertexName, v ->


      // LPPM - must do a deep copy here, because unique is a bit nasty and actually changes the
      // original record.
      List<MatchReq> vFiltered = []

      vFiltered.addAll(v.findAll { it2 -> !(it2.excludeFromSearch) })


      List<MatchReq> vCopy2 = []
      vCopy2.addAll(v)


      List<MatchReq> uniqueProps = vCopy2.unique { a, b -> a.propName <=> b.propName }
      String vertexLabel = uniqueProps.size() > 0 ? uniqueProps.get(0).vertexLabel : vertexName

      // This is used primarily for Sharepoint references, where we have a x to many relationship
      if (uniqueProps.size() < vFiltered.size() && uniqueProps.size() > 0 && uniqueProps.get(0).processAll) {
        int i = 0, ilen = vFiltered.size()
        for (; i < ilen; i++) {
          List<MatchReq> indivList = new ArrayList<>()
          indivList.push(vFiltered.get(i))
          processVerticesSlim(indivList, vertexLabel, vertexName, matchReqListByOridByVertexName)
        }

      } else {
        processVerticesSlim(uniqueProps, vertexLabel, vertexName, matchReqListByOridByVertexName)

      }

//        int maxExpectedSizeOfQueries = uniqueProps.size()


    }
    return [matchReqByVertexName, matchReqListByOridByVertexName]
  }


  static processMatchRequestsSlim(OrientStandardGraph graph, GraphTraversalSource g,
                                  List<MatchReq> matchReqs,
                                  Set<EdgeRequest> edgeReqs) {

    def (
    Map<String, List<MatchReq>>            matchReqByVertexName,
    Map<String, Map<ORID, List<MatchReq>>> matchReqListByOridByVertexName
    ) = matchVerticesSlim(graph, g, matchReqs)

    createEdgesSlim(g, (Set<EdgeRequest>) edgeReqs, matchReqListByOridByVertexName)

  }

  static createEdges(GraphTraversalSource gTrav, Set<EdgeRequest> edgeReqs, Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName, Map<String, AtomicDouble> maxScoresByVertexName, StringBuffer sb = null) {

    edgeReqs.each { it ->

      sb?.append("\n in createEdges; edgeReq = $it ")

      sb?.append("\n in createEdges; finalVertexIdByVertexName = $finalVertexIdByVertexName; \nmaxScoresByVertexName = $maxScoresByVertexName ")

      Double maxFromScore = maxScoresByVertexName.get(it.fromVertexName)?.get()
      Double maxToScore = maxScoresByVertexName.get(it.toVertexName)?.get()

      if (maxFromScore != null && maxToScore != null) {


        Map<ORID, AtomicDouble> fromIds = finalVertexIdByVertexName?.get(it.fromVertexName)
        Map<ORID, AtomicDouble> toIds = finalVertexIdByVertexName?.get(it.toVertexName)

        fromIds?.forEach { fromId, fromScore ->

          toIds?.forEach { toId, toScore ->
            sb?.append("\n in createEdges; from=$fromId; to=$toId ")

            if (fromId != null && toId != null) {

              ORID[] foundIds = gTrav.V(toId)
                      .both()
                      .hasId(P.within(fromId)).id()
                      .toSet() as ORID[]

              sb?.append("\n in createEdges $foundIds")

              if (foundIds.size() == 0) {

                Double fromScorePercent = (maxFromScore > 0 ? (fromScore.get() / maxFromScore) : (double) 1.0) * (double) 100.0
                Double toScorePercent = (maxToScore > 0 ? (toScore.get() / maxToScore) : (double) 1.0) * (double) 100.0
                Double fromScoreDouble = fromScore.get()
                Double toScoreDouble = toScore.get()
                def fromV = gTrav.V(fromId)
                def toV = gTrav.V(toId)
                sb?.append("\n in createEdges about to create new Edges from  $fromId to $toId (fromV = ${fromV}; toV= ${toV}; maxFromScore = $maxFromScore; fromScore = $fromScoreDouble; maxToScore = $maxToScore; toScore: $toScoreDouble")
                gTrav.addE(it.label)
                        .from(fromV).to(toV)
                        .property('maxFromScore', maxFromScore)
                        .property('fromScore', fromScoreDouble)
                        .property('fromScorePercent', fromScorePercent)
                        .property('maxToScore', maxToScore)
                        .property('toScore', toScoreDouble)
                        .property('toScorePercent', toScorePercent)
                        .next()


              } else {
                sb?.append("\n in createEdges SKIPPING Edge creations")

              }
            } else {
              sb?.append("\n in createEdges SKIPPING Edge creations")

            }


          }
        }

      }


    }
  }

  static processMatchRequests(OrientStandardGraph graph, GraphTraversalSource g,
                              List<MatchReq> matchReqs,
                              int maxHitsPerType,
                              Map<String, Double> percentageThresholdByVertexName,
                              Map<String, AtomicDouble> maxScoresByVertexName,
                              Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName,
                              Map<String, List<EdgeRequest>> edgeReqsByVertexType,
                              Set<EdgeRequest> edgeReqs,
                              StringBuffer sb) {

    def (
    Map<String, Map<ORID, AtomicDouble>>   vertexScoreMapByVertexName,
    Map<String, List<MatchReq>>            matchReqByVertexName,
    Map<String, Map<ORID, List<MatchReq>>> matchReqListByOridByVertexName
    ) = matchVertices(graph, g, matchReqs, maxHitsPerType, sb)


    vertexScoreMapByVertexName.each { vertexTypeStr, potentialHitIDs ->

      List<MatchReq> matchReqsForThisVertexType = matchReqByVertexName.get(vertexTypeStr)

      boolean processAll = (matchReqsForThisVertexType.size() > 0 && matchReqsForThisVertexType.get(0).getProcessAll())

      if (processAll) {
        finalVertexIdByVertexName.put(vertexTypeStr, potentialHitIDs)

      } else { // if (!processAll) {
        double maxScore = maxScoresByVertexName.get(vertexTypeStr).get()
        double percentageThreshold = percentageThresholdByVertexName.get(vertexTypeStr)
        double scoreThreshold = (double) (maxScore * 100 * (percentageThreshold / 100) / 100)

        Map<ORID, AtomicDouble> topHits = getTopHitsWithEdgeCheck(g, potentialHitIDs, scoreThreshold, vertexScoreMapByVertexName, vertexTypeStr, edgeReqsByVertexType, sb)

        if (topHits != null && topHits.size() > 0) {
          updateExistingVertexWithMatchReqs(g, topHits, matchReqsForThisVertexType, scoreThreshold, sb)
          finalVertexIdByVertexName.put((String) vertexTypeStr, topHits)

        } else {
          Map<ORID, AtomicDouble> newVertices = new HashMap<>()
          List<ORID> vIds = addNewVertexFromMatchReqs(g, (String) vertexTypeStr, matchReqsForThisVertexType, sb)
          int vlen = vIds.size()
          for (int v = 0; v < vlen; v++) {
            ORID vId = vIds.get(v)
            newVertices.put(vId, new AtomicDouble(maxScore))
            finalVertexIdByVertexName.put((String) vertexTypeStr, newVertices)
            if ('Event_Ingestion'.equalsIgnoreCase(matchReqsForThisVertexType?.get(0)?.getVertexLabel())) {
              String bizRule = JsonSerializer.gson.toJson(matchReqByVertexName)
              sb?.append("\n\n\n ADDING Event_Ingestion_Business_Rules: ${bizRule}\n\n")
              g.V(vId).property('Event_Ingestion_Business_Rules', bizRule).next()
            }
          }
        }


      }


    }

    createEdges(g, (Set<EdgeRequest>) edgeReqs, finalVertexIdByVertexName, maxScoresByVertexName, sb)


  }

  static runIndexQuery(OrientStandardGraph graph, String idx, String value, int maxHitsPerType, MatchReq matchReq, Map<ORID, AtomicDouble> indexQueryResults, StringBuffer sb) {


    OGremlinResultSet results =
            graph.executeSql("SELECT \$score, @rid FROM `${idx}` WHERE SEARCH_CLASS ('${value}') = true", Collections.EMPTY_MAP)


//    JanusGraphIndexQuery query = (graph as OrientStandardGraph)?.
//      indexQuery(idx, value);

    double maxScoreForRawIdx = 0
    Map<ORID, Double> idxQueryRes = new HashMap<>()

    for (OGremlinResult result : results) {

      result.getVertex().ifPresent({ res ->
        Double score = res.value('$score')
        idxQueryRes.put(res.id(), score)
        maxScoreForRawIdx = Math.max(maxScoreForRawIdx, score)

      }
      )

    }

//
//    query?.limit(maxHitsPerType)?.vertexStream()?.forEach { result ->
//      double score = result.score
//      idxQueryRes.put((ORID) result.element.id(), score);
//      maxScoreForRawIdx = Math.max(maxScoreForRawIdx, score);
//    }
//    Long total = query.vertexTotals();

    Long total = results.size()

    sb?.append("\n\nIn runIndexQuery: About to call idxRaw: idx: ${idx}; value = ${value}; total=${total} maxHitsPerType=${maxHitsPerType}; idxQueryRes = ${idxQueryRes}, maxScoreForRawIdx = $maxScoreForRawIdx")

    idxQueryRes.forEach { vId, score ->
      sb?.append("\nIn runIndexQuery:")?.append(vId)?.append(": ")?.append(score)
      AtomicDouble totalScore = indexQueryResults.computeIfAbsent(vId, { key -> new AtomicDouble(0) })
      totalScore.addAndGet(matchReq.matchWeight * (score / maxScoreForRawIdx))

    }

    results.close()

//  OptionalDouble  optionalDoubleMaxScoreForRawIdx = resultStream.mapToDouble{val -> val.score}.max();
//
//  optionalDoubleMaxScoreForRawIdx.ifPresent({  maxScore ->
//    sb?.append("\nmax Score for ${value} is $maxScore")
//
//    resultStream.forEach { JanusGraphIndexQuery.Result<JanusGraphVertex> result ->
//      Long vId = (Long) result.element.id();
//      double score = result.score
//      sb?.append("  )?.append(result.getElement().id())?.append(": ")?.append(result.getScore());
//      AtomicDouble totalScore = indexQueryResults.computeIfAbsent(vId, { key -> new AtomicDouble(0) });
//      totalScore.addAndGet(matchReq.matchWeight * (score / maxScore))
//
//    }
//  })


  }


  static ingestDataUsingRules(OrientStandardGraph graph, GraphTraversalSource g, Map<String, String> bindings, String jsonRules, StringBuffer sb = null) {
    Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = new HashMap<>()

    def jsonSlurper = new JsonSlurper()
    def rules = jsonSlurper.parseText(jsonRules) as com.pontusvision.gdpr.mapping.Rules

    double percentageThreshold = (rules.percentageThreshold == null) ? 10.0 : (double) (rules.percentageThreshold)
    int maxHitsPerType = (rules.maxHitsPerType == null) ? 1000 : (int) rules.maxHitsPerType

    def (Map<String, List<EdgeRequest>> edgeReqsByVertexName, Set<EdgeRequest> edgeReqs) = Matcher.parseEdges(rules.updatereq)
    Transaction trans = graph.tx()
    try {
      if (!trans.isOpen()) {
        trans.open()
      }

      def (List<MatchReq> matchReqs, Map<String, AtomicDouble> maxScoresByVertexName, Map<String, Double> percentageThresholdByVertexName) =
      Matcher.getMatchRequests(bindings, rules.updatereq, percentageThreshold, sb)

      Matcher.processMatchRequests(graph,
              g,
              matchReqs,
              maxHitsPerType,
              percentageThresholdByVertexName,
              maxScoresByVertexName,
              finalVertexIdByVertexName,
              edgeReqsByVertexName,
              edgeReqs,
              sb)


      trans.commit()
    } catch (Throwable t) {
      trans.rollback()
      throw t
    } finally {
      trans.close()
    }

    return finalVertexIdByVertexName
  }


  static ingestRecordListUsingRules(OrientStandardGraph graph, GraphTraversalSource g, List<Map<String, String>> recordList, String jsonRules, StringBuffer sb = null) {
    Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = new HashMap<>()

    def jsonSlurper = new JsonSlurper()
    def rules = jsonSlurper.parseText(jsonRules) as com.pontusvision.gdpr.mapping.Rules


    double percentageThreshold = (rules.percentageThreshold == null) ? 10.0 : (double) (rules.percentageThreshold)
    int maxHitsPerType = (rules.maxHitsPerType == null) ? 1000 : (int) rules.maxHitsPerType

    def (Map<String, List<EdgeRequest>> edgeReqsByVertexName, Set<EdgeRequest> edgeReqs) = Matcher.parseEdges(rules.updatereq)
    Transaction trans = graph.tx()
    try {
      if (!trans.isOpen()) {
        trans.open()
      }

      for (Map<String, String> item in recordList) {

        def (List<MatchReq> matchReqs, Map<String, AtomicDouble> maxScoresByVertexName, Map<String, Double> percentageThresholdByVertexName) =
        Matcher.getMatchRequests(item, rules.updatereq, percentageThreshold, sb)


        Matcher.processMatchRequests(graph, g,
                matchReqs,
                maxHitsPerType,
                percentageThresholdByVertexName,
                maxScoresByVertexName,
                finalVertexIdByVertexName,
                edgeReqsByVertexName,
                edgeReqs,
                sb)

      }


      trans.commit()
    } catch (Throwable t) {
      trans.rollback()
      throw t
    } finally {
      trans.close()
    }
  }

  static findMatchingNeighbours(def gTrav = App.g, Set<ORID> requiredTypeIds, Set<ORID> otherIds, StringBuffer sb = null) {


    def foundIds = gTrav.V(otherIds)
            .both()
            .hasId(within(requiredTypeIds)).id()
            .toSet() as ORID[]

    sb?.append("\n$foundIds")

    return foundIds

  }

}


/*

def rulesStr = '''
{
  "updatereq":
  {
    "vertices":
	[
	  {
		"label": "Person_Natural"
	   ,"props":
		[
		  {
			"name": "Person_Natural_Full_Name_fuzzy"
		   ,"val": "${person}"
		   ,"predicate": "textContainsFuzzy"
		   ,"type":"[Ljava.lang.String;"
		   ,"excludeFromUpdate": true
		   ,"mandatoryInSearch": true
		   ,"postProcessor": "${it?.toUpperCase()?.trim()}"

		  }
		 ,{
			"name": "Person_Natural_Last_Name"
		   ,"val": "${person}"
		   ,"predicate": "textContainsFuzzy"
		   ,"type":"[Ljava.lang.String;"
		   ,"excludeFromUpdate": true
		   ,"postProcessor": "${it?.toUpperCase()?.trim()}"
		  }
		]
	  }
	 ,{
		"label": "Location_Address"
	   ,"props":
		[
		  {
			"name": "Location_Address_parser_postcode"
		   ,"val": "${postcode}"
		   ,"type":"[Ljava.lang.String;"
		   ,"excludeFromUpdate": true
		   ,"mandatoryInSearch": true
		   ,"postProcessorVar": "eachPostCode"
		   ,"postProcessor": "${com.pontusvision.utils.PostCode.format(eachPostCode)}"
		  }
		]

	  }
	 ,{
		"label": "Object_Email_Address"
	   ,"props":
		[
		  {
			"name": "Object_Email_Address_Email"
		   ,"val": "${email}"
		   ,"type":"[Ljava.lang.String;"
		   ,"excludeFromUpdate": true
		   ,"mandatoryInSearch": true
		  }
		]

	  }
	 ,{
		"label": "Object_Insurance_Policy"
	   ,"props":
		[
		  {
			"name": "Object_Insurance_Policy_Number"
		   ,"val": "${policy_number}"
		   ,"type":"[Ljava.lang.String;"
		   ,"excludeFromUpdate": true
		   ,"mandatoryInSearch": true
		  }
		]

	  }
	 ,{
		"label": "Event_Ingestion"
	   ,"props":
		[
		  {
			"name": "Event_Ingestion_Type"
		   ,"val": "Outlook PST Files"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event_Ingestion_Operation"
		   ,"val": "Unstructured Data Insertion"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event_Ingestion_Domain_b64"
		   ,"val": "${original_request?.bytes?.encodeBase64()?.toString()}"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event_Ingestion_Domain_Unstructured_Data_b64"
		   ,"val": "${pg_content?.bytes?.encodeBase64()?.toString()}"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event_Ingestion_Metadata_Create_Date"
		   ,"val": "${new Date()}"
		   ,"excludeFromSearch": true
		   ,"type": "java.util.Date"

		  }

		]
	  }
     ,{
		"label": "Event_Group_Ingestion"
	   ,"props":
		[
		  {
			"name": "Event_Group_Ingestion_Metadata_Start_Date"
		   ,"val": "${pg_currDate}"
		   ,"mandatoryInSearch": true
		   ,"type": "java.util.Date"
		  }
		 ,{
			"name": "Event_Group_Ingestion_Metadata_End_Date"
		   ,"val": "${new Date()}"
		   ,"excludeFromSearch": true
		   ,"excludeFromSubsequenceSearch": true
		   ,"type": "java.util.Date"
		  }

		 ,{
			"name": "Event_Group_Ingestion_Type"
		   ,"val": "Outlook PST Files"
		   ,"mandatoryInSearch": true
		  }
		 ,{
			"name": "Event_Group_Ingestion_Operation"
		   ,"val": "Unstructured Data Insertion"
		   ,"mandatoryInSearch": true
		  }

		]
	  }

	]
   ,"edges":
    [
      { "label": "Has_Ingestion_Event", "fromVertexLabel": "Person_Natural", "toVertexLabel": "Event_Ingestion"  }
     ,{ "label": "Has_Ingestion_Event", "fromVertexLabel": "Event_Group_Ingestion", "toVertexLabel": "Event_Ingestion"  }
    ]
  }
}
'''

groovy.json.JsonSlurper slurper = new groovy.json.JsonSlurper();


def pg_metadataController = 'abc'
def pg_metadataGDPRStatus = 'Personal'
def pg_metadataLineage = 'https://randomuser.me/api/?format=csv'
def pg_nlp_res_address = '[]'
def pg_nlp_res_cred_card = '[]'
def pg_nlp_res_emailaddress = '[]'
def pg_nlp_res_location = '["Como"]'
def pg_currDate = 'Sat Jul 20 09:28:25 UTC 2019'
def pg_content = 'b'
def pg_nlp_res_city = '[]'
def pg_nlp_res_person = '["John Smith","renova\u00E7\u00E3o da","Smith","John","autom\u00E1tico","renova\u00E7\u00E3o"]'
def pg_nlp_res_phone = '["200", "6051"]';
def pg_nlp_res_post_code = '["de 20", "u00ED", "u00EA", "u00FA", "as 48", "u0", "BP 50", "ce 10", "em 27"]'
def pg_nlp_res_policy_number  = '["10330434"]'


def bindings = [:];

bindings['metadataController'] = "${pg_metadataController}";
bindings['metadataGDPRStatus'] = "${pg_metadataGDPRStatus}";
bindings['metadataLineage'] = "${pg_metadataLineage}";
bindings['address'] = "${pg_nlp_res_address}";
//bindings['company'] = "${pg_nlp_res_company?:[]}";
bindings['cred_card'] = "${pg_nlp_res_cred_card}";
bindings['email'] = "${pg_nlp_res_emailaddress}";
bindings['location'] = "${pg_nlp_res_location}";
bindings['pg_currDate'] = "${pg_currDate}";



bindings['pg_content'] = 'parsedContent.text;'

bindings['city'] = "${pg_nlp_res_city}";





def personFilter = ['Name insured person: ','1: ','Self','name: ','0','1','Name insured 1: ','Name: ','2','0: ','1: ',' 1: ']



bindings['person'] = "${com.pontusvision.utils.NLPCleaner.filter(pg_nlp_res_person,(boolean)true,(Set<String>)personFilter) as String}";
// bindings['person'] = "${pg_nlp_res_person}";
bindings['phone'] = "${pg_nlp_res_phone}";
bindings['postcode'] = "${pg_nlp_res_post_code}";
bindings['policy_number'] = "${pg_nlp_res_policy_number}";



StringBuffer sb = new StringBuffer ()

try{
  sb.append("\n\nbindings: ${bindings}");

  ingestDataUsingRules(graph, g, bindings, rulesStr, sb)
}
catch (Throwable t){
  String stackTrace = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)

  sb.append("\n$t\n$stackTrace")

  throw new Throwable(sb.toString())


}
sb.toString()

*/