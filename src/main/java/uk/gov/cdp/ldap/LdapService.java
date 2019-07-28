package uk.gov.cdp.ldap;

/*
  Author: Deepesh Rathore
 */

import java.util.List;

public interface LdapService
{

  boolean login(String userName, String password);

  void createUserAccount(String userName, String password, List<String> groups);

  boolean userExist(String userName);

  static final String LDAP_DOMAIN_ROOT = "ldap.domain.root";
  static final String LDAP_SERVER_HOSTNAME = "ldap.server.hostname";
  static final String LDAP_PORT = "ldap.port";
  static final String LDAP_ADMIN_USER = "ldap.admin.user";
  static final String LDAP_ADMIN_USER_PWD = "ldap.admin.user.pwd";
  static final String LDAP_USER_GROUP = "ldap.user.group";
  static final String LDAP_DOMAIN_NAME = "ldap.domain.name";
  static final String LDAP_SECURITY_AUTHENTICATION = "ldap.security.authentication";
  static final String LDAP_USER_EXP_DATE = "ldap.user.exp.date";
  static final String CN = "cn";

  static final String LDAP_USER_LOGIN_SHELL = "loginShell";
  static final String LDAP_USER_LOGIN_SHELL_DEFAULT = "/sbin/nologin";

  static final String LDAP_USER_HOMEDIR = "homeDirectory";
  static final String LDAP_USER_HOMEDIR_DEFAULT = "/";
  static final String SAM_ACCOUNT_NAME = "sAMAccountName";
  static final String KRB_PASSWORD_EXPIRATION = "krbpasswordexpiration";
  static final String KRB_PASSWORD_EXPIRATION_DATE_DEFAULT = "20351231011529Z";
  static final String LDAP_USER_PREFIX = "ldap.user.prefix";
  static final String LDAP_USER_PREFIX_DEFAULT = "";
  static final String LDAP_USER_SUFFIX = "ldap.user.suffix";
  static final String LDAP_USER_SUFFIX_DEFAULT = ",cn=users,cn=compat";

  static final String LDAP_USER_SEARCH_FILTER_PATTERN = "ldap.user.search.filter.pattern";
  static final String LDAP_USER_SEARCH_FILTER_PATTERN_DEFAULT = "(&(objectClass=user)(sAMAccountName={}))";

  static final String LDAP_GROUP_SEARCH_FILTER_PATTERN = "ldap.group.search.filter.pattern";
  static final String LDAP_GROUP_SEARCH_FILTER_PATTERN_DEFAULT = "(&(objectClass=groupofnames))";
  // (&(objectClass=group))"; (in samba)

  static final String LDAP_USER_CREATION_OBJECTS_CSV = "ldap.user.creation.objects.csv";
  static final String LDAP_USER_CREATION_OBJECTS_CSV_DEFAULT = "top,person,organizationalPerson,user";

  static final String LDAP_USER_FREE_IPA_MODE = "ldap.user.creation.freeipa.mode";
  static final String LDAP_USER_FREE_IPA_MODE_DEFAULT = "true";
  static final String LDAP_USER_KRB_REALM = "ldap.user.krb.realm";
  static final String LDAP_USER_KRB_REALM_DEFAULT = "@NONPROD.CDP.INTERNAL";

  static final String LDAP_USER_KRB_TICKET_FLAGS = "ldap.user.krb.ticket.flags";
  static final String LDAP_USER_KRB_TICKET_FLAGS_DEFAULT = "128";

  static final String USER_PRINCIPAL_NAME = "userPrincipalName";
  static final String UID = "uid";
  static final String USER_ACCOUNT_CONTROL = "userAccountControl";
  static final String LDAP_PROTOCOL = "ldap.protocol";

  final int UF_NORMAL_ACCOUNT = 0x0200;
  final int UF_ACCOUNTENABLE = 0x0001;
  final int UF_PASSWD_NOTREQD = 0x0020;
  final int UF_DONT_EXPIRE_PASSWD = 0x10000;

  final int UF_ACCOUNTDISABLE = 0x0002;
  final int UF_PASSWD_CANT_CHANGE = 0x0040;
  final int UF_PASSWORD_EXPIRED = 0x800000;

}
