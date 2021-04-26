package com.github.vincemann.springrapid.authdemo.config;


import com.github.vincemann.springrapid.auth.config.RapidWebSecurityConfig;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
@Slf4j
public class SecurityConfig extends RapidWebSecurityConfig {
	

	public SecurityConfig() {

	}

	@Override
	protected void authorizeRequests(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.mvcMatchers("/admin/**").hasRole(AuthRoles.ADMIN_RAW);
		super.authorizeRequests(http);
	}
}
