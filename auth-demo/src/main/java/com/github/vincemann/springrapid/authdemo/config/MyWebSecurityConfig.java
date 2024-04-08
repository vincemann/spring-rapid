package com.github.vincemann.springrapid.authdemo.config;

import com.github.vincemann.springrapid.auth.config.WebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class MyWebSecurityConfig extends WebSecurityConfig {
}
