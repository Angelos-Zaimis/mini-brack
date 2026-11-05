# MiniBrack

Learning project: a small e-commerce catalog + flash-deal platform modelled on a Swiss online retailer.

Stack: Java 25, Spring Boot 4, PostgreSQL, Kafka, Solr, Airflow, nginx.

## Modules
- `services/catalog-service`: products, categories, prices, stock (source of truth)

Layout and build conventions: see [ADR 0000](docs/adr/0000-project-structure.md).

## Build & run
```bash
./gradlew build            # compile, test, spotlessCheck
./gradlew spotlessApply    # format
./gradlew :services:catalog-service:bootRun
curl localhost:8080/actuator/health
```

Decisions are recorded in `docs/adr/`.
