package com.example.demo.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ReservationRequestDTO {

    @NotEmpty(message = "Debe seleccionar al menos un paquete")
    private List<@NotNull Long> packageIds;

    @NotNull(message = "El número de pasajeros es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 pasajero")
    private Integer passengersNum;

    @Size(max = 256, message = "El detalle de acompañantes no puede superar los 256 caracteres")
    private String companionsDetails;

    @Size(max = 256, message = "Las solicitudes especiales no pueden superar los 256 caracteres")
    private String specialRequests;

    @Size(max = 256, message = "Las preferencias del cliente no pueden superar los 256 caracteres")
    private String customerPreferences;
}
