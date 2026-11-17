package com.upwind.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Test configuration (application URLs, credentials).
 * <p>
 * A value is resolved in this order, the first non-blank one wins:
 * <ol>
 *     <li>JVM system property, e.g. {@code -Dapi.baseUrl=https://...}</li>
 *     <li>environment variable, e.g. {@code API_BASE_URL=https://...}</li>
 *     <li>{@code test.properties} on the test classpath (defaults)</li>
 * </ol>
 * Browser settings are not here: Selenide reads them itself from {@code selenide.properties}.
 */
public final class TestConfig {

    private static final String DEFAULTS_FILE = "test.properties";
    private static final Properties DEFAULTS = loadDefaults();

    private TestConfig() {
    }

    public static Credentials uiUser() {
        return new Credentials(get("ui.username"), get("ui.password"));
    }

    public static String apiBaseUrl() {
        return get("api.baseUrl");
    }

    public static Credentials apiAdmin() {
        return new Credentials(get("api.username"), get("api.password"));
    }

    static String get(String key) {
        String systemProperty = System.getProperty(key);
        if (isPresent(systemProperty)) {
            return systemProperty;
        }
        String environmentVariable = System.getenv(toEnvironmentVariableName(key));
        if (isPresent(environmentVariable)) {
            return environmentVariable;
        }
        String defaultValue = DEFAULTS.getProperty(key);
        if (isPresent(defaultValue)) {
            return defaultValue;
        }
        throw new IllegalStateException("Missing configuration value for key '" + key + "'");
    }

    /** {@code api.baseUrl} -> {@code API_BASE_URL}. */
    static String toEnvironmentVariableName(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1_$2")
                .replace('.', '_')
                .toUpperCase();
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    private static Properties loadDefaults() {
        Properties properties = new Properties();
        try (InputStream stream = TestConfig.class.getClassLoader().getResourceAsStream(DEFAULTS_FILE)) {
            if (stream == null) {
                throw new IllegalStateException(DEFAULTS_FILE + " not found on the classpath");
            }
            properties.load(stream);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + DEFAULTS_FILE, e);
        }
        return properties;
    }
}
