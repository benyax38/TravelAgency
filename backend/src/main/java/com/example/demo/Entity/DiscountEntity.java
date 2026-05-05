package com.example.demo.Entity;

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

import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long discountId;

    @Column(name = "discount_type", length = 40)
    private String discountType;

    @Column(name = "percentage")
    private Integer percentage;

    @Column(name = "start_date_dis")
    private LocalDateTime startDateDis;

    @Column(name = "end_date_dis")
    private LocalDateTime endDateDis;

    @Column(name = "discount_limit")
    private Integer discountLimit;

    // Relación con Reservation
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private ReservationEntity reservation;
}
