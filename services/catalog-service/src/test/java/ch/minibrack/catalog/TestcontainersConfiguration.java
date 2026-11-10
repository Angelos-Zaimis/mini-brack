package ch.minibrack.catalog;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Throwaway Postgres for tests. {@link ServiceConnection} registers its connection details,
 * so no datasource properties are needed.
 */
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        // keep in sync with infra/compose.yaml
        return new PostgreSQLContainer("postgres:17");
    }
}
