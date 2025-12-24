package com.abnov.infisicalbridge.dokploy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DokployFeignConfig {

    private final DokployProperties properties;

    @Bean
    public RequestInterceptor dokployRequestInterceptor() {
        return requestTemplate -> {
            // Add API key to every request
            requestTemplate.header("x-api-key", properties.getApiKey());
        };
    }
}
