package uk.gov.cdp.ldap;
/*
  Author: Deepesh Rathore
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cdp.shadow.user.auth.exception.LdapGroupNotFoundException;
import uk.gov.cdp.shadow.user.auth.exception.LdapServiceException;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static uk.gov.cdp.shadow.user.auth.util.PropertiesUtil.property;

public class LdapServiceImpl implements LdapService
{


  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private static final String ldapServerUrl = url();

  private static final Random rand = new Random();



  protected String krbRealm;
  protected final String krbTicketFlags;

  protected final String groupSearchFilter;

  protected final boolean ipaMode;

  public LdapServiceImpl()
  {
    ipaMode = Boolean.parseBoolean(property(LDAP_USER_FREE_IPA_MODE, LDAP_USER_FREE_IPA_MODE_DEFAULT));

    groupSearchFilter = property(LDAP_GROUP_SEARCH_FILTER_PATTERN, LDAP_GROUP_SEARCH_FILTER_PATTERN_DEFAULT);
    krbRealm = property(LDAP_DOMAIN_NAME, LDAP_USER_KRB_REALM_DEFAULT);
    if (!krbRealm.startsWith("@"))
    {
      krbRealm = "@" + krbRealm;
    }

    krbTicketFlags = property(LDAP_USER_KRB_TICKET_FLAGS, LDAP_USER_KRB_TICKET_FLAGS_DEFAULT);

  }

  @Override public boolean login(String userName, String password)
  {

    logger.debug(String.format("Trying to login..... user %s", getUserDN(userName)));
    try
    {
      LdapContext ldapContext = connectAsUser(userName, password);
      logger.debug("user {} is authenticated...", userName);
      ldapContext.close();
      return true;
    }
    catch (NamingException e)
    {
      throw new LdapServiceException(e);
    }
  }

  @Override public boolean userExist(String userName)
  {

    String filterPattern = property(LDAP_USER_SEARCH_FILTER_PATTERN, LDAP_USER_SEARCH_FILTER_PATTERN_DEFAULT);

    String searchFilter = filterPattern.replaceAll(Pattern.quote("{}"), userName);
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    String ldapSearchBase = domainRoot();
    boolean userFound;
    LdapContext context = null;
    try
    {
      context = connectAsAdmin();

      logger.debug("Searching for user === {}", userName);

      NamingEnumeration<SearchResult> results = context.search(ldapSearchBase, searchFilter, searchControls);

      userFound = results.hasMoreElements();
    }
    catch (NamingException e)
    {
      throw new LdapServiceException(e);
    }
    finally
    {
      closeContext(context);
    }
    return userFound;
  }

  @Override public void createUserAccount(String userName, String password, List<String> groups)
  {

    logger.info("Creating user --- {}", userName);
    LdapContext context = null;
    try
    {
      context = connectAsAdmin();
      createUser(context, userName, password);
      addUserToUserGroup(context, userName);
      addUserToGroups(context, userName, groups);

      if (!ipaMode)
      {
        setUserPassword(context, getUserDN(userName), password);
      }

      logger.info("Created user --- {}", userName);
    }
    catch (Exception ex)
    {
      throw new LdapServiceException(ex);
    }
    finally
    {
      closeContext(context);
    }
  }

  private void closeContext(LdapContext context)
  {
    try
    {
      if (context != null)
      {
        context.close();
      }
    }
    catch (Exception ex)
    {
    }
  }

  private void addUserToGroups(LdapContext context, String userName, List<String> groups) throws NamingException
  {

    for (String group : groups)
    {
      group = (group.replaceAll(Pattern.quote("/"), ""));
      if (!findGroupByDn(context, group))
      {
        throw new LdapGroupNotFoundException(group);
      }
      addUserToGroup(userName, group, context);
    }
  }

  private boolean findGroupByDn(LdapContext context, String groupDN) throws NamingException
  {
    String searchFilter = groupSearchFilter;
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    String ldapSearchBase = groupDN;
    logger.debug("Searching for group === {}", groupDN);

    NamingEnumeration<SearchResult> results = context.search(ldapSearchBase, searchFilter, searchControls);
    return results.hasMore();
  }

  private void createUser(LdapContext context, String userName, String password) throws NamingException
  {
    // Create a container set of attributes
    Attributes container = new BasicAttributes();
    // Add these to the container
    container.put(getObjectClasses());
    container.put(cnAttribute(userName));
    container.put(uidAttribute(userName));

    if (ipaMode)
    {
      container.put(new BasicAttribute("userPassword", password));
      container.put(passwordExpiration());
      container.put(homeDirectory());
      container.put(loginShell());

      Integer uidHash = userName.hashCode();
      if (uidHash < 0)
      {
        uidHash *= -1;
      }

      container.put(new BasicAttribute("gidNumber", uidHash.toString()));
      container.put(new BasicAttribute("uidNumber", uidHash.toString()));
      container.put(new BasicAttribute("sn", userName));
      container.put(new BasicAttribute("givenName", userName));

      container.put(new BasicAttribute("krbPrincipalName", userName + krbRealm));
      container.put(new BasicAttribute("krbTicketFlags", krbTicketFlags));
      container.put(new BasicAttribute("nsaccountlock", Boolean.FALSE.toString()));
      container.put(new BasicAttribute("displayName", Boolean.FALSE.toString()));
      container.put(new BasicAttribute("initials", userName));



      /*
uid: test39 x
givenname: test39 x
sn: test39 x
cn: test39 test39 x
userPassword: pa55word
krbprincipalname: test39@NONPROD.CDP.INTERNAL
homedirectory: /home/test39
initials: test39
uidnumber: 976000039
gidnumber: 976000039
nsaccountlock: FALSE
displayName: test39 test39
krbPasswordExpiration: 20350606060606Z
krbTicketFlags: 128

       */

      //      uid: barbar
      //      givenname: Bar
      //      sn: Bar
      //      cn: Bar Bar
      //      initials: BB
      //      homedirectory: /home/barbar
      //      gecos: Bar Bar
      //      loginshell: /bin/sh
      //      mail: barbar@rhel72.test
      //    uidnumber: 626000003
      //      gidnumber: 626000003
      //      nsaccountlock: FALSE
      //      has_password: FALSE
      //      has_keytab: FALSE
      //      displayName: Bar Bar
      //      ipaUniqueID: 9d5dddca-dc66-11e5-b542-001a4a23140a
      //      krbPrincipalName: barbar@RHEL72
      //    memberof: cn=ipausers,cn=groups,cn=accounts,dc=rhel72
      //      mepManagedEntry: cn=barbar,cn=groups,cn=accounts,dc=rhel72

      //    String initials = userName.substring(0,2);
      //    container.put(new BasicAttribute("initials",initials));

      NamingException exc = null;

      for (int i = 0; i < 5; i++)
      {
        // Create the entry
        try
        {
          exc = null;
          context.createSubcontext(getUserDN(userName), container);
          break;
        }
        catch (javax.naming.directory.InvalidAttributeValueException e)
        {
          exc = e;
          uidHash = rand.nextInt(9999999);
          container.remove("gidNumber");
          container.remove("uidNumber");
          container.put(new BasicAttribute("gidNumber", uidHash.toString()));
          container.put(new BasicAttribute("uidNumber", uidHash.toString()));

        }

      }

      if (exc != null)
      {
        throw exc;
      }
    }
    else
    {
      container.put(userPrincipalNameAttribute(userName));
      container.put(sAMAccountNameAttribute(userName));

      container.put(userAccountControlAttribute());
      context.createSubcontext(getUserDN(userName), container);

    }

  }

  private void addUserToUserGroup(LdapContext context, String userName) throws NamingException
  {
    String defaultUserGroup = property(LDAP_USER_GROUP);
    addUserToGroup(userName, defaultUserGroup, context);
  }

  private void addUserToGroup(String userName, String userGroup, LdapContext context) throws NamingException
  {
    ModificationItem[] mods = new ModificationItem[1];
    Attribute mod = new BasicAttribute("member", getUserDN(userName));
    mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod);
    context.modifyAttributes(userGroup, mods);
  }

  private void setUserPassword(LdapContext context, String userDn, String password)
      throws NamingException, UnsupportedEncodingException
  {

    String newQuotedPassword = "\"" + password + "\"";
    byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
    ModificationItem[] mods = new ModificationItem[2];

    if (ipaMode)
    {
      mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, passwordExpiration());

      mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", password));

    }
    else
    {
      mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
          new BasicAttribute("unicodePwd", newUnicodePassword));
      mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
          new BasicAttribute(USER_ACCOUNT_CONTROL, Integer.toString(UF_NORMAL_ACCOUNT + UF_DONT_EXPIRE_PASSWD)));

    }
    context.modifyAttributes(userDn, mods);

  }

  private Attribute loginShell()
  {
    return new BasicAttribute(LDAP_USER_LOGIN_SHELL, LDAP_USER_LOGIN_SHELL_DEFAULT);

  }

  private Attribute homeDirectory()
  {
    return new BasicAttribute(LDAP_USER_HOMEDIR, LDAP_USER_HOMEDIR_DEFAULT);

  }

  private Attribute passwordExpiration()
  {
    return new BasicAttribute(KRB_PASSWORD_EXPIRATION,
        property(LDAP_USER_EXP_DATE, KRB_PASSWORD_EXPIRATION_DATE_DEFAULT));

  }

  private Attribute userAccountControlAttribute()
  {
    return new BasicAttribute(USER_ACCOUNT_CONTROL,
        Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD + UF_DONT_EXPIRE_PASSWD + UF_ACCOUNTENABLE));
  }

  private BasicAttribute uidAttribute(String userName)
  {
    return new BasicAttribute(UID, userName);
  }

  private BasicAttribute userPrincipalNameAttribute(String userName)
  {
    return new BasicAttribute(USER_PRINCIPAL_NAME, userName + "@" + property(LDAP_DOMAIN_NAME));
  }

  private BasicAttribute sAMAccountNameAttribute(String userName)
  {
    return new BasicAttribute(SAM_ACCOUNT_NAME, userName);
  }

  private BasicAttribute cnAttribute(String userName)
  {
    return new BasicAttribute(CN, userName);
  }

  private Attribute getObjectClasses()
  {
    Attribute objClasses = new BasicAttribute("objectClass");
    String objectClassesCSV = property(LDAP_USER_CREATION_OBJECTS_CSV, LDAP_USER_CREATION_OBJECTS_CSV_DEFAULT);
    String[] objectClassesCSVArray = objectClassesCSV.split(",");
    for (int i = 0; i < objectClassesCSVArray.length; i++)
    {
      objClasses.add(objectClassesCSVArray[i]);

    }
    //        objClasses.add("top");
    //        objClasses.add("person");
    //        objClasses.add("organizationalPerson.Natural");
    //        objClasses.add("user");
    return objClasses;
  }

  private LdapContext connectAsUser(String userName, String userPwd) throws NamingException
  {
    return connect(getUserDN(userName), userPwd);

  }

  private LdapContext connect(String userName, String userPwd) throws NamingException
  {

    logger.debug("Connecting to LDAP Server === {}", ldapServerUrl);
    Hashtable<String, String> env = new Hashtable<>();

    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

    // set security credentials, note using simple cleartext authentication
    env.put(Context.SECURITY_AUTHENTICATION, property(LDAP_SECURITY_AUTHENTICATION, "simple"));
    env.put(Context.SECURITY_PRINCIPAL, (userName));
    env.put(Context.SECURITY_CREDENTIALS, userPwd);

    // connect to my domain controller
    env.put(Context.PROVIDER_URL, ldapServerUrl);
    return new InitialLdapContext(env, null);
  }

  private LdapContext connectAsAdmin() throws NamingException
  {
    return connect(property(LDAP_ADMIN_USER), property(LDAP_ADMIN_USER_PWD));

  }

  private String getUserDN(String aUsername)
  {

    String domainRootStr = domainRoot();

    domainRootStr = domainRootStr.length() == 0 ? domainRootStr : "," + domainRootStr;

    return property(LDAP_USER_PREFIX, LDAP_USER_PREFIX_DEFAULT) + aUsername + property(LDAP_USER_SUFFIX,
        LDAP_USER_SUFFIX_DEFAULT) + domainRootStr;
  }

  private String domainRoot()
  {

    //        String domainRoot = property(LDAP_DOMAIN_ROOT,"");

    return property(LDAP_DOMAIN_ROOT, "");
  }

  private static String url()
  {
    return String.format("%s://%s:%s", property(LDAP_PROTOCOL), property(LDAP_SERVER_HOSTNAME), property(LDAP_PORT));
  }
}
