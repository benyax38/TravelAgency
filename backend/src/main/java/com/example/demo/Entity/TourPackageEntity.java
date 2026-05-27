package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tour_packages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourPackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Long packageId;

    @Column(name = "package_name", length = 80)
    private String packageName;

    @Column(name = "destination", length = 80)
    private String destination;

    @Column(name = "description", length = 256)
    private String description;

    @NotNull(message = "La fecha de inicio no puede ser null")
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de termino no puede ser null") // nullable = false ¿?
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @NotNull(message = "La duración del paquete no puede ser null")
    @Min(value = 1, message = "El paquete debe tener duración de al menos 1 día")
    @Column(name = "duration")
    private Integer duration;

    @NotNull(message = "El precio no puede ser null")
    @DecimalMin(value = "0.01", message = "El precio mínimo es de 0.01") // Desde aqui se puede definir el precio minimo que puede tener un paquete
    @Column(name = "prize", precision = 10, scale = 2)
    private BigDecimal prize;

    @Column(name = "included_services", length = 100)
    private String includedServices;

    @Column(name = "conditions", length = 256)
    private String conditions;

    @Column(name = "restriction", length = 256)
    private String restriction;

    @NotNull(message = "Los cupos disponibles no pueden ser null")
    @Min(value = 1, message = "El paquete debe tener al menos 1 cupo disponible")
    @Column(name = "available_slots")
    private Integer availableSlots;

    @NotNull(message = "Los cupos totales son obligatorios")
    @Min(value = 1, message = "El paquete debe tener al menos 1 cupo total")
    @Column(name = "total_slots")
    private Integer totalSlots;

    @Column(name = "trip_type", length = 40)
    private String tripType;

    @Column(name = "season", length = 40)
    private String season;

    @Column(name = "category", length = 40)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "package_state", length = 40)
    private PackageState packageState;

    public enum PackageState {
        AVAILABLE,
        SOLD_OUT,
        NOT_AVAILABLE,
        CANCELLED
    }

    @Builder.Default
    @OneToMany(mappedBy = "tourPackage")
    @JsonManagedReference("package-reference")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ReservationPackageEntity> reservationPackages = new ArrayList<>();

}
