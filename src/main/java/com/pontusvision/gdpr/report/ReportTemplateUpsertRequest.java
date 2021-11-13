package com.pontusvision.gdpr.report;

public class ReportTemplateUpsertRequest {
  String reportTextBase64;
  String templatePOLEType;
  String templateName;

  public String getReportTextBase64() {
    return reportTextBase64;
  }

  public void setReportTextBase64(String reportTextBase64) {
    this.reportTextBase64 = reportTextBase64;
  }

  public String getTemplatePOLEType() {
    return templatePOLEType;
  }

  public void setTemplatePOLEType(String templatePOLEType) {
    this.templatePOLEType = templatePOLEType;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }
}
