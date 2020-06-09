package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.util.JpaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@Slf4j
public class RapidGeneralAutoConfiguration {

    @Autowired(required = false)
    EntityManager entityManager;

    public RapidGeneralAutoConfiguration() {
        log.info("Created");
    }

    @Bean
    @ConditionalOnMissingBean(JpaUtils.class)
    public JpaUtils jpaUtils(){
        return new JpaUtils(entityManager);
    }
}
