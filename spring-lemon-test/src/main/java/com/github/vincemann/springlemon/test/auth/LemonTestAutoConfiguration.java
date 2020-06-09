package com.github.vincemann.springlemon.test.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class LemonTestAutoConfiguration {

    //precedence over RapidMockAuthTemplate when MockAuthTemplate is requested
    @Primary
    @Bean
    @ConditionalOnMissingBean(LemonMockAuthenticationTemplate.class)
    public LemonMockAuthenticationTemplate lemonMockAuthenticationTemplate(){
        return new LemonMockAuthenticationTemplateImpl();
    }
}
