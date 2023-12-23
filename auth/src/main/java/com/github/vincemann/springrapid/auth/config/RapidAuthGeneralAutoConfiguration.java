package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.service.captcha.CaptchaValidator;
import com.github.vincemann.springrapid.auth.service.context.AuthServiceCallContext;
import com.github.vincemann.springrapid.auth.util.UserUtils;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.config.RapidCrudControllerAutoConfiguration;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContext;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextFactory;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableConfigurationProperties
@AutoConfigureBefore(RapidCrudControllerAutoConfiguration.class)
public class RapidAuthGeneralAutoConfiguration {



    @ConfigurationProperties(prefix="rapid-auth")
    @ConditionalOnMissingBean(AuthProperties.class)
    @Bean
    public AuthProperties authProperties(CoreProperties coreProperties) {
        return new AuthProperties(coreProperties);
    }


    @Autowired
    public void configureServiceContextHolder(UserUtils userUtils){
        AuthServiceCallContext.setUserUtils(userUtils);
        ServiceCallContextHolder.initialize(new ServiceCallContextFactory() {
            @Override
            public ServiceCallContext create() {
                return new AuthServiceCallContext();
            }
        });
    }

    /**
     * Configures CaptchaValidator if missing
     */
    @Bean
    @ConditionalOnMissingBean(CaptchaValidator.class)
    public CaptchaValidator captchaValidator(AuthProperties properties) {
        return new CaptchaValidator(properties);
    }

}
