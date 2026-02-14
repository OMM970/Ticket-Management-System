package org.example.inventory_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDto {
    private Long id;
    private String flightNumber;

    private String source;
    private String destination;

    private LocalDate flightDate;

    private int economySeats;
    private int economyAvailable;
    private int economyBooked;


    private int businessSeats;
    private int businessAvailable;
    private int businessBooked;

    private BigDecimal economyPrice;

    private BigDecimal businessPrice;
    private Date createdAt;
    private Date updatedAt;



}
