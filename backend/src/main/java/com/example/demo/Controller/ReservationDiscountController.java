package com.example.demo.Controller;

import com.example.demo.Entity.ReservationDiscountEntity;
import com.example.demo.Service.ReservationDiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservation-discounts")
@CrossOrigin("*")
public class ReservationDiscountController {

    private final ReservationDiscountService reservationDiscountService;

    // Inyección por constructor
    public ReservationDiscountController(ReservationDiscountService reservationDiscountService) {
        this.reservationDiscountService = reservationDiscountService;
    }

    /**
     * Obtiene todos los descuentos asociados a reservas.
     */
    @GetMapping
    public ResponseEntity<List<ReservationDiscountEntity>> getAll() {

        List<ReservationDiscountEntity> reservationDiscounts =
                reservationDiscountService.getAllReservationDiscounts();

        return ResponseEntity.ok(reservationDiscounts);
    }
}
