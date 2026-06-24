package com.example.demo.Initializer;

import com.example.demo.Entity.TourPackageEntity;
import com.example.demo.Repository.TourPackageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class TourPackageInitializer {

    // Esta variable nos permitirá controlar desde el docker-compose qué backend inicia los datos
    @Value("${RUN_INIT_DATA:false}")
    private boolean runInitData;

    @Bean
    CommandLineRunner initDatabase(TourPackageRepository repository) {
        return args -> {
            // Solo intentamos insertar si la bandera está en true y la base de datos está vacía
            if (runInitData && repository.count() == 0) {

                TourPackageEntity p1 = TourPackageEntity.builder()
                        .packageName("Tour por el Desierto")
                        .destination("Atacama")
                        .description("Experiencia inolvidable bajo las estrellas")
                        .startDate(LocalDateTime.of(2026, 6, 10, 8, 0))
                        .endDate(LocalDateTime.of(2026, 6, 15, 18, 0))
                        .duration(5)
                        .prize(new BigDecimal("500.00"))
                        .availableSlots(10)
                        .totalSlots(10)
                        .packageState(TourPackageEntity.PackageState.AVAILABLE)
                        .build();

                TourPackageEntity p2 = TourPackageEntity.builder()
                        .packageName("Aventura en la Selva")
                        .destination("Amazonas")
                        .description("Explora la biodiversidad más grande del mundo")
                        .startDate(LocalDateTime.of(2026, 6, 20, 9, 0))
                        .endDate(LocalDateTime.of(2026, 6, 25, 18, 0))
                        .duration(5)
                        .prize(new BigDecimal("750.00"))
                        .availableSlots(5)
                        .totalSlots(5)
                        .packageState(TourPackageEntity.PackageState.AVAILABLE)
                        .build();

                TourPackageEntity p3 = TourPackageEntity.builder()
                        .packageName("Ruta de los Glaciares")
                        .destination("Patagonia")
                        .description("Paisajes helados únicos en el mundo")
                        .startDate(LocalDateTime.of(2026, 7, 1, 10, 0))
                        .endDate(LocalDateTime.of(2026, 7, 6, 18, 0))
                        .duration(5)
                        .prize(new BigDecimal("900.00"))
                        .availableSlots(8)
                        .totalSlots(8)
                        .packageState(TourPackageEntity.PackageState.AVAILABLE)
                        .build();

                repository.saveAll(List.of(p1, p2, p3));
                System.out.println(">>> Datos iniciales cargados correctamente en la base de datos.");
            }
        };
    }
}
