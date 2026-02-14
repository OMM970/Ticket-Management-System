package org.example.bookingservice.Dto.Feign;

import java.util.List;

public record SeatsActionDto(Integer flightId,

        List<String>seatNumbers,

        String holdBy) {
}
