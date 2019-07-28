import com.pontusvision.utils.ElasticSearchHelper
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.apache.commons.math3.util.Pair
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.*
import org.janusgraph.core.schema.*
import org.janusgraph.graphdb.types.vertices.JanusGraphSchemaVertex

import java.util.concurrent.atomic.AtomicInteger

LinkedHashMap globals = [:]
globals << [g: ((JanusGraph) graph).traversal() as GraphTraversalSource]
globals << [mgmt: ((JanusGraph) graph).openManagement() as JanusGraphManagement]


@CompileStatic
def loadSchema(JanusGraph graph, String... files) {
  StringBuffer sb = new StringBuffer()

  JanusGraphManagement mgmt = graph.openManagement();
  Map<String, PropertyKey> propsMap = [:]
  for (f in files) {
    try {

      def jsonFile = new File(f);

      if (jsonFile.exists()) {
        def jsonStr = jsonFile.text
        def json = new JsonSlurper().parseText(jsonStr)
        sb?.append("\nLoading File ${f}\n")


        sb?.append("\nAbout to create elastic search templates\n")

        createElasticTemplates(json, sb);

        sb?.append("\nAbout to create vertex labels\n")

        addVertexLabels(mgmt, json, sb)
        sb?.append("\nAbout to create edge labels\n")

        addEdgeLabels(mgmt, json, sb)

        sb?.append("\nAbout to create Property Keys\n")
        System.out.println("\n\n\nAbout to create Property Keys\n\n\n\n")

        propsMap << addpropertyKeys(mgmt, json, sb)

        sb?.append("\nAbout to load vertex Indices\n")

        System.out.println("\n\n\nAbout to load vertex Indices\n\n\n\n")
        addIndexes(mgmt, json['vertexIndexes'], false, propsMap, sb)

        sb?.append("\nAbout to load edge Indices\n")
        System.out.println("\n\n\nAbout to load edge Indices\n\n\n\n")
        addIndexes(mgmt, json['edgeIndexes'], true, propsMap, sb)
        sb?.append("Loaded File ${f}\n")

      } else {
        sb?.append("NOT LOADING FILE ${f}\n")
      }

    } catch (Throwable t) {
      sb?.append('Failed to load schema!\n')?.append(t);
      t.printStackTrace()

    }
  }
  mgmt.commit()

  sb?.append('Done!\n')
  return sb?.toString()
}


/*
{
 "elasticSearchTemplates": [
    {
      "name": "persondatamixedidx",
      "body": {
        "index_patterns": [
          "janusgraph*persondatamixedidx"
        ],
        "order": 0,
        "settings": {
          "analysis": {
            "filter": {
              "pg_word_delimiter_filter": {
                "type": "word_delimiter",
                "catenate_words": true,
                "preserve_original": true,
                "generate_word_parts": false,
                "generate_number_parts": false
              }
            },
            "analyzer": {
              "pg_word_delimiter": {
                "filter": [
                  "lowercase",
                  "pg_word_delimiter_filter"
                ],
                "tokenizer": "whitespace"
              }
            }
          }
        }
      }
    }
  ],
    ...
}
 */

def createElasticTemplates(def json, StringBuffer sb) {

  if (json['elasticSearchTemplates']) {
    json['elasticSearchTemplates'].each {
      String resp = ElasticSearchHelper.createTemplate((String) it.name, (String) JsonOutput.toJson(it.body).toString())
      sb?.append("\n  Adding elasticSearchTemplate for ${it.name}; result = ${resp}\n")
    }
  }
}

def addIndexes(JanusGraphManagement mgmt, def json, boolean isEdge, Map<String, PropertyKey> propsMap, StringBuffer sb = null) {
  if (!json) {
    return
  }
  json.each {
    List<String> propertyKeys = it.propertyKeys
    if (!propertyKeys) {
      return
    }

    String name = it.name

    def props = []
    propertyKeys.each { key ->
      def prop = propsMap[key]
      if (!prop) {
        throw new RuntimeException("Failed to create index - $name, because property - $key doesn't exist")
      }
      props << prop
    }

    def composite = it.composite
    boolean unique = it.unique
    def mixedIndex = it.mixedIndex
    String mapping = it.mapping

    sb?.append("\nin AddIndexes() - creating ${isEdge ? 'Edge' : 'Vertex'} index ${name}; composite = ${composite}; unique = ${unique}; mixedIndex = ${mixedIndex}; mapping = ${mapping}\n\n")

    if (mixedIndex && composite) {
      throw new RuntimeException("Failed to create index - $name, because it can't be both MixedIndex and CompositeIndex")
    }

    if (mapping && composite) {
      throw new RuntimeException("Failed to create index - $name, because it can't be CompositeIndex and have mapping")
    }

    def idxCreated
    if (mixedIndex) {
      idxCreated = createMixedIdx(mgmt, name, isEdge, propertyKeys, mapping, (Map<String, Object>) it.propertyKeysMappings, sb)
    } else {
      idxCreated = createCompIdx(mgmt, name, isEdge, unique, props as PropertyKey[])
    }

    def status = idxCreated ? 'Success added' : 'Failed to add'
    sb?.append("$status index - $name\n")
  }
}

def addVertexLabels(JanusGraphManagement mgmt, def json, StringBuffer sb = null) {
  json['vertexLabels'].each {
    String name = it.name
    createVertexLabel(mgmt, name)
    sb?.append("Success added vertext label - $name\n")
  }
}

def addEdgeLabels(JanusGraphManagement mgmt, def json, StringBuffer sb = null) {
  json['edgeLabels'].each {
    def name = it.name
    createEdgeLabel(mgmt, name)
    sb?.append("Success added edge label - $name\n")
  }
}

Map<String, PropertyKey> addpropertyKeys(JanusGraphManagement mgmt, def json, StringBuffer sb = null) {
  Map<String, PropertyKey> map = [:]
  json['propertyKeys'].each {
    String name = it.name
    Class<?> typeClass = Class.forName(getClass(it.dataType))
    String cardinality = it.cardinality
    org.janusgraph.core.Cardinality card = Enum.valueOf(org.janusgraph.core.Cardinality, cardinality);
    //cardinality == 'SET' ? org.janusgraph.core.Cardinality.SET : org.janusgraph.core.Cardinality.SINGLE
    def prop = createProp(mgmt, name, typeClass, card);
    sb?.append("Success added property key - $name\n")
    map[name] = prop
  }
  return map
}

String getClass(def type) {
  return (type == 'Date') ? "java.util.Date" : "java.lang.$type"
}


@CompileStatic
PropertyKey createProp(JanusGraphManagement mgmt, String keyName, Class<?> classType, org.janusgraph.core.Cardinality card) {

  try {
    PropertyKey key;
    if (!mgmt.containsPropertyKey(keyName)) {
      key = mgmt.makePropertyKey(keyName).dataType(classType).cardinality(card).make();
      Long id = ((JanusGraphSchemaVertex) key).id() as Long;

      System.out.println("keyName = ${keyName}, keyID = " + id)
    } else {
      key = mgmt.getPropertyKey(keyName);
      Long id = ((JanusGraphSchemaVertex) key).id() as Long;
      System.out.println("keyName = ${keyName}, keyID = " + id)

    }
    return key;
  }
  catch (Throwable t) {
    t.printStackTrace();
  }
  return null
}

@CompileStatic
JanusGraphIndex createCompIdx(JanusGraphManagement mgmt, String idxName, boolean isUnique, PropertyKey... props) {
  return createCompIdx(mgmt, idxName, false, isUnique, props)
}

@CompileStatic
JanusGraphIndex createCompIdx(JanusGraphManagement mgmt, String idxName, boolean isEdge, boolean isUnique, PropertyKey... props) {

  try {
    if (!mgmt.containsGraphIndex(idxName)) {
      def clazz = isEdge ? Edge.class : Vertex.class
      JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)
      if (isUnique) {
        ib.unique()
      }
      for (PropertyKey prop in props) {
        ib.addKey(prop);
//            ib.addKey(prop,Mapping.STRING.asParameter());
        System.out.println("creating Comp IDX ${idxName} for key ${prop}");

      }

      return ib.buildCompositeIndex();
    } else {
      return mgmt.getGraphIndex(idxName);
    }
  }
  catch (Throwable t) {
    t.printStackTrace();
  }
  return null
}


@CompileStatic
JanusGraphIndex createCompIdx(JanusGraphManagement mgmt, String idxName, PropertyKey... props) {
  return createCompIdx(mgmt, idxName, false, props)
}

@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, boolean isEdge, Pair<PropertyKey, Mapping>... pairs) {
  try {
    if (!mgmt.containsGraphIndex(idxName)) {
      def clazz = isEdge ? Edge.class : Vertex.class
      JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)

      for (Pair<PropertyKey, Mapping> pair in pairs) {

        PropertyKey prop = pair.getFirst();
        Mapping mapping = pair.getSecond();
        ib.addKey(prop, mapping.asParameter());
//            ib.addKey(prop,Mapping.STRING.asParameter());
        System.out.println("creating IDX ${idxName} for key ${prop}");

      }
      return ib.buildMixedIndex("search");
    } else {
      return mgmt.getGraphIndex(idxName);
    }
  }
  catch (Throwable t) {
    t.printStackTrace();
  }
  return null
}


@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, Pair<PropertyKey, Mapping>... props) {
  return createMixedIdx(mgmt, idxName, false, props)
}

/*
    {
      "name": "by_O.vehicle_MixedIdx",
      "propertyKeys": [
        "O.vehicle.meta.m_create",
        "O.vehicle.meta.m_subtype",
        "O.vehicle.meta.m_owner",
        "O.vehicle.core.registrationNumber",
        "O.vehicle.meta.m_update",
        "O.vehicle.meta.m_source",
        "O.vehicle.core.vehicleType",
        "O.vehicle.meta.m_version",
        "O.vehicle.core.registrationCountry",
        "O.vehicle.meta.m_createBy",
        "O.vehicle.meta.m_use",
        "O.vehicle.core.objectType",
        "O.vehicle.meta.m_type",
        "O.vehicle.meta.m_correlationID",
        "O.vehicle.meta.m_identityId"
      ],
      "propertyKeysMappings": {
        "O.vehicle.core.registrationNumber": {
          "mapping": "TEXT"
        }
      },
      "composite": false,
      "unique": false,
      "indexOnly": null,
      "mixedIndex": "search",
      "mapping": null
    },

 */
//(PropertyKey vs String representing a Mapping
@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, boolean isEdge, List<String> props, String mappingStr, Map<String, Object> propertyKeysMappings, StringBuffer sb = null) {
  try {

    if (!mgmt.containsGraphIndex(idxName)) {
      def clazz = isEdge ? Edge.class : Vertex.class
      JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)

      sb?.append("\nin createMixedIdx() - creating ${idxName} with props ${props} and propertyKeysMappings ${propertyKeysMappings}\n\n")

      props.each { property ->
        PropertyKey propKey = mgmt.getPropertyKey(property)
        if (!propKey) {
          throw new RuntimeException("$property not found")
        }


        def keyMapping = propertyKeysMappings ? propertyKeysMappings[property] : null
        if (keyMapping) {
          String mappingVal = keyMapping['mapping']
          Parameter mappingParam = Mapping.valueOf(mappingVal)?.asParameter()
          if (!mappingParam) {
            throw new RuntimeException("Cannot get mapping from $mappingVal for $property")
          }

          Map<String, String> analyzer = (Map<String, String>) keyMapping['analyzer']
          if (analyzer) {
            sb?.append("\nAdding analyzer details to IDX ${idxName}: ${analyzer.name} = ${analyzer.value} ")
            ib.addKey(propKey, mappingParam, Parameter.of(analyzer['name'], analyzer['value']))
          } else {
            ib.addKey(propKey, mappingParam)
          }
        } else if (mappingStr) {
          Mapping mapping = Mapping.valueOf(mappingStr)
          if (!mapping) {
            mapping = Mapping.DEFAULT
          }
          ib.addKey(propKey, mapping.asParameter());
        } else {
          //mappingString not specified, depends on the property's type
          if (propKey.dataType() == String.class) {
            ib.addKey(propKey, Mapping.STRING.asParameter());
          } else {
            ib.addKey(propKey);
          }
        }
      }
      System.out.println("creating IDX ${idxName} for key(s) - ${props}")
      return ib.buildMixedIndex("search")
    } else {
      return mgmt.getGraphIndex(idxName)
    }
  }
  catch (Throwable t) {
    t.printStackTrace();
  }

  return null
}


@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, PropertyKey... props) {
  return createMixedIdx(mgmt, idxName, false, props)
}


@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, boolean isEdge, PropertyKey... props) {
  try {
    if (!mgmt.containsGraphIndex(idxName)) {
      def clazz = isEdge ? Edge.class : Vertex.class
      JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)
      for (PropertyKey prop in props) {

        if (prop.dataType() == String.class) {
          ib.addKey(prop, Mapping.STRING.asParameter());

        } else {
          ib.addKey(prop);

        }

//                ib.addKey(prop,Mapping.TEXTSTRING.asParameter());
        System.out.println("creating IDX ${idxName} for key ${prop}");

      }
      return ib.buildMixedIndex("search");
    } else {
      return mgmt.getGraphIndex(idxName);

    }
  }
  catch (Throwable t) {
    t.printStackTrace();
  }


}


PropertyKey createVertexLabel(JanusGraphManagement mgmt, String labelName) {

  try {
    if (!mgmt.containsVertexLabel(labelName)) {
      mgmt.makeVertexLabel(labelName).make()
    }
    return createProp(mgmt, "Metadata.Type." + labelName, String.class, org.janusgraph.core.Cardinality.SINGLE);

  }
  catch (Throwable t) {
    t.printStackTrace();
  }
  return null;
}

@CompileStatic
def createEdgeLabel(JanusGraphManagement mgmt, String labelName) {

  try {
    if (!mgmt.containsEdgeLabel(labelName)) {
      return mgmt.makeEdgeLabel(labelName).make()
    }
    return mgmt.getEdgeLabel(labelName)
  }
  catch (Throwable t) {
    t.printStackTrace();
  }
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


@CompileDynamic
def describeSchema(JanusGraph graph, StringBuffer sb = new StringBuffer()) {
  Schema schema = new Schema(graph, sb)
  schema.describeAll()
  sb.toString()
}

@CompileDynamic

def describeSchema(StringBuffer sb = new StringBuffer()) {
  Schema schema = new Schema((JanusGraph) globals['graph'], sb)
  schema.describeAll()
  sb.toString()
}

@CompileDynamic

def dumpJsonSchema(StringBuffer sb = new StringBuffer()) {
  Schema schema = new Schema((JanusGraph) globals['graph'], sb)
  schema.dumpJsonSchema()
  sb.toString()

}

@CompileStatic
String dumpJsonSchema(JanusGraph graph, StringBuffer sb = new StringBuffer()) {
  Schema schema = new Schema(graph, sb)
  schema.dumpJsonSchema()
  return sb.toString()

}

@CompileStatic
class Schema {
  JanusGraph graph;
  StringBuffer sb;

  public Schema() {}

  @CompileStatic
  public Schema(JanusGraph graph, StringBuffer sb) {
    this.graph = graph
    this.sb = sb
  }


  @CompileDynamic
  public void describeAll() {
    try {
      ensureMgmtOpen()
      printVertex(getVertex([]))
      printEdge(getEdge([]))
      printPropertyKey(getPropertyKey([]))
      printIndex(getIndex([]))
    } finally {
      ensureMgmtClosed()
    }
  }

  @CompileDynamic
  public void dumpJsonSchema() {
    try {
      ensureMgmtOpen()

      this.sb.setLength(0)
      this.sb.append('{\n')
      printPropertyKeyJson(getPropertyKey([]));
      printVertexLabelJson(getVertex([]));
      printEdgeLabelJson(getEdge([]));
      printVertexIndicesJson(getManagement().getGraphIndexes(Vertex.class));

      this.sb.append('\n}\n')

    } finally {
      ensureMgmtClosed()
    }
  }

  @CompileDynamic
  public Iterable<VertexLabel> getVertex(Object args) {
    if (args) {
      def result = getManagement().getVertexLabels().findAll {
        args.contains(it.name())
      }
      result.toSorted { a, b -> a.name() <=> b.name() }
      return result
    } else {
      def result = getManagement().getVertexLabels()
      return result.toSorted { a, b -> a.name() <=> b.name() }
    }
  }

  @CompileDynamic
  public void printVertex(Iterable<VertexLabel> args) {
    def pattern = "%-30s  | %11s | %6s\n"
    this.sb.append(String.format(pattern, '\nVertex Label', 'Partitioned', 'Static'))
    this.sb.append(String.format(pattern, '------------', '-----------', '------'))
    args.each {
      this.sb.append(String.format(pattern, it.name().take(30), it.isPartitioned(), it.isStatic()))
    }
    this.sb.append(String.format(pattern, '------------', '-----------', '------'))
    this.sb.append('\n')
  }

  @CompileDynamic
  public void printVertexLabelJson(Iterable<VertexLabel> args) {
    String pattern = '{ "name": "%s", "partition": %s , "useStatic": %s }'
    this.sb.append(', "vertexLabels": [')
    boolean isFirst = true;
    args.each {
      if (isFirst) {
        isFirst = false;
      } else {
        this.sb.append('\n  ,');
      }
      this.sb.append(String.format(pattern, it.name(), it.isPartitioned(), it.isStatic()));
    }
    this.sb.append(']')
    this.sb.append('\n')
  }

  @CompileDynamic
  public Collection<JanusGraphIndex> getIndex(Object args) {
    Collection<JanusGraphIndex> result = []
    result.addAll(getManagement().getGraphIndexes(Vertex.class))
    result.addAll(getManagement().getGraphIndexes(Edge.class))
    result.addAll(getManagement().getGraphIndexes(PropertyKey.class))
    result.addAll(getRelation(RelationType.class, []).collect {
      getManagement().getRelationIndexes(it).flatten()
    }.flatten())
    if (args) {
      return result.findAll {
        args.contains(it.name())
      }
    }
    result = result.toSorted { a, b -> a.name() <=> b.name() }
    return result
  }

  public void printVertexIndicesJson(Iterable<JanusGraphIndex> args) {
    String pattern = '{ "name": "%s", "composite": %s, "unique": %s, "indexOnly": null, "mixedIndex": %s, "propertyKeys": %s %s }';
    this.sb.append(', "vertexIndexes": [') // sic plural should have been indices
    boolean isFirst = true;
    args.each {
      if (isFirst) {
        isFirst = false;
      } else {
        this.sb.append('\n  ,');
      }
      Set<String> props = new HashSet<>();
      it.fieldKeys.each { PropertyKey fk ->
        props.add((String) fk.name());
      }

      String propertyKeysJsonStr = groovy.json.JsonOutput.toJson(props);

      String propertyKeyMappingsJsonStr = getPropertyKeysMappingJsonStr(it);

      this.sb.append(String.format(pattern, it.name(), it.compositeIndex.toString()
        , it.unique.toString(), it.mixedIndex ? '"search"' : 'null', propertyKeysJsonStr, propertyKeyMappingsJsonStr));

    }
    this.sb.append(']')
    this.sb.append('\n')
  }

  static String getPropertyKeysMappingJsonStr(JanusGraphIndex it) {
    StringBuffer propKeysMappingsSb = new StringBuffer();
    boolean isFirstPropKeysMapping = false;
    StringBuffer sb1 = new StringBuffer();
    boolean isFirstPropMapping = false;

    it.fieldKeys.each { PropertyKey fk ->
      if (!isFirstPropKeysMapping) {
        isFirstPropKeysMapping = true;
        propKeysMappingsSb.append(',"propertyKeysMappings": {')
      }

      Parameter[] params = it.getParametersFor(fk);


      String mappingJsonStr = null;
      String analyzerJsonStr = null;

      params.each { Parameter param ->

        String paramKey = param.key();
        if ("mapping".equalsIgnoreCase(paramKey)) {
          sb1.setLength(0);
          sb1.append('"mapping": "').append(param.value()).append('"');
          mappingJsonStr = sb1.toString();
        } else if (!"mapped-name".equalsIgnoreCase(paramKey) &&
          !"status".equalsIgnoreCase(paramKey)) {
          sb1.setLength(0);
          sb1.append(', "analyzer": { "name": "').append(param.key())
            .append('", "value": "').append(param.value().toString()).append('" }');
          analyzerJsonStr = sb1.toString();

        }
      }


      if (mappingJsonStr || analyzerJsonStr) {

        if (!isFirstPropMapping) {
          isFirstPropMapping = true
        } else {
          propKeysMappingsSb.append(',')
        }
        propKeysMappingsSb.append('"').append(fk.name()).append('": { ').append(mappingJsonStr)

        if (analyzerJsonStr) {
          propKeysMappingsSb.append(analyzerJsonStr);
        }
        propKeysMappingsSb.append('}')
      }

    }


    if (isFirstPropKeysMapping) {
      propKeysMappingsSb.append('}')
    }

    String emptyVal = ',"propertyKeysMappings": {}'
    String retVal = propKeysMappingsSb.toString();
    return emptyVal.equalsIgnoreCase(retVal) ? '' : retVal;

  }

  @CompileDynamic
  public void printIndex(Object args) {
    def pattern = "%-50s | %9s | %16s | %6s | %15s | %40s | %20s\n"
    this.sb.append(String.format(pattern, 'Graph Index', 'Type', 'Element', 'Unique', 'Backing/Mapping', 'PropertyKey [dataType]', 'Status'))
    this.sb.append(String.format(pattern, '-----------', '----', '-------', '------', '-------', '-----------', '------'))
    args.findAll { it instanceof JanusGraphIndex }.each {
      def idxType = "Unknown"
      if (it.isCompositeIndex()) {
        idxType = "Composite"
      } else if (it.isMixedIndex()) {
        idxType = "Mixed"
      }
      def element = it.getIndexedElement().simpleName.take(16)
      this.sb.append(String.format(pattern, it.name().take(50), idxType, element, it.isUnique(), it.getBackingIndex().take(13), "", ""))
      it.getFieldKeys().each { fk ->
        def mapping = it.getParametersFor(fk).findAll { it.key == 'mapping' }[0]
        this.sb.append(String.format(pattern, "", "", "", "", mapping ? mapping : '', fk.name().take(40) + " [" + fk.dataType().getSimpleName() + "]", it.getIndexStatus(fk).name().take(20)))
      }
    }
    this.sb.append(String.format(pattern, '----------', '----', '-------', '------', '-------', '-----------', '------'))
    this.sb.append('\n')

    pattern = "%-50s | %20s | %10s | %10s | %10s | %20s\n"
    this.sb.append(String.format(pattern, 'Relation Index', 'Type', 'Direction', 'Sort Key', 'Sort Order', 'Status'))
    this.sb.append(String.format(pattern, '--------------', '----', '---------', '----------', '--------', '------'))
    args.findAll { it instanceof RelationTypeIndex }.each {
      def keys = it.getSortKey()
      this.sb.append(String.format(pattern, it.name().take(50), it.getType(), it.getDirection(), keys[0], it.getSortOrder(), it.getIndexStatus().name().take(20)))
      keys.tail().each { k ->
        this.sb.append(String.format(pattern, "", "", "", k, "", ""))
      }
    }
    this.sb.append(String.format(pattern, '--------------', '----', '---------', '----------', '--------', '------'))
    this.sb.append('\n')
  }

  @CompileDynamic
  public Collection<PropertyKey> getPropertyKey(Object args) {
    Collection<PropertyKey> result = []
    result.addAll(getManagement().getRelationTypes(PropertyKey.class))

    if (args) {
      return result.findAll {
        args.contains(it.name())
      }
    }
    result = result.toSorted { a, b -> a.name() <=> b.name() }
    return result

  }

  @CompileDynamic
  public Collection<EdgeLabel> getEdge(Object args) {

    Collection<EdgeLabel> result = []
    result.addAll(getManagement().getRelationTypes(EdgeLabel.class))

    if (args) {
      return result.findAll {
        args.contains(it.name())
      }
    }
    result = result.toSorted { a, b -> a.name() <=> b.name() }
    return result

  }

  @CompileDynamic
  Object getRelation(Object type, Object args) {
    def result = []
    result.addAll(getManagement().getRelationTypes(type))

    if (args) {
      return result.findAll {
        args.contains(it.name())
      }
    }
    result = result.toSorted { a, b -> a.name() <=> b.name() }
    return result
  }

  @CompileDynamic
  void printEdge(Collection<EdgeLabel> args) {
    def pattern = "%-50s | %15s | %15s | %15s | %15s\n"
    this.sb.append(String.format(pattern, 'Edge Name', 'Type', 'Directed', 'Unidirected', 'Multiplicity'))
    this.sb.append(String.format(pattern, '---------', '----', '--------', '-----------', '------------'))
    args.each {
      def relType = "Unknown"
      if (it.isEdgeLabel()) {
        relType = 'Edge'
      } else if (it.isPropertyKey()) {
        relType = 'PropertyKey'
      }

      this.sb.append(String.format(pattern, it.name().take(50), relType, it.isDirected(), it.isUnidirected(), it.multiplicity()))
    }
    this.sb.append(String.format(pattern, '---------', '----', '--------', '-----------', '------------'))
    this.sb.append('\n')
  }

  @CompileDynamic
  public void printEdgeLabelJson(Collection<EdgeLabel> args) {
    String pattern = '{ "name": "%s", "multiplicity": "%s" , "unidirected":  %s }'
    this.sb.append(', "edgeLabels": [')
    boolean isFirst = true;
    args.each {
      if (isFirst) {
        isFirst = false;
      } else {
        this.sb.append('\n  ,');
      }
      this.sb.append(String.format(pattern, it.name(), it.multiplicity().toString(), it.isUnidirected().toString()));
    }
    this.sb.append(']')
    this.sb.append('\n')
  }

  @CompileDynamic
  void printPropertyKey(Object args) {
    def pattern = "%-50s | %15s | %15s | %20s\n"
    this.sb.append(String.format(pattern, 'PropertyKey Name', 'Type', 'Cardinality', 'Data Type'))
    this.sb.append(String.format(pattern, '----------------', '----', '-----------', '---------'))
    args.each {
      def relType = "Unknown"
      if (it.isEdgeLabel()) {
        relType = 'Edge'
      } else if (it.isPropertyKey()) {
        relType = 'PropertyKey'
      }

      this.sb.append(String.format(pattern, it.name().take(50), relType, it.cardinality(), it.dataType()))
    }
    this.sb.append(String.format(pattern, '----------------', '----', '-----------', '---------'))
    this.sb.append('\n')
  }

  public void printPropertyKeyJson(Collection<PropertyKey> args) {
    String pattern = '{ "name": "%s", "dataType": "%s" , "cardinality": "%s" }';
    this.sb.append('"propertyKeys": [')
    boolean isFirst = true;
    args.each {
      if (isFirst) {
        isFirst = false;
      } else {
        this.sb.append('\n  ,');
      }
      this.sb.append(String.format(pattern, it.name(), it.dataType().getSimpleName(), it.cardinality().toString()));
    }
    this.sb.append(']')
    this.sb.append('\n')
  }

  private void ensureMgmtOpen() {
    def mgmt = getManagement()
    if (!mgmt.isOpen()) {
      openManagement()
    }
  }

  private void ensureMgmtClosed() {
    def mgmt = getManagement()
    if (mgmt.isOpen()) {
      mgmt.rollback()
    }
  }

  JanusGraphManagement getManagement() {
    return openManagement()
  }

  JanusGraphManagement openManagement() {
    graph.tx().rollback()
    return graph.openManagement()
  }

  JanusGraph getGraph() {
    return this.graph
  }

}

def renderReportInText(long pg_id, String reportType = 'SAR Read', GraphTraversalSource g = g) {
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

    com.hubspot.jinjava.Jinjava jinJava = new com.hubspot.jinjava.Jinjava();
    return jinJava.render(new String(template.decodeBase64()), allData).toString();
  }
  return "Failed to render data"
}

def renderReportInBase64(long pg_id, String pg_templateTextInBase64, GraphTraversalSource g = g) {

  def context = g.V(pg_id).valueMap()[0].collectEntries { key, val ->
    [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
  };

  def neighbours = g.V(pg_id).both().valueMap().toList().collect { item ->
    item.collectEntries { key, val ->
      [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
    }
  };


  def impactedServers = g.V(pg_id)
    .both()
    .has("Metadata.Type.Object.AWS_Instance", P.eq('Object.AWS_Instance'))
    .valueMap().toList().collect { item ->
    item.collectEntries { key, val ->
      [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
    }
  };


  GraphTraversal impactedDataSourcesTrav = g.V(pg_id)
    .both().has("Metadata.Type.Object.AWS_Instance", P.eq('Object.AWS_Instance'))
    .bothE('Runs_On').outV().dedup()

  GraphTraversal dsTravClone = impactedDataSourcesTrav.clone()

  def impactedDataSources = impactedDataSourcesTrav.valueMap().toList().collect { item ->
    item.collectEntries { key, val ->
      [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
    }
  };

  def impactedPeople = dsTravClone
    .out("Has_Ingestion_Event")
    .out("Has_Ingestion_Event")
    .in("Has_Ingestion_Event")
    .has("Metadata.Type.Person.Natural", P.eq('Person.Natural'))
    .valueMap()
    .toList()
    .collect { item ->
    item.collectEntries { key, val ->
      [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
    }
  };

  def allData = new HashMap<>();

  allData.put('context', context);
  allData.put('connected_data', neighbours);
  allData.put('impacted_data_sources', impactedDataSources);
  allData.put('impacted_servers', impactedServers);
  allData.put('impacted_people', impactedPeople);



  com.hubspot.jinjava.Jinjava jinJava = new com.hubspot.jinjava.Jinjava();
  return jinJava.render(new String(pg_templateTextInBase64.decodeBase64()), allData).bytes.encodeBase64().toString();

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

def getVisJsGraph(long pg_vid, int depth = 1) {
  StringBuffer sb = new StringBuffer()
  StringBuffer sb2 = new StringBuffer()

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



