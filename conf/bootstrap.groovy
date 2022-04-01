import com.orientechnologies.orient.core.id.ORID
import com.pontusvision.gdpr.App
import com.pontusvision.graphutils.Matcher
import com.pontusvision.graphutils.ODBSchemaManager
import com.pontusvision.graphutils.PontusJ2ReportingFunctions
import com.pontusvision.graphutils.VisJSGraph
import com.pontusvision.graphutils.gdpr
import org.apache.tinkerpop.gremlin.orientdb.OrientStandardGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import java.util.concurrent.atomic.AtomicInteger

LinkedHashMap globals = [:]
globals << [graph: (graph) as OrientStandardGraph]
globals << [g: (graph).traversal() as GraphTraversalSource]


static def ingestDataUsingRules(OrientStandardGraph graph, GraphTraversalSource g, Map<String, String> bindings,
                                String jsonRules, StringBuffer sb = null) {
    return Matcher.ingestDataUsingRules(graph,g,bindings,jsonRules,sb);
}


static def ingestRecordListUsingRules(OrientStandardGraph graph, GraphTraversalSource g,
                                      List<Map<String, String>> recordList,
                                      String jsonRules, StringBuffer sb = null) {
  return Matcher.ingestRecordListUsingRules(graph,g,recordList,jsonRules,sb);
}

static def findMatchingNeighbours(GraphTraversalSource gTrav, Set<ORID> requiredTypeIds,
                                  Set<ORID> otherIds, StringBuffer sb = null) {
   return Matcher.findMatchingNeighbours(gTrav,requiredTypeIds,otherIds,sb);
}

static def getVisJsGraph(String pg_vid){
    return VisJSGraph.getVisJsGraph(pg_vid);
}


static def getVisJsGraph(String pg_vid, int depth){
    return VisJSGraph.getVisJsGraph(pg_vid,depth);
}

static def getVisJsGraphImmediateNeighbourNodes(String pg_vid, StringBuffer sb,
                                                int counter, Set<ORID> nodeIds, AtomicInteger depth){
    return VisJSGraph.getVisJsGraphImmediateNeighbourEdges(pg_vid,sb,counter,nodeIds,depth);
}

static def getEdgeProperties(String fromVertexId, String toVertexId){
    return VisJSGraph.getEdgeProperties(fromVertexId,toVertexId);
}

static def getVisJsGraph(String pg_vid, long pg_depth){
    return VisJSGraph.getVisJsGraph(pg_vid,pg_depth);
}

static def getPropsNonMetadataAsHTMLTableRows(GraphTraversalSource g, String vid, String origLabel){
    return VisJSGraph.getPropsNonMetadataAsHTMLTableRows(g,vid,origLabel);
}

static def getNumEventsPerDataSource(){
    return gdpr.getNumEventsPerDataSource();
}

static def getNumNaturalPersonPerDataSource(){
    return gdpr.getNumNaturalPersonPerDataSource();
}
static def getNumSensitiveDataPerDataSource(){
    return gdpr.getNumSensitiveDataPerDataSource();
}
static def getNumNaturalPersonPerOrganisation(){
    return gdpr.getNumNaturalPersonPerOrganisation();
}
static def getConsentPerNaturalPersonType(){
    return gdpr.getConsentPerNaturalPersonType();
}
static def getDataProceduresPerDataSource(){
    return gdpr.getDataProceduresPerDataSource();
}
static def getNaturalPersonPerDataProcedures(){
    return gdpr.getNaturalPersonPerDataProcedures();
}
static def getDSARStatsPerOrganisation(){
    return gdpr.getDSARStatsPerOrganisation();
}

static def getScoresJson(){
    return gdpr.getScoresJson();
}

static def calculatePOLECounts(){
    return gdpr.calculatePOLECounts();
}

static def renderReportInBase64(String pg_id, String pg_templateTextInBase64, GraphTraversalSource g = App.g){
    return PontusJ2ReportingFunctions.renderReportInBase64(pg_id,pg_templateTextInBase64,g);
}

static def renderReportInTextPt(String pg_id, String reportType = 'DSAR', GraphTraversalSource g = App.g){
    return PontusJ2ReportingFunctions.renderReportInTextPt(pg_id,reportType,g);
}

static def renderReportInText(ORID pg_id, String reportType = 'SAR Read', GraphTraversalSource g = App.g){
    return PontusJ2ReportingFunctions.renderReportInText(pg_id,reportType,g);

}


try {
    //String gdprModeEnv = System.getenv("PONTUS_GDPR_MODE");
    //if (gdprModeEnv != null && Boolean.parseBoolean(gdprModeEnv)) {
    //createIndicesPropsAndLabels();
    //}
    //loadSchema(graph,'/tmp/graphSchema_full.json', '/tmp/graphSchema_ext.json')
//    OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
//    orient.create("test", ODatabaseType.PLOCAL);


    System.out.println('\n\n\n\nABOUT TO LOAD conf/gdpr-schema.json\n\n\n\n\n')
    String retVal = ODBSchemaManager.loadSchema(graph, '/orientdb/conf/gdpr-schema.json', 'conf/gdpr-schema.json')

    System.out.println("results after loading conf/gdpr-schema.json: ${retVal}\n\n\n\n\n")
    if (!App.g) {
        App.g = graph.traversal()
    }
    if (!App.graph) {
        App.graph = graph
    }

    retVal = Matcher.loadDataMappingFiles(graph, '/orientdb/j2/rules', 'j2/rules/')
    System.out.println("results after loadDataMappingFiles: ${retVal}\n\n\n\n\n")

    gdpr.createNotificationTemplates()

    gdpr.createNotificationTemplatesFromFiles(graph,'/orientdb/conf/reports', 'conf/reports' )
    gdpr.addLawfulBasisAndPrivacyNotices(graph, App.g)

} catch (e) {
    e.printStackTrace()
}

