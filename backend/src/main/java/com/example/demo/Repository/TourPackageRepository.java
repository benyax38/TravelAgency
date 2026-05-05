package com.example.demo.Repository;

import com.example.demo.Entity.TourPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourPackageRepository extends JpaRepository<TourPackageEntity,Long> {
}
