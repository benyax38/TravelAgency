package com.example.demo.Repository;

import com.example.demo.Entity.ReservationPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationPackageRepository
        extends JpaRepository<ReservationPackageEntity, Long> {

    @Query("""
        SELECT COUNT(rd)
        FROM ReservationPackageEntity rd
        WHERE rd.reservation.user.userId = :userId
        AND rd.reservation.reservationDate >= :startDate
        AND rd.reservation.reservationState = com.example.demo.Entity.ReservationEntity.ReservationState.PAID
    """)
    long countPackagesByUserAndDate(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate
    );
}
