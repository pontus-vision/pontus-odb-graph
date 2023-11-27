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
  public String toString() {
    return "FormDataResponse{" +
      "dataType='" + dataType + '\'' +
      ", rid='" + rid + '\'' +
      ", components=" + Arrays.toString(components) +
      ", operation='" + operation + '\'' +
      '}';
  }

  @Override
  public FormDataResponse clone() {
    FormDataResponse ret = new FormDataResponse();
    if (this.components != null) {
      ret.setComponents(Arrays.copyOf(this.components, this.components.length));
      for (int i = 0, ilen = ret.components.length; i < ilen; i++) {
        if (ret.components[i] != null) {
          ret.components[i] = new PVFormData(ret.components[i]);
        }
      }
    }
    ret.setOperation(this.operation);
    ret.setRid(this.rid);
    ret.setDataType(this.dataType);

    return ret;
  }



}
