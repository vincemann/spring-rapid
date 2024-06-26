package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.msg.EmailMessageSender;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.auth.msg.mail.MailSender;
import com.github.vincemann.springrapid.auth.msg.mail.MockMailSender;
import com.github.vincemann.springrapid.auth.msg.mail.SmtpMailSender;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class MailAutoConfiguration {



    @Bean
    @ConditionalOnMissingBean(MessageSender.class)
    public MessageSender messageSender(){
        return new EmailMessageSender();
    }

    /**
     * Configures an SmtpMailSender when the property
     * <code>spring.mail.host</code> is defined.
     *
     * Example:
     * spring.mail.host=smtp.gmail.com
     * spring.mail.port=587
     * spring.mail.username=<login user to smtp server>
     * spring.mail.password=<login password to smtp server>
     * spring.mail.properties.mail.smtp.auth=true
     * spring.mail.properties.mail.smtp.starttls.enable=true
     */
    @Bean
    @ConditionalOnMissingBean(MailSender.class)
    @ConditionalOnProperty("spring.mail.host")
    public MailSender<?> smtpMailSender(JavaMailSender javaMailSender) {
        return new SmtpMailSender(javaMailSender);
    }

    /**
     * Configures a MockMailSender when the property
     * <code>spring.mail.host</code> isn't defined.
     */
    @Bean
    @ConditionalOnMissingBean(MailSender.class)
    @ConditionalOnProperty(name="spring.mail.host",matchIfMissing=true)
    public MailSender<?> mockMailSender() {
        return new MockMailSender();
    }



}
