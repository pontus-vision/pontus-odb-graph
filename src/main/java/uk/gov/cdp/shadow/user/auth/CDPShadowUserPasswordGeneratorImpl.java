package uk.gov.cdp.shadow.user.auth;

import java.security.Key;
import java.util.Base64;
import javax.crypto.Mac;
import uk.gov.cdp.shadow.user.auth.exception.PasswordGenerationFailedException;

public class CDPShadowUserPasswordGeneratorImpl implements CDPShadowUserPasswordGenerator {
  @Override
  public String generate(Key key, String algoStr, String subject) {
    try {
      Mac mac = Mac.getInstance(algoStr);
      mac.init(key);
      byte[] rawHmac = mac.doFinal(subject.getBytes());
      return Base64.getEncoder().encodeToString(rawHmac);
    } catch (Exception e) {
      throw new PasswordGenerationFailedException(e);
    }
  }
}
