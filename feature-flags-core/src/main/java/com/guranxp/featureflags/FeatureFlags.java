package com.guranxp.featureflags;

public interface FeatureFlags {

    boolean isEnabled(String toggleName);

    boolean isEnabled(String toggleName, boolean defaultValue);
}
