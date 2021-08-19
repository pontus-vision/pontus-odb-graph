package com.pontusvision.ingestion;

public class IngestionCSVFileRequest {
    public String csvBase64;
//    public String jsonPath;
    public String ruleName;

    public String getCsvBase64() {
        return csvBase64;
    }

    public void setCsvBase64(String csvBase64) {
        this.csvBase64 = csvBase64;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
