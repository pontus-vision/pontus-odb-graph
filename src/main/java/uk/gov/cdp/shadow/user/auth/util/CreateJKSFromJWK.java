package uk.gov.cdp.shadow.user.auth.util;

import org.keycloak.jose.jwk.JWKParser;

import java.io.File;
import java.security.KeyStore;
import java.security.PublicKey;

public class CreateJKSFromJWK extends KeyStoreCreator
{

  public static void main(String[] args) throws Exception
  {
    JWKParser parser = JWKParser.create();

    parser.parse(args[0]);

    File file = new File(keyStoreLocation);
    if (file.exists()) {
      System.out.println("Key store already exists.");
      System.out.println("Exiting...");
      System.exit(0);
    }

    PublicKey pubKey = parser.toPublicKey();

//    KeyStore.PasswordProtection protectionParam = new KeyStore.PasswordProtection(keyStorePassword.toCharArray());


    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    keyStore.load(null, keyStorePassword.toCharArray());

    keyStore.setKeyEntry(keyAlias,pubKey,keyPassword.toCharArray(),null);

//    keyStore.load(null, keyStorePassword.toCharArray());
    saveKeyStore(keyStore, keyStoreLocation, keyStorePassword);


  }

}
