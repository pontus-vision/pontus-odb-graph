import com.google.common.util.concurrent.AtomicDouble
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.joestelmach.natty.DateGroup
import com.joestelmach.natty.Parser
import com.orientechnologies.orient.core.id.ORID
import com.pontusvision.utils.LocationAddress
import com.pontusvision.utils.PostCode
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import groovy.text.Template
import org.apache.tinkerpop.gremlin.orientdb.OrientStandardGraph
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResult
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.Text
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Transaction
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiPredicate
import java.util.regex.Pattern
import java.util.stream.Collectors

/*
def benchmark = { closure ->
    start = System.nanoTime()
    closure.call()
    now = System.nanoTime()
    now - start
}
*/


class Convert<T> {
  private from
  private to


  public Convert(clazz) {
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
  static Parser parser = new Parser();
  static {

    String.mixin(PVConvMixin)
  }

  static def asType(String self, Class cls, StringBuffer sb = null) {
    if (cls == Date) {

      List<DateGroup> dateGroup = parser.parse(self as String)
      Date retVal = null;

      if (!dateGroup.isEmpty()) {
        DateGroup dg = dateGroup.get(0)

        boolean isTimeInferred = dg.isTimeInferred();

        List<Date> dates = dg.getDates()

        sb?.append("\n\nConverting data $self; found $dates")
        dates.each {

          retVal = it
          if (isTimeInferred) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(retVal);
            calendar.set(Calendar.HOUR_OF_DAY, 1);
            calendar.set(Calendar.MINUTE, 1);
            calendar.set(Calendar.SECOND, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            retVal = calendar.getTime();

          }
        }
        if (retVal == null) {
          sb?.append("\nCould not find a conversion for this date $self")

          return null;
        }
      }
      return retVal;


    } else if (cls == PostCode) {
      return new PostCode(self)
    } else return convert(self, cls)
  }


}

PVConvMixin dummy = null
String.mixin(PVConvMixin)

class EdgeRequest {

  String label;
  String fromVertexName;
  String toVertexName;

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

  private static Map<String, Template> templateMap = new ConcurrentHashMap<>();

  static Template getTemplate(String templateName) {
    return templateMap.computeIfAbsent(templateName, { key -> engine.createTemplate(key) })
  }

}

class JsonSerializer {
  public static GsonBuilder builder = new GsonBuilder();
  public static Gson gson = null;

  static {
    builder.registerTypeAdapter(MatchReq.class, new MatchReqAdapter());
    builder.registerTypeAdapter(Date.class, new TypeAdapter<Date>() {
      @Override
      void write(JsonWriter jsonWriter, Date o) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.value(o.toString())
        jsonWriter.endObject();


      }

      @Override
      Date read(JsonReader jsonReader) throws IOException {
        return null
      }
    });

    builder.setPrettyPrinting();
    gson = builder.create();
  }

}

class MatchReqAdapter extends TypeAdapter<MatchReq> {

  @Override
  public MatchReq read(JsonReader reader) throws IOException {
    return null;
  }

  @Override
  public void write(JsonWriter writer, MatchReq obj) throws IOException {

//    if (!obj.excludeFromSearch) {

    writer.beginObject()
      .name(obj.propName).value(obj.attribVal)
      .name("matchWeight").value(obj.matchWeight)
      .name("excludeFromSearch").value(obj.excludeFromSearch)
      .name("excludeFromSubsequenceSearch").value(obj.excludeFromSubsequenceSearch)
      .name("excludeFromUpdate").value(obj.excludeFromUpdate)
      .name("operator").value(obj.predicateStr)
      .endObject();

//    }

  }
}

class PText<V> extends P<V> {

  public PText(final BiPredicate<V, V> biPredicate, final V value) {
    super(biPredicate, value);

  }

  public static <V> P<V> textContains(final V value) {
    return new P(Text.containing, value);
  }

  public static <V> P<V> textContainsPrefix(final V value) {
    return new P(Text.startingWith, value);
  }


}

class MatchReq<T> {


  private double matchWeight;
  private T attribNativeVal;
  private String attribVal;
  private Class attribType;
  private String propName;
  private String vertexName;
  private String vertexLabel;

  private String predicateStr;
  private Closure predicate;
  private Convert<T> conv;
  private StringBuffer sb = null;

  private boolean mandatoryInSearch;
  private boolean excludeFromSearch;
  private boolean excludeFromSubsequenceSearch
  private boolean excludeFromUpdate;


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
    } else return P.&eq;

  }

  MatchReq(String attribVals, Class<T> attribType, String propName, String vertexName, String vertexLabel, String predicateStr, boolean excludeFromSearch = false, boolean excludeFromSubsequenceSearch = false, boolean excludeFromUpdate = false, boolean mandatoryInSearch = false, double matchWeight, StringBuffer sb = null) {
    this.attribVal = attribVals
    this.attribType = attribType

    this.propName = propName
    this.vertexName = vertexName
    this.vertexLabel = vertexLabel
    this.conv = new Convert<>(attribType)
    this.predicateStr = predicateStr;
    this.predicate = convertPredicateFromStr(predicateStr)

    this.sb = sb;

    this.excludeFromSearch = excludeFromSearch
    this.excludeFromSubsequenceSearch = excludeFromSubsequenceSearch
    this.excludeFromUpdate = excludeFromUpdate
    this.mandatoryInSearch = mandatoryInSearch
    this.matchWeight = matchWeight

    sb?.append("\n In MatchReq($attribVals, $attribType, $propName, $vertexName, $predicateStr)")
    convertToNativeFormat()
  }

  // int compareTo(Object other) {
  //     this.propName <=> other.propName
  // }

  protected void convertToNativeFormat() {

//        Convert.fromString("asdf", this.attribType);

    if (this.attribType == String) {
      this.attribNativeVal = (T) this.attribVal;
    } else {
      this.attribNativeVal = conv.fromString(this.attribVal, this.attribType, this.sb)


    }
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
    return predicateStr;
  }

  Closure getPredicate() {
    return predicate
  }

  void setPredicate(Closure predicate) {
    this.predicate = predicate
  }

  boolean hasGraphEntries(OrientStandardGraph graph, GraphTraversal gtrav, StringBuffer sb) {

    if (!this.excludeFromSearch && this.excludeFromUpdate) {
      List<Long> indexQueryResults = new ArrayList<>(1);
      int maxHitsPerType = 1;

      GraphTraversal localTrav = getGraphEntries(graph, gtrav, indexQueryResults, maxHitsPerType, sb);

      List<Long> travResults = localTrav.range(0, maxHitsPerType).id().toList();

      return indexQueryResults.size() > 0 || travResults.size() > 0;

    }

    return true;
  }

  GraphTraversal getGraphEntries(OrientStandardGraph graph, GraphTraversal gtrav, List<ORID> indexQueryResults, int maxHitsPerType, StringBuffer sb) {

    String predicateStr = this.predicateStr as String;
    if (predicateStr.startsWith("idx")) {
      String[] idxQuery = (predicateStr).split(':');
      String idx = idxQuery[1];

//      String value = "v.\"${this.propName}\":${this.attribNativeVal}"

      OGremlinResultSet results = graph.executeSql("SELECT @id FROM `${this.vertexLabel}` WHERE SEARCH_CLASS('${this.attribNativeVal}') ", Collections.EMPTY_MAP);

      for (OGremlinResult result : results) {
        result.getVertex().ifPresent({ res -> indexQueryResults.add(res.id()) });
      }

      results.close();
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

      return retVal;
    }

    return gtrav;

  }

  double getMatchWeight() {
    return matchWeight
  }

  void setMatchWeight(double matchWeight) {
    this.matchWeight = matchWeight
  }

  @Override
  String toString() {
    StringBuffer sb = new StringBuffer();

    if (!excludeFromSearch) {
      sb.append('\n{\n"')
        .append(propName).append('":"').append(attribVal)
        .append('"\n,"matchWeight":').append(matchWeight)
        .append('\n,"operator":"').append(predicateStr)
        .append('"\n}\n')
      return sb.toString();

    }
    return null;
  }
}


class Matcher {


  static Set<List<MatchReq>> subsequencesUniqueTypes(List<MatchReq> items) {
    Set<List<MatchReq>> ans = new HashSet<>();

    HashSet next;
    for (Iterator iter = items.iterator(); iter.hasNext(); ans = next) {
      MatchReq h = iter.next();
      next = new HashSet();
      Iterator answerIterator = ans.iterator();

      while (answerIterator.hasNext()) {
        List<MatchReq> it = (List) answerIterator.next();
        List<MatchReq> sublist = new ArrayList<>(it);
        sublist.add(h);

        Set<String> types = new HashSet<>()
        boolean hasDups = false;
        int ilen = sublist.size();
        for (int i = 0; i < ilen; i++) {
          MatchReq req = sublist.get(i);
          if (!types.add(req.propName)) {
            hasDups = true;
            break;
          }
        }
        if (!hasDups) {
          next.add(sublist);
        }
      }

      next.addAll(ans);
      List<MatchReq> hlist = new ArrayList<>();
      hlist.add(h);
      next.add(hlist);
    }

    return ans;
  }


  static matchVertices(OrientStandardGraph graph, GraphTraversalSource gTravSource, List<MatchReq> matchReqs, int maxHitsPerType, StringBuffer sb = null) {


    Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName = new HashMap<>();

    Map<String, List<MatchReq>> matchReqByVertexName = new HashMap<>();

    matchReqs.each {
      List<MatchReq> matchReqList = matchReqByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() });

      // LPPM - 25 June 2019 - reduce the number of combinations below by pruning out any records that don't have
      // any hits in the graph as early as possible.  The logic here is that if a match request does not have any matches
      // on its own, what hope do we have to use it as a filter combined with other entries???  This is especially true
      // when the NLP engines give us false positives (e.g. erroneous matches for names, dates, etc).
      if (it.hasGraphEntries(graph, gTravSource.V(), sb)) {
        matchReqList.push(it)
        vertexScoreMapByVertexName.computeIfAbsent(it.vertexName, { k -> new HashMap<>() })
      } else {
        sb?.append("\nremoved match Request $it from the list as it was not in the graph, and marked as searchable without updates\n")

      }
    }


    matchReqByVertexName.each { vertexName, v ->


      // LPPM - must do a deep copy here, because unique is a bit nasty and actually changes the
      // original record.
      List<MatchReq> vFiltered = [];

      vFiltered.addAll(v.findAll { it2 -> !(it2.excludeFromSearch) });

      List<MatchReq> vCopy2 = [];
      vCopy2.addAll(vFiltered);


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
        int currSize = it.size();

        if (currSize == it.unique { entry -> entry.propName }.size()) {


          boolean checkForMandatoryFields = mandatoryFieldPropNames.size() > 0;

          boolean mandatoryFieldChecksOK = true;
          if (checkForMandatoryFields) {
            def currFieldPropNames = []
            it.each { it2 ->
              currFieldPropNames << it2.propName
            }

            mandatoryFieldChecksOK = currFieldPropNames.containsAll(mandatoryFieldPropNames);

          }
          if (mandatoryFieldChecksOK) {

            def searchableItems = it.findAll { it2 -> (!(currSize < maxExpectedSizeOfQueries && it2.excludeFromSubsequenceSearch)) }

            Map<ORID, AtomicDouble> indexQueryResults = new HashMap<>();
            if (searchableItems.size() > 0) {
              boolean atLeastOneTraversal = false;
              double standardScore = 0;
              GraphTraversal graphTraversal = gTravSource.V();

              searchableItems.each { matchReq ->

                String predicateStr = matchReq.predicateStr as String;
                if (predicateStr.startsWith("idx")) {
                  String[] idxQuery = (predicateStr).split(':');
                  String idx = idxQuery[1];

                  String value = "v.\"${matchReq.propName}\":${matchReq.attribNativeVal}"

                  runIndexQuery(graph, idx, value, maxHitsPerType, matchReq, indexQueryResults, sb);


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

                    sb?.append("\ng.V().has('Metadata.Type.")?.append(matchReq.vertexLabel)?.append("',eq('")?.append(matchReq.vertexLabel)?.append("')")
                    graphTraversal = gTravSource.V().has("Metadata.Type." + matchReq.vertexLabel, P.eq(matchReq.vertexLabel));
                    atLeastOneTraversal = true;
                  }
                  standardScore += matchReq.matchWeight;

                  graphTraversal = graphTraversal.has(matchReq.propName, matchReq.predicate(matchReq.attribNativeVal))
                  sb?.append("\n     .has('")
                    ?.append(matchReq.propName)?.append("',")
                    ?.append(matchReq.predicate)?.append(",'")?.append(matchReq.attribNativeVal)?.append("')")

                }


              }
              // Vertex ID vs Score
              Map<ORID, AtomicDouble> vertexScoreMap = vertexScoreMapByVertexName.get(vertexName);

              if (atLeastOneTraversal) {

                (graphTraversal.range(0, maxHitsPerType).id().toList() as ORID[]).each { vId ->
                  AtomicDouble totalScore = vertexScoreMap.computeIfAbsent(vId, { key -> new AtomicDouble(0) });

                  // Get rid of any index scores here in case we have any mixed entries;
                  AtomicDouble idxScores = indexQueryResults.remove(vId);
                  double idxScore = idxScores == null ? 0 : idxScores.get();
                  totalScore.set(Math.max(totalScore.get(), standardScore + idxScore))
                }
              }
              // Look for any entries where the only matches were index scores;

              if (indexQueryResults.size() > 0) {

                indexQueryResults.each { vId, score ->
                  AtomicDouble totalScore = vertexScoreMap.computeIfAbsent(vId, { key -> new AtomicDouble(0) });
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


    return [vertexScoreMapByVertexName, matchReqByVertexName];

  }


  static getTopHits(Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName, String targetType, double scoreThreshold, StringBuffer sb = null) {
    def ids = vertexScoreMapByVertexName.get(targetType) as Map<ORID, AtomicDouble>;

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

    Map<ORID, AtomicDouble> otherIds = new HashMap<>();

    vertexScoreMapByVertexName.each { k, v ->
      if (k != targetType) {
        otherIds.putAll(getTopHits(v, scoreThreshold, sb))
      }
    }

    return otherIds;

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

  static void addNewMatchRequest(Map<String, String> binding, List<MatchReq> matchReqs, String propValItem, Class nativeType, String propName, String vertexName, String vertexLabel, String predicate, boolean excludeFromSearch, boolean excludeFromSubsequenceSearch, boolean excludeFromUpdate, boolean mandatoryInSearch, String postProcessor, String postProcessorVar, double matchWeight, StringBuffer sb = null) {

    MatchReq mreq = null;

    if (nativeType == LocationAddress) {

      LocationAddress addr = LocationAddress.fromString(propValItem as String);

      Class nativeTypeAddrParts = String.class;

      addr.tokens.each { key, val ->

        val.each { it ->


          binding.put(postProcessorVar ?: "it", it);


          String processedVal = (postProcessor != null) ?
            PVValTemplate.getTemplate((String) postProcessor).make(binding) :
            it;

          if (processedVal != null) {

            mreq = new MatchReq(
              (String) processedVal as String
              , nativeTypeAddrParts
              , (String) "${propName}.${key}" as String
              , (String) vertexName
              , (String) vertexLabel
              , (String) predicate
              , (boolean) excludeFromSearch
              , (boolean) excludeFromSubsequenceSearch
              , (boolean) excludeFromUpdate
              , (boolean) mandatoryInSearch
              , (double) matchWeight
              , sb
            );

            if (mreq?.attribNativeVal != null) {
              matchReqs.add(mreq)

            }
          }
        }

      }


    } else {

      binding.put(postProcessorVar ?: "it", propValItem);

      String processedVal = (postProcessor != null) ?
        PVValTemplate.getTemplate((String) postProcessor).make(binding) :
        propValItem;
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
          , (double) matchWeight
          , sb

        );

        if (mreq?.attribNativeVal != null) {
          matchReqs.add(mreq)

        }
      }
    }


  }

  static getMatchRequests(Map<String, String> currRecord, Object parsedRules, String rulesJsonStr, Double percentageThreshold, StringBuffer sb = null) {
    def binding = currRecord

    binding.put("original_request", JsonOutput.prettyPrint(JsonOutput.toJson(currRecord)));

    def rules = parsedRules
    Map<String, AtomicDouble> maxScoresByVertexName = new HashMap<>();
    Map<String, Double> percentageThresholdByVertexName = new HashMap<>();

    List<MatchReq> matchReqs = new ArrayList<>(rules.vertices.size() as int)

    JsonSlurper slurper = new JsonSlurper()

    rules.vertices.each { vtx ->

      String vertexName = vtx.name ?: vtx.label
      String vertexLabel = vtx.label

      Boolean passedCondition = true;


      try {
        if (vtx.condition) {
          passedCondition = Boolean
            .parseBoolean(PVValTemplate.getTemplate((String) vtx.condition).make(binding).toString());
        }
      }
      catch (Throwable t) {
        passedCondition = false;
      }


      if (passedCondition) {

        AtomicDouble maxScore = maxScoresByVertexName.computeIfAbsent(vertexName, { k -> new AtomicDouble(0) })
        percentageThresholdByVertexName.computeIfAbsent(vertexName, { k -> new Double((double) (vtx.percentageThreshold == null ? percentageThreshold : vtx.percentageThreshold)) })
//        int minSizeSubsequences = vtx.minSizeSubsequences ?: -1;


        vtx.props.each { prop ->

          Class nativeType;

          if (prop.type == null) {
            nativeType = String.class
          } else {
            nativeType = Class.forName((String) prop.type)
          }

          String propName = prop.name

          double weight = ((prop.matchWeight == null) ? 1.0 : prop.matchWeight);
          if (!prop.excludeFromSearch) {
            maxScore.addAndGet(weight);

          }


          String propVal = PVValTemplate.getTemplate((String) prop.val).make(binding)


          if (propVal != null && !"null".equals(propVal)) {
            String predicate = prop.predicate ?: "eq"


            if (nativeType.isArray()) {

              nativeType = nativeType.getComponentType();

              def propVals;

              try {
                propVals = slurper.parseText(propVal)

              }
              catch (Throwable t) {
                propVals = null;
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
                    , (double) weight
                    , sb
                  );


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
                , (double) ((prop.matchWeight == null) ? 1.0 : prop.matchWeight)
                , sb
              );
            }
          }
        }
      }
    }
    return [matchReqs, maxScoresByVertexName, percentageThresholdByVertexName];

  }


  static getTopHitsWithEdgeCheck(GraphTraversalSource g,
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


      Map<ORID, AtomicDouble> topHitsFiltered = new HashMap<ORID, AtomicDouble>();

      topHitsFiltered.putAll(topHits.findAll { topHitVid, topHitScore ->
        ORID[] tempTopHits = findMatchingNeighboursFromSingleRequired(g, topHitVid as ORID, otherTopHits, sb);
        return (tempTopHits?.size() > 0);
      });
      sb?.append("\nIn getTopHitsWithEdgeCheck() -- vertType = ${vertexTypeStr} ; topHits  = ${topHitsFiltered} ")

      if (topHitsFiltered.size() == 0) {
        sb?.append("\nFiltered too much; removing filter")

        topHitsFiltered.putAll(topHits);
      }
      sb?.append("\nAfter Filter : In getTopHitsWithEdgeCheck() -- vertType = ${vertexTypeStr} ; topHits  = ${topHitsFiltered} ")

      return topHitsFiltered;

    }


    sb?.append("\nIn getTopHitsWithEdgeCheck() -- vertType = ${vertexTypeStr} ; topHits  = ${topHits} ")

    return topHits;

  }

  static addNewVertexFromMatchReqs(GraphTraversalSource g, String vertexTypeStr, List<MatchReq> matchReqsForThisVertexType, StringBuffer sb = null) {

    def localTrav = g

    List<MatchReq> matchesForUpdate = [];

    matchesForUpdate.addAll(matchReqsForThisVertexType.findAll { it2 -> !(it2.excludeFromUpdate) })

    boolean atLeastOneUpdate = matchesForUpdate.size() > 0

    if (atLeastOneUpdate) {

      String vertexLabel = matchesForUpdate.get(0).vertexLabel;
      localTrav = localTrav.addV(vertexLabel)
        .property('Metadata.Type.' + vertexLabel, vertexLabel)
        .property('Metadata.Type', vertexLabel)

      matchesForUpdate.each { it ->
        if (!it.excludeFromUpdate && it.attribNativeVal != null) {
          localTrav = localTrav.property(it.getPropName(), it.attribNativeVal);
        }
      }

      ORID retVal = localTrav.next().id() as ORID

      sb?.append("\n in addNewVertexFromMatchReqs() - added new vertex of type ${vertexTypeStr}; id = ${retVal}")
      return retVal
    }
    sb?.append("\n in addNewVertexFromMatchReqs() - could not add ${vertexTypeStr}; no match requests marked for update");

    return null;


  }


  static updateExistingVertexWithMatchReqs(GraphTraversalSource g, Map<ORID, AtomicDouble> vertices, List<MatchReq> matchReqsForThisVertexType, double scoreThreshold, StringBuffer sb = null) {

    GraphTraversal localTrav;
    def deletionTrav = g
    sb?.append("\n in updateExistingVertexWithMatchReqs() - about to start Updating vertex of id ${vertices}; ${matchReqsForThisVertexType}")

    vertices.each { vertexId, score ->

      if (score.get() >= scoreThreshold) {


        localTrav = g.V(vertexId)

        boolean atLeastOneUpdate = false;
        matchReqsForThisVertexType.each { it ->
          if (!it.excludeFromUpdate && it.attribNativeVal != null) {

            String propName = it.getPropName();
            sb?.append("\n in updateExistingVertexWithMatchReqs() - updating new vertex of id = ${vertexId} prop=${propName} val = ${it.attribNativeVal}")

            try {
              deletionTrav.V(vertexId).properties(it.getPropName()).drop().iterate()

            }
            catch (Throwable t) {
              sb?.append("\n in updateExistingVertexWithMatchReqs() - FAILED TO DELETE  = ${vertexId} prop=${propName} val = ${it.attribNativeVal}; err = $t")
            }
            localTrav = localTrav.property(propName, it.attribNativeVal)
            atLeastOneUpdate = true

          } else {
            sb?.append("\n in updateExistingVertexWithMatchReqs() - SKIPPING UPDATE either due to null value or excludeFromUpdate == ${it.excludeFromUpdate} ; vertexId = ${vertexId} prop=${it.propName} val = ${it.attribNativeVal} ")

          }
        }

        if (atLeastOneUpdate) {
          localTrav.iterate()
          sb?.append("\n in updateExistingVertexWithMatchReqs() - updated vertex with  id ${vertexId}")

        } else {
          sb?.append("\n in updateExistingVertexWithMatchReqs() - SKIPPED UPDATES for  vertex with id ${vertexId}")

        }
      }
    }
    // Long retVal = localTrav.next().id() as Long

    // return retVal


  }


  static parseEdges(def rules) {

    Map<String, List<EdgeRequest>> edgeReqsByVertexName = new HashMap<>()
    Set<EdgeRequest> edgeReqs = new HashSet<>()

    rules.edges.each { it ->
      String fromVertexName = it.fromVertexName ?: it.fromVertexLabel
      String toVertexName = it.toVertexName ?: it.toVertexLabel
      String label = it.label

      EdgeRequest req = new EdgeRequest(label, fromVertexName, toVertexName);

      edgeReqs.add(req)
      List<EdgeRequest> fromEdgeList = edgeReqsByVertexName.computeIfAbsent(fromVertexName, { k -> new ArrayList<EdgeRequest>() })
      fromEdgeList.add(req)
      List<EdgeRequest> toEdgeList = edgeReqsByVertexName.computeIfAbsent(toVertexName, { k -> new ArrayList<EdgeRequest>() })
      toEdgeList.add(req)

    }

    return [edgeReqsByVertexName, edgeReqs]
  }

  static createEdges(GraphTraversalSource gTrav, Set<EdgeRequest> edgeReqs, Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName, Map<String, AtomicDouble> maxScoresByVertexName, StringBuffer sb = null) {

    edgeReqs.each { it ->

      sb?.append("\n in createEdges; edgeReq = $it ")

      sb?.append("\n in createEdges; finalVertexIdByVertexName = $finalVertexIdByVertexName; \nmaxScoresByVertexName = $maxScoresByVertexName ")

      Double maxFromScore = maxScoresByVertexName.get(it.fromVertexName)?.get();
      Double maxToScore = maxScoresByVertexName.get(it.toVertexName)?.get();

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

                Double fromScorePercent = (maxFromScore > 0 ? (fromScore.get() / maxFromScore) : (double) 1.0) * (double) 100.0;
                Double toScorePercent = (maxToScore > 0 ? (toScore.get() / maxToScore) : (double) 1.0) * (double) 100.0;
                Double fromScoreDouble = fromScore.get();
                Double toScoreDouble = toScore.get();
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
                  .next();


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
    Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName,
    Map<String, List<MatchReq>>          matchReqByVertexName
    ) = Matcher.matchVertices(graph, g, matchReqs, maxHitsPerType, sb);


    vertexScoreMapByVertexName.each { vertexTypeStr, potentialHitIDs ->

      List<MatchReq> matchReqsForThisVertexType = matchReqByVertexName.get(vertexTypeStr)

      double maxScore = maxScoresByVertexName.get(vertexTypeStr).get();
      double percentageThreshold = percentageThresholdByVertexName.get(vertexTypeStr)
      double scoreThreshold = (double) (maxScore * 100 * (percentageThreshold / 100) / 100);

      Map<ORID, AtomicDouble> topHits = getTopHitsWithEdgeCheck(g, potentialHitIDs, scoreThreshold, vertexScoreMapByVertexName, vertexTypeStr, edgeReqsByVertexType, sb)

      if (topHits != null && topHits.size() > 0) {

        updateExistingVertexWithMatchReqs(g, topHits, matchReqsForThisVertexType, scoreThreshold, sb)
        finalVertexIdByVertexName.put((String) vertexTypeStr, topHits)
      } else {
        Map<ORID, AtomicDouble> newVertices = new HashMap<>();
        ORID vId = addNewVertexFromMatchReqs(g, (String) vertexTypeStr, matchReqsForThisVertexType, sb)
        newVertices.put(vId, new AtomicDouble(maxScore));
        finalVertexIdByVertexName.put((String) vertexTypeStr, newVertices)

        if ('Event.Ingestion'.equalsIgnoreCase(matchReqsForThisVertexType?.get(0)?.getVertexLabel())) {

//        json rootKey: matchReqByVertexName

//        String bizRule = JsonOutput.prettyPrint(json.toString())
          String bizRule = JsonSerializer.gson.toJson(matchReqByVertexName);

          sb.append("\n\n\n ADDING Event.Ingestion.Business_Rules: ${bizRule}\n\n")


          g.V(vId).property('Event.Ingestion.Business_Rules', bizRule).next();
        }

      }


    }

    createEdges(g, (Set<EdgeRequest>) edgeReqs, finalVertexIdByVertexName, maxScoresByVertexName, sb)


  }
  static runIndexQuery(OrientStandardGraph graph, String idx, String value, int maxHitsPerType, MatchReq matchReq, Map<ORID, AtomicDouble> indexQueryResults, StringBuffer sb) {


    OGremlinResultSet results =
      graph.executeSql("SELECT \$score, @rid FROM `${idx}` WHERE SEARCH_CLASS ('${value}'", Collections.EMPTY_MAP)



//    JanusGraphIndexQuery query = (graph as OrientStandardGraph)?.
//      indexQuery(idx, value);

    double maxScoreForRawIdx = 0;
    Map<ORID, Double> idxQueryRes = new HashMap<>();

    for (OGremlinResult result : results) {

      result.getVertex().ifPresent({ res ->
        Double score = res.value('$score')
        idxQueryRes.put(res.id(), score)
        maxScoreForRawIdx = Math.max(maxScoreForRawIdx, score);

      }
      );

    }

//
//    query?.limit(maxHitsPerType)?.vertexStream()?.forEach { result ->
//      double score = result.score
//      idxQueryRes.put((ORID) result.element.id(), score);
//      maxScoreForRawIdx = Math.max(maxScoreForRawIdx, score);
//    }
//    Long total = query.vertexTotals();

    Long total = results.size();

    sb?.append("\n\nIn runIndexQuery: About to call idxRaw: idx: ${idx}; value = ${value}; total=${total} maxHitsPerType=${maxHitsPerType}; idxQueryRes = ${idxQueryRes}, maxScoreForRawIdx = $maxScoreForRawIdx")

    idxQueryRes.forEach { vId, score ->
      sb?.append("\nIn runIndexQuery:")?.append(vId)?.append(": ")?.append(score);
      AtomicDouble totalScore = indexQueryResults.computeIfAbsent(vId, { key -> new AtomicDouble(0) });
      totalScore.addAndGet(matchReq.matchWeight * (score / maxScoreForRawIdx))

    }

    results.close();

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

}


def ingestDataUsingRules(OrientStandardGraph graph, GraphTraversalSource g, Map<String, String> bindings, String jsonRules, StringBuffer sb = null) {
  Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = new HashMap<>();

  def jsonSlurper = new JsonSlurper()
  def rules = jsonSlurper.parseText(jsonRules)

  double percentageThreshold = (rules.percentageThreshold == null) ? 10.0 : (double) (rules.percentageThreshold);
  int maxHitsPerType = (rules.maxHitsPerType == null) ? 1000 : (int) rules.maxHitsPerType;

  def (Map<String, List<EdgeRequest>> edgeReqsByVertexName, Set<EdgeRequest> edgeReqs) = Matcher.parseEdges(rules.updatereq)
  Transaction trans = graph.tx()
  try {
    if (!trans.isOpen()) {
      trans.open()
    }

    def (List<MatchReq> matchReqs, Map<String, AtomicDouble> maxScoresByVertexName, Map<String, Double> percentageThresholdByVertexName) =
    Matcher.getMatchRequests(bindings, rules.updatereq, jsonRules, percentageThreshold, sb)

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

  return finalVertexIdByVertexName;
}


def ingestRecordListUsingRules(OrientStandardGraph graph, GraphTraversalSource g, List<Map<String, String>> recordList, String jsonRules, StringBuffer sb = null) {
  Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = new HashMap<>();

  def jsonSlurper = new JsonSlurper()
  def rules = jsonSlurper.parseText(jsonRules)


  double percentageThreshold = (rules.percentageThreshold == null) ? 10.0 : (double) (rules.percentageThreshold);
  int maxHitsPerType = (rules.maxHitsPerType == null) ? 1000 : (int) rules.maxHitsPerType;

  def (Map<String, List<EdgeRequest>> edgeReqsByVertexName, Set<EdgeRequest> edgeReqs) = Matcher.parseEdges(rules.updatereq)
  Transaction trans = graph.tx()
  try {
    if (!trans.isOpen()) {
      trans.open()
    }

    for (Map<String, String> item in recordList) {

      def (List<MatchReq> matchReqs, Map<String, AtomicDouble> maxScoresByVertexName, Map<String, Double> percentageThresholdByVertexName) =
      Matcher.getMatchRequests(item, rules.updatereq, jsonRules, percentageThreshold, sb);


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

def findMatchingNeighbours(gTrav = g, Set<ORID> requiredTypeIds, Set<ORID> otherIds, StringBuffer sb = null) {


  def foundIds = gTrav.V(otherIds)
    .both()
    .hasId(within(requiredTypeIds)).id()
    .toSet() as ORID[]

  sb?.append("\n$foundIds")

  return foundIds

}


/*

def rulesStr = '''
{
  "updatereq":
  {
    "vertices":
	[
	  {
		"label": "Person.Natural"
	   ,"props":
		[
		  {
			"name": "Person.Natural.Full_Name_fuzzy"
		   ,"val": "${person}"
		   ,"predicate": "textContainsFuzzy"
		   ,"type":"[Ljava.lang.String;"
		   ,"excludeFromUpdate": true
		   ,"mandatoryInSearch": true
		   ,"postProcessor": "${it?.toUpperCase()?.trim()}"

		  }
		 ,{
			"name": "Person.Natural.Last_Name"
		   ,"val": "${person}"
		   ,"predicate": "textContainsFuzzy"
		   ,"type":"[Ljava.lang.String;"
		   ,"excludeFromUpdate": true
		   ,"postProcessor": "${it?.toUpperCase()?.trim()}"
		  }
		]
	  }
	 ,{
		"label": "Location.Address"
	   ,"props":
		[
		  {
			"name": "Location.Address.parser.postcode"
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
		"label": "Object.Email_Address"
	   ,"props":
		[
		  {
			"name": "Object.Email_Address.Email"
		   ,"val": "${email}"
		   ,"type":"[Ljava.lang.String;"
		   ,"excludeFromUpdate": true
		   ,"mandatoryInSearch": true
		  }
		]

	  }
	 ,{
		"label": "Object.Insurance_Policy"
	   ,"props":
		[
		  {
			"name": "Object.Insurance_Policy.Number"
		   ,"val": "${policy_number}"
		   ,"type":"[Ljava.lang.String;"
		   ,"excludeFromUpdate": true
		   ,"mandatoryInSearch": true
		  }
		]

	  }
	 ,{
		"label": "Event.Ingestion"
	   ,"props":
		[
		  {
			"name": "Event.Ingestion.Type"
		   ,"val": "Outlook PST Files"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event.Ingestion.Operation"
		   ,"val": "Unstructured Data Insertion"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event.Ingestion.Domain_b64"
		   ,"val": "${original_request?.bytes?.encodeBase64()?.toString()}"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event.Ingestion.Domain_Unstructured_Data_b64"
		   ,"val": "${pg_content?.bytes?.encodeBase64()?.toString()}"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event.Ingestion.Metadata_Create_Date"
		   ,"val": "${new Date()}"
		   ,"excludeFromSearch": true
		   ,"type": "java.util.Date"

		  }

		]
	  }
     ,{
		"label": "Event.Group_Ingestion"
	   ,"props":
		[
		  {
			"name": "Event.Group_Ingestion.Metadata_Start_Date"
		   ,"val": "${pg_currDate}"
		   ,"mandatoryInSearch": true
		   ,"type": "java.util.Date"
		  }
		 ,{
			"name": "Event.Group_Ingestion.Metadata_End_Date"
		   ,"val": "${new Date()}"
		   ,"excludeFromSearch": true
		   ,"excludeFromSubsequenceSearch": true
		   ,"type": "java.util.Date"
		  }

		 ,{
			"name": "Event.Group_Ingestion.Type"
		   ,"val": "Outlook PST Files"
		   ,"mandatoryInSearch": true
		  }
		 ,{
			"name": "Event.Group_Ingestion.Operation"
		   ,"val": "Unstructured Data Insertion"
		   ,"mandatoryInSearch": true
		  }

		]
	  }

	]
   ,"edges":
    [
      { "label": "Has_Ingestion_Event", "fromVertexLabel": "Person.Natural", "toVertexLabel": "Event.Ingestion"  }
     ,{ "label": "Has_Ingestion_Event", "fromVertexLabel": "Event.Group_Ingestion", "toVertexLabel": "Event.Ingestion"  }
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