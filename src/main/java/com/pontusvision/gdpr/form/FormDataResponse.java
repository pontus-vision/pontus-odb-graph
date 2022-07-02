package com.pontusvision.gdpr.form;

import com.pontusvision.gdpr.BaseReply;

public class FormDataResponse extends FormDataRequest {

  public FormDataResponse() {
    this(Status.OK);
  }

  public FormDataResponse(Status status) {
    super(status);
  }
  public FormDataResponse(Status status, String errorMsg) {
    super(status, errorMsg);
  }
  public FormDataResponse(FormDataRequest req) {
    super(Status.OK);
    super.setComponents(req.components);
    super.setOperation(req.operation);
    super.setRid(req.rid);
    super.setDataType(req.dataType);
  }




}
