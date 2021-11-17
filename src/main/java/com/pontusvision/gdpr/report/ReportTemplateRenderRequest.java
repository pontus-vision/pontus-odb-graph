package com.pontusvision.gdpr.report;

public class ReportTemplateRenderRequest {
  String refEntryId;
  String templateId;

  public String getRefEntryId() {
    return refEntryId;
  }

  public void setRefEntryId(String refEntryId) {
    this.refEntryId = refEntryId;
  }

  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }
}
