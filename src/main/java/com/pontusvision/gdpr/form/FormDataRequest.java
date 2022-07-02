package com.pontusvision.gdpr.form;


import com.pontusvision.gdpr.BaseReply;

public class FormDataRequest extends BaseReply {
  public String dataType;
  String rid;
  PVFormData[] components;
  String operation;


  public FormDataRequest() {
    this(Status.OK);
  }

  public FormDataRequest(Status status) {
    super(status);
  }
  public FormDataRequest(Status status, String errorMsg) {
    super(status);
    this.setErrorStr(errorMsg);
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getRid() {
    return rid;
  }

  public void setRid(String rid) {
    this.rid = rid;
  }

  public PVFormData[] getComponents() {
    return components;
  }

  public void setComponents(PVFormData[] components) {
    this.components = components;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }
}
