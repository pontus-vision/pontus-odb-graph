import com.pontusvision.utils.LocationAddress
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair
import org.apache.tinkerpop.gremlin.structure.Vertex

import java.text.SimpleDateFormat

def addRandomUserData(graph, g, pg_dob, pg_metadataController, pg_metadataProcessor, pg_metadataLineage, pg_metadataRedaction, pg_metadataVersion, pg_metadataStatus, pg_metadataGDPRStatus, pg_metadataLineageServerTag, pg_metadataLineageLocationTag, pg_login_username, pg_login_sha256, pg_id_name, pg_id_value, pg_name_first, pg_name_last, pg_gender, pg_nat, pg_name_title, pg_email, pg_location_street, pg_location_city, pg_location_state, pg_location_postcode) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()

    try {
        dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) item.get("pg_dob"))
    } catch (Throwable t) {
        try {
            dob = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) item.get("pg_dob"))

        }
        catch (Throwable t2) {
            dob = new Date("01/01/1666")

        }
    }

    trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open();
        }


        person = g.addV("Person.Natural").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Person.Natural").
                property("Metadata.Type.Person.Natural", "Person.Natural").
                property("Person.Natural.Full_Name", pg_name_first + " " + pg_name_last).
                property("Person.Natural.Last_Name", pg_name_last).
                property("Person.Natural.Gender", pg_gender).
                property("Person.Natural.Nationality", pg_nat).
                property("Person.Natural.Date_Of_Birth", dob).
                property("Person.Natural.Title", pg_name_title).next()

        email = g.addV("Object.Email_Address").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Object.Email_Address").
                property("Metadata.Type.Object.Email_Address", "Object.Email_Address").
                property("Object.Email_Address.Email", pg_email).next()


        credential = g.addV("Object.Credential").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Object.Credential").
                property("Metadata.Type.Object.Credential", "Object.Credential").
                property("Object.Credential.userId", pg_login_username).
                property("Object.Credential.login.sha256", pg_login_sha256).next()

        idCard = g.addV("Object.Identity_Card").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Object.Identity_Card").
                property("Metadata.Type.Object.Identity_Card", "Object.Identity_Card").
                property("Object.Identity_Card.id_name", pg_id_name).
                property("Object.Identity_Card.id_value", pg_id_value).next()


        location = g.addV("Location.Address").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Location.Address").
                property("Metadata.Type.Location.Address", "Location.Address").
                property("Location.Address.Street", pg_location_street).
                property("Location.Address.City", pg_location_city).
                property("Location.Address.State", pg_location_state).
                property("Location.Address.Post_Code", pg_location_postcode).next()





        g.addE("Uses_Email").from(person).to(email).next()
        g.addE("Has_Credential").from(person).to(credential).next()
        g.addE("Has_Id_Card").from(person).to(idCard).next()
        g.addE("Lives").from(person).to(location).next()
        trans.commit()

    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }
}

def dumpData(List<Map<String, String>> listOfMaps) {

    StringBuilder strBuilder = new StringBuilder()
    for (Map<String, String> item in listOfMaps) {

        strBuilder.append(item.toString())

    }
    return strBuilder.toString()
}

/*
 *
def listOfMaps = new LinkedList<HashMap<String,String>>()

def map1 = [pg_metadataProcessor: '',pg_metadataLineage:'', pg_metadataRedaction:'', pg_metadataVersion: 1, pg_metadataStatus:'' , pg_metadataGDPRStatus:'', pg_metadataLineageServerTag:'', pg_metadataLineageLocationTag:'',pg_metadataController:'controller123', "pg_gender":"male","pg_name_title":"mr","pg_name_first":"quoc","pg_name_last":"bastiaansen","pg_location_street":"7247 lucasbolwerk","pg_location_city":"epe","pg_location_state":"flevoland","pg_location_postcode":"92775","pg_email":"quoc.bastiaansen@example.com","pg_login_username":"heavybear983","pg_login_password":"writer","pg_login_salt":"avKgfb4e","pg_login_md5":"d918e55da9d17937c718b9f7688821cb","pg_login_sha1":"2cb2bba91deef25c64f15a6ac96d84c21c1189e3","pg_login_sha256":"2945552ed13c3cf2a845d2f6af78ad7bbb161569af8e08dc3bfa36a3fa09b6df","pg_dob":"1955-12-01 16:58:49","pg_registered":"2012-07-15 09:50:59","pg_phone":"(668)-056-6802","pg_cell":"(859)-113-0976","pg_id_name":"BSN","pg_id_value":"94173707","pg_picture_large":"https://randomuser.me/api/portraits/men/63.jpg","pg_picture_medium":"https://randomuser.me/api/portraits/med/men/63.jpg","pg_picture_thumbnail":"https://randomuser.me/api/portraits/thumb/men/63.jpg","pg_nat":"NL"]
def map2 = [pg_metadataProcessor: '',pg_metadataLineage:'', pg_metadataRedaction:'', pg_metadataVersion: 1, pg_metadataStatus:'' , pg_metadataGDPRStatus:'', pg_metadataLineageServerTag:'', pg_metadataLineageLocationTag:'',pg_metadataController:'controller123', "pg_gender":"male","pg_name_title":"mr","pg_name_first":"leo","pg_name_last":"martins","pg_location_street":"123 street name","pg_location_city":"London","pg_location_state":"London","pg_location_postcode":"e145hq","pg_email":"leo.martins@example.com","pg_login_username":"lmartins123","pg_login_password":"writer","pg_login_salt":"avKgfb4e","pg_login_md5":"d918e55da9d17937c718b9f7688821cb","pg_login_sha1":"2cb2bba91deef25c64f15a6ac96d84c21c1189e3","pg_login_sha256":"2945552ed13c3cf2a845d2f6af78ad7bbb161569af8e08dc3bfa36a3fa09b6df","pg_dob":"1959-12-01 16:58:49","pg_registered":"2016-07-15 09:50:59","pg_phone":"(668)-056-6802","pg_cell":"(859)-113-0976","pg_id_name":"BSN","pg_id_value":"94173707","pg_picture_large":"https://randomuser.me/api/portraits/men/61.jpg","pg_picture_medium":"https://randomuser.me/api/portraits/med/men/61.jpg","pg_picture_thumbnail":"https://randomuser.me/api/portraits/thumb/men/61.jpg","pg_nat":"NL"]

listOfMaps.add(map1);
listOfMaps.add(map2);

addCampaignAwarenessBulk(graph,g, listOfMaps) */

def addCampaignAwarenessBulk(graph, g, List<Map<String, String>> listOfMaps) {

    def metadataCreateDate = new Date()
    def metadataUpdateDate = new Date()
    def trans = graph.tx()
    long counter = 0;

    def awarenessCampaignId = -1L;
    try {
        if (!trans.isOpen()) {
            trans.open()
        }


        awarenessCampaign = g.V().has("Metadata.Type.Object.Awareness_Campaign", eq("Object.Awareness_Campaign"))

        if (awarenessCampaign.hasNext()) {
            awarenessCampaignId = awarenessCampaign.next().id();
        } else {
            awarenessCampaignId = g.addV("Object.Awareness_Campaign").
                    property("Metadata.Controller", "Controller").
                    property("Metadata.Processor", "Processor").
                    property("Metadata.Lineage", "https://trainingcourses.com").
                    property("Metadata.Redaction", "/data/protection/officer").
                    property("Metadata.Version", 1).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", "new").
                    property("Metadata.GDPR_Status", "n/a").
                    property("Metadata.Lineage_Server_Tag", "AWS_AAA").
                    property("Metadata.Lineage_Location_Tag", "GB").
                    property("Metadata.Type.Object.Awareness_Campaign", "Object.Awareness_Campaign").
                    property("Metadata.Type", "Object.Awareness_Campaign").
                    property("Object.Awareness_Campaign.Description", "GDPR Training Course Winter 2017").
                    property("Object.Awareness_Campaign.URL", "https://trainingcourses.com").
                    property("Object.Awareness_Campaign.Start_Date", metadataCreateDate).
                    property("Object.Awareness_Campaign.Stop_Date", metadataUpdateDate).
                    next().id();
        }

        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }


    def probabilities = [
            new org.apache.commons.math3.util.Pair<String, Double>("Link Sent", (Double) 25.0),
            new org.apache.commons.math3.util.Pair<String, Double>("Reminder Sent", (Double) 30.0),
            new org.apache.commons.math3.util.Pair<String, Double>("Failed", (Double) 3.0),
            new org.apache.commons.math3.util.Pair<String, Double>("Passed", (Double) 60.0),
            new org.apache.commons.math3.util.Pair<String, Double>("Second  Reminder", (Double) 45.0)]
    def distribution = new org.apache.commons.math3.distribution.EnumeratedDistribution<String>(probabilities.asList())

//        def roleTypesProbabilities = [
//                new Pair<String, Double>("Operations Manager", (Double) 10.0),
//                new Pair<String, Double>("Operations Engineer", (Double) 100.0),
//                new Pair<String, Double>("Operations Director", (Double) 1.0)]
//
//        def roleTypesDistribution = new EnumeratedDistribution<String>(roleTypesProbabilities.asList())


    for (Map<String, String> item in listOfMaps) {

        counter++;
        role = 'Operations Engineer';
        reportsTo = 'Operations Manager';

        if (counter == 1) {
            role = 'Data Protection Officer';
            reportsTo = 'CEO';

        } else if (counter <= 10) {
            role = 'Operations Director';
            reportsTo = 'Data Protection Officer';

        } else if (counter <= 50) {
            role = 'Operations Manager';
            reportsTo = 'Operations Director';

        }
        try {
            if (!trans.isOpen()) {
                trans.open()
            }



            try {
                dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) item.get("pg_dob"))
            } catch (Throwable t) {
                try {
                    dob = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) item.get("pg_dob"))

                }
                catch (Throwable t2) {
                    dob = new Date("01/01/1666")

                }
            }




            def person = g.addV("Person.Employee").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Person.Employee").
                    property("Metadata.Type.Person.Employee", "Person.Employee").
                    property("Person.Employee.Full_Name", item.get("pg_name_first") + " " + item.get("pg_name_last")).
                    property("Person.Employee.Last_Name", item.get("pg_name_last")).
                    property("Person.Employee.Gender", item.get("pg_gender")).
                    property("Person.Employee.Nationality", item.get("pg_nat")).
                    property("Person.Employee.Date_Of_Birth", dob).
                    property("Person.Employee.Role", role).
                    property("Person.Employee.Title", item.get("pg_name_title"))
                    .next();


            personId = person.id();





            trainingEvent = g.addV("Event.Training").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Event.Training").
                    property("Metadata.Type.Event.Training", "Event.Training").
                    property("Event.Training.Status", distribution.sample()).next()


            g.addE("Event.Training.Awareness_Campaign")
                    .from(trainingEvent)
                    .to(g.V(awarenessCampaignId).next())
                    .property("Metadata.Type", "Event.Training.Awareness_Campaign")
                    .property("Metadata.Create_Date", metadataCreateDate)
                    .next()


            person = g.V(personId).next();

            g.addE("Event.Training.Person")
                    .from(trainingEvent)
                    .to(person)
                    .property("Metadata.Type", "Event.Training.Awareness_Campaign")
                    .property("Metadata.Create_Date", metadataCreateDate)
                    .next()

            if (counter > 1) {
                person = g.V(personId).next();

                try {
                    boss = g.V()
                            .has('Person.Employee.Role', eq(reportsTo))
                            .order()
                            .by(shuffle)
                            .range(0, 1)
                            .next();

                    g.addE("Reports_To").from(person).to(boss).next();
                } catch (e) { /* ignore */
                }
            }



            trans.commit()
        } catch (Throwable t) {
            trans.rollback()
            throw t
        } finally {
            trans.close()
        }


    }


    return counter;


}


def addRandomUserDataBulk(graph, g, List<Map<String, String>> listOfMaps) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open()

        }


        for (Map<String, String> item in listOfMaps) {


            try {
                dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) item.get("pg_dob"))
            } catch (Throwable t) {
                try {
                    dob = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) item.get("pg_dob"))

                }
                catch (Throwable t2) {
                    dob = new Date("01/01/1666")

                }
            }




            person = g.addV("Person.Natural").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Person.Natural").
                    property("Metadata.Type.Person.Natural", "Person.Natural").
                    property("Person.Natural.Full_Name", item.get("pg_name_first") + " " + item.get("pg_name_last")).
                    property("Person.Natural.Last_Name", item.get("pg_name_last")).
                    property("Person.Natural.Gender", item.get("pg_gender")).
                    property("Person.Natural.Nationality", item.get("pg_nat")).
                    property("Person.Natural.Date_Of_Birth", dob).
                    property("Person.Natural.Title", item.get("pg_name_title")).next()


            email = g.addV("Object.Email_Address").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Email_Address").
                    property("Metadata.Type.Object.Email_Address", "Object.Email_Address").
                    property("Object.Email_Address.Email", item.get("pg_email")).next()


            credential = g.addV("Object.Credential").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Credential").
                    property("Metadata.Type.Object.Credential", "Object.Credential").
                    property("Object.Credential.User_Id", item.get("pg_login_username")).
                    property("Object.Credential.Login_SHA256", item.get("pg_login_sha256")).next()

            idCard = g.addV("Object.Identity_Card").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Identity_Card").
                    property("Metadata.Type.Object.Identity_Card", "Object.Identity_Card").
                    property("Object.Identity_Card.Id_Name", item.get("pg_id_name")).
                    property("Object.Identity_Card.Id_Value", item.get("pg_id_value")).next()


            location = g.addV("Location.Address").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Location.Address").
                    property("Metadata.Type.Location.Address", "Location.Address").
                    property("Location.Address.Street", item.get("pg_location_street")).
                    property("Location.Address.City", item.get("pg_location_city")).
                    property("Location.Address.State", item.get("pg_location_state")).
                    property("Location.Address.Post_Code", item.get("pg_location_postcode")).next()




            g.addE("Uses_Email").from(person).to(email).next()
            g.addE("Has_Credential").from(person).to(credential).next()
            g.addE("Has_Id_Card").from(person).to(idCard).next()
            g.addE("Lives").from(person).to(location).next()
        }



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }
}

/*
{
        "Customer ID": "1010100110"
		"First_Name": " ROZELL",
		"Last_Name": "COLEMAN ",
		"Adress": "26 Silverweed Rd, Chatham ME5 0QU, UK",
		"Post_Code": "ME5 0QU",
		"DateofBirth": "8/16/60",
		"Sex": "Male",
		"Maritial_Status": "Married",
		"Phone_Number": "(0189) 188871",
		"Email_address": "rozell@hotmail.com",
		"Permisssion_to_Contact": "Yes",
		"Policynumber": "10333345",
		"PolicyType": "Renewable",
		"PolicyStatus": "Open",
		"ProspectStatus": "Active",
		"RenewalDate": "10/11/18",
		"ClientManager": "SYTE",
		"TypeOfinsurance": "Life",
		"PropertyType": null,
		"Vehicle_Number": null
	}
 */


def ingestCRMData(graph, g, List<Map<String, String>> listOfMaps, StringBuffer sb = null) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open()

        }


        for (Map<String, String> item in listOfMaps) {

            sb?.append("\nin ingestCRMData(); Processing $item");
            Date dob


            String dobRawStr = item.get("pg_DateofBirth");
            try {
                dob = dobRawStr as Date //; new SimpleDateFormat("yyyy-MM-dd").parse((String) dobRawStr)
            } catch (Throwable t) {
                try {
                    dob = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) item.get("pg_dob"))

                }
                catch (Throwable t2) {
                    dob = new Date("01/01/1666")

                }
            }

            sb?.append("\n\nConverted $dobRawStr to $dob")

            String renewalDateRawStr = item.get("pg_RenewalDate")
            Date renewalDate
            try {
                renewalDate = renewalDateRawStr as Date
            }
            catch (Throwable t2) {
                try {

                    renewalDate = new SimpleDateFormat("dd/MM/yyyy").parse((String) renewalDateRawStr)
                } catch (Throwable t) {
                    renewalDate = new Date("01/01/1666")
                }
            }

            def gender = item.get("pg_Sex")?.toUpperCase() ?: "MALE";
            def title = item.get("pg_name_title") ?: (gender.startsWith("M") ? "MR" : "MS")
            def nationality = (item.get("pg_nat"))?.toUpperCase() ?: "GB"

            def emailStr = item.get("pg_Email_address")?.toLowerCase()


            def customerId = item.get("pg_Customer_ID")


            sb?.append("\n Looking for  existing person based on the $customerId ")

            def personTrav = g.V().has("Person.Natural.Customer_ID", customerId)

            def person = null;
            if (personTrav.hasNext()) {
                person = personTrav.next()
                sb?.append("\n Found  existing person based on the $customerId ")

            } else {
                person = g.addV("Person.Natural").
                        property("Metadata.Controller", item.get("pg_metadataController")).
                        property("Metadata.Processor", item.get("pg_metadataProcessor")).
                        property("Metadata.Lineage", item.get("pg_metadataLineage")).
                        property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                        property("Metadata.Version", item.get("pg_metadataVersion")).
                        property("Metadata.Create_Date", metadataCreateDate).
                        property("Metadata.Update_Date", metadataUpdateDate).
                        property("Metadata.Status", item.get("pg_metadataStatus")).
                        property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                        property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                        property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                        property("Metadata.Type", "Person.Natural").
                        property("Metadata.Type.Person.Natural", "Person.Natural").
                        property("Person.Natural.Full_Name", (item.get("pg_First_Name") + " " + item.get("pg_Last_Name"))?.toUpperCase()).
                        property("Person.Natural.Last_Name", item.get("pg_Last_Name")?.toUpperCase()).
                        property("Person.Natural.Gender", gender).
                        property("Person.Natural.Nationality", nationality).
                        property("Person.Natural.Date_Of_Birth", dob).
                        property("Person.Natural.Customer_ID", customerId).
                        property("Person.Natural.Title", title).next()

            }


            def email = null

            if (emailStr) {

                def emailTrav = g.V().has("Object.Email_Address.Email")

                if (emailTrav.hasNext()) {
                    email = emailTrav.next()
                } else {
                    email = g.addV("Object.Email_Address").
                            property("Metadata.Controller", item.get("pg_metadataController")).
                            property("Metadata.Processor", item.get("pg_metadataProcessor")).
                            property("Metadata.Lineage", item.get("pg_metadataLineage")).
                            property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                            property("Metadata.Version", item.get("pg_metadataVersion")).
                            property("Metadata.Create_Date", metadataCreateDate).
                            property("Metadata.Update_Date", metadataUpdateDate).
                            property("Metadata.Status", item.get("pg_metadataStatus")).
                            property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                            property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                            property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                            property("Metadata.Type", "Object.Email_Address").
                            property("Metadata.Type.Object.Email_Address", "Object.Email_Address").
                            property("Object.Email_Address.Email", emailStr).next()
                }

            }

            def postCode = item.get("pg_Post_Code")

            def address = item.get("pg_Address")



            def location = null
            sb?.append("\n Looking for  existing location based on the $postCode and $address")

            LocationAddress addr = LocationAddress.fromString(address);



            def locationTrav = g.V().has("Location.Address.Post_Code", postCode).has("Location.Address.Full_Address", address)






            if (locationTrav.hasNext()) {
                sb?.append("\n found existing location based on the $postCode and $address")
                location = locationTrav.next()
            } else {
                sb?.append("\n DID NOT FIND an existing location based on the $postCode and $address")


                locationTrav = g.addV("Location.Address").
                        property("Metadata.Controller", item.get("pg_metadataController")).
                        property("Metadata.Processor", item.get("pg_metadataProcessor")).
                        property("Metadata.Lineage", item.get("pg_metadataLineage")).
                        property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                        property("Metadata.Version", item.get("pg_metadataVersion")).
                        property("Metadata.Create_Date", metadataCreateDate).
                        property("Metadata.Update_Date", metadataUpdateDate).
                        property("Metadata.Status", item.get("pg_metadataStatus")).
                        property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                        property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                        property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                        property("Metadata.Type", "Location.Address").
                        property("Metadata.Type.Location.Address", "Location.Address").
                        property("Location.Address.Full_Address", address).
                        property("Location.Address.Post_Code", item.get("pg_Post_Code"))



                locationTrav = addr.addPropsToGraphTraverser(locationTrav, "Location.Address.parser.", sb)
                location = locationTrav.next()

                // property("Location.Address.Street", item.get("pg_location_street")).
                // property("Location.Address.City", item.get("pg_location_city")).
                // property("Location.Address.State", item.get("pg_location_state")).


            }


            if (person && email) {
                sb?.append("\nadding email to person link ")
                g.addE("Uses_Email").from(person).to(email).next()

            }
            if (person && location) {
                sb?.append("\nadding person to location link ")

                g.addE("Lives").from(person).to(location).next()

            }
        }



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }
}


def addRandomDataProcedures(graph, g) {

    def trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open()

        }

        def randVal = new Random();
        def randValK = randVal.nextInt(5);

        def oneWeekInMs = 3600000 * 24 * 7;
        def eighteenWeeks = oneWeekInMs * 18;



        def probabilitiesRequestType = [
                new Pair<String, Double>("Automated", (Double) 97.0),
                new Pair<String, Double>("Manual", (Double) 3.0)];
        def distributionRequestType = new EnumeratedDistribution<String>(probabilitiesRequestType.asList());


        def types = new String[6];

        types[0] = "Person.Natural";
        types[1] = "Object.Email_Address";
        types[2] = "Object.Credential";
        types[3] = "Object.Identity_Card";
        types[4] = "Event.Consent";
        types[5] = "Event.Subject_Access_Request";


        for (def i = 0; i < types.length; i++) {
            def typeStr = types[i];

            props = g.V().has('Metadata.Type' + "." + typeStr, eq(typeStr)).range(0, 1).properties().key().findAll {
                (!it.startsWith('Metadata'))
            }


            for (def j = 0; j < props.size(); j++) {


                def propStr = props.get(j);

                def createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks)
                def updateMillis = createMillis + (long) (randVal.nextDouble() * eighteenWeeks)
                def metadataCreateDate = new Date((long) createMillis)
                def metadataUpdateDate = new Date((long) updateMillis)

                def dp = g.addV("Object.Data_Procedures").
                        property("Metadata.Redaction", "/dataprotectionofficer/aaa").
                        property("Metadata.Version", "1").
                        property("Metadata.Create_Date", metadataCreateDate).
                        property("Metadata.Update_Date", metadataUpdateDate).
                        property("Metadata.Lineage_Server_Tag", "AWS EUR1").
                        property("Metadata.Lineage_Location_Tag", "GB").
                        property("Metadata.Type", "Object.Data_Procedures").
                        property("Metadata.Type.Object.Data_Procedures", "Object.Data_Procedures").
                        property("Object.Data_Procedures.Type", typeStr.replaceAll('[_.]', ' ')).
                        property("Object.Data_Procedures.Property", propStr.replaceAll('[_.]', ' ')).
                        property("Object.Data_Procedures.Delete_URL", 'https://api-gateway/delete-' + propStr.toLowerCase()).
                        property("Object.Data_Procedures.Delete_Mechanism", distributionRequestType.sample()).
                        property("Object.Data_Procedures.Update_URL", 'https://api-gateway/update-' + propStr.toLowerCase()).
                        property("Object.Data_Procedures.Update_Mechanism", distributionRequestType.sample()).
                        next()

                for (def k = 0; k < randValK; k++) {
                    def pia = g.V().has('Metadata.Type.Object.Privacy_Impact_Assessment', eq('Object.Privacy_Impact_Assessment')).order().by(shuffle).range(0, 1).next()
                    g.addE("Has_Data_Procedures").from(pia).to(dp).next()
                }
            }


        }








        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }


}


def addRandomDataBreachEvents(graph, g) {

    def trans = graph.tx()
    try {

        def randVal = new Random()
        def randVal1 = randVal.nextInt(30)

        def oneWeekInMs = 3600000 * 24 * 7
        def eighteenWeeks = oneWeekInMs * 18

        def probabilitiesStatus = [
                new Pair<String, Double>("New", (Double) 25.0),
                new Pair<String, Double>("Suspect Internal Theft", (Double) 5.0),
                new Pair<String, Double>("Suspect External Theft", (Double) 5.0),
                new Pair<String, Double>("Open", (Double) 25.0),
                new Pair<String, Double>("Resolved", (Double) 45.0)]
        def distributionStatus = new EnumeratedDistribution<String>(probabilitiesStatus.asList())

        def probabilitiesSource = [
                new Pair<String, Double>("Service Now", (Double) 90.0),
                new Pair<String, Double>("Cloud Watch", (Double) 30.0),
                new Pair<String, Double>("CSOC", (Double) 30.0),
                new Pair<String, Double>("External News ", (Double) 23.0)]
        def distributionSource = new EnumeratedDistribution<String>(probabilitiesSource.asList())

        def probabilitiesImpact = [
                new Pair<String, Double>("Data Lost", (Double) 25.0),
                new Pair<String, Double>("OS Patch Required", (Double) 45.0),
                new Pair<String, Double>("Application Patch Required", (Double) 45.0),
                new Pair<String, Double>("Network Patch Required", (Double) 45.0),
                new Pair<String, Double>("Customer Data Stolen (External)", (Double) 3.0),
                new Pair<String, Double>("Customer Data Stolen (Internal)", (Double) 3.0),
                new Pair<String, Double>("Admin Credentials Stolen", (Double) 3.0),
                new Pair<String, Double>("Security Keys Compromised", (Double) 3.0),
                new Pair<String, Double>("False Alert", (Double) 3.0),
                new Pair<String, Double>("No Impact", (Double) 3.0)];
        def distributionImpact = new EnumeratedDistribution<String>(probabilitiesImpact.asList())



        for (def i = 0; i < randVal1; i++) {

            def createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks)
            def updateMillis = createMillis + (long) (randVal.nextDouble() * eighteenWeeks)
            def metadataCreateDate = new Date((long) createMillis)
            def metadataUpdateDate = new Date((long) updateMillis)

            /*
                eventDataBreachLabel = createVertexLabel(mgmt, "Event.Data_Breach");
                eventDataBreachProp00 = createProp(mgmt, "Event.Data_Breach.Id", String.class, org.janusgraph.core.Cardinality.SINGLE);
                eventDataBreachProp01 = createProp(mgmt, "Event.Data_Breach.Description", String.class, org.janusgraph.core.Cardinality.SINGLE);
                eventDataBreachProp02 = createProp(mgmt, "Event.Data_Breach.Status", String.class, org.janusgraph.core.Cardinality.SINGLE);
                eventDataBreachProp03 = createProp(mgmt, "Event.Data_Breach.Source", String.class, org.janusgraph.core.Cardinality.SINGLE);
                eventDataBreachProp04 = createProp(mgmt, "Event.Data_Breach.Impact", String.class, org.janusgraph.core.Cardinality.SINGLE);


             */
            trans = graph.tx();

            if (!trans.isOpen()) {
                trans.open()

            }

            def status = distributionStatus.sample();
            def source = distributionSource.sample();
            def impact = distributionImpact.sample();
            def dataBreachVid = g.addV("Event.Data_Breach").
                    property("Metadata.Lineage", "Random generator").
                    property("Metadata.Redaction", "/data/protection/officer").
                    property("Metadata.Version", "1").
                    property("Event.Data_Breach.Metadata.Create_Date", metadataCreateDate).
                    property("Event.Data_Breach.Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.GDPR_Status", "Data Breach").
                    property("Metadata.Type", "Event.Data_Breach").
                    property("Metadata.Type.Event.Data_Breach", "Event.Data_Breach").
                    property("Event.Data_Breach.Status", status).
                    property("Event.Data_Breach.Source", source).
                    property("Event.Data_Breach.Impact", impact).
                    property("Event.Data_Breach.Id", 'DBE ' + source + i).
                    property("Event.Data_Breach.Description", "Data Breach event reported from " + source).
                    next().id();

            trans.commit();

            trans.close();

            trans = graph.tx();
            if (!trans.isOpen()) {
                trans.open()

            }

            def numServersImpacted = randVal.nextInt(10);
            for (def j = 0; j < numServersImpacted; j++) {
                def dataBreach = g.V(dataBreachVid).next();

                def awsInstance = g.V().has('Metadata.Type.Object.AWS_Instance', eq('Object.AWS_Instance')).order().by(shuffle).range(0, 1).next()
                g.addE("Impacted_By_Data_Breach").from(awsInstance).to(dataBreach).next()


            }
            trans.commit();


        }


    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        if (trans.isOpen()) {
            trans.close()

        }
    }

    try {


        def event = g.V().has('Metadata.Type.Event.Data_Breach', eq('Event.Data_Breach')).order().by(shuffle).range(0, 1).next();
        def tx = graph.tx();
        if (!tx.isOpen()) {
            tx.open();
        }

        for (def i = 0; i < 10000; i++) {
            g.addE('Data_Impacted_By_Data_Breach').from(g.V().has('Metadata.Type.Person.Natural', eq('Person.Natural')).order().by(shuffle).range(0, 1)).to(event).next();
        }
        tx.commit();
        tx.close();
    } catch (Throwable t) {

    }

}


def addRandomSARs(graph, g) {

    def trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open()

        }

        def randVal = new Random()
        def randVal1 = randVal.nextInt(30)

        def oneWeekInMs = 3600000 * 24 * 7
        def eighteenWeeks = oneWeekInMs * 18


        def probabilitiesStatus = [
                new Pair<String, Double>("New", (Double) 25.0),
                new Pair<String, Double>("Acknowledged", (Double) 30.0),
                new Pair<String, Double>("Reviewed ", (Double) 3.0),
                new Pair<String, Double>("Denied", (Double) 60.0),
                new Pair<String, Double>("Completed", (Double) 45.0)]
        def distributionStatus = new EnumeratedDistribution<String>(probabilitiesStatus.asList())

        def probabilitiesRequestType = [
                new Pair<String, Double>("Read", (Double) 90.0),
                new Pair<String, Double>("Update", (Double) 30.0),
                new Pair<String, Double>("Delete ", (Double) 23.0)]
        def distributionRequestType = new EnumeratedDistribution<String>(probabilitiesRequestType.asList())



        for (def i = 0; i < randVal1; i++) {

            def createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks)
            def updateMillis = createMillis + (long) (randVal.nextDouble() * eighteenWeeks)
            def metadataCreateDate = new Date((long) createMillis)
            def metadataUpdateDate = new Date((long) updateMillis)

            def stat = distributionStatus.sample()
            def sar = g.addV("Event.Subject_Access_Request").
                    property("Metadata.Controller", "ABC INC").
                    property("Metadata.Processor", "ABC INC").
                    property("Metadata.Lineage", "Random generator").
                    property("Metadata.Redaction", "/dataprotectionofficer/aaa").
                    property("Metadata.Version", "1").
                    property("Event.Subject_Access_Request.Metadata.Create_Date", metadataCreateDate).
                    property("Event.Subject_Access_Request.Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", stat).
                    property("Metadata.GDPR_Status", "SAR").
                    property("Metadata.Lineage_Server_Tag", "AWS EUR1").
                    property("Metadata.Lineage_Location_Tag", "GB").
                    property("Metadata.Type", "Event.Subject_Access_Request").
                    property("Metadata.Type.Event.Subject_Access_Request", "Event.Subject_Access_Request").
                    property("Event.Subject_Access_Request.Status", stat).
                    property("Event.Subject_Access_Request.Request_Type", distributionRequestType.sample()).
                    next()


            def employee = g.V().has('Metadata.Type.Person.Employee', eq('Person.Employee')).order().by(shuffle).range(0, 1).tryNext()

            def person = g.V().has('Metadata.Type.Person.Natural', eq('Person.Natural')).order().by(shuffle).range(0, 1).tryNext()


            if (person.isPresent()){
                g.addE("Made_SAR_Request").from(person.get()).to(sar).next()

            }
            if (employee.isPresent()){
                g.addE("Assigned_SAR_Request").from(employee.get()).to(sar).next()

            }


        }








        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }


}


def addRandomChildUserDataBulk(graph, g, List<Map<String, String>> listOfMaps) {

    metadataCreateDate = new Date();
    metadataUpdateDate = new Date();
    trans = graph.tx()
    try {

        if (!trans.isOpen())
            trans.open()

        randVal = new Random()

        long oneYearInMs = 3600000L * 24L * 365L
        long eighteenYearsInMs = oneYearInMs * 18L

        long ageThresholdMs = (long) (System.currentTimeMillis() - (3600000L * 24L * 365L * 18L));
        def dateThreshold = new java.util.Date(ageThresholdMs);


        for (Map<String, String> item in listOfMaps) {


            try {

                dob = new Date(System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenYearsInMs))
            } catch (Throwable t) {
                dob = new Date("01/01/1666")
            }




            person = g.addV("Person.Natural").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Person.Natural").
                    property("Metadata.Type.Person.Natural", "Person.Natural").
                    property("Person.Natural.Full_Name", item.get("pg_name_first") + " " + item.get("pg_name_last")).
                    property("Person.Natural.Last_Name", item.get("pg_name_last")).
                    property("Person.Natural.Gender", item.get("pg_gender")).
                    property("Person.Natural.Nationality", item.get("pg_nat")).
                    property("Person.Natural.Date_Of_Birth", dob).
                    property("Person.Natural.Title", item.get("pg_name_title")).next()


            email = g.addV("Object.Email_Address").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Email_Address").
                    property("Metadata.Type.Object.Email_Address", "Object.Email_Address").
                    property("Object.Email_Address.Email", item.get("pg_email")).next()


            credential = g.addV("Object.Credential").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Credential").
                    property("Metadata.Type.Object.Credential", "Object.Credential").
                    property("Object.Credential.User_Id", item.get("pg_login_username")).
                    property("Object.Credential.Login_SHA256", item.get("pg_login_sha256")).next()

            idCard = g.addV("Object.Identity_Card").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Identity_Card").
                    property("Metadata.Type.Object.Identity_Card", "Object.Identity_Card").
                    property("Object.Identity_Card.Id_Name", item.get("pg_id_name")).
                    property("Object.Identity_Card.Id_Value", item.get("pg_id_value")).next()


            location = g.addV("Location.Address").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Location.Address").
                    property("Metadata.Type.Location.Address", "Location.Address").
                    property("Location.Address.Street", item.get("pg_location_street")).
                    property("Location.Address.City", item.get("pg_location_city")).
                    property("Location.Address.State", item.get("pg_location_state")).
                    property("Location.Address.Post_Code", item.get("pg_location_postcode")).next();


            parentOrGuardian = g.V()
                    .has('Metadata.Type.Person.Natural', eq('Person.Natural'))
                    .where(__.values('Person.Natural.Date_Of_Birth').is(lt(dateThreshold)))
                    .order().by(shuffle).range(0, 1).next();




            g.addE("Uses_Email").from(person).to(email).next()
            g.addE("Has_Credential").from(person).to(credential).next()
            g.addE("Has_Id_Card").from(person).to(idCard).next()
            g.addE("Lives").from(person).to(location).next()

            g.addE("Has_Parent_Or_Guardian").from(person).to(parentOrGuardian).next();
        }



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }
}


def addDataBulk(graph, g, List<Map<String, String>> listOfMaps) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open()

        }

        randVal = new Random()

        def oneYearInMs = 3600000 * 24 * 365
        def eighteenYearsInMs = oneYearInMs * 18


        for (Map<String, String> item in listOfMaps) {

        }
        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }
}

def __addConsentForPrivacyNotice(graph, g, Vertex privNoticeVertex) {

    def randVal = new Random()
    def randVal1 = randVal.nextInt(30)

    def oneWeekInMs = 3600000 * 24 * 7
    def eighteenWeeks = oneWeekInMs * 18


    def probabilitiesStatus = [
            new Pair<String, Double>("Consent Pending", (Double) 25.0),
            new Pair<String, Double>("Consent", (Double) 30.0),
            new Pair<String, Double>("No Consent ", (Double) 3.0)]
    def distributionStatus = new EnumeratedDistribution<String>(probabilitiesStatus.asList())



    for (def i = 0; i < randVal1; i++) {

        def createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks)
        def updateMillis = createMillis + (long) (randVal.nextDouble() * eighteenWeeks)
        def metadataCreateDate = new Date((long) createMillis)
        def metadataUpdateDate = new Date((long) updateMillis)

        /*
            orgLabel = createVertexLabel(mgmt, "Event.Consent")

    eventConsentDate = createProp(mgmt, "Event.Consent.Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    eventConsentStatus = createProp(mgmt, "Event.Consent.Status", String.class, org.janusgraph.core.Cardinality.SINGLE)

         */



        def stat = distributionStatus.sample()
        def consent = g.addV("Event.Consent").
                property("Metadata.Controller", "ABC INC").
                property("Metadata.Processor", "ABC INC").
                property("Metadata.Lineage", "Random generator").
                property("Metadata.Redaction", "/dataprotectionofficer/aaa").
                property("Metadata.Version", "1").
                property("Event.Consent.Metadata.Create_Date", metadataCreateDate).
                property("Event.Consent.Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", stat).
                property("Metadata.GDPR_Status", "SAR").
                property("Metadata.Lineage_Server_Tag", "AWS EUR1").
                property("Metadata.Lineage_Location_Tag", "GB").
                property("Metadata.Type", "Event.Consent").
                property("Event.Consent.Status", stat).
                property("Event.Consent.Date", metadataUpdateDate).
                next()

//        def employee = g.V().has('Metadata.Type',eq('Person.Employee')).order().by(shuffle).range(0,1).next()

        def person = g.V().has('Metadata.Type', eq('Person.Natural')).order().by(shuffle).range(0, 1).next()


        g.addE("Consent").from(person).to(consent).next()
        g.addE("Has_Privacy_Notice").from(consent).to(privNoticeVertex).next()


    }

//    g.V().has("Metadata.Type", eq("Person.Natural")).as("people")
//            .addE("Consent").property("Consent.Date", new Date())
//            .from("people").to(privNoticeVertex).next()


}


def __addMoU(graph, g, Vertex pia) {

    def randVal = new Random();

    def numMoUs = randVal.nextInt(5);

    for (def k = 0; k < numMoUs; k++) {

        def mou = g.addV("Object.MoU").
                property("Object.MoU.Id", new Random().nextLong()).
                property("Object.MoU.Description", "This is an MoU").
                property("Object.MoU.Status", "Active").
                property("Object.MoU.Link", "https://www.abcinc.com/mou").next();


        try {

            def org = g.V().has('Metadata.Type.Person.Organisation', eq('Person.Organisation'))
                    .order().by(shuffle).range(0, 1).next()

            def useTheirData = randVal.nextBoolean();
            if (useTheirData) {
                g.addE("Use_Their_Data").from(pia).to(mou).next()
                g.addE("Use_Their_Data").from(mou).to(org).next()


            } else {
                g.addE("Use_Our_Data").from(pia).to(mou).next()
                g.addE("Use_Our_Data").from(mou).to(org).next()

            }
        } catch (Throwable t) {

        }
    }


}

def __addPrivacyImpactAssessment(graph, g, Vertex privNoticeVertex) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()

//    trans = graph.tx()
//    try {
//        trans.open()


    pia = g.addV("Object.Privacy_Impact_Assessment").
            property("Metadata.Controller", "ABC Inc").
            property("Metadata.Processor", "ABC Inc").
            property("Metadata.Lineage", "oracle://jdbc://oracledb.com").
            property("Metadata.Redaction", "/data/protection/officer").
            property("Metadata.Version", "1").
            property("Metadata.Create_Date", metadataCreateDate).
            property("Metadata.Update_Date", metadataUpdateDate).
            property("Metadata.Status", "New").
            property("Metadata.GDPR_Status", "n/a").
            property("Metadata.Lineage_Server_Tag", "AWS_EUR_HOST3").
            property("Metadata.Lineage_Location_Tag", "GB").
            property("Metadata.Type", "Object.Privacy_Impact_Assessment").
            property("Metadata.Type.Object.Privacy_Impact_Assessment", "Object.Privacy_Impact_Assessment").
            property("Object.Privacy_Impact_Assessment.Description", "PIA for project xyz.").
            property("Object.Privacy_Impact_Assessment.Start_Date", new Date()).
            property("Object.Privacy_Impact_Assessment.Delivery_Date", new Date(System.currentTimeMillis() + 3600 * 24 * 365)).
            property("Object.Privacy_Impact_Assessment.Risk_To_Individuals", "Low").
            property("Object.Privacy_Impact_Assessment.Intrusion_On_Privacy", "Low").
            property("Object.Privacy_Impact_Assessment.Risk_To_Corporation", "Low").
            property("Object.Privacy_Impact_Assessment.Risk_Of_Reputational_Damage", "Low").
            property("Object.Privacy_Impact_Assessment.Compliance_Check_Passed", "true").
            next()

    /*
        Identifying privacy and related risks
 Record the risks to individuals, including possible intrusions on
privacy where appropriate.
 Assess the corporate risks, including regulatory action,
reputational damage, and loss of public trust.
 Conduct a compliance check against the Data Protection Act
and other relevant legislation.
 Maintain a record of the identified risks.

     */

    g.V().has("Metadata.Type.Person.Employee", eq("Person.Employee")).range(0, 10).as("employees").addE("Approved_Compliance_Check").from("employees").to(pia).next()



    g.addE("Has_Privacy_Notice").from(pia).to(privNoticeVertex).next()

    __addMoU(graph, g, pia);

//        trans.commit()

//    } catch (Throwable t) {
//        trans.rollback()
//        throw t
//    } finally{
//        trans.close()
//    }
}

/*


 In particular, they should record the date of consent, the method of consent, who obtained consent, and exactly what
information was provided to the person consenting. They should not rely on a bought-in list unless the seller or list
broker can provide these details. Organisations may be asked to produce their records as evidence to demonstrate compliance in the event of a complaint.

Privacy Impact Assessment

Describing information flows
 Explain how information will be obtained, used, and retained –
there may be several options to consider. This step can be
based on, or form part of, a wider project plan.
 This process can help to identify potential ‘function creep' -
unforeseen or unintended uses of the data (for example data
sharing)

Identifying privacy and related risks
 Record the risks to individuals, including possible intrusions on
privacy where appropriate.
 Assess the corporate risks, including regulatory action,
reputational damage, and loss of public trust.
 Conduct a compliance check against the Data Protection Act
and other relevant legislation.
 Maintain a record of the identified risks.


Identifying and evaluating privacy solutions
 Devise ways to reduce or eliminate privacy risks.
 Assess the costs and benefits of each approach, looking at the
impact on privacy and the effect on the project outcomes.
 Refer back to the privacy risk register until satisfied with the
overall privacy impact.


Signing off and recording the PIA outcomes
 Obtain appropriate signoff within the organisation.
 Produce a PIA report, drawing on material produced earlier
during the PIA.
 Consider publishing the report or other relevant information
about the process.

Integrating the PIA outcomes back into the project plan
 Ensure that the steps recommended by the PIA are
implemented.
 Continue to use the PIA throughout the project lifecycle when
appropriate


Internal Consultation (Stakeholders)

 Project management team
o The team responsible for the overall implementation of
a project will play a central role in the PIA process.
 Data protection officer
If an organisation has a dedicated DPO, they are likely to
have a close link to a PIA. Even if project managers are
responsible for individual PIAs, the DPO will be able to provide
specialist knowledge on privacy issues,
 Engineers, developers and designers
o The people who will be building a product need to have
a clear understanding of how to approach privacy
issues. They will also be able to suggest workable
privacy solutions to the risks which have been identified.
 Information technology (IT)
o Will be able to advise on security risks and solutions.
The role of IT is not limited to security, and might also
include discussions on the usability of any software.
 Procurement
o If the project will require systems or services to be
procured, the needs of the project need to be
established before procurement takes place.
 Potential suppliers and data processors
o If some of the project will be outsourced to a third
party, early engagement will help to understand which
options are available.
 Communications
o A PIA can become a useful part of a project's
communication strategy. For example, involving
communications colleagues in the PIA can help to
establish a clear message to the public about a project
 Customer-facing roles
o It is important to consult with the people who will have
to use a new system or put a policy into practice. They
will be able to advise on whether the system will work
as intended.
 Corporate governance/compliance
o Colleagues who work on risk management for an
organisation should be able to integrate PIAs into their
work. Other areas of compliance can be included in the
PIA process.
 Researchers, analysts, and statisticians
o Information gathered by a new project may be used to
analysing customer behaviour or for other statistical
purposes. Where relevant, consulting with researchers
can lead to more effective safeguards such as
Anonymisation.
 Senior management
o It will be important to involve those with responsibility
for signing off or approving a project.



External consultation:
Effective external consultations should follow these principles:
 Timely – at the right stage and allow enough time for
responses.
 Clear and proportionate– in scope and focused.
 Reach and representative - ensure those likely to be effected
have a voice.
 Ask objective questions and present realistic options.
 Feedback – ensure that those participating get feedback at
the end of the process.
 */


def addLawfulBasisAndPrivacyNotices(graph, g) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open()

        }

        def definitions = new String[6]


        definitions[0] = "Processing is necessary for the performance of a contract to which the data subject is party or in order to take steps at the request of the data subject prior to entering into a contract;"
        definitions[1] = "The data subject has given consent to the processing of his or her personal data for one or more specific purposes;"
        definitions[2] = "Processing is necessary for compliance with a legal obligation to which the controller is subject;"
        definitions[3] = "Processing is necessary in order to protect the vital interests of the data subject or of another natural person;"
        definitions[4] = "Processing is necessary for the performance of a task carried out in the public interest or in the exercise of official authority vested in the controller;"
        definitions[5] = "Processing is necessary for the purposes of the legitimate interests pursued by the controller or by a third party, except where such interests are overridden by the interests or fundamental rights and freedoms of the data subject which require protection of personal data, in particular where the data subject is a child."

        def lawfulBasisVertices = new Vertex[6]

        def ilen = definitions.length


        for (i = 0; i < ilen; i++) {


            lawfulBasis1 = g.V().has("Object.Lawful_Basis.Description", eq(definitions[i]))

            if (lawfulBasis1.hasNext()) {
                lawfulBasisVertices[i] = lawfulBasis1.next()
            } else {
                lawfulBasisVertices[i] = g.addV("Object.Lawful_Basis").
                        property("Metadata.Lineage", "https://gdpr-info.eu/art-6-gdpr/").
                        property("Metadata.Redaction", "/data/protection/officer").
                        property("Metadata.Version", 1).
                        property("Metadata.Create_Date", metadataCreateDate).
                        property("Metadata.Update_Date", metadataUpdateDate).
                        property("Metadata.Status", "new").
                        property("Metadata.GDPR_Status", "n/a").
                        property("Metadata.Lineage_Server_Tag", "AWS_AAA").
                        property("Metadata.Lineage_Location_Tag", "GB").
                        property("Metadata.Type", "Object.Lawful_Basis").
                        property("Metadata.Type.Object.Lawful_Basis", "Object.Lawful_Basis").
                        property("Object.Lawful_Basis.Id", i).
                        property("Object.Lawful_Basis.Description", definitions[i]).
                        next()
            }

        }


        /*

        What information is being collected? - InfoCollected
        Who is collecting it? - WhoIsCollecting
        How is it collected? - HowIsItCollected
          - Electronic Form (Two types: layering, just-in-time)
          - Telephone
          - Paper Form

        Why is it being collected? - WhyIsItCollected
        How will it be used? - HowWillItBeUsed
        Who will it be shared with? - WhoWillItBeShared
        What will be the effect of this on the individuals concerned? - EffectOnIndividuals
        Is the intended use likely to cause individuals to object or complain? - LikelyToComplain
        */


        def pnId0 = g.V().has("Metadata.Type.Object.Privacy_Notice", eq("Object.Privacy_Notice"))?.next()?.id();

        if (pnId0){
            g.addE("Has_Lawful_Basis_On").from(g.V(pnId0).next()).to(lawfulBasisVertices[0]).next()
            g.addE("Has_Lawful_Basis_On").from(g.V(pnId0).next()).to(lawfulBasisVertices[1]).next()

        }


//        __addConsentForPrivacyNotice(graph, g, g.V(pnId0).next())
//        __addConsentForPrivacyNotice(graph, g, g.V(pnId1).next())

//
//        __addPrivacyImpactAssessment(graph, g, g.V(pnId0).next())
//        __addPrivacyImpactAssessment(graph, g, g.V(pnId1).next())



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }

}


def createForms() {

//    objectFormLabel = createVertexLabel(mgmt, "Object.Form");
//
//    objectFormProp00 = createProp(mgmt, "Object.Form.Metadata_Owner", String.class, org.janusgraph.core.Cardinality.SINGLE);
//    objectFormProp01 = createProp(mgmt, "Object.Form.Metadata_Create_Date", String.class, org.janusgraph.core.Cardinality.SINGLE);
//    objectFormProp01 = createProp(mgmt, "Object.Form.Metadata_GUID", String.class, org.janusgraph.core.Cardinality.SINGLE);
//    objectFormProp02 = createProp(mgmt, "Object.Form.URL", String.class, org.janusgraph.core.Cardinality.SINGLE);
//    objectFormProp03 = createProp(mgmt, "Object.Form.Text", String.class, org.janusgraph.core.Cardinality.SINGLE);
//    objectFormProp04 = createProp(mgmt, "Object.Form.Vertex_Label", String.class, org.janusgraph.core.Cardinality.SINGLE);
//
//    objectNotificationTemplatesIdx01 = createMixedIdx(mgmt, "objectFormIdx01", objectFormLabel, objectFormProp00,objectFormProp01,objectFormProp02,objectFormProp04);

    def formData = [
            [
                    formOwner      : 'Leonardo',
                    formURL        : 'forms/gdpr/dsar_read',
                    formText       : "{'display': 'form'}",
                    formVertexLabel: "Event.Subject_Access_Request"
            ]
            ,

            [
                    formOwner      : 'Leonardo',
                    formURL        : 'forms/gdpr/consent',
                    formText       : "{'display': 'form'}",
                    formVertexLabel: "Event.Consent"

            ]
    ]

    def trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open()

        }


        for (def i = 0; i < formData.size(); i++) {
            def formDataObj = formData[i];

            def createMillis = System.currentTimeMillis();
            def metadataCreateDate = new Date((long) createMillis)



            def form = g.addV("Object.Form").
                    property("Metadata.Type", "Object.Form").
                    property("Metadata.Type.Object.Form", "Object.Form").
                    property("Object.Form.Metadata_Owner", formDataObj.formOwner).
                    property("Object.Form.Metadata_Create_Date", metadataCreateDate).
                    property("Object.Form.URL", formDataObj.formURL).
                    property("Object.Form.Text", formDataObj.formText.bytes.encodeBase64().toString()).
                    property("Object.Form.Vertex_Label", formDataObj.formVertexLabel).next();


        }








        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }

}

def createDataProtectionAuthorities() {

    def authData = [
            [
                    orgCountrySet       : 'AU',
                    orgName             : 'Österreichische Datenschutzbehörde',
                    orgShortName        : 'DSB',
                    locationAddrStreet  : 'Hohenstaufengasse 3',
                    locationAddrPostCode: '1010',
                    locationAddrCity    : 'Wien',
                    orgPhone            : '+43 1 531 15 202525',
                    orgFax              : '+43 1 531 15 202690',
                    orgEmail            : 'dsb@dsb.gv.at',
                    orgURL              : 'http://www.dsb.gv.at/'
            ]
            ,
            [
                    orgCountrySet       : 'BE',
                    orgName             : 'Commission de la protection de la vie privée',
                    orgShortName        : 'Privacy Commission',
                    locationAddrStreet  : 'Rue de la Presse 35',
                    locationAddrPostCode: '1000',
                    locationAddrCity    : 'Bruxelles',
                    orgPhone            : '+32 2 274 48 00',
                    orgFax              : '+32 2 274 48 10',
                    orgEmail            : 'commission@privacycommission.be',
                    orgURL              : 'http://www.privacycommission.be'
            ]
            ,
            [
                    orgCountrySet       : 'BG',
                    orgName             : 'Commission for Personal Data Protection',
                    orgShortName        : 'CPDP',
                    locationAddrStreet  : '2, Prof. Tsvetan Lazarov blvd.',
                    locationAddrPostCode: '1592',
                    locationAddrCity    : 'Sofia',
                    orgPhone            : '+359 2 915 3523',
                    orgFax              : '+359 2 915 3525',
                    orgEmail            : 'kzld@cpdp.bg',
                    orgURL              : 'http://www.cpdp.bg/'
            ]
            ,
            [
                    orgCountrySet       : 'HR',
                    orgName             : 'Croatian Personal Data Protection Agency',
                    orgShortName        : 'AZOP',
                    locationAddrStreet  : 'Martićeva 14',
                    locationAddrPostCode: '10000',
                    locationAddrCity    : 'Zagreb',
                    orgPhone            : '+385 1 4609 000',
                    orgFax              : '+385 1 4609 099',
                    orgEmail            : 'azop@azop.hr',
                    orgURL              : 'http://www.azop.hr/'
            ]
            ,
            [
                    orgCountrySet       : 'CY',
                    orgName             : 'Commissioner for Personal Data Protection',
                    orgShortName        : 'CPDP',
                    locationAddrStreet  : '1 Iasonos Street, 1082 Nicosia, P.O. Box 23378',
                    locationAddrPostCode: 'CY-1682',
                    locationAddrCity    : 'Nicosia',
                    orgPhone            : '+357 22 818 456',
                    orgFax              : '+357 22 304 565',
                    orgEmail            : 'commissioner@dataprotection.gov.cy',
                    orgURL              : 'http://www.dataprotection.gov.cy/'
            ]
            ,
            [
                    orgCountrySet       : 'CZ',
                    orgName             : 'Urad pro ochranu osobnich udaju (The Office for Personal Data Protection)',
                    orgShortName        : 'UOOU',
                    locationAddrStreet  : 'Pplk. Sochora 27',
                    locationAddrPostCode: '170 00',
                    locationAddrCity    : 'Prague 7',
                    orgPhone            : '+420 234 665 111',
                    orgFax              : '+420 234 665 444',
                    orgEmail            : 'posta@uoou.cz',
                    orgURL              : 'http://www.uoou.cz/'
            ]
            ,
            [
                    orgCountrySet       : 'DK',
                    orgName             : 'Datatilsynet',
                    orgShortName        : 'Datatilsynet',
                    locationAddrStreet  : 'Borgergade 28, 5',
                    locationAddrPostCode: '1300',
                    locationAddrCity    : 'Copenhagen K',
                    orgPhone            : '+45 33 1932 00',
                    orgFax              : '+45 33 19 32 18',
                    orgEmail            : 'dt@datatilsynet.dk',
                    orgURL              : 'http://www.datatilsynet.dk'
            ]
            ,
            [
                    orgCountrySet       : 'EE',
                    orgName             : 'Estonian Data Protection Inspectorate (Andmekaitse Inspektsioon)',
                    orgShortName        : 'AKI',
                    locationAddrStreet  : 'Väike-Ameerika 19',
                    locationAddrPostCode: '10129',
                    locationAddrCity    : 'Tallinn',
                    orgPhone            : '+372 6274 135',
                    orgFax              : '+372 6274 137',
                    orgEmail            : 'info@aki.ee',
                    orgURL              : 'http://www.aki.ee/en'
            ]
            ,
            [
                    orgCountrySet       : 'FI',
                    orgName             : 'Office of the Data Protection Ombudsman',
                    orgShortName        : 'OM',
                    locationAddrStreet  : 'P.O. Box 315',
                    locationAddrPostCode: 'FIN-00181',
                    locationAddrCity    : 'Helsinki',
                    orgPhone            : '+358 10 3666 700',
                    orgFax              : '+358 10 3666 735',
                    orgEmail            : 'tietosuoja@om.fi',
                    orgURL              : 'http://www.tietosuoja.fi/en/'
            ]
            ,
            [
                    orgCountrySet       : 'FR',
                    orgName             : 'Commission Nationale de l\'Informatique et des Libertés',
                    orgShortName        : 'CNIL',
                    locationAddrStreet  : '8 rue Vivienne, CS 30223',
                    locationAddrPostCode: 'F-75002',
                    locationAddrCity    : 'Paris, Cedex 02',
                    orgPhone            : '+33 1 53 73 22 22',
                    orgFax              : '+33 1 53 73 22 00',
                    orgEmail            : '',
                    orgURL              : 'http://www.cnil.fr/'
            ]
            ,
            [
                    orgCountrySet       : 'DE',
                    orgName             : 'Die Bundesbeauftragte für den Datenschutz und die Informationsfreiheit',
                    orgShortName        : 'BFDI',
                    locationAddrStreet  : 'Husarenstraße 30',
                    locationAddrPostCode: '53117',
                    locationAddrCity    : 'Bonn',
                    orgPhone            : '+49 228 997799 0',
                    orgFax              : '+49 228 997799 550',
                    orgEmail            : 'poststelle@bfdi.bund.de',
                    orgURL              : 'http://www.bfdi.bund.de/'
            ]
            ,
            [
                    orgCountrySet       : 'GR',
                    orgName             : 'Hellenic Data Protection Authority',
                    orgShortName        : 'DPA',
                    locationAddrStreet  : 'Kifisias Av. 1-3',
                    locationAddrPostCode: 'PC 11523',
                    locationAddrCity    : 'Ampelokipi Athens',
                    orgPhone            : '+49 228 997799 0',
                    orgFax              : '+49 228 997799 550',
                    orgEmail            : 'contact@dpa.gr',
                    orgURL              : 'http://www.dpa.gr/'
            ]
            ,
            [
                    orgCountrySet       : 'HU',
                    orgName             : 'Data Protection Commissioner of Hungary',
                    orgShortName        : 'DPA',
                    locationAddrStreet  : 'Szilágyi Erzsébet fasor 22/C',
                    locationAddrPostCode: 'H-1125',
                    locationAddrCity    : 'Budapest',
                    orgPhone            : '+36 1 3911 400',
                    orgFax              : '',
                    orgEmail            : 'peterfalvi.attila@naih.hu',
                    orgURL              : 'http://www.naih.hu/'
            ]
            ,
            [
                    orgCountrySet       : 'IE',
                    orgName             : 'Data Protection Commissioner',
                    orgShortName        : 'DPC',
                    locationAddrStreet  : 'Canal House, Station Road',
                    locationAddrPostCode: 'Co. Laois',
                    locationAddrCity    : 'Portarlington',
                    orgPhone            : '+353 57 868 4800',
                    orgFax              : '+353 57 868 4757',
                    orgEmail            : 'info@dataprotection.ie',
                    orgURL              : 'http://www.dataprotection.ie/'
            ]
            ,
            [
                    orgCountrySet       : 'IT',
                    orgName             : 'Garante per la protezione dei dati personali',
                    orgShortName        : 'Garante Privacy',
                    locationAddrStreet  : 'Piazza di Monte Citorio, 121',
                    locationAddrPostCode: '00186',
                    locationAddrCity    : 'Roma',
                    orgPhone            : '+39 06 69677 1',
                    orgFax              : '+39 06 69677 785',
                    orgEmail            : 'garante@garanteprivacy.it',
                    orgURL              : 'http://www.garanteprivacy.it/'
            ]
            ,
            [
                    orgCountrySet       : 'LV',
                    orgName             : 'Data State Inspectorate',
                    orgShortName        : 'DVI',
                    locationAddrStreet  : 'Blaumana str. 11/13-15',
                    locationAddrPostCode: '1011',
                    locationAddrCity    : 'Riga',
                    orgPhone            : '+371 6722 3131',
                    orgFax              : '+371 6722 3556',
                    orgEmail            : 'info@dvi.gov.lv',
                    orgURL              : 'http://www.dvi.gov.lv/'
            ]
            ,
            [
                    orgCountrySet       : 'LT',
                    orgName             : 'State Data Protection',
                    orgShortName        : 'ADA',
                    locationAddrStreet  : 'Žygimantų str. 11-6a',
                    locationAddrPostCode: '011042',
                    locationAddrCity    : 'Vilnius',
                    orgPhone            : '+ 370 5 279 14 45',
                    orgFax              : '+370 5 261 94 94',
                    orgEmail            : 'ada@ada.lt',
                    orgURL              : 'http://www.ada.lt/'
            ]
            ,
            [
                    orgCountrySet       : 'LU',
                    orgName             : 'Commission Nationale pour la Protection des Données',
                    orgShortName        : 'CNDP',
                    locationAddrStreet  : '1, avenue du Rock n Roll',
                    locationAddrPostCode: 'L-4361',
                    locationAddrCity    : 'Esch-sur-Alzette',
                    orgPhone            : '+352 2610 60 1',
                    orgFax              : '+352 2610 60 29',
                    orgEmail            : 'info@cnpd.lu',
                    orgURL              : 'http://www.cnpd.lu/'
            ]
            ,
            [
                    orgCountrySet       : 'MT',
                    orgName             : 'Office of the Data Protection Commissioner',
                    orgShortName        : 'DPC',
                    locationAddrStreet  : '2, Airways House, High Street',
                    locationAddrPostCode: 'SLM 1549',
                    locationAddrCity    : 'Sliema',
                    orgPhone            : '+356 2328 7100',
                    orgFax              : '+356 2328 7198',
                    orgEmail            : 'commissioner.dataprotection@gov.mt',
                    orgURL              : 'http://www.dataprotection.gov.mt/'
            ]
            ,
            [
                    orgCountrySet       : 'NL',
                    orgName             : 'Autoriteit Persoonsgegevens',
                    orgShortName        : 'APG',
                    locationAddrStreet  : 'Prins Clauslaan 60, P.O. Box 93374',
                    locationAddrPostCode: '2509 AJ',
                    locationAddrCity    : 'Den Haag/The Hague',
                    orgPhone            : '+31 70 888 8500',
                    orgFax              : '+31 70 888 8501',
                    orgEmail            : 'info@autoriteitpersoonsgegevens.nl',
                    orgURL              : 'https://autoriteitpersoonsgegevens.nl/nl'
            ]
            ,
            [
                    orgCountrySet       : 'PL',
                    orgName             : 'The Bureau of the Inspector General for the Protection of Personal Data',
                    orgShortName        : 'GIODO',
                    locationAddrStreet  : 'ul. Stawki 2',
                    locationAddrPostCode: '00-193',
                    locationAddrCity    : 'Warsaw',
                    orgPhone            : '+48 22 53 10 440',
                    orgFax              : '+48 22 53 10 441',
                    orgEmail            : 'kancelaria@giodo.gov.pl',
                    orgURL              : 'http://www.giodo.gov.pl'
            ]
            ,
            [
                    orgCountrySet       : 'PT',
                    orgName             : 'Comissão Nacional de Protecção de Dados',
                    orgShortName        : 'CNPD',
                    locationAddrStreet  : 'R. de São. Bento, 148-3°',
                    locationAddrPostCode: '1200-821',
                    locationAddrCity    : 'Lisboa',
                    orgPhone            : '+351 21 392 84 00',
                    orgFax              : '+351 21 397 68 32',
                    orgEmail            : 'geral@cnpd.pt',
                    orgURL              : 'http://www.cnpd.pt/'
            ]
            ,
            [
                    orgCountrySet       : 'RO',
                    orgName             : 'The National Supervisory Authority for Personal Data Processing',
                    orgShortName        : 'NSAPDP',
                    locationAddrStreet  : 'B-dul Magheru 28-30',
                    locationAddrPostCode: 'Sector 1',
                    locationAddrCity    : 'BUCUREŞTI',
                    orgPhone            : '+40 21 252 5599',
                    orgFax              : '+40 21 252 5757',
                    orgEmail            : 'anspdcp@dataprotection.ro',
                    orgURL              : 'http://www.dataprotection.ro/'
            ]
            ,
            [
                    orgCountrySet       : 'SK',
                    orgName             : 'Office for Personal Data Protection of the Slovak Republic',
                    orgShortName        : 'OPDP',
                    locationAddrStreet  : 'Hraničná 12',
                    locationAddrPostCode: '820 07',
                    locationAddrCity    : 'Bratislava 27',
                    orgPhone            : '+ 421 2 32 31 32 14',
                    orgFax              : '+ 421 2 32 31 32 34',
                    orgEmail            : 'statny.dozor@pdp.gov.sk',
                    orgURL              : 'http://www.dataprotection.gov.sk/'
            ]
            ,
            [
                    orgCountrySet       : 'SI',
                    orgName             : 'Information Commissioner',
                    orgShortName        : 'IP-RS',
                    locationAddrStreet  : 'Zaloška 59',
                    locationAddrPostCode: '1000',
                    locationAddrCity    : 'Ljubljana',
                    orgPhone            : '+386 1 230 9730',
                    orgFax              : '+386 1 230 9778',
                    orgEmail            : 'gp.ip@ip-rs.si',
                    orgURL              : 'https://www.ip-rs.si/'
            ]
            ,
            [
                    orgCountrySet       : 'ES',
                    orgName             : 'Agencia de Protección de Datos',
                    orgShortName        : 'AGPD',
                    locationAddrStreet  : 'C/Jorge Juan, 6',
                    locationAddrPostCode: '28001',
                    locationAddrCity    : 'Madrid',
                    orgPhone            : '+34 91399 6200',
                    orgFax              : '+34 91455 5699',
                    orgEmail            : 'internacional@agpd.es',
                    orgURL              : 'https://www.agpd.es/'
            ]
            ,
            [
                    orgCountrySet       : 'SE',
                    orgName             : 'Datainspektionen',
                    orgShortName        : '',
                    locationAddrStreet  : 'Drottninggatan 29, 5th Floor, Box 8114',
                    locationAddrPostCode: '104 20',
                    locationAddrCity    : 'Stockholm',
                    orgPhone            : '+46 8 657 6100',
                    orgFax              : '+46 8 652 8652',
                    orgEmail            : 'datainspektionen@datainspektionen.se',
                    orgURL              : 'http://www.datainspektionen.se/'
            ]
            ,
            [
                    orgCountrySet       : 'GB',
                    orgName             : "The Information Commissioner's Office",
                    orgShortName        : 'ICO',
                    locationAddrStreet  : 'Water Lane, Wycliffe House',
                    locationAddrPostCode: 'SK9 5AF',
                    locationAddrCity    : 'Wilmslow, Cheshire',
                    orgPhone            : '+44 1625 545 745',
                    orgFax              : '',
                    orgEmail            : 'international.team@ico.org.uk',
                    orgURL              : 'https://ico.org.uk'
            ]
            ,
            [
                    orgCountrySet       : 'EU',
                    orgName             : 'European Data Protection Supervisor',
                    orgShortName        : 'EDPS',
                    locationAddrStreet  : 'Rue Montoyer 63, 6th floor',
                    locationAddrPostCode: '1047',
                    locationAddrCity    : 'Bruxelles/Brussel',
                    orgPhone            : '+32 2 283 19 00',
                    orgFax              : '+32 2 283 19 50',
                    orgEmail            : 'edps@edps.europa.eu',
                    orgURL              : 'http://www.edps.europa.eu/EDPSWEB'
            ]


    ];


    def trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open()

        }

        def randVal = new Random();
        def randValK = randVal.nextInt(5);



        def oneWeekInMs = 3600000 * 24 * 7;
        def eighteenWeeks = oneWeekInMs * 18;


        def types = new String[6];

        types[0] = "Person.Natural";
        types[1] = "Object.Email_Address";
        types[2] = "Object.Credential";
        types[3] = "Object.Identity_Card";
        types[4] = "Event.Consent";
        types[5] = "Event.Subject_Access_Request";


        for (def i = 0; i < authData.size(); i++) {
            def authDataObj = authData[i];

//            orgCountrySet       : 'EU',
//            orgName             : 'European Data Protection Supervisor',
//            orgShortName        : 'EDPS',
//            locationAddrStreet  : 'Rue Montoyer 63, 6th floor',
//            locationAddrPostCode: '1047',
//            locationAddrCity    : 'Bruxelles/Brussel',
//            orgPhone            : '+32 2 283 19 00',
//            orgFax              : '+32 2 283 19 50',
//            orgEmail            : 'edps@edps.europa.eu',
//            orgURL              : 'http://www.edps.europa.eu/EDPSWEB'


            def createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks)
            def updateMillis = createMillis + (long) (randVal.nextDouble() * eighteenWeeks)
            def metadataCreateDate = new Date((long) createMillis)
            def metadataUpdateDate = new Date((long) updateMillis)



            def location = g.addV("Location.Address").
                    property("Metadata.Lineage", "http://ec.europa.eu/justice/data-protection/article-29/structure/data-protection-authorities/index_en.htm").

                    property("Metadata.Redaction", "/dataprotectionofficer/aaa").
                    property("Metadata.Version", "1").
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Lineage_Server_Tag", "AWS EUR1").
                    property("Metadata.Lineage_Location_Tag", "GB").
                    property("Metadata.Type", "Location.Address").
                    property("Metadata.Type.Location.Address", "Location.Address").
                    property("Location.Address.Street", authDataObj.locationAddrStreet).
                    property("Location.Address.City", authDataObj.locationAddrCity).
                    property("Location.Address.Post_Code", authDataObj.locationAddrCity).next()



            long orgId = g.addV("Person.Organisation").
                    property("Metadata.Lineage", "http://ec.europa.eu/justice/data-protection/article-29/structure/data-protection-authorities/index_en.htm").
                    property("Metadata.Redaction", "/dataprotectionofficer/aaa").
                    property("Metadata.Version", "1").
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Lineage_Server_Tag", "AWS EUR1").
                    property("Metadata.Lineage_Location_Tag", "GB").
                    property("Metadata.Type", "Person.Organisation").
                    property("Metadata.Type.Person.Organisation", "Person.Organisation").
                    property("Person.Organisation.Short_Name", authDataObj.orgShortName).
                    property("Person.Organisation.Name", authDataObj.orgName).
                    property("Person.Organisation.URL", authDataObj.orgURL).
                    property("Person.Organisation.orgCountrySet", authDataObj.orgCountrySet).
                    property("Person.Organisation.Phone", authDataObj.orgPhone).
                    property("Person.Organisation.Email", authDataObj.orgEmail).
                    property("Person.Organisation.Fax", authDataObj.orgFax).
                    next().id();


            for (def k = 0; k < randValK; k++) {
                def org = g.V(orgId);

                def pia = g.V().has('Metadata.Type.Object.Privacy_Impact_Assessment', eq('Object.Privacy_Impact_Assessment'))
                        .order().by(shuffle).range(0, 1).next()

                g.addE("Has_Data_Procedures").from(pia).to(org).next()
            }


        }








        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }


}

def createNotificationTemplates() {
    def trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open();
        }
        g.addV("Object.Notification_Templates")
                .property("Metadata.Type.Object.Notification_Templates", "Object.Notification_Templates")
                .property("Metadata.Type", "Object.Notification_Templates")
                .property("Object.Notification_Templates.Id", "SAR READ TEMPLATE")
                .property("Object.Notification_Templates.Text", ("<p>Dear {{ context.Person_Title | capitalize }} {{ context.Person_Last_Name |capitalize }}, </p>\n" +
                "\n" +
                "  Please find below a summary of the data we HOLD about you:\n" +
                "  \n" +
                "  <p><br></p><p>\n" +
                "  <h3>{{ context.Metadata_Type |replace('.',' ')|replace('_',' ') }}</h3>\n" +
                "  {{ \"<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Name</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Value</th></tr>\" }}\n" +
                "  {% for key, value in context.items() %}\n" +
                "  {{  \"<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td>\" | format (key , value )}}\n" +
                "  {% endfor %}\n" +
                "  {{ \"</table>\" }}\n" +
                "  {% endfor %}\n" +
                "  \n" +
                "  \n" +
                "  {% for mainkey in connected_data %}\n" +
                "  <h3>{{ mainkey.Metadata_Type |replace('.',' ')|replace('_',' ') }}</h3>\n" +
                "  {{ \"<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Name</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Value</th></tr>\" }}\n" +
                "  {% for key, value in mainkey.items() %}\n" +
                "  {{  \"<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td>\" | format (key , value )}}\n" +
                "  {% endfor %}\n" +
                "  {{ \"</table>\" }}\n" +
                "  {% endfor %}").bytes.encodeBase64().toString())
                .property("Object.Notification_Templates.Types", "Person.Natural")
                .property("Object.Notification_Templates.URL", "https://localhost:18443/get_sar_read")
                .property("Object.Notification_Templates.Label", "SAR Read")
                .next();

        g.addV("Object.Notification_Templates")
                .property("Metadata.Type", "Object.Notification_Templates")
                .property("Metadata.Type.Object.Notification_Templates", "Object.Notification_Templates")
                .property("Object.Notification_Templates.Id", "SAR UPDATE TEMPLATE")
                .property("Object.Notification_Templates.Text", ("<p>Dear {{ context.Person_Title | capitalize }} {{ context.Person_Last_Name |capitalize }}, </p>\n" +
                "\n" +
                "  Please find below a summary of your personal data that we can UPDATE:\n" +
                "  \n" +
                "  <p><br></p><p>\n" +
                "  <h3>{{ context.Metadata_Type |replace('.',' ')|replace('_',' ') }}</h3>\n" +
                "  {{ \"<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Name</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Value</th></tr>\" }}\n" +
                "  {% for key, value in context.items() %}\n" +
                "  {{  \"<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td>\" | format (key , value )}}\n" +
                "  {% endfor %}\n" +
                "  {{ \"</table>\" }}\n" +
                "  {% endfor %}\n" +
                "  \n" +
                "  \n" +
                "  {% for mainkey in connected_data %}\n" +
                "  <h3>{{ mainkey.Metadata_Type |replace('.',' ')|replace('_',' ') }}</h3>\n" +
                "  {{ \"<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Name</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Value</th></tr>\" }}\n" +
                "  {% for key, value in mainkey.items() %}\n" +
                "  {{  \"<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td>\" | format (key , value )}}\n" +
                "  {% endfor %}\n" +
                "  {{ \"</table>\" }}\n" +
                "  {% endfor %}").bytes.encodeBase64().toString())
                .property("Object.Notification_Templates.Types", "Person.Natural")
                .property("Object.Notification_Templates.URL", "https://localhost:18443/get_sar_update")
                .property("Object.Notification_Templates.Label", "SAR Update")
                .next();

        g.addV("Object.Notification_Templates")
                .property("Metadata.Type", "Object.Notification_Templates")
                .property("Metadata.Type.Object.Notification_Templates", "Object.Notification_Templates")
                .property("Object.Notification_Templates.Id", "SAR DELETE TEMPLATE")
                .property("Object.Notification_Templates.Text", ("<p>Dear {{ context.Person_Title | capitalize }} {{ context.Person_Last_Name |capitalize }}, </p>\n" +
                "\n" +
                "  Please find below a summary of your personal data we can delete:\n" +
                "  \n" +
                "  <p><br></p><p>\n" +
                "  <h3>{{ context.Metadata_Type |replace('.',' ')|replace('_',' ') }}</h3>\n" +
                "  {{ \"<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Name</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Value</th></tr>\" }}\n" +
                "  {% for key, value in context.items() %}\n" +
                "  {{  \"<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td>\" | format (key , value )}}\n" +
                "  {% endfor %}\n" +
                "  {{ \"</table>\" }}\n" +
                "  {% endfor %}\n" +
                "  \n" +
                "  \n" +
                "  {% for mainkey in connected_data %}\n" +
                "  <h3>{{ mainkey.Metadata_Type |replace('.',' ')|replace('_',' ') }}</h3>\n" +
                "  {{ \"<table style='margin: 5px'><tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Name</th><th style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>Value</th></tr>\" }}\n" +
                "  {% for key, value in mainkey.items() %}\n" +
                "  {{  \"<tr style='border: 1px solid #dddddd;text-align: left;padding: 8px;'><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td><td style='border: 1px solid #dddddd;text-align: left;padding: 8px;'>%s</td>\" | format (key , value )}}\n" +
                "  {% endfor %}\n" +
                "  {{ \"</table>\" }}\n" +
                "  {% endfor %}").bytes.encodeBase64().toString())
                .property("Object.Notification_Templates.Types", "Person.Natural")
                .property("Object.Notification_Templates.URL", "https://localhost:18443/get_sar_delete")
                .property("Object.Notification_Templates.Label", "SAR Delete")
                .next();


        g.addV("Object.Notification_Templates")
                .property("Metadata.Type", "Object.Notification_Templates")
                .property("Metadata.Type.Object.Notification_Templates", "Object.Notification_Templates")
                .property("Object.Notification_Templates.Id", "DATA BREACH REGULATOR TEMPLATE")
                .property("Object.Notification_Templates.Text", "Dear {{ Person.Organisation.Short_Name }}, We regret to inform that we have found a data breach in our infrastructure.  The servers involved are .".bytes.encodeBase64().toString())
                .property("Object.Notification_Templates.Types", "Event.Data_Breach")
                .property("Object.Notification_Templates.URL", "https://localhost:18443/get_data_breach_regulator_report")
                .property("Object.Notification_Templates.Label", "Regulator")
                .next();

        g.addV("Object.Notification_Templates")
                .property("Metadata.Type", "Object.Notification_Templates")
                .property("Metadata.Type.Object.Notification_Templates", "Object.Notification_Templates")
                .property("Object.Notification_Templates.Id", "DATA BREACH BOARD TEMPLATE")
                .property("Object.Notification_Templates.Text", "Dear Board Members, We regret to inform that we have found a data breach in our infrastructure.  The servers involved are .".bytes.encodeBase64().toString())
                .property("Object.Notification_Templates.URL", "https://localhost:18443/get_data_breach_board_report")
                .property("Object.Notification_Templates.Types", "Event.Data_Breach")
                .property("Object.Notification_Templates.Label", "Board")
                .next();

        g.addV("Object.Notification_Templates")
                .property("Metadata.Type", "Object.Notification_Templates")
                .property("Metadata.Type.Object.Notification_Templates", "Object.Notification_Templates")
                .property("Object.Notification_Templates.Id", "DATA BREACH PERSON TEMPLATE")
                .property("Object.Notification_Templates.Text", "Dear {{ connected_data.Person_Name }}, We regret to inform you that your data may have been stolen.".bytes.encodeBase64().toString())
                .property("Object.Notification_Templates.URL", "https://localhost:18443/get_data_breach_person_report")
                .property("Object.Notification_Templates.Types", "Event.Data_Breach")
                .property("Object.Notification_Templates.Label", "Person.Natural")
                .next();

        trans.commit();

    }
    catch (t) {
        trans.rollback();
        throw t;
    } finally {
        trans.close();
    }


}


def __addVPCEdgesFromUserIdGroupPairs(
        graph, g,
        Long origVpcVid,
        userIdGroupPairs,
        String origGroupIdStr,
        boolean isEgress) {

    def StringBuilder sb = new StringBuilder();


    userIdGroupPairs.each { uidPair ->


        if (uidPair.VpcId != null && uidPair.PeeringStatus == "active") {
            def peerVpc = g.V().has('Object.AWS_VPC.Id', eq(uidPair.VpcId)).next();
            trans = graph.tx()

            try {
                if (!trans.isOpen())
                    trans.open();

                def origVPC = g.V(origVpcVid).next();

                g.addE(isEgress ? "Has_Egress_Peering" : "Has_Ingress_Peering")
                        .from(origVPC)
                        .to(peerVpc)
                        .property('Metadata.Lineage', origGroupIdStr)
                        .next();

                sb.append('in __addVPCEdgesFromUserIdGroupPairs -')
                        .append('uidPair.VpcId = ').append(uidPair.VpcId).append('\n')


                trans.commit();
            } catch (Throwable t) {
                sb.append('ERROR - in __addVPCEdgesFromUserIdGroupPairs -')
                        .append(t.toString()).append('\n')

                trans.rollback();
            } finally {
                trans.close();
            }

        }
    }
    return sb.toString()
}

def __addSecGroupEdgesFromUserIdGroupPairs(graph, g, Long origSecGroupVid, userIdGroupPairs, String origGroupIdStr, boolean isEgress) {
    def StringBuilder sb = new StringBuilder();

    userIdGroupPairs.each { uidPair ->


        if (uidPair.GroupId != null) {
            def peerSecGroup = g.V().has('Object.AWS_Security_Group.Id', eq(uidPair.GroupId)).next();
            trans = graph.tx()

            try {
                if (!trans.isOpen())
                    trans.open();

                def origSecGroup = g.V(origSecGroupVid).next();

                g.addE(isEgress ? "Has_Egress_Peering" : "Has_Ingress_Peering")
                        .from(origSecGroup)
                        .to(peerSecGroup)
                        .property('Metadata.Lineage', origGroupIdStr)
                        .next();
                sb.append('in __addSecGroupEdgesFromUserIdGroupPairs -')
                        .append('uidPair.Id = ').append(uidPair.GroupId).append('\n')



                trans.commit();
            } catch (Throwable t) {
                sb.append('ERROR - in __addSecGroupEdgesFromUserIdGroupPairs -')
                        .append(t.toString()).append('\n')


                trans.rollback();
            } finally {
                trans.close();
            }

        }
    }
    return sb.toString()
}

def __addSecGroupEdges(graph, g, aws_sec_groups) {

    def StringBuilder sb = new StringBuilder();

    def secGroups = com.jayway.jsonpath.JsonPath.parse(aws_sec_groups).read('$.SecurityGroups.[*]').toSet()

    secGroups.each { sg ->

        sb.append('in __addSecGroupEdges -')
                .append('sg.VpcId = ').append(sg.VpcId).append('\n')

        Long vpcVid = null;
        try {
            vpcVid = g.V().has('Object.AWS_VPC.Id', eq(sg.VpcId)).next().id();

            // sb.append('in __addSecGroupEdges -')
            //   .append('vpcVid = ').append(vpcVid).append('\n')

            // sb.append('in __addSecGroupEdges -')
            //   .append('sg.IpPermissionsEgress = ').append(sg.IpPermissionsEgress.toString()).append('\n')

            sg.IpPermissionsEgress.each { egressIpPerm ->


                if (egressIpPerm.UserIdGroupPairs != null) {
                    sb.append('in __addSecGroupEdges -')
                            .append('egressIpPerm.UserIdGroupPairs = ').append(egressIpPerm.UserIdGroupPairs.toString()).append('\n')

                    retVal = __addVPCEdgesFromUserIdGroupPairs(graph, g, vpcVid, egressIpPerm.UserIdGroupPairs, (String) sg.GroupId, true);
                    sb.append(retVal);
                }
            }


            sg.IpPermissions.each { ingressIpPerm ->
                if (ingressIpPerm.UserIdGroupPairs != null) {
                    sb.append('in __addSecGroupEdges -')
                            .append('ingressIpPerm.UserIdGroupPairs= ').append(ingressIpPerm.UserIdGroupPairs.toString()).append('\n')

                    retVal = __addVPCEdgesFromUserIdGroupPairs(graph, g, vpcVid, ingressIpPerm.UserIdGroupPairs, (String) sg.GroupId, false);
                    sb.append(retVal);

                }

            }

        }
        catch (Throwable t) {
        }

        Long sgVid = null;
        try {
            sgVid = g.V().has('Object.AWS_Security_Group.Id', eq(sg.GroupId)).next().id();

            sg.IpPermissionsEgress.each { egressIpPerm ->
                if (egressIpPerm.UserIdGroupPairs != null) {
                    retVal = __addSecGroupEdgesFromUserIdGroupPairs(graph, g, sgVid, egressIpPerm.UserIdGroupPairs, (String) sg.GroupId, true);
                    sb.append(retVal);

                }
            }
            sg.IpPermissions.each { ingressIpPerm ->
                if (ingressIpPerm.UserIdGroupPairs != null) {
                    retVal = __addSecGroupEdgesFromUserIdGroupPairs(graph, g, sgVid, ingressIpPerm.UserIdGroupPairs, (String) sg.GroupId, false);
                    sb.append(retVal);

                }

            }

        }
        catch (Throwable t) {
        }


    }

    return sb.toString();
}


def addRandomAWSGraph(graph, g, aws_instances, aws_sec_groups) {

    if (aws_instances == null) {

        aws_instances = '{"Reservations":[{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.158","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-08192f6d39ea79dc8","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-158.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:0e:3a:05:6c:30","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-378a4f62","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-158.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.158"}],"PrivateDnsName":"ip-10-230-1-158.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-98901771","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.158"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-03f415a2f4cd7c1c1","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-029a3712213802142","AttachTime":"2017-11-16T15:47:02.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"primary-data","Key":"Type"},{"Value":"test","Key":"Environment"},{"Value":"test-cdp-primary-data2","Key":"Name"},{"Value":"cdp","Key":"Project"}],"AmiLaunchIndex":0}],"ReservationId":"r-096b6c2e4c6fd1a5d","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.91","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-0cd4562955f2886e0","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-91.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:b3:d9:93:27:28","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-26955073","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-91.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.91"}],"PrivateDnsName":"ip-10-230-1-91.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-059017ec","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.91"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-089704dca3dd176a6","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0ef1a12a3288c5120","AttachTime":"2017-11-16T15:47:02.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-cdp-primary-data0","Key":"Name"},{"Value":"primary-data","Key":"Type"},{"Value":"test","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"}],"AmiLaunchIndex":0}],"ReservationId":"r-0c50bb472c62d3d02","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:24.000Z","PrivateIpAddress":"10.230.28.10","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-725b671b","StateTransitionReason":"","InstanceId":"i-01cfc5dbdc70c4de2","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-28-10.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-ed0e8e85"}],"ClientToken":"","SubnetId":"subnet-bebc91c5","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:98:c5:1a:39:8a","SourceDestCheck":true,"VpcId":"vpc-725b671b","Description":"private ip address for abc alpha dev stack ingest primary-flow","NetworkInterfaceId":"eni-7178ce24","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-28-10.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.28.10"}],"PrivateDnsName":"ip-10-230-28-10.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-f2de2891","AttachTime":"2017-12-15T13:56:12.000Z"},"Groups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-ed0e8e85"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-bebc91c5","PrivateIpAddress":"10.230.28.10"}],"SourceDestCheck":true,' +

                '"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0035700dd2213d270","AttachTime":"2017-12-15T13:56:13.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ingest-primary-flow-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-05ceb8157121a771c","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-03c19caed89fb33f8","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-broker-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-07c1aead2dcc43dc9","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"ec2-35-177-16-174.eu-west-2.compute.amazonaws.com","StateReason":{"Message":"","Code":""},"State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-10-07T14:18:11.000Z","PublicIpAddress":"35.177.16.174","PrivateIpAddress":"10.227.100.199","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-0ff813cc7b0de4ca5","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-100-199.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-32ce4249","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:ee:aa:96:9e:f5","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-98bad6e2","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-100-199.eu-west-2.compute.internal","PrivateIpAddress":"10.227.100.199","Primary":true,"Association":{"PublicIp":"35.177.16.174","PublicDnsName":"ec2-35-177-16-174.eu-west-2.compute.amazonaws.com","IpOwnerId":"amazon"}}],"PrivateDnsName":"ip-10-227-100-199.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-ca73eeaa","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","PrivateIpAddress":"10.227.100.199","SubnetId":"subnet-32ce4249","Association":{"PublicIp":"35.177.16.174","PublicDnsName":"ec2-35-177-16-174.eu-west-2.compute.amazonaws.com","IpOwnerId":"amazon"}}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-08991633bacd13fa1","AttachTime":"2017-08-07T19:23:10.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"bastion-server","Key":"Type"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev-cdp-bastion-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-01a4c5ef14e049343","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.30.4","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-6c5a6605","StateTransitionReason":"","InstanceId":"i-0ec76719068ffe6be","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-30-4.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ext-logging-syslog","GroupId":"sg-8e33b0e6"}],"ClientToken":"","SubnetId":"subnet-2abe9351","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:27:a2:b0:1e:c6","SourceDestCheck":true,"VpcId":"vpc-6c5a6605","Description":"private ip address for abc alpha dev stack ext-logging syslog","NetworkInterfaceId":"eni-41b40414","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-30-4.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.30.4"}],"PrivateDnsName":"ip-10-230-30-4.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-a1d026c2","AttachTime":"2017-12-15T13:56:16.000Z"},"Groups":[{"GroupName":"abc-alpha-ext-logging-syslog","GroupId":"sg-8e33b0e6"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-2abe9351","PrivateIpAddress":"10.230.30.4"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0f4de5959397625cc","AttachTime":"2017-12-15T13:56:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ext-logging-syslog-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0bdc2c6e4ffae4ca6","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.115",' +
                '"ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-08c2e1ecc30493abf","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-115.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:70:b6:c6:15:7a","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-ce97529b","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-115.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.115"}],"PrivateDnsName":"ip-10-230-1-115.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-489116a1","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.115"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-025f4ec51f7c3b740","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-02c30cd544d7bd1e4","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"primary-ambari-server","Key":"Type"},{"Value":"test","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"test-cdp-primary-ambari-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-021748ce9816dfad3","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.207","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-082351cce7a36c4ec","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-207.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:63:a1:9a:07:0e","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-ac9752f9","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-207.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.207"}],"PrivateDnsName":"ip-10-230-1-207.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5c9215b5","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.207"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-04bfb87a4e0a39e9e","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0946654070602db5e","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test","Key":"Environment"},{"Value":"secondary-master","Key":"Type"},{"Value":"cdp","Key":"Project"},{"Value":"test-cdp-secondary-master0","Key":"Name"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"}],"AmiLaunchIndex":0}],"ReservationId":"r-0fe1745d31a4ddf71","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.0.57","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-461e2b2f","StateTransitionReason":"","InstanceId":"i-01fd752b2895bd416","EnaSupport":false,"ImageId":"ami-c2243aa6","PrivateDnsName":"ip-10-230-0-57.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"default","GroupId":"sg-cf8b3ca7"}],"ClientToken":"","SubnetId":"subnet-2baa8f50","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:11:e6:ad:84:94","SourceDestCheck":true,"VpcId":"vpc-461e2b2f","Description":"Primary network interface","NetworkInterfaceId":"eni-8718bed2","PrivateIpAddresses":[{"Primary":true,"PrivateIpAddress":"10.230.0.57"}],"SubnetId":"subnet-2baa8f50","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-e452b287","AttachTime":"2017-12-06T10:22:47.000Z"},"Groups":[{"GroupName":"default","GroupId":"sg-cf8b3ca7"}],"Ipv6Addresses":[],"OwnerId":"484676447925","PrivateIpAddress":"10.230.0.57"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0f727edcdb9e54790","AttachTime":"2017-12-06T10:22:48.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","AmiLaunchIndex":0},{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.0.35","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-461e2b2f","StateTransitionReason":"",' +
                '"InstanceId":"i-0e8a1667cccedff2d","EnaSupport":false,"ImageId":"ami-c2243aa6","PrivateDnsName":"ip-10-230-0-35.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"default","GroupId":"sg-cf8b3ca7"}],"ClientToken":"","SubnetId":"subnet-2baa8f50","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:0a:4c:4a:43:84","SourceDestCheck":true,"VpcId":"vpc-461e2b2f","Description":"Primary network interface","NetworkInterfaceId":"eni-8618bed3","PrivateIpAddresses":[{"Primary":true,"PrivateIpAddress":"10.230.0.35"}],"SubnetId":"subnet-2baa8f50","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-e552b286","AttachTime":"2017-12-06T10:22:47.000Z"},"Groups":[{"GroupName":"default","GroupId":"sg-cf8b3ca7"}],"Ipv6Addresses":[],"OwnerId":"484676447925","PrivateIpAddress":"10.230.0.35"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0803775553fa4d069","AttachTime":"2017-12-06T10:22:48.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","AmiLaunchIndex":1}],"ReservationId":"r-0ebdcb7deca14b4c8","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.229.101.56","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-4d5c1c24","StateTransitionReason":"","InstanceId":"i-02eab56ac83e96d30","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-229-101-56.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"logging-cdp-sg","GroupId":"sg-3b27cb53"}],"ClientToken":"","SubnetId":"subnet-7fb2c004","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:15:1d:54:51:be","SourceDestCheck":true,"VpcId":"vpc-4d5c1c24","Description":"","NetworkInterfaceId":"eni-86b388d4","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-229-101-56.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.229.101.56"}],"PrivateDnsName":"ip-10-229-101-56.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-7c30351c","AttachTime":"2017-10-09T14:57:23.000Z"},"Groups":[{"GroupName":"logging-cdp-sg","GroupId":"sg-3b27cb53"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7fb2c004","PrivateIpAddress":"10.229.101.56"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0df46b50c43e414e3","AttachTime":"2017-10-09T14:57:23.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0fba9b9407911e4d3","AttachTime":"2017-10-09T14:58:43.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"logging-cdp-secondary-master0","Key":"Name"},{"Value":"cdp","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"logging","Key":"Environment"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"secondary-master","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-09a5ab2573f3b52b9","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.77","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-0d0585a489c1b07a7","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-77.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:ba:b1:0f:64:fb","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-67bad61d","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-77.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.77"}],"PrivateDnsName":"ip-10-227-101-77.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-0772ef67","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.77"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0f917f77523045e20","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0c261c90476eb18ef","AttachTime":"2017-08-07T19:26:04.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp","Key":"Project"},{"Value":"dev","Key":"Environment"},{"Value":"primary-master","Key":"Type"},{"Value":"dev-cdp-primary-master0","Key":"Name"},{"Value":"dev_cdp_devops","Key":"Owner"}],"AmiLaunchIndex":0}],"ReservationId":"r-0d7c7999ea43d07f4","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOpt' +
                'imized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-0b3084a066c749934","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-security-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0b6527256fee7cfa2","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:12.000Z","PrivateIpAddress":"10.230.24.175","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-0da0796de176881a8","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-175.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-ambari","GroupId":"sg-e628a68e"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:99:aa:6a:8a:ca","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-ambari","NetworkInterfaceId":"eni-38019f6d","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-175.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.175"}],"PrivateDnsName":"ip-10-230-24-175.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-b11f4d58","AttachTime":"2018-01-04T14:07:12.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-ambari","GroupId":"sg-e628a68e"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.175"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0297e4b0001a94040","AttachTime":"2018-01-04T14:07:13.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-ambari-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-06975c748b42cfc5a","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:09.000Z","PrivateIpAddress":"10.230.24.79","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-0314ada0f6966ee2c","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-79.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-security","GroupId":"sg-9db331f5"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:94:05:38:2b:7e","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-security","NetworkInterfaceId":"eni-853ea0d0","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-79.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.79"}],"PrivateDnsName":"ip-10-230-24-79.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-e01f4d09","AttachTime":"2018-01-04T14:07:09.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-security","GroupId":"sg-9db331f5"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.79"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0b0a673f17454d2aa","AttachTime":"2018-01-04T14:07:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-security-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-00c91d08a9d197823","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.28.59","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-725b671b","StateTransitionReason":"","InstanceId":"i-060efdaca835cbbb5","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-28-59.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-ed0e8e85"}],"ClientToken":"","SubnetId":"subnet-bebc91c5","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:78:5b:20:44:ec","SourceDestCheck":true,"VpcId":"vpc-725b671b","Description":"private ip address for abc alpha dev stack ingest primary-flow","NetworkInterfaceId":"eni-a07dcbf5","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-28-59.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.28.59"}],"PrivateDnsName":"ip-10-230-28-59.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-65de2806","AttachTime":"2017-12-15T13:56:17.000Z"},"Groups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-ed0e8e85"}],' +
                '"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-bebc91c5","PrivateIpAddress":"10.230.28.59"}],"SourceDestCheck":true,"Placem' +
                'ent":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0caf9165b897c8cbf","AttachTime":"2017-12-15T13:56:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ingest-primary-flow-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0e8b18a710e2e6c90","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.102","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-0be84db2ab8595f9c","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-102.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:5a:bb:5e:33:2b","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-c3b9d5b9","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-102.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.102"}],"PrivateDnsName":"ip-10-227-101-102.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-6173ee01","AttachTime":"2017-08-07T19:23:09.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.102"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0e5e85f76e5d3ebd9","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-06fb5b5ab438a5486","AttachTime":"2017-08-07T19:26:20.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-cdp-primary-data2","Key":"Name"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"primary-data","Key":"Type"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev","Key":"Environment"},{"Value":"cdp","Key":"Project"}],"AmiLaunchIndex":0}],"ReservationId":"r-0ba82522406b5d2bb","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.49","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-0cf2e21313bdf14e5","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-49.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:c2:36:77:2d:8b","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-49b8d433","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-49.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.49"}],"PrivateDnsName":"ip-10-227-101-49.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-c973eea9","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.49"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-006538299ddf3cb72","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-074583a6ef0f0501e","AttachTime":"2017-08-07T19:26:20.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-cdp-primary-data0","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"primary-data","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-0dbc4821b13dec817","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:14.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:54:25 GMT)","InstanceId":"i-0aa1a4f64c73e94b1","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-master-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0a9333e3fd60ca6a6","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:12.000Z","PrivateIpAddress":"10.230.24.233","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","Produc' +
                'tCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-0642433e3176426a5","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-233.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-10b03278"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:51:3c:5b:cc:f2","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-data","NetworkInterfaceId":"eni-1f009e4a","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-233.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.233"}],"PrivateDnsName":"ip-10-230-24-233.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-731f4d9a","AttachTime":"2018-01-04T14:07:12.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-10b03278"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.233"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-03fefd93b5e3609ee","AttachTime":"2018-01-04T14:07:12.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-data-az1-3","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0f3eaeb3f89e21e51","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-02T09:35:12.000Z","PublicIpAddress":"52.56.154.20","PrivateIpAddress":"10.230.1.68","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-a51722cc","StateTransitionReason":"","InstanceId":"i-0256795a37ebfb32e","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-230-1-68.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-access-cdp-sg","GroupId":"sg-a073c4c8"}],"ClientToken":"","SubnetId":"subnet-bea481c5","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:37:cf:9d:c4:c2","SourceDestCheck":true,"VpcId":"vpc-a51722cc","Description":"Primary network interface","NetworkInterfaceId":"eni-fde438a8","PrivateIpAddresses":[{"PrivateIpAddress":"10.230.1.68","Primary":true,"Association":{"PublicIp":"52.56.154.20","PublicDnsName":"","IpOwnerId":"amazon"}}],"SubnetId":"subnet-bea481c5","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-7d4f541d","AttachTime":"2017-11-30T14:15:42.000Z"},"Groups":[{"GroupName":"dev-access-cdp-sg","GroupId":"sg-a073c4c8"}],"Ipv6Addresses":[],"OwnerId":"484676447925","PrivateIpAddress":"10.230.1.68","Association":{"PublicIp":"52.56.154.20","PublicDnsName":"","IpOwnerId":"amazon"}}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0ea1e073f71957e4e","AttachTime":"2017-11-30T14:15:43.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-access-cdp-bastion-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0857c3abd2e4ae255","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.31.4","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-6659650f","StateTransitionReason":"","InstanceId":"i-0b73883ef24e3e0a1","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-31-4.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-int-logging-syslog","GroupId":"sg-dc1494b4"}],"ClientToken":"","SubnetId":"subnet-11bc916a","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:ac:c5:6d:48:a2","SourceDestCheck":true,"VpcId":"vpc-6659650f","Description":"private ip address for abc alpha dev stack int-logging syslog","NetworkInterfaceId":"eni-0265d357","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-31-4.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.31.4"}],"PrivateDnsName":"ip-10-230-31-4.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-32d12751","AttachTime":"2017-12-15T13:56:16.000Z"},"Groups":[{"GroupName":"abc-alpha-int-logging-syslog","GroupId":"sg-dc1494b4"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-11bc916a","PrivateIpAddress":"10.230.31.4"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0773386a9b177a28b","AttachTime":"2017-12-15T13:56:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-int-logging-syslog-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-01ce18b7b8ee0d4d1","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.30","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-011bcc37aac12b515","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-30.eu-west-2.compute.internal","KeyName' +
                '":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:22:b8:d1:7c:73","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-78b9d502","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-30.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.30"}],"PrivateDnsName":"ip-10-227-101-30.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-8173eee1","AttachTime":"2017-08-07T19:23:31.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.30"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-04d20cca87e1cb6e2","AttachTime":"2017-08-07T19:23:32.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-004bc1ff42a9e3902","AttachTime":"2017-08-07T19:26:20.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"primary-data","Key":"Type"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"dev","Key":"Environment"},{"Value":"dev-cdp-primary-data1","Key":"Name"},{"Value":"cdp","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"}],"AmiLaunchIndex":0}],"ReservationId":"r-0847d0fd9c0c8aaa0","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.228.101.50","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-44f6b02d","StateTransitionReason":"User initiated (2018-01-02 09:38:31 GMT)","InstanceId":"i-0dad34227cefde49d","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-228-101-50.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"ClientToken":"","SubnetId":"subnet-083b4873","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:89:d8:a4:7d:d6","SourceDestCheck":true,"VpcId":"vpc-44f6b02d","Description":"","NetworkInterfaceId":"eni-4f0c331d","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-50.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.50"}],"PrivateDnsName":"ip-10-228-101-50.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-12d2eb72","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-083b4873","PrivateIpAddress":"10.228.101.50"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0d3017bb01e63c296","AttachTime":"2017-10-06T22:12:55.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0d4f920e2fc54be0e","AttachTime":"2017-10-06T22:14:16.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"secondary-data","Key":"Type"},{"Value":"ingest","Key":"Environment"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"ingest-cdp-secondary-data1","Key":"Name"},{"Value":"cdp","Key":"Project"}],"AmiLaunchIndex":0}],"ReservationId":"r-01a2d1e0f0b213011","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.0.39","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-461e2b2f","StateTransitionReason":"","InstanceId":"i-0e9f187558baa6c71","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-230-0-39.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"launch-wizard-2","GroupId":"sg-3a7ccb52"}],"ClientToken":"","SubnetId":"subnet-2baa8f50","InstanceType":"m4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:0e:cb:c9:4c:e2","SourceDestCheck":true,"VpcId":"vpc-461e2b2f","Description":"Primary network interface","NetworkInterfaceId":"eni-4419c511","PrivateIpAddresses":[{"Primary":true,"PrivateIpAddress":"10.230.0.39"}],"SubnetId":"subnet-2baa8f50","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-e3455e83","AttachTime":"2017-11-30T14:41:12.000Z"},"Groups":[{"GroupName":"launch-wizard-2","GroupId":"sg-3a7ccb52"}],"Ipv6Addresses":[{"Ipv6Address":"2a05:d01c:a43:ec00:1188:1033:49d0:8592"}],"OwnerId":"484676447925","PrivateIpAddress":"10.230.0.39"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-06e50fcdda19e9324","AttachTime":"2017-11-30T14:41:12.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-ci-cdp-ci-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-05ea87f6135bab745","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.253","Produc' +
                'tCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-0c30fa3f0b33aa894","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-253.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:62:c5:9d:a5:5f","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-48b8d432","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-253.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.253"}],"PrivateDnsName":"ip-10-227-101-253.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-6273ee02","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.253"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0a048fd3a85bcd321","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0d8f54d557412f727","AttachTime":"2017-08-07T19:26:04.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"primary-ambari-server","Key":"Type"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"dev-cdp-primary-ambari-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0fd190fc76a9e0947","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"ec2-35-177-178-3.eu-west-2.compute.amazonaws.com","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-02T09:35:12.000Z","PublicIpAddress":"35.177.178.3","PrivateIpAddress":"10.228.100.72","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-44f6b02d","StateTransitionReason":"","InstanceId":"i-02352d0843861f2f2","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-228-100-72.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"ClientToken":"","SubnetId":"subnet-48354633","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:8e:38:e1:99:f0","SourceDestCheck":true,"VpcId":"vpc-44f6b02d","Description":"","NetworkInterfaceId":"eni-060a3554","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-100-72.eu-west-2.compute.internal","PrivateIpAddress":"10.228.100.72","Primary":true,"Association":{"PublicIp":"35.177.178.3","PublicDnsName":"ec2-35-177-178-3.eu-west-2.compute.amazonaws.com","IpOwnerId":"amazon"}}],"PrivateDnsName":"ip-10-228-100-72.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-52d2eb32","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","PrivateIpAddress":"10.228.100.72","SubnetId":"subnet-48354633","Association":{"PublicIp":"35.177.178.3","PublicDnsName":"ec2-35-177-178-3.eu-west-2.compute.amazonaws.com","IpOwnerId":"amazon"}}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-08ec292dc424aee0e","AttachTime":"2017-10-06T22:12:55.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"ingest-cdp-bastion-server0","Key":"Name"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"bastion-server","Key":"Type"},{"Value":"cdp","Key":"Project"},{"Value":"ingest","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-082a4ee9c9e68929e","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.58","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-02026c25f4a68257c","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-58.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:9b:0e:89:2d:44","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-a39451f6","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-58.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.58"}],"PrivateDnsName":"ip-10-230-1-58.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-509215b9","AttachTime":"2017-11-16T15:44:51.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.58"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-06eaa3a7c77c9e6a1","AttachTime":"2017-11-16T15:44:51.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0c23b5ddbcf5bfea7","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","Virtua' +
                'lizationType":"hvm","Tags":[{"Value":"test-cdp-primary-master0","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"test","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"primary-master","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-005e9247fd0c20977","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:14.000Z","PrivateIpAddress":"10.230.30.141","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-d15965b8","StateTransitionReason":"","InstanceId":"i-014c2082f087af8e9","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-30-141.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-usrdb-keycloak","GroupId":"sg-38109050"}],"ClientToken":"","SubnetId":"subnet-f7bf928c","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:32:74:39:2a:be","SourceDestCheck":true,"VpcId":"vpc-d15965b8","Description":"private ip address for abc alpha dev stack usrdb keycloak","NetworkInterfaceId":"eni-1d7acc48","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-30-141.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.30.141"}],"PrivateDnsName":"ip-10-230-30-141.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-eed1278d","AttachTime":"2017-12-15T13:56:02.000Z"},"Groups":[{"GroupName":"abc-alpha-usrdb-keycloak","GroupId":"sg-38109050"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f7bf928c","PrivateIpAddress":"10.230.30.141"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-01511a372b7fa71a5","AttachTime":"2017-12-15T13:56:03.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-usrdb-keycloak-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0d1f73253106a9257","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-22T11:34:57.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:54:35 GMT)","InstanceId":"i-07cc0dac00e2b6c60","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary_database-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-01cbd31be6f7d3c8d","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:29.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-042b1a52c49847a80","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-data-az1-3","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-03bda28f6fe838ce2","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.56","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-020719aa8c8b9c952","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-56.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:77:57:20:85:d9","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-79b9d503","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-56.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.56"}],"PrivateDnsName":"ip-10-227-101-56.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-9e70edfe","AttachTime":"2017-08-07T19:23:31.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.56"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-081955d61f50dcea2","AttachTime":"2017-08-07T19:23:32.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0ad20b10eb22cdb05","AttachTime":"2017-08-07T19:25:59.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","Virtua' +
                'lizationType":"hvm","Tags":[{"Value":"secondary-master","Key":"Type"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp","Key":"Project"},{"Value":"dev","Key":"Environment"},{"Value":"dev-cdp-secondary-master0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-04e9658eeda92a349","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.228.101.42","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-44f6b02d","StateTransitionReason":"","InstanceId":"i-058bdf106a4e84be4","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-228-101-42.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"ClientToken":"","SubnetId":"subnet-083b4873","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:86:77:86:b3:ec","SourceDestCheck":true,"VpcId":"vpc-44f6b02d","Description":"","NetworkInterfaceId":"eni-070a3555","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-42.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.42"}],"PrivateDnsName":"ip-10-228-101-42.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-0cd2eb6c","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-083b4873","PrivateIpAddress":"10.228.101.42"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-07a02cd07654629ab","AttachTime":"2017-10-06T22:12:55.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0a19875fa6746f632","AttachTime":"2017-10-06T22:14:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"ingest","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"primary-master","Key":"Type"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"ingest-cdp-primary-flow0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-047fc7f8a4b9492a5","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:09.000Z","PrivateIpAddress":"10.230.24.135","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-073198429e6f6bf92","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-135.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-58b63430"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:07:ed:9a:20:36","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-master","NetworkInterfaceId":"eni-843ea0d1","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-135.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.135"}],"PrivateDnsName":"ip-10-230-24-135.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-631c4e8a","AttachTime":"2018-01-04T14:07:09.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-58b63430"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.135"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-069a356ffb323a634","AttachTime":"2018-01-04T14:07:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-master-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0778143f920456e96","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:09.000Z","PrivateIpAddress":"10.230.24.253","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-0ad54507a0702215a","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-253.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary_database","GroupId":"sg-11028879"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:a7:ad:5a:6b:ba","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary_database","NetworkInterfaceId":"eni-6d3fa138","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-253.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.253"}],"PrivateDnsName":"ip-10-230-24-253.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-5e1d4fb7","AttachTime":"2018-01-04T14:07:09.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary_database","GroupId":"sg-11028879"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.253"}],"SourceDestCheck":true,"Placeme' +
                'nt":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0bb85c7c6e4f49cb6","AttachTime":"2018-01-04T14:07:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary_database-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-00ce0fa5997d36e6d","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.34","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-0e03f580b46144a53","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-34.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:0b:c0:1a:40:0a","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-368a4f63","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-34.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.34"}],"PrivateDnsName":"ip-10-230-1-34.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-1a9017f3","AttachTime":"2017-11-16T15:44:51.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.34"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-006c7a970f0a895bd","AttachTime":"2017-11-16T15:44:51.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-099ee04d06419e8e0","AttachTime":"2017-11-16T15:47:02.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"cdp","Key":"Project"},{"Value":"primary-data","Key":"Type"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"test-cdp-primary-data1","Key":"Name"},{"Value":"test","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-0a1572b7b4b68819f","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:10.000Z","PrivateIpAddress":"10.230.24.48","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-01083e9662c5fa990","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-48.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-10b03278"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:16:82:a7:1d:70","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-data","NetworkInterfaceId":"eni-2d3ea078","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-48.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.48"}],"PrivateDnsName":"ip-10-230-24-48.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-bf1c4e56","AttachTime":"2018-01-04T14:07:10.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-10b03278"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.48"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0c417a845fbbda4c6","AttachTime":"2018-01-04T14:07:11.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-data-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0a267398f261bdedf","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-0d152c47109c6a1b4","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-data-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0247105017eeec7cd","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.229.101.111","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-4d5c1c24","StateTransitionReason":"","InstanceId":"i-0d18ba330e851d6ce","EnaSupport":false,"ImageId":"ami-8dccdde9","Privat' +
                'eDnsName":"ip-10-229-101-111.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"logging-cdp-sg","GroupId":"sg-3b27cb53"}],"ClientToken":"","SubnetId":"subnet-7fb2c004","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:23:c3:9a:36:02","SourceDestCheck":true,"VpcId":"vpc-4d5c1c24","Description":"","NetworkInterfaceId":"eni-17b18a45","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-229-101-111.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.229.101.111"}],"PrivateDnsName":"ip-10-229-101-111.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-2f30354f","AttachTime":"2017-10-09T14:57:23.000Z"},"Groups":[{"GroupName":"logging-cdp-sg","GroupId":"sg-3b27cb53"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7fb2c004","PrivateIpAddress":"10.229.101.111"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0ae35a2fce66a00aa","AttachTime":"2017-10-09T14:57:24.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0cc2554390293cba9","AttachTime":"2017-10-09T14:58:43.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp","Key":"Project"},{"Value":"logging","Key":"Environment"},{"Value":"logging-cdp-primary-master0","Key":"Name"},{"Value":"primary-master","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-08f6aac1906894647","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-08d4559d2a5c83431","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-secondary-security-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-06496ecf29405fab5","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.9.191","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-e3fbda8a","StateTransitionReason":"","InstanceId":"i-095e065c0ebaa4996","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-9-191.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-logging-cdp-sg","GroupId":"sg-b429f2dc"}],"ClientToken":"","SubnetId":"subnet-64194a1f","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:77:82:14:89:60","SourceDestCheck":true,"VpcId":"vpc-e3fbda8a","Description":"","NetworkInterfaceId":"eni-c2af6a97","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-9-191.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.9.191"}],"PrivateDnsName":"ip-10-230-9-191.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-7fad2a96","AttachTime":"2017-11-16T16:58:15.000Z"},"Groups":[{"GroupName":"test-logging-cdp-sg","GroupId":"sg-b429f2dc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-64194a1f","PrivateIpAddress":"10.230.9.191"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-09ad7da20b7e43796","AttachTime":"2017-11-16T16:58:16.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0bc64765804a44e59","AttachTime":"2017-11-16T16:59:41.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"secondary-syslog","Key":"Type"},{"Value":"cdp","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"test-logging","Key":"Environment"},{"Value":"test-logging-cdp-secondary-syslog0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0dedaf898dc636395","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.101","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-0811d366938a67250","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-101.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:4f:c1:c4:4d:63","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-0bbed271","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-101.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.10' +
                '1.101"}],"PrivateDnsName":"ip-10-227-101-101.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-6373ee03","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.101"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-032692208aa7cc89a","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0f90c1921363e6226","AttachTime":"2017-08-07T19:25:49.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-cdp-secondary-data1","Key":"Name"},{"Value":"dev","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"secondary-data","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-051b5986093af7a1e","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.30.136","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-d15965b8","StateTransitionReason":"","InstanceId":"i-0eae77728abee9e7d","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-30-136.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-usrdb-ranger","GroupId":"sg-270f8f4f"}],"ClientToken":"","SubnetId":"subnet-f7bf928c","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:1a:ca:96:3e:22","SourceDestCheck":true,"VpcId":"vpc-d15965b8","Description":"private ip address for abc alpha dev stack usrdb ranger","NetworkInterfaceId":"eni-977bcdc2","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-30-136.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.30.136"}],"PrivateDnsName":"ip-10-230-30-136.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-53de2830","AttachTime":"2017-12-15T13:56:02.000Z"},"Groups":[{"GroupName":"abc-alpha-usrdb-ranger","GroupId":"sg-270f8f4f"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f7bf928c","PrivateIpAddress":"10.230.30.136"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-02f262a316af0fec7","AttachTime":"2017-12-15T13:56:03.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-usrdb-ranger-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-03f7059a57a3eadb5","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-22T11:34:57.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-008d33cbe2c8f9951","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-secondary_database-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0e38c406704ba5bc2","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.228.101.92","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-44f6b02d","StateTransitionReason":"","InstanceId":"i-019f25c8c816099e4","EnaSupport":true,"ImageId":"ami-80f3ede4","PrivateDnsName":"ip-10-228-101-92.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"ClientToken":"","SubnetId":"subnet-083b4873","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:0c:07:12:69:cc","SourceDestCheck":true,"VpcId":"vpc-44f6b02d","Description":"Primary network interface","NetworkInterfaceId":"eni-74913221","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-92.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.92"}],"PrivateDnsName":"ip-10-228-101-92.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5d64863e","AttachTime":"2017-12-07T17:28:53.000Z"},"Groups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-083b4873","PrivateIpAddress":"10.228.101.92"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-031c32a319d8d6c74","AttachTime":"2017-12-07T17:28:54.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0a5e66e8a73fb8852","AttachTime":"2017-12-11T13:08:42.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","Virtua' +
                'lizationType":"hvm","Tags":[{"Value":"test-ingest0","Key":"Name"}],"AmiLaunchIndex":2},{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.228.101.158","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-44f6b02d","StateTransitionReason":"","InstanceId":"i-0b4487fbc6731bc14","EnaSupport":true,"ImageId":"ami-80f3ede4","PrivateDnsName":"ip-10-228-101-158.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"ClientToken":"","SubnetId":"subnet-083b4873","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:1f:5e:1d:d2:f6","SourceDestCheck":true,"VpcId":"vpc-44f6b02d","Description":"Primary network interface","NetworkInterfaceId":"eni-77913222","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-158.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.158"}],"PrivateDnsName":"ip-10-228-101-158.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5c64863f","AttachTime":"2017-12-07T17:28:53.000Z"},"Groups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-083b4873","PrivateIpAddress":"10.228.101.158"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0ef375c746d7995a8","AttachTime":"2017-12-07T17:28:54.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-065e10b572b14abf7","AttachTime":"2017-12-11T13:13:18.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-ingest2","Key":"Name"}],"AmiLaunchIndex":1},{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.228.101.171","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-44f6b02d","StateTransitionReason":"","InstanceId":"i-07c6c6c51d6036a66","EnaSupport":true,"ImageId":"ami-80f3ede4","PrivateDnsName":"ip-10-228-101-171.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"ClientToken":"","SubnetId":"subnet-083b4873","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:02:82:f4:e2:7a","SourceDestCheck":true,"VpcId":"vpc-44f6b02d","Description":"Primary network interface","NetworkInterfaceId":"eni-75913220","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-171.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.171"}],"PrivateDnsName":"ip-10-228-101-171.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-53648630","AttachTime":"2017-12-07T17:28:53.000Z"},"Groups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-083b4873","PrivateIpAddress":"10.228.101.171"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0ce9c90cc82ee51e4","AttachTime":"2017-12-07T17:28:54.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-001b23d2f95df2f2b","AttachTime":"2017-12-11T13:15:04.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-ingest1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-06cca7e9200b954d9","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-19T13:39:52.000Z","PrivateIpAddress":"10.230.29.23","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-a15a66c8","StateTransitionReason":"","InstanceId":"i-091469b02e3498e12","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-29-23.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ci-jenkins","GroupId":"sg-d429a7bc"}],"ClientToken":"","SubnetId":"subnet-08bc9173","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:77:b4:d4:05:b0","SourceDestCheck":true,"VpcId":"vpc-a15a66c8","Description":"private ip address for abc alpha dev stack ci jenkins","NetworkInterfaceId":"eni-b507bee0","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-29-23.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.29.23"}],"PrivateDnsName":"ip-10-230-29-23.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-26847845","AttachTime":"2017-12-19T13:39:52.000Z"},"Groups":[{"GroupName":"abc-alpha-ci-jenkins","GroupId":"sg-d429a7bc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-08bc9173","PrivateIpAddress":"10.230.29.23"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0df10518865d6ddd9","AttachTime":"2017-12-19T13:39:52.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ci-jenkins-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0a9c3897737c06bb3","Groups":[],"OwnerId":"484676447925"},' +
                '{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.13","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-09925911e5cf9c5d4","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-13.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:85:2e:4b:9e:30","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-27955072","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-13.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.13"}],"PrivateDnsName":"ip-10-230-1-13.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-99901770","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.13"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0602237f98769dbc9","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0fdfbe0981c5d5db4","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"primary-security","Key":"Type"},{"Value":"test-cdp-primary-security0","Key":"Name"},{"Value":"cdp","Key":"Project"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"test","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-0ae192593a2d8fc32","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.129","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-042b1be7b628c9f97","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-129.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:54:b2:cf:13:20","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-d7945182","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-129.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.129"}],"PrivateDnsName":"ip-10-230-1-129.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5d9215b4","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.129"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0d8af9cc951545ed3","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0daf13d35a140018b","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"test","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"secondary-master","Key":"Type"},{"Value":"test-cdp-secondary-master1","Key":"Name"},{"Value":"cdp_devops","Key":"Owner"}],"AmiLaunchIndex":0}],"ReservationId":"r-062493ac48ec632ac","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.228.101.192","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-44f6b02d","StateTransitionReason":"","InstanceId":"i-0ff8b0293d7aeee3f","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-228-101-192.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"ClientToken":"","SubnetId":"subnet-083b4873","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:9c:71:9b:28:12","SourceDestCheck":true,"VpcId":"vpc-44f6b02d","Description":"","NetworkInterfaceId":"eni-930a35c1","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-192.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.192"}],"PrivateDnsName":"ip-10-228-101-192.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-a2d2ebc2","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-083b4873","PrivateIpAddress":"10.228.101.192"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-06b4cf7432297e419","AttachTime":"2017-10-06T22:12:55.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0ffa2ee6814d90b80","AttachTime":"2017-10-06T22:14:16.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1",' +
                '"VirtualizationType":"hvm","Tags":[{"Value":"ingest","Key":"Environment"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"secondary-data","Key":"Type"},{"Value":"cdp","Key":"Project"},{"Value":"ingest-cdp-primary-flow2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0335475489899fbd0","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-0f11c9226f5f0b397","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-data-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-00b250b58e63fe9af","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:40.000Z","PrivateIpAddress":"10.230.24.77","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-086fa71c3ff9a49a9","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-77.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-broker","GroupId":"sg-e5b6348d"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:47:b2:20:99:f0","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-broker","NetworkInterfaceId":"eni-223ea077","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-77.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.77"}],"PrivateDnsName":"ip-10-230-24-77.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-d01d4f39","AttachTime":"2018-01-04T14:07:40.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-broker","GroupId":"sg-e5b6348d"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.77"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-04e1bad78dee8f976","AttachTime":"2018-01-04T14:07:40.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-broker-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0d3eeabb7d26ccc98","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:33.000Z","PrivateIpAddress":"10.230.24.191","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-0a18bdc35ec50f483","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-191.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-broker","GroupId":"sg-e5b6348d"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:2c:a8:05:db:8a","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-broker","NetworkInterfaceId":"eni-c53fa190","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-191.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.191"}],"PrivateDnsName":"ip-10-230-24-191.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-fc1d4f15","AttachTime":"2018-01-04T14:07:33.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-broker","GroupId":"sg-e5b6348d"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.191"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-01318513b0c86fa84","AttachTime":"2018-01-04T14:07:33.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-broker-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-09dbae07bfca33ff0","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:23.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-023439a073143fa1e","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm",' +
                '"Tags":[{"Value":"abc-alpha-dev-store-primary-broker-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0aee9bac67f38cc19","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.128","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-00b21c39db2f7f46e","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-128.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:0b:23:56:15:d5","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-c0b9d5ba","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-128.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.128"}],"PrivateDnsName":"ip-10-227-101-128.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-8a73eeea","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.128"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-022dad598827adad1","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-088bb29b08b86eff6","AttachTime":"2017-08-07T19:25:49.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"dev-cdp-secondary-data0","Key":"Name"},{"Value":"cdp","Key":"Project"},{"Value":"dev","Key":"Environment"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"secondary-data","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-093078fd1c73bd6ac","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.44","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"User initiated (2018-01-02 09:38:31 GMT)","InstanceId":"i-0d620af462390bd6c","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-44.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"RJbpa1506351747264","SubnetId":"subnet-f3c94588","InstanceType":"d2.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:98:97:cd:03:62","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"Primary network interface","NetworkInterfaceId":"eni-ddefcc8f","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-44.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.44"}],"PrivateDnsName":"ip-10-227-101-44.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5bb8973b","AttachTime":"2017-09-25T15:02:27.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.44"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-07fb9a4a799dff3e3","AttachTime":"2017-09-25T15:02:28.000Z"}},{"DeviceName":"/dev/sde","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0b8b4ded0acfbf768","AttachTime":"2017-09-25T15:02:28.000Z"}},{"DeviceName":"/dev/sdf","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-096e0fe8d0561229a","AttachTime":"2017-09-25T16:03:20.000Z"}},{"DeviceName":"/dev/sdg","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0456ab88afa2b3d65","AttachTime":"2017-09-25T16:09:32.000Z"}},{"DeviceName":"/dev/sdh","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-06575dc7de84632c0","AttachTime":"2017-09-25T16:10:29.000Z"}},{"DeviceName":"/dev/sdi","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0207b3bc7b2db3962","AttachTime":"2017-09-25T16:11:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev","Key":"Environment"},{"Value":"dmcrypt","Key":"Name"},{"Value":"DMCRYPT","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-0ef62e3bdab922fd8","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:23.000Z","PrivateIpAddress":"10.230.28.57","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-725b671b","StateTransitionReason":"","InstanceId":"i-09e3c6206731b7ad6","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-28-57.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-ed0e8e85"}],"ClientToken":"","SubnetId":"subnet-bebc91c5","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:b2:09:49:3b:4e","SourceDestCheck":true,"VpcId":"vpc-725b671b","Description":"private ip address for abc alpha dev stack ingest primary-flow","NetworkInterfaceId":"eni-847ccad1",' +
                '"PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-28-57.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.28.57"}],"PrivateDnsName":"ip-10-230-28-57.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-f3de2890","AttachTime":"2017-12-15T13:56:12.000Z"},"Groups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-ed0e8e85"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-bebc91c5","PrivateIpAddress":"10.230.28.57"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-088c767c5959e3ba9","AttachTime":"2017-12-15T13:56:13.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ingest-primary-flow-az1-3","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-00f5ce7c7624426f7","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:25.000Z","PrivateIpAddress":"10.227.101.142","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-06152455c1651914e","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-142.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"c4.4xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:17:90:da:60:15","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-9fbad6e5","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-142.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.142"}],"PrivateDnsName":"ip-10-227-101-142.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-9c70edfc","AttachTime":"2017-08-07T19:23:25.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.142"},{"Status":"in-use","MacAddress":"06:73:78:40:19:b6","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"dev-cdp-test-nic-01","NetworkInterfaceId":"eni-f1050ca3","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-104-104.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.104.104"}],"PrivateDnsName":"ip-10-227-104-104.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":1,"DeleteOnTermination":false,"AttachmentId":"eni-attach-498456a0","AttachTime":"2017-10-17T16:04:57.000Z"},"Groups":[{"GroupName":"default","GroupId":"sg-5ca2ff35"},{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"},{"GroupName":"dev-cdp-kafka-sg","GroupId":"sg-0caef465"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-bb057cc0","PrivateIpAddress":"10.227.104.104"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-04e6d725a0af8085c","AttachTime":"2017-08-07T19:23:26.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-09a1abc5ddabad31f","AttachTime":"2017-08-07T19:26:04.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-cdp-client-server0","Key":"Name"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"client-server","Key":"Type"},{"Value":"cdp","Key":"Project"},{"Value":"dev","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-0b3116b1d56f4cbf6","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:09.000Z","PrivateIpAddress":"10.230.24.101","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-0534233dd6b828364","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-101.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-secondary-security","GroupId":"sg-e7b6348f"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:12:99:1e:ba:46","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store secondary-security","NetworkInterfaceId":"eni-db3fa18e","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-101.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.101"}],"PrivateDnsName":"ip-10-230-24-101.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-e11f4d08","AttachTime":"2018-01-04T14:07:09.000Z"},"Groups":[{"GroupName":"abc-alpha-store-secondary-security","GroupId":"sg-e7b6348f"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.101"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-046294f701e13f6ae","AttachTime":"2018-01-04T14:07:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-secondary-security-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0d68102fb06808f35","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},' +
                '"PublicDnsName":"ec2-35-176-39-195.eu-west-2.compute.amazonaws.com","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-02T09:35:12.000Z","PublicIpAddress":"35.176.39.195","PrivateIpAddress":"10.230.0.24","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-0a054f692e1349afa","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-0-24.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-ad1340d6","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:d1:9b:04:45:32","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-cf97529a","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-0-24.eu-west-2.compute.internal","PrivateIpAddress":"10.230.0.24","Primary":true,"Association":{"PublicIp":"35.176.39.195","PublicDnsName":"ec2-35-176-39-195.eu-west-2.compute.amazonaws.com","IpOwnerId":"amazon"}}],"PrivateDnsName":"ip-10-230-0-24.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-cf9e1926","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","PrivateIpAddress":"10.230.0.24","SubnetId":"subnet-ad1340d6","Association":{"PublicIp":"35.176.39.195","PublicDnsName":"ec2-35-176-39-195.eu-west-2.compute.amazonaws.com","IpOwnerId":"amazon"}}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-09648e14f6a880e35","AttachTime":"2017-11-16T15:44:38.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"test","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"test-cdp-bastion-server0","Key":"Name"},{"Value":"bastion-server","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-0cad33b515159ff08","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:14.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:54:35 GMT)","InstanceId":"i-0b900742c5e10ddf0","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-master-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0ab2974bb4dfea0c4","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.228.101.187","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-44f6b02d","StateTransitionReason":"","InstanceId":"i-043ea2dfaa879ab83","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-228-101-187.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"ClientToken":"","SubnetId":"subnet-083b4873","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:74:b4:09:a2:e4","SourceDestCheck":true,"VpcId":"vpc-44f6b02d","Description":"","NetworkInterfaceId":"eni-4e09361c","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-187.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.187"}],"PrivateDnsName":"ip-10-228-101-187.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-13d2eb73","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-cdp-sg","GroupId":"sg-947b98fc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-083b4873","PrivateIpAddress":"10.228.101.187"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-046190078d3637643","AttachTime":"2017-10-06T22:12:55.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-06efb44a4ca17b9b0","AttachTime":"2017-10-06T22:14:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp","Key":"Project"},{"Value":"ingest-cdp-primary-flow1","Key":"Name"},{"Value":"ingest","Key":"Environment"},{"Value":"secondary-master","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-0b6b44f42a26715ce","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.178","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-043834caa1a2836d2","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-178.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588",' +
                '"InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:55:db:31:9e:ef","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-0abed270","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-178.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.178"}],"PrivateDnsName":"ip-10-227-101-178.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-8973eee9","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.178"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0af61dfa74a35ffc1","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0d28a28c5c8b9ae4b","AttachTime":"2017-08-07T19:25:59.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"cdp","Key":"Project"},{"Value":"dev","Key":"Environment"},{"Value":"primary-security","Key":"Type"},{"Value":"dev-cdp-primary-security0","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_cdp_devops","Key":"Owner"}],"AmiLaunchIndex":0}],"ReservationId":"r-0c6ce5cdb874281d9","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.0.29","ProductCodes":[],"VpcId":"vpc-461e2b2f","StateTransitionReason":"","InstanceId":"i-072df6db87e3462b4","EnaSupport":true,"ImageId":"ami-e7d6c983","PrivateDnsName":"ip-10-230-0-29.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"launch-wizard-3","GroupId":"sg-c4f548ac"}],"ClientToken":"","SubnetId":"subnet-2baa8f50","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:2d:95:0a:91:3e","SourceDestCheck":true,"VpcId":"vpc-461e2b2f","Description":"Primary network interface","NetworkInterfaceId":"eni-de4ce98b","PrivateIpAddresses":[{"Primary":true,"PrivateIpAddress":"10.230.0.29"}],"SubnetId":"subnet-2baa8f50","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-eed6308d","AttachTime":"2017-12-04T15:00:09.000Z"},"Groups":[{"GroupName":"launch-wizard-3","GroupId":"sg-c4f548ac"}],"Ipv6Addresses":[{"Ipv6Address":"2a05:d01c:a43:ec00:e5a8:6086:491b:df63"}],"OwnerId":"484676447925","PrivateIpAddress":"10.230.0.29"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/xvda","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0cafdab1675ae4fd5","AttachTime":"2017-12-04T15:00:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","IamInstanceProfile":{"Id":"AIPAJHXVFO5DHPCZIXTBA","Arn":"arn:aws:iam::484676447925:instance-profile/terraform"},"RootDeviceName":"/dev/xvda","VirtualizationType":"hvm","AmiLaunchIndex":0}],"ReservationId":"r-0cd493167f3d8e41d","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:56.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-0d97cd9040ad2336a","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-master-az1-3","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0d5952422cf2c2a95","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:09.000Z","PrivateIpAddress":"10.230.24.113","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-0b150064986304974","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-113.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-58b63430"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:ac:7f:0d:10:fe","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-master","NetworkInterfaceId":"eni-1c009e49","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-113.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.113"}],"PrivateDnsName":"ip-10-230-24-113.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-e31f4d0a","AttachTime":"2018-01-04T14:07:09.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-58b63430"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.113"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-05714203c06d175db","AttachTime":"2018-01-04T14:07:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-master-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0b6249b2daa7de245","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicD' +
                'nsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.98","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-09fb2a8f695d04271","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-98.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:0e:1c:2f:65:62","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-6997523c","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-98.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.98"}],"PrivateDnsName":"ip-10-230-1-98.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-499116a0","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.98"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0c68fb71a465eff60","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-06baba2368a85c275","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"cdp","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"test-cdp-secondary-security0","Key":"Name"},{"Value":"secondary-security","Key":"Type"},{"Value":"test","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-0aacb2190e92c18c2","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":48,"Name":"terminated"},"EbsOptimized":false,"LaunchTime":"2017-12-19T12:40:04.000Z","ProductCodes":[],"StateTransitionReason":"User initiated (2018-01-04 13:53:55 GMT)","InstanceId":"i-0a1e50ba9df51cd14","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"","KeyName":"dev_deployment_key","SecurityGroups":[],"ClientToken":"","InstanceType":"r4.large","NetworkInterfaces":[],"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-ambari-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-02e05161ff2fd2717","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:09.000Z","PrivateIpAddress":"10.230.24.29","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-094d776540ab54dbc","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-29.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-secondary_database","GroupId":"sg-210d8749"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:ec:d1:8f:84:18","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store secondary_database","NetworkInterfaceId":"eni-57019f02","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-29.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.29"}],"PrivateDnsName":"ip-10-230-24-29.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-601c4e89","AttachTime":"2018-01-04T14:07:09.000Z"},"Groups":[{"GroupName":"abc-alpha-store-secondary_database","GroupId":"sg-210d8749"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.29"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-01f343530538327d1","AttachTime":"2018-01-04T14:07:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-secondary_database-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-04a922dc28d32f699","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:09.000Z","PrivateIpAddress":"10.230.24.45","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-0cae91811459dbd53","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-45.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-58b63430"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:09:87:58:13:24","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-master","NetworkInterfaceId":"eni-da3fa18f","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-45.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.45"}],' +
                '"PrivateDnsName":"ip-10-230-24-45.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-b01f4d59","AttachTime":"2018-01-04T14:07:09.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-58b63430"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.45"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-08a44a51ed1cd62df","AttachTime":"2018-01-04T14:07:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-master-az1-3","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0305b1486c58b2495","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.9.130","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-e3fbda8a","StateTransitionReason":"","InstanceId":"i-0bc804646b29a9604","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-9-130.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-logging-cdp-sg","GroupId":"sg-b429f2dc"}],"ClientToken":"","SubnetId":"subnet-64194a1f","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:75:cc:ff:76:5c","SourceDestCheck":true,"VpcId":"vpc-e3fbda8a","Description":"","NetworkInterfaceId":"eni-32ae6b67","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-9-130.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.9.130"}],"PrivateDnsName":"ip-10-230-9-130.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-0cad2ae5","AttachTime":"2017-11-16T16:58:15.000Z"},"Groups":[{"GroupName":"test-logging-cdp-sg","GroupId":"sg-b429f2dc"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-64194a1f","PrivateIpAddress":"10.230.9.130"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-079105678e084de20","AttachTime":"2017-11-16T16:58:16.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0f86976f3ab55a3b5","AttachTime":"2017-11-16T16:59:41.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-logging","Key":"Environment"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"test-logging-cdp-primary-syslog0","Key":"Name"},{"Value":"cdp","Key":"Project"},{"Value":"primary-syslog","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-0eea41a3ef7d7d4f6","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-19T13:39:52.000Z","PrivateIpAddress":"10.230.29.28","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-a15a66c8","StateTransitionReason":"","InstanceId":"i-009f63aa4d2279a62","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-29-28.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ci-artifactory","GroupId":"sg-e528a68d"}],"ClientToken":"","SubnetId":"subnet-08bc9173","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:5f:7e:25:1d:56","SourceDestCheck":true,"VpcId":"vpc-a15a66c8","Description":"private ip address for abc alpha dev stack ci artifactory","NetworkInterfaceId":"eni-d8f9418d","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-29-28.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.29.28"}],"PrivateDnsName":"ip-10-230-29-28.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-21847842","AttachTime":"2017-12-19T13:39:52.000Z"},"Groups":[{"GroupName":"abc-alpha-ci-artifactory","GroupId":"sg-e528a68d"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-08bc9173","PrivateIpAddress":"10.230.29.28"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0cfcb6c45dd6afa32","AttachTime":"2017-12-19T13:39:52.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ci-artifactory-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-06940a7603cdde795","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.149","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-0ec45a469c0e5d11c","EnaSupport":false,"ImageId":"ami-8dccdde9","PrivateDnsName":"ip-10-227-101-149.eu-west-2.compute.internal",' +
                '"KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:f8:c7:a5:a2:95","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"","NetworkInterfaceId":"eni-93bfd3e9","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-149.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.149"}],"PrivateDnsName":"ip-10-227-101-149.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-cb73eeab","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.149"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-07c1330d7b008020e","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-01dd1a112826ee5c0","AttachTime":"2017-08-07T19:25:59.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"secondary-security","Key":"Type"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"dev","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"dev-cdp-secondary-security0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-034d71dc7f8707622","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.230.1.221","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-52fcdd3b","StateTransitionReason":"","InstanceId":"i-00fbd3432426418a6","EnaSupport":false,"ImageId":"ami-89627ded","PrivateDnsName":"ip-10-230-1-221.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"ClientToken":"","SubnetId":"subnet-5712412c","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:54:68:2e:92:aa","SourceDestCheck":true,"VpcId":"vpc-52fcdd3b","Description":"","NetworkInterfaceId":"eni-cc975299","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-221.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.221"}],"PrivateDnsName":"ip-10-230-1-221.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-0e9116e7","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-cdp-sg","GroupId":"sg-2906dd41"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-5712412c","PrivateIpAddress":"10.230.1.221"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0ea5515b606038c2b","AttachTime":"2017-11-16T15:44:38.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"client-server","Key":"Type"},{"Value":"cdp","Key":"Project"},{"Value":"test","Key":"Environment"},{"Value":"test-cdp-client-server0","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp_devops","Key":"Owner"}],"AmiLaunchIndex":0}],"ReservationId":"r-0b216daa4c700ec87","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.91","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-0396ed4e917b4738b","EnaSupport":false,"ImageId":"ami-c2243aa6","PrivateDnsName":"ip-10-227-101-91.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:f9:2b:bd:bb:d8","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"Primary network interface","NetworkInterfaceId":"eni-48e5441d","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-91.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.91"}],"PrivateDnsName":"ip-10-227-101-91.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-d553b3b6","AttachTime":"2017-12-06T10:30:29.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.91"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0d1224f361d627f61","AttachTime":"2017-12-06T10:30:30.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","AmiLaunchIndex":0},{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-02T09:35:12.000Z","PrivateIpAddress":"10.227.101.27","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-79ac3d10","StateTransitionReason":"","InstanceId":"i-0e6dd719167e20f1b","EnaSupport":false,"ImageId":"ami-c2243aa6","PrivateDnsName":"ip-10-227-101-27.eu-west-2.compute.internal",' +
                '"KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"ClientToken":"","SubnetId":"subnet-f3c94588","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:d4:c8:ad:ca:34","SourceDestCheck":true,"VpcId":"vpc-79ac3d10","Description":"Primary network interface","NetworkInterfaceId":"eni-49e5441c","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-27.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.27"}],"PrivateDnsName":"ip-10-227-101-27.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-d253b3b1","AttachTime":"2017-12-06T10:30:29.000Z"},"Groups":[{"GroupName":"dev-cdp-sg","GroupId":"sg-39a9f350"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-f3c94588","PrivateIpAddress":"10.227.101.27"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0ef61cbc74f95e5b6","AttachTime":"2017-12-06T10:30:30.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","AmiLaunchIndex":1}],"ReservationId":"r-0005267a43d9b4686","Groups":[],"OwnerId":"484676447925"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2018-01-04T14:07:30.000Z","PrivateIpAddress":"10.230.24.225","ProductCodes":[{"ProductCodeId":"aw0evgkw8e5c1q413zgy5pjce","ProductCodeType":"marketplace"}],"VpcId":"vpc-5f655936","StateTransitionReason":"","InstanceId":"i-04e739f3b5d05f8fb","EnaSupport":true,"ImageId":"ami-c3afb1a7","PrivateDnsName":"ip-10-230-24-225.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-10b03278"}],"ClientToken":"","SubnetId":"subnet-7eaa8705","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"06:5a:59:f9:68:22","SourceDestCheck":true,"VpcId":"vpc-5f655936","Description":"private ip address for abc alpha dev stack store primary-data","NetworkInterfaceId":"eni-3e019f6b","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-225.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.225"}],"PrivateDnsName":"ip-10-230-24-225.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-ad1f4d44","AttachTime":"2018-01-04T14:07:30.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-10b03278"}],"Ipv6Addresses":[],"OwnerId":"484676447925","SubnetId":"subnet-7eaa8705","PrivateIpAddress":"10.230.24.225"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0b6459b06f80d1b98","AttachTime":"2018-01-04T14:07:31.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-data-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-06b5622e4749646f1","Groups":[],"OwnerId":"484676447925"}],"SecurityGroups":[{"IpPermissionsEgress":[],"Description":"Managed by Terraform","Tags":[{"Value":"dev","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"dev-cdp-kafka-sg","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_cdp_devops","Key":"Owner"}],"IpPermissions":[],"GroupName":"dev-cdp-kafka-sg","VpcId":"vpc-79ac3d10","OwnerId":"484676447925","GroupId":"sg-0caef465"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-10901178"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-e7e9d68e","OwnerId":"484676447925","GroupId":"sg-10901178"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack store primary-data","Tags":[{"Value":"abc-alpha-store-primary-data","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-10b03278"},{"UserId":"484676447925","GroupId":"sg-58b63430"},{"UserId":"484676447925","GroupId":"sg-9db331f5"},{"VpcId":"vpc-a15a66c8","UserId":"484676447925",' +
                '"GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-e5b6348d"},{"UserId":"484676447925","GroupId":"sg-e628a68e"},{"UserId":"484676447925","GroupId":"sg-e7b6348f"},{"VpcId":"vpc-725b671b","UserId":"484676447925","GroupId":"sg-ed0e8e85","VpcPeeringConnectionId":"pcx-e0f35c89","PeeringStatus":"active"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-store-primary-data","VpcId":"vpc-5f655936","OwnerId":"484676447925","GroupId":"sg-10b03278"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack store primary_database","Tags":[{"Value":"abc-alpha-store-primary_database","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-11028879"},{"UserId":"484676447925","GroupId":"sg-210d8749"},{"UserId":"484676447925","GroupId":"sg-9db331f5"},{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-e628a68e"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-store-primary_database","VpcId":"vpc-5f655936","OwnerId":"484676447925","GroupId":"sg-11028879"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-11901179"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-e6e9d68f","OwnerId":"484676447925","GroupId":"sg-11901179"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-1b759673"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-44f6b02d","OwnerId":"484676447925","GroupId":"sg-1b759673"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack store secondary_database","Tags":[{"Value":"abc-alpha-store-secondary_database","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-11028879"},{"UserId":"484676447925","GroupId":"sg-210d8749"},{"UserId":"484676447925","GroupId":"sg-9db331f5"},{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-e628a68e"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-store-secondary_database","VpcId":"vpc-5f655936","OwnerId":"484676447925","GroupId":"sg-210d8749"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-2691104e"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-30ead559","OwnerId":"484676447925","GroupId":"sg-2691104e"},{"IpPermissionsEgress":[],"Description":"Security group for abc alpha stack usrdb ranger","Tags":[{"Value":"abc-alpha-usrdb-ranger","Key":"Name"}],"IpPermissions":[],"GroupName":"abc-alpha-usrdb-ranger","VpcId":"vpc-d15965b8","OwnerId":"484676447925","GroupId":"sg-270f8f4f"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Managed by Terraform","Tags":[{"Value":"cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"test","Key":"Environment"},{"Value":"cdp","Key":"Project"},{"Value":"test-cdp-sg","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-2906dd41"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":1194,"IpRanges":[{"Description":"VPN Leo Home","CidrIp":"92.3.210.135/32"}],"ToPort":1194,"IpProtocol":"udp","UserIdGroupPairs":[],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"213.251.23.185/32"},{"CidrIp":"213.251.23.186/32"},{"CidrIp":"213.251.23.187/32"},{"Description":"SSH HOME Jes","CidrIp":"176.26.96.61/32"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"test-cdp-sg","VpcId":"vpc-52fcdd3b","OwnerId":"484676447925","GroupId":"sg-2906dd41"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-340a8e5c"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-c35864aa","OwnerId":"484676447925","GroupId":"sg-340a8e5c"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-3600db5e"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-52fcdd3b","OwnerId":"484676447925","GroupId":"sg-3600db5e"},{"IpPermissionsEgress":[],"Description":"Security group for abc alpha stack usrdb keycloak","Tags":[{"Value":"abc-alpha-usrdb-keycloak","Key":"Name"}],"IpPermissions":[],"GroupName":"abc-alpha-usrdb-keycloak","VpcId":"vpc-d15965b8","OwnerId":"484676447925","GroupId":"sg-38109050"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Managed by Terraform","Tags":[{"Value":"cdp","Key":"Project"},{"Value":"dev","Key":"Environment"},{"Value":"dev-cdp-sg","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_cdp_devops","Key":"Owner"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"10.228.0.0/16"},{"CidrIp":"10.229.0.0/16"},{"Description":"ABC","CidrIp":"10.53.0.0/23"},{"CidrIp":"10.230.0.0/16"}],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-39a9f350"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":1194,"IpRanges":[{"Description":"VPN from HO 2","CidrIp":"213.251.23.186/32"},{"Description":"VPN from HO 1","CidrIp":"213.251.23.185/32"},{"Description":"VPN from HO 3","CidrIp":"213.251.23.187/32"},{"Description":"VPN Leo Home","CidrIp":"92.3.210.135/32"},{"Description":"VPN Nico Home","CidrIp":"86.129.192.119/32"},{"Description":"VPN from HO 4","CidrIp":"213.251.23.188/32"},{"Description":"VPN Jes Home","CidrIp":"176.26.194.123/32"},{"Description":"VPN Ben Home","CidrIp":"86.153.134.142/32"}],"ToPort":1194,"IpProtocol":"udp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"dev-cdp-sg","VpcId":"vpc-79ac3d10","OwnerId":"484676447925","GroupId":"sg-39a9f350"},' +
                '{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[{"CidrIpv6":"::/0"}]}],"Description":"launch-wizard-2 created 2017-11-30T14:41:04.752+00:00","Tags":[{"Value":"dev-ci-cdp-sg","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"10.230.0.0/16"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"launch-wizard-2","VpcId":"vpc-461e2b2f","OwnerId":"484676447925","GroupId":"sg-3a7ccb52"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Managed by Terraform","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"logging-cdp-sg","Key":"Name"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"logging","Key":"Environment"},{"Value":"cdp","Key":"Project"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"10.227.0.0/16"},{"CidrIp":"10.228.0.0/16"}],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-3b27cb53"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"213.251.23.164/32"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"logging-cdp-sg","VpcId":"vpc-4d5c1c24","OwnerId":"484676447925","GroupId":"sg-3b27cb53"},{"IpPermissionsEgress":[],"Description":"Managed by Terraform","Tags":[{"Value":"test-cdp-kafka-sg","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"test","Key":"Environment"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"cdp","Key":"Project"}],"IpPermissions":[],"GroupName":"test-cdp-kafka-sg","VpcId":"vpc-52fcdd3b","OwnerId":"484676447925","GroupId":"sg-3e05de56"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"launch-wizard-1 created 2017-08-09T11:03:13.618+01:00","IpPermissions":[{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"launch-wizard-1","VpcId":"vpc-908b64f9","OwnerId":"484676447925","GroupId":"sg-5420043d"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-547cf83c"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-5f655936","OwnerId":"484676447925","GroupId":"sg-547cf83c"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-55b05a3c"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"176.26.150.38/32"},{"Description":"Jes Home","CidrIp":"35.177.16.174/32"},{"Description":"Jenkins","CidrIp":"10.227.101.142/32"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-908b64f9","OwnerId":"484676447925","GroupId":"sg-55b05a3c"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack store primary-master","Tags":[{"Value":"abc-alpha-store-primary-master","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-10b03278"},{"UserId":"484676447925","GroupId":"sg-58b63430"},{"UserId":"484676447925","GroupId":"sg-9db331f5"},{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-e5b6348d"},{"UserId":"484676447925","GroupId":"sg-e628a68e"},{"UserId":"484676447925","GroupId":"sg-e7b6348f"},{"VpcId":"vpc-725b671b","UserId":"484676447925","GroupId":"sg-ed0e8e85","VpcPeeringConnectionId":"pcx-e0f35c89","PeeringStatus":"active"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-store-primary-master","VpcId":"vpc-5f655936","OwnerId":"484676447925","GroupId":"sg-58b63430"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group",' +
                '"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-5ca2ff35"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-79ac3d10","OwnerId":"484676447925","GroupId":"sg-5ca2ff35"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-6a901102"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-58e8d731","OwnerId":"484676447925","GroupId":"sg-6a901102"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-6c0a8e04"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-d15965b8","OwnerId":"484676447925","GroupId":"sg-6c0a8e04"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-730b8f1b"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-a15a66c8","OwnerId":"484676447925","GroupId":"sg-730b8f1b"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-7319c21b"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-0beacb62","OwnerId":"484676447925","GroupId":"sg-7319c21b"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-868e0fee"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-ceebd4a7","OwnerId":"484676447925","GroupId":"sg-868e0fee"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-8c1490e4"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-e75b678e","OwnerId":"484676447925","GroupId":"sg-8c1490e4"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack ext-logging syslog","Tags":[{"Value":"abc-alpha-ext-logging-syslog","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-f4f6599d","PeeringStatus":"active"},{"VpcId":"vpc-725b671b","UserId":"484676447925","GroupId":"sg-ed0e8e85","VpcPeeringConnectionId":"pcx-28f55a41","PeeringStatus":"active"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-ext-logging-syslog","VpcId":"vpc-6c5a6605","OwnerId":"484676447925","GroupId":"sg-8e33b0e6"},{"IpPermissionsEgress":[],"Description":"Managed by Terraform","Tags":[{"Value":"ingest-cdp-kafka-sg","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"ingest","Key":"Environment"},{"Value":"cdp","Key":"Project"}],"IpPermissions":[],"GroupName":"ingest-cdp-kafka-sg","VpcId":"vpc-44f6b02d","OwnerId":"484676447925","GroupId":"sg-947497fc"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Managed by Terraform","Tags":[{"Value":"cdp","Key":"Project"},{"Value":"ingest-cdp-sg","Key":"Name"},{"Value":"ingest","Key":"Environment"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"10.227.0.0/16"},{"Description":"ABC","CidrIp":"10.53.0.0/23"}],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-947b98fc"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"213.251.23.164/32"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"ingest-cdp-sg","VpcId":"vpc-44f6b02d","OwnerId":"484676447925","GroupId":"sg-947b98fc"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack store primary-security","Tags":[{"Value":"abc-alpha-store-primary-security","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-10b03278"},{"UserId":"484676447925","GroupId":"sg-11028879"},{"UserId":"484676447925","GroupId":"sg-210d8749"},{"UserId":"484676447925","GroupId":"sg-58b63430"},{"UserId":"484676447925","GroupId":"sg-9db331f5"},{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-e5b6348d"},{"UserId":"484676447925","GroupId":"sg-e628a68e"},{"UserId":"484676447925","GroupId":"sg-e7b6348f"},{"VpcId":"vpc-725b671b","UserId":"484676447925","GroupId":"sg-ed0e8e85","VpcPeeringConnectionId":"pcx-e0f35c89","PeeringStatus":"active"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],' +
                '"GroupName":"abc-alpha-store-primary-security","VpcId":"vpc-5f655936","OwnerId":"484676447925","GroupId":"sg-9db331f5"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"vpn sg","Tags":[{"Value":"dev-access-cdp-sg","Key":"Name"}],"IpPermissions":[{"PrefixListIds":[],"FromPort":1194,"IpRanges":[{"CidrIp":"213.251.23.186/32"},{"Description":"Jes Home VPN","CidrIp":"176.26.96.61/32"}],"ToPort":1194,"IpProtocol":"udp","UserIdGroupPairs":[],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"213.251.23.186/32"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"dev-access-cdp-sg","VpcId":"vpc-a51722cc","OwnerId":"484676447925","GroupId":"sg-a073c4c8"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-a09312c8"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-75e9d61c","OwnerId":"484676447925","GroupId":"sg-a09312c8"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-a19312c9"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-74e9d61d","OwnerId":"484676447925","GroupId":"sg-a19312c9"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-a29312ca"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-77e9d61e","OwnerId":"484676447925","GroupId":"sg-a29312ca"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Managed by Terraform","Tags":[{"Value":"cdp","Key":"Project"},{"Value":"test-logging-cdp-sg","Key":"Name"},{"Value":"cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"test-logging","Key":"Environment"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-b429f2dc"}],"Ipv6Ranges":[]}],"GroupName":"test-logging-cdp-sg","VpcId":"vpc-e3fbda8a","OwnerId":"484676447925","GroupId":"sg-b429f2dc"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"vpn sg","IpPermissions":[{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"213.251.23.186/32"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"dev-access-cdp-sg","VpcId":"vpc-908b64f9","OwnerId":"484676447925","GroupId":"sg-c277c0aa"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[{"CidrIpv6":"::/0"}]}],"Description":"launch-wizard-3 created 2017-12-04T14:59:38.335+00:00","IpPermissions":[{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[{"CidrIpv6":"::/0"}]}],"GroupName":"launch-wizard-3","VpcId":"vpc-461e2b2f","OwnerId":"484676447925","GroupId":"sg-c4f548ac"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-c603d8ae"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-e3fbda8a","OwnerId":"484676447925","GroupId":"sg-c603d8ae"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-c69f28ae"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-a51722cc","OwnerId":"484676447925","GroupId":"sg-c69f28ae"},{"IpPermissionsEgress":[],"Description":"Managed by Terraform","Tags":[{"Value":"logging-cdp-kafka-sg","Key":"Name"},{"Value":"dev_cdp_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp","Key":"Project"},{"Value":"logging","Key":"Environment"}],"IpPermissions":[],"GroupName":"logging-cdp-kafka-sg","VpcId":"vpc-4d5c1c24","OwnerId":"484676447925","GroupId":"sg-ce26caa6"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-ce29c5a6"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-4d5c1c24","OwnerId":"484676447925","GroupId":"sg-ce29c5a6"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[{"CidrIpv6":"::/0"}]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-cf8b3ca7"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.230.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-461e2b2f","OwnerId":"484676447925","GroupId":"sg-cf8b3ca7"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1",' +
                '"PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-d20a8eba"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-6d5a6604","OwnerId":"484676447925","GroupId":"sg-d20a8eba"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-d30a8ebb"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-6c5a6605","OwnerId":"484676447925","GroupId":"sg-d30a8ebb"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack ci jenkins","Tags":[{"Value":"abc-alpha-ci-jenkins","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-9db331f5","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-d429a7bc"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-ci-jenkins","VpcId":"vpc-a15a66c8","OwnerId":"484676447925","GroupId":"sg-d429a7bc"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-d60b8fbe"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-725b671b","OwnerId":"484676447925","GroupId":"sg-d60b8fbe"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack int-logging syslog","Tags":[{"Value":"abc-alpha-int-logging-syslog","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-10b03278","VpcPeeringConnectionId":"pcx-2bf55a42","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-11028879","VpcPeeringConnectionId":"pcx-2bf55a42","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-210d8749","VpcPeeringConnectionId":"pcx-2bf55a42","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-58b63430","VpcPeeringConnectionId":"pcx-2bf55a42","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-9db331f5","VpcPeeringConnectionId":"pcx-2bf55a42","PeeringStatus":"active"},{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc",' +
                '"VpcPeeringConnectionId":"pcx-eff35c86","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-dc1494b4"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-e5b6348d","VpcPeeringConnectionId":"pcx-2bf55a42","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-e628a68e","VpcPeeringConnectionId":"pcx-2bf55a42","PeeringStatus":"active"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-int-logging-syslog","VpcId":"vpc-6659650f","OwnerId":"484676447925","GroupId":"sg-dc1494b4"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-e18e0f89"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-d2ead5bb","OwnerId":"484676447925","GroupId":"sg-e18e0f89"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"default VPC security group","IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-e40b8f8c"}],"Ipv6Ranges":[]}],"GroupName":"default","VpcId":"vpc-6659650f","OwnerId":"484676447925","GroupId":"sg-e40b8f8c"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack ci artifactory","Tags":[{"Value":"abc-alpha-ci-artifactory","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-e528a68d"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":8081,"IpRanges":[],"ToPort":8082,"IpProtocol":"tcp","UserIdGroupPairs":[{"VpcId":"vpc-6c5a6605","UserId":"484676447925","GroupId":"sg-8e33b0e6","VpcPeeringConnectionId":"pcx-f4f6599d","PeeringStatus":"active"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":8081,"IpRanges":[],"ToPort":8081,"IpProtocol":"tcp","UserIdGroupPairs":[{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-10b03278","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-11028879","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-210d8749","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-58b63430","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-9db331f5","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-d429a7bc"},{"VpcId":"vpc-6659650f","UserId":"484676447925","GroupId":"sg-dc1494b4","VpcPeeringConnectionId":"pcx-eff35c86","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-e5b6348d","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-e628a68e","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"VpcId":"vpc-5f655936","UserId":"484676447925","GroupId":"sg-e7b6348f",' +
                '"VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"VpcId":"vpc-725b671b","UserId":"484676447925","GroupId":"sg-ed0e8e85","VpcPeeringConnectionId":"pcx-bef45bd7","PeeringStatus":"active"}],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-ci-artifactory","VpcId":"vpc-a15a66c8","OwnerId":"484676447925","GroupId":"sg-e528a68d"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack store primary-broker","Tags":[{"Value":"abc-alpha-store-primary-broker","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-10b03278"},{"UserId":"484676447925","GroupId":"sg-58b63430"},{"UserId":"484676447925","GroupId":"sg-9db331f5"},{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-e5b6348d"},{"UserId":"484676447925","GroupId":"sg-e628a68e"},{"UserId":"484676447925","GroupId":"sg-e7b6348f"},{"VpcId":"vpc-725b671b","UserId":"484676447925","GroupId":"sg-ed0e8e85","VpcPeeringConnectionId":"pcx-e0f35c89","PeeringStatus":"active"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-store-primary-broker","VpcId":"vpc-5f655936","OwnerId":"484676447925","GroupId":"sg-e5b6348d"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack store primary-ambari",' +
                '"Tags":[{"Value":"abc-alpha-store-primary-ambari","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-10b03278"},{"UserId":"484676447925","GroupId":"sg-58b63430"},{"UserId":"484676447925","GroupId":"sg-9db331f5"},{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-e5b6348d"},{"UserId":"484676447925","GroupId":"sg-e628a68e"},{"UserId":"484676447925","GroupId":"sg-e7b6348f"},{"VpcId":"vpc-725b671b","UserId":"484676447925","GroupId":"sg-ed0e8e85","VpcPeeringConnectionId":"pcx-e0f35c89","PeeringStatus":"active"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-store-primary-ambari","VpcId":"vpc-5f655936","OwnerId":"484676447925","GroupId":"sg-e628a68e"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack store secondary-security","Tags":[{"Value":"abc-alpha-store-secondary-security","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"UserId":"484676447925","GroupId":"sg-10b03278"},{"UserId":"484676447925","GroupId":"sg-11028879"},{"UserId":"484676447925","GroupId":"sg-210d8749"},{"UserId":"484676447925","GroupId":"sg-58b63430"},{"UserId":"484676447925","GroupId":"sg-9db331f5"},{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-8df659e4","PeeringStatus":"active"},{"UserId":"484676447925",' +
                '"GroupId":"sg-e5b6348d"},{"UserId":"484676447925","GroupId":"sg-e628a68e"},{"UserId":"484676447925","GroupId":"sg-e7b6348f"},{"VpcId":"vpc-725b671b","UserId":"484676447925","GroupId":"sg-ed0e8e85","VpcPeeringConnectionId":"pcx-e0f35c89","PeeringStatus":"active"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-store-secondary-security","VpcId":"vpc-5f655936","OwnerId":"484676447925","GroupId":"sg-e7b6348f"},{"IpPermissionsEgress":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[{"CidrIp":"0.0.0.0/0"}],"UserIdGroupPairs":[],"Ipv6Ranges":[]}],"Description":"Security group for abc alpha stack ingest primary-flow","Tags":[{"Value":"abc-alpha-ingest-primary-flow","Key":"Name"}],"IpPermissions":[{"IpProtocol":"-1","PrefixListIds":[],"IpRanges":[],"UserIdGroupPairs":[{"VpcId":"vpc-a15a66c8","UserId":"484676447925","GroupId":"sg-d429a7bc","VpcPeeringConnectionId":"pcx-bef45bd7","PeeringStatus":"active"},{"UserId":"484676447925","GroupId":"sg-ed0e8e85"}],"Ipv6Ranges":[]},{"PrefixListIds":[],"FromPort":22,"IpRanges":[{"CidrIp":"10.227.101.142/32"},{"CidrIp":"10.227.0.0/16"}],"ToPort":22,"IpProtocol":"tcp","UserIdGroupPairs":[],"Ipv6Ranges":[]}],"GroupName":"abc-alpha-ingest-primary-flow","VpcId":"vpc-725b671b","OwnerId":"484676447925","GroupId":"sg-ed0e8e85"},{"IpPermissionsEgress":[],"Description":"Managed by Terraform","Tags":[{"Value":"test-logging","Key":"Environment"},{"Value":"test-logging-cdp-kafka-sg","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"cdp","Key":"Project"},{"Value":"cdp_devops","Key":"Owner"}],"IpPermissions":[],"GroupName":"test-logging-cdp-kafka-sg","VpcId":"vpc-e3fbda8a","OwnerId":"484676447925","GroupId":"sg-fd28f395"}]}';


    }


    sb = new StringBuffer();


    def vpcIds = com.jayway.jsonpath.JsonPath.parse(aws_instances).read('$.Reservations.[*].Instances.[*].VpcId').toSet()

    graph.getOpenTransactions().each {
        sb.append(it.toString()).append('\n');
        // it.close()
    }

// graph.tx().rollback();


    def randVal = new Random()
    def randVal1 = randVal.nextInt(30)

    long oneWeekInMs = 3600000L * 24L * 7L
    long eighteenWeeks = oneWeekInMs * 18L



    vpcIds.each {

        def vpcId = null
        sb.append("VPC looking for  - ").append(it.toString()).append('\n');

        try {
            vpcId = g.V().has("Object.AWS_VPC.Id", eq(it.toString())).next().id();


        } catch (Throwable t) {
            sb.append("VPC error - ").append(t.toString()).append('\n');
        }
        if (vpcId == null) {
            long createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks) * 2;
            long updateMillis = createMillis + (long) (randVal.nextDouble() * eighteenWeeks);
            metadataCreateDate = new Date((long) createMillis)
            metadataUpdateDate = new Date((long) updateMillis)

            trans = graph.tx()

            try {
                if (!trans.isOpen())
                    trans.open();

                vpcId = g.addV("Object.AWS_VPC").
                        property("Metadata.Lineage", "aws ec2 describe-instance").
                        property("Metadata.Redaction", "/data/protection/officer").
                        property("Metadata.Version", 1).
                        property("Metadata.Create_Date", metadataCreateDate).
                        property("Metadata.Update_Date", metadataUpdateDate).
                        property("Metadata.Status", "new").
                        property("Metadata.Lineage_Location_Tag", "GB").
                        property("Metadata.Type", "Object.AWS_VPC").
                        property("Metadata.Type.Object.AWS_VPC", "Object.AWS_VPC").
                        property("Object.AWS_VPC.Id", it.toString()).next().
                        id();

                trans.commit();
                sb.append("VPC added - ").append(it.toString()).append('id = ').append(vpcId).append('\n');
            } catch (Throwable t) {
                trans.rollback();

                sb.append("VPC err - ").append(t.toString()).append('\n');
            } finally {
                trans.close()
            }

        }

        def instanceIds = com.jayway.jsonpath.JsonPath.parse(aws_instances)
                .read('$.Reservations.[*].Instances.[?(@.VpcId == "' + it.toString() + '")]').toSet()

        instanceIds.each { iid ->

            String iidStr = iid.InstanceId;

            sb.append("AWSI looking for  - ").append(iidStr).append('\n');

            def awsiId = null;
            try {
                awsiId = g.V().has("Object.AWS_Instance.Id", eq(iidStr)).next().id()
                sb.append("AWSI found id   - ").append(awsiId).append('\n');


            } catch (Throwable t) {
                sb.append("awsi - error ").append(t.toString());

            }

            if (awsiId == null) {
                long createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks);
                long updateMillis = createMillis + (long) (randVal.nextDouble() * eighteenWeeks) * 2;
                metadataCreateDate = new Date((long) createMillis)
                metadataUpdateDate = new Date((long) updateMillis)

                trans = graph.tx()

                try {

                    if (!trans.isOpen())
                        trans.open();

                    awsiId = g.addV("Object.AWS_Instance").
                            property("Metadata.Type", "Object.AWS_Instance").
                            property("Metadata.Type.Object.AWS_Instance", "Object.AWS_Instance").
                            property("Metadata.Lineage", "aws ec2 describe-instance").
                            property("Metadata.Redaction", "/data/protection/officer").
                            property("Metadata.Version", 1).
                            property("Metadata.Create_Date", metadataCreateDate).
                            property("Metadata.Update_Date", metadataUpdateDate).
                            property("Metadata.Status", "new").
                            property("Metadata.Lineage_Location_Tag", "GB").
                            property("Object.AWS_Instance.Id", iidStr).
                            property("Object.AWS_Instance.Public_Dns_Name", iid.PublicDnsName).
                            property("Object.AWS_Instance.EbsOptimized", iid.EbsOptimized).
                            property("Object.AWS_Instance.LaunchTime", iid.LaunchTime).
                            property("Object.AWS_Instance.PrivateIpAddress", iid.PrivateIpAddress).
                            property("Object.AWS_Instance.ProductCodeTypes", iid.ProductCodeTypes).
                            property("Object.AWS_Instance.ProductCodeIDs", iid.ProductCodeIDs).
                            property("Object.AWS_Instance.ImageId", iid.ImageId).
                            property("Object.AWS_Instance.PrivateDnsName", iid.PrivateDnsName).
                            property("Object.AWS_Instance.KeyName", iid.KeyName).
                            property("Object.AWS_Instance.InstanceType", iid.InstanceType).
                            property("Object.AWS_Instance.EnaSupport", iid.EnaSupport).
                            property("Object.AWS_Instance.LaunchTime", iid.LaunchTime).next().
                            id();
                    sb.append("AWSI added - ").append(iidStr).append('id = ').append(awsiId).append('\n');
                    trans.commit();

                } catch (Throwable t) {
                    sb.append("awsi - error ").append(t.toString());
                    trans.rollback()

                } finally {
                    trans.close()
                }

            }

            iid.SecurityGroups.each { sg ->
                String sgStr = sg.GroupId;

                /*
                    objectAWSProp00 = createProp(mgmt, "Object.AWS_Security_Group.GroupName", String.class, org.janusgraph.core.Cardinality.SINGLE)
              objectAWSProp01 = createProp(mgmt, "Object.AWS_Security_Group.GroupId", String.class, org.janusgraph.core.Cardinality.SINGLE)
              objectAWSProp02 = createProp(mgmt, "Object.AWS_Security_Group.Ip_Perms_Ingress_IpRanges", String.class, org.janusgraph.core.Cardinality.SET)
              objectAWSProp03 = createProp(mgmt, "Object.AWS_Security_Group.Ip_Perms_Egress_IpRanges", String.class, org.janusgraph.core.Cardinality.SET)

                */
                def sgvId = null;
                sb.append("secGroup - looking for ").append(sgStr.toString()).append('\n');

                try {
                    sgvId = g.V().has("Object.AWS_Security_Group.Id", eq(sgStr)).next().id();
                    sb.append("secGroup - found id ").append(sgvId).append('\n');


                } catch (Throwable t) {
                    sb.append("secGroup - error - ").append(t.toString()).append('\n'); ;
                }

                if (sgvId == null) {
                    trans = graph.tx()


                    try {

                        if (!trans.isOpen())
                            trans.open();


                        long createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks)
                        long updateMillis = createMillis + (long) (randVal.nextDouble() * eighteenWeeks) * 2
                        metadataCreateDate = new Date((long) createMillis)
                        metadataUpdateDate = new Date((long) updateMillis)

                        sgvId = g.addV("Object.AWS_Security_Group").
                                property("Metadata.Type", "Object.AWS_Security_Group").
                                property("Metadata.Type.Object.AWS_Security_Group", "Object.AWS_Security_Group").
                                property("Metadata.Lineage", "aws ec2 describe-instance").
                                property("Metadata.Redaction", "/data/protection/officer").
                                property("Metadata.Version", 1).
                                property("Metadata.Create_Date", metadataCreateDate).
                                property("Metadata.Update_Date", metadataUpdateDate).
                                property("Metadata.Status", "new").
                                property("Metadata.Lineage_Location_Tag", "GB").
                                property("Object.AWS_Security_Group.Id", sgStr).
                                property("Object.AWS_Security_Group.GroupName", sg.GroupName).
                                next().
                        // property("Object.AWS_Security_Group.Ip_Perms_Ingress_IpRanges", sg.).
                        // property("Object.AWS_Security_Group.Ip_Perms_Egress_IpRanges",   iid.LaunchTime).
                                id();

                        trans.commit();

                        sb.append("secGroup added - ").append(sgStr).append('id = ').append(sgvId).append('\n');
                    } catch (Throwable t) {
                        trans.rollback();

                        sb.append("secGroup - error ").append(t.toString());
                    } finally {
                        trans.close()
                    }
                }

                trans = graph.tx()

                try {

                    if (!trans.isOpen())
                        trans.open();
                    sb.append("retrieving ").append(sgvId).append('\n');
                    sb.append("retrieving ").append(sgvId.class).append('\n');

                    sgv = g.V(sgvId).next();
                    awsi = g.V(awsiId).next();
                    vpc = g.V(vpcId).next();

                    g.addE("Has_Server")
                            .from(sgv)
                            .to(awsi)
                            .next();

                    sgv = g.V(sgvId).next();

                    g.addE("Has_Security_Group")
                            .from(vpc)
                            .to(sgv).next();

                    trans.commit();

                } catch (Throwable t) {
                    sb.append("Edge - error ").append(t.toString()).append('\n');
                    trans.rollback();
                } finally {
                    trans.close()
                }
            }
        }

    }


    __addSecGroupEdges(graph, g, aws_sec_groups == null ? aws_instances : aws_sec_groups)


}

def addRandomDataInit(graph, g) {
    def listOfMaps = new LinkedList<HashMap<String, String>>()

    def map1 = [pg_metadataProcessor: '', pg_metadataLineage: '', pg_metadataRedaction: '', pg_metadataVersion: 1, pg_metadataStatus: '', pg_metadataGDPRStatus: '', pg_metadataLineageServerTag: '', pg_metadataLineageLocationTag: '', pg_metadataController: 'controller123', "pg_gender": "male", "pg_name_title": "mr", "pg_name_first": "quoc", "pg_name_last": "bastiaansen", "pg_location_street": "7247 lucasbolwerk", "pg_location_city": "epe", "pg_location_state": "flevoland", "pg_location_postcode": "92775", "pg_email": "quoc.bastiaansen@example.com", "pg_login_username": "heavybear983", "pg_login_password": "writer", "pg_login_salt": "avKgfb4e", "pg_login_md5": "d918e55da9d17937c718b9f7688821cb", "pg_login_sha1": "2cb2bba91deef25c64f15a6ac96d84c21c1189e3", "pg_login_sha256": "2945552ed13c3cf2a845d2f6af78ad7bbb161569af8e08dc3bfa36a3fa09b6df", "pg_dob": "1955-12-01 16:58:49", "pg_registered": "2012-07-15 09:50:59", "pg_phone": "(668)-056-6802", "pg_cell": "(859)-113-0976", "pg_id_name": "BSN", "pg_id_value": "94173707", "pg_picture_large": "https://randomuser.me/api/portraits/men/63.jpg", "pg_picture_medium": "https://randomuser.me/api/portraits/med/men/63.jpg", "pg_picture_thumbnail": "https://randomuser.me/api/portraits/thumb/men/63.jpg", "pg_nat": "NL"]
    def map2 = [pg_metadataProcessor: '', pg_metadataLineage: '', pg_metadataRedaction: '', pg_metadataVersion: 1, pg_metadataStatus: '', pg_metadataGDPRStatus: '', pg_metadataLineageServerTag: '', pg_metadataLineageLocationTag: '', pg_metadataController: 'controller123', "pg_gender": "male", "pg_name_title": "mr", "pg_name_first": "leo", "pg_name_last": "martins", "pg_location_street": "123 street name", "pg_location_city": "London", "pg_location_state": "London", "pg_location_postcode": "e145hq", "pg_email": "leo.martins@example.com", "pg_login_username": "lmartins123", "pg_login_password": "writer", "pg_login_salt": "avKgfb4e", "pg_login_md5": "d918e55da9d17937c718b9f7688821cb", "pg_login_sha1": "2cb2bba91deef25c64f15a6ac96d84c21c1189e3", "pg_login_sha256": "2945552ed13c3cf2a845d2f6af78ad7bbb161569af8e08dc3bfa36a3fa09b6df", "pg_dob": "1959-12-01 16:58:49", "pg_registered": "2016-07-15 09:50:59", "pg_phone": "(668)-056-6802", "pg_cell": "(859)-113-0976", "pg_id_name": "BSN", "pg_id_value": "94173707", "pg_picture_large": "https://randomuser.me/api/portraits/men/61.jpg", "pg_picture_medium": "https://randomuser.me/api/portraits/med/men/61.jpg", "pg_picture_thumbnail": "https://randomuser.me/api/portraits/thumb/men/61.jpg", "pg_nat": "NL"]
    def map3 = [pg_metadataProcessor: '', pg_metadataLineage: '', pg_metadataRedaction: '', pg_metadataVersion: 1, pg_metadataStatus: '', pg_metadataGDPRStatus: '', pg_metadataLineageServerTag: '', pg_metadataLineageLocationTag: '', pg_metadataController: 'controller123', "pg_gender": "male", "pg_name_title": "mr", "pg_name_first": "marcus", "pg_name_last": "martins", "pg_location_street": "123 street name", "pg_location_city": "London", "pg_location_state": "London", "pg_location_postcode": "e145hq", "pg_email": "marcus.martins@example.com", "pg_login_username": "mmartins123", "pg_login_password": "writer", "pg_login_salt": "avKgfb4e", "pg_login_md5": "d918e55da9d17937c718b9f7688821cb", "pg_login_sha1": "2cb2bba91deef25c64f15a6ac96d84c21c1189e3", "pg_login_sha256": "2945552ed13c3cf2a845d2f6af78ad7bbb161569af8e08dc3bfa36a3fa09b6df", "pg_dob": "1967-12-01 16:58:49", "pg_registered": "2016-07-19 09:50:59", "pg_phone": "(668)-056-3334", "pg_cell": "(859)-113-7776", "pg_id_name": "BS2", "pg_id_value": "94173999", "pg_picture_large": "https://randomuser.me/api/portraits/men/13.jpg", "pg_picture_medium": "https://randomuser.me/api/portraits/med/men/13.jpg", "pg_picture_thumbnail": "https://randomuser.me/api/portraits/thumb/men/13.jpg", "pg_nat": "BR"]
    def map4 = [pg_metadataProcessor: '', pg_metadataLineage: '', pg_metadataRedaction: '', pg_metadataVersion: 1, pg_metadataStatus: '', pg_metadataGDPRStatus: '', pg_metadataLineageServerTag: '', pg_metadataLineageLocationTag: '', pg_metadataController: 'controller123', "pg_gender": "female", "pg_name_title": "ms", "pg_name_first": "ada", "pg_name_last": "algebra", "pg_location_street": "123 street name", "pg_location_city": "London", "pg_location_state": "London", "pg_location_postcode": "e145hq", "pg_email": "marcus.martins@example.com", "pg_login_username": "aalgebra", "pg_login_password": "writer", "pg_login_salt": "avKgfb4e", "pg_login_md5": "d918e55da9d17937c718b9f7688821cb", "pg_login_sha1": "2cb2bba91deef25c64f15a6ac96d84c21c1189e3", "pg_login_sha256": "2945552ed13c3cf2a845d2f6af78ad7bbb161569af8e08dc3bfa36a3fa09b6df", "pg_dob": "1989-12-01 16:58:49", "pg_registered": "2016-07-19 09:50:59", "pg_phone": "(668)-056-3342", "pg_cell": "(859)-113-1122", "pg_id_name": "BS2", "pg_id_value": "94173900", "pg_picture_large": "https://randomuser.me/api/portraits/women/13.jpg", "pg_picture_medium": "https://randomuser.me/api/portraits/med/women/13.jpg", "pg_picture_thumbnail": "https://randomuser.me/api/portraits/thumb/women/13.jpg", "pg_nat": "BR"]

    listOfMaps.add(map1);
    listOfMaps.add(map2);
    listOfMaps.add(map3);
    listOfMaps.add(map4);

    StringBuffer sb = new StringBuffer();

    try {


        addCampaignAwarenessBulk(graph, g, listOfMaps)
        sb.append("\n called addCampaignAwarenessBulk(graph, g, listOfMaps)");

        addRandomSARs(graph, g);
        sb.append("\n called addRandomSARs(graph, g)");

        addLawfulBasisAndPrivacyNotices(graph, g);
        sb.append("\n called addLawfulBasisAndPrivacyNotices(graph, g)");

        addRandomDataProcedures(graph, g);
        sb.append("\n called addRandomDataProcedures(graph, g)");

        createDataProtectionAuthorities();
        sb.append("\n called createDataProtectionAuthorities()");


        addRandomAWSGraph(graph, g, null, null);
        sb.append("\n called addRandomAWSGraph(graph, g, null, null)");

        addRandomDataBreachEvents(graph, g)
        sb.append("\n called addRandomDataBreachEvents(graph, g)");

        createNotificationTemplates();
        sb.append("\n called createNotificationTemplates()");

//        createForms();


    } catch (e) {
        e.printStackTrace()
        sb.append('\nFailed to load schema!\n').append(e);

    }

    return sb.toString()


}


