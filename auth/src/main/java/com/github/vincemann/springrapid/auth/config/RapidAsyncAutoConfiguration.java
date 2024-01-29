package com.github.vincemann.springrapid.auth.config;

import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@Slf4j
/**
 * needed for async mail sending methods
 */
public class RapidAsyncAutoConfiguration {
    public RapidAsyncAutoConfiguration() {
        log.info("asny enabled");
    }
}
