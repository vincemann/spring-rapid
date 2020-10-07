package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.controller.owner.LemonOwnerLocator;
import com.github.vincemann.springlemon.auth.handler.LemonAuthenticationSuccessHandler;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.auth.service.token.AuthHeaderHttpTokenService;
import com.github.vincemann.springlemon.auth.service.token.HttpTokenService;
import com.github.vincemann.springrapid.core.config.RapidControllerAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@WebConfig
@Slf4j
//we want to override the OwnerLocator
@AutoConfigureBefore({RapidControllerAutoConfiguration.class})
public class LemonWebAutoConfiguration {

    public LemonWebAutoConfiguration() {

    }


    @Bean
    @ConditionalOnMissingBean(HttpTokenService.class)
    public HttpTokenService httpTokenService(){
        return new AuthHeaderHttpTokenService();
    }

    @Bean
    @ConditionalOnMissingBean(name = "lemonOwnerLocator")
    public OwnerLocator lemonOwnerLocator(UserService userService){
        return new LemonOwnerLocator(userService);
    }

    /**
     * Configures AuthenticationSuccessHandler if missing
     */
    @Bean
    @ConditionalOnMissingBean(LemonAuthenticationSuccessHandler.class)
    public LemonAuthenticationSuccessHandler authenticationSuccessHandler(UserService<?, ?> userService) {

        log.info("Configuring AuthenticationSuccessHandler");
        return new LemonAuthenticationSuccessHandler(userService, httpTokenService());
    }

    /**
     * Configures AuthenticationFailureHandler if missing
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler authenticationFailureHandler() {

        log.info("Configuring SimpleUrlAuthenticationFailureHandler");
        return new SimpleUrlAuthenticationFailureHandler();
    }

    /**
     * Configures LemonSecurityConfig if missing
     */
    @Bean
    @ConditionalOnMissingBean(LemonWebSecurityConfig.class)
    public LemonWebSecurityConfig lemonWebSecurityConfig() {

        log.info("Configuring LemonWebSecurityConfig");
        return new LemonWebSecurityConfig();
    }

}
