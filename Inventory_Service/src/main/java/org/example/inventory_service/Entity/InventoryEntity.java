package org.example.inventory_service.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private LocalDate flightDate;

    @Column(name = "econnomy_seats")
    private int economySeats;

    @Column(name = "business_seats")
    private int businessSeats;

    @Column(name = "economy_price")
    private double economyPrice;

    @Column(name = "business_price")
    private double businessPrice;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date updatedAt;



}