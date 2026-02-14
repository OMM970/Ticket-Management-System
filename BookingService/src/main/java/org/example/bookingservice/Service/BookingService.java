package org.example.bookingservice.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.bookingservice.Dto.BookingResponseDto;
import org.example.bookingservice.Dto.Feign.InventoryResponseDto;
import org.example.bookingservice.Dto.Feign.SeatsActionDto;
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

    // 🔥 LockService removed



    public BookingResponseDto createBooking(RequestDto requestDto) {

        String idempotencyKey = UUID.randomUUID().toString();

        // 1️⃣ Fetch latest inventory
        InventoryResponseDto inventory =
                inventoryClient.getInventoryById(requestDto.getFlightId());

        // 2️⃣ Calculate total amount
        BigDecimal totalAmount = calculateTotalAmount(
                inventory,
                requestDto.getSeatsType().name(),
                requestDto.getSeats()
        );

        // 3️⃣ Call Inventory to HOLD seats
        SeatsActionDto holdSeatRequest =
                new SeatsActionDto(
                        Math.toIntExact(requestDto.getFlightId()),
                        requestDto.getSeatNumbers(),
                        idempotencyKey   // 🔥 use idempotencyKey as holdBy
                );

        inventoryClient.holdSeats(holdSeatRequest);

        // 4️⃣ Create booking (CREATED)
        BookingEntity booking = BookingEntity.builder()
                .flightId(Math.toIntExact(requestDto.getFlightId()))
                .idempotencyKey(idempotencyKey)
                .userId(requestDto.getUserId())
                .userEmail(requestDto.getUserEmail())
                .bookingDate(requestDto.getBookingDate())
                .seats(requestDto.getSeats())
                .seatType(requestDto.getSeatsType())
                .seatNumbers(requestDto.getSeatNumbers())
                .amount(totalAmount)
                .status(BookingStatus.CREATED)
                .build();

        // 5️⃣ SAVE to Mongo
        bookingRepository.save(booking);

        // 6️⃣ Generate payment token
        String paymentToken = paymentUtil.generateToken(
                idempotencyKey,
                booking.getUserId(),
                totalAmount,
                booking.getBookingId()
        );

        return mapToDto(booking, paymentToken);
    }



    public BookingResponseDto confirmBooking(String idempotencyKey) {

        BookingEntity booking = bookingRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() ->
                        new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return mapToDto(booking, null);
        }

        // 🔥 Call inventory FIRST
        SeatsActionDto confirmRequest =
                new SeatsActionDto(
                        Math.toIntExact(booking.getFlightId()),
                        booking.getSeatNumbers(),
                        booking.getIdempotencyKey()
                );

        inventoryClient.confirmSeats(confirmRequest);

        // 🔥 Only after inventory success → update booking
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return mapToDto(booking, null);
    }


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
