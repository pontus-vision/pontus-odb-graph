package com.pontusvision.gdpr;

/*
{
    "target":"upper_75",
    "datapoints":[
      [622, 1450754160000],
      [365, 1450754220000]
    ]
 */

public class GrafanaQueryResponse
{
  private String target;
  private long   dataPoints[][];

  public GrafanaQueryResponse(String target, long[][] dataPoints)
  {
    this.target = target;
    this.dataPoints = dataPoints;
  }

  public GrafanaQueryResponse()
  {
  }

  public String getTarget()
  {
    return target;
  }

  public void setTarget(String target)
  {
    this.target = target;
  }

  public long[][] getDataPoints()
  {
    return dataPoints;
  }

  public void setDataPoints(long[][] dataPoints)
  {
    this.dataPoints = dataPoints;
  }
}
