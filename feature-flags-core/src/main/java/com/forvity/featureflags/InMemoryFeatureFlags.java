package com.forvity.featureflags;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * In-memory implementation of {@link FeatureFlags}.
 *
 * <p>Flag values are resolved at construction time using the following priority (highest first):
 * <ol>
 *   <li>System properties ({@code -Dfeature.my-flag=true}) — checked live at each call</li>
 *   <li>{@code feature-flags.properties} on the classpath</li>
 *   <li>Code defaults passed to the constructor</li>
 * </ol>
 */
public final class InMemoryFeatureFlags implements FeatureFlags {

    private final Map<String, Boolean> flags;

    public InMemoryFeatureFlags(final Map<String, Boolean> defaults) {
        final Map<String, Boolean> resolved = new HashMap<>(defaults);
        loadFromFile().forEach(resolved::put);
        this.flags = Collections.unmodifiableMap(resolved);
    }

    @Override
    public boolean isEnabled(final String toggleName) {
        return isEnabled(toggleName, false);
    }

    @Override
    public boolean isEnabled(final String toggleName, final boolean defaultValue) {
        final String sysProp = System.getProperty(toggleName);
        if (sysProp != null) {
            return Boolean.parseBoolean(sysProp);
        }
        return flags.getOrDefault(toggleName, defaultValue);
    }

    private static Map<String, Boolean> loadFromFile() {
        final Map<String, Boolean> result = new HashMap<>();
        final InputStream is = InMemoryFeatureFlags.class.getClassLoader()
                .getResourceAsStream("feature-flags.properties");
        if (is == null) {
            return result;
        }
        try {
            final Properties props = new Properties();
            props.load(is);
            for (final Map.Entry<Object, Object> entry : props.entrySet()) {
                result.put((String) entry.getKey(), Boolean.parseBoolean((String) entry.getValue()));
            }
        } catch (final IOException e) {
            System.err.println("[feature-flags] Failed to load feature-flags.properties: " + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (final IOException ignored) {
            }
        }
        return result;
    }
}