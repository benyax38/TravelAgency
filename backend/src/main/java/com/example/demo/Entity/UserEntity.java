package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true)
    private String keycloakId;

    @Column(name = "username", unique = true, length = 80)
    private String username;

    @Column(name = "first_name", length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "email", unique = true, length = 80)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "dni", length = 20)
    private String dni;

    @Column(name = "nationality", length = 80)
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_state", length = 40)
    private UserState userState;

    public enum UserState {
        ACTIVE,
        BLOCKED,
        INACTIVE
    }

    @Column(name = "paid_reservations")
    private Integer paidReservations = 0;
}
