package uk.gov.cdp.ldap;
/*
  Author: Deepesh Rathore
 */

import static uk.gov.cdp.shadow.user.auth.util.PropertiesUtil.property;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cdp.shadow.user.auth.exception.LdapGroupNotFoundException;
import uk.gov.cdp.shadow.user.auth.exception.LdapServiceException;

public class LdapServiceSambaImpl implements LdapService {

  private static final String LDAP_DOMAIN_ROOT = "ldap.domain.root";
  private static final String LDAP_SERVER_HOSTNAME = "ldap.server.hostname";
  private static final String LDAP_PORT = "ldap.port";
  private static final String LDAP_ADMIN_USER = "ldap.admin.user";
  private static final String LDAP_ADMIN_USER_PWD = "ldap.admin.user.pwd";
  private static final String LDAP_USER_GROUP = "ldap.user.group";
  private static final String LDAP_DOMAIN_NAME = "ldap.domain.name";
  private static final String CN = "cn";
  private static final String SAM_ACCOUNT_NAME = "sAMAccountName";
  private static final String USER_PRINCIPAL_NAME = "userPrincipalName";
  private static final String UID = "uid";
  private static final String USER_ACCOUNT_CONTROL = "userAccountControl";
  private static final String LDAP_PROTOCOL = "ldap.protocol";
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private static final String ldapServerUrl = url();

  private final int UF_NORMAL_ACCOUNT = 0x0200;
  private final int UF_ACCOUNTENABLE = 0x0001;
  private final int UF_PASSWD_NOTREQD = 0x0020;
  private final int UF_DONT_EXPIRE_PASSWD = 0x10000;

  @Override
  public boolean login(String userName, String password) {

    logger.debug(String.format("Trying to login..... user %s", getUserDN(userName)));
    try {
      LdapContext ldapContext = connect(userName, password);
      logger.debug("user {} is authenticated...", userName);
      ldapContext.close();
      return true;
    } catch (NamingException e) {
      throw new LdapServiceException(e);
    }
  }

  @Override
  public boolean userExist(String userName) {
    String searchFilter = "(&(objectClass=user)(sAMAccountName=" + userName + "))";
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    String ldapSearchBase = domainRoot();
    boolean userFound;
    LdapContext context = null;
    try {
      context = connectAsAdmin();

      logger.debug("Searching for user === {}", userName);

      NamingEnumeration<SearchResult> results =
          context.search(ldapSearchBase, searchFilter, searchControls);

      userFound = results.hasMoreElements();
    } catch (NamingException e) {
      throw new LdapServiceException(e);
    } finally {
      closeContext(context);
    }
    return userFound;
  }

  @Override
  public void createUserAccount(String userName, String password, List<String> groups) {

    logger.info("Creating user --- {}", userName);
    LdapContext context = null;
    try {
      context = connectAsAdmin();
      createUser(context, userName);
      addUserToUserGroup(context, userName);
      addUserToGroups(context, userName, groups);
      setUserPassword(context, getUserDN(userName), password);
      logger.info("Created user --- {}", userName);
    } catch (Exception ex) {
      throw new LdapServiceException(ex);
    } finally {
      closeContext(context);
    }
  }

  private void closeContext(LdapContext context) {
    try {
      if (context != null) {
        context.close();
      }
    } catch (Exception ex) {
    }
  }

  private void addUserToGroups(LdapContext context, String userName, List<String> groups) throws NamingException {

    for (String group : groups) {
      if (!findGroupByDn(context, group)) {
        throw new LdapGroupNotFoundException(group);
      }
      addUserToGroup(userName, group, context);
    }
  }

  private boolean findGroupByDn(LdapContext context, String groupDN) throws NamingException {
    String searchFilter = "(&(objectClass=group))";
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    String ldapSearchBase = groupDN;
    logger.debug("Searching for group === {}", groupDN);

    NamingEnumeration<SearchResult> results =
        context.search(ldapSearchBase, searchFilter, searchControls);
    return results.hasMore();
  }

  private void createUser(LdapContext context, String userName) throws NamingException {
    // Create a container set of attributes
    Attributes container = new BasicAttributes();
    // Add these to the container
    container.put(getObjectClasses());
    container.put(sAMAccountNameAttribute(userName));
    container.put(userPrincipalNameAttribute(userName));
    container.put(cnAttribute(userName));
    container.put(uidAttribute(userName));
    container.put(userAccountControlAttribute());

    // Create the entry
    context.createSubcontext(getUserDN(userName), container);
  }

  private void addUserToUserGroup(LdapContext context, String userName) throws NamingException {
    String defaultUserGroup = property(LDAP_USER_GROUP);
    addUserToGroup(userName, defaultUserGroup, context);
  }

  private void addUserToGroup(String userName, String userGroup, LdapContext context) throws NamingException {
    ModificationItem[] mods = new ModificationItem[1];
    Attribute mod = new BasicAttribute("member", getUserDN(userName));
    mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod);
    context.modifyAttributes(userGroup, mods);
  }

  private void setUserPassword(LdapContext context, String userDn, String password)
      throws NamingException, UnsupportedEncodingException {

    String newQuotedPassword = "\"" + password + "\"";
    byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
    ModificationItem[] mods = new ModificationItem[2];
    mods[0] =
        new ModificationItem(
            DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", newUnicodePassword));
    mods[1] =
        new ModificationItem(
            DirContext.REPLACE_ATTRIBUTE,
            new BasicAttribute(
                USER_ACCOUNT_CONTROL, Integer.toString(UF_NORMAL_ACCOUNT + UF_DONT_EXPIRE_PASSWD)));

    context.modifyAttributes(userDn, mods);
  }

  private Attribute userAccountControlAttribute() {
    return new BasicAttribute(
        USER_ACCOUNT_CONTROL,
        Integer.toString(
            UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD + UF_DONT_EXPIRE_PASSWD + UF_ACCOUNTENABLE));
  }

  private BasicAttribute uidAttribute(String userName) {
    return new BasicAttribute(UID, userName);
  }

  private BasicAttribute userPrincipalNameAttribute(String userName) {
    return new BasicAttribute(USER_PRINCIPAL_NAME, userName + "@" + property(LDAP_DOMAIN_NAME));
  }

  private BasicAttribute sAMAccountNameAttribute(String userName) {
    return new BasicAttribute(SAM_ACCOUNT_NAME, userName);
  }

  private BasicAttribute cnAttribute(String userName) {
    return new BasicAttribute(CN, userName);
  }

  private Attribute getObjectClasses() {
    Attribute objClasses = new BasicAttribute("objectClass");
    objClasses.add("top");
    objClasses.add("person");
    objClasses.add("organizationalPerson.Natural");
    objClasses.add("user");
    return objClasses;
  }

  private LdapContext connect(String userName, String userPwd) throws NamingException {

    logger.debug("Connecting to LDAP Server === {}", ldapServerUrl);
    Hashtable<String, String> env = new Hashtable<>();

    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

    // set security credentials, note using simple cleartext authentication
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL, getUserDN(userName));
    env.put(Context.SECURITY_CREDENTIALS, userPwd);

    // connect to my domain controller
    env.put(Context.PROVIDER_URL, ldapServerUrl);
    return new InitialLdapContext(env, null);
  }

  private LdapContext connectAsAdmin() throws NamingException {
    return connect(property(LDAP_ADMIN_USER), property(LDAP_ADMIN_USER_PWD));
  }

  private String getUserDN(String aUsername) {
    return "CN=" + aUsername + "," + domainRoot();
  }

  private String domainRoot() {
    return property(LDAP_DOMAIN_ROOT);
  }

  private static String url() {
    return String.format(
        "%s://%s:%s", property(LDAP_PROTOCOL), property(LDAP_SERVER_HOSTNAME), property(LDAP_PORT));
  }
}
