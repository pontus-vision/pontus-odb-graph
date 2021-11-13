package com.pontusvision.gdpr.report;

public class ReportTemplateUpsertResponse {
  String templatePOLEType;
  String templateName;
  String templateId;

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

  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public ReportTemplateUpsertResponse(String templateType, String templateName, String templateId) {
    this.templatePOLEType = templateType;
    this.templateName = templateName;
    this.templateId = templateId;
  }
}
