package org.example.inventory_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private int businessSeats;

    private double economyPrice;
    private double businessPrice;

    private Date createdAt;
    private Date updatedAt;



}
