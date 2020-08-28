package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.github.vincemann.springrapid.core.controller.RapidController.MEDIA_TYPE_BEAN_NAME;

@WebConfig
@EnableWebMvc
//@ComponentScan
@Slf4j
public class RapidDefaultMediaTypeAutoConfiguration implements WebMvcConfigurer {

    public RapidDefaultMediaTypeAutoConfiguration() {
        log.info("Created");
    }

    @Bean(name = MEDIA_TYPE_BEAN_NAME)
    @ConditionalOnMissingBean(name = MEDIA_TYPE_BEAN_NAME)
    public String rapidDefaultMediaType(){
        return MediaType.APPLICATION_JSON_UTF8_VALUE;
    }

    @Override
    public void configureContentNegotiation (ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON_UTF8);
    }

}
