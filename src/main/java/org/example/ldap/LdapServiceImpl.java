package org.example.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class LdapServiceImpl implements LdapService {

  @Autowired
  private LdapTemplate ldapTemplate;

  public List<LdapUser> getAllUsersWithRoles() {
    List<LdapUser> usersWithRoles = new ArrayList<>();

    var rolesQuery = LdapQueryBuilder.query()
                                     .base("ou=Roles")
                                     .attributes("cn", "member")
                                     .where("objectClass").is("groupOfNames");

    ldapTemplate.search(rolesQuery, (AttributesMapper<Void>) attributes -> {
      var roleName = (String) attributes.get("cn").get();
      var membersAttribute = attributes.get("member");

      if (membersAttribute != null) {
        var membersList = Collections.list(membersAttribute.getAll());
        for (var memberDn : membersList) {
          var username = extractUsernameFromDn((String) memberDn);

          if (username != null) {
            usersWithRoles.add(
                LdapUser.builder()
                        .username(username)
                        .dn(((String) memberDn).trim())
                        .role(roleName)
                        .build()
            );
          }
        }
      }

      return null;
    });

    return usersWithRoles;
  }

  private String extractUsernameFromDn(final String dn) {
    if (dn.startsWith("cn=")) {
      var endIndex = dn.indexOf(',');
      return endIndex != -1 ? dn.substring(3, endIndex) : dn.substring(3);
    }
    return null;
  }

}
