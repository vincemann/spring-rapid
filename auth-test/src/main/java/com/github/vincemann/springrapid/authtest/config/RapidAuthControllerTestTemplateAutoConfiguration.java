package com.github.vincemann.springrapid.authtest.config;

import com.github.vincemann.springrapid.authtest.AbstractUserControllerTestTemplate;
import com.github.vincemann.springrapid.authtest.RapidUserControllerTestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RapidAuthControllerTestTemplateAutoConfiguration {

   @ConditionalOnMissingBean(AbstractUserControllerTestTemplate.class)
   @Bean
   public RapidUserControllerTestTemplate userControllerTestTemplate(){
      return new RapidUserControllerTestTemplate();
   }

}
