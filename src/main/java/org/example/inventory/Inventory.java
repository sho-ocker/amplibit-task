package org.example.inventory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.users.User;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory")
@Schema(description = "Represents an inventory item.")
public class Inventory {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Schema(description = "Unique identifier of the inventory.", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  @Column(nullable = false)
  @Schema(description = "Name of the inventory item.", example = "Laptop")
  private String name;

  @ManyToOne
  @JoinColumn(name = "assigned_to", referencedColumnName = "id")
  @Schema(description = "The user to whom the inventory is assigned.")
  private User assignedTo;

}
