package com.example.demo.Service;

import com.example.demo.DTOs.ReservationRequestDTO;
import com.example.demo.Entity.ConfigAdminDiscountEntity;
import com.example.demo.Entity.ReservationDiscountEntity;
import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Repository.ConfigAdminDiscountRepository;
import com.example.demo.Repository.ReservationDiscountRepository;
import com.example.demo.Repository.ReservationPackageRepository;
import com.example.demo.Repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DiscountApplicationService {

    private final ReservationRepository reservationRepository;
    private final ConfigAdminDiscountRepository discountConfigRepository;
    private final ReservationDiscountRepository reservationDiscountRepository;
    private final ReservationPackageRepository reservationPackageRepository;

    /*
        Constructor
        - Descripción: Inyección por constructor. Permite utilizar otras entidades
        - Entradas: ReservationRepository, ConfigAdminDiscountRepository, ReservationDiscountRepository
        - Salidas: void
     */
    public DiscountApplicationService(
            ReservationRepository reservationRepository,
            ConfigAdminDiscountRepository discountConfigRepository,
            ReservationDiscountRepository reservationDiscountRepository,
            ReservationPackageRepository reservationPackageRepository) {

        this.reservationRepository = reservationRepository;
        this.discountConfigRepository = discountConfigRepository;
        this.reservationDiscountRepository = reservationDiscountRepository;
        this.reservationPackageRepository = reservationPackageRepository;
    }

    /*
        Orquestador de descuentos
        - Descripción: Inicia con el subtotal (variable currentAmount) obtenido de (num_pasajeros * precio_paquete) y
        luego se llama a las funciones que aplican cada descuento y se va actualizando currentAmount
        - Entradas: reservation (ReservationEntity), subtotal (BigDecimal), reservationDTO (ReservationRequestDTO)
        - Salidas: currentAmount (BigDecimal)
     */
    public BigDecimal applyDiscounts(
            ReservationEntity reservation,
            BigDecimal subtotal,
            ReservationRequestDTO reservationDTO) {

        // Variable para guardar el monto con descuentos
        BigDecimal currentAmount = subtotal;

        // --- Calcular limite minimo permitido ---

        // Limite maximo de descuento permitido
        Integer maxDiscountPercentage = 20;

        // Monto de descuento maximo
        BigDecimal maxDiscountAmount =
                calculatePercentageDiscount(
                        subtotal,
                        maxDiscountPercentage
                );

        // Monto minimo tras aplicar descuentos
        BigDecimal minimumAllowedAmount =
                subtotal.subtract(maxDiscountAmount);

        // --- Descuentos por grupo ---

        // Descuento por cantidad de personas
        currentAmount = applyGroupDiscount(
                reservation,
                currentAmount,
                reservationDTO,
                minimumAllowedAmount
        );

        // Descuento por cliente frecuente
        currentAmount = applyFrequentCustomerDiscount(
                reservation,
                currentAmount,
                minimumAllowedAmount
        );

        // Descuento por múltiples paquetes
        currentAmount = applyMultiplePackagesDiscount(
                reservation,
                currentAmount,
                minimumAllowedAmount
        );

        return currentAmount;
    }

    /*
        Aplica descuento por cantidad de pasajeros
        - Descripción:
            1. Busca que el descuento de tipo "GROUP_DISCOUNT" se encuentre activo
            2. Comprueba que la cantidad de pasajeros sea mayor o igual al umbral definido en la configuración
            3. Aplica descuento (a partir del atributo percentage de la configuración) retornando el currentAmount
            con el descuento aplicado
            4. Se crea la entidad intermedia reserva-descuento
        - Entradas: reservation (ReservationEntity), currentAmount (BigDecimal), reservationDTO (ReservationRequestDTO)
        - Salidas: currentAmount (BigDecimal)
     */
    public BigDecimal applyGroupDiscount(
            ReservationEntity reservation,
            BigDecimal currentAmount,
            ReservationRequestDTO reservationDTO,
            BigDecimal minimumAllowedAmount) {

        // Comprueba si es que el descuento por cantidad de pasajeros se encuentra activo
        Optional<ConfigAdminDiscountEntity> optionalDiscount =
                discountConfigRepository
                        .findByDiscountTypeAndActiveTrue(
                                ConfigAdminDiscountEntity
                                        .DiscountType
                                        .GROUP_DISCOUNT);

        // Si no existe el tipo de descuento o no se encuentra activo, se retorna el currentAmount sin cambios
        if(optionalDiscount.isEmpty()) {
            return currentAmount;
        }

        // Se extrae el objeto para acceder a los atributos del ConfigAdminDiscountEntity
        ConfigAdminDiscountEntity discount =
                optionalDiscount.get();

        // Validar vigencia de promoción
        if (!isPromotionActive(
                discount.getPromotionStartDate(),
                discount.getPromotionEndDate()
        )) {
            return currentAmount;
        }

        // Se compara la cantidad de pasajeros ingresados (DTO de entrada) con el umbral definido en la configuración
        // Ej: Si el num_pasajeros es 2 y el umbral es 3, no aplica descuento y devuelve el currentAmount
        if(reservationDTO.getPassengersNum()
                < discount.getMinPassengers()) {

            return currentAmount;
        }

        // Obtiene el monto de descuento (discountAmount = currentAmount * percentage/100)
        BigDecimal discountAmount =
                calculatePercentageDiscount(
                        currentAmount,
                        discount.getPercentage()
                );

        // Calcular nuevo monto potencial
        BigDecimal newAmount =
                currentAmount.subtract(discountAmount);

        // Verificar si rompe límite
        if (newAmount.compareTo(minimumAllowedAmount) < 0) {
            return currentAmount;
        }

        // Crea una entidad intermedia entre reserva y descuento
        saveAppliedDiscount(
                reservation,
                discount,
                discountAmount
        );

        return newAmount;
    }

    /*
        Aplica descuento por cliente frecuente
        - Descripción:
            1. Consulta la cantidad de reservas pagadas que tiene el usuario
            2. Comprueba que la cantidad de reservas pagadas sea mayor al definido (3 en este caso)
            3. Comprueba que el descuento exista y se encuentre activo
            4. Calcula el monto de descuento y lo aplica sobre el currentAmount
            5. Se crea la entidad intermedia reserva-descuento
        - Entradas: reservation (ReservationEntity), currentAmount (BigDecimal)
        - Salidas: currentAmount (BigDecimal)
     */
    public BigDecimal applyFrequentCustomerDiscount(
            ReservationEntity reservation,
            BigDecimal currentAmount,
            BigDecimal minimumAllowedAmount) {

        // Busca en la configuración si el descuento por cliente frecuente se encuentra activo
        Optional<ConfigAdminDiscountEntity> optionalDiscount =
                discountConfigRepository
                        .findByDiscountTypeAndActiveTrue(
                                ConfigAdminDiscountEntity
                                        .DiscountType
                                        .FREQUENT_CUSTOMER);

        // Si el descuento no está activo o no se encuentra, se devuelve el mismo currentAmount de la entrada
        if(optionalDiscount.isEmpty()) {
            return currentAmount;
        }

        // Obtiene el objeto de configuración
        ConfigAdminDiscountEntity discount =
                optionalDiscount.get();

        // Validar vigencia de promoción
        if (!isPromotionActive(
                discount.getPromotionStartDate(),
                discount.getPromotionEndDate()
        )) {
            return currentAmount;
        }

        // Consulta a la BD cuantas reservas pagadas tiene el usuario que está haciendo la reserva
        long paidReservations =
                reservationRepository
                        .countByUser_UserIdAndReservationState(
                                reservation.getUser().getUserId(),
                                ReservationEntity.ReservationState.PAID
                        );

        // Se define la cantidad de reservas históricas pagadas necesarias para aplicar el descuento
        if (paidReservations < discount.getMinReservations()) {
            return currentAmount;
        }

        // Obtiene el monto de descuento (discountAmount = currentAmount * percentage/100)
        BigDecimal discountAmount =
                calculatePercentageDiscount(
                        currentAmount,
                        discount.getPercentage()
                );

        // Calcular nuevo monto potencial
        BigDecimal newAmount =
                currentAmount.subtract(discountAmount);

        // Verificar si rompe límite
        if (newAmount.compareTo(minimumAllowedAmount) < 0) {
            return currentAmount;
        }

        // Crea una entidad intermedia entre reserva y descuento
        saveAppliedDiscount(
                reservation,
                discount,
                discountAmount
        );

        return newAmount;
    }

    /*
        Descuento por compra de múltiples paquetes
        - Descripción: Aplica descuento si se cumple una o ambas condiciones:
          1. La reserva actual contiene múltiples paquetes
          2. El cliente ha reservado múltiples paquetes dentro de un período definido
        - Entradas: reservation (ReservationEntity) + currentAmount (BigDecimal)
        - Salidas: currentAmount con descuento aplicado
     */
    public BigDecimal applyMultiplePackagesDiscount(
            ReservationEntity reservation,
            BigDecimal currentAmount,
            BigDecimal minimumAllowedAmount
    ) {
        // Verifica si el tipo de descuento existe y si se encuentra activo
        ConfigAdminDiscountEntity config =
                discountConfigRepository
                        .findByDiscountTypeAndActiveTrue(
                                ConfigAdminDiscountEntity.DiscountType.MULTI_PACKAGE
                        )
                        .orElse(null);

        // Si el tipo de descuento no existe, no aplica descuento
        if (config == null) {
            return currentAmount;
        }

        // Validar vigencia de promoción
        if (!isPromotionActive(
                config.getPromotionStartDate(),
                config.getPromotionEndDate()
        )) {
            return currentAmount;
        }

        // Obtiene la cantidad de paquetes que tiene la reserva
        long currentPackages =
                reservation.getReservationPackages().size();

        // Comprueba si hay suficientes paquetes en la reserva actual para aplicar el descuento
        boolean qualifiesCurrentReservation =
                currentPackages >= 1;

        // La fecha inicial del periodo para aplicar descuento se obtiene restando periodDays a la fecha actual
        LocalDateTime startDate =
                LocalDateTime.now()
                        .minusDays(config.getPeriodDays());

        // Obtiene la cantidad de reservas con estado PAID, dentro del periodo definido (startDate hasta hoy)
        long historicalPackages =
                reservationPackageRepository
                        .countPackagesByUserAndDate(
                                reservation.getUser().getUserId(),
                                startDate
                        );

        // Comprueba si hay suficientes paquetes en el período definido para aplicar descuento
        boolean qualifiesByPeriod =
                historicalPackages >= 1;

        // Si alguna de las condiciones se cumple, se aplica descuento
        if (qualifiesCurrentReservation || qualifiesByPeriod) {

            // Calcular monto de descuento
            BigDecimal discountAmount =
                    calculatePercentageDiscount(
                            currentAmount,
                            config.getPercentage()
                    );

            // Calcular nuevo monto potencial
            BigDecimal newAmount =
                    currentAmount.subtract(discountAmount);

            // Verificar si rompe límite
            if (newAmount.compareTo(minimumAllowedAmount) < 0) {
                return currentAmount;
            }

            // Guardar descuento aplicado
            saveAppliedDiscount(
                    reservation,
                    config,
                    discountAmount
            );

            // Restar descuento al monto actual
            currentAmount = newAmount;
        }

        return currentAmount;
    }

    /*
        Calculo del monto de descuento
        - Descripción: Metodo que aplica un porcentaje sobre el monto ingresado
        - Entradas: amount (BigDecimal), percentage (Integer)
        - Salidas: discountAmount (BigDecimal)
     */
    private BigDecimal calculatePercentageDiscount(
            BigDecimal amount,
            Integer percentage) {

        return amount.multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP);
    }

    /*
        Guarda la entidad intermedia reserva-descuento
        - Descripción: Crea una nueva entidad que une reserva y descuento mediante Hibernate
        - Entradas: reservation (ReservationEntity), discount (ConfigAdminDiscountEntity), discountAmount (BigDecimal)
        - Salidas: void
     */
    private void saveAppliedDiscount(
            ReservationEntity reservation,
            ConfigAdminDiscountEntity discount,
            BigDecimal discountAmount) {

        // Crea la entidad (id automático)
        ReservationDiscountEntity reservationDiscount =
                new ReservationDiscountEntity();

        // Actualiza sus valores
        reservationDiscount.setReservation(reservation);
        reservationDiscount.setDiscountConfig(discount);
        reservationDiscount.setDiscountAmount(discountAmount);

        // Guardado en la BD
        reservationDiscountRepository.save(reservationDiscount);
    }

    /*
        isPromotionActive
        - Descripción: Consulta si un descuento es aplicable segun fecha
        - Entradas: fecha de inicio y fecha de termino del descuento
        - Salidas: true si la fecha actual esta dentro del rango de fechas definido en la configuracion
     */
    private boolean isPromotionActive(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        if (startDate == null || endDate == null) {
            return false;
        }

        LocalDateTime today = LocalDateTime.now();

        return today.isEqual(startDate) || today.isEqual(endDate)
                || (today.isAfter(startDate) && today.isBefore(endDate));
    }
}
