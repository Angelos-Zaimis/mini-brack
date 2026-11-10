# ADR 0001: Flyway for schema migrations

- Status: Accepted
- Date: 2026-11-10
- Ticket: MIN-10

## Context

The schema will keep changing (new columns, renames, data fixes). Every environment (laptop, tests, production) has to
get the same changes, in the same order, exactly once, and we need to see what ran and when.

## Decision

- **Flyway** with plain SQL files in `services/<service>/src/main/resources/db/migration`. Each service owns its schema.
- In Spring Boot 4 this needs `spring-boot-starter-flyway` (Flyway's auto-configuration is no longer included with
  `flyway-core` alone) plus `flyway-database-postgresql`.
- Hibernate runs with `ddl-auto: validate`: it checks entities against the schema and never changes it.

## Rules

- Naming: `V<int>__<description>.sql`. Integer versions are fine while one branch is in flight at a time; switch to
  timestamps if parallel branches start clashing.
- An applied migration is never edited. Flyway's checksums enforce this. Mistakes are fixed with a new migration.
  `flyway repair` is only for local accidents.
- Changes to tables that are in use follow **expand/contract** (add, dual-write, backfill, switch, drop later), so old
  and new app versions both work during a rolling deploy.
- `clean` stays disabled.

## Alternatives

- `ddl-auto: update`: no history, can't rename or migrate data, environments drift. Not used anywhere.
- Liquibase: more features (free rollback scripts, XML/YAML changelogs), but more to learn. We roll forward instead of
  rolling back, so plain SQL with Flyway is enough.
