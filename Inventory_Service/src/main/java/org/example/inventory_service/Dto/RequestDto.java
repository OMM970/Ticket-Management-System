package org.example.inventory_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDto {
    private String flightNumber;

    private String source;
    private String destination;

    private LocalDate flightDate;

    private int economySeats;
    private int businessSeats;

    private BigDecimal economyPrice;

    private BigDecimal businessPrice;
}
