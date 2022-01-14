import com.joestelmach.natty.DateGroup
import com.joestelmach.natty.Parser
import com.pontusvision.utils.LocationAddress
import com.pontusvision.utils.PostCode
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import groovy.text.Template
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.codehaus.groovy.runtime.StringGroovyMethods
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphIndexQuery
import org.janusgraph.core.JanusGraphVertex

import java.util.concurrent.ConcurrentHashMap
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


def class PVValTemplate {
  private static GStringTemplateEngine engine = new GStringTemplateEngine(PVValTemplate.class.getClassLoader())

  private static Map<String, Template> templateMap = new ConcurrentHashMap<>();

  static Template getTemplate(String templateName) {
    return templateMap.computeIfAbsent(templateName, { key -> engine.createTemplate(key) })
  }

}


class MatchReq<T> {

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
      return org.janusgraph.core.attribute.Text.&textContains
    } else if ("textContainsPrefix".equals(predicateStr)) {
      return org.janusgraph.core.attribute.Text.&textContainsPrefix
    } else if ("textContainsRegex".equals(predicateStr)) {
      return org.janusgraph.core.attribute.Text.&textContainsRegex
    } else if ("textContainsFuzzy".equals(predicateStr)) {
      return org.janusgraph.core.attribute.Text.&textContainsFuzzy
    } else if ("textPrefix".equals(predicateStr)) {
      return org.janusgraph.core.attribute.Text.&textPrefix
    } else if ("textRegex".equals(predicateStr)) {
      return org.janusgraph.core.attribute.Text.&textRegex
    } else if ("textFuzzy".equals(predicateStr)) {
      return org.janusgraph.core.attribute.Text.&textFuzzy
    } else return P.eq;

  }

  MatchReq(String attribVals, Class<T> attribType, String propName, String vertexName, String vertexLabel, String predicateStr, boolean excludeFromSearch = false, boolean excludeFromSubsequenceSearch = false, boolean excludeFromUpdate = false, boolean mandatoryInSearch = false, StringBuffer sb = null) {
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

    sb?.append("\n In MatchReq($attribVals, $attribType, $propName, $vertexName, $predicateStr)")
    convertToNativeFormat()
  }

  // int compareTo(Object other) {
  //     this.propName <=> other.propName
  // }

  protected void convertToNativeFormat() {

//        Convert.fromString("asdf", this.attribType);

    if (this.attribType == String) {
      this.attribNativeVal = this.attribVal;
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
  };

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

  @Override
  String toString() {
    return propName + '=' + attribNativeVal
  }
}


def matchVertices(gTrav = g, List<MatchReq> matchReqs, int maxHitsPerType, StringBuffer sb = null) {


  HashMap<String, List<Long>> vertexListsByVertexName = new HashMap();

  HashMap<String, List<MatchReq>> matchReqByVertexName = new HashMap<>();

  matchReqs.each {
    List<MatchReq> matchReqList = matchReqByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() });
    matchReqList.push(it)
    vertexListsByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() })

  }


  matchReqByVertexName.each { vertexName, v ->

    def gtrav = gTrav

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


    Set<List<MatchReq>> subs = vFiltered.subsequences()

//        expectedSizeOfQueries = expectedSizeOfQueries > 2? expectedSizeOfQueries - 1: expectedSizeOfQueries

    // LPPM - 23/08/2018
    // NOTICE: To get an accurate answer, we need to get this to only do a  match across properties with
    // relatively few hits; otherwise, with large datasets, the other subsequences of smaller
    // sizes may return loads of false positives, and many false negatives (e.g. if a person lives
    // in London, we may end up with loads of hits for London, and with the cap, we may exclude the real
    // match).  To achieve this, we get all the match requests that have a excludeFromSearch set to false,
    // for the sequences with the full number of props, and then, for subsequences with smaller values, we
    // only include entries that have excludeFromSubsequenceSearch set to false.


    subs.each { it ->

      // WARNING: it.unique changes the list; DONT call it as the first arg!
      // Also, we should always this lambda
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

          List<Long> indexQueryResults = new LinkedList<>();
          if (searchableItems.size() > 0) {
            String vertexLabel = searchableItems.get(0).vertexLabel;

            sb?.append("\ng.V().has('Metadata.Type.")?.append(vertexLabel)?.append("',eq('")?.append(vertexLabel)?.append("')")
            gtrav = gTrav.V().has("Metadata.Type." + vertexLabel, eq(vertexLabel)).clone()

            searchableItems.each { it2 ->

              String predicateStr = it2.predicateStr as String;
              if (predicateStr.startsWith("idx:")) {
                String[] idxQuery = (predicateStr).split(':');
                String idx = idxQuery[1];

                String value = "v.\"${it2.propName}\":${it2.attribNativeVal}"

//                                indexQueryResults.addAll((graph as JanusGraph).indexQuery(idx, value).vertexStream().mapToLong({ result -> result.getElement().longId()}).collect(Collectors.toList()));
                for (JanusGraphIndexQuery.Result<JanusGraphVertex> result : (graph as JanusGraph).indexQuery(idx, value).limit(maxHitsPerType).vertexStream().collect(Collectors.toList())) {
                  indexQueryResults.add(result.getElement().longId());
                }
              } else if (predicateStr.startsWith("idxRaw:")) {
                String[] idxQuery = (predicateStr).split(':');
                String idx = idxQuery[1];

                String value = it2.attribNativeVal.toString();

//                                indexQueryResults.addAll((graph as JanusGraph).indexQuery(idx, value).vertexStream().mapToLong({ result -> result.getElement().longId()}).collect(Collectors.toList()));
                for (JanusGraphIndexQuery.Result<JanusGraphVertex> result : (graph as JanusGraph).indexQuery(idx, value).limit(maxHitsPerType).vertexStream().collect(Collectors.toList())) {
                  indexQueryResults.add(result.getElement().longId());
                }
              } else {
                gtrav = gtrav.has(it2.propName, it2.predicate(it2.attribNativeVal)).clone()
                sb?.append("\n     .has('")?.append(it2.propName)?.append("',")
                  ?.append(it2.predicate)?.append(",'")?.append(it2.attribNativeVal)?.append("')")

              }


            }
            vertexListsByVertexName.get(vertexName).addAll(gtrav.range(0, maxHitsPerType).id().toList() as Long[])
            if (indexQueryResults.size() > 0) {
              vertexListsByVertexName.get(vertexName).addAll(indexQueryResults.subList(0, maxHitsPerType))

            }
            sb?.append("\n $it")

          }
        }


      }

    }

    sb?.append('\n')?.append(vertexListsByVertexName)?.append("\n")


  }


  return [vertexListsByVertexName, matchReqByVertexName];

}


def getTopHits(HashMap<String, List<Long>> vertexListsByVertexName, String targetType, int countThreshold, StringBuffer sb = null) {
  def ids = vertexListsByVertexName.get(targetType) as Long[];

  return getTopHits(ids, countThreshold, sb)

}


def getTopHits(Long[] ids, int countThreshold, StringBuffer sb = null) {

  Map<Long, Integer> counts = ids.countBy { it }

  counts = counts.sort { a, b -> b.value <=> a.value }

  List<Long> retVal = new ArrayList<>()
  counts.each { k, v ->
    if (v >= countThreshold) {
      retVal.add(k)
    }

  }


  return retVal

}


def getOtherTopHits(Map<String, List<Long>> vertexListsByVertexName, String targetType, int countThreshold, StringBuffer sb = null) {

  Set<Long> otherIdsSet = new HashSet<>();

  vertexListsByVertexName.each { k, v ->
    if (k != targetType) {
      otherIdsSet.addAll(getTopHits(v as Long[], countThreshold, sb))
    }
  }

  return otherIdsSet;

}


def findMatchingNeighboursFromSingleRequired(gTrav = g, Long requiredTypeId, Set<Long> otherIds, StringBuffer sb = null) {


  def foundIds = gTrav.V(otherIds)
    .both()
    .hasId(requiredTypeId).id()
    .toSet() as Long[]

  sb?.append("\n in findMatchingNeighboursFromSingleRequired() - foundIds = $foundIds")

  return foundIds

}

def findMatchingNeighbours(gTrav = g, Set<Long> requiredTypeIds, Set<Long> otherIds, StringBuffer sb = null) {


  def foundIds = gTrav.V(otherIds)
    .both()
    .hasId(within(requiredTypeIds)).id()
    .toSet() as Long[]

  sb?.append("\n$foundIds")

  return foundIds

}

/*

 */

void addNewMatchRequest(Map<String, String> binding, List<MatchReq> matchReqs, String propValItem, Class nativeType, String propName, String vertexName, String vertexLabel, String predicate, boolean excludeFromSearch, boolean excludeFromSubsequenceSearch, boolean excludeFromUpdate, boolean mandatoryInSearch, String postProcessor, String postProcessorVar, StringBuffer sb = null) {

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
        , sb

      );

      if (mreq?.attribNativeVal != null) {
        matchReqs.add(mreq)

      }
    }
  }


}

def getMatchRequests(Map<String, String> currRecord, Object parsedRules, String rulesJsonStr, StringBuffer sb = null) {
  def binding = currRecord

  binding.put("original_request", JsonOutput.prettyPrint(JsonOutput.toJson(currRecord)));


  def rules = parsedRules

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
    catch (Throwable t){
      passedCondition = false;
    }



    if (passedCondition) {
      vtx.props.each { prop ->

        Class nativeType;

        if (prop.type == null) {
          nativeType = String.class
        } else {
          nativeType = Class.forName((String) prop.type)
        }

        String propName = prop.name

        String propVal = PVValTemplate.getTemplate((String) prop.val).make(binding)
        if (propVal != null && !"null".equals(propVal)) {
          String predicate = prop.predicate ?: "eq"


          if (nativeType.isArray()) {

            nativeType = nativeType.getComponentType();


            def propVals;

            try {
              propVals = slurper.parseText(propVal)

            }
            catch (Throwable t){
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
                  , sb
                );


              }
            }

          } else {
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
              , sb
            );
          }


        }


      }
    }

  }
  return matchReqs;

}


def getTopHit(g, Long[] potentialHitIDs, int numHitsRequiredForMatch, HashMap<String, List<Long>> matchIdsByVertexType, String vertexTypeStr, Map<String, List<EdgeRequest>> edgeReqsByVertexType, StringBuffer sb = null) {

  sb?.append("\nIn getTopHit() -- vertType = ${vertexTypeStr} ; potentialHitIDs = ${potentialHitIDs} ")
  Long[] topHits = getTopHits(potentialHitIDs as Long[], numHitsRequiredForMatch, sb)

  sb?.append("\nIn getTopHit() -- vertType = ${vertexTypeStr} ; topHits = ${topHits} ")

  Long topHit = null
  Integer numEdgesRequired = edgeReqsByVertexType.get(vertexTypeStr)?.size()

  if (numEdgesRequired != null && numEdgesRequired > 0 && topHits.size() > 1) {
    // Sanity check: we now have one or more candidates, so let's check
    // if this has conns to other vertices in our little world
    def otherTopHits = getOtherTopHits(matchIdsByVertexType, vertexTypeStr, 1, sb)

    int ilen = topHits.size()

    for (int i = 0; i < ilen; i++) {

      Long[] tempTopHits = findMatchingNeighboursFromSingleRequired(g, topHits[i] as Long, otherTopHits as Set<Long>, sb)
      if (tempTopHits?.size() > 0) {
        topHit = tempTopHits[0]
        break
      }
    }


  } else {
    if (topHits.size() > 0) {
      topHit = topHits[0]
    }

  }

  sb?.append("\nIn getTopHit() -- vertType = ${vertexTypeStr} ; topHit  = ${topHit} ")

  return topHit;

}

def addNewVertexFromMatchReqs(g, String vertexTypeStr, List<MatchReq> matchReqsForThisVertexType, StringBuffer sb = null) {

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

    Long retVal = localTrav.next().id() as Long

    sb?.append("\n in addNewVertexFromMatchReqs() - added new vertex of type ${vertexTypeStr}; id = ${retVal}")
    return retVal
  }
  sb?.append("\n in addNewVertexFromMatchReqs() - could not add ${vertexTypeStr}; no match requests marked for update");

  return null;


}


def updateExistingVertexWithMatchReqs(g, Long vertexId, List<MatchReq> matchReqsForThisVertexName, StringBuffer sb = null) {

  def localTrav = g
  def deletionTrav = g
  sb?.append("\n in updateExistingVertexWithMatchReqs() - about to start Updating vertex of id ${vertexId}; ${matchReqsForThisVertexName}")

  localTrav = localTrav.V(vertexId)

  boolean atLeastOneUpdate = false;
  matchReqsForThisVertexName.each { it ->
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

  // Long retVal = localTrav.next().id() as Long

  // return retVal


}


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

  String getToVertexLabel() {
    return toVertexName
  }

  void setToVertexLabel(String toVertexName) {
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
    return "${label} = ($fromVertexName)->($toVertexLabel)"
  }
}


def parseEdges(def rules) {

  Map<String, List<EdgeRequest>> edgeReqsByVertexName = new HashMap<>()
  Set<EdgeRequest> edgeReqs = new HashSet<>()

  rules.edges.each { it ->
    String fromVertexName = it.fromVertexName ?: it.fromVertexLabel
    String toVertexName = it.toVertexName ?: it.toVertexLabel
    String label = it.label

    EdgeRequest req = new EdgeRequest(label, fromVertexName, toVertexName);

    edgeReqs.add(req)
    fromEdgeList = edgeReqsByVertexName.computeIfAbsent(fromVertexName, { k -> new ArrayList<EdgeRequest>() })
    fromEdgeList.add(req)
    toEdgeList = edgeReqsByVertexName.computeIfAbsent(toVertexName, { k -> new ArrayList<EdgeRequest>() })
    toEdgeList.add(req)

  }

  return [edgeReqsByVertexName, edgeReqs]
}

def createEdges(gTrav, Set<EdgeRequest> edgeReqs, Map<String, Long> finalVertexIdByVertexName, StringBuffer sb = null) {

  edgeReqs.each { it ->

    sb?.append("\n in createEdges; edgeReq = $it ")

    sb?.append("\n in createEdges; finalVertexIdByVertexName = $finalVertexIdByVertexName ")

    Long fromId = finalVertexIdByVertexName.get(it.fromVertexName)
    Long toId = finalVertexIdByVertexName.get(it.toVertexName)

    sb?.append("\n in createEdges; from=$fromId; to=$toId ")

    if (fromId != null && toId != null) {

      def foundIds = gTrav.V(toId)
        .both()
        .hasId(within(fromId)).id()
        .toSet() as Long[]

      sb?.append("\n in createEdges $foundIds")

      if (foundIds.size() == 0) {
        def fromV = gTrav.V(fromId)
        def toV = gTrav.V(toId)
        sb?.append("\n in createEdges about to create new Edges from  $fromId to $toId")
        gTrav.addE(it.label).from(fromV).to(toV).next()
      } else {
        sb?.append("\n in createEdges SKIPPING Edge creations")

      }
    } else {
      sb?.append("\n in createEdges SKIPPING Edge creations")

    }


  }
}

def ingestDataUsingRules(graph, g, Map<String, String> bindings, String jsonRules, StringBuffer sb = null) {
  Map<String, Long> finalVertexIdByVertexName = new HashMap<>();

  def jsonSlurper = new JsonSlurper()
  def rules = jsonSlurper.parseText(jsonRules)

  def (edgeReqsByVertexName, edgeReqs) = parseEdges(rules.updatereq)
  trans = graph.tx()
  try {
    if (!trans.isOpen()) {
      trans.open()
    }

    def matchReqs = getMatchRequests(bindings, rules.updatereq, jsonRules, sb)
    def (matchIdsByVertexType, vertexListsByVertexName) = matchVertices(g, matchReqs, 10, sb);

    matchIdsByVertexType.each { vertexTypeStr, potentialHitIDs ->

      List<MatchReq> matchReqsForThisVertexType = vertexListsByVertexName.get(vertexTypeStr)
//            int numHitsRequiredForMatch = matchReqsForThisVertexType?.size()
//
//            if (numHitsRequiredForMatch > 0) {
//                numHitsRequiredForMatch += (numHitsRequiredForMatch - 1)
//            }

      int numHitsRequiredForMatch = 1;

      Long topHit = getTopHit(g
        , potentialHitIDs as Long[]
        , (int) numHitsRequiredForMatch
        , (HashMap<String, List<Long>>) matchIdsByVertexType
        , (String) vertexTypeStr
        , (Map<String, List<EdgeRequest>>) edgeReqsByVertexName
        , sb)

      if (topHit != null) {

        updateExistingVertexWithMatchReqs(g, topHit, matchReqsForThisVertexType, sb)
        finalVertexIdByVertexName.put((String) vertexTypeStr, topHit)
      } else {
        Long newVertexId = addNewVertexFromMatchReqs(g, (String) vertexTypeStr, matchReqsForThisVertexType, sb)
        finalVertexIdByVertexName.put((String) vertexTypeStr, newVertexId)

      }


    }

    createEdges(g, (Set<EdgeRequest>) edgeReqs, (Map<String, Long>) finalVertexIdByVertexName, sb)


    trans.commit()
  } catch (Throwable t) {
    trans.rollback()
    throw t
  } finally {
    trans.close()
  }

  return finalVertexIdByVertexName;
}


def ingestRecordListUsingRules(graph, g, List<Map<String, String>> recordList, String jsonRules, StringBuffer sb = null) {

  def jsonSlurper = new JsonSlurper()
  def rules = jsonSlurper.parseText(jsonRules)

  def (edgeReqsByVertexName, edgeReqs) = parseEdges(rules.updatereq)
  trans = graph.tx()
  try {
    if (!trans.isOpen()) {
      trans.open()
    }

    for (Map<String, String> item in recordList) {

      def matchReqs = getMatchRequests(item, rules.updatereq, jsonRules, sb)
      def (matchIdsByVertexName, vertexListsByVertexName) = matchVertices(g, matchReqs, 10, sb);

      Map<String, Long> finalVertexIdByVertexName = new HashMap<>();
      matchIdsByVertexName.each { vertexNameStr, potentialHitIDs ->

        List<MatchReq> matchReqsForThisVertexName = vertexListsByVertexName.get(vertexNameStr)
        int numHitsRequiredForMatch = 1;
//                        matchReqsForThisVertexName?.size()
//
//                if (numHitsRequiredForMatch > 0) {
//                    numHitsRequiredForMatch += (numHitsRequiredForMatch - 1)
//                }

        Long topHit = getTopHit(g
          , potentialHitIDs as Long[]
          , (int) numHitsRequiredForMatch
          , (HashMap<String, List<Long>>) matchIdsByVertexName
          , (String) vertexNameStr
          , (Map<String, List<EdgeRequest>>) edgeReqsByVertexName
          , sb)

        if (topHit != null) {

          updateExistingVertexWithMatchReqs(g, topHit, matchReqsForThisVertexName, sb)
          finalVertexIdByVertexName.put((String) vertexNameStr, topHit)
        } else {
          Long newVertexId = addNewVertexFromMatchReqs(g, (String) vertexNameStr, matchReqsForThisVertexName, sb)
          finalVertexIdByVertexName.put((String) vertexNameStr, newVertexId)

        }


      }

      createEdges(g, (Set<EdgeRequest>) edgeReqs, (Map<String, Long>) finalVertexIdByVertexName, sb)


    }


    trans.commit()
  } catch (Throwable t) {
    trans.rollback()
    throw t
  } finally {
    trans.close()
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
    "label": "Person.Natural"
     ,"props":
    [
      {
      "name": "Person.Natural.Full_Name"
       ,"val": "${pg_nlp_res_person}"
       ,"predicate": "eq"
       ,"type":"[Ljava.lang.String;"
       ,"excludeFromUpdate": true
       ,"postProcessor": "${it?.toUpperCase()}"

      }
    ]
    }
   ,{
    "label": "Location.Address"
     ,"props":
    [
      {
      "name": "Location.Address.parser.postcode"
       ,"val": "${pg_nlp_res_postcode}"
       ,"type":"[Ljava.lang.String;"
       ,"postProcessorVar": "eachPostCode"
       ,"postProcessor": "${com.pontusvision.utils.PostCode.format(eachPostCode)}"
       ,"excludeFromUpdate": true
      }

    ]

    }
   ,{
    "label": "Object.Email_Address"
     ,"props":
    [
      {
      "name": "Object.Email_Address.Email"
       ,"val": "${pg_nlp_res_emailaddress}"
       ,"type":"[Ljava.lang.String;"
       ,"excludeFromUpdate": true
      }
    ]

    }
   ,{
    "label": "Object.Insurance_Policy"
     ,"props":
    [
      {
      "name": "Object.Insurance_Policy.Number"
       ,"val": "${pg_nlp_res_policy_number}"
       ,"type":"[Ljava.lang.String;"
       ,"excludeFromUpdate": true
      }
    ]

    }
   ,{
    "label": "Event.Ingestion"
     ,"props":
    [
      {
      "name": "Event.Ingestion.Type"
       ,"val": "MarketingEmailSystem"
       ,"excludeFromSearch": true
      }
     ,{
      "name": "Event.Ingestion.Operation"
       ,"val": "Upsert"
       ,"excludeFromSearch": true
      }
     ,{
      "name": "Event.Ingestion.Domain_b64"
       ,"val": "${original_request?.bytes?.encodeBase64()?.toString()}"
       ,"excludeFromSearch": true
      }
     ,{
      "name": "Event.Ingestion.Metadata_Create_Date"
       ,"val": "${new Date()}"
       ,"excludeFromSearch": true
      }

    ]
    }
  ]
   ,"edges":
    [
      { "label": "Has_Ingestion_Event", "fromVertexName": "Person.Natural", "toVertexName": "Event.Ingestion"  }
    ]
  }
}
'''

// edgeLabel = createEdgeLabel(mgmt, "Has_Policy")
// trigger the String.Mixin() call in the static c-tor

// sb.append("${PostCode.format(pg_ZipCode)}")


def bindings = [:]


bindings['pg_metadataController'] = 'abc inc';
bindings['pg_metadataGDPRStatus'] = 'Personal';
bindings['pg_metadataLineage'] = 'https://randomuser.me/api/?format=csv';
bindings['pg_nlp_res_address'] = '[" ","ddress: "]';
bindings['pg_nlp_res_company'] = '["DoD"]';
bindings['pg_nlp_res_cred_card'] = '[]';
bindings['pg_nlp_res_emailaddress'] = '["retoh@optonline.net"]';
bindings['pg_nlp_res_location'] = '["Greenock","UK","London","Paris"]';
bindings['pg_nlp_res_city'] = '[]';
bindings['pg_nlp_res_person'] = '["John Smith"," ","\t1: "]';
bindings['pg_nlp_res_phone'] = '[null,"01475545350","01","5350","47","554"]';
bindings['pg_nlp_res_postcode'] = '["PA15",null,"PA15 4SY"]';
bindings['pg_nlp_res_policy_number'] = '[]';



StringBuffer sb = new StringBuffer();
try {
    ingestDataUsingRules(graph, g, bindings, rulesStr, sb)
}
catch (Throwable t) {
    String stackTrace = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)

    sb.append("\n$t\n$stackTrace")

    throw new Throwable(sb.toString())


}
sb.toString()

*/

/*
def jsonSlurper = new JsonSlurper()
def listOfMaps = jsonSlurper.parseText '''
[ {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "MICHAEL",
  "pg_LastName" : "PLATINI",
  "pg_ZipCode" : "B6 7NP",
  "pg_City" : "Birmingham",
  "pg_NumOfMarketingEmailSent" : "15",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "8",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "kiddailey@hotmail.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "18/10/1969",
  "pg_MailBounced" : "1",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "JUDY",
  "pg_LastName" : "CAMEROON",
  "pg_ZipCode" : "B60 1DX",
  "pg_City" : "Bromsgrove",
  "pg_NumOfMarketingEmailSent" : "13",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "7",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "knorr@live.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : "yeugo@hotmail.com",
  "pg_PermissionToContactSecondary" : "No",
  "pg_DateofBirth" : "04/12/1972",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Female",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "SACHIN",
  "pg_LastName" : "KUMAR",
  "pg_ZipCode" : "B742NH",
  "pg_City" : "Coldfield",
  "pg_NumOfMarketingEmailSent" : "11",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "7",
  "pg_NumTotalClickThrough" : "13",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "mbswan@optonline.net",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "12/09/1973",
  "pg_MailBounced" : "1",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "YES",
  "pg_FirstName" : "CORY",
  "pg_LastName" : "RHODES",
  "pg_ZipCode" : "DE75 7PQ",
  "pg_City" : "Heanor",
  "pg_NumOfMarketingEmailSent" : "10",
  "pg_NumOpened" : "9",
  "pg_NumOfBrandEnagementEmailSent" : "7",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "dieman@yahoo.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "05/04/1975",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : "98497047",
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : "Open",
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : "VVAP"
}, {
  "pg_ExistingCustomer" : "YES",
  "pg_FirstName" : "MICKEY",
  "pg_LastName" : "CRISTINO",
  "pg_ZipCode" : "NE70 7QG",
  "pg_City" : "Belford",
  "pg_NumOfMarketingEmailSent" : "13",
  "pg_NumOpened" : "9",
  "pg_NumOfBrandEnagementEmailSent" : "10",
  "pg_NumTotalClickThrough" : "14",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "jaxweb@sbcglobal.net",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "31/08/1976",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Female",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : "10330435",
  "pg_PolicyType" : "Non- Renewable",
  "pg_PolicyStatus" : "Open",
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : "WUFP"
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "HERMAN",
  "pg_LastName" : "STONE",
  "pg_ZipCode" : "HS8 5QX",
  "pg_City" : "South Uist",
  "pg_NumOfMarketingEmailSent" : "13",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "9",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "hermanab@live.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "13/08/1979",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "YES",
  "pg_FirstName" : "JOHN",
  "pg_LastName" : "SMITH",
  "pg_ZipCode" : "PA15 4SY",
  "pg_City" : "Greenock",
  "pg_NumOfMarketingEmailSent" : "15",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "9",
  "pg_NumTotalClickThrough" : "10",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "retoh@optonline.net",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "08/04/1973",
  "pg_MailBounced" : "1",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : "10330434",
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : "Open",
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : "RIKR"
}, {
  "pg_ExistingCustomer" : "YES",
  "pg_FirstName" : "TRACY",
  "pg_LastName" : "NOAH",
  "pg_ZipCode" : "CM2 9HX",
  "pg_City" : "Chemsford",
  "pg_NumOfMarketingEmailSent" : "14",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "9",
  "pg_NumTotalClickThrough" : "10",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "tromey@mac.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "26/10/1982",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Female",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : "49949479",
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : "Open",
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : "JUDV"
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "JOHN",
  "pg_LastName" : "DAILEY",
  "pg_ZipCode" : "BH8 1  HM",
  "pg_City" : "Bournemouth",
  "pg_NumOfMarketingEmailSent" : "12",
  "pg_NumOpened" : "10",
  "pg_NumOfBrandEnagementEmailSent" : "10",
  "pg_NumTotalClickThrough" : "13",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "sabren@icloud.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : "kuaoiio@gmail.com",
  "pg_PermissionToContactSecondary" : "Yes",
  "pg_DateofBirth" : "20/11/1984",
  "pg_MailBounced" : "2",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "KEITH",
  "pg_LastName" : "SAUNDERS",
  "pg_ZipCode" : "PH34 3ET",
  "pg_City" : "Speam Bridge",
  "pg_NumOfMarketingEmailSent" : "12",
  "pg_NumOpened" : "9",
  "pg_NumOfBrandEnagementEmailSent" : "10",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "shazow@yahoo.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "22/01/1987",
  "pg_MailBounced" : "1",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "MICHELLE",
  "pg_LastName" : "DAVIDSON",
  "pg_ZipCode" : "SG13 7EJ",
  "pg_City" : "Hertford",
  "pg_NumOfMarketingEmailSent" : "11",
  "pg_NumOpened" : "10",
  "pg_NumOfBrandEnagementEmailSent" : "8",
  "pg_NumTotalClickThrough" : "14",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "moxfulder@sbcglobal.net",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "27/01/1987",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Female",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
} ]
'''


def rulesStr =  '''
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
			"name": "Person.Natural.Full_Name"
		   ,"val": "${pg_FirstName?.toUpperCase()?.trim()} ${pg_LastName?.toUpperCase()?.trim()}"
		   ,"predicate": "eq"
		   ,"mandatoryInSearch": true
		  }
		 ,{
			"name": "Person.Natural.Last_Name"
		   ,"val": "${pg_LastName?.toUpperCase()?.trim()}"
		   ,"excludeFromSubsequenceSearch": true
		  }
		 ,{
			"name": "Person.Natural.Date_Of_Birth"
		   ,"val": "${pg_DateofBirth}"
		   ,"type": "java.util.Date"
		   ,"mandatoryInSearch": true

		  }
		 ,{
			"name": "Person.Natural.Gender"
		   ,"val": "${pg_Sex?.toUpperCase()}"
		   ,"excludeFromSubsequenceSearch": true

		  }
		 ,{
			"name": "Person.Natural.Title"
		   ,"val": "${'MALE' == pg_Sex?.toUpperCase()? 'MR':'MS'}"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Person.Natural.Nationality"
		   ,"val": "Not Provided"
		   ,"excludeFromSearch": true
		  }
		]
	  }
	 ,{
		"label": "Location.Address"
		,"props":
		[
		  {
			"name": "Location.Address.parser.postcode"
		   ,"val": "${com.pontusvision.utils.PostCode.format(pg_ZipCode)}"
		   ,"mandatoryInSearch": true
		  }
		 ,{
			"name": "Location.Address.parser.city"
		   ,"val": "${pg_City?.toLowerCase()}"
		   ,"excludeFromSubsequenceSearch": true
		  }
		 ,{
			"name": "Location.Address.Post_Code"
		   ,"val": "${com.pontusvision.utils.PostCode.format(pg_ZipCode)}"
		   ,"excludeFromSearch": true
		  }
		]

	  }
	 ,{
		"label": "Object.Email_Address"
		,"props":
		[
		  {
			"name": "Object.Email_Address.Email"
		   ,"val": "${pg_PrimaryEmailAddress}"
		  }
		]

	  }
	 ,{
		"label": "Object.Insurance_Policy"
		,"props":
		[
		  {
			"name": "Object.Insurance_Policy.Number"
		   ,"val": "${pg_Policynumber}"
		   ,"mandatoryInSearch": true
		  }
		 ,{
			"name": "Object.Insurance_Policy.Type"
		   ,"val": "${pg_PolicyType}"
		   ,"excludeFromSubsequenceSearch": true

		  }
		 ,{
			"name": "Object.Insurance_Policy.Status"
		   ,"val": "${pg_PolicyStatus}"
		   ,"excludeFromSearch": true
		  }

		]

	  }
	 ,{
		"label": "Event.Ingestion"
	   ,"props":
		[
		  {
			"name": "Event.Ingestion.Type"
		   ,"val": "MarketingEmailSystem"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event.Ingestion.Operation"
		   ,"val": "Upsert"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event.Ingestion.Domain_b64"
		   ,"val": "${original_request?.bytes?.encodeBase64()?.toString()}"
		   ,"excludeFromSearch": true
		  }
		 ,{
			"name": "Event.Ingestion.Metadata_Create_Date"
		   ,"val": "${new Date()}"
		   ,"excludeFromSearch": true
		  }

		]
	  }
	]
   ,"edges":
    [
      { "label": "Uses_Email", "fromVertexName": "Person.Natural", "toVertexName": "Object.Email_Address" }
     ,{ "label": "Lives", "fromVertexName": "Person.Natural", "toVertexName": "Location.Address"  }
     ,{ "label": "Has_Policy", "fromVertexName": "Person.Natural", "toVertexName": "Object.Insurance_Policy"  }
     ,{ "label": "Has_Ingestion_Event", "fromVertexName": "Person.Natural", "toVertexName": "Event.Ingestion"  }
    ]
  }
}

'''
// edgeLabel = createEdgeLabel(mgmt, "Has_Policy")

StringBuffer sb = new StringBuffer ()

// trigger the String.Mixin() call in the static c-tor

// sb.append("${PostCode.format(pg_ZipCode)}")
try{
    ingestRecordListUsingRules(graph, g, listOfMaps, rulesStr, sb)
}
catch (Throwable t){
    String stackTrace = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)

    sb.append("\n$t\n$stackTrace")


}
sb.toString()

// g.E().drop().iterate()
// g.V().drop().iterate()

// describeSchema()
// g.V()

*/
