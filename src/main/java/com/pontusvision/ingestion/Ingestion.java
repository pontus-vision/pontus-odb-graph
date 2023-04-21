package com.pontusvision.ingestion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.orientechnologies.apache.commons.csv.CSVFormat;
import com.orientechnologies.apache.commons.csv.CSVRecord;
import com.pontusvision.gdpr.App;
import com.pontusvision.gdpr.BaseReply;
import com.pontusvision.gdpr.VertexLabelsReply;
import com.pontusvision.graphutils.EmailNLPRequest;
import com.pontusvision.graphutils.FileNLPRequest;
import org.apache.commons.math3.util.Pair;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Path("ingestion")
public class Ingestion {

  //  @Inject
  //  KeycloakSecurityContext keycloakSecurityContext;


  public static String getDataSourceName(IngestionJsonObjArrayRequest request) {
    String dsName = (request.getDataSourceName() == null) ? request.getRuleName() : request.getDataSourceName();
    return (dsName == null) ? "UNKNOWN" : dsName.toUpperCase();
  }

  static Configuration conf = new Configuration.ConfigurationBuilder().build()
      .jsonProvider(new GsonJsonProvider())
      .mappingProvider(new GsonMappingProvider());

  public static Pair<Long, Long> getNumBytesNumObjects(IngestionJsonObjArrayRequest request) {

    Long totalBytes = 0L;
    Long numItems = 0L;
//    Configuration.setDefaults(conf);

    if ("pv_email".equalsIgnoreCase(request.ruleName)) {
      EmailNLPRequest[] recordList = JsonPath.parse(request.jsonString, conf)
          .read(request.jsonPath, EmailNLPRequest[].class);
//      List<EmailNLPRequest> recordList = JsonPath.read(request.jsonString, request.jsonPath);
      for (int i = 0, ilen = recordList.length; i < ilen; i++) {
        EmailNLPRequest rec = recordList[i];
        if (rec.getSizeBytes() != null) {
          totalBytes += rec.getSizeBytes();
        }
      }
      numItems = (long) recordList.length;

    } else if ("pv_file".equalsIgnoreCase(request.ruleName)) {
      FileNLPRequest[] recordList = JsonPath.parse(request.jsonString, conf)
          .read(request.jsonPath, FileNLPRequest[].class);
      for (int i = 0, ilen = recordList.length; i < ilen; i++) {
        FileNLPRequest rec = recordList[i];
        if (rec.getSizeBytes() != null) {
          totalBytes += rec.getSizeBytes();
        }
      }
      numItems = (long) recordList.length;

    }

    return new Pair<>(totalBytes, numItems);

  }

  public Ingestion() {

  }


  @GET
  @Path("liveliness")
  @Produces(MediaType.APPLICATION_JSON)
  public BaseReply liveliness() throws ExecutionException, InterruptedException {
    VertexLabelsReply reply = new VertexLabelsReply(
        App.graph.getRawDatabase().getMetadata().getSchema().getClasses());

    if (reply.getLabels().length > 5) {
      BaseReply retVal = new BaseReply(Response.Status.OK,"{ \"status\": \"OK\" }" );
      return retVal;
    }
    return  new BaseReply(Response.Status.PRECONDITION_FAILED,"{ \"status\": \"NOT OK\" }" );
  }

  @POST
  @Path("pv_o365_email_json_obj_array")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public String pvO365EmailJsonObjArray(IngestionJsonObjArrayRequest request) throws ExecutionException, InterruptedException {


//    Map<String, Object> bindings = new HashMap() {{
//      put("jsonString", request.jsonString);
//      put("jsonPath", (request.jsonPath == null) ? "$.objs" : request.jsonPath);
//      put("ruleName", request.ruleName);
//    }};

    FileNLPRequest.upsertDataSourceStatus(getDataSourceName(request), "In Progress", true);

    String res = com.pontusvision.graphutils.Matcher.ingestEmail(
        request.jsonString,
        request.jsonPath,
        request.ruleName);

    Pair<Long, Long> stats = getNumBytesNumObjects(request);

    FileNLPRequest.upsertDataSourceStatus(getDataSourceName(request), "Finished", false, stats.getFirst(),
        stats.getSecond());

//    String res = App.executor.eval("com.pontusvision.graphutils.Matcher.ingestRecordListUsingRules(jsonString,jsonPath,ruleName)",
//        bindings).get().toString();
    return res;

  }

  @POST
  @Path("pv_file_json_obj_array")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public String pvFileJsonObjArray(IngestionJsonObjArrayRequest request) throws ExecutionException, InterruptedException {


//    Map<String, Object> bindings = new HashMap() {{
//      put("jsonString", request.jsonString);
//      put("jsonPath", (request.jsonPath == null) ? "$.objs" : request.jsonPath);
//      put("ruleName", request.ruleName);
//    }};
    FileNLPRequest.upsertDataSourceStatus(getDataSourceName(request), "In Progress", true, null);


    String res = com.pontusvision.graphutils.Matcher.ingestFile(
        request.jsonString,
        request.jsonPath,
        request.ruleName);

    Pair<Long, Long> stats = getNumBytesNumObjects(request);

    FileNLPRequest.upsertDataSourceStatus(getDataSourceName(request), "Finished", false, stats.getFirst(),
        stats.getSecond());

//    String res = App.executor.eval("com.pontusvision.graphutils.Matcher.ingestRecordListUsingRules(jsonString,jsonPath,ruleName)",
//        bindings).get().toString();
    return res;

  }

  public static String ingestMd2Data(IngestionJsonObjArrayRequest request) {
    return com.pontusvision.graphutils.Matcher.ingestMD2BulkData(request.jsonString, request.jsonPath, request.ruleName);

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
    FileNLPRequest.upsertDataSourceStatus(getDataSourceName(request), "In Progress", true, null);
    String retVal;

    if ("pv_email".equalsIgnoreCase(request.ruleName)) {
      retVal = com.pontusvision.graphutils.Matcher.ingestEmail(
          request.jsonString,
          request.jsonPath,
          request.ruleName);
    } else if ("pv_file".equalsIgnoreCase(request.ruleName)) {
      retVal = com.pontusvision.graphutils.Matcher.ingestFile(
          request.jsonString,
          request.jsonPath,
          request.ruleName);
    } else if ("pv_md2".equalsIgnoreCase(request.ruleName)) {
      retVal = ingestMd2Data(request);
    } else {

      retVal = com.pontusvision.graphutils.Matcher.ingestRecordListUsingRules(
          request.jsonString,
          request.jsonPath,
          request.ruleName);
    }
    Pair<Long, Long> stats = getNumBytesNumObjects(request);

    FileNLPRequest.upsertDataSourceStatus(getDataSourceName(request), "Finished", false, stats.getFirst(),
        stats.getSecond());

//    String res = App.executor.eval("com.pontusvision.graphutils.Matcher.ingestRecordListUsingRules(jsonString,jsonPath,ruleName)",
//        bindings).get().toString();
    return retVal;

  }

  public static String cleanHeader(String headerSub) {
    String retVal = StringReplacer.replaceAll(headerSub, (" ,.;:-)([]{}$\"'!£%^&*+/\\?><@~#`|º"), "_");
    retVal = java.text.Normalizer.normalize(retVal, java.text.Normalizer.Form.NFD);
    retVal = retVal.replaceAll("\\p{M}", "");
    retVal = retVal.replaceAll("[^\\p{ASCII}]", "");
    return retVal;
  }


  @POST
  @Path("status_update")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public String statusUpdate(IngestionStatusRequest request) {
//    upsertDataSourceStatus
    FileNLPRequest.upsertDataSourceStatus(request.dataSourceName, request.status, request.isStart,
        null, null, request.errorStr);
    return "{\"status\": true}";
  }

  @POST
  @Path("csvBase64")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public synchronized String csvFile(IngestionCSVFileRequest request) throws ExecutionException, InterruptedException, IOException {

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
      Map<String, String> recMap = record.toMap();
      recMap.put("currDate", new Date().toString());
      JsonObject entry = new JsonObject();

      recMap.forEach((key, value) -> {
        entry.addProperty(cleanHeader(key), value);
      });
      entries.add(entry);

    }
    in.close();

    obj.add("entries", entries);

    IngestionJsonObjArrayRequest req = new IngestionJsonObjArrayRequest();
    req.ruleName = request.ruleName;
    req.jsonPath = "$.entries";
    req.jsonString = obj.toString();

    String res = this.jsonObjArray(req);
//    String res = App.executor.eval("com.pontusvision.graphutils.Matcher.ingestRecordListUsingRules(jsonString,jsonPath,ruleName)",
//        bindings).get().toString();

    return res;

  }


}
