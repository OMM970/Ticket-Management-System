package org.example.inventory_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoldSeatRequestDto {
    private String flightId;
    private String seatType;      // "ECONOMY" / "BUSINESS"
    private int seats;
    private String idempotencyKey;
}
