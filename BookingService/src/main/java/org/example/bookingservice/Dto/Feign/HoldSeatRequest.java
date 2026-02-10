package org.example.bookingservice.Dto.Feign;

public record HoldSeatRequest( String flightId,
        String seatType,
         int seats,
         String idempotencyKey) {
}
