package com.naturalprogrammer.spring.lemon.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
//@EnableJpaAuditing
@AutoConfigureBefore({LemonCommonsWebAutoConfiguration.class})
@Slf4j
public class LemonCommonsJpaAutoConfiguration {
    public LemonCommonsJpaAutoConfiguration() {
        log.info("Created");
    }
}
