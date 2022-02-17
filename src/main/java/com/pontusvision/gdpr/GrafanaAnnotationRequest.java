package com.pontusvision.gdpr;
import org.apache.commons.lang.StringEscapeUtils;

/*
{
  "range": {
    "from": "2016-04-15T13:44:39.070Z",
    "to": "2016-04-15T14:44:39.070Z"
  },
  "rangeRaw": {
    "from": "now-1h",
    "to": "now"
  },
  "annotation": {
    "name": "deploy",
    "datasource": "Simple JSON Datasource",
    "iconColor": "rgba(255, 96, 96, 1)",
    "enable": true,
    "query": "#deploy"
  }
}
 */

public class GrafanaAnnotationRequest
{
  private GrafanaRange      range;
  private GrafanaRange      rangeRaw;
  private GrafanaAnnotation annotation;

  public GrafanaRange getRange()
  {
    return range;
  }

  public void setRange(GrafanaRange range)
  {
    this.range = range;
  }

  public GrafanaRange getRangeRaw()
  {
    return rangeRaw;
  }

  public void setRangeRaw(GrafanaRange rangeRaw)
  {
    this.rangeRaw = rangeRaw;
  }

  public GrafanaAnnotation getAnnotation()
  {
    return annotation;
  }

  public void setAnnotation(GrafanaAnnotation annotation)
  {
    this.annotation = annotation;
  }

  public GrafanaAnnotationRequest(GrafanaRange range, GrafanaRange rangeRaw,
                                  GrafanaAnnotation annotation)
  {
    this.range = range;
    this.rangeRaw = rangeRaw;
    this.annotation = annotation;
  }

  public GrafanaAnnotationRequest()
  {

  }

  public String getSQLQuery ()
  {
    StringBuilder sb = new StringBuilder();

    if (this.annotation != null && this.annotation.query != null)
    {
      String table = this.annotation.query.toLowerCase();
      if (table.contains("ingest") )
      {
        sb.append("SELECT `Event_Group_Ingestion_Operation` + ' ' +`Event_Group_Ingestion_Type` as description, `Event_Group_Ingestion_Metadata_Start_Date`.asLong() as event_time FROM `Event_Group_Ingestion`")
          .append(" WHERE `Event_Group_Ingestion_Metadata_Start_Date` BETWEEN '")
          .append(this.range.from)
          .append("' AND '")
          .append(this.range.to)
          .append("' order by event_time");

      }
      else if (table.contains("consent"))
      {
        sb.append("SELECT 'Consent Event' + `Event_Consent_Status` as description,  `Event_Consent_Metadata_Update_Date`.asLong() as event_time  FROM `Event_Consent`")
          .append(" WHERE `Event_Consent_Metadata_Update_Date` BETWEEN '")
          .append(this.range.from)
          .append("' AND '")
          .append(this.range.to)
          .append("' order by event_time");

      }
      else if (table.contains("form"))
      {
        sb.append("SELECT 'Form Ingestion' + `Event_Form_Ingestion_Operation` + ' ' + `Event_Form_Ingestion_Type` as description , `Event_Form_Ingestion_Metadata_Create_Date`.asLong() as event_time  FROM `Event_Form_Ingestion`")
          .append(" WHERE `Event_Form_Ingestion_Metadata_Create_Date` BETWEEN '")
          .append(this.range.from)
          .append("' AND '")
          .append(this.range.to)
          .append("' order by event_time");
      }
      else if (table.contains("dsar")|| table.contains("reque"))
      {
        sb.append("SELECT  'Tipo: ' + `Event_Subject_Access_Request_Request_Type` + '; Estado: ' + `Event_Subject_Access_Request_Status` as description, `Event_Subject_Access_Request_Metadata_Update_Date`.asLong() as event_time  FROM `Event_Subject_Access_Request`")
          .append(" WHERE `Event_Subject_Access_Request_Metadata_Update_Date` BETWEEN '")
          .append(this.range.from)
          .append("' AND '")
          .append(this.range.to)
          .append("' order by event_time");
      }


    }

    return sb.toString();
  }

}
