package org.example.inventory_service.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SeatsActionDto {
    private Integer flightId;

    private List<String> seatNumbers;

    private String holdBy;
}
