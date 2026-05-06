package com.example.demo.Controller;


import com.example.demo.DTOs.ReservationRequestDTO;
import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    // Inyección por constructor
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequestDTO reservationRequest) {

        try {
            ReservationEntity reservation = reservationService.createReservation(reservationRequest);
            return ResponseEntity.ok(reservation);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // READ
    @GetMapping
    public List<ReservationEntity> getAllReservationsController() {
        return reservationService.getAllReservations();
    }
}
