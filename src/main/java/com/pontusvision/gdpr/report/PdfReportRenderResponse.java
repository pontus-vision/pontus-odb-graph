package com.pontusvision.gdpr.report;

import com.pontusvision.gdpr.BaseReply;

public class PdfReportRenderResponse extends ReportRenderResponse {

  public PdfReportRenderResponse() {
    this(Status.OK);
  }

  public PdfReportRenderResponse(Status status) {
    super(status);
  }
  public PdfReportRenderResponse(Status status, String errorMsg) {
    super(status,errorMsg);
    this.setErrorStr(errorMsg);
  }

}
