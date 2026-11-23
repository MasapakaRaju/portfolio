package com.portfolio.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RenderDatabaseUrlInitializerTest {

    private final RenderDatabaseUrlInitializer initializer = new RenderDatabaseUrlInitializer();

    @Test
    void convertsDatabaseUrlAndPreservesEncodedCredentialsAndQuery() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("DATABASE_URL", "postgresql://portfolio:p%2Bass%3Aword@db.example.com:5432/portfolio?sslmode=require");

        initializer.postProcessEnvironment(environment, new SpringApplication());

        assertEquals("jdbc:postgresql://db.example.com:5432/portfolio?sslmode=require",
                environment.getProperty("spring.datasource.url"));
        assertEquals("portfolio", environment.getProperty("spring.datasource.username"));
        assertEquals("p+ass:word", environment.getProperty("spring.datasource.password"));
    }

    @Test
    void convertsPostgresFormatFromSpringDatasourceUrl() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("SPRING_DATASOURCE_URL", "postgres://portfolio:secret@db.example.com/portfolio");

        initializer.postProcessEnvironment(environment, new SpringApplication());

        assertEquals("jdbc:postgresql://db.example.com:5432/portfolio",
                environment.getProperty("spring.datasource.url"));
        assertEquals("portfolio", environment.getProperty("spring.datasource.username"));
        assertEquals("secret", environment.getProperty("spring.datasource.password"));
    }

    @Test
    void leavesJdbcUrlAndExplicitCredentialsUnchanged() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("SPRING_DATASOURCE_URL", "jdbc:postgresql://db.example.com:5432/portfolio")
                .withProperty("SPRING_DATASOURCE_USERNAME", "configured-user")
                .withProperty("SPRING_DATASOURCE_PASSWORD", "configured-password");

        initializer.postProcessEnvironment(environment, new SpringApplication());

        assertEquals("jdbc:postgresql://db.example.com:5432/portfolio",
                environment.getProperty("SPRING_DATASOURCE_URL"));
        assertEquals("configured-user", environment.getProperty("SPRING_DATASOURCE_USERNAME"));
        assertEquals("configured-password", environment.getProperty("SPRING_DATASOURCE_PASSWORD"));
    }

    @Test
    void routesSupabaseSharedPoolerDatabaseUrlThroughTransactionMode() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("DATABASE_URL", "postgresql://postgres.project:p%2Bass@aws-0-ap-southeast-2.pooler.supabase.com:5432/postgres?sslmode=require");

        initializer.postProcessEnvironment(environment, new SpringApplication());

        assertEquals("jdbc:postgresql://aws-0-ap-southeast-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0",
                environment.getProperty("spring.datasource.url"));
        assertEquals("postgres.project", environment.getProperty("spring.datasource.username"));
        assertEquals("p+ass", environment.getProperty("spring.datasource.password"));
    }

    @Test
    void routesSupabaseJdbcUrlThroughTransactionModeAndAvoidsDuplicateParameter() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("SPRING_DATASOURCE_URL", "jdbc:postgresql://aws-0-ap-southeast-2.pooler.supabase.com:5432/postgres?sslmode=require&prepareThreshold=4");

        initializer.postProcessEnvironment(environment, new SpringApplication());

        assertEquals("jdbc:postgresql://aws-0-ap-southeast-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0",
                environment.getProperty("spring.datasource.url"));
    }
}
