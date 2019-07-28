package uk.gov.cdp.shadow.user.auth.exception;
/*
  Author: Deepesh Rathore
 */
public class PasswordGenerationFailedException extends RuntimeException {
  public PasswordGenerationFailedException(Exception cause) {
    super(cause);
  }
}
