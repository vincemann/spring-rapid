package com.naturalprogrammer.spring.lemon.auth.config;

import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;

@ServiceConfig
@EnableAsync
@Slf4j
public class LemonAsyncAutoConfiguration {
    public LemonAsyncAutoConfiguration() {
        log.info("Created");
    }
}
