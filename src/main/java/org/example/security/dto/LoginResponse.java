package org.example.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

  @Schema(description = "JWT token generated upon successful login.", example = "eyJhbGciOiJIUzI1...")
  private String token;

  @Schema(description = "Token expiration time in seconds.", example = "3600")
  private long expiresIn;

}
