package uk.gov.cdp.shadow.user.auth.integration;


import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.cdp.shadow.user.auth.AuthenticationService;
import uk.gov.cdp.shadow.user.auth.guice.TestApplicationModule;


@RunWith(JukitoRunner.class)
public class AuthenticationServiceTest {

  public static class TestModule extends JukitoModule {

    @Override
    protected void configureTest() {
      install(new TestApplicationModule());
    }
  }

  //  @Inject private LdapService ldapService;

  @Before
  public void setup()  {
    /*LdapConnection connection = new LdapNetworkConnection("127.0.0.1", 389);

    connection.add(
        new DefaultEntry(
            "cn=testadd,ou=system", // The Dn
            "ObjectClass: top",
            "ObjectClass: person",
            "cn: testadd_cn",
            "sn: testadd_sn"));

    assertTrue(connection.exists("cn=testadd,ou=system"));*/
  }

  @Test
  public void whenUserDoesntExist_ItsCreated_AndAuthenticated(AuthenticationService underTest) {

//    underTest.authenticate("cdp_test_deepesh", "pa55w0rdDSR", "biz/org", Collections.emptyList());
    //    verify(ldapService).createUserAccount(any(), any());
  }
}
