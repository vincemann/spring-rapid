package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.domain.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.domain.IdConverter;
import com.github.vincemann.springrapid.auth.domain.RapidAuditorAware;
import com.github.vincemann.springrapid.auth.mail.MailSender;
import com.github.vincemann.springrapid.auth.mail.MockMailSender;
import com.github.vincemann.springrapid.auth.mail.SmtpMailSender;
import com.github.vincemann.springrapid.auth.service.RapidUserDetailsService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.validation.RetypePasswordValidator;
import com.github.vincemann.springrapid.auth.validation.UniqueEmailValidator;

import com.github.vincemann.springrapid.core.service.password.BcryptRapidPasswordEncoder;
import com.github.vincemann.springrapid.core.service.password.RapidPasswordEncoder;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.Serializable;

@ServiceConfig
@Slf4j
@EnableJpaAuditing
@EnableTransactionManagement
//@AutoConfigureBefore({AclAutoConfiguration.class})
public class RapidUserServiceAutoConfiguration {


    /**
     * Configures an Auditor Aware if missing
     */
    @Bean
    @ConditionalOnMissingBean(AuditorAware.class)
    public <ID extends Serializable>
    AuditorAware<ID> auditorAware() {

        return new RapidAuditorAware<ID>();
    }

    /**
     * Configures RetypePasswordValidator if missing
     */
    @Bean
    @ConditionalOnMissingBean(RetypePasswordValidator.class)
    public RetypePasswordValidator retypePasswordValidator() {
        return new RetypePasswordValidator();
    }

    /**
     * Configures UniqueEmailValidator if missing
     */
    @Bean
    public UniqueEmailValidator uniqueEmailValidator(AbstractUserRepository<?, ?> userRepository) {
        return new UniqueEmailValidator(userRepository);
    }

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
    @ConditionalOnMissingBean(IdConverter.class)
    public <ID extends Serializable>
    IdConverter<ID> idConverter( UserService<?,ID> userService) {
        return id -> userService.toId(id);
    }


    /**
     * Configures Password encoder if missing
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public RapidPasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new BcryptRapidPasswordEncoder();
    }


    /**
     * Configures a MockMailSender when the property
     * <code>spring.mail.host</code> isn't defined.
     */
    @Bean
    @ConditionalOnMissingBean(MailSender.class)
    @ConditionalOnProperty(name="spring.mail.host", havingValue="foo", matchIfMissing=true)
    public MailSender<?> mockMailSender() {
        return new MockMailSender();
    }


    /**
     * Configures an SmtpMailSender when the property
     * <code>spring.mail.host</code> is defined.
     */
    @Bean
    @ConditionalOnMissingBean(MailSender.class)
    @ConditionalOnProperty("spring.mail.host")
    public MailSender<?> smtpMailSender(JavaMailSender javaMailSender) {
        return new SmtpMailSender(javaMailSender);
    }



}
