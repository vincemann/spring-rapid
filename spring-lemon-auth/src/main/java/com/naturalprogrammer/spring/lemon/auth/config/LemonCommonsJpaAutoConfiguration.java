package com.naturalprogrammer.spring.lemon.auth.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@AutoConfigureBefore({LemonCommonsWebAutoConfiguration.class})
public class LemonCommonsJpaAutoConfiguration {

}
