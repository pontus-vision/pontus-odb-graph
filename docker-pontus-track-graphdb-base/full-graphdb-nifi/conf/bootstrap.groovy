//action

try {
    //String gdprModeEnv = System.getenv("PONTUS_GDPR_MODE");
    //if (gdprModeEnv != null && Boolean.parseBoolean(gdprModeEnv)) {
       //createIndicesPropsAndLabels();
    //}
    //loadSchema(graph,'/tmp/graphSchema_full.json', '/tmp/graphSchema_ext.json')
    System.out.println('\n\n\n\nABOUT TO LOAD /opt/pontus-graphdb/graphdb-current//conf/gdpr-schema.json\n\n\n\n\n')
    String retVal = loadSchema(graph,'/opt/pontus-graphdb/graphdb-current//conf/gdpr-schema.json')
    
    System.out.println("results after loading /opt/pontus-graphdb/graphdb-current//conf/gdpr-schema.json: ${retVal}\n\n\n\n\n")

} catch (e) {
    e.printStackTrace()
}

