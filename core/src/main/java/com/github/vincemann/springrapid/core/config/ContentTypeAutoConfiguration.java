package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@WebConfig
@EnableWebMvc
//@ComponentScan
@Slf4j
public class ContentTypeAutoConfiguration implements WebMvcConfigurer {

    public ContentTypeAutoConfiguration() {
        log.info("Created");
    }

    @Override
    public void configureContentNegotiation (ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON_UTF8);
    }

}
