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

    // Metodo simple para probar el backend
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
