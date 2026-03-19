package com.guranxp.featureflags;

public enum FlagType {

    /** Short-lived flag for branch-by-abstraction. Clean up after rollout. */
    RELEASE,

    /** Long-lived kill switch for operational control. */
    OPERATIONAL,

    /** Short-lived flag for A/B testing or experiments. */
    EXPERIMENT,

    /** Permanent flag for access control. */
    PERMISSION
}
