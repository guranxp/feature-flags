package com.guranxp.featureflags.spring;

import com.guranxp.featureflags.FeatureFlags;
import org.springframework.core.env.Environment;

/**
 * Spring implementation of {@link FeatureFlags} backed by Spring's {@link Environment}.
 *
 * <p>Reads flags from the full Spring property resolution chain (highest priority first):
 * <ol>
 *   <li>System properties</li>
 *   <li>Environment variables</li>
 *   <li>{@code application.properties} / {@code application.yml}</li>
 *   <li>Profile-specific property files</li>
 * </ol>
 *
 * <p>This makes it consistent with {@code @ConditionalOnProperty} — both read from the same source.
 */
public final class SpringFeatureFlags implements FeatureFlags {

    private final Environment env;

    public SpringFeatureFlags(final Environment env) {
        this.env = env;
    }

    @Override
    public boolean isEnabled(final String toggleName) {
        return isEnabled(toggleName, false);
    }

    @Override
    public boolean isEnabled(final String toggleName, final boolean defaultValue) {
        return env.getProperty(toggleName, Boolean.class, defaultValue);
    }
}
