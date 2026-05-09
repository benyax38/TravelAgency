package com.example.demo.Service;

import com.example.demo.Config.ResourceNotFoundException;
import com.example.demo.DTOs.ReservationRequestDTO;
import com.example.demo.Entity.ConfigAdminDiscountEntity;
import com.example.demo.Entity.ReservationDiscountEntity;
import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Entity.TourPackageEntity;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.ConfigAdminDiscountRepository;
import com.example.demo.Repository.ReservationDiscountRepository;
import com.example.demo.Repository.ReservationRepository;
import com.example.demo.Repository.TourPackageRepository;
import com.example.demo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TourPackageRepository tourPackageRepository;
    private final ConfigAdminDiscountRepository configAdminDiscountRepository;
    private final ReservationDiscountRepository reservationDiscountRepository;

    // Inyección por constructor (recomendado)
    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              TourPackageRepository tourPackageRepository, ConfigAdminDiscountRepository configAdminDiscountRepository, ReservationDiscountRepository reservationDiscountRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.configAdminDiscountRepository = configAdminDiscountRepository;
        this.reservationDiscountRepository = reservationDiscountRepository;
    }

    // CREATE
    @Transactional
    public ReservationEntity createReservation(ReservationRequestDTO reservationDTO) {

        // Validaciones de existencia
        UserEntity user = userRepository.findById(reservationDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));

        TourPackageEntity tourPackage = tourPackageRepository.findById(reservationDTO.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paquete no encontrado"));

        // Reglas de negocio
        validateReservation(reservationDTO, tourPackage);

        // Crear la entidad de reserva
        ReservationEntity reservation = buildReservation(reservationDTO, user, tourPackage);

        // Calcular el precio base (subtotal = precio_paquete * num_pasajeros)
        BigDecimal subtotal = calculateSubtotal(tourPackage, reservationDTO);

        // Aplicar descuentos
        BigDecimal finalAmount = applyDiscounts(reservation, subtotal, reservationDTO);

        // Valor del descuento aplicado
        BigDecimal discountAmount = subtotal.subtract(finalAmount);

        // Asignar valores a la entidad
        reservation.setSubtotalAmount(subtotal);
        reservation.setDiscountAmount(discountAmount);
        reservation.setTotalAmount(finalAmount);

        // Descontar los cupos al paquete
        updateAvailableSlots(tourPackage, reservationDTO);

        return reservationRepository.save(reservation);
    }

    public void validateReservation(ReservationRequestDTO reservationDTO, TourPackageEntity tourPackage) {

        // Una reserva debe estar asociada obligatoriamente a un cliente y a un paquete turístico existente --> Épica 4

        // --------------------------------- VALIDACIONES PREVIAS -----------------------------------------------

        // No se puede generar una reserva si la cantidad solicitada excede los cupos disponibles del paquete --> Épica 4
        Integer availableSlots = tourPackage.getAvailableSlots();

        if (availableSlots == null || availableSlots <= 0) {
            throw new RuntimeException("No hay cupos disponibles para este paquete");
        }
        if (reservationDTO.getPassengersNum() > availableSlots) {
            throw new RuntimeException(
                    "La cantidad de pasajeros excede los cupos disponibles (" + availableSlots + ")"
            );
        }

        // No se puede registrar una reserva para un paquete cancelado, no vigente o agotado --> Épica 4
        TourPackageEntity.PackageState state = tourPackage.getPackageState();
        if (state == TourPackageEntity.PackageState.NOT_AVAILABLE ||
                state == TourPackageEntity.PackageState.SOLD_OUT ||
                state == TourPackageEntity.PackageState.CANCELLED) {

            throw new RuntimeException("No se puede reservar este paquete (estado: " + state + ")");
        }
    }

    public ReservationEntity buildReservation(
            ReservationRequestDTO reservationDTO,
            UserEntity user,
            TourPackageEntity tourPackage) {

        // --------------------------------- CONSTRUCCION DE LA ENTIDAD -----------------------------------------------

        ReservationEntity reservation = new ReservationEntity();

        // Atributos provenientes del DTO de entrada
        reservation.setPassengersNum(reservationDTO.getPassengersNum());
        reservation.setCompanionsDetails(reservationDTO.getCompanionsDetails());
        reservation.setSpecialRequests(reservationDTO.getSpecialRequests());
        reservation.setCustomerPreferences(reservationDTO.getCustomerPreferences());

        reservation.setUser(user);
        reservation.setTourPackage(tourPackage);

        // Valores automáticos
        reservation.setReservationDate(LocalDateTime.now());
        // La reserva debe crearse con un estado inicial controlado, por ejemplo: pendiente de pago --> Épica 4
        reservation.setReservationState(ReservationEntity.ReservationState.PENDING);
        reservation.setPaymentDeadline(LocalDateTime.now().plusHours(24));

        return reservation;
    }

    public BigDecimal calculateSubtotal(
            TourPackageEntity tourPackage,
            ReservationRequestDTO reservationDTO) {

        // El monto total de la reserva debe calcularse en función del precio vigente del paquete y la cantidad de
        // pasajeros --> Épica 4
        // Básicamente monto_total = precio_paquete * n_de_pasajeros
        return tourPackage.getPrize()
                .multiply(
                        BigDecimal.valueOf(
                                reservationDTO.getPassengersNum()
                        )
                );
    }

    public BigDecimal applyDiscounts(
            ReservationEntity reservation,
            BigDecimal subtotal,
            ReservationRequestDTO reservationDTO) {

        BigDecimal finalAmount = subtotal;

        finalAmount = discountNumPassengers(
                reservation,
                finalAmount,
                reservationDTO
        );

        return finalAmount;
    }

    public BigDecimal discountNumPassengers(
            ReservationEntity reservation,
            BigDecimal currentAmount,
            ReservationRequestDTO reservationDTO) {

        Optional<ConfigAdminDiscountEntity> optionalDiscount =
                configAdminDiscountRepository
                        .findByDiscountTypeAndActiveTrue(
                                ConfigAdminDiscountEntity.DiscountType.GROUP_DISCOUNT);

        if(optionalDiscount.isPresent()) {

            ConfigAdminDiscountEntity discount =
                    optionalDiscount.get();

            // Verificar mínimo de pasajeros
            if (reservationDTO.getPassengersNum() >= discount.getMinPassengers()) {

                BigDecimal percentage =
                        BigDecimal.valueOf(discount.getPercentage());

                BigDecimal discountAmount =
                        currentAmount.multiply(percentage)
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                BigDecimal finalAmount =
                        currentAmount.subtract(discountAmount);

                // Guardar descuento aplicado
                ReservationDiscountEntity reservationDiscount =
                        new ReservationDiscountEntity();

                reservationDiscount.setReservation(reservation);
                reservationDiscount.setDiscountConfig(discount);
                reservationDiscount.setDiscountAmount(discountAmount);

                reservationDiscountRepository.save(reservationDiscount);

                return finalAmount;
            }
        }

        return currentAmount;
    }

    public void updateAvailableSlots(
            TourPackageEntity tourPackage,
            ReservationRequestDTO reservationDTO) {

        // Al crearse una reserva válida, el sistema debe descontar los cupos correspondientes del paquete --> Épica 4
        tourPackage.setAvailableSlots(
                tourPackage.getAvailableSlots() - reservationDTO.getPassengersNum()
        );
    }

    // READ
    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.findAll();
    }
}
