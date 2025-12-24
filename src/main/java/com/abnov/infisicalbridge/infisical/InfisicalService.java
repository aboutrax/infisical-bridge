package com.abnov.infisicalbridge.infisical;

import org.springframework.stereotype.Service;

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.util.InfisicalException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InfisicalService {
    private final InfisicalProperties properties;

    @Getter
    private final InfisicalSdk sdk;

    public InfisicalService(InfisicalProperties properties) {
        this.properties = properties;
        this.sdk = initializeClient();
    }

    private InfisicalSdk initializeClient() {
        try {
            log.info("Initializing Infisical SDK");
            var sdkInstance = new InfisicalSdk(
                    new SdkConfig.Builder()
                            .withSiteUrl(properties.getApiUrl())
                            .build());

            sdkInstance.Auth().UniversalAuthLogin(
                    properties.getClientId(),
                    properties.getClientSecret());

            log.info("Successfully authenticated with Infisical");
            return sdkInstance;

        } catch (InfisicalException e) {
            log.error("Failed to initialize Infisical SDK", e);
            throw new IllegalStateException("Failed to initialize Infisical SDK", e);
        }
    }
}
