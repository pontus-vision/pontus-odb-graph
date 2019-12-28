import groovy.json.JsonSlurper


//
//// describeSchema()
//// g.V().has('Metadata.Type.Object.Credential','Object.Credential')
//g.V()
//
//def jsonData = '{"request":{"data":{"Description":"This is a privacy impact assessment for app xyz","Start_Date":"12/01/2018","Delivery_Date":"11/03/2019","Risk_To_Individuals":"Med","Risk_Of_Reputational_Damage":"High","Intrusion_On_Privacy":"High","Risk_To_Corporation":"High","Compliance_Check_Passed":true,"submit":true},"owner":"5b0a74637a574e0832269bcb","access":[],"form":"5b1d3503e3acf26950581de3"},"submission":{"owner":"5b0a74637a574e0832269bcb","deleted":null,"roles":[],"_id":"5b713ef31e97e512e943d3a8","data":{"Description":"This is a privacy impact assessment for app xyz","Start_Date":"12/01/2018","Delivery_Date":"11/03/2019","Risk_To_Individuals":"Med","Risk_Of_Reputational_Damage":"High","Intrusion_On_Privacy":"High","Risk_To_Corporation":"High","Compliance_Check_Passed":true,"submit":true},"access":[],"form":"5b1d3503e3acf26950581de3","externalIds":[],"created":"2018-08-13T08:18:59.374Z","modified":"2018-08-13T08:18:59.375Z","__v":0},"params":{"formId":"5b1d3503e3acf26950581de3"}}'
//
//
////def dataFromFormInJSON='{"request":{"data":{"Description":"test1","Start_Date":"10/02/2018","Delivery_Date":"09/03/2019","Risk_To_Individuals":"Low","Risk_Of_Reputational_Damage":"Med","Intrusion_On_Privacy":"Med","Risk_To_Corporation":"Low","Compliance_Check_Passed":true,"submit":true},"owner":"5b0a74637a574e0832269bcb","access":[],"form":"5b1d3503e3acf26950581de3"},"submission":{"owner":"5b0a74637a574e0832269bcb","deleted":null,"roles":[],"_id":"5b704c81c447a028f1a7009f","data":{"Description":"test1","Start_Date":"10/02/2018","Delivery_Date":"09/03/2019","Risk_To_Individuals":"Low","Risk_Of_Reputational_Damage":"Med","Intrusion_On_Privacy":"Med","Risk_To_Corporation":"Low","Compliance_Check_Passed":true,"submit":true},"access":[],"form":"5b1d3503e3acf26950581de3","externalIds":[],"created":"2018-08-12T15:04:33.062Z","modified":"2018-08-12T15:04:33.064Z","__v":0},"params":{"formId":"5b1d3503e3acf26950581de3"}}';
////
////
////
////// def jsonStr = '{"reqs":[{"attribVal":"Leo","attribType":"String","propName":"Person.Natural.Full_Name","vertexName":"Person.Natural","predicateStr":"textPrefix"},{"attribVal":"Mardy","attribType":"String","propName":"Person.Natural.Full_Name","vertexName":"Person.Natural","predicateStr":"textFuzzy"},{"attribVal":"Martins","attribType":"String","propName":"Person.Natural.Last_Name","vertexName":"Person.Natural","predicateStr":"textFuzzy"},{"attribVal":"Zukker","attribType":"String","propName":"Person.Natural.Last_Name","vertexName":"Person.Natural","predicateStr":"textFuzzy"},{"attribVal":"Silva","attribType":"String","propName":"Person.Natural.Last_Name","vertexName":"Person.Natural","predicateStr":"textFuzzy"},{"attribVal":"SW1W 9QL","attribType":"String","propName":"Location.Address.Post_Code","vertexName":"Location.Address","predicateStr":"eq"},{"attribVal":"E14 4BB","attribType":"String","propName":"Location.Address.Post_Code","vertexName":"Location.Address","predicateStr":"eq"},{"attribVal":"SW1W 3LL","attribType":"String","propName":"Location.Address.Post_Code","vertexName":"Location.Address","predicateStr":"eq"}]}'
////// def jsonStr = '{"reqs":[{"attribVal":"Leo","attribType":"String","propName":"Person.Natural.Full_Name","vertexName":"Person.Natural","predicateStr":"textPrefix"}]}'
////
////
////
//StringBuffer sb = new StringBuffer()
////
////// matchPerson(jsonStr,sb,  g)
////// sb.toString()
////// g.V()
////
////// g.V().has('Person.Natural.Full_Name',org.janusgraph.core.attribute.Text.textPrefix('Leo')).id()
////
//updateFormData(g,jsonData,"Object.Privacy_Impact_Assessment",sb)
////
//sb.toString()
// describeSchema()
def getOrCreateOwnerVid(gtran, String submissionOwner, StringBuffer sb = new StringBuffer()) {

    sb.append("\nin getOrCreateOwnerVid(); before cloning gtran")

    def localGtrav = gtran.clone()
    def localGtrav2 = gtran.clone()

    sb.append("\nin getOrCreateOwnerVid(); before checking for current entries")

    credential = localGtrav.V().has("Object.Credential.User_Id", submissionOwner)
    sb.append("\nin getOrCreateOwnerVid(); after  checking for current entries")

    def credentialVid = -1 as Long
    if (credential.hasNext()) {
        sb.append("\nin getOrCreateOwnerVid(); Found one old entry")

        credentialVid = credential.next().id();
    } else {
        sb.append("\nin getOrCreateOwnerVid(); creating a new entry")

        credentialVid = localGtrav2.addV("Object.Credential")
        // property("Metadata.Controller", pg_metadataController).
        // property("Metadata.Processor", pg_metadataProcessor).
        // property("Metadata.Lineage", pg_metadataLineage).
        // property("Metadata.Redaction", pg_metadataRedaction).
        // property("Metadata.Version", pg_metadataVersion).
        // property("Metadata.Create_Date", metadataCreateDate).
        // property("Metadata.Update_Date", metadataUpdateDate).
        // property("Metadata.Status", pg_metadataStatus).
        // property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
        // property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
        // property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                .property("Metadata.Type", "Object.Credential")
                .property("Metadata.Type.Object.Credential", "Object.Credential")
                .property("Object.Credential.User_Id", submissionOwner)
                .next().id()
        // property("Object.Credential.login.sha256", pg_login_sha256).next().id()
        sb.append("\nin getOrCreateOwnerVid(); after creating a new entry; id = ")
                .append(credentialVid)

    }

    return credentialVid;

}

enum IngestionOperation {

    CREATE,
    UPDATE,
    DELETE
}

def createIngestionEventId(gtrans, String eventGUID, String eventType, IngestionOperation operation, String origDataClearNonB64, String submissionOwner, Long formDataId = null, StringBuffer sb = new StringBuffer()) {
    Date now = new Date(System.currentTimeMillis())

    sb.append("\nIn createIngestionEventId; adding Event Ingestion")

    def localgTrav = gtrans.clone()
    def localgTrav2 = gtrans.clone()

    def ingestionEvent = gtrans.addV("Event.Form_Ingestion")

    sb.append("\nIn createIngestionEventId; added Event Ingestion; before setting props; ")

    ingestionEvent.property("Event.Form_Ingestion.Metadata_Create_Date", now)
            .property("Metadata.Type.Event.Form_Ingestion", "Event.Form_Ingestion")
            .property("Metadata.Type", "Event.Form_Ingestion")
            .property("Event.Form_Ingestion.Metadata_GUID", eventGUID)
            .property("Event.Form_Ingestion.Type", eventType)
            .property("Event.Form_Ingestion.Operation", operation.toString())
            .property("Event.Form_Ingestion.Domain_b64", origDataClearNonB64.bytes.encodeBase64().toString())
    // .iterate()

    sb.append("\nIn createIngestionEventId; added Event Ingestion; before getting id; ")


    def ingestionEventId = ingestionEvent.next().id() as Long

    sb.append("\nIn createIngestionEventId; added Event Ingestion; Id = ")
            .append(ingestionEventId)


    if (submissionOwner != null) {

        sb.append("\nIn createIngestionEventId; about to getOrCreateOwnerVid")

        Long ownerVid = getOrCreateOwnerVid(localgTrav, submissionOwner, sb)
        sb.append("\nIn createIngestionEventId; after getOrCreateOwnerVid = ")
                .append(ownerVid)
        def fromV = g.V(ownerVid).next()
        def toV = g.V(ingestionEventId).next()

        localgTrav2.addE('Has_Form_Ingestion_Event').from(fromV).to(toV).next()
        sb.append("\nIn createIngestionEventId; after creating edge Has_Form_Ingestion_Event between ownerVid ")
                .append(ownerVid).append(" and ingestionEventId ").append(ingestionEventId)


    }
    if (formDataId != null) {

        localgTrav.addE('Has_Form_Ingestion_Event').from(g.V(ingestionEventId)).to(g.V(formDataId)).next()
        sb.append("\nIn createIngestionEventId; after creating edge Has_Form_Ingestion_Event between ingestionEventId ")
                .append(ingestionEventId).append(" and formDataId ").append(formDataId)

    }

    return ingestionEventId as Long

}


def upsertFormData(
        def gtrav, String dataFromFormInJSON, String dataType, String otherDataType, String otherDataTypeSubmissionId, String relationshipName, StringBuffer sb = new StringBuffer()) {

    sb.append("\nAbout to upsert $dataType; data= $dataFromFormInJSON")

    def gtrav2 = gtrav.clone();

    def retVal = updateFormData(g, dataFromFormInJSON, dataType, sb)

    if (retVal == -1L) {
        retVal = addFormData(g, dataFromFormInJSON, dataType, sb)
    }
    if (otherDataType && otherDataTypeSubmissionId) {
        def trans = graph.tx()

        try {
            if (!trans.isOpen()) {
                trans.open();
            }

            def otherDataTypeSubmissionIdKey = "${otherDataType}.Form_Submission_Id" as String

            def otherTypeId = g.V().has(otherDataTypeSubmissionIdKey, otherDataTypeSubmissionId).next().id() as Long

            if (otherTypeId) {


                def foundIds = g.V(otherTypeId)
                        .both()
                        .hasId(retVal).id().toList() as List<Long>

                if (foundIds.isEmpty()) {


                    def localMgmt = graph.openManagement();
                    createEdgeLabel(mgmt, relationshipName)



                    localMgmt.close()
                    gtrav2.addE(relationshipName).from(g.V(retVal)).to(g.V(otherTypeId)).next()

                    sb.append("\nAfter creating new relationship $relationshipName")

                }
                trans.commit();

            }

        } catch (Throwable t) {
            sb.append("\n$t")
            trans.rollback();
        }
        finally {
            trans.close()
        }
    }


    sb.append("\n $retVal")
    return retVal;
}

def updateFormData(def gtrav, String dataFromFormInJSON, String dataType, StringBuffer sb = new StringBuffer()) {

    def slurper = new JsonSlurper()
    def formDataParsed = slurper.parseText(dataFromFormInJSON)
    def formOwnerId = formDataParsed.request.owner as String
    def formId = formDataParsed.request.form as String

    def submissionId = formDataParsed.submission._id as String
    def submissionOwner = formDataParsed.submission.owner as String

    def gtrav2 = gtrav.clone()

    def localGtrav = gtrav.clone()
    def data = formDataParsed.request.data

    def trans = graph.tx()

    def localMgmt = null;

    def retVal = -1L;

    try {

        if (!trans.isOpen()) {
            trans.open();
        }

        // g.V().drop()
        // g.E().drop()


        def Key_Form_Owner_Id = "${dataType}.Form_Owner_Id" as String
        def Key_Form_Id = "${dataType}.Form_Id" as String
        def Key_Form_Submission_Id = "${dataType}.Form_Submission_Id" as String
        def Key_Form_Submission_Owner_Id = "${dataType}.Form_Submission_Owner_Id" as String
        def Key_Metadata_Type = "Metadata.Type.${dataType}" as String

        sb.append("\nLooking for $Key_Form_Submission_Id with val $submissionId")


        localGtrav = localGtrav.V()
                .has(Key_Form_Submission_Id, (String) submissionId)


        if (!localGtrav.hasNext()) {

            sb.append("\nDid not find a form  with $Key_Form_Submission_Id with val $submissionId")

            retVal = -1L;// _addFormData( gtrav, dataFromFormInJSON, dataType, sb )

        } else {
            localMgmt = graph.openManagement()

            sb.append("Found existing form; updating basic form props\n")
            retVal = localGtrav.next().id() as Long

            gtrav = gtrav.V(retVal)
            gtrav.property(Key_Form_Owner_Id, (String) formOwnerId)
            gtrav.property(Key_Form_Id, (String) formId)
            gtrav.property(Key_Form_Submission_Id, (String) submissionId)
            gtrav.property(Key_Metadata_Type, (String) dataType)
            gtrav.property("Metadata.Type", (String) dataType)
            gtrav.property(Key_Form_Submission_Owner_Id, (String) submissionOwner)


            sb.append("Updated basic form props\n")


            sb.append("${dataType}.Form_Owner_Id = ").append(formOwnerId)
            data.each { k, v ->
                if (k != 'submit') {
                    def key = "$dataType.$k" as String
                    def val = "$v" as String
                    sb.append("\nupdating $key with val = $v =>").append(v.getClass().toString())

                    try {
                        def prop = localMgmt.getPropertyKey(key);
                        if (prop == null) {
//                            prop = createProp(localMgmt, val, String.class, org.janusgraph.core.Cardinality.SINGLE);
                        }

                        if (prop != null) {
                            def dataTypeClazz = prop.dataType();
                            gtrav.property(key, PVConvMixin.asType(val, dataTypeClazz))
                        }
                    } catch (Throwable t) {
                        sb.append("Error Updating Prop $k, $v:\n$t")
                    }
                }
            }

            sb.append("\nAbout to get the ID")


            assert retVal == (gtrav.next().id() as Long)

            sb.append("\ngot  the ID ").append(retVal)

            createIngestionEventId(gtrav2, submissionId, dataType, IngestionOperation.UPDATE,
                    dataFromFormInJSON, submissionOwner, retVal, sb)


        }

        trans.commit();
        sb.append("\nAFTER COMMITT")

        // def newEntry = gtrav.next()
    } catch (Throwable t) {
        sb.append("\n$t")
        trans.rollback();
    }
    finally {
        trans.close()
    }

    if (localMgmt) {
        localMgmt.commit();
    }
//    mgmt.commit();


    return retVal;


}

def deleteFormData(def gtrav, String dataFromFormInJSON, String dataType, StringBuffer sb = new StringBuffer()) {

    def slurper = new JsonSlurper()
    def formDataParsed = slurper.parseText(dataFromFormInJSON)


    def submissionId = formDataParsed.params.submissionId as String


    def trans = graph.tx()

    mgmt = graph.openManagement()


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
    mgmt.commit();

}

def createPropsIfNotThere(mgmt, String key, boolean createIdxIfNotPresent = false, StringBuffer sb = new StringBuffer()) {
    def prop = mgmt.getPropertyKey(key);
    if (prop == null) {
        sb.append("\nDid not find prop $key")

//        prop = createProp(mgmt, val, String.class, org.janusgraph.core.Cardinality.SINGLE);
        if (createIdxIfNotPresent) {
            sb.append("\nAbout to add prop $key to index ${key}CompIdx")

            createCompIdx(mgmt, key + "CompIdx", prop);

        }

    } else {
        sb.append("\nFound prop $key")

    }

    return key

}

def addFormData(def gtrav, String dataFromFormInJSON, String dataType, StringBuffer sb = new StringBuffer()) {
    def slurper = new JsonSlurper()
    def formDataParsed = slurper.parseText(dataFromFormInJSON)
    def formOwnerId = formDataParsed.request.owner as String
    def formId = formDataParsed.request.form as String

    def submissionId = formDataParsed.submission._id as String
    def submissionOwner = formDataParsed.submission.owner as String

    def data = formDataParsed.request.data

    def trans = graph.tx()

    def mgmt = graph.openManagement()

    def retVal = -1L;
    def gtrav2 = gtrav.clone()

    try {

        if (!trans.isOpen()) {
            trans.open();
        }

        // g.V().drop()
        // g.E().drop()
        sb.append("Adding basic form props\n")


        def Key_Form_Owner_Id = "${dataType}.Form_Owner_Id" as String
        def Key_Form_Id = "${dataType}.Form_Id" as String
        def Key_Form_Submission_Id = "${dataType}.Form_Submission_Id" as String
        def Key_Form_Submission_Owner_Id = "${dataType}.Form_Submission_Owner_Id" as String
        def Key_Metadata_Type = "Metadata.Type.${dataType}" as String


        gtrav = gtrav.addV(dataType)

        gtrav.property(createPropsIfNotThere(mgmt, Key_Form_Owner_Id, false, sb), (String) formOwnerId)
        gtrav.property(createPropsIfNotThere(mgmt, Key_Form_Id, false, sb), (String) formId)
        gtrav.property(createPropsIfNotThere(mgmt, Key_Form_Submission_Id, true), (String) submissionId)
        gtrav.property(Key_Metadata_Type, (String) dataType)
        gtrav.property("Metadata.Type", (String) dataType)
        gtrav.property(createPropsIfNotThere(mgmt, Key_Form_Submission_Owner_Id, false, sb), (String) submissionOwner)


        sb.append("ADDED basic form props\n")


        sb.append("${dataType}.Form_Owner_Id = ").append(formOwnerId)
        data.each { k, v ->
            if (k != 'submit') {
                def key = "$dataType.$k" as String
                def val = "$v" as String
                sb.append("\nadding $key with val = $v =>").append(v.getClass().toString())

                try {
                    def prop = mgmt.getPropertyKey(key);
                    if (prop == null) {
//                        prop = createProp(mgmt, val, String.class, org.janusgraph.core.Cardinality.SINGLE);
                    }

                    if (prop != null) {
                        def dataTypeClazz = prop.dataType();
                        gtrav.property(key, PVConvMixin.asType(val, dataTypeClazz))
                    }
                } catch (Throwable t) {
                    sb.append("Error adding prop $k with val ($v): \n$t")
                }
            }
        }

        sb.append("\nAbout to get the ID")

        retVal = gtrav.next().id() as Long

        sb.append("\ngot  the ID: ").append(retVal)


        createIngestionEventId(gtrav2, submissionId, dataType, IngestionOperation.CREATE,
                dataFromFormInJSON, submissionOwner, retVal, sb)


        trans.commit();
        sb.append("\nAFTER COMMITT")

        // def newEntry = gtrav.next()
    } catch (Throwable t) {
        sb.append("\n$t")
        if (trans.isOpen()) {
            trans.rollback();
        }
    }
    finally {
        mgmt.commit();

    }


    return retVal;

}

