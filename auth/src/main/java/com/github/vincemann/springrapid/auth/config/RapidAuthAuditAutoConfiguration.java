package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.core.model.audit.LongIdAuthAuditorAwareImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

@Configuration
// is enabled in core already
//@EnableJpaAuditing
public class RapidAuthAuditAutoConfiguration {

    /**
     * Configures an Auditor Aware if missing
     */
    @Bean
    @ConditionalOnMissingBean(name = "rapidAuthSecurityAuditorAware")
    public AuditorAware<Long> rapidSecurityAuditorAware() {
        return new LongIdAuthAuditorAwareImpl();
    }
}
