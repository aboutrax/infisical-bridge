package com.abnov.infisicalbridge.dto;

public record ProjectResponse(
        String workspaceId,
        String projectId,
        String projectName,
        String environment,
        String secretPath) {

}
