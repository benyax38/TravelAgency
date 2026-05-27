package com.example.demo.Service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakAdminService {

    private final Keycloak keycloak;

    public KeycloakAdminService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Value("${keycloak.realm}")
    private String realm;

    public void setUserEnabled(String keycloakUserId, boolean enabled) {

        UserResource userResource = keycloak
                .realm(this.realm)
                .users()
                .get(keycloakUserId);

        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(enabled);
        userResource.update(user);
    }

}
