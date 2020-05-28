package io.github.vincemann.springlemon.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.springlemon.auth.controller.LemonCurrentUserIdProvider;
import io.github.vincemann.springlemon.auth.properties.LemonProperties;
import io.github.vincemann.springlemon.auth.security.config.LemonJpaSecurityConfig;
import io.github.vincemann.springlemon.auth.security.config.LemonWebSecurityConfig;
import io.github.vincemann.springlemon.auth.security.handlers.LemonAuthenticationSuccessHandler;
import io.github.vincemann.springlemon.auth.service.LemonService;
import io.github.vincemann.springrapid.core.controller.rapid.CurrentUserIdProvider;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@WebConfig
@Slf4j
@AutoConfigureBefore({LemonAutoConfiguration.class})
public class LemonWebAutoConfiguration {

    public LemonWebAutoConfiguration() {
        log.info("Created");
    }


    /**
     * Configures AuthenticationSuccessHandler if missing
     */
    @Bean
    @ConditionalOnMissingBean(LemonAuthenticationSuccessHandler.class)
    public LemonAuthenticationSuccessHandler authenticationSuccessHandler(
            ObjectMapper objectMapper, LemonService<?, ?,?> lemonService, LemonProperties properties) {

        log.info("Configuring AuthenticationSuccessHandler");
        return new LemonAuthenticationSuccessHandler(objectMapper, lemonService, properties);
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
    public LemonWebSecurityConfig lemonSecurityConfig() {

        log.info("Configuring LemonJpaSecurityConfig");
        return new LemonJpaSecurityConfig();
    }

    @Bean
    @ConditionalOnMissingBean(CurrentUserIdProvider.class)
    public CurrentUserIdProvider currentUserIdProvider(LemonService<?,?,?> lemonService){
        return new LemonCurrentUserIdProvider(lemonService);
    }
}
