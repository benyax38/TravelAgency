package com.travelagency.keycloak;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomEventListenerProviderFactory implements EventListenerProviderFactory {

    // OPTIMIZACIÓN: Mantenemos una única instancia en memoria para reusar el HttpClient
    private CustomEventListenerProvider instance;

    @Override
    public void init(Config.Scope config) {
        // Se ejecuta una sola vez cuando Keycloak carga el archivo .jar
        this.instance = new CustomEventListenerProvider();
    }

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        // Retornamos la instancia única existente. Ya no hacemos un "new" por cada evento.
        return this.instance;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {
        // Opcional: Si necesitas liberar recursos del proveedor al apagar el servidor
    }

    @Override
    public String getId() {
        // Sigue perfecto, este es el nombre técnico que buscarás en la pestaña "Events" -> "Config"
        return "travelagency-event-listener";
    }
}