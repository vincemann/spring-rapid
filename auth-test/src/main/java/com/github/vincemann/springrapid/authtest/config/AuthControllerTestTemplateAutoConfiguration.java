package com.github.vincemann.springrapid.authtest.config;

import com.github.vincemann.springrapid.authtest.controller.template.UserControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.config.RapidControllerTestTemplateAutoconfiguration;
import com.github.vincemann.springrapid.coretest.slicing.WebTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

@WebTestConfig
public class AuthControllerTestTemplateAutoConfiguration {

   @ConditionalOnMissingBean(name = "userControllerTestTemplate")
   @Autowired
   public UserControllerTestTemplate userControllerTestTemplate(){
      return new UserControllerTestTemplate();
   }

}
