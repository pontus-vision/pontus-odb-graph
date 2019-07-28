package uk.gov.cdp.shadow.user.auth;

import static uk.gov.cdp.shadow.user.auth.util.PropertiesUtil.property;

import com.pontusvision.gdpr.JWTToKerberosAuthenticator;
import java.security.Key;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cdp.ldap.LdapService;
import uk.gov.cdp.shadow.user.auth.exception.AuthenticationFailureException;

public class AuthenticationServiceImpl implements AuthenticationService {

  private static final String SHADOW_USER_KEY_ALGO = "shadow.user.key.algo";
  @Inject private LdapService ldapService;

  @Inject private CDPShadowUserPasswordGenerator cdpShadowUserPasswordGenerator;

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  private boolean createUser = property("ldap.create.user").equals("true");

  private boolean authenticateWithKerberos = property("kerberos.authentication").equals("true");
  private boolean useCDPShadowSalt = property("shadow.user.salt.password.enable", "false").equals("true");
  private static String keyAlgo = property(SHADOW_USER_KEY_ALGO);
    @Inject
    public AuthenticationServiceImpl(LdapService ldapService, CDPShadowUserPasswordGenerator cdpShadowUserPasswordGenerator) {
        this.ldapService = ldapService;
        this.cdpShadowUserPasswordGenerator = cdpShadowUserPasswordGenerator;
    }

    @Override
  public String authenticate(String userName, String subject, String bizContext, List<String> groups) {

    LOGGER.info(
        String.format("Authenticating user === %s for bizContext == %s", userName, bizContext));
    try {
      Key key = CDPShadowUserSaltKey.instance().get();

      String password = useCDPShadowSalt? cdpShadowUserPasswordGenerator.generate(key, keyAlgo, subject): subject;
      if (createUser && !ldapService.userExist(userName)) {
        ldapService.createUserAccount(userName, password, groups);

      }

      if (authenticateWithKerberos) {
        JWTToKerberosAuthenticator.kinit(userName, password);
      } else {
        ldapService.login(userName, password);
      }
      return password;
    } catch (Exception e) {
      throw new AuthenticationFailureException(e);
    }
  }
}
