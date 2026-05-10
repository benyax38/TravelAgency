package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "purchases")
public class PurchaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    private BigDecimal totalAmount;

    private LocalDateTime purchaseDate;

    private String purchaseState;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ReservationEntity> reservations;

    @OneToOne(mappedBy = "purchase", cascade = CascadeType.ALL)
    @JsonManagedReference
    private PaymentEntity payment;
}
