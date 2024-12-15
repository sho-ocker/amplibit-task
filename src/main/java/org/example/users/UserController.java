package org.example.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @Operation(summary = "Get a list of all users (Admin only).")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful retrieval of users"),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "403", description = "User doesn't have access")
  })
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

}
