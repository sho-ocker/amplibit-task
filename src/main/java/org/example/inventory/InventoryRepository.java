package org.example.inventory;

import java.util.List;
import java.util.UUID;
import org.example.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

  List<Inventory> findByAssignedTo(User user);

}
