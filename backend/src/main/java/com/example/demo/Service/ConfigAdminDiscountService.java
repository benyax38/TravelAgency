package com.example.demo.Service;

import com.example.demo.Entity.ConfigAdminDiscountEntity;
import com.example.demo.Repository.ConfigAdminDiscountRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return configAdminDiscountRepository.findAll(Sort.by("discountConfigId"));
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
     * Modifica una configuración de descuento.
     */
    public ConfigAdminDiscountEntity updateDiscount(
            Long id,
            ConfigAdminDiscountEntity newData
    ){

        validateDates(newData);

        if(newData.getPromotionStartDate() != null && newData.getPromotionEndDate() != null){
            if(newData.getPromotionStartDate().isAfter(newData.getPromotionEndDate())){
                throw new RuntimeException(
                        "La fecha de inicio no puede ser posterior a la fecha de término"
                );
            }
        }

        ConfigAdminDiscountEntity discount =
                configAdminDiscountRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Descuento no encontrado"
                                )
                        );

        if(newData.getPercentage() != null){
            discount.setPercentage(
                    newData.getPercentage()
            );
        }

        if(newData.getPromotionStartDate() != null){
            discount.setPromotionStartDate(
                    newData.getPromotionStartDate()
            );
        }

        if(newData.getPromotionEndDate() != null){
            discount.setPromotionEndDate(
                    newData.getPromotionEndDate()
            );
        }

        switch(discount.getDiscountType()){

            case GROUP_DISCOUNT:

                if(newData.getMinPassengers() != null){
                    discount.setMinPassengers(
                            newData.getMinPassengers()
                    );
                }

                discount.setMinReservations(null);
                discount.setPeriodDays(null);

                break;


            case FREQUENT_CUSTOMER:

                if(newData.getMinReservations() != null){
                    discount.setMinReservations(
                            newData.getMinReservations()
                    );
                }

                discount.setMinPassengers(null);
                discount.setPeriodDays(null);

                break;


            case MULTI_PACKAGE:

                if(newData.getPeriodDays() != null){
                    discount.setPeriodDays(
                            newData.getPeriodDays()
                    );
                }

                discount.setMinPassengers(null);
                discount.setMinReservations(null);

                break;

        }

        if(newData.getActive() != null){
            discount.setActive(
                    newData.getActive()
            );
        }
        return configAdminDiscountRepository.save(discount);
    }

    /**
     * Metodo auxiliar para fechas
     */
    private void validateDates(ConfigAdminDiscountEntity newData){

        LocalDateTime today = LocalDateTime.now();

        if(newData.getPromotionStartDate() != null && newData.getPromotionStartDate().isBefore(today)){
            throw new RuntimeException(
                    "La fecha de inicio no puede ser anterior a hoy"
            );
        }

        if(newData.getPromotionEndDate() != null && newData.getPromotionEndDate().isBefore(today)
        ){
            throw new RuntimeException(
                    "La fecha de término no puede ser anterior a hoy"
            );
        }

    }

    /**
     * Elimina una configuración de descuento.
     */
    public void deleteDiscountConfig(Long id) {
        configAdminDiscountRepository.deleteById(id);
    }
}
