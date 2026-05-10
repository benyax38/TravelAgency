package com.example.demo.Service;

import com.example.demo.Config.ResourceNotFoundException;
import com.example.demo.DTOs.ReservationRequestDTO;
import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Entity.TourPackageEntity;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.ReservationRepository;
import com.example.demo.Repository.TourPackageRepository;
import com.example.demo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TourPackageRepository tourPackageRepository;
    private final DiscountApplicationService discountApplicationService;

    // Inyección por constructor
    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            TourPackageRepository tourPackageRepository,
            DiscountApplicationService discountApplicationService) {

        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.discountApplicationService = discountApplicationService;
    }

    // CREATE
    @Transactional
    public ReservationEntity createReservation(
            ReservationRequestDTO reservationDTO) {

        // Validaciones de existencia
        UserEntity user = userRepository.findById(reservationDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));

        TourPackageEntity tourPackage = tourPackageRepository.findById(
                        reservationDTO.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paquete no encontrado"));

        // Validaciones de negocio
        validateReservation(reservationDTO, tourPackage);

        // Crear reserva
        ReservationEntity reservation =
                buildReservation(reservationDTO, user, tourPackage);

        // Subtotal inicial (precio_paquete * num_pasajeros)
        BigDecimal subtotal =
                calculateSubtotal(tourPackage, reservationDTO);

        // Guardar reserva parcial sin descuentos
        reservation.setSubtotalAmount(subtotal);
        reservation.setTotalAmount(subtotal);

        ReservationEntity savedReservation =
                reservationRepository.save(reservation);

        // Obtiene el monto de descuento total
        BigDecimal discountAmount =
                discountApplicationService.applyDiscounts(
                        savedReservation,
                        subtotal,
                        reservationDTO
                );

        // Obtiene el monto final con descuento aplicado
        BigDecimal finalAmount =
                subtotal.subtract(discountAmount);

        // Actualizar reserva con descuentos
        savedReservation.setDiscountAmount(discountAmount);
        savedReservation.setTotalAmount(finalAmount);

        // Actualizar cupos
        updateAvailableSlots(tourPackage, reservationDTO);

        return reservationRepository.save(savedReservation);
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
