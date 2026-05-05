package com.example.demo.Controller;

import com.example.demo.Entity.UserEntity;
import com.example.demo.Service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // Inyección por constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint para probar el backend
    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

}
