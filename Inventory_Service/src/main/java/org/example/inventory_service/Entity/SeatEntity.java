package org.example.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.inventory_service.Enums.SeatStatus;
import org.example.inventory_service.Enums.SeatsType;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "seats_db",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"flight_id", "seatNumber"})
        }
)
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatsType seatType;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private String holdBy;

    private LocalDateTime holdUntil;


    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private InventoryEntity flight;
}
