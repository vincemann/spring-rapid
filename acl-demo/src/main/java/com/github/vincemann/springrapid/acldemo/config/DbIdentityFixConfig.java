package com.github.vincemann.springrapid.acldemo.config;

//@Configuration
//public class DbIdentityFixConfig {
//

// mysql
//    @Autowired
//    public void configureDatabaseQueries(JdbcMutableAclService service){
//        service.setClassIdentityQuery("SELECT @@IDENTITY");
//        service.setSidIdentityQuery("SELECT @@IDENTITY");
//    }

//    https://stackoverflow.com/questions/54859029/spring-security-acl-object/56275135#56275135
// postgresql
//@Autowired
//public void configureDatabaseQueries(JdbcMutableAclService service){
//        service.setSidIdentityQuery("SELECT currval('acl_sid_id_seq')");
//        service.setClassIdentityQuery("SELECT currval('acl_class_id_seq')");
//        }
//}
