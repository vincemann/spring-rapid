package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.config.layers.config.WebConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@WebConfig
@EnableWebMvc
@ComponentScan
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation (ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

}
