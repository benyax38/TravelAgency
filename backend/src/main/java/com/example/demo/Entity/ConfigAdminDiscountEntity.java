package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "discount_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigAdminDiscountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_config_id")
    private Long discountConfigId;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    public enum DiscountType {
        GROUP_DISCOUNT,
        FREQUENT_CUSTOMER,
        MULTI_PACKAGE
    }

    @Column(name = "percentage", nullable = false)
    private Integer percentage;

    // GROUP_DISCOUNT
    @Column(name = "min_passengers")
    private Integer minPassengers;

    // FREQUENT_CUSTOMER
    @Column(name = "min_reservations")
    private Integer minReservations;

    // MULTI_PACKAGE
    @Column(name = "period_days")
    private Integer periodDays;

    @Column(name = "promotion_start_date")
    private LocalDateTime promotionStartDate;

    @Column(name = "promotion_end_date")
    private LocalDateTime promotionEndDate;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
