package com.example.demo.Service;

import com.example.demo.Entity.ConfigAdminDiscountEntity;
import com.example.demo.Repository.ConfigAdminDiscountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigAdminDiscountService {

    private final ConfigAdminDiscountRepository configAdminDiscountRepository;

    // Inyección por constructor (recomendado)
    public ConfigAdminDiscountService(ConfigAdminDiscountRepository configAdminDiscountRepository) {
        this.configAdminDiscountRepository = configAdminDiscountRepository;
    }

    /**
     * Obtiene todas las configuraciones de descuentos.
     */
    public List<ConfigAdminDiscountEntity> getAllDiscountConfigs() {
        return configAdminDiscountRepository.findAll();
    }

    /**
     * Obtiene una configuración por id.
     */
    public ConfigAdminDiscountEntity getDiscountConfigById(Long id) {
        return configAdminDiscountRepository.findById(id).orElse(null);
    }

    /**
     * Crea una nueva configuración de descuento.
     */
    public ConfigAdminDiscountEntity createDiscountConfig(
            ConfigAdminDiscountEntity discountConfig) {

        return configAdminDiscountRepository.save(discountConfig);
    }

    /**
     * Elimina una configuración de descuento.
     */
    public void deleteDiscountConfig(Long id) {
        configAdminDiscountRepository.deleteById(id);
    }
}
