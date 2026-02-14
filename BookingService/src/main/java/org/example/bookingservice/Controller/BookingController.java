package org.example.bookingservice.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookingservice.Dto.RequestDto;
import org.example.bookingservice.Dto.BookingResponseDto;
import org.example.bookingservice.Service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid @RequestBody RequestDto requestDto
    ) {
        BookingResponseDto response = bookingService.createBooking(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/confirmBooking/{idempotencyKey}")
    public ResponseEntity<?> confirmBooking(
            @PathVariable String idempotencyKey
    ){
        BookingResponseDto response = bookingService.confirmBooking(idempotencyKey);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
