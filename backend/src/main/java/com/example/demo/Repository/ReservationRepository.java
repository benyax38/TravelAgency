package com.example.demo.Repository;

import com.example.demo.Entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity,Long> {

    // Cuenta la cantidad de reservas asociadas a un usuario que tienen el estado ingresado en argumentos
    long countByUser_UserIdAndReservationState(
            Long userId,
            ReservationEntity.ReservationState reservationState);
}
