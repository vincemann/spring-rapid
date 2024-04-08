package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.auth.BcryptRapidPasswordEncoder;
import com.github.vincemann.springrapid.auth.RapidPasswordEncoder;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.jwt.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.jwt.JwtAuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.*;
import com.github.vincemann.springrapid.auth.val.ContactInformationValidator;
import com.github.vincemann.springrapid.auth.val.EmailContactInformationValidator;
import com.github.vincemann.springrapid.auth.val.PasswordValidator;
import com.github.vincemann.springrapid.auth.val.PasswordValidatorImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthServiceAutoConfiguration {


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

    @Root
    @Bean
    @ConditionalOnMissingBean(name = "passwordService")
    public PasswordService passwordService(){
        return new PasswordServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "userAuthTokenService")
    public UserAuthTokenService userAuthTokenService(){
        return new UserAuthTokenServiceImpl();
    }


    @Bean
    @ConditionalOnMissingBean(AuthorizationTokenService.class)
    public AuthorizationTokenService authorizationTokenService(){
        return new JwtAuthorizationTokenService();
    }


}
