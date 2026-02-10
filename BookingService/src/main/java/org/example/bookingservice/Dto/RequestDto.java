package org.example.bookingservice.Dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bookingservice.Enums.SeatsType;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDto {

    @NotBlank(message = "User ID cannot be empty")
    private String userId;

    @NotBlank(message = "User email cannot be empty")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotNull(message = "Booking date is required")
    private Date bookingDate;

    @NotNull(message = "Seats count is required")
    @Min(value = 1, message = "At least 1 seat must be booked")
    private Integer seats;

    @NotNull(message = "Seat type is required")
    private SeatsType seatsType;

    @NotNull(message = "flight-id is required")
    private Long flightId;
}
