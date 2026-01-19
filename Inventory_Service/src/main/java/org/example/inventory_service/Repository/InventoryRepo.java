package org.example.inventory_service.Repository;

import org.example.inventory_service.Entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryRepo extends JpaRepository<InventoryEntity,Integer> , JpaSpecificationExecutor<InventoryEntity> {
}
