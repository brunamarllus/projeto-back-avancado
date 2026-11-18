package br.edu.uniesp.salgateria;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Sobe a aplicação com PostgreSQL real: aplica as migrations do Flyway e valida o mapeamento
 * das entidades contra o esquema (spring.jpa.hibernate.ddl-auto=validate). Requer Docker.
 */
@SpringBootTest
@Testcontainers
class SalgateriaApplicationIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Test
    void contextoCarregaEEsquemaCorrespondeAsEntidades() {
    }
}
