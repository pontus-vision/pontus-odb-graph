package com.pontusvision.gdpr;

/*

  {
    annotation: annotation, // The original annotation sent from Grafana.
    time: time, // Time since UNIX Epoch in milliseconds. (required)
    title: title, // The title for the annotation tooltip. (required)
    tags: tags, // Tags for the annotation. (optional)
    text: text // Text for the annotation. (optional)
  }


 {
        "id": 1124,
        "alertId": 0,
        "dashboardId": 468,
        "panelId": 2,
        "userId": 1,
        "userName": "",
        "newState": "",
        "prevState": "",
        "time": 1507266395000,
        "timeEnd": 1507266395000,
        "text": "test",
        "metric": "",
        "type": "event",
        "tags": [
            "tag1",
            "tag2"
        ],
        "data": {}
    },
    {
        "id": 1123,
        "alertId": 0,
        "dashboardId": 468,
        "panelId": 2,
        "userId": 1,
        "userName": "",
        "newState": "",
        "prevState": "",
        "time": 1507265111000,
        "text": "test",
        "metric": "",
        "type": "event",
        "tags": [
            "tag1",
            "tag2"
        ],
        "data": {}
    }

 */
public class GrafanaAnnotationReply
{

  private GrafanaAnnotation annotation;
  private long              time;
  private String            title;
  private String[]          tags;
  private String            text;

  public GrafanaAnnotation getAnnotation()
  {
    return annotation;
  }

  public void setAnnotation(GrafanaAnnotation annotation)
  {
    this.annotation = annotation;
  }

  public long getTime()
  {
    return time;
  }

  public void setTime(long time)
  {
    this.time = time;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String[] getTags()
  {
    return tags;
  }

  public void setTags(String[] tags)
  {
    this.tags = tags;
  }

  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  public GrafanaAnnotationReply()
  {

  }

  public GrafanaAnnotationReply(GrafanaAnnotation annotation, long time, String title, String[] tags,
                                String text)
  {
    this.annotation = annotation;
    this.time = time;
    this.title = title;
    this.tags = tags;
    this.text = text;
  }
}
