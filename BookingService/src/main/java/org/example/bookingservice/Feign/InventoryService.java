package org.example.bookingservice.Feign;

import org.example.bookingservice.Dto.BookingResponseDto;
import org.example.bookingservice.Dto.Feign.InventoryResponseDto;
import org.example.bookingservice.Dto.Feign.SeatsActionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "${inventory.service.name}",
        url  = "${inventory.service.url}"
)
public interface InventoryService {
    @GetMapping("/api/v1/admin/inventory/{id}")
    InventoryResponseDto getInventoryById(@PathVariable Long id);

    @PostMapping("/api/v1/admin/inventory/hold")
    void holdSeats(@RequestBody SeatsActionDto request);

    @PostMapping("/api/v1/admin/inventory/confirm")
    void confirmSeats(@RequestBody SeatsActionDto request);

    @PostMapping("/api/v1/admin/inventory/release")
    void releaseSeats(@RequestBody SeatsActionDto request);
}
