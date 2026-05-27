package com.example.demo.Controller;


import com.example.demo.DTOs.ReservationRequestDTO;
import com.example.demo.DTOs.ReservationSummaryDTO;
import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ReservationSummaryDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO reservationRequest) {

        ReservationSummaryDTO response =
                reservationService.createReservation(
                        reservationRequest
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // READ
    @GetMapping
    public List<ReservationEntity> getAllReservationsController() {
        return reservationService.getAllReservations();
    }
}
