package com.example.demo.Service;

import com.example.demo.Entity.DiscountEntity;
import com.example.demo.Repository.DiscountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    // Inyección por constructor (recomendado)
    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    // Metodo simple para probar el backend
    public List<DiscountEntity> getAllDiscounts() {
        return discountRepository.findAll();
    }
}
