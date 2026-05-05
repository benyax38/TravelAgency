package com.example.demo.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePackageDTO {

    @NotBlank(message = "El nombre del paquete es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String packageName;

    @NotBlank(message = "El destino es obligatorio")
    @Size(max = 80, message = "El destino no puede superar los 80 caracteres")
    private String destination;

    @Size(max = 256, message = "La descripción no puede superar los 256 caracteres")
    private String description;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime startDate;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser de al menos 1 día")
    private Integer duration;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio mínimo es 0.01")
    private BigDecimal prize;

    @Size(max = 100, message = "Los servicios incluidos no pueden superar los 100 caracteres")
    private String includedServices;

    @Size(max = 256, message = "Las condiciones no pueden superar los 256 caracteres")
    private String conditions;

    @Size(max = 256, message = "Las restricciones no pueden superar los 256 caracteres")
    private String restriction;

    @NotNull(message = "Los cupos totales son obligatorios")
    @Min(value = 1, message = "Debe haber al menos 1 cupo")
    private Integer totalSlots;

    @Size(max = 40, message = "El tipo de viaje no puede superar los 40 caracteres")
    private String tripType;

    @Size(max = 40, message = "La temporada no puede superar los 40 caracteres")
    private String season;

    @Size(max = 40, message = "La categoría no puede superar los 40 caracteres")
    private String category;

}
