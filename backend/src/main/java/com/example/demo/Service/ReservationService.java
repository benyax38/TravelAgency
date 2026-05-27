package com.example.demo.Service;

import com.example.demo.Config.ResourceNotFoundException;
import com.example.demo.DTOs.ReservationRequestDTO;
import com.example.demo.DTOs.ReservationSummaryDTO;
import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Entity.ReservationPackageEntity;
import com.example.demo.Entity.TourPackageEntity;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.ReservationPackageRepository;
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
    private final ReservationPackageRepository reservationPackageRepository;
    private final UserRepository userRepository;
    private final TourPackageRepository tourPackageRepository;
    private final DiscountApplicationService discountApplicationService;

    // Inyección por constructor
    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationPackageRepository reservationPackageRepository,
            UserRepository userRepository,
            TourPackageRepository tourPackageRepository,
            DiscountApplicationService discountApplicationService) {

        this.reservationRepository = reservationRepository;
        this.reservationPackageRepository = reservationPackageRepository;
        this.userRepository = userRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.discountApplicationService = discountApplicationService;
    }

    /*
     * createReservation
     * Descripcion: Metodo orquestador para la creacion de reservas
     * Entrada: DTO de entrada
     * Salida: Resumen para mostrar en frontend
     * */
    @Transactional
    public ReservationSummaryDTO createReservation(
            ReservationRequestDTO reservationDTO) {

        // Validaciones de existencia
        UserEntity user = findUserById(reservationDTO.getUserId());

        List<TourPackageEntity> tourPackages =
                findPackagesByIds(reservationDTO.getPackageIds());

        // Validaciones de negocio
        validateReservation(reservationDTO, tourPackages);

        // Crear reserva
        ReservationEntity reservation =
                buildReservation(reservationDTO, user);

        // Guardar reserva
        ReservationEntity savedReservation =
                reservationRepository.save(reservation);

        // Crear relación intermedia
        createReservationPackageRelations(
                savedReservation,
                tourPackages
        );

        // Subtotal inicial sin descuentos (suma de precio de todos los paquetes * numero de pasajeros)
        BigDecimal subtotal =
                calculateSubtotal(tourPackages, reservationDTO);

        // Actualizar montos en la reserva
        initializeReservationAmounts(
                savedReservation,
                subtotal
        );

        // Obtiene el monto de descuento total
        BigDecimal finalAmount =
                discountApplicationService.applyDiscounts(
                        savedReservation,
                        subtotal,
                        reservationDTO
                );

        finalizeReservationAmounts(
                savedReservation,
                subtotal,
                finalAmount
        );

        // Actualizar cupos
        updateAvailableSlots(tourPackages, reservationDTO);

        // Persistencia en bd
        reservation = reservationRepository.save(reservation);

        return ReservationSummaryDTO.builder()
                .reservationId(
                        reservation.getReservationId()
                )
                .reservationState(
                        reservation.getReservationState().name()
                )
                .reservationDate(
                        reservation.getReservationDate()
                )
                .paymentDeadline(
                        reservation.getPaymentDeadline()
                )
                .passengersNum(
                        reservation.getPassengersNum()
                )
                .subtotalAmount(
                        reservation.getSubtotalAmount()
                )
                .discountAmount(
                        reservation.getDiscountAmount()
                )
                .totalAmount(
                        reservation.getTotalAmount()
                )
                .build();
    }

    /*
     * findUserbyId
     * Descripcion: Comprueba si existe el usuario en la base de datos
     * Entrada: Id de usuario
     * Salida: Entidad usuario
     * */
    private UserEntity findUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado"
                        )
                );
    }

    /*
     * findPackagesByIds
     * Descripcion: Comprueba si existen los paquetes en la base de datos
     * Entrada: Lista de ids de paquetes
     * Salida: Lista de entidades de paquetes
     * */
    private List<TourPackageEntity> findPackagesByIds(
            List<Long> packageIds
    ) {

        List<TourPackageEntity> packages =
                tourPackageRepository.findAllById(packageIds);

        if (packages.size() != packageIds.size()) {

            throw new ResourceNotFoundException(
                    "Uno o más paquetes no existen"
            );
        }

        return packages;
    }

    /*
     * validateReservation
     * Descripcion: Metodo orquestador de validaciones
     * Entrada: DTO de entrada + Lista de paquetes
     * Salida: void
     * */
    public void validateReservation(ReservationRequestDTO reservationDTO, List<TourPackageEntity> tourPackages
    ) {
        // Valida número de pasajeros
        validatePassengersNumber(reservationDTO);

        // Valida disponibilidad de los paquetes
        validatePackagesAvailability(
                reservationDTO,
                tourPackages
        );

        // Valida estado de los paquetes
        validatePackagesState(tourPackages);
    }

    /*
     * validatePassengersNumber
     * Descripcion: Comprueba que el numero de pasajeros sea mayor a cero
     * Entrada: DTO de entrada
     * Salida: void
     * */
    private void validatePassengersNumber(
            ReservationRequestDTO reservationDTO
    ) {

        if (reservationDTO.getPassengersNum() == null ||
                reservationDTO.getPassengersNum() <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad de pasajeros debe ser mayor a 0"
            );
        }
    }

    /*
     * validatePackagesAvailability
     * Descripcion: Para cada paquete comprueba:
     *  1. Que tenga cupos disponibles
     *  2. Que la cantidad de pasajeros no supere a los cupos disponibles
     * Entrada: DTO de entrada + Lista de paquetes
     * Salida: void
     * */
    private void validatePackagesAvailability(
            ReservationRequestDTO reservationDTO,
            List<TourPackageEntity> tourPackages
    ) {

        for (TourPackageEntity tourPackage : tourPackages) {

            Integer availableSlots =
                    tourPackage.getAvailableSlots();

            if (availableSlots == null ||
                    availableSlots <= 0) {

                throw new IllegalArgumentException(
                        "El paquete '" +
                                tourPackage.getPackageName() +
                                "' no tiene cupos disponibles"
                );
            }

            if (reservationDTO.getPassengersNum() >
                    availableSlots) {

                throw new IllegalArgumentException(
                        "La cantidad de pasajeros excede " +
                                "los cupos disponibles del paquete '" +
                                tourPackage.getPackageName() +
                                "' (" + availableSlots + ")"
                );
            }
        }
    }

    /*
     * validatePackageState
     * Descripcion: Evita que se reserven paquetes con estado NOT_AVAILABLE, SOLD_OUT o CANCELLED
     * Entrada: Lista de paquetes
     * Salida: void
     * */
    private void validatePackagesState(
            List<TourPackageEntity> tourPackages
    ) {

        for (TourPackageEntity tourPackage : tourPackages) {

            TourPackageEntity.PackageState state =
                    tourPackage.getPackageState();

            if (state ==
                    TourPackageEntity.PackageState.NOT_AVAILABLE ||

                    state ==
                            TourPackageEntity.PackageState.SOLD_OUT ||

                    state ==
                            TourPackageEntity.PackageState.CANCELLED) {

                throw new IllegalArgumentException(
                        "No se puede reservar el paquete '" +
                                tourPackage.getPackageName() +
                                "' (estado: " + state + ")"
                );
            }
        }
    }

    /*
     * buildReservation
     * Descripcion: Crea al reserva considerando:
     *  1. Se asignan los valores del DTO de entrada al paquete
     *  2. Se crean valores automaticos
     * Entrada: DTO de entrada + Entidad de usuario
     * Salida: Entidad de reserva
     * */
    private ReservationEntity buildReservation(
            ReservationRequestDTO reservationDTO,
            UserEntity user
    ) {

        // Se crea una reserva vacia (inicializa el id)
        ReservationEntity reservation =
                new ReservationEntity();

        // --- Parte 1: Datos ingresados por el usuario (DTO) ---

        // Numero de pasajeros
        reservation.setPassengersNum(
                reservationDTO.getPassengersNum()
        );

        // Detalle de acompañantes
        reservation.setCompanionsDetails(
                reservationDTO.getCompanionsDetails()
        );

        // Solicitudes especiales
        reservation.setSpecialRequests(
                reservationDTO.getSpecialRequests()
        );

        // Preferencias del cliente
        reservation.setCustomerPreferences(
                reservationDTO.getCustomerPreferences()
        );

        // Relación con usuario
        reservation.setUser(user);

        // --- Parte 2: Valores automáticos ---

        // Se asigna estado PENDING
        reservation.setReservationState(
                ReservationEntity.ReservationState.PENDING
        );

        // La fecha de reserva es cuando se crea
        reservation.setReservationDate(
                LocalDateTime.now()
        );

        // Fecha limite de pago: 24 horas actualmente
        reservation.setPaymentDeadline(
                LocalDateTime.now().plusHours(24)
        );

        // Inicialización de montos para evitar nulls
        reservation.setSubtotalAmount(BigDecimal.ZERO);

        reservation.setDiscountAmount(BigDecimal.ZERO);

        reservation.setTotalAmount(BigDecimal.ZERO);

        return reservation;
    }

    /*
     * createReservationPackageRelations
     * Descripcion: Crea una entidad intermedia por cada paquete agregado a la reserva
     * Entrada: Entidad de reserva + Lista de paquetes
     * Salida: void
     * */
    private void createReservationPackageRelations(
            ReservationEntity reservation,
            List<TourPackageEntity> tourPackages
    ) {

        // Se crea una entidad intermedia por cada paquete en la lista
        List<ReservationPackageEntity> relations =
                tourPackages.stream()
                        .map(pkg -> {

                            // Inicia la entidad
                            ReservationPackageEntity relation =
                                    new ReservationPackageEntity();

                            // Le asigna valores a las relaciones
                            relation.setReservation(reservation);

                            relation.setTourPackage(pkg);

                            return relation;
                        })
                        .toList();

        // Guarda las entidades intermedias creadas
        reservationPackageRepository.saveAll(relations);

        // Asocia la lista de entidades intermedias a la reserva
        reservation.setReservationPackages(relations);
    }

    /*
     * calculateSubtotal
     * Descripcion: Suma el precio de cada paquete en la reserva y multiplica el resultado por la cantidad de pasajeros
     * Entrada: Lista de paquetes + DTO de entrada
     * Salida: Valor subtotal (sin descuentos aplicados)
     * */
    public BigDecimal calculateSubtotal(
            List<TourPackageEntity> tourPackages,
            ReservationRequestDTO reservationDTO) {

        // Suma de precios de todos los paquetes
        BigDecimal packagesTotal = tourPackages.stream()
                .map(TourPackageEntity::getPrize)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // subtotal = suma paquetes * cantidad pasajeros
        // Ojo: se asume que todos los pasajeros solicitan todos los paquetes
        return packagesTotal.multiply(
                BigDecimal.valueOf(
                        reservationDTO.getPassengersNum()
                )
        );
    }

    /*
     * initializeReservationAmounts
     * Descripcion: Permite iniciar montos una vez calculado el subtotal (sin descuentos)
     * Entrada: Entidad de reserva + subtotal
     * Salida: void
     * */
    private void initializeReservationAmounts(
            ReservationEntity reservation,
            BigDecimal subtotal
    ) {
        // Guarda el subtotal
        reservation.setSubtotalAmount(subtotal);

        // Total temporal (sin descuentos)
        reservation.setTotalAmount(subtotal);

        // Aun no se aplican descuentos
        reservation.setDiscountAmount(BigDecimal.ZERO);
    }

    /*
     * finalizeReservationAmounts
     * Descripcion: Actualiza los montos con los descuentos ya calculados
     * Entrada: Entidad de reserva + subtotal + finalAmount
     * Salida: void
     * */
    private void finalizeReservationAmounts(
            ReservationEntity reservation,
            BigDecimal subtotal,
            BigDecimal finalAmount
    ) {

        // Seguridad extra
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        // Calcular descuento real aplicado
        BigDecimal discountAmount =
                subtotal.subtract(finalAmount);

        reservation.setDiscountAmount(discountAmount);

        reservation.setTotalAmount(finalAmount);
    }

    /*
     * updateAvailableSlots
     * Descripcion: Permite iniciar montos una vez calculado el subtotal (sin descuentos)
     * Entrada: Lista de paquetes + DTO de entrada
     * Salida: void
     * */
    public void updateAvailableSlots(
            List<TourPackageEntity> tourPackages,
            ReservationRequestDTO reservationDTO) {

        // Obtiene cantidad de pasajeros
        Integer passengersNum = reservationDTO.getPassengersNum();

        // Recorre todos los paquetes de la reserva
        tourPackages.forEach(pkg -> {

            // Descuenta cupos
            pkg.setAvailableSlots(
                    pkg.getAvailableSlots() - passengersNum
            );

            // Si se agotan los cupos
            if (pkg.getAvailableSlots() <= 0) {

                pkg.setAvailableSlots(0);

                pkg.setPackageState(
                        TourPackageEntity.PackageState.SOLD_OUT
                );
            }
        });

        // Persistir cambios
        tourPackageRepository.saveAll(tourPackages);
    }

    // READ
    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.findAll();
    }
}
