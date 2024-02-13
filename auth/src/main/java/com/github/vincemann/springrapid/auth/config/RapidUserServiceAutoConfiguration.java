package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.service.ext.acl.CleanUpAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.CrudAclChecksExtension;
import com.github.vincemann.springrapid.auth.service.*;
import com.github.vincemann.springrapid.auth.service.ext.acl.SignupServiceAclExtension;
import com.github.vincemann.springrapid.auth.service.ext.sec.ContactInformationServiceSecurityExtension;
import com.github.vincemann.springrapid.auth.service.ext.sec.PasswordServiceSecurityExtension;
import com.github.vincemann.springrapid.auth.service.ext.sec.UserAuthTokenServiceSecurityExtension;
import com.github.vincemann.springrapid.auth.service.ext.sec.UserServiceSecurityExtension;
import com.github.vincemann.springrapid.auth.service.val.ContactInformationValidator;
import com.github.vincemann.springrapid.auth.service.val.EmailContactInformationValidator;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidator;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidatorImpl;
import com.github.vincemann.springrapid.auth.util.UserUtils;

import com.github.vincemann.springrapid.core.proxy.ExtensionProxies;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.service.pass.BcryptRapidPasswordEncoder;
import com.github.vincemann.springrapid.core.service.pass.RapidPasswordEncoder;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.github.vincemann.springrapid.core.proxy.ExtensionProxies.crudProxy;
import static com.github.vincemann.springrapid.core.proxy.ExtensionProxies.proxy;

@Configuration
@Slf4j
@EnableTransactionManagement
//@AutoConfigureBefore({AclAutoConfiguration.class})
public class RapidUserServiceAutoConfiguration {


    /**
     * Configures UserDetailsService if missing
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailService() {
        return new RapidUserDetailsService();
    }



    // keep it like that - otherwise stuff is not proxied and much other sht happening
    // this way user can define its UserServiceImpl with @Service or @Component and everything works
    // user must not set its implementation to Primary tho
    @Bean
    @Primary
    public UserService myUserService(JpaUserService abstractUserService) {
//        return createInstance();
        return abstractUserService;
    }

    @Bean
    @ConditionalOnMissingBean(UserUtils.class)
    public UserUtils userUtils(){
        return new UserUtils();
    }


    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public RapidPasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new BcryptRapidPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordValidator.class)
    public PasswordValidator passwordValidator(){
        return new PasswordValidatorImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ContactInformationValidator.class)
    public ContactInformationValidator contactInformationValidator(){
        return new EmailContactInformationValidator();
    }


    @Bean
    @ConditionalOnMissingBean(VerificationService.class)
    public VerificationService verificationService(){
        return new VerificationServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(SignupService.class)
    @Primary
    public SignupService signupService(){
        return new SignupServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordService.class)
    @Primary
    public PasswordService passwordService(){
        return new PasswordServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(UserAuthTokenService.class)
    @Primary
    public UserAuthTokenService userAuthTokenService(){
        return new UserAuthTokenServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ContactInformationService.class)
    @Primary
    public ContactInformationService contactInformationService(){
        return new ContactInformationServiceImpl();
    }

    @Bean
    @Secured
    @ConditionalOnMissingBean(name = "securedContactInformationService")
    public ContactInformationService securedContactInformationService(ContactInformationService service,
                                                                      ContactInformationServiceSecurityExtension securityExtension){
        return proxy(service)
                .addExtension(securityExtension)
                .build();
    }

    @Bean
    @Secured
    @ConditionalOnMissingBean(name = "securedUserAuthTokenService")
    public UserAuthTokenService securedUserAuthTokenService(UserAuthTokenService service,
                                                                      UserAuthTokenServiceSecurityExtension securityExtension){
        return proxy(service)
                .addExtension(securityExtension)
                .build();
    }

    @Bean
    @Secured
    @ConditionalOnMissingBean(name = "securedPasswordService")
    public PasswordService securedPasswordService(PasswordService service,
                                                            PasswordServiceSecurityExtension securityExtension){
        return proxy(service)
                .addExtension(securityExtension)
                .build();
    }

    @Bean
    @Acl
    @ConditionalOnMissingBean(name = "aclSignupService")
    public SignupService aclSignupService(SignupService service,
                                          SignupServiceAclExtension signupServiceAclExtension
    ) {
        return proxy(service)
                .defaultExtensionsEnabled(false)
                .addExtension(signupServiceAclExtension)
                .build();
    }


    @ConditionalOnMissingBean(name = "aclUserService")
    @Bean
    @Acl
    public UserService<?, ?> aclUserService(UserService<?, ?> service,
                                            CleanUpAclExtension cleanUpAclExtension
    ) {
        return crudProxy(service)
                // dont work with default extensions to keep things simple and concrete for user
                .defaultExtensionsEnabled(false)
                // acl info is only created in signup
                .addExtension(cleanUpAclExtension)
                .build();
    }


    @ConditionalOnMissingBean(name = "securedUserService")
    @Bean
    @Secured
    public UserService<?, ?> securedUserService(@Acl UserService<?, ?> service,
                                                UserServiceSecurityExtension securityRule,
                                                CrudAclChecksExtension crudAclChecksExtension
    ) {
        return crudProxy(service)
                // dont work with default extensions to keep things safer for user related stuff
                .defaultExtensionsEnabled(false)
                .addExtension(securityRule)
                .addExtension(crudAclChecksExtension)
                .build();
    }


}
