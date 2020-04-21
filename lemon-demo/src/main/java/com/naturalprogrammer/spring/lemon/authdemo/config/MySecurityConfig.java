package com.naturalprogrammer.spring.lemon.authdemo.config;


import com.naturalprogrammer.spring.lemon.auth.security.config.LemonJpaSecurityConfig;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;


@Configuration
public class MySecurityConfig extends LemonJpaSecurityConfig {
	
	private static final Log log = LogFactory.getLog(MySecurityConfig.class);
	
	public MySecurityConfig() {
		log.info("Created");
	}

	@Override
	protected void authorizeRequests(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.mvcMatchers("/admin/**").hasRole(LemonRole.GOOD_ADMIN_RAW);
		super.authorizeRequests(http);
	}
}
