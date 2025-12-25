# Infisical ↔ Dokploy Bridge

A Spring Boot (Java 21) application acting as a secure bridge between Infisical and Dokploy, enabling automated synchronization and deployment of secrets through APIs and webhooks.

## Features

- Secure integration with Infisical
- Automated updates via Dokploy API
- Webhook-driven synchronization
- Docker and Docker Compose ready

## Architecture Overview

Infisical  
↓ (Webhook / API)  
Infisical–Dokploy Bridge (Spring Boot)  
↓ (Dokploy API)  
Dokploy  

## Requirements

- Java 21
- Docker and Docker Compose
- Infisical account
- Dokploy instance with API access

## Environment Variables

### Infisical

- INFISICAL_API_URL: Base URL of Infisical API
- INFISICAL_CLIENT_ID: Infisical service client ID
- INFISICAL_CLIENT_SECRET: Infisical service client secret
- INFISICAL_WEBHOOK_SECRET: Webhook signature validation secret

### Dokploy

- DOKPLOY_API_URL: Base URL of Dokploy API
- DOKPLOY_API_KEY: Dokploy API key

## Docker Compose

```txt
services:
  infisical-bridge:
    build: .
    restart: always
    environment:
      INFISICAL_API_URL: ${INFISICAL_API_URL}
      INFISICAL_CLIENT_ID: ${INFISICAL_CLIENT_ID}
      INFISICAL_CLIENT_SECRET: ${INFISICAL_CLIENT_SECRET}
      INFISICAL_WEBHOOK_SECRET: ${INFISICAL_WEBHOOK_SECRET}
      DOKPLOY_API_URL: ${DOKPLOY_API_URL}
      DOKPLOY_API_KEY: ${DOKPLOY_API_KEY}
```

## Running

With Docker Compose:

```sh
docker compose up -d --build
```

Local development:

```sh
./gradlew bootRun
```

Application runs on http://localhost:8080

Use a service like ngrok.

## Infisical Webhook Configuration

When creating a webhook in Infisical, the following rules must be respected.

### Webhook URL Formats

Infisical bridge supports two webhook URL formats, depending on the Dokploy resource you want to update.

#### Dokploy Compose Webhook

`${INFISICAL_API_URL}/webhook?dokployComposeId=${DOKPLOY_COMPOSE_ID}`

Parameters:
- dokployComposeId (required):
  The identifier of the target Dokploy Compose.
  This value is used to determine which Dokploy compose service should be updated when the webhook is triggered.

#### Dokploy Application Webhook

`${INFISICAL_API_URL}/webhook?dokployApplicationId=${DOKPLOY_APPLICATION_ID}`

Parameters:
- dokployApplicationId (required):
  The identifier of the target Dokploy Application.
  This value is used to determine which Dokploy application should be updated when the webhook is triggered.

#### Notes
- Exactly one identifier must be provided per webhook URL.
- If no identifier or multiple identifiers are provided, the webhook request will be rejected.
- Ensure the provided ID matches an existing Dokploy resource.

### Webhook Secret

The webhook secret **must exactly match**:

`${INFISICAL_WEBHOOK_SECRET}`

Requests with an invalid or missing secret will be rejected.

## Webhooks Behavior

- Incoming webhook signatures are validated
- Secrets are fetched from Infisical
- Dokploy is updated using its API
- Invalid or unsigned requests are ignored

## Security Notes

- Secrets are never persisted
- Configuration is environment-driven
- HTTPS is recommended in production
- Restrict network access to trusted sources only

## Testing

```sh
./gradlew test
```

## Tech Stack

- Java 21
- Spring Boot
- Gradle (Kotlin DSL)
- Docker / Docker Compose

## License

MIT License
