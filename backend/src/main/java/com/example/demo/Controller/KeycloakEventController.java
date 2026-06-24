package com.example.demo.Controller;

import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/events")
public class KeycloakEventController {

    @Autowired
    private UserRepository userRepository; // O tu UserService

    @PostMapping("/user-lockout")
    public ResponseEntity<Void> handleUserLockout(@RequestBody Map<String, String> payload) {
        String keycloakUserId = payload.get("userId");

        // Buscamos al usuario en tu BD usando el ID de Keycloak (sub)
        userRepository.findByKeycloakId(keycloakUserId).ifPresent(user -> {
            user.setUserState(UserEntity.UserState.BLOCKED); // Tu enum o String BLOCKED
            userRepository.save(user);
            System.out.println("¡Alerta! Usuario " + user.getUsername() + " sincronizado a BLOCKED.");
        });

        return ResponseEntity.ok().build();
    }
}
