package com.example.demo.Service;

import com.example.demo.DTOs.ReservationRequestDTO;
import com.example.demo.Entity.ConfigAdminDiscountEntity;
import com.example.demo.Entity.ReservationDiscountEntity;
import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Repository.ConfigAdminDiscountRepository;
import com.example.demo.Repository.ReservationDiscountRepository;
import com.example.demo.Repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class DiscountApplicationService {

    private final ReservationRepository reservationRepository;
    private final ConfigAdminDiscountRepository discountConfigRepository;
    private final ReservationDiscountRepository reservationDiscountRepository;

    /*
        Constructor:
        - Descripción: Inyección por constructor. Permite utilizar otras entidades
        - Entradas: ReservationRepository, ConfigAdminDiscountRepository, ReservationDiscountRepository
        - Salidas: void
     */
    public DiscountApplicationService(
            ReservationRepository reservationRepository,
            ConfigAdminDiscountRepository discountConfigRepository,
            ReservationDiscountRepository reservationDiscountRepository) {

        this.reservationRepository = reservationRepository;
        this.discountConfigRepository = discountConfigRepository;
        this.reservationDiscountRepository = reservationDiscountRepository;
    }

    /*
        Orquestador de descuentos:
        - Descripción: Inicia con el subtotal (variable currentAmount) obtenido de (num_pasajeros * precio_paquete) y
        luego se llama a las funciones que aplican cada descuento y se va actualizando currentAmount
        - Entradas: reservation (ReservationEntity), subtotal (BigDecimal), reservationDTO (ReservationRequestDTO)
        - Salidas: currentAmount (BigDecimal)
     */
    public BigDecimal applyDiscounts(
            ReservationEntity reservation,
            BigDecimal subtotal,
            ReservationRequestDTO reservationDTO) {

        BigDecimal currentAmount = subtotal;

        currentAmount = applyGroupDiscount(
                reservation,
                currentAmount,
                reservationDTO
        );

        currentAmount = applyFrequentCustomerDiscount(
                reservation,
                currentAmount
        );

        return currentAmount;
    }

    /*
        Aplica descuento por cantidad de pasajeros:
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
            ReservationRequestDTO reservationDTO) {

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

        // Crea una entidad intermedia entre reserva y descuento
        saveAppliedDiscount(
                reservation,
                discount,
                discountAmount
        );

        // Resta el descuento obtenido al currentAmount
        return currentAmount.subtract(discountAmount);
    }

    /*
        Aplica descuento por cliente frecuente:
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
            BigDecimal currentAmount) {

        // Consulta a la BD cuantas reservas pagadas tiene el usuario que está haciendo la reserva
        long paidReservations =
                reservationRepository
                        .countByUser_UserIdAndReservationState(
                                reservation.getUser().getUserId(),
                                ReservationEntity.ReservationState.PAID
                        );

        // Se define la cantidad de reservas históricas pagadas necesarias para aplicar el descuento
        // Ej: Aquí se requieren 3 o más reservas pagadas para aplicar descuento
        if (paidReservations < 3) {
            return currentAmount;
        }

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

        // Obtiene el monto de descuento (discountAmount = currentAmount * percentage/100)
        BigDecimal discountAmount =
                calculatePercentageDiscount(
                        currentAmount,
                        discount.getPercentage()
                );

        // Crea una entidad intermedia entre reserva y descuento
        saveAppliedDiscount(
                reservation,
                discount,
                discountAmount
        );

        // Resta el descuento obtenido al currentAmount
        return currentAmount.subtract(discountAmount);
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
}
