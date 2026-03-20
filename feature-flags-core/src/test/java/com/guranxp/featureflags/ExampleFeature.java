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
 *     FeatureFlags flags = new InMemoryFeatureFlags(Collections.emptyMap());
 *
 *     if (ExampleFeature.NEW_DASHBOARD.isEnabled(flags)) {
 *         showNewDashboard();
 *     }
 * </pre>
 *
 * <p>Copy this class into your own project and rename it to {@code Feature}.
 */
enum ExampleFeature {

    /** Gradual rollout of the redesigned dashboard. Remove after full rollout. */
    NEW_DASHBOARD("feature.new-dashboard", LocalDate.of(2026, 3, 20)),

    /** Kill switch for the external payment provider integration. */
    PAYMENT_PROVIDER("feature.payment-provider", LocalDate.of(2026, 3, 20), FlagType.OPERATIONAL),

    /** A/B test for the new onboarding flow. Remove after experiment concludes. */
    ONBOARDING_V2("feature.onboarding-v2", LocalDate.of(2026, 3, 20), FlagType.EXPERIMENT),

    /** Controls access to the beta reporting module. */
    BETA_REPORTS("feature.beta-reports", LocalDate.of(2026, 3, 20), FlagType.PERMISSION);

    private final String key;
    private final LocalDate createdAt;
    private final FlagType type;

    ExampleFeature(final String key, final LocalDate createdAt) {
        this(key, createdAt, FlagType.RELEASE);
    }

    ExampleFeature(final String key, final LocalDate createdAt, final FlagType type) {
        this.key = key;
        this.createdAt = createdAt;
        this.type = type;
    }

    public boolean isEnabled(final FeatureFlags flags) {
        return flags.isEnabled(key);
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