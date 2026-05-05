package com.example.demo.Service;

import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    // Inyección por constructor (recomendado)
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Metodo simple para probar el backend
    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.findAll();
    }
}
