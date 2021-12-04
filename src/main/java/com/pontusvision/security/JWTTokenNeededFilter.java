package com.pontusvision.security;

//import io.jsonwebtoken.Jwts;
//import org.agoncal.sample.jaxrs.jwt.util.KeyGenerator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@Provider
@JWTTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class JWTTokenNeededFilter implements ContainerRequestFilter {

  public static class KeycloakRealmInfo {
    //    {
//      "realm":"pontus",
//      "public_key":"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgnk17ayp/s79DDTrKVPLJTrgJd1fa6ov3JmAHMNHIWPFUku86T3mXZVPhBTB5WJF8MtAjV59+dCftPZmn9D052w+EyxE7gRHhSONrpd+NwmlAUO5MnYlQY7vKlg4EwSCFAKeQN1THE4TWZWpGYSm+TkEfy+iARLNxtnzg9hFba3D0I7wbBk16lqDtxgwpNFccNmhzfY6d99siZ1qx9Q42SmgWb7PNsmY64OLV9VKggDdukX2TviuvbJ4E8SYXN5tzkjNvVJCK2oIe/pS9zKhHGYENXP0LitizjogKSAnTzQpADNUiZzV531SyTENYTKKd0VGYUR+RJmQQJyu21Mw3wIDAQAB",
//      "token-service":"http://localhost:18443/auth/realms/pontus/protocol/openid-connect",
//      "account-service":"http://localhost:18443/auth/realms/pontus/account",
//      "tokens-not-before":1519324283
//    }
    String realm;
    @SerializedName("public_key")
    String publicKey;
    @SerializedName("token-service")
    String tokenService;
    @SerializedName("account-service")
    String accountService;
    @SerializedName("tokens-not-before")
    Long tokensNotBefore;

  }


  // ======================================
  // =          Injection Points          =
  // ======================================
  public static boolean jwtAuthEnabled = "true".equalsIgnoreCase(System.getenv("PV_USE_JWT_AUTH"));
  public static String keycloakBaseURLEnv =  System.getenv("PV_KEYCLOAK_BASE_URL") ;
  public static String keycloakPublicKeyRealmURL =  keycloakBaseURLEnv == null ?
      "http://pontus-comply-keycloak:8080/auth/realms/pontus":
      keycloakBaseURLEnv + "/auth/realms/pontus";

  public static String keycloakPublicKey = System.getenv("PV_KEYCLOAK_PUB_KEY");
  public static JWTVerifier verifier;
  static KeycloakRealmInfo keycloakRealmInfo;

  static {
    if (jwtAuthEnabled){
      HttpClient client = HttpClients.createSystem();
      HttpGet request = new HttpGet(URI.create(keycloakPublicKeyRealmURL));
      request.setHeader("Accept", MediaType.APPLICATION_JSON);

      try {
        if (keycloakPublicKey == null) {
          HttpResponse resp = client.execute(request);

          String output = IOUtils.toString(resp.getEntity().getContent());
          Gson gson = new Gson();
          keycloakRealmInfo = gson.fromJson(output, KeycloakRealmInfo.class);
          keycloakPublicKey = keycloakRealmInfo.publicKey;
        }
        byte[] bytes = Base64.getDecoder().decode(keycloakPublicKey);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bytes);
        PublicKey pk = factory.generatePublic(encodedKeySpec);
        Algorithm algorithm = Algorithm.RSA256(
            (RSAPublicKey) pk,
            null);
        verifier = JWT.require(algorithm).build();
//        DecodedJWT jwt = verifier.verify(token);


      } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
        e.printStackTrace();
        System.exit (-1);
      }


    }
  }
//  @Inject
  private Logger logger;

//  private ;

//  @Inject
//  private KeyGenerator keyGenerator;

  // ======================================
  // =          Business methods          =
  // ======================================

  public static PVDecodedJWT createDummyDecodedJWT (){
    PVDecodedJWT ret = new PVDecodedJWT();
    ret.setJwt(new DecodedJWT() {
      @Override
      public String getToken() {
        return "";
      }

      @Override
      public String getHeader() {
        return "";
      }

      @Override
      public String getPayload() {
        return "{\n" +
            "  \"bizctx\": \"/pontus/data_protection_officer,/pontus/admin\"\n" +
            "}";
      }

      @Override
      public String getSignature() {
        return null;
      }

      @Override
      public String getAlgorithm() {
        return null;
      }

      @Override
      public String getType() {
        return null;
      }

      @Override
      public String getContentType() {
        return null;
      }

      @Override
      public String getKeyId() {
        return null;
      }

      @Override
      public Claim getHeaderClaim(String s) {
        return null;
      }

      @Override
      public String getIssuer() {
        return null;
      }

      @Override
      public String getSubject() {
        return null;
      }

      @Override
      public List<String> getAudience() {
        return null;
      }

      @Override
      public Date getExpiresAt() {
        return null;
      }

      @Override
      public Date getNotBefore() {
        return null;
      }

      @Override
      public Date getIssuedAt() {
        return null;
      }

      @Override
      public String getId() {
        return null;
      }

      @Override
      public Claim getClaim(String s) {
        return null;
      }

      @Override
      public Map<String, Claim> getClaims() {
        return null;
      }
    });
    return ret;
  }
  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    if (!jwtAuthEnabled){
      requestContext.setProperty("pvDecodedJWT", createDummyDecodedJWT());
      return;
    }
    // Get the HTTP Authorization header from the request
//    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
//    logger.info("#### authorizationHeader : " + authorizationHeader);

    // Check if the HTTP Authorization header is present and formatted correctly
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//      logger.severe("#### invalid authorizationHeader : " + authorizationHeader);
      throw new NotAuthorizedException("Authorization header must be provided with a 'Bearer prefix' ");
    }

    // Extract the token from the HTTP Authorization header
    String token = authorizationHeader.substring("Bearer".length()).trim();

    try {

      // Validate the token
//      Key key = keyGenerator.generateKey();
//      Jwts.parser().setSigningKey(key).parseClaimsJws(token);
//      logger.info("#### valid token : " + token);
      DecodedJWT jwt = verifier.verify(token);
      PVDecodedJWT pvDecodedJWT = new PVDecodedJWT();

      pvDecodedJWT.setJwt(jwt);
      requestContext.setProperty("pvDecodedJWT", pvDecodedJWT);


    } catch (Exception e) {
//      logger.severe("#### invalid token : " + token);
      requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }
  }
}