package io.github.vincemann.generic.crud.lib.config;

//import io.github.vincemann.generic.crud.lib.service.plugin.SessionReattachmentPlugin;
//import io.github.vincemann.generic.crud.lib.service.sessionReattach.EntityGraphSessionReattacher;
//import io.github.vincemann.generic.crud.lib.service.sessionReattach.EntityMangerSessionReattacher;
//import io.github.vincemann.generic.crud.lib.service.sessionReattach.SessionReattacher;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
//
//@Configuration
//@Profile("service")
//public class SessionReattachmentConfig {
////
////    @Bean
////    public LocalEntityManagerFactoryBean entityManagerFactory() {
////        LocalEntityManagerFactoryBean em =
////                new LocalEntityManagerFactoryBean();
////        em.setPersistenceUnitName("myPersistenceUnit");
////        return em;
////    }
//
//
//    @Bean
//    public SessionReattacher sessionReattacher(){
//        return new EntityMangerSessionReattacher();
//    }
//
//    @Bean
//    public EntityGraphSessionReattacher entityGraphSessionReattacher(){
//        return new EntityGraphSessionReattacher(sessionReattacher());
//    }
//
//    @Bean
//    public SessionReattachmentPlugin sessionReattachmentPlugin(){
//        return new SessionReattachmentPlugin(entityGraphSessionReattacher());
//    }
//}
