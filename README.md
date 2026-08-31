# Stepflow Sample Services

Tiny Spring Boot provider services for testing `end-to-end-order-flow.yaml`.

These are demo services only. They use Spring Web, Spring Data JPA, H2, and actuator health endpoints. There is no Kafka, Docker, Redis, security, or external database.

## Ports

Ports match `business-service-registry/src/main/resources/services-registry.yml`.

| Service | Port |
| --- | ---: |
| order-service | 8081 |
| payment-service | 8082 |
| customer-service | 8083 |
| inventory-service | 8084 |
| pricing-service | 8085 |
| fraud-service | 8086 |
| fulfillment-service | 8087 |
| shipping-service | 8088 |
| notification-service | 8089 |

## Run

From this directory:

```bash
./start-all.sh
```

Stop all:

```bash
./stop-all.sh
```

Or run one service at a time:

```bash
../mvnw -pl order-service spring-boot:run
../mvnw -pl payment-service spring-boot:run
../mvnw -pl customer-service spring-boot:run
../mvnw -pl inventory-service spring-boot:run
../mvnw -pl pricing-service spring-boot:run
../mvnw -pl fraud-service spring-boot:run
../mvnw -pl fulfillment-service spring-boot:run
../mvnw -pl shipping-service spring-boot:run
../mvnw -pl notification-service spring-boot:run
```

Compile all:

```bash
../mvnw test
```

## Note

The services expose the API paths expected by `end-to-end-order-flow.yaml`. The orchestrator still needs request payload mapping and path variable substitution to run the full multi-step workflow end to end.
