package org.example.ldap;

import java.util.List;

public interface LdapService {

  List<LdapUser> getAllUsersWithRoles();

}