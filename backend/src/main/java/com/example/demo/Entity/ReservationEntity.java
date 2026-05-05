package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "passengers_num")
    private Integer passengersNum;

    @Column(name = "companions_details", length = 256)
    private String companionsDetails;

    @Column(name = "special_requests", length = 256)
    private String specialRequests;

    @Column(name = "customer_preferences", length = 256)
    private String customerPreferences;

    @Column(name = "reservation_state", length = 40)
    private String reservationState;

    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    @Column(name = "payment_deadline")
    private LocalDateTime paymentDeadline;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Relación con User (muchas reservas pueden pertenecer a un usuario)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // Relación con TourPackage (muchas reservas pueden ser del mismo paquete)
    @ManyToOne
    @JoinColumn(name = "package_id")
    @JsonBackReference
    private TourPackageEntity tourPackage;
}
