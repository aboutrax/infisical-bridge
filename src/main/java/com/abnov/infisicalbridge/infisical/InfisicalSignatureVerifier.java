package com.abnov.infisicalbridge.infisical;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

/**
 * Service to verify Infisical webhook signatures
 */
@Component
@Slf4j
public class InfisicalSignatureVerifier {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final int DEFAULT_TOLERANCE_SECONDS = 300; // 5 minutes

    /**
     * Verifies the signature of an Infisical webhook request
     * 
     * @param payload         The raw request body as string
     * @param signatureHeader The value of x-infisical-signature header
     * @param secretKey       Your secret key from Infisical
     * @return true if signature is valid, false otherwise
     */
    public boolean verifySignature(String payload, String signatureHeader, String secretKey) {
        return verifySignature(payload, signatureHeader, secretKey, DEFAULT_TOLERANCE_SECONDS);
    }

    /**
     * Verifies the signature with custom tolerance
     * 
     * @param payload            The raw request body as string
     * @param signatureHeader    The value of x-infisical-signature header (format:
     *                           t=<timestamp>;<signature>)
     * @param secretKey          Your secret key from Infisical
     * @param toleranceInSeconds Time tolerance for replay attacks
     * @return true if signature is valid, false otherwise
     */
    public boolean verifySignature(String payload, String signatureHeader, String secretKey, int toleranceInSeconds) {
        try {
            // Parse the signature header: t=<timestamp>;<signature>
            String[] parts = signatureHeader.split(";");
            if (parts.length != 2) {
                log.error("Invalid signature header format");
                return false;
            }

            String timestamp = parts[0].substring(2); // Remove "t="
            String receivedSignature = parts[1];

            // Check timestamp to prevent replay attacks
            long webhookTime = Long.parseLong(timestamp);
            long currentTime;

            // Detect if timestamp is in milliseconds or seconds
            if (timestamp.length() > 10) {
                // Timestamp is in milliseconds
                currentTime = Instant.now().toEpochMilli();
                long toleranceInMillis = (long) toleranceInSeconds * 1000;
                if (currentTime - webhookTime > toleranceInMillis) {
                    log.error("Webhook timestamp is too old");
                    return false;
                }
            } else {
                // Timestamp is in seconds
                currentTime = Instant.now().getEpochSecond();
                if (currentTime - webhookTime > toleranceInSeconds) {
                    log.error("Webhook timestamp is too old");
                    return false;
                }
            }

            // Try different signature formats that Infisical might use

            // Format 1: timestamp.payload (most common)
            String signedPayload1 = timestamp + "." + payload;
            String expectedSignature1 = generateHmacSHA256(signedPayload1, secretKey);

            if (MessageDigest.isEqual(
                    receivedSignature.getBytes(StandardCharsets.UTF_8),
                    expectedSignature1.getBytes(StandardCharsets.UTF_8))) {
                return true;
            }

            // Format 2: payload only
            String expectedSignature2 = generateHmacSHA256(payload, secretKey);

            if (MessageDigest.isEqual(
                    receivedSignature.getBytes(StandardCharsets.UTF_8),
                    expectedSignature2.getBytes(StandardCharsets.UTF_8))) {
                return true;
            }

            // Format 3: t=timestamp.payload (with t= prefix)
            String signedPayload3 = signatureHeader.split(";")[0] + "." + payload;
            String expectedSignature3 = generateHmacSHA256(signedPayload3, secretKey);

            if (MessageDigest.isEqual(
                    receivedSignature.getBytes(StandardCharsets.UTF_8),
                    expectedSignature3.getBytes(StandardCharsets.UTF_8))) {
                return true;
            }

            // No format matched
            log.error("Signature verification failed - no matching format found");
            return false;

        } catch (Exception e) {
            log.error("Error verifying signature: {}" + e.getMessage());
            return false;
        }
    }

    /**
     * Generates HMAC SHA256 signature
     */
    private String generateHmacSHA256(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256);
        mac.init(secretKeySpec);

        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmacBytes);
    }

    /**
     * Converts byte array to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
