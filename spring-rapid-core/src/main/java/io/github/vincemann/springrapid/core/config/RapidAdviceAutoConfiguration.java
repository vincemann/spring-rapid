package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.advice.log.LogComponentInteractionAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class RapidAdviceAutoConfiguration {

    public RapidAdviceAutoConfiguration() {
        log.info("Created");
    }

    @Bean
    @ConditionalOnMissingBean(LogComponentInteractionAdvice.class)
    public LogComponentInteractionAdvice logComponentInteractionAdvice(){
        return new LogComponentInteractionAdvice();
    }
}
