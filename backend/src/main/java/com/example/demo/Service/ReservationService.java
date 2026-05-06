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

    // Inyección por constructor (recomendado)
    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              TourPackageRepository tourPackageRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.tourPackageRepository = tourPackageRepository;
    }

    // CREATE
    @Transactional
    public ReservationEntity createReservation(ReservationRequestDTO reservationDTO) {

        // Una reserva debe estar asociada obligatoriamente a un cliente y a un paquete turístico existente --> Épica 4

        // --------------------------------- VALIDACIONES PREVIAS -----------------------------------------------

        // Validar existencia de usuario
        UserEntity user = userRepository.findById(reservationDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + reservationDTO.getUserId() + " no encontrado"));

        // Validar existencia de paquete
        TourPackageEntity tourPackage = tourPackageRepository.findById(reservationDTO.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Paquete no encontrado"));

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

        // --------------------------------- CONSTRUCCION DE LA ENTIDAD -----------------------------------------------

        // Construye la entidad
        ReservationEntity reservation = new ReservationEntity();

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

        // --------------------------------- CALCULOS ----------------------------------------------------

        // El monto total de la reserva debe calcularse en función del precio vigente del paquete y la cantidad de
        // pasajeros --> Épica 4
        // Básicamente monto_total = precio_paquete * n_de_pasajeros
        BigDecimal total = tourPackage.getPrize()
                .multiply(BigDecimal.valueOf(reservationDTO.getPassengersNum()));
        reservation.setTotalAmount(total);

        // ------------------------------ AJUSTES FUERA DE LA ENTIDAD -----------------------------------

        // Al crearse una reserva válida, el sistema debe descontar los cupos correspondientes del paquete --> Épica 4
        tourPackage.setAvailableSlots(
                availableSlots - reservationDTO.getPassengersNum()
        );

        return reservationRepository.save(reservation);
    }

    // READ
    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.findAll();
    }
}
