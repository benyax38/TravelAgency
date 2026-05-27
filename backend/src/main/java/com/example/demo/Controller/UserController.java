package com.example.demo.Controller;

import com.example.demo.DTOs.UpdateUserProfileDTO;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // =========================================
    // Obtiene el usuario autenticado y lo sincroniza con el sistema
    // =========================================

    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UserEntity user =
                userService.syncUserWithKeycloak(jwt);

        // Devuelve status 200 + JSON del usuario
        return ResponseEntity.ok(user);
    }

    // =========================================
    // Obtiene todos los usuarios
    // =========================================

    @GetMapping
    public List<UserEntity> getAllUsers() {

        return userService.getAllUsers();
    }

    // =========================================
    // Actualiza un usuario
    // =========================================
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProfileDTO dto
            ) {

        UserEntity user = userService.updateUser(id, dto);

        return ResponseEntity.ok(user);
    }

    // =========================================
    // Deja a un usuario inactivo
    // =========================================
    @PatchMapping("/{id}/inactive")
    public ResponseEntity<UserEntity> deactivateUser(
            @PathVariable Long id
    ) {

        UserEntity user = userService.deactivateUser(id);

        return ResponseEntity.ok(user);
    }

    // =========================================
    // Vuelve a activar un usuario
    // =========================================
    @PatchMapping("/{id}/active")
    public ResponseEntity<UserEntity> activateUser(
            @PathVariable Long id
    ) {

        UserEntity user = userService.activateUser(id);

        return ResponseEntity.ok(user);
    }
}
