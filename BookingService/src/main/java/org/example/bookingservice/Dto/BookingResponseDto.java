    package org.example.bookingservice.Dto;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.example.bookingservice.Enums.BookingStatus;
    import org.example.bookingservice.Enums.SeatsType;

    import java.math.BigDecimal;
    import java.util.Date;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class BookingResponseDto {
        private String userId;
        private String userEmail;
        private Date bookingDate;
        private Integer seats;
        private SeatsType seatsType;
        private BookingStatus status;
        private String idempotencyKey;
        private BigDecimal totalAmount;
        private String paymentToken;
    }
