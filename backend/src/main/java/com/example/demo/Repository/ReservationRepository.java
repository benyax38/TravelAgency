package com.example.demo.Repository;

import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity,Long> {

    // Cuenta la cantidad de reservas asociadas a un usuario que tienen el estado ingresado en argumentos
    long countByUser_UserIdAndReservationState(
            Long userId,
            ReservationEntity.ReservationState reservationState);

    List<ReservationEntity> findByUser(UserEntity user);
}
