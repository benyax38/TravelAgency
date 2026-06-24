package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "discount_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigAdminDiscountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_config_id")
    private Long discountConfigId;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    public enum DiscountType {
        GROUP_DISCOUNT,
        FREQUENT_CUSTOMER,
        MULTI_PACKAGE
    }

    @Min(0)
    @Column(name = "percentage", nullable = false)
    private Integer percentage;

    // GROUP_DISCOUNT
    @Min(1)
    @Column(name = "min_passengers")
    private Integer minPassengers;

    // FREQUENT_CUSTOMER
    @Min(1)
    @Column(name = "min_reservations")
    private Integer minReservations;

    // MULTI_PACKAGE
    @Min(1)
    @Column(name = "period_days")
    private Integer periodDays;

    @Column(name = "promotion_start_date")
    private LocalDateTime promotionStartDate;

    @Column(name = "promotion_end_date")
    private LocalDateTime promotionEndDate;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
