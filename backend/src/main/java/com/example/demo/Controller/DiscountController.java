package com.example.demo.Controller;

import com.example.demo.Entity.DiscountEntity;
import com.example.demo.Service.DiscountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {

    private final DiscountService discountService;

    // Inyección por constructor
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    // Endpoint para probar el backend
    @GetMapping
    public List<DiscountEntity> getAllDiscountsController() {
        return discountService.getAllDiscounts();
    }
}
