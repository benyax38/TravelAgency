package com.example.demo.Repository;

import com.example.demo.Entity.ReservationDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationDiscountRepository extends JpaRepository<ReservationDiscountEntity, Long> {

    /**
     * Obtiene todos los descuentos asociados a una reserva.
     *
     * @param reservationId ID de la reserva
     * @return lista de descuentos aplicados
     */
    List<ReservationDiscountEntity> findByReservation_ReservationId(Long reservationId);

}
