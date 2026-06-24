package com.example.demo.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSummaryDTO {

    private Long reservationId;

    private String reservationState;

    private LocalDateTime reservationDate;

    private LocalDateTime paymentDeadline;

    private Integer passengersNum;

    private List<ReservedPackageDTO> packages;

    private BigDecimal subtotalAmount;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private List<AppliedDiscountDTO> appliedDiscounts;
}
