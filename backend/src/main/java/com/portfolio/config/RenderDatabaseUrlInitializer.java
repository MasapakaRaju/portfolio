package com.portfolio.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RenderDatabaseUrlInitializer implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(RenderDatabaseUrlInitializer.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String springDatasourceUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        String databaseUrl = environment.getProperty("DATABASE_URL");
        String configuredUrl = hasText(springDatasourceUrl) ? springDatasourceUrl : databaseUrl;

        if (!hasText(configuredUrl) || configuredUrl.regionMatches(true, 0, "jdbc:postgresql:", 0, "jdbc:postgresql:".length())) {
            return;
        }

        DatabaseConnection connection = parseDatabaseUrl(configuredUrl);
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.datasource.url", connection.jdbcUrl());

        if (!hasText(environment.getProperty("SPRING_DATASOURCE_USERNAME"))) {
            properties.put("spring.datasource.username", connection.username());
        }
        if (!hasText(environment.getProperty("SPRING_DATASOURCE_PASSWORD"))) {
            properties.put("spring.datasource.password", connection.password());
        }

        environment.getPropertySources().addFirst(new MapPropertySource("renderDatabaseUrl", properties));
        String source = hasText(springDatasourceUrl) ? "SPRING_DATASOURCE_URL" : "DATABASE_URL";
        log.info("Converted {} from PostgreSQL URL format to JDBC URL (host={})", source, connection.host());
    }

    static DatabaseConnection parseDatabaseUrl(String databaseUrl) {
        URI uri;
        try {
            uri = URI.create(databaseUrl);
        } catch (IllegalArgumentException exception) {
            // Do not attach the parser exception: it may include the URL and its credentials.
            throw new IllegalArgumentException("Database URL is malformed");
        }

        String scheme = uri.getScheme();
        if (!"postgres".equalsIgnoreCase(scheme) && !"postgresql".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("Database URL must use postgres:// or postgresql:// format");
        }
        if (!hasText(uri.getHost()) || !hasText(uri.getRawPath())) {
            throw new IllegalArgumentException("Database URL must include a host and database name");
        }

        String rawUserInfo = uri.getRawUserInfo();
        int separator = rawUserInfo == null ? -1 : rawUserInfo.indexOf(':');
        String username = separator < 0 ? decode(rawUserInfo) : decode(rawUserInfo.substring(0, separator));
        String password = separator < 0 ? "" : decode(rawUserInfo.substring(separator + 1));
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String query = uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery();
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getRawPath() + query;

        return new DatabaseConnection(jdbcUrl, username, password, uri.getHost());
    }

    private static String decode(String value) {
        if (value == null) {
            return "";
        }
        return URLDecoder.decode(value.replace("+", "%2B"), StandardCharsets.UTF_8);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    record DatabaseConnection(String jdbcUrl, String username, String password, String host) {
    }
}
