package com.pontusvision.ingestion;

public class IngestionCSVFileRequest extends IngestionJsonObjArrayRequest{
    public String csvBase64;

    public String getCsvBase64() {
        return csvBase64;
    }

    public void setCsvBase64(String csvBase64) {
        this.csvBase64 = csvBase64;
    }

}
