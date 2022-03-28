package com.pontusvision.kpis;

import java.util.ArrayList;

public class K8sMetricsResults {
  public String kind = "MetricValueList";
  public String apiVersion = "custom.metrics.k8s.io/"+K8sMetrics.version;
  public K8sMetricsMetadata k8sMetricsMetadata;
  public ArrayList<K8sMetricsItem> items;


  public K8sMetricsResults(K8sMetricsItem metrics) {

    k8sMetricsMetadata = new K8sMetricsMetadata();
    items = new ArrayList<>();
    items.add(metrics);

  }

//  {
//    kind: "MetricValueList",
//        apiVersion: "custom.metrics.k8s.io/v1beta1",
//      metadata: {
//    selfLink: "/apis/custom.metrics.k8s.io/v1beta1/"
//  },
//    items: [{
//    describedObject: {
//      kind: "Service",
//          namespace: "<NAMESPACE>",
//          name: "<SERVICE>",
//          apiVersion: "/v1beta1"
//    },
//    metricName: "<METRIC>",
//        timestamp: new Date(),
//        value: <METRIC_VALUE>
//  }]
//  }


}