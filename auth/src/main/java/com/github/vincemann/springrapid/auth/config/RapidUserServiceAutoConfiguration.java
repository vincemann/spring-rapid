package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.validation.PasswordValidator;
import com.github.vincemann.springrapid.auth.service.validation.RapidPasswordValidator;
import com.github.vincemann.springrapid.auth.util.UserUtils;
import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.LongIdConverter;
import com.github.vincemann.springrapid.core.model.LongIdRapidAuthAuditorAware;
import com.github.vincemann.springrapid.core.model.RapidAuthAuditorAware;
import com.github.vincemann.springrapid.auth.service.RapidUserDetailsService;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.password.BcryptRapidPasswordEncoder;
import com.github.vincemann.springrapid.core.service.password.RapidPasswordEncoder;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.Serializable;

@ServiceConfig
@Slf4j
@EnableTransactionManagement
//@EnableJpaAuditing
//@AutoConfigureBefore({AclAutoConfiguration.class})
public class RapidUserServiceAutoConfiguration {


//    @Autowired
//    private UserService userService;



    /**
     * Configures an Auditor Aware if missing
     */
    @Bean
    @ConditionalOnMissingBean(name = "rapidAuthSecurityAuditorAware")
    public AuditorAware<Long> rapidSecurityAuditorAware() {
        return new LongIdRapidAuthAuditorAware();
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

//    @Bean
//    @ConditionalOnMissingBean(IdConverter.class)
//    public IdConverter<Long> idConverter() {
////        return id -> userService.toId(id);
//        return new LongIdConverter();
//    }


    // keep it like that - otherwise stuff is not proxied and much other sht happening
    // this way user can define its UserServiceImpl with @Service or @Component and everything works
    // user must not set its implementation to Primary tho
    @Bean
    @Primary
    public UserService myUserService(AbstractUserService abstractUserService) {
//        return createInstance();
        return abstractUserService;
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

    @Bean
    @ConditionalOnMissingBean(PasswordValidator.class)
    public PasswordValidator passwordValidator(){
        return new RapidPasswordValidator();
    }

//    @Autowired
//    public void configureAuthUtils(UserService<AbstractUser<Serializable>,Serializable> userService){
//        UserUtils.setUserService(userService);
//    }
//    @Autowired
//    public void configureAuthUtils(CrudServiceLocator crudServiceLocator, UserService<AbstractUser<Serializable>,Serializable> userService, ApplicationContext applicationContext){
//        UserUtils.setCrudServiceLocator(crudServiceLocator);
//        UserUtils.setUserService(userService);
//        UserUtils.setApplicationContext(applicationContext);
//    }

    @Bean
    @ConditionalOnMissingBean(UserUtils.class)
    public UserUtils userUtils(){
        return new UserUtils();
    }



}
