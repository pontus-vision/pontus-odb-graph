package com.pontusvision.gdpr.form;

import java.util.Arrays;

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

  @Override
  public FormDataResponse clone() {
    FormDataResponse ret = new FormDataResponse();
    ret.setComponents(Arrays.copyOf(this.components,this.components.length));
    for (int i = 0, ilen = ret.components.length; i < ilen; i++){
      ret.components[i] = new PVFormData(ret.components[i]);
    }
    ret.setOperation(this.operation);
    ret.setRid(this.rid);
    ret.setDataType(this.dataType);

    return ret;
  }



}
