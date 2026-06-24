package com.example.demo.Controller;

import com.example.demo.Entity.ConfigAdminDiscountEntity;
import com.example.demo.Service.ConfigAdminDiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discount-configs")
@CrossOrigin("*")
public class ConfigAdminDiscountController {

    private final ConfigAdminDiscountService discountService;

    // Inyección por constructor
    public ConfigAdminDiscountController(ConfigAdminDiscountService configAdminDiscountService) {
        this.discountService = configAdminDiscountService;
    }

    /**
     * Obtiene todas las configuraciones.
     */
    @GetMapping
    public ResponseEntity<List<ConfigAdminDiscountEntity>> getAll() {

        List<ConfigAdminDiscountEntity> discounts =
                discountService.getAllDiscountConfigs();

        return ResponseEntity.ok(discounts);
    }

    /**
     * Obtiene una configuración por id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConfigAdminDiscountEntity> getById(
            @PathVariable Long id) {

        ConfigAdminDiscountEntity discount =
                discountService.getDiscountConfigById(id);

        if (discount == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(discount);
    }

    /**
     * Crea una nueva configuración.
     */
    @PostMapping
    public ResponseEntity<ConfigAdminDiscountEntity> create(
            @RequestBody ConfigAdminDiscountEntity discountConfig) {

        ConfigAdminDiscountEntity created =
                discountService.createDiscountConfig(discountConfig);

        return ResponseEntity.ok(created);
    }

    /**
     * Modifica una configuración.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ConfigAdminDiscountEntity> updateDiscount(
            @PathVariable Long id,
            @RequestBody
            ConfigAdminDiscountEntity discount
    ){
        return ResponseEntity.ok(
                discountService.updateDiscount(
                        id,
                        discount
                )
        );
    }

    /**
     * Elimina una configuración.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        discountService.deleteDiscountConfig(id);

        return ResponseEntity.noContent().build();
    }
}
