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
        SEASONAL
    }

    @Column(name = "percentage", nullable = false)
    private Integer percentage;

    // Umbral mínimo de pasajeros
    @Column(name = "min_passengers", nullable = false)
    private Integer minPassengers;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
