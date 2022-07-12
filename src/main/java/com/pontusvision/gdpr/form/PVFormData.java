package com.pontusvision.gdpr.form;

import java.util.Arrays;

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

  public PVFormData (){

  }
  public PVFormData(PVFormData other) {
    this.type = other.type;
    this.subtype = other.subtype;
    this.labels = other.labels;
    this.className = other.className;
    this.name = other.name;
    this.access = other.access;
    this.requireValidOption = other.requireValidOption;
    this.inline = other.inline;
    this.multiple = other.multiple;
    if (other.values != null) {
      this.values = Arrays.copyOf(other.values, other.values.length);
    }
    this.other = other.other;
    this.description = other.description;
    this.placeholder = other.placeholder;
    this.value = other.value;
    this.maxlength = other.maxlength;
    this.rows = other.rows;
    this.role = other.role;
    this.toggle = other.toggle;
    this.required = other.required;
    this.min = other.min;
    this.max = other.max;
    this.step = other.step;
    if (other.userData != null) {
      this.userData = Arrays.copyOf(other.userData, other.userData.length);
    }
  }

  public String getType() {
    return type;
  }

  public PVFormData setType(String types) {
    this.type = type;
    return this;
  }

  public String getSubtype() {
    return subtype;
  }

  public PVFormData setSubtype(String subtype) {
    this.subtype = subtype;
    return this;
  }

  public String getLabels() {
    return labels;
  }

  public PVFormData setLabels(String labels) {
    this.labels = labels;
    return this;
  }

  public String getClassName() {
    return className;
  }

  public PVFormData setClassName(String className) {
    this.className = className;
    return this;
  }

  public String getName() {
    if (name != null && name.startsWith("#") || name.startsWith("@")) {
      return name.substring(1);
    }
    return name;
  }

  public PVFormData setName(String name) {
    this.name = name;
    return this;
  }

  public Boolean getAccess() {
    return access;
  }

  public PVFormData setAccess(Boolean access) {
    this.access = access;
    return this;
  }

  public Boolean getRequireValidOption() {
    return requireValidOption;
  }

  public PVFormData setRequireValidOption(Boolean requireValidOption) {
    this.requireValidOption = requireValidOption;
    return this;
  }

  public Boolean getInline() {
    return inline;
  }

  public PVFormData setInline(Boolean inline) {
    this.inline = inline;
    return this;
  }

  public Boolean getMultiple() {
    return multiple;
  }

  public PVFormData setMultiple(Boolean multiple) {
    this.multiple = multiple;
    return this;
  }

  public PVFormDataValues[] getValues() {
    return values;
  }

  public PVFormData setValues(PVFormDataValues[] values) {
    this.values = values;
    return this;
  }

  public Boolean getOther() {
    return other;
  }

  public PVFormData setOther(Boolean other) {
    this.other = other;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public PVFormData setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public PVFormData setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
    return this;
  }

  public String getValue() {
    return value;
  }

  public PVFormData setValue(String value) {
    this.value = value;
    return this;
  }

  public Long getMaxlength() {
    return maxlength;
  }

  public PVFormData setMaxlength(Long maxlength) {
    this.maxlength = maxlength;
    return this;
  }

  public Long getRows() {
    return rows;
  }

  public PVFormData setRows(Long rows) {
    this.rows = rows;
    return this;
  }

  public String getRole() {
    return role;
  }

  public PVFormData setRole(String role) {
    this.role = role;
    return this;
  }

  public Boolean getToggle() {
    return toggle;
  }

  public PVFormData setToggle(Boolean toggle) {
    this.toggle = toggle;
    return this;
  }

  public Boolean getRequired() {
    return required;
  }

  public PVFormData setRequired(Boolean required) {
    this.required = required;
    return this;
  }

  public Long getMin() {
    return min;
  }

  public PVFormData setMin(Long min) {
    this.min = min;
    return this;
  }

  public Long getMax() {
    return max;
  }

  public PVFormData setMax(Long max) {
    this.max = max;
    return this;
  }

  public Long getStep() {
    return step;
  }

  public PVFormData setStep(Long step) {
    this.step = step;
    return this;
  }

  public String[] getUserData() {
    return userData;
  }

  public PVFormData setUserData(String[] userData) {
    this.userData = userData;
    return this;
  }
}
