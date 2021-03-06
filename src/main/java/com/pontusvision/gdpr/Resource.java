package com.pontusvision.gdpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.orientdb.executor.OGremlinResultSet;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.core.Request;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

@Path("home") public class Resource
{

  //  @Inject
  //  KeycloakSecurityContext keycloakSecurityContext;

  public Resource()
  {

  }

  @GET @Path("hello") @Produces(MediaType.TEXT_PLAIN) public String helloWorld()
  {
    return "Hello, world!";
  }

  Gson gson = new Gson();

  GsonBuilder gsonBuilder = new GsonBuilder();

  @POST @Path("agrecords") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
  public RecordReply agrecords(RecordRequest req)
  {

    if (req.cols != null && req.dataType != null)
    {

      Set<String> valsSet          = new HashSet<>();
      Set<String> reportButtonsSet = new HashSet<>();

      for (int i = 0, ilen = req.cols.length; i < ilen; i++)
      {
        if (!req.cols[i].id.startsWith("@"))
        {
          valsSet.add(req.cols[i].id);
        }
        else
        {
          reportButtonsSet.add(req.cols[i].id);
        }

      }

      String[] vals = valsSet.toArray(new String[valsSet.size()]);

      try
      {

        String sqlQueryCount = req.getSQL(true);

        String sqlQueryData = req.getSQL(false);

        String dataType = req.dataType; //req.search.extraSearch[0].value;

        boolean hasFilters = req.filters != null && req.filters.length > 0;

        OGremlinResultSet resultSet = App.graph.executeSql(sqlQueryCount, Collections.EMPTY_MAP);
        Long              count     = resultSet.iterator().next().getRawResult().getProperty("COUNT(*)");
        resultSet.close();

        if (count > 0)
        {
          List<Map<String, Object>> res = new LinkedList<>();

          OResultSet oResultSet = App.graph.executeSql(sqlQueryData, Collections.EMPTY_MAP).getRawResultSet();

          while (oResultSet.hasNext())
          {
            OResult             oResult = oResultSet.next();
            Map<String, Object> props   = new HashMap<>();

            oResult.getPropertyNames().forEach(propName -> props.put(propName, oResult.getProperty(propName)));
            oResult.getIdentity().ifPresent(id -> props.put("id", id.toString()));

            res.add(props);
          }

          oResultSet.close();

          String[]     recs      = new String[res.size()];
          ObjectMapper objMapper = new ObjectMapper();

          for (int i = 0, ilen = res.size(); i < ilen; i++)
          {
            Map<String, Object> map = res.get(i);
            Map<String, String> rec = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet())
            {
              Object val = entry.getValue();
              if (val instanceof ArrayList)
              {
                ArrayList<Object> arrayList = (ArrayList) val;

                String val2 = arrayList.get(0).toString();

                rec.put(entry.getKey(), val2);

              }
              else
              {
                rec.put(entry.getKey(), val == null ? null : val.toString());
              }

            }

            recs[i] = objMapper.writeValueAsString(rec);
          }
          RecordReply reply = new RecordReply(req.from, req.to, count, recs);

          return reply;

        }

        RecordReply reply = new RecordReply(req.from, req.to, count, new String[0]);

        return reply;

      }
      catch (Throwable t)
      {
        t.printStackTrace();
      }

    }

    return new RecordReply(req.from, req.to, 0L, new String[0]);

  }

  @POST @Path("graph") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
  public GraphReply graph(GraphRequest greq)
  {

    Set<Vertex> outNodes = App.g.V(Long.parseLong(greq.graphId)).to(Direction.OUT).toSet();
    Set<Vertex> inNodes  = App.g.V(Long.parseLong(greq.graphId)).to(Direction.IN).toSet();
    Vertex      v        = App.g.V(Long.parseLong(greq.graphId)).next();

    Set<Edge> outEdges = App.g.V(Long.parseLong(greq.graphId)).toE(Direction.OUT).toSet();
    Set<Edge> inEdges  = App.g.V(Long.parseLong(greq.graphId)).toE(Direction.IN).toSet();

    GraphReply retVal = new GraphReply(v, inNodes, outNodes, inEdges, outEdges);

    return retVal;
  }

  Map<String, Pattern> compiledPatterns = new HashMap<>();

  @GET @Path("vertex_prop_values") @Produces(MediaType.APPLICATION_JSON)
  public FormioSelectResults getVertexPropertyValues(
      @QueryParam("search") String search
      , @QueryParam("limit") Long limit
      , @QueryParam("skip") Long skip

  )
  {
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

    if (limit == null)
    {
      limit = 100L;
    }

    if (skip == null)
    {
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

    for (Map<String, Object> res : querRes)
    {
      selectOptions.add(new ReactSelectOptions(res.get("val").toString(), res.get("id").toString()));
    }

    FormioSelectResults retVal = new FormioSelectResults(selectOptions);

    return retVal;

    //    }
    //
    //    return new FormioSelectResults();

  }

  @POST @Path("vertex_labels") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public VertexLabelsReply vertexLabels(String str)
  {
    try
    {

      VertexLabelsReply reply = new VertexLabelsReply(
          App.graph.getRawDatabase().getMetadata().getSchema().getClasses());

      return reply;
    }
    catch (Exception e)
    {

    }
    return new VertexLabelsReply();

  }

  @POST @Path("country_data_count") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public CountryDataReply countryDataCount(CountryDataRequest req)
  {
    if (req != null)
    {

      String searchStr = req.searchStr;

      //      GraphTraversal g =
      try
      {
        GraphTraversal resSet = App.g.V(); //.has("Metadata.Type", "Person.Natural");
        //        Boolean searchExact = req.search.getSearchExact();

        CountryDataReply data = new CountryDataReply();

        List<Map<String, Long>> res =
            StringUtils.isNotEmpty(searchStr) ?
                resSet.has("Person.Natural.FullName", P.eq(searchStr)).values("Person.Natural.Nationality")
                      .groupCount()
                      .toList() :
                resSet.has("Person.Natural.Nationality").values("Person.Natural.Nationality").groupCount().toList();

        if (res.size() == 1)
        {
          data.countryData.putAll(res.get(0));
        }

        return data;

      }
      catch (Throwable t)
      {
        t.printStackTrace();
      }

    }

    return new CountryDataReply();

  }

  @POST @Path("node_property_names") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public NodePropertyNamesReply nodeProperties(VertexLabelsReply req)
  {

    try
    {
      if (req != null && req.labels != null && req.labels.length > 0 && req.labels[0].value != null)
      {

        //        String[] labels = new String[req.labels.length - 1];
        //        String label0 = req.labels[0].value;
        //        GraphTraversal g = App.g.V();
        //        for (int i = 0, ilen = req.labels.length; i < ilen; i++)
        //        {
        //          g = g.has("Metadata.Type", req.labels[i].value).range(0, 1);
        //
        //          //          labels[i] = (req.labels[i + 1].value);
        //
        //        }

        Set<String>  props = new HashSet<>();
        final String label = req.labels[0].value;

        OClass oClass = App.graph.getRawDatabase().getMetadata().getSchema().getClass(label);

        oClass.properties().forEach(oProperty ->
            {
              String currLabel = oProperty.getName();
              if (currLabel.startsWith(label))
              {
                String labelPrefix = "#";
                try
                {
                  final AtomicReference<Boolean> isIndexed = new AtomicReference<>(false);
                  oClass.getClassIndexes().forEach(idx ->
                  {
                    isIndexed.set(isIndexed.get() || idx.getDefinition().getFields().contains(currLabel));

                  });

                  if (!isIndexed.get())
                  {
                    labelPrefix = "";
                  }
                }
                catch (Throwable t)
                {
                  labelPrefix = "";
                }

                props.add(labelPrefix + currLabel);
              }

            }

        );

        List<Map<Object, Object>> notificationTemplates = App.g.V()
                                                               .has("Object.Notification_Templates.Types", eq(label))
                                                               .valueMap("Object.Notification_Templates.Label",
                                                                   "Object.Notification_Templates.Text")
                                                               .toList();

        notificationTemplates.forEach(map -> {
          props.add("@" + map.get("Object.Notification_Templates.Label") + "@" + map
              .get("Object.Notification_Templates.Text"));

        });

        NodePropertyNamesReply reply = new NodePropertyNamesReply(props);
        return reply;

      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    return new NodePropertyNamesReply(Collections.EMPTY_SET);
  }

  @POST @Path("edge_labels") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public EdgeLabelsReply edgeLabels(String str)
  {

    EdgeLabelsReply reply = new EdgeLabelsReply(App.graph.getRawDatabase().getMetadata().getSchema().getClasses());

    return reply;
  }

  @GET @Path("param") @Produces(MediaType.TEXT_PLAIN) public String paramMethod(@QueryParam("name") String name,
                                                                                @HeaderParam("AUTHORIZATION") String auth)
  {
    return "Hello, " + name + " AUTHORIZATION" + auth;
  }


  @GET @Path("grafana_backend") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public GrafanaHealthcheckReply grafanaBackendHealthCheck(String str)
  {
    return new GrafanaHealthcheckReply("success", "success", "Data source is working");
/*
status: "success", message: "Data source is working", title: "Success"
 */



  }


  @POST @Path("grafana_backend/search") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public String[] grafanaBackendSearch(ContainerRequest request)
  {

    return new String [] {};


  }

  @POST @Path("grafana_backend/annotations") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public GrafanaAnnotationReply[] grafanaBackendAnnotations(GrafanaAnnotationRequest request)
  {

//    reply.setText("");

    List<GrafanaAnnotationReply> retVal = new LinkedList<>();

//    List<Map<String, Object>> res = new LinkedList<>();
//    String queryFromGrafanaStr = request.getAnnotation().getQuery();


    String sqlQueryData = request.getSQLQuery();
    OResultSet oResultSet = App.graph.executeSql(sqlQueryData, Collections.EMPTY_MAP).getRawResultSet();
    Long lastTime = 0L;
    Map<Long, List<GrafanaAnnotationReply>> perTimeMap = new HashMap<>();
    while (oResultSet.hasNext())
    {
      OResult             oResult = oResultSet.next();
      Map<String, Object> props   = new HashMap<>();
      oResult.getPropertyNames().forEach(propName -> props.put(propName, oResult.getProperty(propName)));
//      oResult.getIdentity().ifPresent(id -> props.put("id", id.toString()));
      GrafanaAnnotationReply reply = new GrafanaAnnotationReply();
      reply.setAnnotation(request.getAnnotation());
      reply.setTitle(request.getAnnotation().getQuery());
      reply.setText(props.get("description").toString());
      Long currTime = (Long)props.get("event_time");

      List<GrafanaAnnotationReply> entries = perTimeMap.putIfAbsent(currTime, new LinkedList<>() );
      if (entries == null){
        entries  = perTimeMap.get(currTime);
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

  @POST @Path("grafana_backend/query") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public GrafanaQueryResponse[] grafanaBackendQuery(GrafanaQueryRequest request)
  {

    GrafanaTarget [] targets = request.getTargets();
    List<GrafanaQueryResponse> retVal = new LinkedList<>();

    for (int i = 0; i < targets.length; i++)
    {
      GrafanaQueryResponse  reply = new GrafanaQueryResponse(
          targets[i].getTarget(),new long[][] {}
      );

      retVal.add(reply);
    }


    return retVal.toArray(new GrafanaQueryResponse[0]);

  }

}