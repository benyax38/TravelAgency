package com.example.demo.Service;

import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Inyección por constructor (recomendado)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE
    public UserEntity createUser(UserEntity user) {

        // Validación básica de duplicados
        if (userRepository.existsByMail(user.getMail())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        if (userRepository.existsByDni(user.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        // Valores por defecto
        user.setUserState("ACTIVE");
        user.setPaidReservations(0);

        return userRepository.save(user);
    }

    // READ
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
