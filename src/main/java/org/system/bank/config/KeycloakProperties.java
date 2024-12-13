package org.system.bank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "keycloak")
@Component
@Data
public class KeycloakProperties {
    private String realm;
    private String authServerUrl;
    private String resource;
    private boolean publicClient;
    private boolean bearerOnly;
    private String sslRequired;
    private Credentials credentials;

    @Data
    public static class Credentials {
        private String secret;
    }
}