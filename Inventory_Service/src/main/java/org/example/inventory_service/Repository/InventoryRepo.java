package org.example.inventory_service.Repository;

import org.example.inventory_service.Entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepo extends JpaRepository<InventoryEntity,Integer> {
}
