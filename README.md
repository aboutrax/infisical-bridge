# Infisical ↔ Dokploy Bridge

A Spring Boot (Java 21) application acting as a secure bridge between Infisical and Dokploy, enabling automated synchronization and deployment of secrets through APIs and webhooks.

---

## Features

- Secure integration with Infisical
- Automated updates via Dokploy API
- Webhook-driven synchronization
- Docker and Docker Compose ready

---

## Architecture Overview

Infisical  
↓ (Webhook / API)  
Infisical–Dokploy Bridge (Spring Boot)  
↓ (Dokploy API)  
Dokploy  

---

## Requirements

- Java 21
- Docker and Docker Compose
- Infisical account
- Dokploy instance with API access

---

## Environment Variables

### Infisical

- INFISICAL_API_URL: Base URL of Infisical API
- INFISICAL_CLIENT_ID: Infisical service client ID
- INFISICAL_CLIENT_SECRET: Infisical service client secret
- INFISICAL_WEBHOOK_SECRET: Webhook signature validation secret

### Dokploy

- DOKPLOY_API_URL: Base URL of Dokploy API
- DOKPLOY_API_KEY: Dokploy API key

---

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
---

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

---

## Infisical Webhook Configuration

When creating a webhook in Infisical, the following rules must be respected.

### Webhook URL format

${INFISICAL_API_URL}/webhook?dokployComposeId=${DOKPLOY_COMPOSE_ID}

- `dokployComposeId` must be the target Dokploy compose identifier
- This value is required and used to determine which Dokploy service is updated

### Webhook Secret

The webhook secret **must exactly match**:

${INFISICAL_WEBHOOK_SECRET}

Requests with an invalid or missing secret will be rejected.

---

## Webhooks Behavior

- Incoming webhook signatures are validated
- Secrets are fetched from Infisical
- Dokploy is updated using its API
- Invalid or unsigned requests are ignored

---

## Security Notes

- Secrets are never persisted
- Configuration is environment-driven
- HTTPS is recommended in production
- Restrict network access to trusted sources only

---

## Testing

./gradlew test

---

## Tech Stack

- Java 21
- Spring Boot
- Gradle (Kotlin DSL)
- Docker / Docker Compose

---

## License

MIT License
