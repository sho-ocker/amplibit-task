package org.example.ldap;

import java.util.Collection;
import org.example.users.User;
import org.example.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomLdapUserDetailsMapper extends LdapUserDetailsMapper {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {

    var user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

    return User.builder()
               .username(username)
               .role(user.getRole())
               .build();
  }

}