package com.pontusvision.graphutils

import com.google.common.util.concurrent.AtomicDouble
import com.orientechnologies.orient.core.id.ORID
import com.pontusvision.gdpr.App
import com.pontusvision.gdpr.mapping.Edge
import com.pontusvision.gdpr.mapping.UpdateReq
import com.pontusvision.gdpr.mapping.Vertex
import com.pontusvision.gdpr.mapping.VertexProps
import org.apache.tinkerpop.gremlin.orientdb.OrientStandardGraph
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.structure.Transaction

import java.text.SimpleDateFormat

class FileNLPRequest implements Serializable {
  String metadataController
  String metadataGDPRStatus
  String metadataLineage
  String dataSourceName

  String pg_currDate
  String pg_content
  String[] address
  String[] cred_card
  String[] email
  String[] location
  String[] person
  String[] phone
  String[] postcode
  String[] policy_number
  String[] org
  String[] nationality
  String[] language
  String[] misc
  String[] money
  String[] date
  String[] time
  String[] categories
  String[] cpf
  String[] cnpj

  String name
  String created
  String fileType
  String lastAccess
  String owner
  String path
  String server
  Long sizeBytes


  static Map<String, String> getMapFromFileNLPRequest(FileNLPRequest req) {
    Map<String, String> retVal = [:]

    retVal.put("created", req.created)
    retVal.put("fileType", req.fileType)
    retVal.put("lastAccess", req.lastAccess)
    retVal.put("name", req.name)
    retVal.put("owner", req.owner)
    retVal.put("path", req.path)
    retVal.put("server", req.server)
    retVal.put("metadataController", req.metadataController?.toString() ?: "[]")
    retVal.put("metadataGDPRStatus", req.metadataGDPRStatus?.toString() ?: "[]")
    retVal.put("metadataLineage", req.metadataLineage?.toString() ?: "[]")
    retVal.put("pg_currDate", req.pg_currDate?.toString() ?: "[]")
    retVal.put("pg_content", req.pg_content?.toString() ?: "[]")
    retVal.put("nlp_location", req.location?.toArrayString() ?: "[]")
    retVal.put("nlp_person", req.person?.toArrayString() ?: "[]")
    retVal.put("nlp_phone", req.phone?.toArrayString() ?: "[]")
    retVal.put("nlp_postcode", req.postcode?.toArrayString() ?: "[]")
    retVal.put("nlp_policy_number", req.policy_number?.toArrayString() ?: "[]")
    retVal.put("nlp_nationality", req.nationality?.toArrayString() ?: "[]")
    retVal.put("nlp_address", req.address?.toArrayString() ?: "[]")
    retVal.put("nlp_cred_card", req.cred_card?.toString() ?: "[]")
    retVal.put("nlp_email", req.email?.toArrayString() ?: "[]")
    retVal.put("nlp_date", req.date?.toArrayString() ?: "[]")
    retVal.put("nlp_time", req.time?.toArrayString() ?: "[]")
    retVal.put("nlp_money", req.money?.toArrayString() ?: "[]")
    retVal.put("nlp_misc", req.misc?.toArrayString() ?: "[]")
    retVal.put("nlp_language", req.language?.toArrayString() ?: "[]")
    retVal.put("nlp_org", req.org?.toArrayString() ?: "[]")

    return retVal
  }

  static Vertex createObjectDataSourceVtx(UpdateReq req, String dataSourceType = 'Office365/email', String status = 'In Progress', Boolean isStart = null, String errorStr = null) {
    final String vtxLabel = 'Object_Data_Source'
    Vertex vtx = new Vertex()
    vtx.label = vtxLabel
    vtx.name = vtxLabel
    VertexProps vtxProps = new VertexProps()
    vtxProps.name = "${vtxLabel}_Name"
    vtxProps.mandatoryInSearch = true
    vtxProps.val = dataSourceType

    VertexProps vtxPropStatus = new VertexProps()
    vtxPropStatus.name = "${vtxLabel}_Status"
    vtxPropStatus.mandatoryInSearch = false
    vtxPropStatus.excludeFromSearch = true
    vtxPropStatus.excludeFromUpdate = false
    vtxPropStatus.val = status



    vtx.props = [vtxProps, vtxPropStatus]
    if (isStart != null){
      VertexProps vtxPropTime = new VertexProps()
      vtxPropTime.name = "${vtxLabel}_Ingestion_${isStart?'Start':'Finish'}"
      vtxPropTime.mandatoryInSearch = false
      vtxPropTime.excludeFromSearch = true
      vtxPropTime.excludeFromUpdate = false
      vtxPropTime.val = new Date()
      vtx.props.push(vtxPropTime)
    }
    if (errorStr != null){
      VertexProps vtxPropError = new VertexProps()
      vtxPropError.name = "${vtxLabel}_Error"
      vtxPropError.mandatoryInSearch = false
      vtxPropError.excludeFromSearch = true
      vtxPropError.excludeFromUpdate = false
      vtxPropError.val = errorStr
      vtx.props.push(vtxPropError)
    }
    req.vertices.push(vtx)
    return vtx
  }

  static void upsertFileNLPRequestArray(
          OrientStandardGraph graph,
          GraphTraversalSource g,
          FileNLPRequest[] reqs,
          String ruleName) {
    Boolean isSlim = App.useSlim || ruleName?.toLowerCase()?.contains("slim");

    Transaction trans = App.graph.tx()
    try {
      if (!trans.isOpen()) {
        trans.open()
      }
      for (FileNLPRequest req : reqs) {
        upsertFileNLPRequest(graph, g, req, isSlim)
      }
      trans.commit()
    } catch (Throwable t) {
      trans.rollback()
      throw t
    } finally {
      trans.close()
    }
  }


  static Map<String, Map<ORID, AtomicDouble>> upsertFileNLPRequest(
          OrientStandardGraph graph,
          GraphTraversalSource g,
          FileNLPRequest req,
          Boolean useSlim) {
    Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName

    UpdateReq updateReq = new UpdateReq()
    updateReq.vertices = []
    updateReq.edges = []
    String dataSourceName = req.dataSourceName?: "file_server_${req.server}"
    Vertex dataSourceVtx = createObjectDataSourceVtx(updateReq, dataSourceName)
    Vertex eventGroupFileIngestionVtx = createEventGroupIngestionVtx(updateReq, dataSourceName)

    createEdge('Has_Ingestion_Event', dataSourceVtx.name, eventGroupFileIngestionVtx.name, updateReq)

    Vertex eventFileVtx = createEventFileIngestionVtx(req, updateReq)
    createEdge('Has_Ingestion_Event', eventGroupFileIngestionVtx.name, eventFileVtx.name, updateReq)

    if (useSlim){
      processUpsertFileNLPRequestSlim(graph, g, req, updateReq)
    }
    else {
      processUpsertFileNLPRequest(graph,g,req,updateReq)

    }

  }
  static void upsertDataSourceStatus(String dataSourceName, String status, Boolean isStart = null, String errorStr = null){
    UpdateReq updateReq = new UpdateReq()
    updateReq.vertices = []
    updateReq.edges = []

    createObjectDataSourceVtx(updateReq, dataSourceName, status, isStart, errorStr)
    def (
    List<MatchReq>            matchReqs,
    Map<String, AtomicDouble> maxScoresByVertexName,
    Map<String, Double>       percentageThresholdByVertexName
    ) = Matcher.getMatchRequests(Collections.EMPTY_MAP, updateReq, 0.0)

    List<MatchReq> mandatoryFields = matchReqs.findAll { it2 -> it2.mandatoryInSearch }


    String whereClause = Matcher.createWhereClauseAttribs(mandatoryFields)

    def (String jsonToMerge, Map<String, Object> sqlParams) = Matcher.createJsonMergeParam(matchReqs,"Object_Data_Source")

    App.graph.executeSql("UPDATE `Object_Data_Source` MERGE ${jsonToMerge}  UPSERT  RETURN AFTER WHERE ${whereClause} LOCK record LIMIT 1 ",
            sqlParams   ).toList()

  }

  static processUpsertFileNLPRequestSlim(OrientStandardGraph graph,
                                     GraphTraversalSource g,
                                     FileNLPRequest req,
                                     UpdateReq updateReq){

    StringBuffer sb = new StringBuffer()
    Double pcntThreshold = 1.0
    Map<String, String> item = getMapFromFileNLPRequest(req)
    def (
    List<MatchReq>            matchReqs,
    Map<String, AtomicDouble> maxScoresByVertexName,
    Map<String, Double>       percentageThresholdByVertexName
    ) = Matcher.getMatchRequests(item as Map<String, String>, updateReq, pcntThreshold, sb)

    def (
    Map<String, List<MatchReq>>            matchReqByVertexName,
    Map<String, Map<ORID, List<MatchReq>>> matchReqListByOridByVertexName
    ) = Matcher.matchVerticesSlim(graph, g, matchReqs)

    def (Map<String, List<EdgeRequest>> edgeReqsByVertexName, Set<EdgeRequest> edgeReqs) =
    Matcher.parseEdges(updateReq)

    Matcher.createEdgesSlim(g, (Set<EdgeRequest>) edgeReqs, matchReqListByOridByVertexName)


//    Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = processUpdateReq(g, vertexScoreMapByVertexNameLocal, matchReqByVertexName, maxScoresByVertexName,
//            percentageThresholdByVertexName, edgeReqsByVertexName, edgeReqs)

    matchReqListByOridByVertexName.get("Event_File_Ingestion").entrySet().forEach({
      createEventNLPGroups(req, it.key, req.email)
    })


  }

  static processUpsertFileNLPRequest(OrientStandardGraph graph,
                                     GraphTraversalSource g,
                                     FileNLPRequest req,
                                     UpdateReq updateReq){

    StringBuffer sb = new StringBuffer()
    Double pcntThreshold = 1.0
    Map<String, String> item = getMapFromFileNLPRequest(req)
    def (
    List<MatchReq>            matchReqs,
    Map<String, AtomicDouble> maxScoresByVertexName,
    Map<String, Double>       percentageThresholdByVertexName
    ) = Matcher.getMatchRequests(item as Map<String, String>, updateReq, pcntThreshold, sb)

    def (
    Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexNameLocal,
    Map<String, List<MatchReq>>          matchReqByVertexName
    ) = Matcher.matchVertices(graph, g, matchReqs, 100, sb)

//    vertexScoreMapByVertexName = vertexScoreMapByVertexNameLocal

    def (Map<String, List<EdgeRequest>> edgeReqsByVertexName, Set<EdgeRequest> edgeReqs) =
    Matcher.parseEdges(updateReq)

    Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = processUpdateReq(g, vertexScoreMapByVertexNameLocal, matchReqByVertexName, maxScoresByVertexName,
            percentageThresholdByVertexName, edgeReqsByVertexName, edgeReqs)

    finalVertexIdByVertexName.get("Event_File_Ingestion").entrySet().forEach({
      createEventNLPGroups(req, it.key, req.email)
    })


  }

  static Map<String, Map<ORID, AtomicDouble>> processUpdateReq(
          final GraphTraversalSource g,
          final Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName,
          final Map<String, List<MatchReq>> matchReqByVertexName,
          final Map<String, AtomicDouble> maxScoresByVertexName,
          final Map<String, Double> percentageThresholdByVertexName,
          final Map<String, List<EdgeRequest>> edgeReqsByVertexName,
          final Set<EdgeRequest> edgeReqs

  ) {

    Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = new HashMap()

    vertexScoreMapByVertexName.each { String vertexTypeStr, Map<ORID, AtomicDouble> potentialHitIDs ->

      List<MatchReq> matchReqsForThisVertexType = matchReqByVertexName.get(vertexTypeStr)

      double maxScore = maxScoresByVertexName.get(vertexTypeStr).get()
      double percentageThreshold = percentageThresholdByVertexName.get(vertexTypeStr)
      double scoreThreshold = (double) (maxScore * 100 * (percentageThreshold / 100) / 100)

      Map<ORID, AtomicDouble> topHits = Matcher.getTopHitsWithEdgeCheck(g, potentialHitIDs, scoreThreshold,
              vertexScoreMapByVertexName, vertexTypeStr, edgeReqsByVertexName)

      if (topHits != null && topHits.size() > 0) {

        Matcher.updateExistingVertexWithMatchReqs(g, topHits, matchReqsForThisVertexType, scoreThreshold)
        finalVertexIdByVertexName.put((String) vertexTypeStr, topHits)
      } else {
        Map<ORID, AtomicDouble> newVertices = new HashMap<>()
        List<ORID> vIds = Matcher.addNewVertexFromMatchReqs(g, (String) vertexTypeStr, matchReqsForThisVertexType)
        int vlen = vIds.size()
        for (int v = 0; v < vlen; v++) {
          ORID vId = vIds.get(v)
          newVertices.put(vId, new AtomicDouble(maxScore))
          finalVertexIdByVertexName.put((String) vertexTypeStr, newVertices)

          if ('Event_Ingestion'.equalsIgnoreCase(matchReqsForThisVertexType?.get(0)?.getVertexLabel())) {
            String bizRule = JsonSerializer.gson.toJson(matchReqByVertexName)
            g.V(vId).property('Event_Ingestion_Business_Rules', bizRule).next()
          }
        }


      }


    }

    Matcher.createEdges(g, (Set<EdgeRequest>) edgeReqs, finalVertexIdByVertexName, maxScoresByVertexName)

    return finalVertexIdByVertexName
  }

  static Vertex createEventGroupIngestionVtx(UpdateReq updateReq, String groupName,
                                             final String vtxLabel = 'Event_File_Group_Ingestion') {

    Vertex vtx = new Vertex()
    vtx.label = vtxLabel
    vtx.name = vtxLabel
    VertexProps vtxProps = new VertexProps()
    vtxProps.name = "${vtxLabel}_Ingestion_Date"
    vtxProps.mandatoryInSearch = true
    vtxProps.val = new SimpleDateFormat('yyyy-MM-dd').format(new Date())
    VertexProps vtxPropsGroupName = new VertexProps()
    vtxPropsGroupName.name = "${vtxLabel}_Group_Name"
    vtxPropsGroupName.mandatoryInSearch = true
    vtxPropsGroupName.val = groupName

    vtx.props = [vtxProps, vtxPropsGroupName]

    updateReq.vertices.push(vtx)
    return vtx
  }

  static Set<ORID> createEventNLPGroups(FileNLPRequest req, ORID emailBodyOrAttachment, String[] emailAddrs,
                                        minThreshold = 1, Integer maxThreshold = 100) {

    List <Traversal> personOptions = new LinkedList<>()
    if (req.person) {
      String[] person = req.person
      for (int i = 0; i < person.length; i++) {
        personOptions.add( __.has('Person_Natural_Full_Name', P.eq(person[i]?.toUpperCase())))
      }
    }


    List<Traversal> cpfOptions = new LinkedList<>()

    if (req.cpf) {
      String[] cpfs = req.cpf
      for (int i = 0; i < cpfs.length; i++) {
        String cpf = cpfs[i] != null? cpfs[i].replaceAll("[^0-9]",""): null;
        if (cpf == null){
          continue;
        }
        cpfOptions.push( __.has('Person_Natural_Customer_ID', P.eq(cpf)))
      }
    }

    List<Traversal> emailOptions = new LinkedList<>()
    if (emailAddrs) {
      for (int i = 0; i < emailAddrs.length; i++) {
        emailOptions.push(__.has('Object_Email_Address_Email', P.eq(emailAddrs[i]?.toLowerCase())))
      }
    }

    Traversal[] personNameCpfOptions =
            [personOptions.toArray(new Traversal[0]), cpfOptions.toArray(new Traversal[0])]
            .flatten().toArray(new Traversal[0])

    System.out.println("NLP searching for matches for ${personOptions?.size()?:0} names, ${cpfOptions?.size()?:0} cpfs, ${emailAddrs?.length?:0} emails in file ${req.name}")


    def persons = personNameCpfOptions.length> 0?
            (App.g.V().or(personNameCpfOptions)):
            null


//    GraphTraversal personsClone =  persons?.clone() as GraphTraversal

    Set<ORID> retVal = (persons?.id()?.toSet())
    if (!retVal){
      retVal = []
    }

    System.out.println("NLP found ${retVal?.size()} graph person matches on cust id or name from file ${req.name}")

    if (emailOptions.size() > 0){
      def emails = App.g.V().or(emailOptions.toArray(new Traversal[0]) as Traversal<?, ?>[])


      try {
        Set<ORID> emailSet = emails?.in('Uses_Email')?.id()?.toSet()
        if (emailSet && emailSet.size() > 0){
          retVal.addAll(emailSet);
          System.out.println("NLP found ${emailSet?.size()} graph person matches on email from file ${req.name}")
        }

//      retVal = persons?.hasId(P.within(emails.in('Uses_Email')?.id()?.toList()))?.id()?.toSet() as Set<ORID>:
//               emails?.in('Uses_Email')?.id()?.toSet() as Set<ORID>

      } catch (Throwable t) {
        //ignore
      }
    }

    String currDate = new SimpleDateFormat('yyyy-MM-dd').format(new Date())

    if (!retVal ) {
      retVal = []
    }

    int count = 0
    for (ORID orid : (retVal as Set<ORID>)) {

      String custId = App.g.V(orid).values('Person_Natural_Customer_ID').next().toString()
      def nlpGroupTrav =
              App.g.V().has('Event_NLP_Group_Person_Id', custId)
                      .has('Event_NLP_Group_Ingestion_Date', currDate)
                      .id()

      def nlpGroupVtxId
      if (nlpGroupTrav.hasNext()) {
        nlpGroupVtxId = nlpGroupTrav.next()
      } else {
        nlpGroupVtxId = App.g.addV('Event_NLP_Group')
                .property('Event_NLP_Group_Person_Id', custId)
                .property('Event_NLP_Group_Ingestion_Date', currDate).id().next()
      }
      upsertEdge (emailBodyOrAttachment,nlpGroupVtxId,'Has_NLP_Events')
      upsertEdge (nlpGroupVtxId,orid, 'Has_NLP_Events')
      count++
      if (count >= maxThreshold) {
        break
      }
    }
    if (retVal.size() == 0){
      System.out.println("Failed to find any NLP events for file ${req.name}")
    }
    else {
      System.out.println("Finished processing ${retVal.size()} NLP events for file ${req.name}")
    }
    return retVal
  }

  static void upsertEdge(ORID fromId, ORID toId, String label){
    ORID[] foundIds = App.g.V(toId)
            .both(label)
            .hasId(P.within(fromId)).id()
            .toSet() as ORID[]


    if (foundIds.size() == 0) {
      App.g.addE(label).from(App.g.V(fromId)).to(App.g.V(toId)).next()

    }

  }

  static Vertex createEventFileIngestionVtx(FileNLPRequest req, UpdateReq updateReq) {
    String fileIngestionVtxLabel = 'Event_File_Ingestion'
    Vertex fileIngestionVtx = new Vertex()
    fileIngestionVtx.props = []

    fileIngestionVtx.label = fileIngestionVtxLabel
    fileIngestionVtx.name = fileIngestionVtxLabel


    if (req.created) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}_Created"
      props.mandatoryInSearch = true
      props.val = req.created
      fileIngestionVtx.props.push(props)
    }
    if (req.fileType) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}_File_Type"
      props.mandatoryInSearch = true
      props.val = req.fileType
      fileIngestionVtx.props.push(props)
    }
    if (req.lastAccess) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}_Last_Access"
      props.mandatoryInSearch = true
      props.val = req.lastAccess
      fileIngestionVtx.props.push(props)
    }
    if (req.name) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}_Name"
      props.mandatoryInSearch = true
      props.val = req.name
      fileIngestionVtx.props.push(props)
    }
    if (req.owner) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}_Owner"
      props.mandatoryInSearch = true
      props.val = req.owner
      fileIngestionVtx.props.push(props)
    }
    if (req.path) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}_Path"
      props.mandatoryInSearch = true
      props.val = req.path
      fileIngestionVtx.props.push(props)
    }
    if (req.server) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}_Server"
      props.mandatoryInSearch = true
      props.val = req.server
      fileIngestionVtx.props.push(props)
    }
    if (req.sizeBytes) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}_Size_Bytes"
      props.mandatoryInSearch = true
      props.val = req.sizeBytes.toString()
      props.type = VertexProps.TypeEnum.JAVA_LANG_DOUBLE
      fileIngestionVtx.props.push(props)
    }
    updateReq.vertices.push(fileIngestionVtx)
    return fileIngestionVtx

  }


  static Edge createEdge(String edgeLabel, String fromVtxName, String toVtxName, UpdateReq req) {
    Edge edge = new Edge()
    edge.label(edgeLabel)
    edge.fromVertexName(fromVtxName)
    edge.toVertexName(toVtxName)

    req.edges.push(edge)

    return edge
  }


  String getMetadataController() {
    return metadataController
  }

  void setMetadataController(String metadataController) {
    this.metadataController = metadataController
  }

  String getMetadataGDPRStatus() {
    return metadataGDPRStatus
  }

  void setMetadataGDPRStatus(String metadataGDPRStatus) {
    this.metadataGDPRStatus = metadataGDPRStatus
  }

  String getMetadataLineage() {
    return metadataLineage
  }

  void setMetadataLineage(String metadataLineage) {
    this.metadataLineage = metadataLineage
  }

  String getPg_currDate() {
    return pg_currDate
  }

  void setPg_currDate(String pg_currDate) {
    this.pg_currDate = pg_currDate
  }

  String getPg_content() {
    return pg_content
  }

  void setPg_content(String pg_content) {
    this.pg_content = pg_content
  }

  String[] getAddress() {
    return address
  }

  void setAddress(String[] address) {
    this.address = address
  }

  String[] getCred_card() {
    return cred_card
  }

  void setCred_card(String[] cred_card) {
    this.cred_card = cred_card
  }

  String[] getEmail() {
    return email
  }

  void setEmail(String[] email) {
    this.email = email
  }

  String[] getLocation() {
    return location
  }

  void setLocation(String[] location) {
    this.location = location
  }

  String[] getPerson() {
    return person
  }

  void setPerson(String[] person) {
    this.person = person
  }

  String[] getPhone() {
    return phone
  }

  void setPhone(String[] phone) {
    this.phone = phone
  }

  String[] getPostcode() {
    return postcode
  }

  void setPostcode(String[] postcode) {
    this.postcode = postcode
  }

  String[] getPolicy_number() {
    return policy_number
  }

  void setPolicy_number(String[] policy_number) {
    this.policy_number = policy_number
  }

  String[] getOrg() {
    return org
  }

  void setOrg(String[] org) {
    this.org = org
  }

  String[] getNationality() {
    return nationality
  }

  void setNationality(String[] nationality) {
    this.nationality = nationality
  }

  String[] getLanguage() {
    return language
  }

  void setLanguage(String[] language) {
    this.language = language
  }

  String[] getMisc() {
    return misc
  }

  void setMisc(String[] misc) {
    this.misc = misc
  }

  String[] getMoney() {
    return money
  }

  void setMoney(String[] money) {
    this.money = money
  }

  String[] getDate() {
    return date
  }

  void setDate(String[] date) {
    this.date = date
  }

  String[] getTime() {
    return time
  }

  void setTime(String[] time) {
    this.time = time
  }

  String[] getCategories() {
    return categories
  }

  void setCategories(String[] categories) {
    this.categories = categories
  }

  String[] getCpf() {
    return cpf
  }

  void setCpf(String[] cpf) {
    this.cpf = cpf
  }

  String[] getCnpj() {
    return cnpj
  }

  void setCnpj(String[] cnpj) {
    this.cnpj = cnpj
  }

  String getName() {
    return name
  }

  void setName(String name) {
    this.name = name
  }

  String getCreated() {
    return created
  }

  void setCreated(String created) {
    this.created = created
  }

  String getFileType() {
    return fileType
  }

  void setFileType(String fileType) {
    this.fileType = fileType
  }

  String getLastAccess() {
    return lastAccess
  }

  void setLastAccess(String lastAccess) {
    this.lastAccess = lastAccess
  }

  String getOwner() {
    return owner
  }

  void setOwner(String owner) {
    this.owner = owner
  }

  String getPath() {
    return path
  }

  void setPath(String path) {
    this.path = path
  }

  String getServer() {
    return server
  }

  void setServer(String server) {
    this.server = server
  }

  Long getSizeBytes() {
    return sizeBytes
  }

  void setSizeBytes(Long sizeBytes) {
    this.sizeBytes = sizeBytes
  }


//  @Override
//  public String toString() {
//    return "FileNLPRequest{" +
//            "metadataController='" + metadataController + '\'' +
//            ", metadataGDPRStatus='" + metadataGDPRStatus + '\'' +
//            ", metadataLineage='" + metadataLineage + '\'' +
//            ", pg_currDate='" + pg_currDate + '\'' +
//            ", pg_content='" + pg_content + '\'' +
//            ", address=" + Arrays.toString(address) +
//            ", cred_card=" + Arrays.toString(cred_card) +
//            ", email=" + Arrays.toString(email) +
//            ", location=" + Arrays.toString(location) +
//            ", person=" + Arrays.toString(person) +
//            ", phone=" + Arrays.toString(phone) +
//            ", postcode=" + Arrays.toString(postcode) +
//            ", policy_number=" + Arrays.toString(policy_number) +
//            ", org=" + Arrays.toString(org) +
//            ", nationality=" + Arrays.toString(nationality) +
//            ", language=" + Arrays.toString(language) +
//            ", misc=" + Arrays.toString(misc) +
//            ", money=" + Arrays.toString(money) +
//            ", date=" + Arrays.toString(date) +
//            ", time=" + Arrays.toString(time) +
//            ", categories=" + Arrays.toString(categories) +
//            ", cpf=" + Arrays.toString(cpf) +
//            ", cnpj=" + Arrays.toString(cnpj) +
//            ", name='" + name + '\'' +
//            ", created='" + created + '\'' +
//            ", fileType='" + fileType + '\'' +
//            ", lastAccess='" + lastAccess + '\'' +
//            ", owner='" + owner + '\'' +
//            ", path='" + path + '\'' +
//            ", server='" + server + '\'' +
//            ", sizeBytes=" + sizeBytes +
//            '}';
//  }
}
