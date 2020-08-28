package com.github.vincemann.springlemon.demo.config;


import com.github.vincemann.springlemon.auth.config.security.LemonJpaSecurityConfig;
import com.github.vincemann.springlemon.auth.config.security.LemonWebSecurityConfig;
import com.github.vincemann.springlemon.auth.domain.LemonRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
@Slf4j
public class MySecurityConfig extends LemonWebSecurityConfig {
	

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
