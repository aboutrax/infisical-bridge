package com.abnov.infisicalbridge.infisical;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Data
@Configuration
@ConfigurationProperties(prefix = "infisical")
@Validated
public class InfisicalProperties {

    @NotBlank(message = "Infisical API URL is required")
    private String apiUrl;

    @NotBlank(message = "Infisical client ID is required")
    private String clientId;

    @NotBlank(message = "Infisical client secret is required")
    private String clientSecret;

    @NotBlank(message = "Infisical webhook secret is required")
    private String webhookSecret;
}
