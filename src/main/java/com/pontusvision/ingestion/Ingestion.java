package com.pontusvision.ingestion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orientechnologies.apache.commons.csv.CSVFormat;
import com.orientechnologies.apache.commons.csv.CSVRecord;
import com.pontusvision.gdpr.App;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


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

  public static String cleanHeader (String headerSub){
    String retVal  = StringReplacer.replaceAll(headerSub, (" .;:-"), "_");
    retVal = java.text.Normalizer.normalize(retVal,  java.text.Normalizer.Form.NFD);
    retVal = retVal.replaceAll("\\p{M}", "");
    return retVal;
  }

  @POST
  @Path("csvBase64")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public String csvFile(IngestionCSVFileRequest  request) throws ExecutionException, InterruptedException, IOException {


//    Map<String, Object> bindings = new HashMap() {{
//      put("jsonString", request.jsonString);
//      put("jsonPath", (request.jsonPath == null) ? "$.objs" : request.jsonPath);
//      put("ruleName", request.ruleName);
//    }};
    byte[] decodedBytes = Base64.getDecoder().decode(request.csvBase64);
    Reader in = new InputStreamReader(new ByteArrayInputStream(decodedBytes));
    Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

    JsonObject obj = new JsonObject();
    JsonArray entries = new JsonArray();
//    List<Map> listOfMaps = new ArrayList<>();
    for (CSVRecord record : records) {
      Map<String,String> recMap = record.toMap();
      recMap.put("currDate", new Date().toString());
      JsonObject entry = new JsonObject();

      recMap.forEach((key,value)-> {
        entry.addProperty(cleanHeader(key),value);
      });
      entries.add(entry);

    }

    obj.add("entries", entries);


    String res = com.pontusvision.graphutils.Matcher.ingestRecordListUsingRules(
        obj.getAsString(),
        "$.entries",
        request.ruleName);

//    String res = App.executor.eval("com.pontusvision.graphutils.Matcher.ingestRecordListUsingRules(jsonString,jsonPath,ruleName)",
//        bindings).get().toString();
    return res;

  }


}