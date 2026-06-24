package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "reservation_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationPackageId;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    @JsonBackReference("reservation-reference")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ReservationEntity reservation;

    @ManyToOne
    @JoinColumn(name = "package_id")
    @JsonBackReference("package-reference")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TourPackageEntity tourPackage;
}
