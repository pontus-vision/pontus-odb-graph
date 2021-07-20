package com.pontusvision.gdpr.mapping;

public class Rules {

  UpdateReq updatereq;
  Double percentageThreshold;
  Integer maxHitsPerType;

  public Double getPercentageThreshold() {
    return percentageThreshold;
  }

  public void setPercentageThreshold(Double percentageThreshold) {
    this.percentageThreshold = percentageThreshold;
  }

  public Integer getMaxHitsPerType() {
    return maxHitsPerType;
  }

  public void setMaxHitsPerType(Integer maxHitsPerType) {
    this.maxHitsPerType = maxHitsPerType;
  }

  public UpdateReq getUpdatereq() {
    return updatereq;
  }

  public void setUpdatereq(UpdateReq updatereq) {
    this.updatereq = updatereq;
  }

}
