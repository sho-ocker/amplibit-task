package org.example.security;

import jakarta.servlet.http.HttpServletRequest;
import org.example.security.dto.LoginResponse;
import org.example.security.dto.LoginUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Override
  public LoginResponse authenticate(LoginUserDto authRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
      );

      UserDetails user = (UserDetails) authentication.getPrincipal();
      String jwtToken = jwtService.generateToken(user);

      return LoginResponse.builder()
                          .token(jwtToken)
                          .expiresIn(jwtService.getExpirationTime())
                          .build();
    } catch (BadCredentialsException ex) {
      throw new BadCredentialsException("Invalid username or password");
    }
  }

  @Override
  public void logout(HttpServletRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
      throw new IllegalStateException("User is not logged in.");
    }

    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      jwtService.invalidateToken(token);
    }
    SecurityContextHolder.clearContext();
  }

}
