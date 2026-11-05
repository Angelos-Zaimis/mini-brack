# ADR 0000: Project structure and build conventions

- Status: Accepted
- Date: 2026-11-05
- Ticket: MIN-73

## Context

MiniBrack will grow from a single `catalog-service` to several services (search, deal, order, payment) plus shared
libraries and infrastructure config. Without shared conventions, each module would copy and slowly change its own
toolchain, plugin versions, compiler flags and test setup. We want adding a new module to be a two-line change, and
every module to build, test and format the same way.

## Decision

### Repository layout

```
services/        deployable Spring Boot services (catalog-service, later search, deal, order, payment)
libs/            shared libraries (e.g. catalog-events)
infra/           docker compose, nginx, solr, airflow, grafana configs
build-logic/     convention plugins (included build)
gradle/          wrapper + libs.versions.toml
docs/adr/        architecture decision records
docs/notes/      learning notes
load/            k6 load tests
```

Gradle project paths follow the folders: `:services:catalog-service`, `:libs:<name>`.

### Version catalog

All versions live in `gradle/libs.versions.toml`. Module build files hold no version numbers. Libraries managed by
a BOM (Spring Boot, JUnit) are listed in the catalog without a version, so the BOM stays the single owner of those
versions.

### Convention plugins in `build-logic/`

`build-logic` is an included build (`pluginManagement { includeBuild("build-logic") }`) with precompiled Kotlin script
plugins:

- `minibrack.java-conventions`: group/version, Java toolchain from the catalog, `-parameters`, `-Xlint:all`,
  `-Werror` on main code only, JUnit 5 through `junit-bom`, Spotless with palantir-java-format.
- `minibrack.spring-service`: applies `java-conventions` and the Spring Boot plugin, and imports the Spring Boot BOM as
  a Gradle `platform(...)`.

A service build file is then just plugins plus its own dependencies:

```kotlin
plugins { id("minibrack.spring-service") }
dependencies { implementation(libs.spring.boot.starter.webmvc) }
```

Repositories are declared once in `settings.gradle.kts` with `RepositoriesMode.FAIL_ON_PROJECT_REPOS`.

### Dependency management: Gradle platform, not `io.spring.dependency-management`

Gradle's native `platform()` already imports BOMs. It is one less plugin and uses Gradle's normal conflict resolution,
and it stays consistent with how non-Spring modules get versions. We give up the plugin's property-based version
overrides (`ext["x.version"]`). If we need one, we use a dependency constraint instead.

### Formatting

Spotless with palantir-java-format (4-space indent, 120 columns, better lambda/stream layout than google-java-format).
`.editorconfig` covers non-Java files. `./gradlew spotlessApply` fixes and `spotlessCheck` (part of `check`) checks.

### Packages: by feature, not by layer

Inside a service, top-level packages are business features: `ch.minibrack.catalog.product`, `.category`, `.pricing`.
Each feature package holds all its classes (controller, service, mapper, repository, entity) side by side. There are
no `controller/`, `service/` or `repository/` packages.

## Why

**`build-logic` over `buildSrc`.** `buildSrc` is added to every build script's classpath, and any change to it
invalidates the configuration of the whole build. An included build is a normal build: it is cached and tested like
one, plugins are applied explicitly by id, and it can later move to its own repo. Gradle's own docs recommend it for
convention plugins.

**Convention plugins over `allprojects {}` / `subprojects {}`.** Those blocks inject config from the root into
projects that never asked for it. That makes projects depend on each other's configuration, breaks project isolation
and the configuration cache, and makes it hard to tell where a setting comes from. With convention plugins each
module states what it is (`plugins { id("minibrack.spring-service") }`), and modules that are not Spring services
just don't apply that plugin.

**Version catalog over hard-coded versions.** One file to update, type-safe accessors (`libs.x.y`) in the IDE, and
the catalog is shared with `build-logic` (its `settings.gradle.kts` imports the same TOML).

**Package by feature over package by layer.**
- Cohesion: a change to "pricing" touches one package, not five.
- Visibility: classes can be package-private, so only a feature's real API is public. Package by layer forces
  everything to be `public` so the controller package can see the service package.
- Splitting later: a feature package can become its own module or service (tickets 60/61) by moving one folder.

## Consequences

- New module = `include(":services:x")` in `settings.gradle.kts` + a 3-line `build.gradle.kts`.
- Upgrading Java, Boot or a tool is a one-line change in `libs.versions.toml`.
- Precompiled script plugins can't use the type-safe `libs` accessor, so convention plugins read the catalog by name
  (`libs.findVersion("java")`). This is a known Gradle limitation, and we accept it rather than use the
  `files(libs.javaClass...)` workaround.
- `-Werror` means a new compiler warning in main code fails CI. Suppress only with a targeted `@SuppressWarnings` and
  a reason.
