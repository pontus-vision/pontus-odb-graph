package com.pontusvision.gdpr.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;


public class UpdateReq {
  private @Valid List<Vertex> vertices = null;
  private @Valid List<Edge> edges = null;

  private Double percentageThreshold = null;

  /**
   *
   **/
  public UpdateReq vertices(List<Vertex> vertices) {
    this.vertices = vertices;
    return this;
  }


  @JsonProperty("vertices")

  public List<Vertex> getVertices() {
    return vertices;
  }

  public void setVertices(List<Vertex> vertices) {
    this.vertices = vertices;
  }

  /**
   *
   **/
  public UpdateReq edges(List<Edge> edges) {
    this.edges = edges;
    return this;
  }


  //  @ApiModelProperty(value = "")
  @JsonProperty("edges")

  public List<Edge> getEdges() {
    return edges;
  }

  public void setEdges(List<Edge> edges) {
    this.edges = edges;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateReq updateReqUpdatereq = (UpdateReq) o;
    return Objects.equals(vertices, updateReqUpdatereq.vertices) &&
        Objects.equals(edges, updateReqUpdatereq.edges);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vertices, edges);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReqUpdatereq {\n");

    sb.append("    vertices: ").append(toIndentedString(vertices)).append("\n");
    sb.append("    edges: ").append(toIndentedString(edges)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
