package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acl.service.sec.*;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecuredAuthServicesAutoConfiguration {

    @Root
    @Bean
    @ConditionalOnMissingBean(name = "contactInformationService")
    public ContactInformationService contactInformationService(){
        return new ContactInformationServiceImpl();
    }


    @Secured
    @Bean
    @ConditionalOnMissingBean(name = "securedContactInformationService")
    public ContactInformationService securedContactInformationService(@Root ContactInformationService service){
        return new SecuredContactInformationService(service);
    }

    @Secured
    @Bean
    @ConditionalOnMissingBean(name = "securedPasswordService")
    public PasswordService securedPasswordService(@Root PasswordService service){
        return new SecuredPasswordService(service);
    }

    @Secured
    @Bean
    @ConditionalOnMissingBean(name = "securedVerificationService")
    public VerificationService securedVerificationService(@Root VerificationService service){
        return new SecuredVerificationService(service);
    }

    @ConditionalOnMissingBean(name = "securedUserService")
    @Bean
    @Secured
    public UserService securedUserService(@Root UserService service) {
        return new SecuredUserService(service);
    }

}
