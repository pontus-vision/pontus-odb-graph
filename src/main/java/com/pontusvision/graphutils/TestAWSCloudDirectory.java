//package com.pontusvision.graphutils;
//
//import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
//import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.clouddirectory.CloudDirectoryClient;
//import software.amazon.awssdk.services.clouddirectory.model.*;
//
//import javax.xml.validation.Schema;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//
//public class TestAWSCloudDirectory {
//
//  public static void main(String[] args) throws Exception {
//    DefaultCredentialsProvider credentials = null;
//    try {
//      credentials =  DefaultCredentialsProvider.create(); // where is the file ?!
//    } catch (Exception e) {
//      throw new Exception(
//        "Cannot load the credentials from the credential profiles file. " +
//          "Please make sure that your credentials file is at the correct " +
//          "location, and is in valid format.",
//        e);
//    }
//
////  what about AWS Region ?!
////    Region oregon = Region.of("us-west-2");
//
//    CloudDirectoryClient client = CloudDirectoryClient.builder()
////    .region(oregon)
//      .credentialsProvider(credentials).build();
//
//    AttributeKey attributeKey = AttributeKey.builder().schemaArn("arn:aws:clouddirectory:::schema/managed/quick_start/1.0")
//      .facetName("DynamicObjectFacet")
//      .name("Person_Natural").build();
////    ObjectReference objectReference = ObjectReference.builder().selector("/").build();
//
////  create an index node hang under root node
////    client.createIndex(CreateIndexRequest.builder()
////      .directoryArn("arn:aws:clouddirectory:us-west-2:723510791261:directory/AVdQcSCGPkWAs4G9B9dMiD4")
////      .orderedIndexedAttributeList(attributeKey)
////      .isUnique(true)
////      .linkName("indices")
////      .parentReference(objectReference).build());
//
////  AWS New session
////    AwsSessionCredentials session = AwsSessionCredentials.create("AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY", "credentials?"); //?!?!
//
////    ApplySchemaResponse schemaResponse = client.applySchema( ApplySchemaRequest.builder().directoryArn("arn:aws:clouddirectory:us-west-2:723510791261:directory/AVdQcSCGPkWAs4G9B9dMiD4").publishedSchemaArn("arn:aws:clouddirectory:::schema/managed/quick_start/1.0").build());  // which schema is it applying ?!
////    schemaResponse.sdkHttpResponse().isSuccessful();
//
////    CreateFacetResponse resp = client.createFacet(CreateFacetRequest.builder().facetStyle(FacetStyle.fromValue("DYNAMIC")).build()); // is it "STATIC" or "DYNAMIC" or WHAT ?!
////    resp.sdkHttpResponse().isSuccessful();
////    AttributeKey attributeKey = AttributeKey.builder().schemaArn("arn:aws:clouddirectory:::schema/managed/quick_start/1.0").facetName("DynamicObjectFacet").name("DynamicObjectFacet").build();
////    client.createIndex(CreateIndexRequest.builder().orderedIndexedAttributeList(attributeKey).directoryArn("arn:aws:clouddirectory:us-west-2:723510791261:directory/AVdQcSCGPkWAs4G9B9dMiD4").orderedIndexedAttributeList("").build());
//
//    TypedAttributeValue attributeValue = TypedAttributeValue.fromStringValue("Leo");
//    AttributeKeyAndValue attrib = AttributeKeyAndValue.builder().key(attributeKey).value(attributeValue).build();
//
//    SchemaFacet schemaFacet = SchemaFacet.builder().facetName("DynamicObjectFacet").schemaArn("arn:aws:clouddirectory:::schema/managed/quick_start/1.0").build();
//
//    Collection<AttributeKeyAndValue> objectAttributeList = new ArrayList<>();
//    objectAttributeList.add(attrib);
//    ObjectReference objectReference = ObjectReference.builder().selector("/").build();
//
//
//    client.createObject(CreateObjectRequest.builder().directoryArn("arn:aws:clouddirectory:us-west-2:723510791261:directory/AVdQcSCGPkWAs4G9B9dMiD4")
//      .objectAttributeList(objectAttributeList)
//      .schemaFacets(schemaFacet)
////      .parentReference(objectReference).linkName("")
//      .build());
//
//
//    client.close();
//
//  }
//}
