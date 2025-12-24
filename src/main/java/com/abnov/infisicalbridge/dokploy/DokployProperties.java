package com.abnov.infisicalbridge.dokploy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Data
@Configuration
@ConfigurationProperties(prefix = "dokploy")
@Validated
public class DokployProperties {

    @NotBlank(message = "Dokploy API URL is required")
    private String apiUrl;

    @NotBlank(message = "Dokploy API KEY is required")
    private String apiKey;
}
