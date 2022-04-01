package com.pontusvision.graphutils

import com.orientechnologies.orient.core.index.OIndex
import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OProperty
import com.pontusvision.gdpr.App
import groovy.json.JsonSlurper
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

//
//// describeSchema()
//// g.V().has('Metadata_Type_Object_Credential','Object_Credential')
//g.V()
//
//def jsonData = '{"request":{"data":{"Description":"This is a privacy impact assessment for app xyz","Start_Date":"12/01/2018","Delivery_Date":"11/03/2019","Risk_To_Individuals":"Med","Risk_Of_Reputational_Damage":"High","Intrusion_On_Privacy":"High","Risk_To_Corporation":"High","Compliance_Check_Passed":true,"submit":true},"owner":"5b0a74637a574e0832269bcb","access":[],"form":"5b1d3503e3acf26950581de3"},"submission":{"owner":"5b0a74637a574e0832269bcb","deleted":null,"roles":[],"_id":"5b713ef31e97e512e943d3a8","data":{"Description":"This is a privacy impact assessment for app xyz","Start_Date":"12/01/2018","Delivery_Date":"11/03/2019","Risk_To_Individuals":"Med","Risk_Of_Reputational_Damage":"High","Intrusion_On_Privacy":"High","Risk_To_Corporation":"High","Compliance_Check_Passed":true,"submit":true},"access":[],"form":"5b1d3503e3acf26950581de3","externalIds":[],"created":"2018-08-13T08:18:59.374Z","modified":"2018-08-13T08:18:59.375Z","__v":0},"params":{"formId":"5b1d3503e3acf26950581de3"}}'
//
//
////def dataFromFormInJSON='{"request":{"data":{"Description":"test1","Start_Date":"10/02/2018","Delivery_Date":"09/03/2019","Risk_To_Individuals":"Low","Risk_Of_Reputational_Damage":"Med","Intrusion_On_Privacy":"Med","Risk_To_Corporation":"Low","Compliance_Check_Passed":true,"submit":true},"owner":"5b0a74637a574e0832269bcb","access":[],"form":"5b1d3503e3acf26950581de3"},"submission":{"owner":"5b0a74637a574e0832269bcb","deleted":null,"roles":[],"_id":"5b704c81c447a028f1a7009f","data":{"Description":"test1","Start_Date":"10/02/2018","Delivery_Date":"09/03/2019","Risk_To_Individuals":"Low","Risk_Of_Reputational_Damage":"Med","Intrusion_On_Privacy":"Med","Risk_To_Corporation":"Low","Compliance_Check_Passed":true,"submit":true},"access":[],"form":"5b1d3503e3acf26950581de3","externalIds":[],"created":"2018-08-12T15:04:33.062Z","modified":"2018-08-12T15:04:33.064Z","__v":0},"params":{"formId":"5b1d3503e3acf26950581de3"}}';
////
////
////
////// def jsonStr = '{"reqs":[{"attribVal":"Leo","attribType":"String","propName":"Person_Natural_Full_Name","vertexName":"Person_Natural","predicateStr":"textPrefix"},{"attribVal":"Mardy","attribType":"String","propName":"Person_Natural_Full_Name","vertexName":"Person_Natural","predicateStr":"textFuzzy"},{"attribVal":"Martins","attribType":"String","propName":"Person_Natural_Last_Name","vertexName":"Person_Natural","predicateStr":"textFuzzy"},{"attribVal":"Zukker","attribType":"String","propName":"Person_Natural_Last_Name","vertexName":"Person_Natural","predicateStr":"textFuzzy"},{"attribVal":"Silva","attribType":"String","propName":"Person_Natural_Last_Name","vertexName":"Person_Natural","predicateStr":"textFuzzy"},{"attribVal":"SW1W 9QL","attribType":"String","propName":"Location_Address_Post_Code","vertexName":"Location_Address","predicateStr":"eq"},{"attribVal":"E14 4BB","attribType":"String","propName":"Location_Address_Post_Code","vertexName":"Location_Address","predicateStr":"eq"},{"attribVal":"SW1W 3LL","attribType":"String","propName":"Location_Address_Post_Code","vertexName":"Location_Address","predicateStr":"eq"}]}'
////// def jsonStr = '{"reqs":[{"attribVal":"Leo","attribType":"String","propName":"Person_Natural_Full_Name","vertexName":"Person_Natural","predicateStr":"textPrefix"}]}'
////
////
////
//StringBuffer sb = new StringBuffer()
////
////// matchPerson(jsonStr,sb,  g)
////// sb.toString()
////// g.V()
////
////// g.V().has('Person_Natural_Full_Name',org.janusgraph.core.attribute.Text.textPrefix('Leo')).id()
////
//updateFormData(g,jsonData,"Object_Privacy_Impact_Assessment",sb)
////
//sb.toString()
// describeSchema()

enum IngestionOperation {

  CREATE,
  UPDATE,
  DELETE
}


class FormData {


  static getOrCreateOwnerVid(GraphTraversalSource gtran, String submissionOwner, StringBuffer sb = new StringBuffer()) {

    sb.append("\nin getOrCreateOwnerVid(); before cloning gtran")

    def localGtrav = gtran.clone()
    def localGtrav2 = gtran.clone()

    sb.append("\nin getOrCreateOwnerVid(); before checking for current entries")

    def credential = localGtrav.V().has("Object_Credential_User_Id", submissionOwner)
    sb.append("\nin getOrCreateOwnerVid(); after  checking for current entries")

    def credentialVid = "" as Object
    if (credential.hasNext()) {
      sb.append("\nin getOrCreateOwnerVid(); Found one old entry")

      credentialVid = credential.next().id();
    } else {
      sb.append("\nin getOrCreateOwnerVid(); creating a new entry")

      credentialVid = localGtrav2.addV("Object_Credential")
      // property("Metadata_Controller", pg_metadataController).
      // property("Metadata_Processor", pg_metadataProcessor).
      // property("Metadata_Lineage", pg_metadataLineage).
      // property("Metadata_Redaction", pg_metadataRedaction).
      // property("Metadata_Version", pg_metadataVersion).
      // property("Metadata_Create_Date", metadataCreateDate).
      // property("Metadata_Update_Date", metadataUpdateDate).
      // property("Metadata_Status", pg_metadataStatus).
      // property("Metadata_GDPR_Status", pg_metadataGDPRStatus).
      // property("Metadata_Lineage_Server_Tag", pg_metadataLineageServerTag).
      // property("Metadata_Lineage_Location_Tag", pg_metadataLineageLocationTag).
        .property("Metadata_Type", "Object_Credential")
        .property("Metadata_Type_Object_Credential", "Object_Credential")
        .property("Object_Credential_User_Id", submissionOwner)
        .next().id()
      // property("Object_Credential_login.sha256", pg_login_sha256).next().id()
      sb.append("\nin getOrCreateOwnerVid(); after creating a new entry; id = ")
        .append(credentialVid)

    }

    return credentialVid;

  }

  static Map<String, String> prepareFormData(String dataFromFormInJSON, String dataType, OClass vertexClass,StringBuffer sb = new StringBuffer()) {
    def slurper = new JsonSlurper()
    def formDataParsed = slurper.parseText(dataFromFormInJSON)
    def formOwnerId = formDataParsed.request.owner as String
    def formId = formDataParsed.request.form as String

    def submissionId = formDataParsed.submission._id as String
    def submissionOwner = formDataParsed.submission.owner as String

    def data = formDataParsed.request.data


    def Key_Form_Owner_Id = "${dataType}.Form_Owner_Id" as String
    def Key_Form_Id = "${dataType}.Form_Id" as String
    def Key_Form_Submission_Id = "${dataType}.Form_Submission_Id" as String
    def Key_Form_Submission_Owner_Id = "${dataType}.Form_Submission_Owner_Id" as String
    def Key_Metadata_Type = "Metadata_Type_${dataType}" as String


    Map<String, String> classFields = new HashMap<>()

    classFields.put(Key_Form_Owner_Id, formOwnerId)
    classFields.put(Key_Form_Id, formId)
    classFields.put(Key_Form_Submission_Id, submissionId)
    classFields.put(Key_Form_Submission_Owner_Id, submissionOwner)
    classFields.put(Key_Metadata_Type, dataType)
    classFields.put("Metadata_Type", dataType)
    classFields.putAll(data as Map)


    classFields.each { k, v ->
      if (k != 'submit') {
        def key = ((k.startsWith(dataType) || k.startsWith("Metadata_Type")) ?
          k :
          "$dataType.$k") as String

        createPropsIfNotThere(vertexClass, key, String.class, sb)
      }
    }
    Set<OIndex> indices = vertexClass.getIndexes();

    if (indices.size() == 0) {
      String[] fields = classFields.values().toArray(new String[0]);
      vertexClass.createIndex("${dataType}.MixedIdx" as String,
        OClass.INDEX_TYPE.FULLTEXT.toString(), fields);
      System.out.println("Added  fields ${fields} to ${dataType}.MixedIdx ");
    }

    return classFields;

  }

  static String updateFormData(GraphTraversalSource gtrav, Map<String, String> fields, String dataType, OClass vertexClass, StringBuffer sb = new StringBuffer()) {


    String Key_Form_Submission_Id = "${dataType}.Form_Submission_Id" as String
    String submissionId = fields.get(Key_Form_Submission_Id);

    def localGtrav = gtrav.V()
      .has(Key_Form_Submission_Id, (String) submissionId)

    localGtrav = localGtrav.hasNext() ? localGtrav : gtrav.addV(dataType);

    fields.each { k, v ->
      if (k != 'submit') {


        def key = ((k.startsWith(dataType) || k.startsWith("Metadata_Type")) ?
          k :
          "$dataType.$k") as String

        def val = "$v" as String
        sb.append("\nadding $key with val = $v =>").append(v.getClass().toString())

        try {
          OProperty prop = vertexClass.getProperty(key);
          if (prop) {

            localGtrav = localGtrav.property(key, PVConvMixin.asType(val, prop.getType().getDefaultJavaType(), sb))

          }

        } catch (Throwable t) {
          if (key.toLowerCase().indexOf("date") > 0) {
            try {
              localGtrav = localGtrav.property(key, val as Date)

            }
            catch (Throwable t2) {
              sb.append("Error adding prop $k with val ($v): \n$t")
            }
          } else {
            sb.append("Error adding prop $k with val ($v): \n$t")
          }
        }
      }
    }

    def retVal = localGtrav.next().id()


    return retVal;


  }


  static String createIngestionEventId(GraphTraversalSource gtrans, String eventGUID, String eventType, IngestionOperation operation, String origDataClearNonB64, String submissionOwner, Object formDataId = null, StringBuffer sb = new StringBuffer()) {
    Date now = new Date(System.currentTimeMillis())

    sb.append("\nIn createIngestionEventId; adding Event Ingestion")

    def localgTrav = gtrans.clone()
    def localgTrav2 = gtrans.clone()

    def ingestionEvent = gtrans.addV("Event_Form_Ingestion")

    sb.append("\nIn createIngestionEventId; added Event Ingestion; before setting props; ")

    ingestionEvent.property("Event_Form_Ingestion_Metadata_Create_Date", now)
      .property("Metadata_Type_Event_Form_Ingestion", "Event_Form_Ingestion")
      .property("Metadata_Type", "Event_Form_Ingestion")
      .property("Event_Form_Ingestion_Metadata_GUID", eventGUID)
      .property("Event_Form_Ingestion_Type", eventType)
      .property("Event_Form_Ingestion_Operation", operation.toString())
      .property("Event_Form_Ingestion_Domain_b64", origDataClearNonB64.bytes.encodeBase64().toString())
    // .iterate()

    sb.append("\nIn createIngestionEventId; added Event Ingestion; before getting id; ")


    def ingestionEventId = ingestionEvent.next().id()

    sb.append("\nIn createIngestionEventId; added Event Ingestion; Id = ")
      .append(ingestionEventId)


    if (submissionOwner != null) {

      sb.append("\nIn createIngestionEventId; about to getOrCreateOwnerVid")

      def ownerVid = getOrCreateOwnerVid(localgTrav, submissionOwner, sb)
      sb.append("\nIn createIngestionEventId; after getOrCreateOwnerVid = ")
        .append(ownerVid)
      def fromV = gtrans.V(ownerVid).next()
      def toV = gtrans.V(ingestionEventId).next()

      localgTrav2.addE('Has_Form_Ingestion_Event').from(fromV).to(toV).next()
      sb.append("\nIn createIngestionEventId; after creating edge Has_Form_Ingestion_Event between ownerVid ")
        .append(ownerVid).append(" and ingestionEventId ").append(ingestionEventId)


    }
    if (formDataId != null) {

      localgTrav.addE('Has_Form_Ingestion_Event').from(gtrans.V(ingestionEventId)).to(gtrans.V(formDataId)).next()
      sb.append("\nIn createIngestionEventId; after creating edge Has_Form_Ingestion_Event between ingestionEventId ")
        .append(ingestionEventId).append(" and formDataId ").append(formDataId)

    }

    return ingestionEventId as Object

  }


  static String upsertFormData(
    GraphTraversalSource gtrav, String dataFromFormInJSON, String dataType, String otherDataType,
    String otherDataTypeSubmissionId, String relationshipName, StringBuffer sb = new StringBuffer())
  {
    sb.append("\nAbout to upsert $dataType; data= $dataFromFormInJSON")

    ODBSchemaManager.createEdgeLabel(App.graph, relationshipName)

    OClass vertexClass = ODBSchemaManager.createVertexLabel(App.graph, dataType);

    Map<String, String> fields = prepareFormData(dataFromFormInJSON, dataType, vertexClass,sb);

    String submissionId = fields.get("${dataType}.Form_Submission_Id")
    String submissionOwner = fields.get("${dataType}.Form_Submission_Owner_Id")

    def trans = App.graph.tx()
    def retVal = "";

    try
    {
      if (!trans.isOpen())
      {
        trans.open();
      }
      retVal = updateFormData(gtrav, fields, dataType, vertexClass, sb)

      createIngestionEventId(gtrav, submissionId, dataType, IngestionOperation.UPDATE,
        dataFromFormInJSON, submissionOwner, retVal, sb)


      if (otherDataType && otherDataTypeSubmissionId)
      {
        def otherDataTypeSubmissionIdKey = "${otherDataType}.Form_Submission_Id" as String
        def otherTypeId = gtrav.V().has(otherDataTypeSubmissionIdKey, otherDataTypeSubmissionId).next().id()

        if (otherTypeId)
        {
          def foundIds = gtrav.V(otherTypeId)
            .both()
            .hasId(retVal).id().toList() as List

          if (foundIds.isEmpty())
          {
            gtrav.addE(relationshipName).from(gtrav.V(retVal)).to(gtrav.V(otherTypeId)).next()
            sb.append("\nAfter creating new relationship $relationshipName")
          }
        }
      }
      trans.commit();


    }
    catch (Throwable t)
    {
      sb.append("\n$t")
      trans.rollback();
    }
    finally
    {
      trans.close()
    }

    sb.append(" \n  $retVal ")
    return retVal;
  }


  static deleteFormData(def gtrav, String dataFromFormInJSON, String dataType, StringBuffer sb = new StringBuffer()) {

    def slurper = new JsonSlurper()
    def formDataParsed = slurper.parseText(dataFromFormInJSON)


    def submissionId = formDataParsed.params.submissionId as String


    def trans = App.graph.tx()


    try {

      if (!trans.isOpen()) {
        trans.open();
      }
      def Key_Form_Submission_Id = "${dataType}.Form_Submission_Id" as String
      def Key_Form_Submission_Owner_Id = "${dataType}.Form_Submission_Owner_Id" as String

      def v = gtrav.V()
        .has(Key_Form_Submission_Id, (String) submissionId)
        .next();

      def submissionOwnerId = gtrav.V(v).property(Key_Form_Submission_Owner_Id).next() as String

      gtrav.V(v).bothE().drop().iterate()
      gtrav.V(v).drop().iterate()


      sb.append("DELETED form \n")


      createIngestionEventId(gtrav, submissionId, dataType, IngestionOperation.DELETE,
        dataFromFormInJSON, submissionOwnerId, null, sb)


      trans.commit();
      sb.append("\nAFTER COMMIT")

      // def newEntry = gtrav.next()
    } catch (Throwable t) {
      sb.append("in deleteFormData()\n$t")
      if (trans.isOpen()) {
        trans.rollback();
      }
    }
    finally {
      if (trans.isOpen()) {
        trans.close()
      }
    }

  }

  static createPropsIfNotThere(OClass vertexClass, String key, Class<?> classType = String.class, StringBuffer sb = new StringBuffer()) {
    def prop = ODBSchemaManager.createProp(vertexClass, key, classType);

    return key

  }

}

def upsertFormData(
  String dataFromFormInJSON, String dataType, String otherDataType,
  String otherDataTypeSubmissionId,
  String relationshipName, StringBuffer sb = new StringBuffer()) {
  return FormData.upsertFormData(App.g,
    dataFromFormInJSON,
    dataType,
    otherDataType,
    otherDataTypeSubmissionId,
    relationshipName,
    sb);

}
