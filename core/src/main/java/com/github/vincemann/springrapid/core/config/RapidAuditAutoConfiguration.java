package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.model.audit.AuditTemplate;
import com.github.vincemann.springrapid.core.model.audit.AuditTemplateImpl;
import com.github.vincemann.springrapid.core.model.audit.LongIdSecurityAuditorAware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
public class RapidAuditAutoConfiguration {

    // you can overwrite existing autorAware, is done in auth modules autoconfig
    // if you need an other type than long, define a bean with name rapidSecurityAuditorAware with other generic parameter
    @ConditionalOnMissingBean(name = "rapidSecurityAuditorAware")
    @Bean
    public AuditorAware<Long> auditorAware(){
        return new LongIdSecurityAuditorAware();
    }

    @Bean
    @ConditionalOnMissingBean(AuditTemplate.class)
    public AuditTemplate auditTemplate(){
        return new AuditTemplateImpl();
    }
}
