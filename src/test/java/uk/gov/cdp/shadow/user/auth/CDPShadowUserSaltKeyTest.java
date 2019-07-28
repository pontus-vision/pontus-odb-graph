package uk.gov.cdp.shadow.user.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static uk.gov.cdp.shadow.user.auth.util.PropertiesUtil.property;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;

import org.junit.BeforeClass;
import org.junit.Test;

public class CDPShadowUserSaltKeyTest {
  private static final String KEYSTORE_TYPE = "JCEKS";

    @BeforeClass
    public static void setup() {

        System.setProperty("shadow.user.keystore.location", "keystore.ks");
        System.setProperty("shadow.user.keystore.pwd", "**874");
        System.setProperty("shadow.user.key.pwd", "874_###");
        System.setProperty("shadow.user.key.alias", "Key_Alias");
        System.setProperty("shadow.user.key.algo", "HmacSHA512");
        System.setProperty("shadow.user.key.store.type", "JCEKS");
    }

  @Test
  public void secretSaltKeyIsSuccessfullyLoadedFromKeyStore() throws Exception {
    String keyStoreLocation = property("shadow.user.keystore.location");
    String keyStorePassword = property("shadow.user.keystore.pwd");
    String keyAlias = property("shadow.user.key.alias");
    String keyPassword = property("shadow.user.key.pwd");
    String keyStoreType = property("shadow.user.key.store.type");

    try (InputStream fileInputStream =
        this.getClass().getClassLoader().getResourceAsStream(keyStoreLocation)) {
      KeyStore keyStore = KeyStore.getInstance(keyStoreType);
      keyStore.load(fileInputStream, keyStorePassword.toCharArray());

      Key expectedKey = keyStore.getKey(keyAlias, keyPassword.toCharArray());

      assertThat(expectedKey, is(CDPShadowUserSaltKey.instance().get()));
    }
  }
}
