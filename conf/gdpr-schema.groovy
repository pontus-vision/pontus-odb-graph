import org.apache.commons.math3.util.Pair
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.schema.Mapping


def createIndicesPropsAndLabels() {

/*
    "vpc-02e7f84d
  [{Monitoring={State=disabled},
    PublicDnsName=, State={Code=16, Name=running}, EbsOptimized=false, LaunchTime=2017-12-16T15:21:50.000Z, PrivateIpAddress=10.230.30.4, ProductCodes=[{\"ProductCodeId\":\"awee1f4ac842336951c35aef1\",\"ProductCodeType\":\"marketplace\"}], VpcId=vpc-02e7f84d, StateTransitionReason=, InstanceId=i-ff46e1a3ae996c9f8, EnaSupport=true, ImageId=ami-20060558, PrivateDnsName=ip-10-230-30-4.eu-west-2.compute.internal, KeyName=dev_deployment_key,  ClientToken=, SubnetId=subnet-e0a83226, InstanceType=r4.large,

    SecurityGroups=[{\"GroupName\":\"abc-alpha-ext-logging-syslog\",\"GroupId\":\"sg-652e8012\"}]


   , SourceDestCheck=true,
   Placement={Tenancy=default, GroupName=, AvailabilityZone=eu-west-2a},
   Hypervisor=xen,
   BlockDeviceMappings=[{\"DeviceName\":\"\\/dev\\/sda1\",\"Ebs\":{\"Status\":\"attached\",\"DeleteOnTermination\":true,\"VolumeId\":\"vol-3a18a09582a52ab00\",\"AttachTime\":\"2017-12-15T13:56:17.000Z\"}}],
   Architecture=x86_64,
   RootDeviceType=ebs,
   RootDeviceName=/dev/sda1,
   VirtualizationType=hvm,
   Tags=[{\"Value\":\"abc-alpha-dev-ext-logging-syslog-az1-1\",\"Key\":\"Name\"}],
   AmiLaunchIndex=0}]"+

  */




    def mgmt = graph.openManagement();


    metadataController = createProp(mgmt, "Metadata.Controller", String.class, org.janusgraph.core.Cardinality.SET)
    metadataProcessor = createProp(mgmt, "Metadata.Processor", String.class, org.janusgraph.core.Cardinality.SET)
    metadataLineage = createProp(mgmt, "Metadata.Lineage", String.class, org.janusgraph.core.Cardinality.SET)
    metadataRedaction = createProp(mgmt, "Metadata.Redaction", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataVersion = createProp(mgmt, "Metadata.Version", Integer.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataCreateDate = createProp(mgmt, "Metadata.Create_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataUpdateDate = createProp(mgmt, "Metadata.Update_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataStatus = createProp(mgmt, "Metadata.Status", String.class, org.janusgraph.core.Cardinality.SET)
    metadataOrigId = createProp(mgmt, "Metadata.Orig_Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataGDPRStatus = createProp(mgmt, "Metadata.GDPR_Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataLineageServerTag = createProp(mgmt, "Metadata.Lineage_Server_Tag", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataLineageLocationTag = createProp(mgmt, "Metadata.Lineage_Location_Tag", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataType = createProp(mgmt, "Metadata.Type", String.class, org.janusgraph.core.Cardinality.SINGLE)

//    metadataLineageServerTagIdx = createMixedIdx(mgmt, "metadataLineageServerTagIdx", metadataLineageServerTag)
    metadataTypeIdx = createMixedIdx(mgmt, "metadataTypeIdx", metadataType, metadataLineageLocationTag, metadataCreateDate)
//    metadataLineageLocationTagIdx = createMixedIdx(mgmt, "metadataLineageLocationTagIdx", metadataLineageLocationTag)
//    metadataTypeCreateDateIdx = createMixedIdx(mgmt, 'metadataTypeCreateDateMixedIdx',metadataType,Mapping.DEFAULT.asParameter(),metadataCreateDate)

//    metadataCreateDateIdx = createCompIdx(mgmt, "metadataCreateDateMixedIdx", metadataCreateDate)
//    metadataUpdateDateIdx = createCompIdx(mgmt, "metadataUpdateDateMixedIdx", metadataUpdateDate)

//    metadataGDPRStatusIdx = createMixedIdx(mgmt, "metadataGDPRStatusMixedIdx", metadataGDPRStatus)

/*
O-Form Store
O.Form.Metadata.Owner
O.Form.Metadata.Create.Date
O.Form.Metadata.GUID
O.Form.Text
O.Form.URL
O.Form.Vertex_Label

*/





    eventIngestionGroupLabel = createVertexLabel(mgmt, "Event.Group_Ingestion");

    eventIngestionGroupProp01 = createProp(mgmt, "Event.Group_Ingestion.Metadata_Start_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE);
    eventIngestionGroupProp02 = createProp(mgmt, "Event.Group_Ingestion.Metadata_End_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE);
    eventIngestionGroupProp03 = createProp(mgmt, "Event.Group_Ingestion.Type", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventIngestionGroupProp04 = createProp(mgmt, "Event.Group_Ingestion.Operation", String.class, org.janusgraph.core.Cardinality.SINGLE);

    eventIngestionIdx01 = createMixedIdx(mgmt, "eventIngestionGroupIdx01"
            ,eventIngestionGroupLabel
            ,eventIngestionGroupProp01
            ,eventIngestionGroupProp02
            ,eventIngestionGroupProp03
            ,eventIngestionGroupProp04
    );




    eventIngestionLabel = createVertexLabel(mgmt, "Event.Ingestion");

    eventIngestionProp01 = createProp(mgmt, "Event.Ingestion.Metadata_Create_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE);
    eventIngestionProp02 = createProp(mgmt, "Event.Ingestion.Metadata_GUID", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventIngestionProp03 = createProp(mgmt, "Event.Ingestion.Type", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventIngestionProp04 = createProp(mgmt, "Event.Ingestion.Operation", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventIngestionProp05 = createProp(mgmt, "Event.Ingestion.Domain_b64", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventIngestionProp06 = createProp(mgmt, "Event.Ingestion.Domain_Unstructured_Data_b64", String.class, org.janusgraph.core.Cardinality.SINGLE);

    eventIngestionIdx01 = createMixedIdx(mgmt, "eventIngestionIdx01"
            ,eventIngestionLabel
            ,eventIngestionProp01
            ,eventIngestionProp02
            ,eventIngestionProp03
            ,eventIngestionProp04
    );




    eventFormIngestionLabel = createVertexLabel(mgmt, "Event.Form_Ingestion");

    eventFormIngestionProp01 = createProp(mgmt, "Event.Form_Ingestion.Metadata_Create_Date", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventFormIngestionProp02 = createProp(mgmt, "Event.Form_Ingestion.Metadata_GUID", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventFormIngestionProp03 = createProp(mgmt, "Event.Form_Ingestion.Type", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventFormIngestionProp04 = createProp(mgmt, "Event.Form_Ingestion.Operation", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventFormIngestionProp05 = createProp(mgmt, "Event.Form_Ingestion.Domain_b64", String.class, org.janusgraph.core.Cardinality.SINGLE);

    eventFormIngestionIdx01 = createMixedIdx(mgmt, "eventFormIngestionIdx01"
            ,eventFormIngestionLabel
            ,eventFormIngestionProp01
            ,eventFormIngestionProp02
            ,eventFormIngestionProp03
            ,eventFormIngestionProp04
    );


    objectFormLabel = createVertexLabel(mgmt, "Object.Form");

    objectFormProp00 = createProp(mgmt, "Object.Form.Metadata_Owner", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectFormProp01 = createProp(mgmt, "Object.Form.Metadata_Create_Date", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectFormProp01 = createProp(mgmt, "Object.Form.Metadata_GUID", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectFormProp02 = createProp(mgmt, "Object.Form.URL", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectFormProp03 = createProp(mgmt, "Object.Form.Text", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectFormProp04 = createProp(mgmt, "Object.Form.Vertex_Label", String.class, org.janusgraph.core.Cardinality.SINGLE);

    objectNotificationTemplatesIdx01 = createMixedIdx(mgmt, "objectFormIdx01", objectFormLabel, objectFormProp00, objectFormProp01, objectFormProp02, objectFormProp04);




    objectAWSInstanceLabel = createVertexLabel(mgmt, "Object.Notification_Templates");

    objectNotificationTemplatesProp00 = createProp(mgmt, "Object.Notification_Templates.Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectNotificationTemplatesProp01 = createProp(mgmt, "Object.Notification_Templates.Types", String.class, org.janusgraph.core.Cardinality.SET);
    objectNotificationTemplatesProp02 = createProp(mgmt, "Object.Notification_Templates.URL", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectNotificationTemplatesProp03 = createProp(mgmt, "Object.Notification_Templates.Text", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectNotificationTemplatesProp04 = createProp(mgmt, "Object.Notification_Templates.Label", String.class, org.janusgraph.core.Cardinality.SINGLE);

    objectNotificationTemplatesIdx00 = createCompIdx(mgmt, "objectNotificationTemplatesIdx00", objectNotificationTemplatesProp00);
    objectNotificationTemplatesIdx01 = createMixedIdx(mgmt, "objectNotificationTemplatesIdx01", objectAWSInstanceLabel, objectNotificationTemplatesProp01);
//    objectNotificationTemplatesIdx02 = createCompIdx(mgmt, "objectNotificationTemplatesIdx02", objectNotificationTemplatesProp02);
//    objectNotificationTemplatesIdx03 = createCompIdx(mgmt, "objectNotificationTemplatesIdx03", objectNotificationTemplatesProp03);


    eventDataBreachLabel = createVertexLabel(mgmt, "Event.Data_Breach");
    eventDataBreachProp00 = createProp(mgmt, "Event.Data_Breach.Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventDataBreachProp01 = createProp(mgmt, "Event.Data_Breach.Description", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventDataBreachProp02 = createProp(mgmt, "Event.Data_Breach.Status", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventDataBreachProp03 = createProp(mgmt, "Event.Data_Breach.Source", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventDataBreachProp04 = createProp(mgmt, "Event.Data_Breach.Impact", String.class, org.janusgraph.core.Cardinality.SINGLE);
    eventDataBreachProp05 = createProp(mgmt, "Event.Data_Breach.Metadata.Create_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE);
    eventDataBreachProp06 = createProp(mgmt, "Event.Data_Breach.Metadata.Update_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE);

    eventDataBreachIdx00 = createMixedIdx(mgmt, "eventDataBreachIdx00", eventDataBreachLabel, eventDataBreachProp00, eventDataBreachProp02, eventDataBreachProp03, eventDataBreachProp04,eventDataBreachProp05,eventDataBreachProp06);
//    eventDataBreachIdx01 = createCompIdx(mgmt, "eventDataBreachIdx01", eventDataBreachProp01);
//    eventDataBreachIdx02 = createMixedIdx(mgmt, "eventDataBreachIdx02", metadataType, eventDataBreachProp02);
//    eventDataBreachIdx03 = createMixedIdx(mgmt, "eventDataBreachIdx03", metadataType, eventDataBreachProp03);
//    eventDataBreachIdx04 = createMixedIdx(mgmt, "eventDataBreachIdx04",  metadataType,eventDataBreachProp04);
//    eventDataBreachIdx05 = createMixedIdx(mgmt, "eventDataBreachIdx05",  eventDataBreachProp02,eventDataBreachProp04);


    objectMoULabel = createVertexLabel(mgmt, "Object.MoU")
    objectMoUProp00 = createProp(mgmt, "Object.MoU.Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectMoUProp01 = createProp(mgmt, "Object.MoU.Description", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectMoUProp02 = createProp(mgmt, "Object.MoU.Status", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectMoUProp03 = createProp(mgmt, "Object.MoU.Link", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectMoUProp04 = createProp(mgmt, "Object.MoU.Form_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectMoUProp05 = createProp(mgmt, "Object.MoU.Form_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectMoUProp06 = createProp(mgmt, "Object.MoU.Form_Submission_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectMoUProp07 = createProp(mgmt, "Object.MoU.Form_Submission_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);

    objectMoUIdx00 = createMixedIdx(mgmt, "objectMoUIdx00", objectMoULabel, objectMoUProp00, objectMoUProp02, objectMoUProp03, objectMoUProp04, objectMoUProp05, objectMoUProp06, objectMoUProp07);

    createEdgeLabel(mgmt, "Has_MoU")


    objectAWSInstanceLabel = createVertexLabel(mgmt, "Object.AWS_Instance");


    objectAWSProp00 = createProp(mgmt, "Object.AWS_Instance.Public_Dns_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp01 = createProp(mgmt, "Object.AWS_Instance.EbsOptimized", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp02 = createProp(mgmt, "Object.AWS_Instance.LaunchTime", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp03 = createProp(mgmt, "Object.AWS_Instance.PrivateIpAddress", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp04 = createProp(mgmt, "Object.AWS_Instance.ProductCodeTypes", String.class, org.janusgraph.core.Cardinality.SET)
    objectAWSProp05 = createProp(mgmt, "Object.AWS_Instance.ProductCodeIDs", String.class, org.janusgraph.core.Cardinality.SET)

    objectAWSProp06 = createProp(mgmt, "Object.AWS_Instance.Id", String.class, org.janusgraph.core.Cardinality.SINGLE)

    objectAWSProp07 = createProp(mgmt, "Object.AWS_Instance.ImageId", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp08 = createProp(mgmt, "Object.AWS_Instance.PrivateDnsName", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp09 = createProp(mgmt, "Object.AWS_Instance.KeyName", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp10 = createProp(mgmt, "Object.AWS_Instance.InstanceType", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp11 = createProp(mgmt, "Object.AWS_Instance.EnaSupport", Boolean.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp12 = createProp(mgmt, "Object.AWS_Instance.Tags", String.class, org.janusgraph.core.Cardinality.SET)

    objectAWSIdx00 = createMixedIdx(mgmt, "objectAWS_InstanceIdx00", objectAWSInstanceLabel, objectAWSProp00, objectAWSProp06, objectAWSProp07, objectAWSProp10, objectAWSProp12)
////    objectAWSIdx01 = createMixedIdx(mgmt, "objectAWS_InstanceIdx01", objectAWSProp01)
////    objectAWSIdx02 = createMixedIdx(mgmt, "objectAWS_InstanceIdx02", objectAWSProp02)
////    objectAWSIdx03 = createMixedIdx(mgmt, "objectAWS_InstanceIdx03", objectAWSProp03)
////    objectAWSIdx04 = createMixedIdx(mgmt, "objectAWS_InstanceIdx04", objectAWSProp04)
////    objectAWSIdx05 = createMixedIdx(mgmt, "objectAWS_InstanceIdx05", objectAWSProp05)
//    objectAWSIdx06 = createMixedIdx(mgmt, "objectAWS_InstanceIdx06", objectAWSProp06)
//    objectAWSIdx07 = createMixedIdx(mgmt, "objectAWS_InstanceIdx07", objectAWSProp07)
////    objectAWSIdx08 = createMixedIdx(mgmt, "objectAWS_InstanceIdx08", objectAWSProp08)
////    objectAWSIdx09 = createMixedIdx(mgmt, "objectAWS_InstanceIdx09", objectAWSProp09)
//    objectAWSIdx10 = createMixedIdx(mgmt, "objectAWS_InstanceIdx10", objectAWSProp10)
////    objectAWSIdx11 = createMixedIdx(mgmt, "objectAWS_InstanceIdx11", objectAWSProp11)
//    objectAWSIdx12 = createMixedIdx(mgmt, "objectAWS_InstanceIdx12", objectAWSProp12)
//
//    objectAWSIdx06 = createMixedIdx(mgmt, "objectAWS_InstanceIdxMixedIdx06", objectAWSProp06)


    objectAWSSecurityGroupLabel = createVertexLabel(mgmt, "Object.AWS_Security_Group");

    objectAWSProp00 = createProp(mgmt, "Object.AWS_Security_Group.GroupName", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp01 = createProp(mgmt, "Object.AWS_Security_Group.Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp02 = createProp(mgmt, "Object.AWS_Security_Group.Ip_Perms_Ingress_IpRanges", String.class, org.janusgraph.core.Cardinality.SET)
    objectAWSProp03 = createProp(mgmt, "Object.AWS_Security_Group.Ip_Perms_Egress_IpRanges", String.class, org.janusgraph.core.Cardinality.SET)

    objectAWSIdx00 = createMixedIdx(mgmt, "objectAWS_Security_GroupIdx00", objectAWSSecurityGroupLabel, objectAWSProp00, objectAWSProp01, objectAWSProp02, objectAWSProp03)

//    objectAWSIdx01 = createMixedIdx(mgmt, "objectAWS_Security_GroupMixedIdx01", objectAWSSecurityGroupLabel,objectAWSProp01)

    objectAWSVPCLabel = createVertexLabel(mgmt, "Object.AWS_VPC");

    objectAWSProp00 = createProp(mgmt, "Object.AWS_VPC.Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
//    objectAWSIdx00 = createMixedIdx(mgmt, "objectAWS_VPCIdx01", objectAWSProp00)


    createMixedIdx(mgmt, 'objectAWS_VPCIdx01', objectAWSVPCLabel, objectAWSProp00);

    /*
        NetworkInterfaces=[
        {\"Status\":\"in-use\",
        \"MacAddress\":\"45:e6:bf:51:01:17\",
        \"SourceDestCheck\":true,
        \"VpcId\":\"vpc-02e7f84d\",
        \"Description\":\"private ip address for abc alpha dev stack ext-logging syslog\",
        \"NetworkInterfaceId\":\"eni-8ea68414\",
        \"PrivateIpAddresses\":[{\"PrivateDnsName\":\"ip-10-230-30-4.eu-west-2.compute.internal\",\"Primary\":true,\"PrivateIpAddress\":\"10.230.30.4\"}],
        \"PrivateDnsName\":\"ip-10-230-30-4.eu-west-2.compute.internal\",
        \"Attachment\":{\"Status\":\"attached\",\"DeviceIndex\":0,\"DeleteOnTermination\":false,\"AttachmentId\":\"eni-attach-a1d026c2\",
        \"AttachTime\":\"2017-12-15T13:56:16.000Z\"},
        \"Groups\":[{\"GroupName\":\"abc-alpha-ext-logging-syslog\",\"GroupId\":\"sg-652e8012\"}],
        \"Ipv6Addresses\":[],\"OwnerId\":\"524174466850\",\"SubnetId\":\"subnet-e0a83226\",\"PrivateIpAddress\":\"10.230.30.4\"}
        ]

     */

    objectAWSNetworkInterfaceLabel = createVertexLabel(mgmt, "Object.AWS_Network_Interface");

    objectAWSProp00 = createProp(mgmt, "Object.AWS_Network_Interface.MacAddress", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp01 = createProp(mgmt, "Object.AWS_Network_Interface.Description", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp02 = createProp(mgmt, "Object.AWS_Network_Interface.NetworkInterfaceId", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp03 = createProp(mgmt, "Object.AWS_Network_Interface.PrivateIpAddresses", String.class, org.janusgraph.core.Cardinality.SET)
    objectAWSProp04 = createProp(mgmt, "Object.AWS_Network_Interface.PrivateDnsName", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAWSProp05 = createProp(mgmt, "Object.AWS_Network_Interface.AttachTime", String.class, org.janusgraph.core.Cardinality.SINGLE)

    objectAWSIdx00 = createCompIdx(mgmt, "objectAWS_Network_InterfaceIdx00", objectAWSNetworkInterfaceLabel, objectAWSProp00, objectAWSProp01, objectAWSProp02, objectAWSProp03, objectAWSProp04)
//    objectAWSIdx01 = createCompIdx(mgmt, "objectAWS_Network_InterfaceIdx01", objectAWSProp01)
//    objectAWSIdx02 = createCompIdx(mgmt, "objectAWS_Network_InterfaceIdx02", objectAWSProp02)
//    objectAWSIdx03 = createCompIdx(mgmt, "objectAWS_Network_InterfaceIdx03", objectAWSProp03)
//    objectAWSIdx04 = createCompIdx(mgmt, "objectAWS_Network_InterfaceIdx04", objectAWSProp04)
//    objectAWSIdx05 = createCompIdx(mgmt, "objectAWS_Network_InterfaceIdx05", objectAWSProp05)


    objectDataProceduresLabel = createVertexLabel(mgmt, "Object.Data_Procedures")

    objectDataProceduresType = createProp(mgmt, "Object.Data_Procedures.Type", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresProperty = createProp(mgmt, "Object.Data_Procedures.Property", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresDeleteURL = createProp(mgmt, "Object.Data_Procedures.Delete_URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresUpdateURL = createProp(mgmt, "Object.Data_Procedures.Update_URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresDeleteMechanism = createProp(mgmt, "Object.Data_Procedures.Delete_Mechanism", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresUpdateMechanism = createProp(mgmt, "Object.Data_Procedures.Update_Mechanism", String.class, org.janusgraph.core.Cardinality.SINGLE)

    objectDataProceduresTypeIdx = createMixedIdx(mgmt, "objectDataProceduresIdx", objectDataProceduresLabel, objectDataProceduresType, objectDataProceduresProperty, objectDataProceduresDeleteMechanism, objectDataProceduresUpdateMechanism)
//    objectDataProceduresPropertyIdx = createMixedIdx(mgmt, "objectDataProceduresPropertyIdx", objectDataProceduresProperty)
////    objectDataProceduresDeleteURLIdx = createMixedIdx(mgmt, "objectDataProceduresDeleteURLIdx", objectDataProceduresDeleteURL)
////    objectDataProceduresUpdateURLIdx = createMixedIdx(mgmt, "objectDataProceduresUpdateURLIdx", objectDataProceduresUpdateURL)
//    objectDataProceduresDeleteMechanismIdx = createMixedIdx(mgmt, "objectDataProceduresDeleteMechanismIdx", objectDataProceduresDeleteMechanism)
//    objectDataProceduresUpdateMechanismIdx = createMixedIdx(mgmt, "objectDataProceduresUpdateMechanismIdx", objectDataProceduresUpdateMechanism)


    eventTrainingLabel = createVertexLabel(mgmt, "Event.Training")

    eventTrainingStatus = createProp(mgmt, "Event.Training.Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
//    metadataGDPRStatusIdx = createCompIdx(mgmt, "eventTrainingStatusIdx", eventTrainingStatus)
    metadataGDPRStatusIdx = createMixedIdx(mgmt, "eventTrainingStatusMixedIdx", eventTrainingLabel, eventTrainingStatus)




    eventSubjAccessReq = createVertexLabel(mgmt, "Event.Subject_Access_Request")

    eventSARStatus = createProp(mgmt, "Event.Subject_Access_Request.Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
//    eventSARStatusIdx = createCompIdx(mgmt, "eventSARStatusIdx", eventSARStatus)

    eventSARRequestType = createProp(mgmt, "Event.Subject_Access_Request.Request_Type", String.class, org.janusgraph.core.Cardinality.SINGLE)
    eventSARMetadataCreateDate = createProp(mgmt, "Event.Subject_Access_Request.Metadata.Create_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    eventSARMetadataUpdateDate = createProp(mgmt, "Event.Subject_Access_Request.Metadata.Update_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)

    eventSARStatusIdx = createMixedIdx(mgmt, "eventSARStatusMixedIdx", eventSARMetadataUpdateDate,eventSARMetadataCreateDate,eventSubjAccessReq, eventSARStatus, eventSARRequestType)


//    eventSARRequestTypeIdx = createCompIdx(mgmt, "eventSARRequestTypeIdx", eventSARRequestType)

    createEdgeLabel(mgmt, "Made_SAR_Request")
    createEdgeLabel(mgmt, "Assigned_SAR_Request")





    personLabel = createVertexLabel(mgmt, "Person.Natural")
    personDateOfBirth = createProp(mgmt, "Person.Natural.Date_Of_Birth", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    personFullName = createProp(mgmt, "Person.Natural.Full_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personFullNameFuzzy = createProp(mgmt, "Person.Natural.Full_Name_fuzzy", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personLastName = createProp(mgmt, "Person.Natural.Last_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personGender = createProp(mgmt, "Person.Natural.Gender", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personNationality = createProp(mgmt, "Person.Natural.Nationality", String.class, org.janusgraph.core.Cardinality.SET)
    personPlaceOfBirth = createProp(mgmt, "Person.Natural.Place_Of_Birth", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personReligion = createProp(mgmt, "Person.Natural.Religion", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEthnicity = createProp(mgmt, "Person.Natural.Ethnicity", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personMaritalStatus = createProp(mgmt, "Person.Natural.Marital_Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personHeight = createProp(mgmt, "Person.Natural.Height", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personNameQualifier = createProp(mgmt, "Person.Natural.Name_Qualifier", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personTitle = createProp(mgmt, "Person.Natural.Title", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personCustomerId = createProp(mgmt, "Person.Natural.Customer_ID", String.class, org.janusgraph.core.Cardinality.SINGLE)

//    createMixedIdx(mgmt, "personDataOfBirthMixedIdx", personLabel, personFullName, personLastName, personGender, personNationality, personDateOfBirth,
//            personPlaceOfBirth, personReligion, personEthnicity, personMaritalStatus, personNameQualifier, personTitle,personCustomerId);
    Pair<PropertyKey,Mapping>[] personIdxProps = [
            new Pair<>(personLabel, Mapping.STRING)
            ,new Pair<>(personFullName, Mapping.STRING)
            ,new Pair<>(personFullNameFuzzy, Mapping.TEXT)
            ,new Pair<>(personLastName, Mapping.STRING)
            ,new Pair<>(personGender, Mapping.STRING)
            ,new Pair<>(personNationality, Mapping.STRING)
            ,new Pair<>(personDateOfBirth, Mapping.DEFAULT)
            ,new Pair<>(personPlaceOfBirth, Mapping.STRING)
            ,new Pair<>(personReligion, Mapping.STRING)
            ,new Pair<>(personEthnicity, Mapping.STRING)
            ,new Pair<>(personMaritalStatus, Mapping.STRING)
            ,new Pair<>(personNameQualifier, Mapping.STRING)
            ,new Pair<>(personTitle, Mapping.STRING)
            ,new Pair<>(personCustomerId, Mapping.STRING)
    ] ;

    createMixedIdx(mgmt, "Person.Natural.MixedIdx", false, personIdxProps) ;


//    createCompIdx(mgmt, "personDateOfBirth", personDateOfBirth)
//    createMixedIdx(mgmt, "personTitleMixedIdx", metadataType, personTitle)
//    createMixedIdx(mgmt, "personFullNameMixedIdx",metadataType, personFullName)
//    createMixedIdx(mgmt, "personLastNameMixedIdx", metadataType, personLastName)
//    createMixedIdx(mgmt, "personGenderMixedIdx",metadataType,  personGender)
//    createMixedIdx(mgmt, "personNationalityMixedIdx",metadataType,  personNationality)
//    createMixedIdx(mgmt, "personDateOfBirthMixedIdx", personDateOfBirth)


    objectEmailAddressLabel = createVertexLabel(mgmt, "Object.Email_Address")
    objectEmailAddressEmail = createProp(mgmt, "Object.Email_Address.Email", String.class, org.janusgraph.core.Cardinality.SET)
    createMixedIdx(mgmt, "objectEmailAddressEmailMixedIdx", objectEmailAddressLabel, objectEmailAddressEmail)


    objectCredentialLabel = createVertexLabel(mgmt, "Object.Credential")
    objectCredentialUserId = createProp(mgmt, "Object.Credential.User_Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectCredentialLoginSha256 = createProp(mgmt, "Object.Credential.Login_SHA256", String.class, org.janusgraph.core.Cardinality.SINGLE)
    createMixedIdx(mgmt, "objectCredentialMixedIdx", objectCredentialLabel, objectCredentialUserId)


    objectIdentityCardLabel = createVertexLabel(mgmt, "Object.Identity_Card")
    objectIdentityCardIdName = createProp(mgmt, "Object.Identity_Card.Id_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectIdentityCardIdValue = createProp(mgmt, "Object.Identity_Card.Id_Value", String.class, org.janusgraph.core.Cardinality.SINGLE)
    createMixedIdx(mgmt, "objectIdentityCardIdNameMixedIdx", objectIdentityCardLabel, objectIdentityCardIdName)
//    createMixedIdx(mgmt, "objectIdentityCardIdNameMixedIdx", objectIdentityCardIdName)

    locationAddressLabel = createVertexLabel(mgmt, "Location.Address")
    locationAddressStreet = createProp(mgmt, "Location.Address.Street", String.class, org.janusgraph.core.Cardinality.SINGLE)
    locationAddressCity = createProp(mgmt, "Location.Address.City", String.class, org.janusgraph.core.Cardinality.SINGLE)
    locationAddressState = createProp(mgmt, "Location.Address.State", String.class, org.janusgraph.core.Cardinality.SINGLE)
    locationAddressPostCode = createProp(mgmt, "Location.Address.Post_Code", String.class, org.janusgraph.core.Cardinality.SINGLE)
    locationAddressFullAddress = createProp(mgmt, "Location.Address.Full_Address", String.class, org.janusgraph.core.Cardinality.SINGLE)
    locationAddressParser_00 = createProp(mgmt, "Location.Address.parser.house", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_01 = createProp(mgmt, "Location.Address.parser.category", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_02 = createProp(mgmt, "Location.Address.parser.near", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_03 = createProp(mgmt, "Location.Address.parser.house_number", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_04 = createProp(mgmt, "Location.Address.parser.road", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_05 = createProp(mgmt, "Location.Address.parser.unit", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_06 = createProp(mgmt, "Location.Address.parser.level", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_07 = createProp(mgmt, "Location.Address.parser.staircase", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_08 = createProp(mgmt, "Location.Address.parser.entrance", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_09 = createProp(mgmt, "Location.Address.parser.po_box", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_10 = createProp(mgmt, "Location.Address.parser.postcode", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_11 = createProp(mgmt, "Location.Address.parser.suburb", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_12 = createProp(mgmt, "Location.Address.parser.city_district", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_13 = createProp(mgmt, "Location.Address.parser.city", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_14 = createProp(mgmt, "Location.Address.parser.island", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_15 = createProp(mgmt, "Location.Address.parser.state_district", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_16 = createProp(mgmt, "Location.Address.parser.state", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_17 = createProp(mgmt, "Location.Address.parser.country_region", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_18 = createProp(mgmt, "Location.Address.parser.country", String.class, org.janusgraph.core.Cardinality.SET)
    locationAddressParser_19 = createProp(mgmt, "Location.Address.parser.world_region", String.class, org.janusgraph.core.Cardinality.SET)

    /*
    house: venue name e.g. "Brooklyn Academy of Music", and building names e.g. "Empire State Building"
category: for category queries like "restaurants", etc.
near: phrases like "in", "near", etc. used after a category phrase to help with parsing queries like "restaurants in Brooklyn"
house_number: usually refers to the external (street-facing) building number. In some countries this may be a compount, hyphenated number which also includes an apartment number, or a block number (a la Japan), but libpostal will just call it the house_number for simplicity.
road: street name(s)
unit: an apartment, unit, office, lot, or other secondary unit designator
level: expressions indicating a floor number e.g. "3rd Floor", "Ground Floor", etc.
staircase: numbered/lettered staircase
entrance: numbered/lettered entrance
po_box: post office box: typically found in non-physical (mail-only) addresses
postcode: postal codes used for mail sorting
suburb: usually an unofficial neighborhood name like "Harlem", "South Bronx", or "Crown Heights"
city_district: these are usually boroughs or districts within a city that serve some official purpose e.g. "Brooklyn" or "Hackney" or "Bratislava IV"
city: any human settlement including cities, towns, villages, hamlets, localities, etc.
island: named islands e.g. "Maui"
state_district: usually a second-level administrative division or county.
state: a first-level administrative division. Scotland, Northern Ireland, Wales, and England in the UK are mapped to "state" as well (convention used in OSM, GeoPlanet, etc.)
country_region: informal subdivision of a country without any political status
country: sovereign nations and their dependent territories, anything with an ISO-3166 code.
world_region: currently only used for appending “West Indies” after the country name, a pattern frequently used in the English-speaking Caribbean e.g. “Jamaica, West Indies”

     */




    createMixedIdx(mgmt, "locationAddressStreetMixedIdx", locationAddressLabel,
            locationAddressStreet
            , locationAddressCity
            , locationAddressState
            , locationAddressPostCode
            , locationAddressFullAddress
            , locationAddressParser_00
            , locationAddressParser_01
            , locationAddressParser_02
            , locationAddressParser_03
            , locationAddressParser_04
            , locationAddressParser_05
            , locationAddressParser_06
            , locationAddressParser_07
            , locationAddressParser_08
            , locationAddressParser_09
            , locationAddressParser_10
            , locationAddressParser_11
            , locationAddressParser_12
            , locationAddressParser_13
            , locationAddressParser_14
            , locationAddressParser_15
            , locationAddressParser_16
            , locationAddressParser_17
            , locationAddressParser_18
            , locationAddressParser_19
    )
//    createMixedIdx(mgmt, "locationAddressCityMixedIdx", locationAddressCity)
//    createMixedIdx(mgmt, "locationAddressStateMixedIdx", locationAddressState)
//    createMixedIdx(mgmt, "locationAddressPostCodeMixedIdx", locationAddressPostCode)
//


    objectPrivacyImpactAssessmentLabel = createVertexLabel(mgmt, "Object.Privacy_Impact_Assessment")
    objectPrivacyImpactAssessment0 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Description", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment1 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Start_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment2 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Delivery_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment3 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Risk_To_Individuals", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment4 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Intrusion_On_Privacy", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment5 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Risk_To_Corporation", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment6 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Risk_Of_Reputational_Damage", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment7 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Compliance_Check_Passed", String.class, org.janusgraph.core.Cardinality.SINGLE)

    objectPrivacyImpactAssessment8 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Form_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectPrivacyImpactAssessment9 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Form_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectPrivacyImpactAssessment10 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Form_Submission_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectPrivacyImpactAssessment11 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Form_Submission_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);

//    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx0", objectPrivacyImpactAssessment0)
    createMixedIdx(mgmt, "Object_Privacy_Impact_Assessment_Start_Date", objectPrivacyImpactAssessmentLabel, objectPrivacyImpactAssessment1, objectPrivacyImpactAssessment2, objectPrivacyImpactAssessment3, objectPrivacyImpactAssessment4, objectPrivacyImpactAssessment5, objectPrivacyImpactAssessment6, objectPrivacyImpactAssessment7, objectPrivacyImpactAssessment8, objectPrivacyImpactAssessment9, objectPrivacyImpactAssessment10, objectPrivacyImpactAssessment11)

//    createCompIdx(mgmt, "Object_Privacy_Impact_Assessment_Delivery_Date", objectPrivacyImpactAssessment2)
//    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx3", metadataType, objectPrivacyImpactAssessment3)
//    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx4", metadataType, objectPrivacyImpactAssessment4)
//    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx5", metadataType, objectPrivacyImpactAssessment5)
//    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx6", metadataType, objectPrivacyImpactAssessment6)
//    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx7", metadataType, objectPrivacyImpactAssessment7)

    objectAwarenessCampaignLabel = createVertexLabel(mgmt, "Object.Awareness_Campaign")
    objectAwarenessCampaignDescription = createProp(mgmt, "Object.Awareness_Campaign.Description", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAwarenessCampaignURL = createProp(mgmt, "Object.Awareness_Campaign.URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAwarenessCampaignStart_Date = createProp(mgmt, "Object.Awareness_Campaign.Start_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAwarenessCampaignStop_Date = createProp(mgmt, "Object.Awareness_Campaign.Stop_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)

    objectAwarenessCampaignFormProp01 = createProp(mgmt, "Object.Awareness_Campaign.Form_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectAwarenessCampaignFormProp02 = createProp(mgmt, "Object.Awareness_Campaign.Form_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectAwarenessCampaignFormProp03 = createProp(mgmt, "Object.Awareness_Campaign.Form_Submission_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectAwarenessCampaignFormProp04 = createProp(mgmt, "Object.Awareness_Campaign.Form_Submission_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);



    createMixedIdx(mgmt, "objectAwarenessCampaignDescriptionMixedIdx", objectAwarenessCampaignLabel, objectAwarenessCampaignDescription, objectAwarenessCampaignURL, objectAwarenessCampaignStart_Date, objectAwarenessCampaignStop_Date
            , objectAwarenessCampaignFormProp01
            , objectAwarenessCampaignFormProp02
            , objectAwarenessCampaignFormProp03
            , objectAwarenessCampaignFormProp04)

//    createMixedIdx(mgmt, "objectAwarenessCampaignURLMixedIdx", objectAwarenessCampaignURL)
//    createCompIdx(mgmt, "objectAwarenessCampaignStart_DateCompIdx", objectAwarenessCampaignStart_Date)
//    createCompIdx(mgmt, "objectAwarenessCampaignStop_DateCompIdx", objectAwarenessCampaignStop_Date)

    objectInsurancePolicyLabel         = createVertexLabel(mgmt, "Object.Insurance_Policy")
    objectInsurancePolicyNumber        = createProp(mgmt, "Object.Insurance_Policy.Number", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectInsurancePolicyType          = createProp(mgmt, "Object.Insurance_Policy.Type", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectInsurancePolicyStatus        = createProp(mgmt, "Object.Insurance_Policy.Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectInsurancePolicyProduct_Type  = createProp(mgmt, "Object.Insurance_Policy.Product_Type", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectInsurancePolicyProperty_Type = createProp(mgmt, "Object.Insurance_Policy.Property_Type", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectInsurancePolicyRenewal_Date  = createProp(mgmt, "Object.Insurance_Policy.Renewal_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE);
    objectInsurancePolicyFormProp01    = createProp(mgmt, "Object.Insurance_Policy.Form_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectInsurancePolicyFormProp02    = createProp(mgmt, "Object.Insurance_Policy.Form_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectInsurancePolicyFormProp03    = createProp(mgmt, "Object.Insurance_Policy.Form_Submission_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectInsurancePolicyFormProp04    = createProp(mgmt, "Object.Insurance_Policy.Form_Submission_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);



    createMixedIdx(mgmt, "objectInsurancePolicyMixedIdx"
            , objectInsurancePolicyLabel
            , objectInsurancePolicyNumber
            , objectInsurancePolicyType
            , objectInsurancePolicyStatus
            , objectInsurancePolicyProduct_Type
            , objectInsurancePolicyProperty_Type
            , objectInsurancePolicyRenewal_Date
            , objectInsurancePolicyFormProp01
            , objectInsurancePolicyFormProp02
            , objectInsurancePolicyFormProp03
            , objectInsurancePolicyFormProp04
    )






    objectLawfulBasisLabel = createVertexLabel(mgmt, "Object.Lawful_Basis")
    objectLawfulBasis0 = createProp(mgmt, "Object.Lawful_Basis.Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectLawfulBasis1 = createProp(mgmt, "Object.Lawful_Basis.Description", String.class, org.janusgraph.core.Cardinality.SINGLE)

    createMixedIdx(mgmt, "objectLawfulBasis0MixedIdx", objectLawfulBasisLabel, objectLawfulBasis0, objectLawfulBasis1)
//    createMixedIdx(mgmt, "objectLawfulBasis1MixedIdx", objectLawfulBasis1)

    objectPrivacyNoticeLabel = createVertexLabel(mgmt, "Object.Privacy_Notice")
    objectPrivacyNotice00 = createProp(mgmt, "Object.Privacy_Notice.Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice01 = createProp(mgmt, "Object.Privacy_Notice.Description", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice02 = createProp(mgmt, "Object.Privacy_Notice.Text", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice03 = createProp(mgmt, "Object.Privacy_Notice.Delivery_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice04 = createProp(mgmt, "Object.Privacy_Notice.Expiry_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice05 = createProp(mgmt, "Object.Privacy_Notice.URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice06 = createProp(mgmt, "Object.Privacy_Notice.Info_Collected", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice07 = createProp(mgmt, "Object.Privacy_Notice.Who_Is_Collecting", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice08 = createProp(mgmt, "Object.Privacy_Notice.How_Is_It_Collected", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice09 = createProp(mgmt, "Object.Privacy_Notice.Why_Is_It_Collected", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice10 = createProp(mgmt, "Object.Privacy_Notice.How_Will_It_Be_Used", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice11 = createProp(mgmt, "Object.Privacy_Notice.Who_Will_It_Be_Shared", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice12 = createProp(mgmt, "Object.Privacy_Notice.Effect_On_Individuals", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice13 = createProp(mgmt, "Object.Privacy_Notice.Likely_To_Complain", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice14 = createProp(mgmt, "Object.Privacy_Notice.Metadata.Create_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice15 = createProp(mgmt, "Object.Privacy_Notice.Metadata.Update_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNoticeFormProp01 = createProp(mgmt, "Object.Privacy_Notice.Form_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectPrivacyNoticeFormProp02 = createProp(mgmt, "Object.Privacy_Notice.Form_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectPrivacyNoticeFormProp03 = createProp(mgmt, "Object.Privacy_Notice.Form_Submission_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
    objectPrivacyNoticeFormProp04 = createProp(mgmt, "Object.Privacy_Notice.Form_Submission_Owner_Id", String.class, org.janusgraph.core.Cardinality.SINGLE);

    createMixedIdx(mgmt, "objectPrivacyNotice00MixedIdx", objectPrivacyNoticeLabel, objectPrivacyNotice00, objectPrivacyNotice05, objectPrivacyNotice06, objectPrivacyNotice07, objectPrivacyNotice08, objectPrivacyNotice09, objectPrivacyNotice10, objectPrivacyNotice11, objectPrivacyNotice12, objectPrivacyNotice13, objectPrivacyNotice14, objectPrivacyNotice15
            , objectPrivacyNoticeFormProp01
            , objectPrivacyNoticeFormProp02
            , objectPrivacyNoticeFormProp03
            , objectPrivacyNoticeFormProp04

    )
//    createMixedIdx(mgmt, "objectPrivacyNotice01MixedIdx", objectPrivacyNotice01)
//    createMixedIdx(mgmt, "objectPrivacyNotice02MixedIdx", objectPrivacyNotice02)
//    createMixedIdx(mgmt, "objectPrivacyNotice03MixedIdx", objectPrivacyNotice03)
//    createMixedIdx(mgmt, "objectPrivacyNotice04MixedIdx", objectPrivacyNotice04)
//    createMixedIdx(mgmt, "objectPrivacyNotice05MixedIdx", metadataType, objectPrivacyNotice05)
//    createMixedIdx(mgmt, "objectPrivacyNotice06MixedIdx", metadataType, objectPrivacyNotice06)
//    createMixedIdx(mgmt, "objectPrivacyNotice07MixedIdx", metadataType, objectPrivacyNotice07)
//    createMixedIdx(mgmt, "objectPrivacyNotice08MixedIdx", metadataType, objectPrivacyNotice08)
//    createMixedIdx(mgmt, "objectPrivacyNotice09MixedIdx", metadataType, objectPrivacyNotice09)
//    createMixedIdx(mgmt, "objectPrivacyNotice10MixedIdx", metadataType, objectPrivacyNotice10)
//    createMixedIdx(mgmt, "objectPrivacyNotice11MixedIdx", metadataType, objectPrivacyNotice11)
//    createMixedIdx(mgmt, "objectPrivacyNotice12MixedIdx", metadataType, objectPrivacyNotice12)
//    createMixedIdx(mgmt, "objectPrivacyNotice13MixedIdx", metadataType, objectPrivacyNotice13)

    personEmployee = createVertexLabel(mgmt, "Person.Employee")
    personEmployee00 = createProp(mgmt, "Person.Employee.Role", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployee01 = createProp(mgmt, "Person.Employee.Is_GDPR_Role", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeDateOfBirth = createProp(mgmt, "Person.Employee.Date_Of_Birth", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeFullName = createProp(mgmt, "Person.Employee.Full_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeLastName = createProp(mgmt, "Person.Employee.Last_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeGender = createProp(mgmt, "Person.Employee.Gender", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeNationality = createProp(mgmt, "Person.Employee.Nationality", String.class, org.janusgraph.core.Cardinality.SET)
    personEmployeePlaceOfBirth = createProp(mgmt, "Person.Employee.Place_Of_Birth", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeReligion = createProp(mgmt, "Person.Employee.Religion", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeEthnicity = createProp(mgmt, "Person.Employee.Ethnicity", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeMaritalStatus = createProp(mgmt, "Person.Employee.Marital_Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeHeight = createProp(mgmt, "Person.Employee.Height", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeNameQualifier = createProp(mgmt, "Person.Employee.Name_Qualifier", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEmployeeTitle = createProp(mgmt, "Person.Employee.Title", String.class, org.janusgraph.core.Cardinality.SINGLE)

    createMixedIdx(mgmt, "personEmployeeMixedIdx", personEmployee, personEmployee00, personEmployee01, personEmployeeFullName, personEmployeeLastName, personEmployeeGender, personEmployeeNationality, personEmployeeDateOfBirth, personEmployeePlaceOfBirth, personEmployeeReligion, personEmployeeEthnicity, personEmployeeMaritalStatus, personEmployeeNameQualifier, personEmployeeTitle);
//    createMixedIdx(mgmt, "personEmployeeMixedIdx01", personEmployee01)
//    createCompIdx(mgmt, "personEmployeeCompositeIdx00", personEmployee00)
//    createCompIdx(mgmt, "personEmployeeCompositeIdx01", personEmployee01)


    edgeLabel = createEdgeLabel(mgmt, "Reports_To")
    edgeLabel = createEdgeLabel(mgmt, "Has_Parent_Or_Guardian")


    edgeLabel = createEdgeLabel(mgmt, "Uses_Email")
    edgeLabel = createEdgeLabel(mgmt, "Approved_Compliance_Check")
    edgeLabel = createEdgeLabel(mgmt, "Has_Credential")
    edgeLabel = createEdgeLabel(mgmt, "Has_Id_Card")
    edgeLabel = createEdgeLabel(mgmt, "Lives")
    edgeLabel = createEdgeLabel(mgmt, "Has_Lawful_Basis_On")
    edgeLabel = createEdgeLabel(mgmt, "Event.Training.Awareness_Campaign")
    edgeLabel = createEdgeLabel(mgmt, "Event.Training.Person")
    edgeLabel = createEdgeLabel(mgmt, "Has_Policy")

    if (!mgmt.containsGraphIndex("eventTrainingAwareness_CampaignIdx")) {
        try {
            mgmt.buildEdgeIndex(edgeLabel, "eventTrainingAwareness_CampaignIdx", Direction.BOTH, metadataType, metadataCreateDate);
        } catch (e) {
        }
    }

    edgeLabel = createEdgeLabel(mgmt, "Event.Training.Person")

    if (!mgmt.containsGraphIndex("eventTrainingPersonIdx")) {
        try {
            mgmt.buildEdgeIndex(edgeLabel, "eventTrainingPersonIdx", Direction.BOTH, metadataType, metadataCreateDate);
        } catch (e) {
        }
    }

    edgeLabel = createEdgeLabel(mgmt, "Has_Data_Procedures")
    edgeLabel = createEdgeLabel(mgmt, "Consent")
    edgeLabel = createEdgeLabel(mgmt, "Has_Privacy_Notice")
    edgeLabel = createEdgeLabel(mgmt, "Has_Server")
    edgeLabel = createEdgeLabel(mgmt, "Has_Ingestion_Event")
    edgeLabel = createEdgeLabel(mgmt, "Has_Form_Ingestion_Event")
    edgeLabel = createEdgeLabel(mgmt, "Has_Security_Group")
    edgeLabel = createEdgeLabel(mgmt, "Has_Security_Group_Connectivity")
    edgeLabel = createEdgeLabel(mgmt, "Has_Ingress_Peering")
    edgeLabel = createEdgeLabel(mgmt, "Has_Egress_Peering")
    edgeLabel = createEdgeLabel(mgmt, "Impacted_By_Data_Breach")
    edgeLabel = createEdgeLabel(mgmt, "Data_Impacted_By_Data_Breach")




    orgLabel = createVertexLabel(mgmt, "Person.Organisation")
    orgRegNumber = createProp(mgmt, "Person.Organisation.Registration_Number", String.class, org.janusgraph.core.Cardinality.SINGLE)
    orgType = createProp(mgmt, "Person.Organisation.Type", String.class, org.janusgraph.core.Cardinality.SET)
    orgName = createProp(mgmt, "Person.Organisation.Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    orgShortName = createProp(mgmt, "Person.Organisation.Short_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    orgTaxId = createProp(mgmt, "Person.Organisation.Tax_Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    orgSector = createProp(mgmt, "Person.Organisation.Sector", String.class, org.janusgraph.core.Cardinality.SET)
    orgPhone = createProp(mgmt, "Person.Organisation.Phone", String.class, org.janusgraph.core.Cardinality.SET)
    orgFax = createProp(mgmt, "Person.Organisation.Fax", String.class, org.janusgraph.core.Cardinality.SET)
    orgEmail = createProp(mgmt, "Person.Organisation.Email", String.class, org.janusgraph.core.Cardinality.SET)


    orgURL = createProp(mgmt, "Person.Organisation.URL", String.class, org.janusgraph.core.Cardinality.SET)

    orgCountry = createProp(mgmt, "Person.Organisation.orgCountrySet", String.class, org.janusgraph.core.Cardinality.SET)
    createMixedIdx(mgmt, "personOrgShortNameMixedIdx", orgLabel, orgShortName, orgCountry, orgName, orgRegNumber, orgURL)
//    createMixedIdx(mgmt, "personOrgCountryMixedIdx", metadataType, orgCountry)
//
//    createMixedIdx(mgmt, "personOrgNameMixedMixedIdx", metadataType, orgName)
//    createMixedIdx(mgmt, "personOrgRegNumberMixedIdx", metadataType, orgRegNumber)
//    createMixedIdx(mgmt, "personOrgURLMixedIdx",       metadataType, orgURL)


    orgLabel = createVertexLabel(mgmt, "Event.Consent")

    eventConsentDate = createProp(mgmt, "Event.Consent.Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    eventConsentStatus = createProp(mgmt, "Event.Consent.Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    eventConsentMetadataCreateDate = createProp(mgmt, "Event.Consent.Metadata.Create_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    eventConsentMetadataUpdateDate = createProp(mgmt, "Event.Consent.Metadata.Update_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    createMixedIdx(mgmt, "eventConsentStatusMtdMixedIdx", orgLabel, eventConsentStatus, eventConsentMetadataUpdateDate, eventConsentMetadataCreateDate, eventConsentDate)
//    createCompIdx(mgmt, "eventConsentDateCompIdx",  eventConsentDate)
    mgmt.commit();

}

