package com.pontusvision.gdpr.report;

public class PdfReportRenderRequest extends ReportRenderResponse {

  public PdfReportRenderRequest() {
    this(Status.OK);
  }

  public PdfReportRenderRequest(Status status) {
    super(status);
  }
  public PdfReportRenderRequest(Status status, String errorMsg) {
    super(status,errorMsg);
    this.setErrorStr(errorMsg);
  }

  public String toJson(){
    StringBuilder sb = new StringBuilder("{\"refEntryId\": \"");
    sb.append(refEntryId).append("\",\"base64Report\": \"").append(base64Report).append("\"}");
    return sb.toString();
  }

}
