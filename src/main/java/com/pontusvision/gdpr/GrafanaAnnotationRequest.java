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
        sb.append("SELECT `Event.Group_Ingestion.Operation` + ' ' +`Event.Group_Ingestion.Type` as description, `Event.Group_Ingestion.Metadata_Start_Date`.asLong() as event_time FROM `Event.Group_Ingestion`")
          .append(" WHERE `Event.Group_Ingestion.Metadata_Start_Date` BETWEEN '")
          .append(this.range.from)
          .append("' AND '")
          .append(this.range.to)
          .append("' order by event_time");

      }
      else if (table.contains("consent"))
      {
        sb.append("SELECT 'Consent Event' + `Event.Consent.Status` as description,  `Event.Consent.Metadata.Update_Date`.asLong() as event_time  FROM `Event.Consent`")
          .append(" WHERE `Event.Consent.Metadata.Update_Date` BETWEEN '")
          .append(this.range.from)
          .append("' AND '")
          .append(this.range.to)
          .append("' order by event_time");

      }
      else if (table.contains("form"))
      {
        sb.append("SELECT 'Form Ingestion' + `Event.Form_Ingestion.Operation` + ' ' + `Event.Form_Ingestion.Type` as description , `Event.Form_Ingestion.Metadata_Create_Date`.asLong() as event_time  FROM `Event.Form_Ingestion`")
          .append(" WHERE `Event.Form_Ingestion.Metadata_Create_Date` BETWEEN '")
          .append(this.range.from)
          .append("' AND '")
          .append(this.range.to)
          .append("' order by event_time");
      }
      else if (table.contains("dsar")|| table.contains("reque"))
      {
        sb.append("SELECT  'Tipo: ' + `Event.Subject_Access_Request.Request_Type` + '; Estado: ' + `Event.Subject_Access_Request.Status` as description, `Event.Subject_Access_Request.Metadata.Update_Date`.asLong() as event_time  FROM `Event.Subject_Access_Request`")
          .append(" WHERE `Event.Subject_Access_Request.Metadata.Update_Date` BETWEEN '")
          .append(this.range.from)
          .append("' AND '")
          .append(this.range.to)
          .append("' order by event_time");
      }


    }

    return sb.toString();
  }

}
