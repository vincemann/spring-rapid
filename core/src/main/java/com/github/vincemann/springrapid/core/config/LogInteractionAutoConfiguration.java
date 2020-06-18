package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.advice.log.IndentingInteractionLogger;
import com.github.vincemann.springrapid.core.advice.log.InteractionLogger;
import com.github.vincemann.springrapid.core.advice.log.LogInteractionAdvice;
import com.github.vincemann.springrapid.log.nickvl.rapid.resolve.LogInteractionInfoResolver;
import com.github.vincemann.springrapid.log.nickvl.rapid.resolve.ProxyAwareLogInteractionInfoResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class LogInteractionAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public LogInteractionInfoResolver logInteractionInfoResolver(){
        return new ProxyAwareLogInteractionInfoResolver();
    }

    @ConditionalOnMissingBean(InteractionLogger.class)
    @Bean
    public InteractionLogger interactionLogger(){
        return new IndentingInteractionLogger();
    }

    @ConditionalOnMissingBean(LogInteractionAdvice.class)
    @Bean
    public LogInteractionAdvice logInteractionAdvice(){
        return new LogInteractionAdvice(interactionLogger(),logInteractionInfoResolver());
    }
}
