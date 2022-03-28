package com.pontusvision.kpis;

public class K8sMetricsDescribedObject {
  public String kind = "Service";
  public String namespace = "default";
  public String name;
  public String apiVersion = "/"+K8sMetrics.version;

  public K8sMetricsDescribedObject(String name) {
    this.name = name;
  }
}
