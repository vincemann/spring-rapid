package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.auth.service.context.AuthServiceCallCacheAdvice;
import com.github.vincemann.springrapid.auth.service.context.AuthServiceCallContext;
import com.github.vincemann.springrapid.auth.service.context.AuthServiceCallContextFactory;
import com.github.vincemann.springrapid.core.config.RapidServiceAutoConfiguration;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContext;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextFactory;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@ServiceConfig
// overwrites beans of there
//@AutoConfigureAfter(RapidServiceAutoConfiguration.class)
public class RapidAuthServiceAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(ServiceCallContextFactory.class)
    public ServiceCallContextFactory serviceCallContextFactory(){
        return new AuthServiceCallContextFactory();
    }

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    @ConditionalOnMissingBean(AuthServiceCallContext.class)
    public AuthServiceCallContext authServiceCallContext(){
        return new AuthServiceCallContext();
    }


//    @Bean
////    @ConditionalOnMissingBean(name = "serviceCallCacheAdvice")
//    public AuthServiceCallCacheAdvice serviceCallCacheAdvice(){
//        return new AuthServiceCallCacheAdvice();
//    }


}
