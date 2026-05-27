package com.example.demo.Service;

import com.example.demo.DTOs.UpdateUserProfileDTO;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    /*
    * syncUserWithKeycloak
    * Descripcion:
    *  1. Lee el token JWT
    *  2. Comprueba si el usuario existe en la base de datos
    *  3. Si no existe, lo crea
    * Entrada: Token JWT autenticado por spring security
    * Salida: Entidad de usuario
    * */
    @Transactional
    public UserEntity syncUserWithKeycloak(Jwt jwt) {

        // Obtiene el claim sub desde el token (keycloakId)
        String keycloakId = jwt.getSubject();

        return userRepository
                // Si el usuario existe, devolverlo
                .findByKeycloakId(keycloakId)
                // Si no existe, crearlo
                .orElseGet(() -> createUserFromJwt(jwt));
    }

    /*
     * createUserFromJwt
     * Descripcion:
     *  1. Lee el token JWT
     *  2. Crea una entidad de usuario vacia
     *  3. Obtiene los claims del token
     *  4. Asigna el valor de los claims a cada atributo del usuario
     *  5. Se guarda el nuevo usuario en la base de datos y lo retorna
     * Entrada: Token JWT autenticado por spring security
     * Salida: Entidad de usuario creado
     * */
    private UserEntity createUserFromJwt(Jwt jwt) {

        // Crea la entidad usuario vacia
        UserEntity user = new UserEntity();

        // Guarda el ID de keycloak
        user.setKeycloakId(jwt.getSubject());

        // Guarda el nombre de usuario
        user.setUsername(
                jwt.getClaim("preferred_username")
        );

        // Guarda el email
        user.setEmail(
                jwt.getClaim("email")
        );

        // Guarda el nombre
        user.setFirstName(
                jwt.getClaim("given_name")
        );

        // Guarda el apellido
        user.setLastName(
                jwt.getClaim("family_name")
        );

        // Los usuarios se crean con estado ACTIVO
        user.setUserState(
                UserEntity.UserState.ACTIVE
        );

        // Los usuarios inician con 0 reservas pagadas
        user.setPaidReservations(0);

        // Se guarda el usuario creado en la base de datos
        return userRepository.save(user);
    }

    // =========================================
    // READ
    // =========================================

    public List<UserEntity> getAllUsers() {

        return userRepository.findAll();
    }

    /*
     * updateUser
     * Descripcion: Permite actualizar telefono, dni o nacionalidad de un usuario
     * Entrada: ID del usuario a modificar y los nuevos datos enviados del frontend
     * Salida: Entidad de usuario
     * */
    @Transactional
    public UserEntity updateUser(Long id, UpdateUserProfileDTO dto) {

        // Busca al usuario en la base de datos mediante el id
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Solo actualizamos campos del perfil (NO identidad Keycloak)
        existingUser.setPhone(dto.getPhone());
        existingUser.setDni(dto.getDni());
        existingUser.setNationality(dto.getNationality());

        // Guardar cambios en la base de datos
        return userRepository.save(existingUser);
    }

    /*
     * deactivateUser
     * Descripcion: Deja un usuario con estado INACTIVO
     * Entrada: ID del usuario a modificar
     * Salida: Entidad de usuario
     * */
    @Transactional
    public UserEntity deactivateUser(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Evita trabajo innecesario
        if (user.getUserState() == UserEntity.UserState.INACTIVE) {
            return user;
        }

        // Baja lógica de la cuenta
        user.setUserState(UserEntity.UserState.INACTIVE);

        // Desactiva el usuario en keycloak
        keycloakAdminService.setUserEnabled(user.getKeycloakId(), false);

        // Guarda en la base de datos
        return userRepository.saveAndFlush(user);
    }

    /*
     * activateUser
     * Descripcion: Deja un usuario con estado ACTIVO
     * Entrada: ID del usuario a modificar
     * Salida: Entidad de usuario
     * */
    @Transactional
    public UserEntity activateUser(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar estado del usuario
        user.setUserState(UserEntity.UserState.ACTIVE);

        // Activar usuario en keycloak
        keycloakAdminService.setUserEnabled(user.getKeycloakId(), true);

        // Guardar cambios en la base de datos
        return userRepository.saveAndFlush(user);
    }
}