package org.example.bookingservice.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.bookingservice.Dto.BookingResponseDto;
import org.example.bookingservice.Dto.Feign.HoldSeatRequest;
import org.example.bookingservice.Dto.Feign.InventoryResponseDto;
import org.example.bookingservice.Dto.RequestDto;
import org.example.bookingservice.Entity.BookingEntity;
import org.example.bookingservice.Enums.BookingStatus;
import org.example.bookingservice.Feign.InventoryService;
import org.example.bookingservice.Repository.BookingRepository;
import org.example.bookingservice.UtilService.PaymentUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final InventoryService inventoryClient;
    private final PaymentUtil paymentUtil;
    private final LockService lockService;




    public BookingResponseDto createBooking(RequestDto requestDto) {

        String idempotencyKey = UUID.randomUUID().toString();

        String lockKey = "LOCK:flight:" +
                requestDto.getFlightId() + ":" +
                requestDto.getSeatsType();

        boolean locked = lockService.acquireLock(
                lockKey,
                idempotencyKey,
                45
        );

        if (!locked) {
            throw new IllegalStateException(
                    "Seats are currently being booked, please try again"
            );
        }

        try {
            // 1️⃣ Fetch latest inventory
            InventoryResponseDto inventory =
                    inventoryClient.getInventoryById(requestDto.getFlightId());

            // 2️⃣ Calculate total amount
            BigDecimal totalAmount = calculateTotalAmount(
                    inventory,
                    requestDto.getSeatsType().name(),
                    requestDto.getSeats()
            );
            HoldSeatRequest holdSeatRequest =
                    new HoldSeatRequest(
                            requestDto.getFlightId().toString(),
                            requestDto.getSeatsType().name(), // enum → string
                            requestDto.getSeats(),
                            idempotencyKey
                    );


            inventoryClient.holdSeats(holdSeatRequest);

            // 4️⃣ Create booking (CREATED)
            BookingEntity booking = BookingEntity.builder()
                    .idempotencyKey(idempotencyKey)
                    .userId(requestDto.getUserId())
                    .userEmail(requestDto.getUserEmail())
                    .bookingDate(requestDto.getBookingDate())
                    .seats(requestDto.getSeats())
                    .seatType(requestDto.getSeatsType())
                    .amount(totalAmount)
                    .status(BookingStatus.CREATED)
                    .build();

            // 5️⃣ SAVE to Mongo (SOURCE OF TRUTH)
            bookingRepository.save(booking);

            // 6️⃣ Generate payment token
            String paymentToken = paymentUtil.generateToken(
                    idempotencyKey,
                    booking.getUserId(),
                    totalAmount
            );

            return mapToDto(booking, paymentToken);

        } finally {
            // 🔓 ALWAYS release lock
            lockService.releaseLock(lockKey, idempotencyKey);
        }
    }



    public BookingResponseDto confirmBooking(String idempotencyKey) {

        BookingEntity booking = bookingRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() ->
                        new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return mapToDto(booking, null);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return mapToDto(booking, null);
    }

    /* ---------------- HELPERS ---------------- */

    private BookingResponseDto mapToDto(
            BookingEntity booking,
            String paymentToken
    ) {
        return BookingResponseDto.builder()
                .idempotencyKey(booking.getIdempotencyKey())
                .userId(booking.getUserId())
                .userEmail(booking.getUserEmail())
                .bookingDate(booking.getBookingDate())
                .seats(booking.getSeats())
                .seatsType(booking.getSeatType())
                .status(booking.getStatus())
                .totalAmount(booking.getAmount())
                .paymentToken(paymentToken)
                .build();
    }

    private BigDecimal calculateTotalAmount(
            InventoryResponseDto inventory,
            String seatType,
            int seats
    ) {
        if (seats <= 0) {
            throw new IllegalArgumentException("Seats must be > 0");
        }

        return switch (seatType) {
            case "ECONOMY" -> {
                if (inventory.economySeats() < seats) {
                    throw new RuntimeException("Not enough economy seats");
                }
                yield inventory.economyPrice()
                        .multiply(BigDecimal.valueOf(seats));
            }
            case "BUSINESS" -> {
                if (inventory.businessSeats() < seats) {
                    throw new RuntimeException("Not enough business seats");
                }
                yield inventory.businessPrice()
                        .multiply(BigDecimal.valueOf(seats));
            }
            default -> throw new IllegalArgumentException("Invalid seat type");
        };
    }
}
