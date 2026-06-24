package com.travelagency.keycloak;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomEventListenerProvider implements EventListenerProvider {

    private static final Logger logger = Logger.getLogger(CustomEventListenerProvider.class.getName());
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // 1. Captura Eventos Interactivos en Pantalla
    @Override
    public void onEvent(Event event) {
        // Opción segura: Captura fallas de login recurrentes
        if (event.getType() == EventType.LOGIN_ERROR) {
            String errorDetail = event.getDetails() != null ? event.getDetails().get("error") : "";

            // Si coincide con cualquiera de los indicadores estándar de bloqueo de Keycloak
            if ("user_locked".equals(errorDetail) || "user_disabled".equals(errorDetail)) {
                String userId = event.getUserId();
                logger.info("¡Gatillado! Bloqueo interactivo detectado en login para ID: " + userId);
                sendLockoutToSpringBoot(userId);
            }
        }
    }

    // 2. Captura Mutaciones en el Modelo Administrativo (Bloqueo Automatizado Permanente)
    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        // Si la operación es una actualización (UPDATE) sobre un recurso de tipo USUARIO (USER)
        if (adminEvent.getOperationType() == OperationType.UPDATE &&
                adminEvent.getResourceType() == ResourceType.USER) {

            // El ID del usuario afectado viaja en la ruta del recurso administrativo (ej: "users/91956e04-...")
            String resourcePath = adminEvent.getResourcePath();
            if (resourcePath != null && resourcePath.startsWith("users/")) {
                String userId = resourcePath.replace("users/", "");

                logger.info("¡Gatillado! Mutación administrativa/fuerza bruta detectada sobre usuario ID: " + userId);
                sendLockoutToSpringBoot(userId);
            }
        }
    }

    private void sendLockoutToSpringBoot(String userId) {
        try {
            String jsonPayload = "{\"userId\":\"" + userId + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://nginx-backend:80/api/v1/internal/events/user-lockout"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            logger.info("Despachando webhook asíncrono al backend para sincronizar estado BLOCKED...");
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error crítico al enviar el evento de bloqueo al backend", e);
        }
    }

    @Override
    public void close() {}
}