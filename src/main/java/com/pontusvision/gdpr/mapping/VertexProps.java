package com.pontusvision.gdpr.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;


public class VertexProps {
  private @Valid String name = null;
  private @Valid String val = null;
  private @Valid String predicate = PredicateEnum.EQ.toString();
  private @Valid Boolean mandatoryInSearch = false;
  private @Valid Boolean excludeFromSubsequenceSearch = false;
  private @Valid Boolean excludeFromSearch = false;
  private @Valid Boolean excludeFromUpdate = false;
  private @Valid TypeEnum type = TypeEnum.JAVA_LANG_STRING;
  private @Valid String postProcessor = "";
  private @Valid String postProcessorVar = "it";
  private @Valid Double matchWeight = null;
  private @Valid String splitChar = null;


  @JsonProperty("excludeFromUpdate")
  public Boolean getExcludeFromUpdate() {
    return excludeFromUpdate;
  }

  public void setExcludeFromUpdate(Boolean excludeFromUpdate) {
    this.excludeFromUpdate = excludeFromUpdate;
  }

  @JsonProperty("matchWeight")
  public Double getMatchWeight() {
    return matchWeight;
  }

  public void setMatchWeight(Double matchWeight) {
    this.matchWeight = matchWeight;
  }


  @JsonProperty("splitChar")
  public String getSplitChar() {
    return splitChar;
  }

  public void setSplitChar(String splitChar) {
    this.splitChar = splitChar;
  }

  /**
   *
   **/
  public VertexProps name(String name) {
    this.name = name;
    return this;
  }

  //  @ApiModelProperty(example = "property name", required = true, value = "")
  @JsonProperty("name")
  @NotNull

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   *
   **/
  public VertexProps val(String val) {
    this.val = val;
    return this;
  }

  //  @ApiModelProperty(example = "property value", value = "")
  @JsonProperty("val")

  public String getVal() {
    return val;
  }

  public void setVal(String val) {
    this.val = val;
  }

  /**
   * the operation that needs to be executed to compare the value with existing records for upsert operations (e.g. eq, neq, gt, lt, gte, lte, textContains, textContainsPrefix)
   **/
  public VertexProps predicate(PredicateEnum predicate) {
    this.predicate = predicate.toString();
    return this;
  }

  public VertexProps predicate(String predicate) throws Exception {
    if (predicate.startsWith("idx")|| PredicateEnum.fromValue(predicate)!= null){
      this.predicate = predicate;
    }
    else {
      throw new Exception("Invalid Predicate ${predicate}");

    }

    return this;
  }

  //  @ApiModelProperty(example = "eq", value = "the operation that needs to be executed to compare the value with existing records for upsert operations (e.g. eq, neq, gt, lt, gte, lte, textContains, textContainsPrefix)")
  @JsonProperty("predicate")

  public String getPredicate() {
    return predicate;
  }

  public void setPredicate(PredicateEnum predicate) {
    this.predicate = predicate.value();
  }
  public void setPredicate(String predicate) throws Exception {
    this.predicate( predicate);
  }


  /**
   *
   **/
  public VertexProps mandatoryInSearch(Boolean mandatoryInSearch) {
    this.mandatoryInSearch = mandatoryInSearch;
    return this;
  }

  //  @ApiModelProperty(example = "true", value = "")
  @JsonProperty("mandatoryInSearch")

  public Boolean isMandatoryInSearch() {
    return mandatoryInSearch;
  }

  public void setMandatoryInSearch(Boolean mandatoryInSearch) {
    this.mandatoryInSearch = mandatoryInSearch;
  }

  /**
   *
   **/
  public VertexProps excludeFromSubsequenceSearch(Boolean excludeFromSubsequenceSearch) {
    this.excludeFromSubsequenceSearch = excludeFromSubsequenceSearch;
    return this;
  }

  //  @ApiModelProperty(example = "true", value = "")
  @JsonProperty("excludeFromSubsequenceSearch")

  public Boolean isExcludeFromSubsequenceSearch() {
    return excludeFromSubsequenceSearch;
  }

  public void setExcludeFromSubsequenceSearch(Boolean excludeFromSubsequenceSearch) {
    this.excludeFromSubsequenceSearch = excludeFromSubsequenceSearch;
  }

  /**
   *
   **/
  public VertexProps excludeFromSearch(Boolean excludeFromSearch) {
    this.excludeFromSearch = excludeFromSearch;
    return this;
  }

  //  @ApiModelProperty(example = "true", value = "")
  @JsonProperty("excludeFromSearch")

  public Boolean isExcludeFromSearch() {
    return excludeFromSearch;
  }

  public void setExcludeFromSearch(Boolean excludeFromSearch) {
    this.excludeFromSearch = excludeFromSearch;
  }

  /**
   *
   **/
  public VertexProps type(TypeEnum type) {
    this.type = type;
    return this;
  }

  //  @ApiModelProperty(value = "")
  @JsonProperty("type")

  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  /**
   * If the type is array, the postProcessor splits it into the individual elements, and enables cleaning up of each array element
   **/
  public VertexProps postProcessor(String postProcessor) {
    this.postProcessor = postProcessor;
    return this;
  }

  //  @ApiModelProperty(example = "${it?.toUpperCase()?.trim()}", value = "If the type is array, the postProcessor splits it into the individual elements, and enables cleaning up of each array element")
  @JsonProperty("postProcessor")

  public String getPostProcessor() {
    return postProcessor;
  }

  public void setPostProcessor(String postProcessor) {
    this.postProcessor = postProcessor;
  }

  /**
   * If the type is array, the postProcessor splits it into the individual elements, and enables cleaning up of each array element; this enables users to set the value of the iterated elements (e.g. \&quot;it2\&quot; will be the value used when setting up the postProcessor logic above).
   **/
  public VertexProps postProcessorVar(String postProcessorVar) {
    this.postProcessorVar = postProcessorVar;
    return this;
  }

  //  @ApiModelProperty(example = "it2", value = "If the type is array, the postProcessor splits it into the individual elements, and enables cleaning up of each array element; this enables users to set the value of the iterated elements (e.g. \"it2\" will be the value used when setting up the postProcessor logic above).")
  @JsonProperty("postProcessorVar")

  public String getPostProcessorVar() {
    return postProcessorVar;
  }

  public void setPostProcessorVar(String postProcessorVar) {
    this.postProcessorVar = postProcessorVar;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VertexProps vertexPropsInner = (VertexProps) o;
    return Objects.equals(name, vertexPropsInner.name) &&
        Objects.equals(val, vertexPropsInner.val) &&
        Objects.equals(predicate, vertexPropsInner.predicate) &&
        Objects.equals(mandatoryInSearch, vertexPropsInner.mandatoryInSearch) &&
        Objects.equals(excludeFromSubsequenceSearch, vertexPropsInner.excludeFromSubsequenceSearch) &&
        Objects.equals(excludeFromSearch, vertexPropsInner.excludeFromSearch) &&
        Objects.equals(type, vertexPropsInner.type) &&
        Objects.equals(postProcessor, vertexPropsInner.postProcessor) &&
        Objects.equals(postProcessorVar, vertexPropsInner.postProcessorVar);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, val, predicate, mandatoryInSearch, excludeFromSubsequenceSearch, excludeFromSearch, type, postProcessor, postProcessorVar);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VertexPropsInner {\n");

    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    val: ").append(toIndentedString(val)).append("\n");
    sb.append("    predicate: ").append(toIndentedString(predicate)).append("\n");
    sb.append("    mandatoryInSearch: ").append(toIndentedString(mandatoryInSearch)).append("\n");
    sb.append("    excludeFromSubsequenceSearch: ").append(toIndentedString(excludeFromSubsequenceSearch)).append("\n");
    sb.append("    excludeFromSearch: ").append(toIndentedString(excludeFromSearch)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    postProcessor: ").append(toIndentedString(postProcessor)).append("\n");
    sb.append("    postProcessorVar: ").append(toIndentedString(postProcessorVar)).append("\n");
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

  public enum PredicateEnum {

    EQ("eq"), NEQ("neq"), GT("gt"), LT("lt"), GTE("gte"), LTE("lte"), TEXTCONTAINS("textContains"),
    TEXTCONTAINSPREFIX("textContainsPrefix");


    private final String value;

    PredicateEnum(String v) {
      value = v;
    }

    @JsonCreator
    public static PredicateEnum fromValue(String v) {
      for (PredicateEnum b : PredicateEnum.values()) {
        if (String.valueOf(b.value).equals(v)) {
          return b;
        }
      }
      return null;
    }

    public String value() {
      return value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }
  }

  public enum TypeEnum {

    JAVA_UTIL_DATE("java.util.Date"), _LJAVA_LANG_STRING_("[Ljava.lang.String;"), JAVA_LANG_STRING("java.lang.String");


    private final String value;

    TypeEnum(String v) {
      value = v;
    }

    @JsonCreator
    public static TypeEnum fromValue(String v) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(v)) {
          return b;
        }
      }
      return null;
    }

    public String value() {
      return value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }
  }
}
