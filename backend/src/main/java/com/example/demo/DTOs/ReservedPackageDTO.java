package com.example.demo.DTOs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReservedPackageDTO {
    private Long packageId;

    private String packageName;

    private String destination;

    private BigDecimal packagePrice;
}
