package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.model.audit.LongIdSecurityAuditorAware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
public class RapidAuditAutoConfiguration {

    @ConditionalOnMissingBean(name = "rapidSecurityAuditorAware")
    @Bean
    public AuditorAware<Long> auditorAware(){
        return new LongIdSecurityAuditorAware();
    }

}
