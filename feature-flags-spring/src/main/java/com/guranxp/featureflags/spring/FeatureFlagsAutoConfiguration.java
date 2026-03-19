package com.guranxp.featureflags.spring;

import com.guranxp.featureflags.FeatureFlags;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@AutoConfiguration
public class FeatureFlagsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FeatureFlags featureFlags(final Environment env) {
        return new SpringFeatureFlags(env);
    }
}
