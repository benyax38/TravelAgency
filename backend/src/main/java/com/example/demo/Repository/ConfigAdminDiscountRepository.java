package com.example.demo.Repository;

import com.example.demo.Entity.ConfigAdminDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigAdminDiscountRepository extends JpaRepository<ConfigAdminDiscountEntity, Long> {

    Optional<ConfigAdminDiscountEntity>
    findByDiscountTypeAndActiveTrue(ConfigAdminDiscountEntity.DiscountType discountType);
}
