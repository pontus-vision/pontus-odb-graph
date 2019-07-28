package uk.gov.cdp.shadow.user.auth.guice;

import com.google.inject.AbstractModule;
import uk.gov.cdp.shadow.user.auth.AuthenticationService;
import uk.gov.cdp.shadow.user.auth.AuthenticationServiceImpl;
import uk.gov.cdp.shadow.user.auth.CDPShadowUserPasswordGenerator;
import uk.gov.cdp.shadow.user.auth.CDPShadowUserPasswordGeneratorImpl;

import javax.inject.Singleton;

class AuthModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(AuthenticationService.class).to(AuthenticationServiceImpl.class).in(Singleton.class);
    bind(CDPShadowUserPasswordGenerator.class)
        .to(CDPShadowUserPasswordGeneratorImpl.class)
        .in(Singleton.class);
  }
}

public class TestApplicationModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new AuthModule());
  }
}
