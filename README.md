# MiniBrack

Learning project: a small e-commerce catalog + flash-deal platform modelled on a Swiss online retailer.

Stack: Java 25, Spring Boot 4, PostgreSQL, Kafka, Solr, Airflow, nginx.

## Modules
- `catalog-service` — products, categories, prices, stock (source of truth)

## Run
```bash
./gradlew :catalog-service:bootRun
curl localhost:8080/actuator/health
```

Decisions are recorded in `docs/adr/`.
