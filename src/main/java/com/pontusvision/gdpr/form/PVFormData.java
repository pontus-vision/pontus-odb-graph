package com.pontusvision.gdpr.form;

public class PVFormData {
  String type;
  String subtype;
  String labels;
  String className;
  String name;
  Boolean access;
  //  String default;
  Boolean requireValidOption;
  Boolean inline;
  Boolean multiple;
  PVFormDataValues[] values;
  Boolean other;
  String description;
  String placeholder;
  String value;
  Long maxlength;
  Long rows;
  String role;
  Boolean toggle;
  Boolean required;
  Long min;
  Long max;
  Long step;
  String[] userData;

  public String getType() {
    return type;
  }

  public void setType(String types) {
    this.type = type;
  }

  public String getSubtype() {
    return subtype;
  }

  public void setSubtype(String subtype) {
    this.subtype = subtype;
  }

  public String getLabels() {
    return labels;
  }

  public void setLabels(String labels) {
    this.labels = labels;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getName() {
    if (name.startsWith("#")|| name.startsWith("@")){
      return name.substring(1);
    }
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getAccess() {
    return access;
  }

  public void setAccess(Boolean access) {
    this.access = access;
  }

  public Boolean getRequireValidOption() {
    return requireValidOption;
  }

  public void setRequireValidOption(Boolean requireValidOption) {
    this.requireValidOption = requireValidOption;
  }

  public Boolean getInline() {
    return inline;
  }

  public void setInline(Boolean inline) {
    this.inline = inline;
  }

  public Boolean getMultiple() {
    return multiple;
  }

  public void setMultiple(Boolean multiple) {
    this.multiple = multiple;
  }

  public PVFormDataValues[] getValues() {
    return values;
  }

  public void setValues(PVFormDataValues[] values) {
    this.values = values;
  }

  public Boolean getOther() {
    return other;
  }

  public void setOther(Boolean other) {
    this.other = other;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Long getMaxlength() {
    return maxlength;
  }

  public void setMaxlength(Long maxlength) {
    this.maxlength = maxlength;
  }

  public Long getRows() {
    return rows;
  }

  public void setRows(Long rows) {
    this.rows = rows;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Boolean getToggle() {
    return toggle;
  }

  public void setToggle(Boolean toggle) {
    this.toggle = toggle;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public Long getMin() {
    return min;
  }

  public void setMin(Long min) {
    this.min = min;
  }

  public Long getMax() {
    return max;
  }

  public void setMax(Long max) {
    this.max = max;
  }

  public Long getStep() {
    return step;
  }

  public void setStep(Long step) {
    this.step = step;
  }

  public String[] getUserData() {
    return userData;
  }

  public void setUserData(String[] userData) {
    this.userData = userData;
  }
}
