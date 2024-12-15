package org.example.inventory;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface InventoryService {

  List<Inventory> getInventory();

  ResponseEntity<String> assignInventoryToUser(UUID inventoryId, UUID userId);

  void createTestInventoryItemsForUser(UUID userId);

}
