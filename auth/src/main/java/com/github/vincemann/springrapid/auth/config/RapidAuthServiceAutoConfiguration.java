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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Slf4j
@EnableTransactionManagement
public class RapidAuthServiceAutoConfiguration {


    /**
     * Configures UserDetailsService if missing
     */
    @Bean
    @Primary
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
    @ConditionalOnMissingBean(name = "verificationService")
    public VerificationService verificationService(){
        return new VerificationServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "signupService")
    public SignupService signupService(){
        return new SignupServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "passwordService")
    @Root
    public PasswordService passwordService(){
        return new PasswordServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "userAuthTokenService")
    @Root
    public UserAuthTokenService userAuthTokenService(){
        return new UserAuthTokenServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "contactInformationService")
    @Root
    public ContactInformationService contactInformationService(){
        return new ContactInformationServiceImpl();
    }


    @Bean
    @Secured
    @ConditionalOnMissingBean(name = "securedContactInformationService")
    public ContactInformationService securedContactInformationService(@Root ContactInformationService service){
        return new SecuredContactInformationService(service);
    }

    @Bean
    @Secured
    @ConditionalOnMissingBean(name = "securedUserAuthTokenService")
    public UserAuthTokenService securedUserAuthTokenService(@Root UserAuthTokenService service){
        return new SecuredUserAuthTokenService(service);
    }

    @Bean
    @Secured
    @ConditionalOnMissingBean(name = "securedPasswordService")
    public PasswordService securedPasswordService(@Root PasswordService service){
        return new SecuredPasswordService(service);
    }


    @ConditionalOnMissingBean(name = "securedUserService")
    @Bean
    @Secured
    public UserService securedUserService(@Root UserService service) {
        return new SecuredUserService(service);
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationTokenService.class)
    public AuthorizationTokenService authorizationTokenService(){
        return new JwtAuthorizationTokenService();
    }


}
