package uk.gov.cdp.shadow.user.auth;

import java.security.Key;

public interface CDPShadowUserPasswordGenerator {
  String generate(Key key, String algoStr, String subject);
}
