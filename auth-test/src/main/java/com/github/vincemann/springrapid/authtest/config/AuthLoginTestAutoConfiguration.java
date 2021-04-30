package com.github.vincemann.springrapid.authtest.config;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.authtest.controller.login.AuthITLoginTemplate;
import com.github.vincemann.springrapid.authtest.controller.login.AuthMockLoginTemplate;
import com.github.vincemann.springrapid.coretest.config.RapidLoginTestAutoconfiguration;
import com.github.vincemann.springrapid.coretest.login.MockLoginTemplate;
import com.github.vincemann.springrapid.coretest.slicing.WebTestConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebTestConfig
@AutoConfigureAfter(RapidLoginTestAutoconfiguration.class)
public class AuthLoginTestAutoConfiguration {

    @ConditionalOnMissingBean(name = "mockAuthLoginTemplate")
    @Bean
    public MockLoginTemplate<? extends AbstractUser> mockAuthLoginTemplate(){
        return new AuthMockLoginTemplate();
    }

    @ConditionalOnMissingBean(name = "itAuthLoginTemplate")
    @Bean
    public AuthITLoginTemplate itAuthLoginTemplate(){
        return new AuthITLoginTemplate();
    }

}
