package com.github.vincemann.springrapid.authdemo.config;

import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@WebConfig
public class RequestLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
                = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setIncludeClientInfo(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}
