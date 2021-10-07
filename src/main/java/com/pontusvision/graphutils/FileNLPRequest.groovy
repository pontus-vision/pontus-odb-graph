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

  static Vertex createObjectDataSourceVtx(UpdateReq req, String dataSourceType = 'Office365/email') {
    final String vtxLabel = 'Object.Data_Source'
    Vertex vtx = new Vertex()
    vtx.label = vtxLabel
    vtx.name = vtxLabel
    VertexProps vtxProps = new VertexProps()
    vtxProps.name = "${vtxLabel}.Name"
    vtxProps.mandatoryInSearch = true
    vtxProps.val = dataSourceType

    vtx.props = [vtxProps]
    req.vertices.push(vtx)
    return vtx
  }

  static void upsertFileNLPRequestArray(
          OrientStandardGraph graph,
          GraphTraversalSource g,
          FileNLPRequest[] reqs) {
    Transaction trans = App.graph.tx()
    try {
      if (!trans.isOpen()) {
        trans.open()
      }
      for (FileNLPRequest req : reqs) {
        upsertFileNLPRequest(graph, g, req)
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
          FileNLPRequest req) {
    Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName

    UpdateReq updateReq = new UpdateReq()
    updateReq.vertices = []
    updateReq.edges = []
    String dataSourceName = "file_server_${req.server}"
    Vertex dataSourceVtx = createObjectDataSourceVtx(updateReq, dataSourceName)
    Vertex eventGroupFileIngestionVtx = createEventGroupFileIngestionVtx(updateReq, dataSourceName)

    createEdge('Has_Ingestion', dataSourceVtx.name, eventGroupFileIngestionVtx.name, updateReq)

    Vertex eventFileVtx = createEventFileIngestionVtx(req, updateReq)
    createEdge('Has_Ingestion', eventGroupFileIngestionVtx.name, eventFileVtx.name, updateReq)


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

    vertexScoreMapByVertexName = vertexScoreMapByVertexNameLocal

    def (Map<String, List<EdgeRequest>> edgeReqsByVertexName, Set<EdgeRequest> edgeReqs) =
    Matcher.parseEdges(updateReq)

    Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = processUpdateReq(g, vertexScoreMapByVertexName, matchReqByVertexName, maxScoresByVertexName,
            percentageThresholdByVertexName, edgeReqsByVertexName, edgeReqs)

    finalVertexIdByVertexName.get("Event.File_Ingestion").entrySet().forEach({
      createEventNLPGroups(req, it.key, req.email)
    })


    return finalVertexIdByVertexName
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

          if ('Event.Ingestion'.equalsIgnoreCase(matchReqsForThisVertexType?.get(0)?.getVertexLabel())) {
            String bizRule = JsonSerializer.gson.toJson(matchReqByVertexName)
            g.V(vId).property('Event.Ingestion.Business_Rules', bizRule).next()
          }
        }


      }


    }

    Matcher.createEdges(g, (Set<EdgeRequest>) edgeReqs, finalVertexIdByVertexName, maxScoresByVertexName)

    return finalVertexIdByVertexName
  }

  static Vertex createEventGroupFileIngestionVtx(UpdateReq updateReq, String groupName) {

    final String vtxLabel = 'Event.File_Group_Ingestion'
    Vertex vtx = new Vertex()
    vtx.label = vtxLabel
    vtx.name = vtxLabel
    VertexProps vtxProps = new VertexProps()
    vtxProps.name = "${vtxLabel}.Ingestion_Date"
    vtxProps.mandatoryInSearch = true
    vtxProps.val = new SimpleDateFormat('yyyy-MM-dd').format(new Date())
    VertexProps vtxPropsGroupName = new VertexProps()
    vtxPropsGroupName.name = "${vtxLabel}.Group_Name"
    vtxPropsGroupName.mandatoryInSearch = true
    vtxPropsGroupName.val = groupName

    vtx.props = [vtxProps, vtxPropsGroupName]

    updateReq.vertices.push(vtx)
    return vtx
  }

  static Set<ORID> createEventNLPGroups(FileNLPRequest req, ORID emailBodyOrAttachment, String[] emailAddrs,
                                        minThreshold = 1, Integer maxThreshold = 100) {

    String[] person = req.person

    Traversal[] personOptions = new Traversal[person.length]
    for (int i = 0; i < person.length; i++) {
      personOptions[i] = __.has('Person.Natural.Full_Name', PText.textContains(person[i]))
    }

    String[] cpfs = req.cpf
    Traversal[] cpfOptions = new Traversal[cpfs.length]
    for (int i = 0; i < cpfs.length; i++) {
      cpfOptions[i] = __.has('Person.Natural.Customer_Id', P.eq(cpfs[i]))
    }

    Traversal[] emailOptions = new Traversal[emailAddrs.length]
    for (int i = 0; i < emailAddrs.length; i++) {
      emailOptions[i] = __.has('Object.Email_Address.Email', PText.textContains(emailAddrs[i]))
    }
    Traversal[] personNameCpfOptions = [personOptions, cpfOptions].flatten().toArray(new Traversal[0])

    def persons = (App.g.V().or(personNameCpfOptions))

    GraphTraversal personsClone = persons.clone() as GraphTraversal

    def emails = App.g.V().or(emailOptions)

    Set<ORID> retVal = null
    try {
      retVal = persons.hasId(P.within(emails.in('Uses_Email')?.id()?.toList()))?.id()?.toSet() as Set<ORID>

    } catch (Throwable t) {
      //ignore
    }

    if (!retVal || retVal.size() <= minThreshold) {
      retVal = personsClone.id().toSet() as Set<ORID>
    }
    String currDate = new SimpleDateFormat('yyyy-MM-dd').format(new Date())

    int count = 0
    for (ORID orid : retVal) {

      String custId = App.g.V(orid).properties('Person.Natural.Customer_Id').next().toString()
      def nlpGroupTrav =
              App.g.V().has('Event.NLP_Group.Person_Id', custId)
                      .has('Event.NLP_Group.Ingestion_Date', currDate)

      def nlpGroupVtxId
      if (nlpGroupTrav.hasNext()) {
        nlpGroupVtxId = nlpGroupTrav.id().next()
      } else {
        nlpGroupVtxId = App.g.addV('Event.NLP_Group')
                .property('Event.NLP_Group.Person_Id', custId)
                .property('Event.NLP_Group.Ingestion_Date', currDate).id().next()
      }

      App.g.addE('Has_NLP_Events').from(App.g.V(emailBodyOrAttachment)).to(App.g.V(nlpGroupVtxId))
      App.g.addE('Has_NLP_Events').from(App.g.V(nlpGroupVtxId)).to(App.g.V(orid))
      count++
      if (count >= maxThreshold) {
        break
      }
    }
    return retVal
  }

  static Vertex createEventFileIngestionVtx(FileNLPRequest req, UpdateReq updateReq) {
    String fileIngestionVtxLabel = 'Event.File_Ingestion'
    Vertex fileIngestionVtx = new Vertex()
    fileIngestionVtx.props = []

    fileIngestionVtx.label = fileIngestionVtxLabel
    fileIngestionVtx.name = req.name


    if (req.created) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}.Created"
      props.mandatoryInSearch = true
      props.val = req.created
      fileIngestionVtx.props.push(props)
    }
    if (req.fileType) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}.File_Type"
      props.mandatoryInSearch = true
      props.val = req.fileType
      fileIngestionVtx.props.push(props)
    }
    if (req.lastAccess) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}.Last_Access"
      props.mandatoryInSearch = true
      props.val = req.lastAccess
      fileIngestionVtx.props.push(props)
    }
    if (req.name) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}.Name"
      props.mandatoryInSearch = true
      props.val = req.name
      fileIngestionVtx.props.push(props)
    }
    if (req.owner) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}.Owner"
      props.mandatoryInSearch = true
      props.val = req.owner
      fileIngestionVtx.props.push(props)
    }
    if (req.path) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}.Path"
      props.mandatoryInSearch = true
      props.val = req.path
      fileIngestionVtx.props.push(props)
    }
    if (req.server) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}.Server"
      props.mandatoryInSearch = true
      props.val = req.server
      fileIngestionVtx.props.push(props)
    }
    if (req.sizeBytes) {
      VertexProps props = new VertexProps()
      props.name = "${fileIngestionVtxLabel}.SizeBytes"
      props.type = VertexProps.TypeEnum.JAVA_LANG_DOUBLE
      props.mandatoryInSearch = true
      props.val = req.sizeBytes.toString()
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
}
