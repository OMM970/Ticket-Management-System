package org.example.inventory_service.Service;

import org.example.inventory_service.Dto.CustomResponseDto;
import org.example.inventory_service.Entity.InventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface CustomerInventoryService {
    CustomResponseDto maptoinventoryCustomResponseDto(InventoryEntity entity);

    Page<CustomResponseDto> getAllInventory(
            String source,
            String destination,
            Date fromDate,
            Date toDate,
            Pageable pageable);
}
