package com.pontusvision.gdpr;

import java.io.Serializable;

public class Md2Request implements  Serializable{
  public static class Settings implements  Serializable{
    Long start;
    Long limit;

    public Long getStart() {
      return start;
    }

    public void setStart(Long start) {
      this.start = start;
    }

    public Long getLimit() {
      return limit;
    }

    public void setLimit(Long limit) {
      this.limit = limit;
    }

  }
  public static class Query implements Serializable{
    String name;
    String docCpf;
    String email;
    Long reqId = 0L;

    public Long getReqId() {
      return reqId;
    }

    public void setReqId(Long reqId) {
      this.reqId = reqId;
    }


    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDocCpf() {
      return docCpf;
    }

    public void setDocCpf(String docCpf) {
      this.docCpf = docCpf;
    }
    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }


  }
  Settings settings = new Settings();
  Query query = new Query();

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public Query getQuery() {
    return query;
  }

  public void setQuery(Query query) {
    this.query = query;
  }
}
