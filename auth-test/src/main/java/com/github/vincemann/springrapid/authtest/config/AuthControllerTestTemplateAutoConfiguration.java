package com.github.vincemann.springrapid.authtest.config;

import com.github.vincemann.springrapid.authtest.controller.template.UserControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.slicing.WebTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebTestConfig
public class AuthControllerTestTemplateAutoConfiguration {

   @ConditionalOnMissingBean(name = "userControllerTestTemplate")
   @Bean
   public UserControllerTestTemplate userControllerTestTemplate(){
      return new UserControllerTestTemplate();
   }

}
