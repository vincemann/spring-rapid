package io.github.vincemann.springrapid.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class VersionPrintingConfig {
    public static final String VERSION = "1.0.role-dto";

    public VersionPrintingConfig() {
        log.info("Using Spring-Crud-Lib Version: " + VERSION);
    }
}
