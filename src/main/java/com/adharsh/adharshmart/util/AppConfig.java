package com.adharsh.adharshmart.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central config reader: config.properties on the classpath, falling back to environment
 * variables. Used for values outside the DataSource lifecycle (AI provider selection, API keys)
 * so those concerns don't leak into {@code DataSourceListener}.
 */
public final class AppConfig {

    private static final Properties PROPERTIES = load();

    private AppConfig() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
            // fall through to environment-variable-only resolution
        }
        return props;
    }

    public static String get(String key, String fallback) {
        if (PROPERTIES.containsKey(key)) {
            return PROPERTIES.getProperty(key);
        }
        String envValue = System.getenv(key.toUpperCase().replace('.', '_'));
        return envValue != null ? envValue : fallback;
    }
}
