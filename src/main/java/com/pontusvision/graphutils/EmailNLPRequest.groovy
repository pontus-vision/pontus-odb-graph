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

class EmailNLPRequest {
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


  String attachmentContentType
  String attachmentId

  String emailSubject
  String emailId
  String emailUserId
  String emailFolderId
  String emailCreatedDateTime
  String emailReceivedDateTime
  String emailSentDateTime
  String[] toEmailAddresses
  String[] toEmailNames
  String fromEmailAddresses
  String fromEmailNames
  String[] bccEmailAddresses
  String[] bccEmailNames
  String[] ccEmailAddresses
  String[] ccEmailNames

  static Map<String, String> getMapFromEmailNLPRequest(EmailNLPRequest req) {
    Map<String, String> retVal = [:]

    retVal.put("emailSubject", req.emailSubject.toString()?:"[]")
    retVal.put("emailId", req.emailId.toString()?:"[]")
    retVal.put("emailUserId", req.emailUserId.toString()?:"[]")
    retVal.put("emailFolderId", req.emailFolderId.toString()?:"[]")
    retVal.put("emailCreatedDateTime", req.emailCreatedDateTime?.toString()?:"[]")
    retVal.put("emailReceivedDateTime", req.emailReceivedDateTime?.toString()?:"[]")
    retVal.put("emailSentDateTime", req.emailSentDateTime?.toString()?:"[]")
    retVal.put("fromEmailAddresses", req.fromEmailAddresses?.toString()?:"[]")
    retVal.put("fromEmailNames", req.fromEmailNames?.toString()?:"[]")
    retVal.put("toEmailAddresses", req.toEmailAddresses?.toArrayString()?:"[]")
    retVal.put("toEmailNames", req.toEmailNames?.toArrayString()?:"[]")
    retVal.put("metadataController", req.metadataController?.toString()?:"[]")
    retVal.put("metadataGDPRStatus", req.metadataGDPRStatus?.toString()?:"[]")
    retVal.put("metadataLineage", req.metadataLineage?.toString()?:"[]")
    retVal.put("pg_currDate", req.pg_currDate?.toString()?:"[]")
    retVal.put("pg_content", req.pg_content?.toString()?:"[]")
    retVal.put("nlp_location", req.location?.toArrayString()?:"[]")
    retVal.put("nlp_person", req.person?.toArrayString()?:"[]")
    retVal.put("nlp_phone", req.phone?.toArrayString()?:"[]")
    retVal.put("nlp_postcode", req.postcode?.toArrayString()?:"[]")
    retVal.put("nlp_policy_number", req.policy_number?.toArrayString()?:"[]")
    retVal.put("nlp_nationality", req.nationality?.toArrayString()?:"[]")
    retVal.put("nlp_address", req.address?.toArrayString()?:"[]")
    retVal.put("nlp_cred_card", req.cred_card?.toString()?:"[]")
    retVal.put("bccEmailAddresses", req.bccEmailAddresses?.toArrayString()?:"[]")
    retVal.put("bccEmailNames", req.bccEmailNames?.toArrayString()?:"[]")
    retVal.put("categories", req.categories?.toArrayString()?:"[]")
    retVal.put("ccEmailAddresses", req.ccEmailAddresses?.toArrayString()?:"[]")
    retVal.put("ccEmailNames", req.ccEmailNames?.toArrayString()?:"[]")
    retVal.put("nlp_email", req.email?.toArrayString()?:"[]")
    retVal.put("nlp_date", req.date?.toArrayString()?:"[]")
    retVal.put("nlp_time", req.time?.toArrayString()?:"[]")
    retVal.put("nlp_money", req.money?.toArrayString()?:"[]")
    retVal.put("nlp_misc", req.misc?.toArrayString()?:"[]")
    retVal.put("nlp_language", req.language?.toArrayString()?:"[]")
    retVal.put("nlp_org", req.org?.toArrayString()?:"[]")

    return retVal
  }

  static Vertex createObjectDataSourceVtx(UpdateReq req) {
    final String vtxLabel = 'Object.Data_Source'
    Vertex vtx = new Vertex()
    vtx.label = vtxLabel
    vtx.name = vtxLabel
    VertexProps vtxProps = new VertexProps()
    vtxProps.name = "${vtxLabel}.Name"
    vtxProps.mandatoryInSearch = true
    vtxProps.val = 'Office365/email'

    vtx.props = [vtxProps]
    req.vertices.push(vtx)
    return vtx
  }

  static Map<String, Map<ORID, AtomicDouble>> upsertEmailNLPRequestArray(
          OrientStandardGraph graph,
          GraphTraversalSource g,
          EmailNLPRequest[] reqs) {
    Transaction trans = App.graph.tx()
    try {
      if (!trans.isOpen()) {
        trans.open()
      }
      for (EmailNLPRequest req: reqs){
        upsertEmailNLPRequest(graph,g,req);
      }
      trans.commit()
    } catch (Throwable t) {
      trans.rollback()
      throw t
    } finally {
      trans.close()
    }
  }


  static Map<String, Map<ORID, AtomicDouble>> upsertEmailNLPRequest(
          OrientStandardGraph graph,
          GraphTraversalSource g,
          EmailNLPRequest req) {
    Map<String, Map<ORID, AtomicDouble>> vertexScoreMapByVertexName

    UpdateReq updateReq = new UpdateReq()
    updateReq.vertices = []
    updateReq.edges = []

    Vertex dataSourceVtx = createObjectDataSourceVtx(updateReq)
    Vertex eventEmailMessageGroupVtx = createEventEmailMessageGroupVtx(updateReq)

    createEdge('Has_Ingestion', dataSourceVtx.name, eventEmailMessageGroupVtx.name, updateReq)

    Vertex eventEmailVtx = createEventEmailMessageVtx(req, updateReq)
    createEdge('Has_Ingestion', eventEmailMessageGroupVtx.name, eventEmailVtx.name, updateReq)

    createEventEmailxxxGroupVtx(updateReq, eventEmailVtx, 'Event.Email_To_Group', 'Email_To', req.toEmailAddresses)
    createEventEmailxxxGroupVtx(updateReq, eventEmailVtx, 'Event.Email_From_Group', 'Email_From', req.toEmailAddresses)
    createEventEmailxxxGroupVtx(updateReq, eventEmailVtx, 'Event.Email_CC_Group', 'Email_CC', req.toEmailAddresses)
    createEventEmailxxxGroupVtx(updateReq, eventEmailVtx, 'Event.Email_BCC_Group', 'Email_BCC', req.toEmailAddresses)

    Vertex emailBodyOrAttachmentVtx = createObjectEmailMessageBodyOrAttachmentVtx(req, updateReq)

    createEdge(req.attachmentId ? 'Email_Attachment' : 'Email_Body', eventEmailVtx.name, emailBodyOrAttachmentVtx.name, updateReq)


    StringBuffer sb = new StringBuffer()
    Double pcntThreshold = 1.0
    Map<String, String> item = getMapFromEmailNLPRequest(req)
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

    processUpdateReq(g, vertexScoreMapByVertexName, matchReqByVertexName, maxScoresByVertexName,
            percentageThresholdByVertexName, edgeReqsByVertexName, edgeReqs)

    vertexScoreMapByVertexNameLocal.get(getObjectEmailBodyOrAttachmentVtxLabel(req)).entrySet().forEach({
      createEventNLPGroups(req, it.key)
    })


    return vertexScoreMapByVertexName
  }

  static void processUpdateReq(
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
        ORID vId = Matcher.addNewVertexFromMatchReqs(g, (String) vertexTypeStr, matchReqsForThisVertexType)
        newVertices.put(vId, new AtomicDouble(maxScore))
        finalVertexIdByVertexName.put((String) vertexTypeStr, newVertices)

        if ('Event.Ingestion'.equalsIgnoreCase(matchReqsForThisVertexType?.get(0)?.getVertexLabel())) {
          String bizRule = JsonSerializer.gson.toJson(matchReqByVertexName)
          g.V(vId).property('Event.Ingestion.Business_Rules', bizRule).next()
        }

      }


    }

    Matcher.createEdges(g, (Set<EdgeRequest>) edgeReqs, finalVertexIdByVertexName, maxScoresByVertexName)

  }

  static Vertex createEventEmailMessageGroupVtx(UpdateReq updateReq) {

    final String vtxLabel = 'Event.Email_Msg_Group'
    Vertex vtx = new Vertex()
    vtx.label = vtxLabel
    vtx.name = vtxLabel
    VertexProps vtxProps = new VertexProps()
    vtxProps.name = "${vtxLabel}.Ingestion_Date"
    vtxProps.mandatoryInSearch = true
    vtxProps.val = new SimpleDateFormat('yyyy-MM-dd').format(new Date())

    vtx.props = [vtxProps]

    updateReq.vertices.push(vtx)
    return vtx
  }

  static Vertex createEmailGroupVtx(String vtxLabel, String vtxName, String email, UpdateReq updateReq) {
    Vertex emailGroupVtx = new Vertex()
    emailGroupVtx.label = vtxLabel
    emailGroupVtx.name = vtxName
    VertexProps emailGroupIngestionDateVtxProps = new VertexProps()
    emailGroupIngestionDateVtxProps.name = "${vtxLabel}.Ingestion_Date"
    emailGroupIngestionDateVtxProps.mandatoryInSearch = true
    emailGroupIngestionDateVtxProps.val = new SimpleDateFormat('yyyy-MM-dd').format(new Date())

    VertexProps emailVtxProps = new VertexProps()
    emailVtxProps.name = "${vtxLabel}.Email"
    emailVtxProps.mandatoryInSearch = true
    emailVtxProps.val = email

    emailGroupVtx.props = [emailGroupIngestionDateVtxProps, emailVtxProps]

    updateReq.vertices.push(emailGroupVtx)
    return emailGroupVtx

  }

  static Vertex createEmailAddressVertex(String email, String vtxName, UpdateReq req) {
    String emailVtxLabel = 'Object.Email_Address'
    Vertex emailVtx = new Vertex()
    emailVtx.label = emailVtxLabel
    emailVtx.name = vtxName
    VertexProps emailAddrVtxProps = new VertexProps()
    emailAddrVtxProps.name = "${emailVtxLabel}.Email"
    emailAddrVtxProps.mandatoryInSearch = true
    emailAddrVtxProps.val = email

    emailVtx.props = [emailAddrVtxProps]

    req.vertices.push(emailVtx)

    return emailVtx

  }

  static List<ORID> createEventNLPGroups(EmailNLPRequest req, ORID emailBodyOrAttachment,
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


    String[] emailAddrs = [req.email, req.toEmailAddresses,
                           req.fromEmailAddresses, req.ccEmailAddresses, req.bccEmailAddresses].flatten()
    Traversal[] emailOptions = new Traversal[emailAddrs.length]
    for (int i = 0; i < emailAddrs.length; i++) {
      emailOptions[i] = __.has('Object.Email_Address.Email', PText.textContains(emailAddrs[i]))
    }
    Traversal[] personNameCpfOptions = [personOptions, cpfOptions].flatten().toArray(new Traversal[0])

    def persons = (App.g.V().or(personNameCpfOptions))

    GraphTraversal personsClone = persons.clone() as GraphTraversal

    def emails = App.g.V().or(emailOptions)

    def retVal = persons.hasId(P.within(emails.in('Uses_Email').id().toList())).id().toSet() as Set<ORID>

    if (retVal.size() <= minThreshold) {
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

  static Vertex createEventEmailMessageVtx(EmailNLPRequest req, UpdateReq updateReq) {
    String emailVtxLabel = 'Event.Email_Message'
    Vertex emailVtx = new Vertex()
    emailVtx.props = []

    emailVtx.label = emailVtxLabel
    emailVtx.name = req.emailId
    VertexProps emailIdVtxProps = new VertexProps()
    emailIdVtxProps.name = "${emailVtxLabel}.Email_Id"
    emailIdVtxProps.mandatoryInSearch = true
    emailIdVtxProps.val = req.emailId

    emailVtx.props.push(emailIdVtxProps)

    if (req.emailCreatedDateTime) {
      VertexProps props = new VertexProps()
      props.name = "${emailVtxLabel}.Created_Date_Time"
      props.mandatoryInSearch = true
      props.val = req.emailCreatedDateTime
      emailVtx.props.push(props)
    }
    if (req.emailSentDateTime) {
      VertexProps props = new VertexProps()
      props.name = "${emailVtxLabel}.Sent_Date_Time"
      props.mandatoryInSearch = true
      props.val = req.emailSentDateTime
      emailVtx.props.push(props)
    }
    if (req.emailReceivedDateTime) {
      VertexProps props = new VertexProps()
      props.name = "${emailVtxLabel}.Received_Date_Time"
      props.mandatoryInSearch = true
      props.val = req.emailReceivedDateTime
      emailVtx.props.push(props)
    }
    if (req.categories) {
      VertexProps props = new VertexProps()
      props.name = "${emailVtxLabel}.Categories"
      props.mandatoryInSearch = false
      props.val = req.categories.toArrayString()
      emailVtx.props.push(props)
    }
    if (req.emailSubject) {
      VertexProps props = new VertexProps()
      props.name = "${emailVtxLabel}.Subject"
      props.mandatoryInSearch = false
      props.val = req.emailSubject
      emailVtx.props.push(props)
    }
    if (req.emailFolderId) {
      VertexProps props = new VertexProps()
      props.name = "${emailVtxLabel}.Folder_Id"
      props.mandatoryInSearch = false
      props.val = req.emailFolderId
      emailVtx.props.push(props)
    }
    if (req.emailUserId) {
      VertexProps props = new VertexProps()
      props.name = "${emailVtxLabel}.User_Id"
      props.mandatoryInSearch = false
      props.val = req.emailUserId
      emailVtx.props.push(props)
    }

    updateReq.vertices.push(emailVtx)
    return emailVtx

  }


  static Vertex createEmailNLPVertexProp(String name, String[] val, Vertex vtx) {
    if (!val) {
      return vtx
    }
    VertexProps props = new VertexProps()
    props.name = name
//    props.mandatoryInSearch = true;
    props.val = val.toArrayString()
//    props.type = VertexProps.TypeEnum._LJAVA_LANG_STRING_;
//    props.excludeFromUpdate = true;
//    props.postProcessor=postProcessor;
//      props.
    vtx.props.push(props)

    return vtx
  }

  static String getObjectEmailBodyOrAttachmentVtxLabel(EmailNLPRequest req) {
    return req.attachmentId ? 'Object.Email_Message_Attachment' : 'Object.Email_Message_Body'
  }

  static Vertex createObjectEmailMessageBodyOrAttachmentVtx(EmailNLPRequest req, UpdateReq updateReq) {
    String emailVtxLabel = getObjectEmailBodyOrAttachmentVtxLabel(req)
    Vertex emailVtx = new Vertex()
    emailVtx.props = []

    emailVtx.label = emailVtxLabel
    emailVtx.name = req.attachmentId ?: req.emailId
    VertexProps emailIdVtxProps = new VertexProps()
    emailIdVtxProps.name = "${emailVtxLabel}.Email_Id"
    emailIdVtxProps.mandatoryInSearch = true
    emailIdVtxProps.val = req.emailId

    emailVtx.props.push(emailIdVtxProps)

    if (req.attachmentId) {
      VertexProps attachmentIdVtxProp = new VertexProps()
      attachmentIdVtxProp.name = "${emailVtxLabel}.Attachment_Id"
      attachmentIdVtxProp.mandatoryInSearch = true
      attachmentIdVtxProp.val = req.attachmentId
    }
    if (req.attachmentContentType) {
      VertexProps attachmentIdVtxProp = new VertexProps()
      attachmentIdVtxProp.name = "${emailVtxLabel}.Attachment_Content_Type"
      attachmentIdVtxProp.mandatoryInSearch = true
      attachmentIdVtxProp.val = req.attachmentContentType
    }


    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Address", req.address, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Cred_card", req.cred_card, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Email", req.email, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Location", req.location, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Person", req.person, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Phone", req.phone, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Postcode", req.postcode, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Policy_number", req.policy_number, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Org", req.org, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_CPF", req.cpf, emailVtx)
    createEmailNLPVertexProp("${emailVtxLabel}.NLP_Money", req.money, emailVtx)

    updateReq.vertices.push(emailVtx)
    return emailVtx

  }

  static Edge createEdge(String edgeLabel, String fromVtxName, String toVtxName, UpdateReq req) {
    Edge edge = new Edge()
    edge.label(edgeLabel)
    edge.fromVertexName(fromVtxName)
    edge.toVertexName(toVtxName)

    req.edges.push(edge)

    return edge
  }

  static UpdateReq createEventEmailxxxGroupVtx(
          UpdateReq updateReq,
          Vertex emailEventVtx,
          String vertexLabel,
          String edgeLabel,
          String[] emails) {
    for (String email : emails) {
      Vertex emailGroupVtx = createEmailGroupVtx(vertexLabel, "${email}_${vertexLabel}", email, updateReq)
      Vertex emailVtx = createEmailAddressVertex(email, email, updateReq)

      createEdge(edgeLabel, emailGroupVtx.name, emailVtx.name, updateReq)
      createEdge(edgeLabel, emailEventVtx.name, emailGroupVtx.name, updateReq)
    }


    return updateReq
  }


}
