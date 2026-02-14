package org.example.inventory_service.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "inventory_db_FMS")
public class InventoryEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "flight_number")
    private String flightNumber;

    @Column(name = "source")
    private String source;

    @Column(name = "destination")
    private String destination;

    @Column(name = "flight_Date")
    @Temporal(TemporalType.DATE)
    private LocalDate flightDate;

    @Column(name = "econnomy_seats")
    private Integer economySeats;

    @Column(name = "business_seats")
    private Integer businessSeats;

    @Column(name = "economy_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal economyPrice;

    @Column(name = "business_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal businessPrice;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date updatedAt;

    @Column(name = "economy_available_seats")
    private Integer economyAvailable;

    @Column(name = "econmy_held_seats")
    private Integer economyHeld;

    @Column(name = "econmy_booked_seats")
    private Integer economyBooked;

    @Column(name = "business_available_seats")
    private Integer businessAvailable;

    @Column(name = "business_held_seats")
    private Integer businessHeld;

    @Column(name = "business_booked_seats")
    private Integer businessBooked;


}