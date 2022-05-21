package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.service.validation.PasswordValidator;
import com.github.vincemann.springrapid.auth.service.validation.RapidPasswordValidator;
import com.github.vincemann.springrapid.auth.util.UserUtils;
import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.RapidAuthAuditorAware;
import com.github.vincemann.springrapid.auth.service.RapidUserDetailsService;
import com.github.vincemann.springrapid.auth.service.UserService;

import com.github.vincemann.springrapid.core.service.password.BcryptRapidPasswordEncoder;
import com.github.vincemann.springrapid.core.service.password.RapidPasswordEncoder;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
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
@EnableJpaAuditing
@EnableTransactionManagement
//@AutoConfigureBefore({AclAutoConfiguration.class})
public class RapidUserServiceAutoConfiguration {


    /**
     * Configures an Auditor Aware if missing
     */
    @Bean
    @ConditionalOnMissingBean(name = "rapidAuthSecurityAuditorAware")
    public <ID extends Serializable> AuditorAware<ID> rapidSecurityAuditorAware() {
        return new RapidAuthAuditorAware<ID>();
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
