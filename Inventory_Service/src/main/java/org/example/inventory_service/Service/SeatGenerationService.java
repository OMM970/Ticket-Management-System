package org.example.inventory_service.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inventory_service.Entity.InventoryEntity;
import org.example.inventory_service.Entity.SeatEntity;
import org.example.inventory_service.Enums.SeatStatus;
import org.example.inventory_service.Enums.SeatsType;
import org.example.inventory_service.Repository.SeatsRepo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatGenerationService {

    private final SeatsRepo seatsRepo;
    @Async("seatExecutor")
    @Transactional
    public void generateSeatsForFlight(InventoryEntity flight) {

        List<SeatEntity> seats = new ArrayList<>();

        //  Economy Seats
        for (int i = 1; i <= flight.getEconomySeats(); i++) {

            seats.add(
                    SeatEntity.builder()
                            .seatNumber("E" + i)
                            .seatType(SeatsType.ECONOMY)
                            .status(SeatStatus.AVAILABLE)
                            .holdBy(null)
                            .holdUntil(null)
                            .flight(flight)
                            .build()
            );
        }

        //  Business Seats
        for (int i = 1; i <= flight.getBusinessSeats(); i++) {

            seats.add(
                    SeatEntity.builder()
                            .seatNumber("B" + i)
                            .seatType(SeatsType.BUSINESS)
                            .status(SeatStatus.AVAILABLE)
                            .holdBy(null)
                            .holdUntil(null)
                            .flight(flight)
                            .build()
            );
        }

        seatsRepo.saveAll(seats);
    }
}
