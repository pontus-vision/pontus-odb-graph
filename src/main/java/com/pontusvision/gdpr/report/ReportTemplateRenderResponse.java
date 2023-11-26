package com.pontusvision.gdpr.report;

import com.pontusvision.gdpr.BaseReply;

public class ReportTemplateRenderResponse extends BaseReply {
  String base64Report;
  String refEntryId;
  String templateId;

  public ReportTemplateRenderResponse() {
    this(Status.OK);
  }

  public ReportTemplateRenderResponse(Status status) {
    super(status);
  }

  public ReportTemplateRenderResponse(Status status, String errorMsg) {
    super(status);
    this.setErrorStr(errorMsg);
  }

  @Override
  public String toString() {
    return "ReportTemplateRenderResponse{" +
      "base64Report='" + base64Report + '\'' +
      ", refEntryId='" + refEntryId + '\'' +
      ", templateId='" + templateId + '\'' +
      '}';
  }

  public String getBase64Report() {
    return base64Report;
  }

  public void setBase64Report(String base64Report) {
    this.base64Report = base64Report;
  }

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
