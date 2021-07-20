package com.pontusvision.gdpr.mapping;

public class MappingReq {
  String name;
  UpdateReq updateRequest;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UpdateReq getUpdateRequest() {
    return updateRequest;
  }

  public void setUpdateRequest(UpdateReq updateRequest) {
    this.updateRequest = updateRequest;
  }
}
