package com.example.demo.Controller;


import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Service.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    // Inyección por constructor
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Endpoint para probar el backend
    @GetMapping
    public List<ReservationEntity> getAllReservationsController() {
        return reservationService.getAllReservations();
    }
}
