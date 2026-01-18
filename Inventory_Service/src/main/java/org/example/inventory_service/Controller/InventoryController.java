package org.example.inventory_service.Controller;

import lombok.RequiredArgsConstructor;
import org.example.inventory_service.Dto.RequestDto;
import org.example.inventory_service.Dto.ResponseDto;
import org.example.inventory_service.Service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/add")
    public ResponseEntity<ResponseDto> addInventory(
            @RequestBody RequestDto requestDto
    ) {
        ResponseDto response = inventoryService.addToinventory(requestDto);
        return ResponseEntity.ok(response);
    }
}
