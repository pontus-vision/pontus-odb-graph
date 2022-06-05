package com.pontusvision.gdpr.report;

public class ReportRenderRequest {
  String refEntryId;
  String reportTemplateBase64;

  public String getRefEntryId() {
    return refEntryId;
  }

  public void setRefEntryId(String refEntryId) {
    this.refEntryId = refEntryId;
  }

  public String getReportTemplateBase64() {
    return reportTemplateBase64;
  }

  public void setReportTemplateBase64(String reportTemplateBase64) {
    this.reportTemplateBase64 = reportTemplateBase64;
  }
}
