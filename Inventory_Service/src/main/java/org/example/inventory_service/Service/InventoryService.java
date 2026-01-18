package org.example.inventory_service.Service;

import jakarta.persistence.Entity;
import org.example.inventory_service.Dto.RequestDto;
import org.example.inventory_service.Dto.ResponseDto;
import org.example.inventory_service.Entity.InventoryEntity;

public interface InventoryService {
    ResponseDto addToinventory(RequestDto RequestDto);

    ResponseDto maptoinventoryResponseDto(InventoryEntity entity);
}
