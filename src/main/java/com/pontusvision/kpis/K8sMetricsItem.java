package com.pontusvision.kpis;

import java.util.Date;

public class K8sMetricsItem {
  public K8sMetricsDescribedObject describedObject;
  public String metricName;
  public Date timestamp;
  public double value;

  public K8sMetricsItem(String serviceName) {
    this.describedObject = new K8sMetricsDescribedObject(serviceName);

  }
}
