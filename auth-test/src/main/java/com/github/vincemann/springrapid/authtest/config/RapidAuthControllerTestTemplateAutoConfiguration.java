package com.github.vincemann.springrapid.authtest.config;

import com.github.vincemann.springrapid.authtest.UserControllerTestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RapidAuthControllerTestTemplateAutoConfiguration {

   @ConditionalOnMissingBean(name = "userControllerTestTemplate")
   @Bean
   public UserControllerTestTemplate userControllerTestTemplate(){
      return new UserControllerTestTemplate();
   }

}
