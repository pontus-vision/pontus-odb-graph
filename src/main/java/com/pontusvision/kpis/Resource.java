package com.pontusvision.kpis;

import com.pontusvision.graphutils.gdpr;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("home")
public class Resource {


  public Resource() {

  }


  @GET
  @Path("kpi/calculatePOLECounts")
  @Produces(MediaType.TEXT_PLAIN)
  public String calculatePOLECounts() {
    return gdpr.calculatePOLECounts().toString();
  }
  @GET
  @Path("kpi/getMd2Stats")
  @Produces(MediaType.TEXT_PLAIN)
  public String getMd2Stats() {
    return gdpr.getMd2Stats().toString();
  }

  @GET
  @Path("kpi/getScoresJson")
  @Produces(MediaType.TEXT_PLAIN)
  public String getScoresJson() {
    return gdpr.getScoresJson().toString();
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
