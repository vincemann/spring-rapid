package com.naturalprogrammer.spring.lemon.auth.config;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.auth.domain.IdConverter;
import com.naturalprogrammer.spring.lemon.auth.security.service.LemonUserDetailsService;
import com.naturalprogrammer.spring.lemon.auth.service.LemonMockAuthService;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import com.naturalprogrammer.spring.lemon.auth.validation.RetypePasswordValidator;
import com.naturalprogrammer.spring.lemon.auth.validation.UniqueEmailValidator;
import io.github.vincemann.springrapid.acl.config.AclAutoConfiguration;
import io.github.vincemann.springrapid.acl.service.MockAuthService;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.Serializable;

/**
 * Spring Lemon Auto Configuration
 * 
 * @author Sanjay Patel
 */
@ServiceConfig
@EnableTransactionManagement
@EnableJpaAuditing
@AutoConfigureBefore({LemonCommonsJpaAutoConfiguration.class,AclAutoConfiguration.class})
public class LemonAutoConfiguration {
	
	private static final Log log = LogFactory.getLog(LemonAutoConfiguration.class);
	
	public LemonAutoConfiguration() {
		log.info("Created");
	}

	@Bean
	@ConditionalOnMissingBean(IdConverter.class)
	public <ID extends Serializable>
	IdConverter<ID> idConverter(LemonService<?,ID,?> lemonService) {
		return id -> lemonService.toId(id);
	}
	


	/**
	 * Configures UserDetailsService if missing
	 */
	@Bean
	@Primary
	@ConditionalOnMissingBean(UserDetailsService.class)
	public <U extends AbstractUser<ID>, ID extends Serializable>
	LemonUserDetailsService userDetailService(AbstractUserRepository<U, ID> userRepository) {
		
        log.info("Configuring LemonUserDetailsService");       
		return new LemonUserDetailsService<U, ID>(userRepository);
	}

	@Bean
	@ConditionalOnMissingBean(MockAuthService.class)
	public MockAuthService lemonMockAuthService(){
		return new LemonMockAuthService();
	}
	
	/**
	 * Configures RetypePasswordValidator if missing
	 */
	@Bean
	@ConditionalOnMissingBean(RetypePasswordValidator.class)
	public RetypePasswordValidator retypePasswordValidator() {
		
        log.info("Configuring RetypePasswordValidator");       
		return new RetypePasswordValidator();
	}
	
	/**
	 * Configures UniqueEmailValidator if missing
	 */
	@Bean
	public UniqueEmailValidator uniqueEmailValidator(AbstractUserRepository<?, ?> userRepository) {
		
        log.info("Configuring UniqueEmailValidator");       
		return new UniqueEmailValidator(userRepository);		
	}
	
}
