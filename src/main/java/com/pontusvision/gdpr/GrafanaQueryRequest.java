package com.pontusvision.gdpr;

/*

{
  "range": { "from": "2015-12-22T03:06:13.851Z", "to": "2015-12-22T06:48:24.137Z" },
  "interval": "5s",
  "targets": [
    { "refId": "B", "target": "upper_75" },
    { "refId": "A", "target": "upper_90" }
  ],
  "format": "json",
  "maxDataPoints": 2495 // decided by the panel
}


 */
class GrafanaTarget{
  private String refId;
  private String target;

  public String getRefId()
  {
    return refId;
  }

  public void setRefId(String refId)
  {
    this.refId = refId;
  }

  public String getTarget()
  {
    return target;
  }

  public void setTarget(String target)
  {
    this.target = target;
  }

  public GrafanaTarget()
  {
  }

  public GrafanaTarget(String refId, String target)
  {
    this.refId = refId;
    this.target = target;
  }
}

public class GrafanaQueryRequest
{
  private GrafanaRange range;
  private String interval;
  private String format;
  private GrafanaTarget[] targets;
  private int maxDataPoints;

  public GrafanaRange getRange()
  {
    return range;
  }

  public void setRange(GrafanaRange range)
  {
    this.range = range;
  }

  public String getInterval()
  {
    return interval;
  }

  public void setInterval(String interval)
  {
    this.interval = interval;
  }

  public String getFormat()
  {
    return format;
  }

  public void setFormat(String format)
  {
    this.format = format;
  }

  public GrafanaTarget[] getTargets()
  {
    return targets;
  }

  public void setTargets(GrafanaTarget[] targets)
  {
    this.targets = targets;
  }

  public int getMaxDataPoints()
  {
    return maxDataPoints;
  }

  public void setMaxDataPoints(int maxDataPoints)
  {
    this.maxDataPoints = maxDataPoints;
  }

  public GrafanaQueryRequest()
  {
  }

  public GrafanaQueryRequest(GrafanaRange range, String interval, String format,
                             GrafanaTarget[] targets, int maxDataPoints)
  {
    this.range = range;
    this.interval = interval;
    this.format = format;
    this.targets = targets;
    this.maxDataPoints = maxDataPoints;
  }



}
