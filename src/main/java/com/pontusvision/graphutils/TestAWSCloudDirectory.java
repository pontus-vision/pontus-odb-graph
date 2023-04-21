package com.pontusvision.graphutils;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.clouddirectory.CloudDirectoryClient;
import software.amazon.awssdk.services.clouddirectory.model.*;

import java.util.Collection;
import java.util.Collections;

public class TestAWSCloudDirectory {

  public static void main() throws Exception {
    DefaultCredentialsProvider credentials = null;
    try {
      credentials =  DefaultCredentialsProvider.create();
    } catch (Exception e) {
      throw new Exception(
        "Cannot load the credentials from the credential profiles file. " +
          "Please make sure that your credentials file is at the correct " +
          "location, and is in valid format.",
        e);
    }
    CloudDirectoryClient client = CloudDirectoryClient.builder().credentialsProvider(credentials).build();

    ApplySchemaResponse schemaResponse = client.applySchema( ApplySchemaRequest.builder().directoryArn("directory_arn").build());
    schemaResponse.sdkHttpResponse().isSuccessful();

    CreateFacetResponse resp = client.createFacet(CreateFacetRequest.builder().facetStyle(FacetStyle.fromValue("")).build());
    resp.sdkHttpResponse().isSuccessful();
    AttributeKey attributeKey = AttributeKey.builder().facetName("Person_Natural").name("Person_Natural_Full_Name").build();
    client.createIndex(CreateIndexRequest.builder().orderedIndexedAttributeList(attributeKey).build());

    TypedAttributeValue attributeValue = TypedAttributeValue.fromStringValue("Leo");
    AttributeKeyAndValue attrib = AttributeKeyAndValue.builder().key(attributeKey).value(attributeValue).build();

    client.createObject(CreateObjectRequest.builder().directoryArn("")
      .objectAttributeList((Collection<AttributeKeyAndValue>) Collections.singletonList(attrib))
      .build());

  }
}
