package org.example.users;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  @Modifying
  @Transactional
  @Query(value = """
                 INSERT INTO users (id, username, role)
                 VALUES (:id, :username, :role)
                 ON CONFLICT (username) 
                 DO UPDATE SET role = :role
                 """, nativeQuery = true)
  void upsertUser(@Param("id") UUID id, @Param("username") String username, @Param("role") String role);

  Optional<User> findByUsername(String username);

}
