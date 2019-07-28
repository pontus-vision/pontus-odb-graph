package uk.gov.cdp.shadow.user.auth.exception;

public class LdapGroupNotFoundException extends RuntimeException {
    public LdapGroupNotFoundException(String group) {
        super("Ldap group not found == "+group);
    }
}
