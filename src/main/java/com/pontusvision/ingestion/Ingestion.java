package com.pontusvision.ingestion;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Path("ingestion")
public class Ingestion {

  //  @Inject
  //  KeycloakSecurityContext keycloakSecurityContext;


  public Ingestion() {

  }

  @POST
  @Path("json_obj_array")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public String jsonObjArray(IngestionJsonObjArrayRequest request) throws ExecutionException, InterruptedException {


//    Map<String, Object> bindings = new HashMap() {{
//      put("jsonString", request.jsonString);
//      put("jsonPath", (request.jsonPath == null) ? "$.objs" : request.jsonPath);
//      put("ruleName", request.ruleName);
//    }};

    String res = com.pontusvision.graphutils.Matcher.ingestRecordListUsingRules(
        request.jsonString,
        request.jsonPath,
        request.ruleName);

//    String res = App.executor.eval("com.pontusvision.graphutils.Matcher.ingestRecordListUsingRules(jsonString,jsonPath,ruleName)",
//        bindings).get().toString();
    return res;

  }


}