import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig

//action

try {
    //String gdprModeEnv = System.getenv("PONTUS_GDPR_MODE");
    //if (gdprModeEnv != null && Boolean.parseBoolean(gdprModeEnv)) {
       //createIndicesPropsAndLabels();
    //}
    //loadSchema(graph,'/tmp/graphSchema_full.json', '/tmp/graphSchema_ext.json')
    OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
    orient.create("test", ODatabaseType.PLOCAL);

    System.out.println('\n\n\n\nABOUT TO LOAD /opt/pontus-graphdb/graphdb-current//conf/gdpr-schema.json\n\n\n\n\n')
    String retVal = loadSchema(orient,'/opt/pontus-graphdb/graphdb-current//conf/gdpr-schema.json')
    
    System.out.println("results after loading /opt/pontus-graphdb/graphdb-current//conf/gdpr-schema.json: ${retVal}\n\n\n\n\n")

} catch (e) {
    e.printStackTrace()
}

