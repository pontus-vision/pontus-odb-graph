package uk.gov.cdp.shadow.user.auth;

import static uk.gov.cdp.shadow.user.auth.util.PropertiesUtil.property;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;

class CDPShadowUserSaltKey {
  private static volatile CDPShadowUserSaltKey cdpShadowUserSaltKey;
  private static final Object mutex = new Object();
  private Key key;

  private CDPShadowUserSaltKey() throws Exception {
    String keyStoreLocation = property("shadow.user.keystore.location");
    String keyStorePassword = property("shadow.user.keystore.pwd");
    String keyAlias = property("shadow.user.key.alias");
    String keyPassword = property("shadow.user.key.pwd");
    String keyStoreType = property("shadow.user.key.store.type");

//    try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(keyStoreLocation)) {
      try(InputStream is = new FileInputStream(keyStoreLocation)) {
      KeyStore keystore = KeyStore.getInstance(keyStoreType);
      keystore.load(is, keyStorePassword.toCharArray());
      key = keystore.getKey(keyAlias, keyPassword.toCharArray());
    }
  }

  public Key get() {
    return key;
  }

  public static CDPShadowUserSaltKey instance() throws Exception {
    CDPShadowUserSaltKey result = cdpShadowUserSaltKey;
    if (result == null) {
      synchronized (mutex) {
        result = cdpShadowUserSaltKey;
        if (result == null) cdpShadowUserSaltKey = result = new CDPShadowUserSaltKey();
      }
    }
    return result;
  }
}
