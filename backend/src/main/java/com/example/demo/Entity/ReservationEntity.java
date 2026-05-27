package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_state", length = 40)
    private ReservationState reservationState;

    public enum ReservationState {
        PENDING,     // recién creada, esperando pago
        PAID,   // pagada
        ANULLED,   // cancelada por usuario o sistema
        EXPIRED      // no se pagó a tiempo
    }

    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    @Column(name = "payment_deadline")
    private LocalDateTime paymentDeadline;

    @Column(name = "subtotal_amount", precision = 10, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Relación con User (muchas reservas pueden pertenecer a un usuario)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // Relación intermedia con paquetes turísticos
    @OneToMany(mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference("reservation-reference")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ReservationPackageEntity> reservationPackages = new ArrayList<>();

    // Relación con Purchase (muchas reservas se asocia a una única compra --> carrito de compras)
    @ManyToOne
    @JoinColumn(name = "purchase_id")
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PurchaseEntity purchase;
}
