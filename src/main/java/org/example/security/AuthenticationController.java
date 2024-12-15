package org.example.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.example.security.dto.LoginResponse;
import org.example.security.dto.LoginUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

  @Autowired
  private AuthenticationService authenticationService;

  @Operation(summary = "Authenticate a user and generate a JWT token.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User authenticated successfully.",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
      @ApiResponse(responseCode = "401", description = "Invalid username or password.",
                   content = @Content(mediaType = "application/json"))
  })
  @PostMapping("/login")
  public ResponseEntity<?> authenticate(@RequestBody LoginUserDto authRequest) {
    try {
      LoginResponse loginResponse = authenticationService.authenticate(authRequest);
      return ResponseEntity.ok(loginResponse);
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
  }

  @Operation(summary = "Logout the authenticated user and invalidate their JWT token.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User logged out successfully.",
                   content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "401", description = "User is not logged in.",
                   content = @Content(mediaType = "application/json"))
  })
  @PostMapping("/logout")
  public ResponseEntity<String> logout(final HttpServletRequest request) {
    try {
      authenticationService.logout(request);
      return ResponseEntity.ok("You have been logged out successfully.");
    } catch (IllegalStateException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
  }

}
