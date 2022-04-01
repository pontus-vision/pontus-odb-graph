package com.pontusvision.gdpr.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;


public class Vertex {
  private @Valid String label = null;
  private @Valid String name = "";
  private @Valid String condition = null;
  private @Valid List<VertexProps> props = null;
  private @Valid VertexProps createMany = null;
  private Class nativeType;


  private @Valid Double percentageThreshold = 0.00000000001;

  @JsonProperty("createMany")

  public VertexProps getCreateMany() {
    return createMany;
  }

  public void setCreateMany(VertexProps createMany) {
    this.createMany = createMany;
  }

  public Class getNativeType() {
    return nativeType;
  }

  public void setNativeType(Class nativeType) {
    this.nativeType = nativeType;
  }

  /**
   * POLE object name
   **/
  public Vertex label(String label) {
    this.label = label;
    return this;
  }


  //  @ApiModelProperty(example = "Person_Natural", required = true, value = "POLE object name")
  @JsonProperty("label")
  @NotNull

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Alias to reference this Vertex in the Edge Creation
   **/
  public Vertex name(String name) {
    this.name = name;
    return this;
  }

  @JsonProperty("percentageThreshold")

  public Double getPercentageThreshold() {
    return percentageThreshold;
  }

  public void setPercentageThreshold(Double percentageThreshold) {
    this.percentageThreshold = percentageThreshold;
  }


  //  @ApiModelProperty(example = "Titular", value = "Alias to reference this Vertex in the Edge Creation")
  @JsonProperty("name")

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * A groovy boolean condition to determine whether to add this vertex
   **/
  public Vertex condition(String condition) {
    this.condition = condition;
    return this;
  }


  //  @ApiModelProperty(example = "${val.equals('some value')}", value = "A groovy boolean condition to determine whether to add this vertex")
  @JsonProperty("condition")

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  /**
   *
   **/
  public Vertex props(List<VertexProps> props) {
    this.props = props;
    return this;
  }


  //  @ApiModelProperty(required = true, value = "")
  @JsonProperty("props")
  @NotNull

  public List<VertexProps> getProps() {
    return props;
  }

  public void setProps(List<VertexProps> props) {
    this.props = props;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vertex vertex = (Vertex) o;
    return Objects.equals(label, vertex.label) &&
        Objects.equals(name, vertex.name) &&
        Objects.equals(condition, vertex.condition) &&
        Objects.equals(props, vertex.props);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, name, condition, props);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Vertex {\n");

    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    condition: ").append(toIndentedString(condition)).append("\n");
    sb.append("    props: ").append(toIndentedString(props)).append("\n");
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
