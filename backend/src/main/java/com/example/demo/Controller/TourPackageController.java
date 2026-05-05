package com.example.demo.Controller;

import com.example.demo.DTOs.CreatePackageDTO;
import com.example.demo.Entity.TourPackageEntity;
import com.example.demo.Service.TourPackageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class TourPackageController {

    private final TourPackageService tourPackageService;

    // Inyección por constructor
    public TourPackageController(TourPackageService tourPackageService) {
        this.tourPackageService = tourPackageService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<TourPackageEntity> createPackage(@Valid @RequestBody CreatePackageDTO packageRequest) {

        TourPackageEntity created = tourPackageService.createPackage(packageRequest);

        return ResponseEntity.ok(created);
        // return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ
    @GetMapping
    public List<TourPackageEntity> getAllPackagesController() {
        return tourPackageService.getAllPackagesService();
    }

    // UPDATE
    // PATCH /packages/{id} --> update general de campos no críticos
    // PUT /packages/{id}/capacity --> update controlado de capacidad
    // PUT /packages/{id}/schedule --> update de fechas

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<TourPackageEntity> updatePackage(
            @PathVariable Long id,
            @RequestBody TourPackageEntity updatedData) {

        TourPackageEntity updated = tourPackageService.updatePackage(id, updatedData);

        return ResponseEntity.ok(updated);
    }

    // DELETE
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelPackage(@PathVariable Long id) {
        tourPackageService.cancelPackage(id);
        return ResponseEntity.ok("Paquete cancelado correctamente");
    }
}
