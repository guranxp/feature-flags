package com.forvity.featureflags;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFeatureFlagsTest {

    @AfterEach
    void clearSystemProps() {
        System.clearProperty("feature.test");
    }

    @Test
    void shouldReturnFalseWhenFlagNotDefined() {
        final FeatureFlags flags = new InMemoryFeatureFlags(Collections.emptyMap());

        assertThat(flags.isEnabled("feature.unknown")).isFalse();
    }

    @Test
    void shouldReturnDefaultValueWhenFlagNotDefined() {
        final FeatureFlags flags = new InMemoryFeatureFlags(Collections.emptyMap());

        assertThat(flags.isEnabled("feature.unknown", true)).isTrue();
    }

    @Test
    void shouldReturnCodeDefaultWhenFlagDefinedInCode() {
        final Map<String, Boolean> defaults = new HashMap<>();
        defaults.put("feature.test", true);
        final FeatureFlags flags = new InMemoryFeatureFlags(defaults);

        assertThat(flags.isEnabled("feature.test")).isTrue();
    }

    @Test
    void shouldPreferSystemPropertyOverCodeDefault() {
        System.setProperty("feature.test", "true");
        final Map<String, Boolean> defaults = new HashMap<>();
        defaults.put("feature.test", false);
        final FeatureFlags flags = new InMemoryFeatureFlags(defaults);

        assertThat(flags.isEnabled("feature.test")).isTrue();
    }

    @Test
    void shouldPreferSystemPropertyOverCallSiteDefault() {
        System.setProperty("feature.test", "true");
        final FeatureFlags flags = new InMemoryFeatureFlags(Collections.emptyMap());

        assertThat(flags.isEnabled("feature.test", false)).isTrue();
    }
}