package com.example.demo.Controller;

import com.example.demo.Entity.UserEntity;
import com.example.demo.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Inyección por constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserEntity user) {

        try {
            UserEntity newUser = userService.createUser(user);
            return ResponseEntity.ok(newUser);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // READ
    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

}
