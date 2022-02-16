package com.pontusvision.gdpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.pontusvision.gdpr.mapping.MappingReq;
import com.pontusvision.gdpr.report.ReportTemplateRenderRequest;
import com.pontusvision.gdpr.report.ReportTemplateRenderResponse;
import com.pontusvision.gdpr.report.ReportTemplateUpsertRequest;
import com.pontusvision.gdpr.report.ReportTemplateUpsertResponse;
import com.pontusvision.graphutils.PText;
import com.pontusvision.graphutils.PontusJ2ReportingFunctions;
import com.pontusvision.graphutils.gdpr;
import com.pontusvision.security.JWTTokenNeeded;
import com.pontusvision.security.PVDecodedJWT;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.driver.message.ResponseMessage;
import org.apache.tinkerpop.gremlin.driver.message.ResponseStatusCode;
import org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV3d0;
import org.apache.tinkerpop.gremlin.driver.ser.SerializationException;
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet;
import org.apache.tinkerpop.gremlin.orientdb.io.OrientIoRegistry;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoRegistry;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ContainerRequest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static org.apache.tinkerpop.gremlin.process.traversal.P.eq;
import static org.apache.tinkerpop.gremlin.process.traversal.P.neq;

//import org.janusgraph.core.EdgeLabel;
//import org.janusgraph.core.PropertyKey;
//import org.janusgraph.core.schema.JanusGraphIndex;
//import org.janusgraph.core.schema.JanusGraphManagement;
//import org.janusgraph.core.schema.SchemaStatus;
//import static org.janusgraph.core.attribute.Text.textContainsFuzzy;

//import org.json.JSONArray;
//import org.json.JSONObject;

@RequestScoped
@JWTTokenNeeded
@Path("home")
public class Resource {

  //  @Inject
  //  KeycloakSecurityContext keycloakSecurityContext;


  Gson gson = new Gson();
  GsonBuilder gsonBuilder = new GsonBuilder();
  Map<String, Pattern> compiledPatterns = new HashMap<>();

  public Resource() {

  }

  @GET
  @Path("hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String helloWorld() {
    return "Hello, world!";
  }

  //addRandomDataInit(App.graph,App.g)


  public static boolean isAdminOrDPO(ContainerRequestContext request){
    PVDecodedJWT pvDecodedJWT = (PVDecodedJWT) request.getProperty("pvDecodedJWT");
    return (pvDecodedJWT != null && pvDecodedJWT.isAdmin() || pvDecodedJWT.isDPO());
  }

  @POST
  @Path("clean_data")
  @Produces(MediaType.TEXT_PLAIN)
  public BaseReply cleanData(@Context ContainerRequestContext req) {
    if (!isAdminOrDPO(req)) {
      return new BaseReply(Response.Status.UNAUTHORIZED, "Auth Token does not give rights to execute this");
    }
    return new BaseReply(Response.Status.OK, gdpr.cleanData(App.graph, App.g));
  }

  @POST
  @Path("random_init")
  @Produces(MediaType.TEXT_PLAIN)
  public BaseReply randomInit(@Context ContainerRequestContext req) {
    if (!isAdminOrDPO(req)) {
      return new BaseReply(Response.Status.UNAUTHORIZED, "Auth Token does not give rights to execute this");
    }
    return new BaseReply(Response.Status.OK,
        gdpr.addRandomDataInit(App.graph, App.g));
  }

  /*
   */


  public static String getFirstStringItem (List<Object> list, String defaultVal){

    if (list == null || list.size() != 1){
      return defaultVal;
    }
    return list.get(0).toString();

  }
  public static Double getFirstDoubleItem (List<Object> list, Double defaultVal){

    if (list == null || list.size() != 1){
      return defaultVal;
    }
    return (Double)list.get(0);

  }
  @POST
  @Path("md2_search")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Md2Reply md2Search(@Context ContainerRequestContext req,Md2Request md2req) throws HTTPException {
    if (!isAdminOrDPO(req)) {
      return new Md2Reply(Response.Status.UNAUTHORIZED,
          "Auth Token does not give rights to execute this");

    }
    if (md2req.settings == null || md2req.query == null || md2req.query.name == null) {
      return new Md2Reply(Response.Status.BAD_REQUEST,
          "Invalid Request; missing the settings, query, or query.name fields");
    }

    GraphTraversal<Vertex, Vertex> gt =
        App.g.V().has("Person_Natural_Full_Name",
            PText.textContains(md2req.query.name.trim().toUpperCase(Locale.ROOT)));

    if (md2req.query.docCpf != null) {
      gt = gt.has("Person_Natural_Customer_ID", eq(md2req.query.docCpf.replaceAll("[^0-9]", "")));
    }
    try {
      List<Object> ids = (gt.id().toList());
      if (ids.size() == 0) {
        System.out.println("Found 0 ids matching the request");
        Md2Reply reply = new Md2Reply(Response.Status.NOT_FOUND);
        reply.errorStr = "Found 0 ids matching the request";
        return reply;
      } else if (ids.size() > 1) {
        Md2Reply reply = new Md2Reply(Response.Status.CONFLICT);

        reply.reqId = md2req.query.reqId;
        reply.errorStr = "Found more than one person associated with " + md2req.query.name + " (docCpf=" +
            md2req.query.docCpf + ")";

        return reply;

      }
      if (md2req.query.email != null) {
        if (!App.g.V(ids.get(0)).out("Uses_Email").has("Object_Email_Address_Email",
            eq(md2req.query.email.toLowerCase(Locale.ROOT).trim())).hasNext()) {
          System.out.println("Not found email " + md2req.query.email + " associated with " + ids.size() + " (" +
              md2req.query.name + ")");

          Md2Reply reply = new Md2Reply(Response.Status.CONFLICT);

          reply.reqId = md2req.query.reqId;
          reply.errorStr = "Not found email " + md2req.query.email + " associated with " + md2req.query.name + " (docCpf=" +
              md2req.query.docCpf + ")";

          return reply;

//          throw new HTTPException(404);
        }
      }
      Md2Reply reply = new Md2Reply(Response.Status.OK);

      reply.total = App.g.V(ids.get(0)).in("Has_NLP_Events").in("Has_NLP_Events").dedup().count().next();
      reply.reqId = md2req.query.reqId;

      List<Map<String, Object>> res = App.g.V(ids.get(0)).in("Has_NLP_Events").order().by("Event_NLP_Group_Ingestion_Date", Order.asc)
          .in("Has_NLP_Events").dedup().range(md2req.settings.start, md2req.settings.start + md2req.settings.limit).as("EVENTS")
//          .in()
          .match(
              __.as("EVENTS").id().as("ID"),
//              __.as("EVENTS").has("Metadata_Type_Object_Email_Message_Body", P.eq("Object_Email_Message_Body")).valueMap().as("email_body"),
//              __.as("EVENTS").has("Metadata_Type_Object_Email_Message_Attachment",P.eq ("Object_Email_Message_Attachment")).valueMap().as("email_attachment"),
              __.as("EVENTS").label().as("eventType"),
              __.as("EVENTS").valueMap().as("values")
          )
//          .select("id","email_body", "email_attachment", "file")
          .select("ID", "eventType", "values")
//          .has("Metadata_Type_Event_File_Ingestion",P.eq ("Event_File_Ingestion")).valueMap().as("file")
          .toList();

      reply.track = new Md2Reply.Register[res.size()];

      for (int i = 0, ilen = res.size(); i < ilen; i++) {
        Md2Reply.Register reg = new Md2Reply.Register();
        String eventType = res.get(i).get("eventType").toString();
        Map<String, List<Object>> values = (Map<String, List<Object>>) res.get(i).get("values");
        Object eventId = res.get(i).get("ID");

        if ("Event_File_Ingestion".equalsIgnoreCase(eventType)) {
          reg.fileType = getFirstStringItem(values.get("Event_File_Ingestion_File_Type"),"");
          reg.sizeBytes = getFirstDoubleItem(values.get("Event_File_Ingestion_Size_Bytes"),0.0).longValue();
          reg.name = getFirstStringItem(values.get("Event_File_Ingestion_Name"),"");
          reg.path = getFirstStringItem(values.get("Event_File_Ingestion_Path"),"");
          reg.created =  getFirstStringItem(values.get("Event_File_Ingestion_Created"),"");
          reg.owner = getFirstStringItem(values.get("Event_File_Ingestion_Owner"),"");
          reg.server = getFirstStringItem(values.get("Event_File_Ingestion_Server"),"");

          reg.lastAccess = getFirstStringItem(values.get("Event_File_Ingestion_Last_Access"),"");
        } else if ("Object_Email_Message_Attachment".equalsIgnoreCase(eventType)) {
          reg.fileType = "Email_Message_Attachment";
          reg.sizeBytes = getFirstDoubleItem(values.get("Object_Email_Message_Attachment_Size_Bytes"),0.0).longValue();
          reg.name = getFirstStringItem(values.get("Object_Email_Message_Attachment_Attachment_Name"),"");
          StringBuilder sb = new StringBuilder();
          sb.append("https://outlook.office365.com/mail/deeplink?ItemID=");
          sb.append(getFirstStringItem(values.get("Object_Email_Message_Attachment_Attachment_Id"),""));
          reg.path = sb.toString();
          List<Object> createdDateTime = values.get("Object_Email_Message_Attachment_Created_Date_Time");

          reg.created = getFirstStringItem(
              values.get("Object_Email_Message_Attachment_Created_Date_Time"),
              "");


          GraphTraversal<Vertex, Object> trav = App.g.V(eventId).in("Email_Attachment")
              .out("Email_From").values("Event_Email_From_Group_Email");
          reg.owner = trav.hasNext() ? trav.next().toString() : "";
          reg.server = "office365/email";
        } else if ("Object_Email_Message_Body".equalsIgnoreCase(eventType)) {
          reg.fileType = "Email_Message_Body";
          reg.sizeBytes = getFirstDoubleItem(values.get("Object_Email_Message_Body.Size_Bytes"),0.0).longValue();
          reg.name = getFirstStringItem(values.get("Object_Email_Message_Body.Email_Subject"),"");
          StringBuffer sb = new StringBuffer();
          sb.append("https://outlook.office365.com/mail/deeplink?ItemID=");
          String emailId = getFirstStringItem(values.get("Object_Email_Message_Body.Email_Id"),"");
          sb.append(URLEncoder.encode(emailId, "UTF-8"));
          reg.path = sb.toString();
          reg.created = getFirstStringItem(values.get("Object_Email_Message_Body.Created_Date_Time"), "" );

          GraphTraversal<Vertex, Object> trav = App.g.V(eventId).in("Email_Body")
              .out("Email_From").values("Event_Email_From_Group_Email");
          //.next().toString();

          String owner = trav.hasNext() ? trav.next().toString() : "";

          reg.lastAccess = values.get("Object_Email_Message_Body.Received_Date_Time") != null ?
              values.get("Object_Email_Message_Body.Received_Date_Time").get(0).toString():
              (values.get("Object_Email_Message_Body.Sent_Date_Time") != null ?
                  values.get("Object_Email_Message_Body.Sent_Date_Time").get(0).toString():
                  "");


          reg.owner = owner;
          reg.server = "office365/email";

        }


        reply.track[i] = reg;

      }


//          .properties("Object_Email_Message_Body", "Object_Email_Message_Attachment");

//      reply.track.created;
//      reply.track.fileType;
//      reply.track.lastAccess;
//      reply.track.name;
//      reply.track.owner;
//      reply.track.path;
//      reply.track.server;
//      reply.track.sizeBytes;


      return reply;
    } catch (Exception e) {
      System.err.println("Error processing data: " + e.getMessage());
      e.printStackTrace();
      return new Md2Reply(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }


  @POST
  @Path("agrecords")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public RecordReply agrecords(@Context ContainerRequestContext req, RecordRequest recordRequest) {
    if (!isAdminOrDPO(req)) {
      return new RecordReply(Response.Status.UNAUTHORIZED,
          "Auth Token does not give rights to execute this");
    }

    if (recordRequest.cols != null && recordRequest.dataType != null) {

      Set<String> valsSet = new HashSet<>();
      Set<String> reportButtonsSet = new HashSet<>();
      for (int i = 0, ilen = recordRequest.cols.length; i < ilen; i++) {
        if (!recordRequest.cols[i].id.startsWith("@")) {
          valsSet.add(recordRequest.cols[i].id);
        } else {
          reportButtonsSet.add(recordRequest.cols[i].id);
        }

      }

      String[] vals = valsSet.toArray(new String[valsSet.size()]);

      try {

        String sqlQueryCount = recordRequest.getSQL(true);

        String sqlQueryData = recordRequest.getSQL(false);

        String dataType = recordRequest.dataType; //req.search.extraSearch[0].value;

        boolean hasFilters = recordRequest.filters != null && recordRequest.filters.length > 0;

        OGremlinResultSet resultSet = App.graph.executeSql(sqlQueryCount, Collections.EMPTY_MAP);
        Long count = resultSet.iterator().next().getRawResult().getProperty("COUNT(*)");
        resultSet.close();

        if (count > 0) {
          List<Map<String, Object>> res = new LinkedList<>();

          OResultSet oResultSet = App.graph.executeSql(sqlQueryData, Collections.EMPTY_MAP).getRawResultSet();

          while (oResultSet.hasNext()) {
            OResult oResult = oResultSet.next();
            Map<String, Object> props = new HashMap<>();

            oResult.getPropertyNames().forEach(propName -> props.put(propName, oResult.getProperty(propName)));
            oResult.getIdentity().ifPresent(id -> props.put("id", id.toString()));

            res.add(props);
          }

          oResultSet.close();

          String[] recs = new String[res.size()];
          ObjectMapper objMapper = new ObjectMapper();

          for (int i = 0, ilen = res.size(); i < ilen; i++) {
            Map<String, Object> map = res.get(i);
            Map<String, String> rec = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
              Object val = entry.getValue();
              if (val instanceof ArrayList) {
                ArrayList<Object> arrayList = (ArrayList) val;

                String val2 = arrayList.get(0).toString();

                rec.put(entry.getKey(), val2);

              } else {
                rec.put(entry.getKey(), val == null ? null : val.toString());
              }

            }

            recs[i] = objMapper.writeValueAsString(rec);
          }
          RecordReply reply = new RecordReply(recordRequest.from, recordRequest.to, count, recs);

          return reply;

        }

        RecordReply reply = new RecordReply(recordRequest.from, recordRequest.to, count, new String[0]);

        return reply;

      } catch (Throwable t) {
        t.printStackTrace();
      }

    }

    return new RecordReply(recordRequest.from, recordRequest.to, 0L, new String[0]);

  }

  @POST
  @Path("graph")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GraphReply graph(@Context ContainerRequestContext req, GraphRequest greq) {
    if (!isAdminOrDPO(req)) {
      return new GraphReply(Response.Status.UNAUTHORIZED,
          "Auth Token does not give rights to execute this");
    }

    Set<Vertex> outNodes = App.g.V((greq.graphId)).to(Direction.OUT).toSet();
    Set<Vertex> inNodes = App.g.V((greq.graphId)).to(Direction.IN).toSet();
    Vertex v = App.g.V((greq.graphId)).next();

    Set<Edge> outEdges = App.g.V((greq.graphId)).toE(Direction.OUT).toSet();
    Set<Edge> inEdges = App.g.V((greq.graphId)).toE(Direction.IN).toSet();

    GraphReply retVal = new GraphReply(v, inNodes, outNodes, inEdges, outEdges);

    return retVal;
  }

  @GET
  @Path("vertex_prop_values")
  @Produces(MediaType.APPLICATION_JSON)
  public FormioSelectResults getVertexPropertyValues(
      @Context ContainerRequestContext req,
      @QueryParam("search") String search
      , @QueryParam("limit") Long limit
      , @QueryParam("skip") Long skip

  ) {
    if (!isAdminOrDPO(req)) {
      return new FormioSelectResults(Response.Status.UNAUTHORIZED,
          "Auth Token does not give rights to execute this");
    }
    //    final  String bizCtx = "BizCtx";
    //
    //    final AtomicBoolean matches = new AtomicBoolean(false);
    //
    //    keycloakSecurityContext.getAuthorizationContext().getPermissions().forEach(perm -> perm.getClaims().forEach(
    //        (s, strings) -> {
    //          if (bizCtx.equals(s)){
    //            strings.forEach( allowedVal -> {
    //              Pattern patt = compiledPatterns.computeIfAbsent(allowedVal, Pattern::compile);
    //              matches.set(patt.matcher(search).matches());
    //
    //            }  );
    //          }
    //        }));
    //
    //    if (matches.get()){

    if (limit == null) {
      limit = 100L;
    }

    if (skip == null) {
      skip = 0L;
    }

    List<Map<String, Object>> querRes = App
        .g.V()
        .has(search, neq(""))
        .limit(limit + skip)
        .skip(skip)
        .as("matches")
        .match(
            __.as("matches").values(search).as("val")
            , __.as("matches").id().as("id")
        )
        .select("id", "val")
        .toList();

    List<ReactSelectOptions> selectOptions = new ArrayList<>(querRes.size());

    for (Map<String, Object> res : querRes) {
      selectOptions.add(new ReactSelectOptions(res.get("val").toString(), res.get("id").toString()));
    }

    FormioSelectResults retVal = new FormioSelectResults(selectOptions);

    return retVal;

    //    }
    //
    //    return new FormioSelectResults();

  }

  @POST
  @Path("vertex_labels")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

  public VertexLabelsReply vertexLabels(@Context ContainerRequestContext req, String str) {
    try {
      if (!isAdminOrDPO(req)) {
        return new VertexLabelsReply(Response.Status.UNAUTHORIZED,
            "Auth Token does not give rights to execute this");
      }

      VertexLabelsReply reply = new VertexLabelsReply(
          App.graph.getRawDatabase().getMetadata().getSchema().getClasses());

      return reply;
    } catch (Exception e) {

    }
    return new VertexLabelsReply();

  }

  @POST
  @Path("country_data_count")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

  public CountryDataReply countryDataCount(CountryDataRequest req) {
    if (req != null) {

      String searchStr = req.searchStr;

      //      GraphTraversal g =
      try {
        GraphTraversal resSet = App.g.V(); //.has("Metadata_Type", "Person_Natural");
        //        Boolean searchExact = req.search.getSearchExact();

        CountryDataReply data = new CountryDataReply();

        List<Map<String, Long>> res =
            StringUtils.isNotEmpty(searchStr) ?
                resSet.has("Person_Natural_FullName", P.eq(searchStr)).values("Person_Natural_Nationality")
                    .groupCount()
                    .toList() :
                resSet.has("Person_Natural_Nationality").values("Person_Natural_Nationality").groupCount().toList();

        if (res.size() == 1) {
          data.countryData.putAll(res.get(0));
        }

        return data;

      } catch (Throwable t) {
        t.printStackTrace();
      }

    }

    return new CountryDataReply();

  }

  @POST
  @Path("discovery")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

  public DiscoveryReply discovery(DiscoveryRequest req) {
    List<OProperty> props = new LinkedList<>();
    Collection<OClass> classes =
        App.graph.getRawDatabase().getMetadata().getSchema().getClasses();

    Pattern reqPatt = null;

    if (req.regexPattern != null) {
      reqPatt = Pattern.compile(req.regexPattern, Pattern.CASE_INSENSITIVE);
    }

    final Pattern pattern = reqPatt;
    for (OClass oClass : classes) {
      String lbl = oClass.getName();
      oClass.properties().forEach(oProperty ->
      {
        String currLabel = oProperty.getName();
        if (currLabel.startsWith(lbl)) {
          if (pattern != null && pattern.matcher(currLabel).find()) {
            props.add(oProperty);
          } else {
            props.add(oProperty);
          }
        }
      });
    }

    DiscoveryReply reply = new DiscoveryReply();
    reply.colMatchPropMap = new HashMap<>(req.colMetaData.size());
    for (ColMetaData metadata : req.colMetaData) {
      for (OProperty poleProperty : props) {
        int numHits = 0, totalCount = metadata.vals.size();
        for (String val : metadata.vals) {
          if (poleProperty.getType() == OType.STRING && poleProperty.getAllIndexes().size() > 0) {
            if (App.g.V().has(poleProperty.getName(), P.eq(val)).hasNext()) {
              numHits++;
            }
          }
        }
        if (totalCount > 0) {
          double probability = (double) numHits / (double) totalCount;
          if (probability > req.percentThreshold) {
            List<ColMatchProbability> probabilitiesList = reply.colMatchPropMap
                .putIfAbsent(metadata, new LinkedList<>());
            ColMatchProbability colMatchProbability = new ColMatchProbability(poleProperty.getName(), probability);
            probabilitiesList.add(colMatchProbability);
          }
        }
      }
    }
    return reply;
  }

  @POST
  @Path("node_property_names")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

  public NodePropertyNamesReply nodeProperties(
      @Context ContainerRequestContext req,
      VertexLabelsReply vlabReq) {
    if (!isAdminOrDPO(req)) {
      return new NodePropertyNamesReply(Response.Status.UNAUTHORIZED,
          "Auth Token does not give rights to execute this");
    }

    if (vlabReq == null || vlabReq.labels == null || vlabReq.labels.length == 0 || vlabReq.labels[0].value == null) {
      return new NodePropertyNamesReply(Response.Status.BAD_REQUEST, "Missing request labels");
    }
    try {

      //        String[] labels = new String[req.labels.length - 1];
      //        String label0 = req.labels[0].value;
      //        GraphTraversal g = App.g.V();
      //        for (int i = 0, ilen = req.labels.length; i < ilen; i++)
      //        {
      //          g = g.has("Metadata_Type", req.labels[i].value).range(0, 1);
      //
      //          //          labels[i] = (req.labels[i + 1].value);
      //
      //        }

      Set<String> props = new HashSet<>();
      final String label = vlabReq.labels[0].value;

      OClass oClass = App.graph.getRawDatabase().getMetadata().getSchema().getClass(label);

      oClass.properties().forEach(oProperty ->
          {
            String currLabel = oProperty.getName();
            if (currLabel.startsWith(label)) {
              String labelPrefix = "#";
              try {
                final AtomicReference<Boolean> isIndexed = new AtomicReference<>(false);
                oClass.getClassIndexes().forEach(idx ->
                {
                  isIndexed.set(isIndexed.get() || idx.getDefinition().getFields().contains(currLabel));

                });

                if (!isIndexed.get()) {
                  labelPrefix = "";
                }
              } catch (Throwable t) {
                labelPrefix = "";
              }

              props.add(labelPrefix + currLabel);
            }

          }

      );

      List<Map<Object, Object>> notificationTemplates = App.g.V()
          .has("Object_Notification_Templates_Types", eq(label))
          .valueMap("Object_Notification_Templates_Label",
              "Object_Notification_Templates_Text",
              "Object_Notification_Templates_Id")
          .toList();

      boolean useNewMode = vlabReq.version != null && vlabReq.version.startsWith("v2.");
      notificationTemplates.forEach(map -> {
        props.add("@" + map.get("Object_Notification_Templates_Label") + "@" +
            (useNewMode?
              map.get("Object_Notification_Templates_Id"):
              map.get("Object_Notification_Templates_Text"))
        );

      });

      return new NodePropertyNamesReply(props);
//      return reply;

    } catch (Throwable e) {
      e.printStackTrace();
      return new NodePropertyNamesReply(Response.Status.INTERNAL_SERVER_ERROR,e.getMessage());
    }
//    return new NodePropertyNamesReply(Collections.EMPTY_SET);
  }

  @POST
  @Path("edge_labels")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

  public EdgeLabelsReply edgeLabels(String str) {

    EdgeLabelsReply reply = new EdgeLabelsReply(App.graph.getRawDatabase().getMetadata().getSchema().getClasses());

    return reply;
  }

  @GET
  @Path("param")
  @Produces(MediaType.TEXT_PLAIN)
  public String paramMethod(@QueryParam("name") String name,
                            @HeaderParam("X-PV-NAME") String auth) {
    return "Hello, " + name + " X-PV-NAME" + auth;
  }

  @GET
  @Path("grafana_backend")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

// why grafanaBackendHealthCheck function takes a String as param if it doesn't seem to make use of it ?!?!
  public GrafanaHealthcheckReply grafanaBackendHealthCheck(String str) {
    return new GrafanaHealthcheckReply("success", "success", "Data source is working");
/*
status: "success", message: "Data source is working", title: "Success"
 */

  }

  @POST
  @Path("grafana_backend/search")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

  public String grafanaBackendSearch(ContainerRequest request) throws IOException {

    String reqStr = IOUtils.toString(request.getEntityStream());

    System.out.println(reqStr);

    return "{\"status\": \"OK\"}";

  }

  @POST
  @Path("grafana_backend/annotations")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

  public GrafanaAnnotationReply[] grafanaBackendAnnotations(GrafanaAnnotationRequest request) {

    //    reply.setText("");

    List<GrafanaAnnotationReply> retVal = new LinkedList<>();

    //    List<Map<String, Object>> res = new LinkedList<>();
    //    String queryFromGrafanaStr = request.getAnnotation().getQuery();

    String sqlQueryData = request.getSQLQuery();
    OResultSet oResultSet = App.graph.executeSql(sqlQueryData, Collections.EMPTY_MAP)
        .getRawResultSet();
    Long lastTime = 0L;
    Map<Long, List<GrafanaAnnotationReply>> perTimeMap = new HashMap<>();
    while (oResultSet.hasNext()) {
      OResult oResult = oResultSet.next();
      Map<String, Object> props = new HashMap<>();
      oResult.getPropertyNames().forEach(propName -> props.put(propName, oResult.getProperty(propName)));
      //      oResult.getIdentity().ifPresent(id -> props.put("id", id.toString()));
      GrafanaAnnotationReply reply = new GrafanaAnnotationReply();
      reply.setAnnotation(request.getAnnotation());
      reply.setTitle(request.getAnnotation().getQuery());
      reply.setText(props.get("description").toString());
      Long currTime = (Long) props.get("event_time");

      List<GrafanaAnnotationReply> entries = perTimeMap.putIfAbsent(currTime, new LinkedList<>());
      if (entries == null) {
        entries = perTimeMap.get(currTime);
      }
      reply.setTime(currTime);

      //    reply.setText("");

      entries.add(reply);

    }
    StringBuilder sb = new StringBuilder();

    perTimeMap.forEach((timestamp, grafanaAnnotationReplies) -> {
      GrafanaAnnotationReply reply = new GrafanaAnnotationReply();
      reply.setAnnotation(request.getAnnotation());
      reply.setTitle(request.getAnnotation().getQuery());
      sb.setLength(0);
      grafanaAnnotationReplies.forEach(
          grafanaAnnotationReply -> sb.append(grafanaAnnotationReply.getText()).append("\n"));
      reply.setText(sb.toString());
      reply.setTime(timestamp);
      retVal.add(reply);

    });

    oResultSet.close();

    return retVal.toArray(new GrafanaAnnotationReply[0]);

  }

  @POST
  @Path("grafana_backend/query")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public String grafanaBackendQuery(ContainerRequest request) throws IOException {
    String reqStr = IOUtils.toString(request.getEntityStream());

    System.out.println(reqStr);
    return "{\"status\": \"OK\"}";

  }

//  public GrafanaQueryResponse[] grafanaBackendQuery(GrafanaQueryRequest request) {

//    GrafanaTarget[] targets = request.getTargets();
//    List<GrafanaQueryResponse> retVal = new LinkedList<>();
//
//    for (int i = 0; i < targets.length; i++) {
//      GrafanaQueryResponse reply = new GrafanaQueryResponse(
//          targets[i].getTarget(), new long[][]{}
//      );
//
//      retVal.add(reply);
//    }
//
//    return retVal.toArray(new GrafanaQueryResponse[0]);

//  }

  @POST
  @Path("gremlin")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public String gremlinQuery(@Context ContainerRequestContext req,
                             String requestStr) {

    if (!isAdminOrDPO(req)) {
      return "{\"status\": \"unauthorized\"}";
    }

    GremlinRequest request = gson.fromJson(requestStr, GremlinRequest.class);
    final UUID uuid = request.requestId == null ? UUID.randomUUID() :
        UUID.fromString(request.requestId);
    if (request.gremlin == null) {
      System.err.println("Failed to find the gremlin query: " + request);
      throw new BadRequestException("Invalid request; missing the gremlin query");
    }
    IoRegistry registry = OrientIoRegistry.getInstance();

    GraphSONMapper.Builder builder = GraphSONMapper.build().addRegistry(registry);
    GraphSONMessageSerializerV3d0 serializer = new GraphSONMessageSerializerV3d0(builder);

    try {
      // TODO: apply filter to request.gremlin to only allow certain queries.

      System.out.println("Received inbound request:" + request);
      Object res;


      String gremlin = request.gremlin;

      if (request.bindings == null) {
        res = App.executor.eval(gremlin).get();
      } else {
//        Bindings bindingsMap = new SimpleBindings();
//        request.getAsJsonObject("bindings").entrySet().forEach(entry ->
//            bindingsMap.put(entry.getKey(),entry.getValue().getAsString()));
//        ObjectMapper mapper = new ObjectMapper();

//        JsonObject bindingsNode = request.getAsJsonObject("bindings");
//        Bindings bindings = mapper.convertValue(bindingsNode, new TypeReference<Bindings>() { });

//        Map<String, Object> bindings = new HashMap<>();
//
//        NamedNodeMap attribs = ((Element) request.bindings).getAttributes();
//        for (int i = 0, ilen = attribs.getLength(); i < ilen; i++){
//          bindings.put(attribs.item(i).getNodeName(), attribs.item(i).getNodeValue());

//        Bindings bindings = gson.fromJson(request.bindings,Bindings.class);
        request.bindings.put("g", App.g);

//            .entrySet().stream()
//            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        res = App.executor.eval(gremlin, request.bindings).get();
      }

      final ResponseMessage msg = ResponseMessage.build(uuid)
          .result(IteratorUtils.asList(res))
          .code(ResponseStatusCode.SUCCESS).create();

      return serializer.serializeResponseAsString(msg);

//      JsonObject jsonObject = new JsonObject();
//      jsonObject.addProperty("requestId", uuid.toString());
//      jsonObject.add("status", gson.toJsonTree(msg.getStatus()));
//      jsonObject.add("result", gson.toJsonTree(msg.getResult()));
//      return jsonObject.toString();


    } catch (InterruptedException | ExecutionException | SerializationException e) {
      e.printStackTrace();

      final ResponseMessage msg = ResponseMessage.build(uuid)
          .statusMessage(e.getMessage())
          .code(ResponseStatusCode.SERVER_ERROR_SCRIPT_EVALUATION).create();

      try {
        return serializer.serializeResponseAsString(msg);
      } catch (SerializationException serializationException) {
        serializationException.printStackTrace();

        return "{ \"error\":  \"" + e.getMessage() + "\" }";
      }
//      JsonObject jsonObject = new JsonObject();
//      jsonObject.addProperty("requestId", uuid.toString());
//      jsonObject.add("status", JsonParser.parseString(gson.toJson(msg.getStatus())));
//      return jsonObject.toString();

    }

  }

// Work In Progress
  @POST
  @Path("admin/mapping")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)

  public String mappingPost(MappingReq request) {
    return App.g.V().addV("Object_Data_Src_Mapping_Rule")
        .property("Name", "")
        .property("Create_Date", "")
        .property("Update_Date", "")
        .property("Business_Rules_JSON", "")
        .property("", "").next().id().toString();

  }


  @POST
  @Path("admin/report/template/upsert")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

  public ReportTemplateUpsertResponse reportTemplateUpsert(ReportTemplateUpsertRequest request) {

    if (request.getTemplateName() == null) {
      return new ReportTemplateUpsertResponse(Response.Status.BAD_REQUEST, "Missing Template Name");
    }
    if (request.getTemplatePOLEType() == null) {
      return new ReportTemplateUpsertResponse(Response.Status.BAD_REQUEST, "Missing POLE Type");
    }

    String templateName = request.getTemplateName();
    String templatePOLEType = request.getTemplatePOLEType();
    String templateId = gdpr.upsertNotificationTemplate(templatePOLEType, templateName, request.getReportTextBase64());
    System.out.println("Upsert templateId ");

    ReportTemplateUpsertResponse reply = new ReportTemplateUpsertResponse(templatePOLEType,
        templateName, templateId);
    return reply;

  }

  @POST
  @Path("report/template/render")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)

  public ReportTemplateRenderResponse reportTemplateRender(ReportTemplateRenderRequest request) {

    if (request.getTemplateId() == null) {
      return new ReportTemplateRenderResponse(Response.Status.BAD_REQUEST, "Missing ReportId");
    }
    if (request.getRefEntryId() == null) {
      return new ReportTemplateRenderResponse(Response.Status.BAD_REQUEST, "Missing RefId");
    }

    String templateId = request.getTemplateId();

    GraphTraversal<Vertex, Vertex> trav = App.g.V().has("Object_Notification_Templates_Id", P.eq(templateId));

    if (!trav.hasNext()) {
      return new ReportTemplateRenderResponse(Response.Status.NOT_FOUND, "Cannot find template id " +
          templateId);

    }


    ORecordId refId = new ORecordId(request.getRefEntryId());

    String templateTextBase64 = App.g.V().has("Object_Notification_Templates_Id", P.eq(templateId))
        .values("Object_Notification_Templates_Text").next().toString();
    String resolvedStr = "";

    try {
      resolvedStr = PontusJ2ReportingFunctions.renderReportInBase64(refId, templateTextBase64, App.g);
    } catch (Throwable t){

      resolvedStr = Base64.getEncoder().encodeToString(
          ("Error resolving template:  " + t.getMessage()).getBytes(StandardCharsets.UTF_8));
    }

    ReportTemplateRenderResponse reply = new ReportTemplateRenderResponse();
    reply.setBase64Report(resolvedStr);
    reply.setRefEntryId(request.getRefEntryId());
    reply.setTemplateId(request.getTemplateId());
    return reply;

  }


}