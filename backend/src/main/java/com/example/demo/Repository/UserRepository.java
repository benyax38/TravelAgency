package com.example.demo.Repository;

import com.example.demo.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    // Busca un usuario en la base de datos mediante el keycloakId
    Optional<UserEntity> findByKeycloakId(String keycloakId);

    UserEntity findByUsername(String username);
}
