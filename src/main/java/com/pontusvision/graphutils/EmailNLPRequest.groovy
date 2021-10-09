package com.pontusvision.graphutils

import com.google.common.util.concurrent.AtomicDouble
import com.orientechnologies.orient.core.id.ORID
import com.pontusvision.gdpr.App
import com.pontusvision.gdpr.mapping.UpdateReq
import com.pontusvision.gdpr.mapping.Vertex
import com.pontusvision.gdpr.mapping.VertexProps
import org.apache.tinkerpop.gremlin.orientdb.OrientStandardGraph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Transaction

import java.text.SimpleDateFormat

class EmailNLPRequest extends FileNLPRequest implements Serializable {


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

    retVal.put("emailSubject", req.emailSubject.toString() ?: "[]")
    retVal.put("emailId", req.emailId.toString() ?: "[]")
    retVal.put("emailUserId", req.emailUserId.toString() ?: "[]")
    retVal.put("emailFolderId", req.emailFolderId.toString() ?: "[]")
    retVal.put("emailCreatedDateTime", req.emailCreatedDateTime?.toString() ?: "[]")
    retVal.put("emailReceivedDateTime", req.emailReceivedDateTime?.toString() ?: "[]")
    retVal.put("emailSentDateTime", req.emailSentDateTime?.toString() ?: "[]")
    retVal.put("fromEmailAddresses", req.fromEmailAddresses?.toString() ?: "[]")
    retVal.put("fromEmailNames", req.fromEmailNames?.toString() ?: "[]")
    retVal.put("toEmailAddresses", req.toEmailAddresses?.toArrayString() ?: "[]")
    retVal.put("toEmailNames", req.toEmailNames?.toArrayString() ?: "[]")
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
    retVal.put("bccEmailAddresses", req.bccEmailAddresses?.toArrayString() ?: "[]")
    retVal.put("bccEmailNames", req.bccEmailNames?.toArrayString() ?: "[]")
    retVal.put("categories", req.categories?.toArrayString() ?: "[]")
    retVal.put("ccEmailAddresses", req.ccEmailAddresses?.toArrayString() ?: "[]")
    retVal.put("ccEmailNames", req.ccEmailNames?.toArrayString() ?: "[]")
    retVal.put("nlp_email", req.email?.toArrayString() ?: "[]")
    retVal.put("nlp_date", req.date?.toArrayString() ?: "[]")
    retVal.put("nlp_time", req.time?.toArrayString() ?: "[]")
    retVal.put("nlp_money", req.money?.toArrayString() ?: "[]")
    retVal.put("nlp_misc", req.misc?.toArrayString() ?: "[]")
    retVal.put("nlp_language", req.language?.toArrayString() ?: "[]")
    retVal.put("nlp_org", req.org?.toArrayString() ?: "[]")

    return retVal
  }

  static void upsertEmailNLPRequestArray(
          OrientStandardGraph graph,
          GraphTraversalSource g,
          EmailNLPRequest[] reqs) {
    Transaction trans = App.graph.tx()
    try {
      if (!trans.isOpen()) {
        trans.open()
      }
      for (EmailNLPRequest req : reqs) {
        upsertEmailNLPRequest(graph, g, req)
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
    String dataSourceName = "Office365/email"

    Vertex dataSourceVtx = createObjectDataSourceVtx(updateReq, dataSourceName)
    Vertex eventEmailMessageGroupVtx = createEventGroupIngestionVtx(updateReq, dataSourceName,"Event.Email_Msg_Group")

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

    Map<String, Map<ORID, AtomicDouble>> finalVertexIdByVertexName = processUpdateReq(g, vertexScoreMapByVertexName, matchReqByVertexName, maxScoresByVertexName,
            percentageThresholdByVertexName, edgeReqsByVertexName, edgeReqs)
    String[] emailAddrs = [req.email, req.toEmailAddresses,
                           req.fromEmailAddresses, req.ccEmailAddresses, req.bccEmailAddresses].flatten()
    finalVertexIdByVertexName.get(getObjectEmailBodyOrAttachmentVtxLabel(req)).entrySet().forEach({
      createEventNLPGroups(req, it.key, emailAddrs)
    })


    return finalVertexIdByVertexName
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
    emailVtx.name = emailVtxLabel //req.attachmentId ?: req.emailId
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
      emailVtx.props.push(attachmentIdVtxProp)

    }
    if (req.attachmentContentType) {
      VertexProps attachmentIdVtxProp = new VertexProps()
      attachmentIdVtxProp.name = "${emailVtxLabel}.Attachment_Content_Type"
      attachmentIdVtxProp.mandatoryInSearch = true
      attachmentIdVtxProp.val = req.attachmentContentType
      emailVtx.props.push(attachmentIdVtxProp)

    }
    if (req.sizeBytes) {
      VertexProps attachmentIdVtxProp = new VertexProps()
      attachmentIdVtxProp.name = "${emailVtxLabel}.Size_Bytes"
      attachmentIdVtxProp.mandatoryInSearch = true
      attachmentIdVtxProp.val = req.sizeBytes.toString()
      emailVtx.props.push(attachmentIdVtxProp)

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



  String getAttachmentContentType() {
    return attachmentContentType
  }

  void setAttachmentContentType(String attachmentContentType) {
    this.attachmentContentType = attachmentContentType
  }

  String getAttachmentId() {
    return attachmentId
  }

  void setAttachmentId(String attachmentId) {
    this.attachmentId = attachmentId
  }


  String getEmailSubject() {
    return emailSubject
  }

  void setEmailSubject(String emailSubject) {
    this.emailSubject = emailSubject
  }

  String getEmailId() {
    return emailId
  }

  void setEmailId(String emailId) {
    this.emailId = emailId
  }

  String getEmailUserId() {
    return emailUserId
  }

  void setEmailUserId(String emailUserId) {
    this.emailUserId = emailUserId
  }

  String getEmailFolderId() {
    return emailFolderId
  }

  void setEmailFolderId(String emailFolderId) {
    this.emailFolderId = emailFolderId
  }

  String getEmailCreatedDateTime() {
    return emailCreatedDateTime
  }

  void setEmailCreatedDateTime(String emailCreatedDateTime) {
    this.emailCreatedDateTime = emailCreatedDateTime
  }

  String getEmailReceivedDateTime() {
    return emailReceivedDateTime
  }

  void setEmailReceivedDateTime(String emailReceivedDateTime) {
    this.emailReceivedDateTime = emailReceivedDateTime
  }

  String getEmailSentDateTime() {
    return emailSentDateTime
  }

  void setEmailSentDateTime(String emailSentDateTime) {
    this.emailSentDateTime = emailSentDateTime
  }

  String[] getToEmailAddresses() {
    return toEmailAddresses
  }

  void setToEmailAddresses(String[] toEmailAddresses) {
    this.toEmailAddresses = toEmailAddresses
  }

  String[] getToEmailNames() {
    return toEmailNames
  }

  void setToEmailNames(String[] toEmailNames) {
    this.toEmailNames = toEmailNames
  }

  String getFromEmailAddresses() {
    return fromEmailAddresses
  }

  void setFromEmailAddresses(String fromEmailAddresses) {
    this.fromEmailAddresses = fromEmailAddresses
  }

  String getFromEmailNames() {
    return fromEmailNames
  }

  void setFromEmailNames(String fromEmailNames) {
    this.fromEmailNames = fromEmailNames
  }

  String[] getBccEmailAddresses() {
    return bccEmailAddresses
  }

  void setBccEmailAddresses(String[] bccEmailAddresses) {
    this.bccEmailAddresses = bccEmailAddresses
  }

  String[] getBccEmailNames() {
    return bccEmailNames
  }

  void setBccEmailNames(String[] bccEmailNames) {
    this.bccEmailNames = bccEmailNames
  }

  String[] getCcEmailAddresses() {
    return ccEmailAddresses
  }

  void setCcEmailAddresses(String[] ccEmailAddresses) {
    this.ccEmailAddresses = ccEmailAddresses
  }

  String[] getCcEmailNames() {
    return ccEmailNames
  }

  void setCcEmailNames(String[] ccEmailNames) {
    this.ccEmailNames = ccEmailNames
  }

}
