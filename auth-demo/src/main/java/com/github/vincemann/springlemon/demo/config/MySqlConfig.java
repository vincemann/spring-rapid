package com.github.vincemann.springlemon.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;

@Configuration
public class MySqlConfig {

    @Autowired
    public void configureDatabaseQueries(JdbcMutableAclService service){
        service.setClassIdentityQuery("SELECT @@IDENTITY");
        service.setSidIdentityQuery("SELECT @@IDENTITY");
    }
}
