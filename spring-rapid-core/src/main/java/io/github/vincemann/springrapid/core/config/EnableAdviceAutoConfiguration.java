package io.github.vincemann.springrapid.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class EnableAdviceAutoConfiguration {

    public EnableAdviceAutoConfiguration() {
        log.info("Created");
    }
}
