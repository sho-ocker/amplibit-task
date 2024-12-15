package org.example.users;

import java.util.UUID;
import org.example.inventory.InventoryService;
import org.example.ldap.LdapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSynchronizationService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LdapService ldapService;

  @Autowired
  private InventoryService inventoryService;

  @Scheduled(fixedRate = 300000)
  @Transactional
  public void synchronizeUsers() {
    var ldapUsers = ldapService.getAllUsersWithRoles();

    ldapUsers.forEach(ldapUser -> {
      var user = User.builder()
                     .id(UUID.randomUUID())
                     .username(ldapUser.getUsername())
                     .role(ldapUser.getRole())
                     .build();

      userRepository.upsertUser(user.getId(), user.getUsername(), user.getRole());
      inventoryService.createTestInventoryItemsForUser(user.getId());
    });
  }

}
