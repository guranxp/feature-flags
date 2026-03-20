package com.guranxp.featureflags;

import java.time.LocalDate;

/**
 * Example of a project-specific feature flag enum.
 *
 * <p>Each constant declares:
 * <ul>
 *   <li>the flag key — matches the key in {@code feature-flags.properties}</li>
 *   <li>the date the flag was introduced — used to detect flags that have overstayed their welcome</li>
 *   <li>the flag type — communicates the intended lifetime of the flag</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 *     // Once at startup (e.g. main method or Spring @PostConstruct):
 *     ExampleFeature.configure(new InMemoryFeatureFlags(Collections.emptyMap()));
 *
 *     // Anywhere in the codebase — default value defined on the enum constant:
 *     if (ExampleFeature.NEW_DASHBOARD.isEnabled()) {
 *         showNewDashboard();
 *     }
 * </pre>
 *
 * <p>Copy this class into your own project and rename it to {@code Feature}.
 */
enum ExampleFeature {

    /** Gradual rollout of the redesigned dashboard. Remove after full rollout. */
    NEW_DASHBOARD("feature.new-dashboard", false, LocalDate.of(2026, 3, 20)),

    /** Kill switch for the external payment provider integration. Defaults to on. */
    PAYMENT_PROVIDER("feature.payment-provider", true, LocalDate.of(2026, 3, 20), FlagType.OPERATIONAL),

    /** A/B test for the new onboarding flow. Remove after experiment concludes. */
    ONBOARDING_V2("feature.onboarding-v2", false, LocalDate.of(2026, 3, 20), FlagType.EXPERIMENT),

    /** Controls access to the beta reporting module. */
    BETA_REPORTS("feature.beta-reports", false, LocalDate.of(2026, 3, 20), FlagType.PERMISSION);

    private static FeatureFlags featureFlags;

    public static void configure(final FeatureFlags flags) {
        featureFlags = flags;
    }

    private final String key;
    private final boolean defaultValue;
    private final LocalDate createdAt;
    private final FlagType type;

    ExampleFeature(final String key, final boolean defaultValue, final LocalDate createdAt) {
        this(key, defaultValue, createdAt, FlagType.RELEASE);
    }

    ExampleFeature(final String key, final boolean defaultValue, final LocalDate createdAt, final FlagType type) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.createdAt = createdAt;
        this.type = type;
    }

    public boolean isEnabled() {
        return featureFlags.isEnabled(key, defaultValue);
    }

    public String key() {
        return key;
    }

    public LocalDate createdAt() {
        return createdAt;
    }

    public FlagType type() {
        return type;
    }
}