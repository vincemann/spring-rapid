package com.naturalprogrammer.spring.lemon.authdemo.config;

import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;

@Configuration
public class MySqlConfig {

    @Autowired
    public void configureDatabaseQueries(JdbcMutableAclService service){
        service.setClassIdentityQuery("SELECT @@IDENTITY");
        service.setSidIdentityQuery("SELECT @@IDENTITY");
    }
}
