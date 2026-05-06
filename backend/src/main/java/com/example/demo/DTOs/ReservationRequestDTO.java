package com.example.demo.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReservationRequestDTO {
    @NotNull(message = "El userId es obligatorio")
    private Long userId;

    @NotNull(message = "El packageId es obligatorio")
    private Long packageId;

    @NotNull(message = "El número de pasajeros es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 pasajero")
    private Integer passengersNum;

    @Size(max = 256, message = "La descripción no puede superar los 256 caracteres")
    private String companionsDetails;

    @Size(max = 256, message = "La descripción no puede superar los 256 caracteres")
    private String specialRequests;

    @Size(max = 256, message = "La descripción no puede superar los 256 caracteres")
    private String customerPreferences;
}
