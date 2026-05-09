package com.example.demo.Service;

import com.example.demo.Entity.ReservationDiscountEntity;
import com.example.demo.Repository.ReservationDiscountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationDiscountService {

    private final ReservationDiscountRepository reservationDiscountRepository;

    // Inyección por constructor (recomendado)
    public ReservationDiscountService(ReservationDiscountRepository reservationDiscountRepository) {
        this.reservationDiscountRepository = reservationDiscountRepository;
    }

    /**
     * Obtiene todos los descuentos aplicados a reservas.
     */
    public List<ReservationDiscountEntity> getAllReservationDiscounts() {

        return reservationDiscountRepository.findAll();
    }
}
