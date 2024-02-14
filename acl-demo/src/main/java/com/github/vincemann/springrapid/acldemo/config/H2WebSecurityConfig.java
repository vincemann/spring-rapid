package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.auth.config.RapidWebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
// only run it like this in dev or test env
public class H2WebSecurityConfig extends RapidWebSecurityConfig {


    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        // SpringSecurity will ignore below paths
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void otherConfigurations(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
    }

}
