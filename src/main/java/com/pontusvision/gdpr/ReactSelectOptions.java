package com.pontusvision.gdpr;

public class ReactSelectOptions {
    String label;
    String value;

    // must have this default constructor to get this class serialized as a reply!!!
    public ReactSelectOptions() {
    }

    public ReactSelectOptions(String label, String value) {
        this.label = label.replaceAll("_", " ");
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
