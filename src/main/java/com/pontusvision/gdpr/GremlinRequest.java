package com.pontusvision.gdpr;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Objects;


public class GremlinRequest {


  String gremlin;

  @JsonProperty("bindings")
  Map<String,Object> bindings;

  String requestId;

  public GremlinRequest() {
  }

  @Override
  public String toString() {
    return "GremlinRequest{" +
        "gremlin='" + gremlin + '\'' +
        ", bindings=" + bindings +
        ", requestId='" + requestId + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof GremlinRequest))
      return false;

    GremlinRequest that = (GremlinRequest) o;

    if (!Objects.equals(gremlin, that.gremlin))
      return false;
    return Objects.equals(bindings, that.bindings);
  }

  @Override
  public int hashCode() {
    int result = gremlin != null ? gremlin.hashCode() : 0;
    result = 31 * result + (bindings != null ? bindings.hashCode() : 0);
    return result;
  }

  public String getGremlin() {
    return gremlin;
  }

  public void setGremlin(String gremlin) {
    this.gremlin = gremlin;
  }


  public Map<String, Object> getBindings() {
    return bindings;
  }

  public void setBindings(Map<String, Object> bindings) {
    this.bindings = bindings;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }
}
