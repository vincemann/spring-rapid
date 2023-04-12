package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;

@ServiceConfig
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
