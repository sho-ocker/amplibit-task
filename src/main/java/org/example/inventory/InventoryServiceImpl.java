package org.example.inventory;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.users.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final UserRepository userRepository;

  public List<Inventory> getInventory() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var username = authentication.getName();

    var user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

    if ("USER".equals(user.getRole())) {
      return inventoryRepository.findByAssignedTo(user);
    } else if ("VIEWER".equals(user.getRole()) || "ADMIN".equals(user.getRole())) {
      return inventoryRepository.findAll();
    } else {
      throw new IllegalAccessError("Unauthorized role");
    }
  }

  @Transactional
  public ResponseEntity<String> assignInventoryToUser(final UUID inventoryId, final UUID userId) {
    var inventoryOpt = inventoryRepository.findById(inventoryId);
    var userOpt = userRepository.findById(userId);

    if (inventoryOpt.isPresent() && userOpt.isPresent()) {
      var inventory = inventoryOpt.get();
      inventory.setAssignedTo(userOpt.get());
      inventoryRepository.save(inventory);
      return ResponseEntity.ok().body("Inventory assigned successfully");
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory or User not found");
    }
  }

  @Transactional
  public void createTestInventoryItemsForUser(UUID userId) {
    var userOpt = userRepository.findById(userId);

    if (userOpt.isPresent()) {
      var user = userOpt.get();

      var inventory1 = Inventory.builder().id(UUID.randomUUID()).name(user.getUsername() + "'s item 1").assignedTo(user).build();
      var inventory2 = Inventory.builder().id(UUID.randomUUID()).name(user.getUsername() + "'s item 2").assignedTo(user).build();

      inventoryRepository.save(inventory1);
      inventoryRepository.save(inventory2);

      ResponseEntity.ok().body("Inventory items created successfully for user " + userId);
    } else {
      ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
  }

}
