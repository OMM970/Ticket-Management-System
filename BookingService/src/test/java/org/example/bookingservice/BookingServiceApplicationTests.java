package org.example.bookingservice;

import org.example.bookingservice.Dto.RequestDto;
import org.example.bookingservice.Enums.SeatsType;
import org.example.bookingservice.Repository.BookingRepository;
import org.example.bookingservice.Service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingServiceApplicationTests {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void cleanDatabase() {
        bookingRepository.deleteAll();
    }

    // =====================================================
    // 1️⃣ SAME SEAT → Only One Should Succeed
    // =====================================================
    @Test
    void testSameSeatTwoUsers() throws InterruptedException {

        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        Runnable task = () -> {
            try {
                ready.countDown();
                start.await();

                RequestDto request = RequestDto.builder()
                        .userId("USR" + Thread.currentThread().getId())
                        .userEmail("test@example.com")
                        .bookingDate(new Date())
                        .seats(1)
                        .seatsType(SeatsType.ECONOMY)
                        .flightId(2633L)
                        .seatNumbers(List.of("B14"))  // same seat
                        .build();

                bookingService.createBooking(request);

            } catch (Exception ignored) {
            } finally {
                done.countDown();
            }
        };

        executor.submit(task);
        executor.submit(task);

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();

        long bookingCount = bookingRepository.count();

        System.out.println("Same seat bookings: " + bookingCount);

        assertEquals(1, bookingCount);
    }

    // =====================================================
    // 2️⃣ DIFFERENT SEATS → Both Should Succeed
    // =====================================================
    @Test
    void testDifferentSeatsTwoUsers() throws InterruptedException {

        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        Runnable task1 = () -> {
            try {
                ready.countDown();
                start.await();

                RequestDto request = RequestDto.builder()
                        .userId("USR1")
                        .userEmail("test@example.com")
                        .bookingDate(new Date())
                        .seats(1)
                        .seatsType(SeatsType.ECONOMY)
                        .flightId(2633L)
                        .seatNumbers(List.of("B12"))  // different seat
                        .build();

                bookingService.createBooking(request);

            } catch (Exception ignored) {
            } finally {
                done.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                ready.countDown();
                start.await();

                RequestDto request = RequestDto.builder()
                        .userId("USR2")
                        .userEmail("test@example.com")
                        .bookingDate(new Date())
                        .seats(1)
                        .seatsType(SeatsType.ECONOMY)
                        .flightId(2633L)
                        .seatNumbers(List.of("B13"))  // different seat
                        .build();

                bookingService.createBooking(request);

            } catch (Exception ignored) {
            } finally {
                done.countDown();
            }
        };

        executor.submit(task1);
        executor.submit(task2);

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();

        long bookingCount = bookingRepository.count();

        System.out.println("Different seat bookings: " + bookingCount);

        assertEquals(2, bookingCount);
    }
}
