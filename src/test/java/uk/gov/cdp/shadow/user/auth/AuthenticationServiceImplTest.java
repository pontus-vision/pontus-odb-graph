package uk.gov.cdp.shadow.user.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.apache.hadoop.hbase.shaded.org.apache.commons.io.IOUtils;
import org.hamcrest.core.IsInstanceOf;
import org.jukito.JukitoRunner;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.gov.cdp.ldap.LdapService;
import uk.gov.cdp.shadow.user.auth.exception.AuthenticationFailureException;
import uk.gov.cdp.shadow.user.auth.exception.LdapServiceException;
import uk.gov.cdp.shadow.user.auth.exception.PasswordGenerationFailedException;

@RunWith(JukitoRunner.class)
public class AuthenticationServiceImplTest {

  @Inject private AuthenticationServiceImpl authenticationService;

  @Mock private Key key;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  private String subject = "UID-1234";
  private String userName = "Deepesh";
  private String bizContext = "/org/bu/role";
  private List<String> groups = Collections.emptyList();

  @BeforeClass
  public static void setup() {
      System.setProperty("ldap.create.user", "true");
      System.setProperty("kerberos.authentication", "false");
      URL resource = AuthenticationServiceImplTest.class.getResource("/keystore.ks");
      System.setProperty("shadow.user.keystore.location", resource.getFile());
      System.setProperty("shadow.user.keystore.pwd", "**874");
      System.setProperty("shadow.user.key.pwd", "874_###");
      System.setProperty("shadow.user.key.alias", "Key_Alias");
      System.setProperty("shadow.user.key.algo", "HmacSHA512");
      System.setProperty("shadow.user.key.store.type", "JCEKS");
      System.setProperty("shadow.user.salt.password.enable", "true");
  }

  @Test
  public void testJWKPublicKey(){

    String jwkStr = "{\"kid\":\"lX9v8wAa0YA6SCGBwOJ_qvKLBQP8N8EeRRVFFOFkd-U\",\"kty\":\"RSA\",\"alg\":\"RS256\",\"use\":\"sig\",\"n\":\"0WPixA5JOpqKpBIbPT1ZFdHcaOgkAXK7TkXza11_-OGbCXxT-pQ8isLGwrqjBm5DNnZif2XpBlIuDMWNfr9QGtENqLYdkwN05GuIUVGmEWIRZqGVJ26Lu3akfCk4bEZcNnuic6Qv1FhLqUWr7pgvea_sfYh1Stjl2x-GiZzmVSHx-rFMMyDLSkGSNqFbyclGEhWgLd7BDH3jzsSzFcb0dxrrjp_Jh-yT3a64RVqYjOTtjcxF_jpgUgR5bXGU8Iy3_FJDs7eyUUi6z8LkYDdu6yhvng_eY-IVajKHA5GYSQyXMm6FrWXadEbKITMQkBTaO1y60WJbppV_tl8_kuiYqOwtEJJc2AOjD9Fj0S4Oz5OfVtjfbIavhknIGKm2A_qE8Hfq4aEmarr063YKjGy8ll6SuAt4W_hshwDPOa_DY_SNh8uA_Ei1CkMtUV2bGX7keeEc5qTDW08PBYNgoPqza6GpgepXYNarKkqdgMfRt1qwR9p75gr9yNBEbPWZS_TmA3Bm8rv9BlPK2ek2DXvvlLUUjzpsGpqV0JK0E7lR3DCurnP28VzOqaQFT5cU539QKp_5HLPRth0Zv4MqX68BtjKhG4HzS55Vq8BWCSv34OhzqxeGJ4mFIyijbToVJhEqfFZpozO2wjW7tZCsHA4QjjTCWeiODkscO0hEn-FLHh0\",\"e\":\"AQAB\"}";

    try
    {
      IOUtils.write(jwkStr,new FileOutputStream("jwkFile"));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    WsAndHttpJWTAuthenticationHandler.SSLContextService svc = new WsAndHttpJWTAuthenticationHandler.SSLContextService();
    svc.setKeyStoreFile("jwkFile");
    svc.setKeyStoreType("JWK");

    try
    {
      Key pubKey [] = WsAndHttpJWTAuthenticationHandler.getPublicKey(svc,"");

      for (int i = 0, ilen = pubKey.length; i < ilen; i++)
      {
        assert ("RS256".equals(pubKey[i].getAlgorithm()));
        assert ("X.509".equals(pubKey[i].getFormat()));
      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      assert(false);
    }


  }
  @Test
  public void userAuthenticatedWithGeneratedPassword_WhenUserExists(
      LdapService ldapService, CDPShadowUserPasswordGenerator cdpShadowUserPasswordGenerator) {

    String password = "password";
    when(cdpShadowUserPasswordGenerator.generate(any(Key.class), eq("HmacSHA512"), eq(subject)))
        .thenReturn(password);

    when(ldapService.userExist(userName)).thenReturn(true);

    authenticationService.authenticate(userName, subject, bizContext, groups);

    verify(ldapService).login(userName, password);
    verify(ldapService, never()).createUserAccount(userName, password, groups);
  }

  @Test
  public void userCreatedWithGeneratedPassword_WhenUserDoesntExist(
      LdapService ldapService, CDPShadowUserPasswordGenerator cdpShadowUserPasswordGenerator) {

    String password = "password";
    when(cdpShadowUserPasswordGenerator.generate(any(Key.class), eq("HmacSHA512"), eq(subject)))
        .thenReturn(password);

    when(ldapService.userExist(userName)).thenReturn(false);

    authenticationService.authenticate(userName, subject, bizContext, groups);

    verify(ldapService).login(userName, password);
    verify(ldapService).createUserAccount(userName, password, groups);
  }

  @Test
  public void throwAuthenticationFailureException_WhenPasswordGenerationFailed(
      CDPShadowUserPasswordGenerator cdpShadowUserPasswordGenerator) {
    doThrow(new PasswordGenerationFailedException(new NoSuchAlgorithmException()))
        .when(cdpShadowUserPasswordGenerator)
        .generate(any(), any(), any());
    expectedException.expect(AuthenticationFailureException.class);
    expectedException.expectCause(IsInstanceOf.instanceOf(PasswordGenerationFailedException.class));

    authenticationService.authenticate(userName, subject, bizContext, groups);
  }

  @Test
  public void throwAuthenticationFailureException_WhenLdapExceptionOccur_WhileCreatingUser(
      LdapService ldapService, CDPShadowUserPasswordGenerator cdpShadowUserPasswordGenerator) {

    String password = "password";
    when(cdpShadowUserPasswordGenerator.generate(any(Key.class), eq("HmacSHA512"), eq(subject)))
        .thenReturn(password);

    doThrow(new LdapServiceException(new NamingException()))
        .when(ldapService)
        .createUserAccount(userName, password, groups);

    when(ldapService.userExist(userName)).thenReturn(false);

    expectedException.expect(AuthenticationFailureException.class);
    expectedException.expectCause(IsInstanceOf.instanceOf(LdapServiceException.class));

    authenticationService.authenticate(userName, subject, bizContext, groups);
  }

  @Test
  public void throwAuthenticationFailureException_WhenLdapExceptionOccurWhileLoggingIn(
      LdapService ldapService, CDPShadowUserPasswordGenerator cdpShadowUserPasswordGenerator) {
    String password = "password";
    when(cdpShadowUserPasswordGenerator.generate(any(Key.class), eq("HmacSHA512"), eq(subject)))
        .thenReturn(password);

    when(ldapService.userExist(userName)).thenReturn(true);
    doThrow(new LdapServiceException(new NamingException()))
        .when(ldapService)
        .login(userName, password);
    expectedException.expect(AuthenticationFailureException.class);
    expectedException.expectCause(IsInstanceOf.instanceOf(LdapServiceException.class));

    authenticationService.authenticate(userName, subject, bizContext, groups);
  }
}
