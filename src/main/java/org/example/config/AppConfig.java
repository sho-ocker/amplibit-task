package org.example.config;

import org.example.ldap.CustomLdapUserDetailsMapper;
import org.example.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CustomLdapUserDetailsMapper customLdapUserDetailsMapper;

  @Value("${ldap.url}")
  private String ldapUrl;

  @Value("${ldap.base}")
  private String ldapBase;

  @Value("${ldap.userDn}")
  private String ldapUserDn;

  @Value("${ldap.password}")
  private String ldapPassword;

  @Bean
  public LdapContextSource contextSource() {
    LdapContextSource ldapContextSource = new LdapContextSource();
    ldapContextSource.setUrl(ldapUrl);
    ldapContextSource.setBase(ldapBase);
    ldapContextSource.setUserDn(ldapUserDn);
    ldapContextSource.setPassword(ldapPassword);
    ldapContextSource.afterPropertiesSet();
    return ldapContextSource;
  }

  @Bean
  public LdapTemplate ldapTemplate() {
    return new LdapTemplate(contextSource());
  }

  @Bean
  AuthenticationManager authenticationManager(BaseLdapPathContextSource source) {
    LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(source);
    factory.setUserDnPatterns("cn={0},ou=Users");
    factory.setUserDetailsContextMapper(customLdapUserDetailsMapper);
    return factory.createAuthenticationManager();
  }

//  @Bean
//  UserDetailsService userDetailsService() {
//    return username -> userRepository.findByUsername(username)
//                                     .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//  }
//
//  @Bean
//  AuthenticationProvider authenticationProvider() {
//    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
//    authProvider.setUserDetailsService(userDetailsService());
//    authProvider.setPasswordEncoder(passwordEncoder());
//
//    return authProvider;
//  }
//
//  @Bean
//  BCryptPasswordEncoder passwordEncoder() {
//    return new BCryptPasswordEncoder();
//  }

}
