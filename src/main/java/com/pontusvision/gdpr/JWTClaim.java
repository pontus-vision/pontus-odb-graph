package com.pontusvision.gdpr;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.google.gson.Gson;
import java.util.List;
import org.apache.hadoop.util.Time;

public class JWTClaim
{


  static Gson gson = new Gson();
  protected String bizctx = null;
  protected String sub = null;
  protected String iss = null;
  protected long startTimeMs = 0L;
  protected long exp = 0L;
  protected List<String> groups = null;

  public static JWTClaim fromJson(String json) {
    return (JWTClaim)gson.fromJson(json, JWTClaim.class);
  }

  public JWTClaim() {
  }

  public boolean isValid() {
    if (this.startTimeMs == 0L && this.exp == 0L) {
      return true;
    } else {
      long currTime = Time.monotonicNow();
      return this.startTimeMs <= currTime && currTime <= this.exp;
    }
  }

  public String getSub() {
    return this.sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public String getIss() {
    return this.iss;
  }

  public void setIss(String iss) {
    this.iss = iss;
  }

  public List<String> getGroups() {
    return this.groups;
  }

  public void setGroups(List<String> groups) {
    this.groups = groups;
  }

  public String getBizctx() {
    return this.bizctx;
  }

  public void setBizctx(String bizctx) {
    this.bizctx = bizctx;
  }

  public long getStartTimeMs() {
    return this.startTimeMs;
  }

  public void setStartTimeMs(long startTimeMs) {
    this.startTimeMs = startTimeMs;
  }

  public long getExp() {
    return this.exp;
  }

  public void setExp(long exp) {
    this.exp = exp;
  }

  public long heapSize() {
    return this.bizctx == null ? 0L : (long)this.bizctx.length();
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o != null && this.getClass() == o.getClass()) {
      JWTClaim jwtClaim1 = (JWTClaim)o;
      if (this.startTimeMs != jwtClaim1.startTimeMs) {
        return false;
      } else if (this.exp != jwtClaim1.exp) {
        return false;
      } else {
        return this.bizctx != null ? this.bizctx.equals(jwtClaim1.bizctx) : jwtClaim1.bizctx == null;
      }
    } else {
      return false;
    }
  }

  public int hashCode() {
    int result = this.bizctx != null ? this.bizctx.hashCode() : 0;
    result = 31 * result + this.sub != null ? this.sub.hashCode() : 0;
    result = 31 * result + this.iss != null ? this.iss.hashCode() : 0;
    result = 31 * result + (int)(this.startTimeMs ^ this.startTimeMs >>> 32);
    result = 31 * result + (int)(this.exp ^ this.exp >>> 32);
    return result;
  }

  public String toString() {
    return gson.toJson(this, this.getClass());
  }
}

