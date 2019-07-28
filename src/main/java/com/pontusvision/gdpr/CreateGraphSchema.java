package com.pontusvision.gdpr;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.*;
import org.janusgraph.core.attribute.Geo;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.example.GraphOfTheGodsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateGraphSchema {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateGraphSchema.class);

    public static void main(String[] args) {
//        MultiFileHierarchicalConfiguration conf = new MultiFileHierarchicalConfiguration();
        try {
//            conf.load(Thread.currentThread().getContextClassLoader()
//                    .getResourceAsStream("conf/janusgraph-hbase-es.properties"));

            String confFile = args.length == 0? "conf/janusgraph-hbase-es.properties" : args[0];
            JanusGraph graph = JanusGraphFactory.open(confFile);
            graph.tx().rollback(); // Never create new indices while a transaction is active

            JanusGraphManagement mgmt = graph.openManagement();
            PropertyKey metadataOwner = mgmt.makePropertyKey("Metadata.Owner").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataLineage = mgmt.makePropertyKey("Metadata.Lineage").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey metadataRedaction = mgmt.makePropertyKey("Metadata.Redaction").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataVersion = mgmt.makePropertyKey("Metadata.Version").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataCreateDate = mgmt.makePropertyKey("Metadata.CreateDate").dataType(Date.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataUpdateDate = mgmt.makePropertyKey("Metadata.UpdateDate").dataType(Date.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataStatus = mgmt.makePropertyKey("Metadata.Status").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey metadataOrigId = mgmt.makePropertyKey("Metadata.OrigId").dataType(UUID.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataGDPRStatus = mgmt.makePropertyKey("Metadata.GDPRStatus").dataType(String.class).cardinality(Cardinality.SINGLE).make();


            VertexLabel partyPersonLabel = mgmt.makeVertexLabel("Party.Person.Natural").make();

            PropertyKey partyPersonDateOfBirth = mgmt.makePropertyKey("Party.Person.Natural.DateOfBirth").dataType(Date.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonFullName = mgmt.makePropertyKey("Party.Person.Natural.FullName").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonEmailAddress = mgmt.makePropertyKey("Party.Person.Natural.EmailAddress").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonGender = mgmt.makePropertyKey("Party.Person.Natural.Gender").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonNationality = mgmt.makePropertyKey("Party.Person.Natural.Nationality").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey partyPersonPlaceOfBirth = mgmt.makePropertyKey("Party.Person.Natural.PlaceOfBirth").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonReligion = mgmt.makePropertyKey("Party.Person.Natural.Religion").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonEthnicity = mgmt.makePropertyKey("Party.Person.Natural.Ethnicity").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonMaritalStatus = mgmt.makePropertyKey("Party.Person.Natural.MaritalStatus").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonHeight = mgmt.makePropertyKey("Party.Person.Natural.Height").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonNameQualifier = mgmt.makePropertyKey("Party.Person.Natural.NameQualifier").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonTitle = mgmt.makePropertyKey("Party.Person.Natural.Title").dataType(String.class).cardinality(Cardinality.SINGLE).make();

            PropertyKey partyOrgRegNumber = mgmt.makePropertyKey("Party.Org.RegNumber").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyOrgType = mgmt.makePropertyKey("Party.Org.Type").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey partyOrgName = mgmt.makePropertyKey("Party.Org.Name").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyOrgShortName = mgmt.makePropertyKey("Party.Org.ShortName").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyOrgTaxId = mgmt.makePropertyKey("Party.Org.TaxId").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyOrgSector = mgmt.makePropertyKey("Party.Org.Sector").dataType(String.class).cardinality(Cardinality.SET).make();


            mgmt.makeEdgeLabel("Metadata.isNext");

            mgmt.makeEdgeLabel("Party.person.isParent");
            mgmt.makeEdgeLabel("Party.person.isPartner");
            mgmt.makeEdgeLabel("Party.person.isMarried");
            mgmt.makeEdgeLabel("Party.person.isDivorced");
            mgmt.makeEdgeLabel("Party.person.hasAlias");
            mgmt.makeEdgeLabel("Party.person.isWidowed");
            mgmt.makeEdgeLabel("Party.person.isFriend");
            mgmt.makeEdgeLabel("Party.person.isDating");
            mgmt.makeEdgeLabel("Party.person.worksAt");
            mgmt.makeEdgeLabel("Party.person.earns");

            mgmt.makeEdgeLabel("Party.person.lives");
            mgmt.makeEdgeLabel("Party.person.isAt");
            mgmt.makeEdgeLabel("Party.person.studied");
            mgmt.makeEdgeLabel("Party.person.has");
            mgmt.makeEdgeLabel("Party.person.owns");
            mgmt.makeEdgeLabel("Party.person.rents");

            VertexLabel objectCredentialLabel = mgmt.makeVertexLabel("Object.Credential").make();
            PropertyKey objectCredentialUsername = mgmt.makePropertyKey("Object.Credential.Username").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey objectCredentialQuestions = mgmt.makePropertyKey("Object.Credential.Questions").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey objectCredentialAnswers = mgmt.makePropertyKey("Object.Credential.Answers").dataType(String.class).cardinality(Cardinality.SET).make();


            VertexLabel partyOrgLabel = mgmt.makeVertexLabel("Party.Organization").make();
            VertexLabel objectDocument = mgmt.makeVertexLabel("Object.Document").make();
            VertexLabel objectAccount = mgmt.makeVertexLabel("Object.Account").make();

            VertexLabel locationPhysicalAddressLabel = mgmt.makeVertexLabel("Location.PhysicalAddress").make();
            VertexLabel locationIPAddressLabel = mgmt.makeVertexLabel("Location.IPAddress").make();
            VertexLabel locationPhoneNumberLabel = mgmt.makeVertexLabel("Location.PhoneNumber").make();

            VertexLabel eventLoginLabel = mgmt.makeVertexLabel("Event.Login").make();
            VertexLabel eventLogoutLabel = mgmt.makeVertexLabel("Event.Logout").make();


            mgmt.buildIndex("nameAndAge", Vertex.class).addKey(partyPersonFullName).addKey(partyPersonDateOfBirth).buildMixedIndex("search");
        /*
        The example above defines a mixed index containing the property keys name and age.
        The definition refers to the indexing backend name search so that JanusGraph knows which
        configured indexing backend it should use for this particular index. The search parameter
        specified in the buildMixedIndex call must match the second clause in the JanusGraph
        configuration definition like this: index.search.backend If the index was named solrsearch
        then the configuration definition would appear like this: index.solrsearch.backend.


        */

            mgmt.commit();


//            GraphTraversalSource g = graph.traversal();
//            if (g.V().count().next() == 0) {
//                // load the schema and graph data
//                GraphOfTheGodsFactory.load(graph);
//            }
//            Map<Object, Object> saturnProps = g.V().has("name", "saturn").valueMap(true).next();
//            LOGGER.info(saturnProps.toString());
//            List<Edge> places = g.E().has("place", Geo.geoWithin(Geoshape.circle(37.97, 23.72, 50))).toList();
//            LOGGER.info(places.toString());
//            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

