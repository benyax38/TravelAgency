package com.example.demo.Service;

import com.example.demo.DTOs.CreatePackageDTO;
import com.example.demo.Entity.ReservationEntity;
import com.example.demo.Entity.ReservationPackageEntity;
import com.example.demo.Entity.TourPackageEntity;
import com.example.demo.Repository.TourPackageRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TourPackageService {

    private final TourPackageRepository tourPackageRepository;

    // Inyección por constructor (recomendado)
    public TourPackageService(TourPackageRepository tourPackageRepository) {
        this.tourPackageRepository = tourPackageRepository;
    }

    // CREATE
    public TourPackageEntity createPackage(CreatePackageDTO tourPackageDTO) {

        // El precio del paquete debe ser mayor que cero (Épica 2) --> manejado con validaciones en la entidad


        // ------------------------------- COMPROBACIONES DE FECHAS -------------------------------------------

        // - Considerar que se puede crear un paquete con fecha "ahora" o 1 min desde ahora
        // - Se debera definir por el cliente cual es la duracion del paquete
        // - Cliente no tendra acceso a la fecha de termino (se calcula a partir de la duracion definida)

        // Variable para fecha actual
        LocalDateTime today = LocalDateTime.now();

        // La fecha de inicio debe ser posterior a la fecha actual
        if(tourPackageDTO.getStartDate().isBefore(today)) {
            throw new IllegalArgumentException("La fecha de inicio no puede estar en el pasado");
        }

        // Cálculo automático de la fecha de término (se puede mejorar especificando que el día termine al final del dia)
        LocalDateTime endDate = tourPackageDTO.getStartDate().plusDays(tourPackageDTO.getDuration());

        // ------------------------------- COMPROBACIONES DE CUPOS -------------------------------------------

        // Los cupos totales del paquete deben ser mayores que cero (Épica 2) --> manejado por CreatePackageDTO
        // - El cliente sólo podrá elegir los cupos totales siendo este valor asignado a los cupos disponibles
        // availableSlots <= totalSlots ¿?

        // Un paquete no puede publicarse como disponible si no tiene cupos (Épica 2) --> En el builder: availableSlots = totalSlots

        // ------------------------------- BUILDER -------------------------------------------

        // - El paquete inicia con estado AVAILABLE

        TourPackageEntity entity = TourPackageEntity.builder()
                .packageName(tourPackageDTO.getPackageName())
                .destination(tourPackageDTO.getDestination())
                .description(tourPackageDTO.getDescription())
                .startDate(tourPackageDTO.getStartDate())
                .duration(tourPackageDTO.getDuration())
                .prize(tourPackageDTO.getPrize())
                .includedServices(tourPackageDTO.getIncludedServices())
                .conditions(tourPackageDTO.getConditions())
                .restriction(tourPackageDTO.getRestriction())
                .totalSlots(tourPackageDTO.getTotalSlots())
                .availableSlots(tourPackageDTO.getTotalSlots())
                .endDate(endDate)
                .tripType(tourPackageDTO.getTripType())
                .season(tourPackageDTO.getSeason())
                .category(tourPackageDTO.getCategory())
                .packageState(TourPackageEntity.PackageState.AVAILABLE) //
                .build();

        return tourPackageRepository.save(entity);
    }

    // READ
    public List<TourPackageEntity> getAllPackagesService() {
        return tourPackageRepository.findAll();
    }

    public List<TourPackageEntity> getVisiblePackagesForUsers() {

        return tourPackageRepository.findTop6ByPackageStateIn(
                List.of(
                        TourPackageEntity.PackageState.AVAILABLE,
                        TourPackageEntity.PackageState.NOT_AVAILABLE
                )
        );
    }

    public List<TourPackageEntity> searchPackages(
            String destination,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer duration,
            String tripType
    ) {

        return tourPackageRepository.searchPackages(
                destination,
                minPrice,
                maxPrice,
                duration,
                tripType,
                LocalDateTime.now()
        );
    }

    // UPDATE --> POR REVISAR
    // Si un paquete ya tiene reservas registradas, no deben modificarse campos críticos que
    // afecten la consistencia de la operación sin validación previa, como fechas base o cupos
    // totales menores al número ya reservado (Épica 2)
    public TourPackageEntity updatePackage(Long id, TourPackageEntity updatedData) {

        // 🔍 1. Obtener el paquete existente
        TourPackageEntity existing = tourPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado"));

        // 🔍 2. Determinar si el paquete tiene reservas asociadas
        boolean hasReservations = !existing.getReservationPackages().isEmpty();

        // 📅 Variables útiles
        LocalDateTime today = LocalDateTime.now();

        LocalDateTime newStartDate = updatedData.getStartDate();
        LocalDateTime newEndDate = updatedData.getEndDate();

        LocalDateTime currentStartDate = existing.getStartDate();
        LocalDateTime currentEndDate = existing.getEndDate();

        Integer newTotalSlots = updatedData.getTotalSlots();


        // =========================================================
        // 🟢 3. VALIDACIONES GENERALES (SIEMPRE SE APLICAN)
        // Estas validaciones aseguran coherencia básica del sistema
        // independiente de si existen reservas o no
        // =========================================================

        // ✔ Validar fechas si ambas vienen en la actualización
        if (newStartDate != null && newEndDate != null) {
            if (!newStartDate.isBefore(newEndDate)) {
                throw new IllegalArgumentException(
                        "La fecha de inicio debe ser anterior a la fecha de término"
                );
            }
        }

        // ✔ Validar que startDate no sea en el pasado (si se modifica)
        if (newStartDate != null) {
            if (!newStartDate.isAfter(today)) {
                throw new IllegalArgumentException(
                        "La fecha de inicio debe ser posterior a la fecha actual"
                );
            }
        }

        // ✔ Validar cupos básicos
        if (newTotalSlots != null && newTotalSlots <= 0) {
            throw new IllegalArgumentException(
                    "El paquete debe tener al menos 1 cupo total"
            );
        }


        // =========================================================
        // 🔒 4. VALIDACIONES CONDICIONADAS (SOLO SI HAY RESERVAS)
        // Aquí protegemos la consistencia del negocio:
        // evitar cambios que afecten reservas ya realizadas
        // =========================================================

        if (hasReservations) {

            // ⚠️ Caso 1: Se intenta modificar startDate
            if (newStartDate != null && !newStartDate.equals(currentStartDate)) {

                // Debe seguir siendo anterior a la fecha de término actual
                if (!newStartDate.isBefore(currentEndDate)) {
                    throw new IllegalArgumentException(
                            "La nueva fecha de inicio debe ser anterior a la fecha de término actual"
                    );
                }
            }

            // ⚠️ Caso 2: Se intenta modificar endDate
            if (newEndDate != null && !newEndDate.equals(currentEndDate)) {

                // Usamos la nueva startDate si viene, si no la actual
                LocalDateTime referenceStart = (newStartDate != null)
                        ? newStartDate
                        : currentStartDate;

                if (!newEndDate.isAfter(referenceStart)) {
                    throw new IllegalArgumentException(
                            "La nueva fecha de término debe ser posterior a la fecha de inicio"
                    );
                }
            }

            // ⚠️ Caso 3: Validación de cupos con reservas existentes
            // (Aquí sí importa la cantidad reservada)
            int reservedSlots = existing.getReservationPackages()
                    .stream()
                    .map(ReservationPackageEntity::getReservation)
                    .mapToInt(ReservationEntity::getPassengersNum)
                    .sum();

            if (newTotalSlots != null && newTotalSlots < reservedSlots) {
                throw new IllegalArgumentException(
                        "Los cupos totales no pueden ser menores a los ya reservados"
                );
            }
        }


        // =========================================================
        // ✏️ 5. APLICAR CAMBIOS (solo despues de validar todo)
        // =========================================================

        if (newStartDate != null) {
            existing.setStartDate(newStartDate);
        }

        if (newEndDate != null) {
            existing.setEndDate(newEndDate);
        }

        if (newTotalSlots != null) {
            existing.setTotalSlots(newTotalSlots);
        }

        existing.setPackageName(updatedData.getPackageName());
        existing.setDescription(updatedData.getDescription());
        existing.setPrize(updatedData.getPrize());


        // 💾 6. Guardar cambios
        return tourPackageRepository.save(existing);
    }

    // DELETE --> POR REVISAR
    // Un paquete con reservas asociadas no debe eliminarse físicamente; solo puede cambiar su estado (Épica 2)
    public void cancelPackage(Long id) {

        TourPackageEntity pkg = tourPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado"));

        if (pkg.getPackageState() != TourPackageEntity.PackageState.AVAILABLE &&
                pkg.getPackageState() != TourPackageEntity.PackageState.SOLD_OUT &&
                pkg.getPackageState() != TourPackageEntity.PackageState.NOT_AVAILABLE) {

            throw new IllegalStateException(
                    "El paquete no puede ser cancelado en su estado actual"
            );
        }

        pkg.setPackageState(TourPackageEntity.PackageState.CANCELLED);
        tourPackageRepository.save(pkg);
    }


    //@Scheduled(cron = "*/15 * * * * *") // 15 segundos
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updatePackagesAvailability() {

        LocalDateTime now = LocalDateTime.now();

        List<TourPackageEntity> packages =
                tourPackageRepository.findByPackageState(
                        TourPackageEntity.PackageState.AVAILABLE
                );

        List<TourPackageEntity> updatedPackages =
                new ArrayList<>();

        for (TourPackageEntity pkg : packages) {

            if (pkg.getStartDate() == null) {
                continue;
            }

            LocalDateTime limitDate =
                    pkg.getStartDate().minusDays(1);

            if (!now.isBefore(limitDate)) {

                pkg.setPackageState(
                        TourPackageEntity.PackageState.NOT_AVAILABLE
                );

                updatedPackages.add(pkg);
            }
        }

        if (!updatedPackages.isEmpty()) {

            tourPackageRepository.saveAll(updatedPackages);
        }
    }
}
