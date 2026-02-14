package org.example.inventory_service.Repository;

import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.example.inventory_service.Entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatsRepo extends JpaRepository<SeatEntity,Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s FROM SeatEntity s
        WHERE s.flight.id = :flightId
        AND s.seatNumber IN (:seatNumbers)
    """)
    List<SeatEntity> findByFlightIdAndSeatNumberIn(
            @Param("flightId") Integer flightId,
            @Param("seatNumbers") List<String> seatNumbers
    );
}
