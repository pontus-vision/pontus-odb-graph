package com.pontusvision.gdpr.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;


public class Edge {
  private @Valid String label = null;
  private @Valid String fromVertexName = null;
  private @Valid String fromVertexLabel = null;
  private @Valid String toVertexName = null;
  private @Valid String toVertexLabel = null;

  /**
   * The name of the relationship between POLE vertices
   **/
  public Edge label(String label) {
    this.label = label;
    return this;
  }


  //  @ApiModelProperty(example = "lives", required = true, value = "The name of the relationship between POLE vertices")
  @JsonProperty("label")
  @NotNull

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * POLE object alias
   **/
  public Edge fromVertexName(String fromVertexName) {
    this.fromVertexName = fromVertexName;
    return this;
  }


  //  @ApiModelProperty(example = "Titular", value = "POLE object alias")
  @JsonProperty("fromVertexName")

  public String getFromVertexName() {
    return fromVertexName;
  }

  public void setFromVertexName(String fromVertexName) {
    this.fromVertexName = fromVertexName;
  }

  /**
   * POLE object name
   **/
  public Edge fromVertexLabel(String fromVertexLabel) {
    this.fromVertexLabel = fromVertexLabel;
    return this;
  }


  //  @ApiModelProperty(example = "Person.Natural", value = "POLE object name")
  @JsonProperty("fromVertexLabel")

  public String getFromVertexLabel() {
    return fromVertexLabel;
  }

  public void setFromVertexLabel(String fromVertexLabel) {
    this.fromVertexLabel = fromVertexLabel;
  }

  /**
   * POLE object alias
   **/
  public Edge toVertexName(String toVertexName) {
    this.toVertexName = toVertexName;
    return this;
  }


  //  @ApiModelProperty(example = "Home", value = "POLE object alias")
  @JsonProperty("toVertexName")

  public String getToVertexName() {
    return toVertexName;
  }

  public void setToVertexName(String toVertexName) {
    this.toVertexName = toVertexName;
  }

  /**
   * POLE object name
   **/
  public Edge toVertexLabel(String toVertexLabel) {
    this.toVertexLabel = toVertexLabel;
    return this;
  }


  //  @ApiModelProperty(example = "Location.Address", value = "POLE object name")
  @JsonProperty("toVertexLabel")

  public String getToVertexLabel() {
    return toVertexLabel;
  }

  public void setToVertexLabel(String toVertexLabel) {
    this.toVertexLabel = toVertexLabel;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Edge edge = (Edge) o;
    return Objects.equals(label, edge.label) &&
        Objects.equals(fromVertexName, edge.fromVertexName) &&
        Objects.equals(fromVertexLabel, edge.fromVertexLabel) &&
        Objects.equals(toVertexName, edge.toVertexName) &&
        Objects.equals(toVertexLabel, edge.toVertexLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, fromVertexName, fromVertexLabel, toVertexName, toVertexLabel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Edge {\n");

    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    fromVertexName: ").append(toIndentedString(fromVertexName)).append("\n");
    sb.append("    fromVertexLabel: ").append(toIndentedString(fromVertexLabel)).append("\n");
    sb.append("    toVertexName: ").append(toIndentedString(toVertexName)).append("\n");
    sb.append("    toVertexLabel: ").append(toIndentedString(toVertexLabel)).append("\n");
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
