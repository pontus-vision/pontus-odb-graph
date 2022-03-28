package com.pontusvision.kpis;

import com.google.gson.Gson;
import com.pontusvision.graphutils.gdpr;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Path("/apis/custom.metrics.k8s.io")
public class K8sMetrics {

  public K8sMetrics() {

  }


  public static final String version = "v1"; // gdpr.getEnv("PV_K8S_METRICS_VER","v1beta1");

  public static final Gson gson = new Gson();
  @GET
  @Path(version)
  @Produces(MediaType.APPLICATION_JSON)
  public String getVersion() {
    return "{ \"status\": \"healthy\" }";
  }

  @GET
  @Path(version + "/namespaces/default/services/pv-extract/getScoresJson")
  @Produces(MediaType.APPLICATION_JSON)
  public String getScoresJson() {
    K8sMetricsItem metricsItem = new K8sMetricsItem("pv-extract");
    metricsItem.metricName = "getScoresJson";
    metricsItem.timestamp = new Date();
    metricsItem.value = 100.5;

    K8sMetricsResults res = new K8sMetricsResults(metricsItem);


    return gson.toJson(res);
  }

  @GET
  @Path("kpi/getDSARStatsPerOrganisation")
  @Produces(MediaType.TEXT_PLAIN)
  public String getDSARStatsPerOrganisation() {
    return gdpr.getDSARStatsPerOrganisation().toString();
  }

  @GET
  @Path("kpi/getNaturalPersonPerDataProcedures")
  @Produces(MediaType.TEXT_PLAIN)
  public String getNaturalPersonPerDataProcedures() {
    return gdpr.getNaturalPersonPerDataProcedures().toString();
  }

  @GET
  @Path("kpi/getDataProceduresPerDataSource")
  @Produces(MediaType.TEXT_PLAIN)
  public String getDataProceduresPerDataSource() {
    return gdpr.getDataProceduresPerDataSource().toString();
  }

  @GET
  @Path("kpi/getConsentPerNaturalPersonType")
  @Produces(MediaType.TEXT_PLAIN)
  public String getConsentPerNaturalPersonType() {
    return gdpr.getConsentPerNaturalPersonType().toString();
  }

  @GET
  @Path("kpi/getNumNaturalPersonPerOrganisation")
  @Produces(MediaType.TEXT_PLAIN)
  public String getNumNaturalPersonPerOrganisation() {
    return gdpr.getNumNaturalPersonPerOrganisation().toString();
  }

  @GET
  @Path("kpi/getNumSensitiveDataPerDataSource")
  @Produces(MediaType.TEXT_PLAIN)
  public String getNumSensitiveDataPerDataSource() {
    return gdpr.getNumSensitiveDataPerDataSource().toString();
  }

  @GET
  @Path("kpi/getNumNaturalPersonPerDataSource")
  @Produces(MediaType.TEXT_PLAIN)
  public String getNumNaturalPersonPerDataSource() {
    return gdpr.getNumNaturalPersonPerDataSource().toString();
  }

  @GET
  @Path("kpi/getNumEventsPerDataSource")
  @Produces(MediaType.TEXT_PLAIN)
  public String getNumEventsPerDataSource() {
    return gdpr.getNumEventsPerDataSource().toString();
  }

}