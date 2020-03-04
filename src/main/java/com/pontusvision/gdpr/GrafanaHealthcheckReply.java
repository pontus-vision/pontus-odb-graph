package com.pontusvision.gdpr;

/*
{
  "annotation": {
    "name": "deploy",
    "datasource": "Simple JSON Datasource",
    "iconColor": "rgba(255, 96, 96, 1)",
    "enable": true,
    "query": "#deploy"
  }

}
 */
public class GrafanaHealthcheckReply
{


  String status;
  String title;
  String message;

  public GrafanaHealthcheckReply(String status, String title, String message)
  {
    this.status = status;
    this.title = title;
    this.message = message;
  }

  public GrafanaHealthcheckReply()
  {
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }
}
