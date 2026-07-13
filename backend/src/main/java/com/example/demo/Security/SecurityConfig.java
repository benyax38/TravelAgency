package com.example.demo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita el uso de @PreAuthorize en controladores
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        // OPTIONS para CORS (Siempre primero)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Permite cualquier metodo y cualquier sub-ruta dentro de ese path
                        .requestMatchers("/api/v1/internal/events/**").permitAll()

                        // Cualquier otra request
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    /*
     * jwtAuthenticationConverter
     * Descripcion:
     *  1. Extrae los roles a partir del token JWT
     *  2. Los convierte en GrantedAuthority
     *  3. Ahora Spring Security puede usar hasRole(...)
     * Entrada: void
     * Salida: JwtAuthenticationConverter
     * */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Crea un conversor estandar de spring
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Indica que los roles vienen desde el claim realm_access
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access");
        // Agrega el prefijo ROLE_ para que spring pueda entender los roles
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        // Se crea el conversor principal
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(token -> {
            // Obtiene realm_access
            Map<String, Object> realmAccess = token.getClaim("realm_access");

            // Validacion en caso de que el JWT venga sin roles o vacio
            if (realmAccess == null || realmAccess.isEmpty()) {
                return Collections.emptyList();
            }

            // Obtiene roles
            Object rolesObject = realmAccess.get("roles");
            Collection<String> roles = Collections.emptyList();

            // Se verifica formato correcto de roles y se transforma a List<String>
            if (rolesObject instanceof Collection<?>) {
                roles = ((Collection<?>) rolesObject).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .toList();
            }

            // Se convierte a GrantedAuthority
            return roles.stream()
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        });

        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ¡IMPORTANTE! El origen debe coincidir exactamente con lo que ves en el navegador
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
