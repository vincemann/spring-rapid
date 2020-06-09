package com.github.vincemann.springlemon.demo.config;


import com.github.vincemann.springlemon.auth.security.config.LemonJpaSecurityConfig;
import com.github.vincemann.springlemon.auth.security.domain.LemonRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


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
