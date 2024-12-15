package org.example.inventory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import org.example.ldap.LdapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

  @Autowired
  LdapService ldapService;

  @Autowired
  private InventoryService inventoryService;

  @Operation(summary = "Get inventory items based on the user's role.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful retrieval of inventory items",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventory.class))),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "403", description = "User doesn't have access")
  })
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'VIEWER')")
  public List<Inventory> getInventory() {
    return inventoryService.getInventory();
  }

  @Operation(summary = "Assign an inventory item to a user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful assignment of inventory item",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventory.class))),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "403", description = "User doesn't have access"),
      @ApiResponse(responseCode = "404", description = "Inventory or user not found")
  })
  @PostMapping("/assign")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> assignInventory(
      @RequestParam @Schema(description = "ID of the inventory item to assign.", example = "123e4567-e89b-12d3-a456-426614174000") final UUID inventoryId,
      @RequestParam @Schema(description = "ID of the user to assign the inventory to.", example = "123e4567-e89b-12d3-a456-426614174000") final UUID userId) {
    return inventoryService.assignInventoryToUser(inventoryId, userId);
  }

}
