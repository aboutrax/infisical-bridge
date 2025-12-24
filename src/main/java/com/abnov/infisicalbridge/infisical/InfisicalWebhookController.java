package com.abnov.infisicalbridge.infisical;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class InfisicalWebhookController {
    private final InfisicalSignatureVerifier signatureVerifier;

    @PostMapping
    public void handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Infisical-Signature", required = false) String signature) {
        // Check if signature header is present
        if (signature == null || signature.isEmpty()) {
            log.error("Missing signature header");
            return;
        }

        // Verify the signature
        if (!signatureVerifier.verifySignature(payload, signature, "demoa")) {
            log.error("Invalid signature");
            return;
        }

        log.info("Webhook received and verified: {}", payload);
    }
}
