package uk.gov.cdp.shadow.user.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.cdp.shadow.user.auth.exception.PasswordGenerationFailedException;

public class CDPShadowUserPasswordGeneratorTest {

  private CDPShadowUserPasswordGeneratorImpl cdpShadowUserPasswordGenerator =
      new CDPShadowUserPasswordGeneratorImpl();
  private Key salt = new SecretKeySpec("SECRET-KEY".getBytes(), "AES");
  private String subject = "HEX-12454";
  private String algoStr = "HmacSHA512";

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void passwordGeneratedBasedOnSaltAndHashAlgorithm() throws Exception {

    String expectedPassword = generateExpectedPassword();

    String actualPassword = cdpShadowUserPasswordGenerator.generate(salt, algoStr, subject);

    assertThat(actualPassword, is(expectedPassword));
  }

  private String generateExpectedPassword() throws NoSuchAlgorithmException, InvalidKeyException {
    Mac mac = Mac.getInstance(algoStr);
    mac.init(salt);
    byte[] rawHmac = mac.doFinal(subject.getBytes());
    return Base64.getEncoder().encodeToString(rawHmac);
  }

  @Test
  public void throwExceptionWhenInvalidAlgoStringPassed() {

    expectedException.expect(PasswordGenerationFailedException.class);
    expectedException.expectCause(IsInstanceOf.instanceOf(NoSuchAlgorithmException.class));
    cdpShadowUserPasswordGenerator.generate(salt, "Invalid-algo", subject);
  }

  @Test
  public void throwExceptionWhenInvalidSaltKeyPassed() {
    expectedException.expect(PasswordGenerationFailedException.class);
    expectedException.expectCause(IsInstanceOf.instanceOf(InvalidKeyException.class));
    cdpShadowUserPasswordGenerator.generate(null, algoStr, subject);
  }
}
