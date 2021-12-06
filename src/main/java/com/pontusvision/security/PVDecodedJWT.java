package com.pontusvision.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.ws.rs.ext.Provider;
import java.util.Base64;

@RequestScoped
@Provider
public class PVDecodedJWT {

  static Gson gson = new Gson();
  PVJWTPayload payload;
  DecodedJWT jwt;

  public DecodedJWT getJwt() {
    return jwt;
  }

  public void setJwt(DecodedJWT jwt) {
    this.jwt = jwt;
    byte[] bytes = Base64.getDecoder().decode(jwt.getPayload());
    this.payload = gson.fromJson(new String(bytes), PVJWTPayload.class);
  }

  public PVJWTPayload getPayload() {
    return payload;
  }

  public boolean checkStr(String str) {
    if (this.payload == null) {
      return false;
    }

    String bizctx = this.payload.bizctx;

    if (bizctx == null) {
      return false;
    }
    return bizctx.toLowerCase().contains(str);

  }

  public boolean isDPO() {
    return checkStr("/pontus/data_protection_officer");
  }

  public boolean isAdmin() {
    return checkStr("/pontus/admin");
  }

  public boolean isReader() {
    return checkStr("/pontus/reader");
  }

  public class PVJWTPayload {
    public int exp;
    public int iat;
    public int auth_time;
    public String jti;
    public String iss;
    public String aud;
    public String sub;
    public String typ;
    public String azp;
    public String nonce;
    public String session_state;
    public String at_hash;
    public String acr;
    public boolean email_verified;
    public String name;
    public String preferred_username;
    public String given_name;
    public String family_name;
    public String email;
    public String bizctx;
  }
}
