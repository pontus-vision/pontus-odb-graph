package com.pontusvision.gdpr;

/*
{
  "range": {
    "from": "2016-04-15T13:44:39.070Z",
    "to": "2016-04-15T14:44:39.070Z"
  },
}
 */
public class GrafanaRange
{
  public String getFrom()
  {
    return from;
  }

  public void setFrom(String from)
  {
    this.from = from;
  }

  public String getTo()
  {
    return to;
  }

  public void setTo(String to)
  {
    this.to = to;
  }

  String from;
  String to;

  public GrafanaRange()
  {

  }

  public GrafanaRange(String from, String to)
  {
    this.from = from;
    this.to = to;
  }

}
