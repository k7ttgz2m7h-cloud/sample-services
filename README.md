# Stepflow Sample Services

Spring Boot provider services for testing the framework's `end-to-end-order-process.yaml` workflow.

These are demo services only. They use Spring Web, Spring Data JPA, H2, and actuator health endpoints. There is no Kafka, Docker, Redis, security, or external database.

## Requirements

- Java 17 or later
- Maven 3.6 or later

## Ports

Ports match `business-process-service-registry/src/main/resources/services-registry.yml` in the framework repository.

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

From this directory, run services individually as needed:

```bash
mvn -pl order-service spring-boot:run
mvn -pl payment-service spring-boot:run
mvn -pl customer-service spring-boot:run
mvn -pl inventory-service spring-boot:run
mvn -pl pricing-service spring-boot:run
mvn -pl fraud-service spring-boot:run
mvn -pl fulfillment-service spring-boot:run
mvn -pl shipping-service spring-boot:run
mvn -pl notification-service spring-boot:run
```

Build and test all services:

```bash
mvn clean test
```

## Order Creation

The workflow creates an order with `clientRequestId` and `customerId`. Do not send an `orderId` in the create request. `order-service` generates it and returns the same order when a request is retried with the same `clientRequestId`.

```json
{
  "clientRequestId": "REQ-1001",
  "customerId": "CUST-1"
}
```

## Workflow Note

The services expose the API paths used by `end-to-end-order-process.yaml`. The current end-to-end order workflow does not execute notification steps; `notification-service` remains available as an independent sample service for future workflows.

## Centralized logging

Before launching services individually from this directory in Bash, run
`source ../business-process-framework/stepflow-env.sh`. This resolves the shared
log folder from the checkout location, even when you later change directories.
`start-all.sh` sets this automatically. For IDE launches, set `STEPFLOW_LOG_DIR`
to `<stepflow>/temp/stepflow-logs` in the run configuration.

Each service writes ECS JSON to
`${STEPFLOW_LOG_DIR:-../temp/stepflow-logs}/<service-name>.json`, with its identity in
`service.name`. Restart services after configuration changes. Console logging is
also retained. Files rotate at 10 MB, with seven days / 100 MB of archives per
service.

Use the shared `logstash.conf` in the sibling `business-process-framework`
repository to collect all services into Elasticsearch. Its
`docs/centralized-logging.md` documents setup and search examples. All services
use Spring Boot 4.1.1 built-in ECS logging, configured entirely in
`application.yml`; no custom Logback XML or encoder dependency is needed.

The services retain their Java 17 compilation target. Swagger/OpenAPI uses
springdoc 3.1.1 for Spring Boot 4 compatibility. Database-backed services include
Boot's `spring-boot-h2console` module to preserve `/h2-console/` support.

After an upgrade, run `mvn clean verify` from this repository before restarting
services so removed logging resources are not left in `target/classes`.
