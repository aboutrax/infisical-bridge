package com.abnov.infisicalbridge.infisical;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abnov.infisicalbridge.dokploy.DokployClient;
import com.abnov.infisicalbridge.dto.DokployComposeUpdateRequest;
import com.abnov.infisicalbridge.dto.DokployComposeUpdateResponse;
import com.abnov.infisicalbridge.dto.InfisicalWebhookEventResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.models.Secret;
import com.infisical.sdk.util.InfisicalException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class InfisicalWebhookController {
    private final InfisicalSignatureVerifier signatureVerifier;
    private final InfisicalService service;
    private final InfisicalProperties infisicalProperties;
    private final ObjectMapper objectMapper;
    private final DokployClient dokployClient;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestParam String dokployComposeId,
            @RequestHeader(value = "X-Infisical-Signature", required = false) String signature)
            throws InfisicalException {

        if (signature == null || signature.isEmpty()) {
            log.warn("Missing X-Infisical-Signature header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!signatureVerifier.verifySignature(payload, signature, infisicalProperties.getWebhookSecret())) {
            log.warn("Invalid webhook signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        InfisicalWebhookEventResponse webhookEvent;
        try {
            webhookEvent = objectMapper.readValue(payload, InfisicalWebhookEventResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Invalid webhook payload", e);
            return ResponseEntity.badRequest().build();
        }

        InfisicalSdk sdk = service.getSdk();
        List<Secret> secrets = sdk.Secrets().ListSecrets(
                webhookEvent.project().projectId(),
                webhookEvent.project().environment(),
                webhookEvent.project().secretPath(),
                false,
                false,
                false);

        if (secrets.isEmpty()) {
            log.warn("No secrets found for project={} env={}",
                    webhookEvent.project().projectName(),
                    webhookEvent.project().environment());
            return ResponseEntity.noContent().build();
        }

        String envContent = secrets.stream()
                .map(s -> s.getSecretKey() + "=" + s.getSecretValue())
                .collect(Collectors.joining("\n"));

        try {
            dokployClient.updateCompose(
                    new DokployComposeUpdateRequest(dokployComposeId, envContent));
        } catch (Exception e) {
            log.error("Failed to update Dokploy compose {}", dokployComposeId, e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

        return ResponseEntity.ok().build();
    }
}
