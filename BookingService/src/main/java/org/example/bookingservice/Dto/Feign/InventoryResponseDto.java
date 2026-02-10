package org.example.bookingservice.Dto.Feign;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
public record InventoryResponseDto(Long id,
                                   String flightNumber,

                                   String source,
                                   String destination,

                                   LocalDate flightDate,

                                   int economySeats,
                                   int businessSeats,

                                   BigDecimal economyPrice,

                                   BigDecimal businessPrice,
                                   Date createdAt,
                                   Date updatedAt) {
}
