import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import com.pontusvision.gdpr.App

//action

try {
    //String gdprModeEnv = System.getenv("PONTUS_GDPR_MODE");
    //if (gdprModeEnv != null && Boolean.parseBoolean(gdprModeEnv)) {
       //createIndicesPropsAndLabels();
    //}
    //loadSchema(graph,'/tmp/graphSchema_full.json', '/tmp/graphSchema_ext.json')
//    OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
//    orient.create("test", ODatabaseType.PLOCAL);

    System.out.println('\n\n\n\nABOUT TO LOAD conf/gdpr-schema.json\n\n\n\n\n')
    String retVal = loadSchema(graph,'/orientdb/conf/gdpr-schema.json', 'conf/gdpr-schema.json')
    
    System.out.println("results after loading conf/gdpr-schema.json: ${retVal}\n\n\n\n\n")
    if (!App.g) {
        App.g = g;
    }

    retVal = loadDataMappingFiles(graph, '/orientdb/j2/rules', 'j2/rules/');
    System.out.println("results after loadDataMappingFiles: ${retVal}\n\n\n\n\n")


    createNotificationTemplates();
    addLawfulBasisAndPrivacyNotices(graph,g)

} catch (e) {
    e.printStackTrace()
}

