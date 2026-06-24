package com.example.demo.Repository;

import com.example.demo.Entity.TourPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TourPackageRepository extends JpaRepository<TourPackageEntity,Long> {

    List<TourPackageEntity> findByPackageState(
            TourPackageEntity.PackageState packageState
    );

    List<TourPackageEntity> findTop6ByPackageStateIn(
            List<TourPackageEntity.PackageState> states
    );

    @Query("""
        SELECT p
        FROM TourPackageEntity p
        WHERE p.packageState IN (
            com.example.demo.Entity.TourPackageEntity.PackageState.AVAILABLE,
            com.example.demo.Entity.TourPackageEntity.PackageState.NOT_AVAILABLE
        )
        AND p.endDate > :now
    
        AND (
            :destination IS NULL
            OR LOWER(p.destination)
            LIKE CONCAT('%', :destination, '%')
        )
    
        AND (
            :minPrice IS NULL
            OR p.prize >= :minPrice
        )
    
        AND (
            :maxPrice IS NULL
            OR p.prize <= :maxPrice
        )
    
        AND (
            :duration IS NULL
            OR p.duration = :duration
        )
    
        AND (
            :tripType IS NULL
            OR LOWER(p.tripType) = :tripType
        )
    """)
    List<TourPackageEntity> searchPackages(
            @Param("destination") String destination,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("duration") Integer duration,
            @Param("tripType") String tripType,
            @Param("now") LocalDateTime now
    );
}
