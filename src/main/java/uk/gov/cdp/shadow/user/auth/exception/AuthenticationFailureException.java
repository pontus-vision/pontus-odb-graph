package uk.gov.cdp.shadow.user.auth.exception;
/*
  Author: Deepesh Rathore
 */
public class AuthenticationFailureException extends RuntimeException {
  public AuthenticationFailureException(Throwable cause) {
    super(cause);
  }
}
