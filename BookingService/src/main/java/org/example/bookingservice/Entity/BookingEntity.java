package org.example.bookingservice.Entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.bookingservice.Enums.BookingStatus;
import org.example.bookingservice.Enums.SeatsType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bookings")
@CompoundIndex(
        name = "idem_key_unique",
        def = "{'idempotencyKey': 1}",
        unique = true
)
public class BookingEntity {

    @Id
    private String bookingId;

    private String idempotencyKey;

    private String userId;
    private String userEmail;

    private Integer seats;

    @Enumerated(EnumType.STRING)
    private SeatsType seatType;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Date bookingDate;

    private Integer flightId;

    private BigDecimal amount;

}
