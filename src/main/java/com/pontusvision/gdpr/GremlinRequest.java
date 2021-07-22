package com.pontusvision.gdpr;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class GremlinRequest implements Serializable {
  //    {
  //        searchStr: self.searchstr,
  //                from: from,
  //            to: to,
  //            sortBy: self.sortcol,
  //            sortDir: ((self.sortdir > 0) ? "+asc" : "+desc")
  //    }

  String gremlin;

  @Override
  public String toString() {
    return "GremlinRequest{" +
        "gremlin='" + gremlin + '\'' +
        ", bindingsInbound=" + bindingsInbound +
        ", requestId='" + requestId + '\'' +
        '}';
  }

  Bindings bindingsInbound;
  String requestId;

  public GremlinRequest() {
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
    return Objects.equals(bindingsInbound, that.bindingsInbound);
  }

  @Override
  public int hashCode() {
    int result = gremlin != null ? gremlin.hashCode() : 0;
    result = 31 * result + (bindingsInbound != null ? bindingsInbound.hashCode() : 0);
    return result;
  }

  public String getGremlin() {
    return gremlin;
  }

  public void setGremlin(String gremlin) {
    this.gremlin = gremlin;
  }

  @SuppressWarnings("unchecked")
  @JsonProperty("bindings")
  private void unpackBindings(Map<String, Object> bindings) {
    this.bindingsInbound = new SimpleBindings(bindings);

  }


  public Bindings getBindingsInbound() {
    return bindingsInbound;
  }

  public void setBindingsInbound(Bindings bindingsInbound) {
    this.bindingsInbound = bindingsInbound;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }
}
