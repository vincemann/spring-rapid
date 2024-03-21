package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.service.*;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.JwtAuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.val.ContactInformationValidator;
import com.github.vincemann.springrapid.auth.service.val.EmailContactInformationValidator;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidator;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidatorImpl;
import com.github.vincemann.springrapid.core.service.pass.BcryptRapidPasswordEncoder;
import com.github.vincemann.springrapid.core.service.pass.RapidPasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class RapidAuthServiceAutoConfiguration {


    /**
     * Configures UserDetailsService if missing
     */
    @Primary
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailService() {
        return new RapidUserDetailsService();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public RapidPasswordEncoder passwordEncoder() {
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
    @Root
    @ConditionalOnMissingBean(name = "verificationService")
    public VerificationService verificationService(){
        return new VerificationServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "signupService")
    public SignupService signupService(){
        return new SignupServiceImpl();
    }

    @Root
    @Bean
    @ConditionalOnMissingBean(name = "passwordService")
    public PasswordService passwordService(){
        return new PasswordServiceImpl();
    }

    @Root
    @Bean
    @ConditionalOnMissingBean(name = "userAuthTokenService")
    public UserAuthTokenService userAuthTokenService(){
        return new UserAuthTokenServiceImpl();
    }

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
    @ConditionalOnMissingBean(name = "securedUserAuthTokenService")
    public UserAuthTokenService securedUserAuthTokenService(@Root UserAuthTokenService service){
        return new SecuredUserAuthTokenService(service);
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


//    @ConditionalOnMissingBean(name = "securedUserService")
//    @Bean
//    @Secured
//    public UserService securedUserService(@Root UserService service) {
//        return new SecuredUserService(service);
//    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationTokenService.class)
    public AuthorizationTokenService authorizationTokenService(){
        return new JwtAuthorizationTokenService();
    }


}
