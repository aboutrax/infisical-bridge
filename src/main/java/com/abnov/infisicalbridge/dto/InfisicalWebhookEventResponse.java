package com.abnov.infisicalbridge.dto;

public record InfisicalWebhookEventResponse(
        String event,
        ProjectResponse project,
        long timestamp) {
}
