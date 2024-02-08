package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.service.JpaUserService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidator;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidatorImpl;
import com.github.vincemann.springrapid.auth.util.UserUtils;
import com.github.vincemann.springrapid.auth.service.RapidUserDetailsService;

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

@Configuration
@Slf4j
@EnableTransactionManagement
//@AutoConfigureBefore({AclAutoConfiguration.class})
public class RapidUserServiceAutoConfiguration {


//    @Autowired
//    private UserService userService;



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
    public UserService myUserService(JpaUserService abstractUserService) {
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
        return new PasswordValidatorImpl();
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
