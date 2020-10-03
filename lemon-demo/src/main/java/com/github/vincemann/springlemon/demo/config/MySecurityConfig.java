package com.github.vincemann.springlemon.demo.config;


import com.github.vincemann.springlemon.auth.config.LemonWebSecurityConfig;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.domain.LemonRoles;
import com.github.vincemann.springlemon.auth.security.AuthenticatedPrincipalFactory;
import com.github.vincemann.springlemon.auth.security.LemonAuthenticatedPrincipalFactory;
import com.github.vincemann.springlemon.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
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
			.mvcMatchers("/admin/**").hasRole(LemonRoles.ADMIN_RAW);
		super.authorizeRequests(http);
	}
}
