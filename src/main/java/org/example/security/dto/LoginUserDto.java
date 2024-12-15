package org.example.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginUserDto {

  @Schema(description = "Username of the user attempting to log in.", example = "johndoe")
  private String username;

  @Schema(description = "Password of the user attempting to log in.", example = "securePassword123")
  private String password;

}
