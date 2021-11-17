package com.pontusvision.gdpr.report;

import com.pontusvision.gdpr.BaseReply;

public class ReportTemplateUpsertResponse extends BaseReply {
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
    this(Status.OK);
    this.templatePOLEType = templateType;
    this.templateName = templateName;
    this.templateId = templateId;
  }

  public ReportTemplateUpsertResponse() {
    this(Status.OK);
  }

  public ReportTemplateUpsertResponse(Status status) {
    super(status);
  }
  public ReportTemplateUpsertResponse(Status status, String errMsg) {
    super(status);
    this.setErrorStr(errMsg);
  }
}
