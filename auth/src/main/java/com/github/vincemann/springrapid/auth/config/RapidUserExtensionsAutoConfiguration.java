package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.acl.config.RapidAclExtensionsAutoConfiguration;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acl.service.ext.acl.CleanUpAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.CrudAclChecksExtension;
import com.github.vincemann.springrapid.auth.service.SignupService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.ext.acl.SignupServiceAclExtension;
import com.github.vincemann.springrapid.auth.service.ext.sec.ContactInformationServiceSecurityExtension;
import com.github.vincemann.springrapid.auth.service.ext.sec.PasswordServiceSecurityExtension;
import com.github.vincemann.springrapid.auth.service.ext.sec.UserAuthTokenServiceSecurityExtension;
import com.github.vincemann.springrapid.auth.service.ext.sec.UserServiceSecurityExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.model.MutableAclService;


/**
 * Creates @{@link Acl} and @{@link Secured} versions (proxies) of {@link UserService}
 * with default security- and acl-extensions for user service.
 */
@Configuration
@Slf4j
//we need the acl beans here
@AutoConfigureAfter({RapidAclExtensionsAutoConfiguration.class})
public class RapidUserExtensionsAutoConfiguration {


    @Autowired
    RapidAclService permissionService;

    @Autowired
    MutableAclService mutableAclService;


    @ConditionalOnMissingBean(name = "userServiceSecurityExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public UserServiceSecurityExtension userServiceSecurityExtension() {
        return new UserServiceSecurityExtension();
    }


    @ConditionalOnMissingBean(name = "contactInformationServiceSecurityExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public ContactInformationServiceSecurityExtension contactInformationServiceSecurityExtension(){
        return new ContactInformationServiceSecurityExtension();
    }


    @ConditionalOnMissingBean(name = "passwordServiceSecurityExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public PasswordServiceSecurityExtension passwordServiceSecurityExtension(){
        return new PasswordServiceSecurityExtension();
    }

    @ConditionalOnMissingBean(name = "userAuthTokenServiceSecurityExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public UserAuthTokenServiceSecurityExtension userAuthTokenServiceSecurityExtension(){
        return new UserAuthTokenServiceSecurityExtension();
    }



    @Bean("signupServiceAclExtension")
    @ConditionalOnMissingBean(name = "signupServiceAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SignupServiceAclExtension signupServiceAclExtension() {
        return new SignupServiceAclExtension();
    }


}
