package org.example.inventory_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomResponseDto {
    private String flightNumber;

    private String source;
    private String destination;

    private LocalDate flightDate;
    private BigDecimal economyPrice;

    private BigDecimal businessPrice;
}
