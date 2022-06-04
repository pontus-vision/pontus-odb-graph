package com.pontusvision.gdpr.report;

import com.pontusvision.gdpr.BaseReply;

public class ReportRenderResponse extends BaseReply {
  String base64Report;
  String refEntryId;

  public ReportRenderResponse() {
    this(Status.OK);
  }

  public ReportRenderResponse(Status status) {
    super(status);
  }
  public ReportRenderResponse(Status status, String errorMsg) {
    super(status);
    this.setErrorStr(errorMsg);
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

}
