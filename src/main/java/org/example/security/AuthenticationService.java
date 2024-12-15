package org.example.security;

import jakarta.servlet.http.HttpServletRequest;
import org.example.security.dto.LoginResponse;
import org.example.security.dto.LoginUserDto;

public interface AuthenticationService {

  LoginResponse authenticate(LoginUserDto authRequest);

  void logout(HttpServletRequest request);

}
