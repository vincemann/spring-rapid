package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.acl.AclTemplateImpl;
import com.github.vincemann.springrapid.acl.framework.AdministrationDefaultPermissionGrantingStrategy;
import com.github.vincemann.springrapid.acl.framework.VerboseAclPermissionEvaluator;
import com.github.vincemann.springrapid.acl.service.PermissionStringConverter;
import com.github.vincemann.springrapid.acl.service.PermissionStringConverterImpl;
import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acl.service.RapidAclServiceImpl;
import com.github.vincemann.springrapid.acl.util.AclUtils;
import com.github.vincemann.springrapid.core.sec.Roles;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

/**
 * Auto-configures AclBeans and Acl-Caching.
 */
@Configuration
@Slf4j
public class RapidAclAutoConfiguration {

    public RapidAclAutoConfiguration() {

    }

    @Autowired
    DataSource dataSource;


    @Bean
    @ConditionalOnMissingBean(AclTemplate.class)
    public AclTemplate aclTemplate(){
        return new AclTemplateImpl();
    }



    @Bean
    public CacheManager aclCacheManager() {
        // Reuse the existing CacheManager instance if it exists
        return CacheManager.getCacheManager("rapidCacheManager");
    }

    @ConditionalOnMissingBean(EhCacheBasedAclCache.class)
    @Bean
    public EhCacheBasedAclCache aclCache() {
        return new EhCacheBasedAclCache(
                aclEhCacheFactoryBean().getObject(),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }


    @ConditionalOnMissingBean(EhCacheFactoryBean.class)
    @Bean
    public EhCacheFactoryBean aclEhCacheFactoryBean() {
        EhCacheFactoryBean factoryBean = new EhCacheFactoryBean();
//        factoryBean.setCacheManager(aclCacheManagerBean().getObject());
        factoryBean.setCacheManager(aclCacheManager());
        factoryBean.setCacheName("aclCache");
        return factoryBean;
    }

//    @ConditionalOnMissingBean(EhCacheManagerFactoryBean.class)
//    @Bean
//    @Primary
//    public EhCacheManagerFactoryBean aclCacheManagerBean() {
//        //for unit tests, es soll nicht immer wieder neuer manager erstellt werden, der dann cacheFactory mit selben name kreiert, was die rules violated
//        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
////        ehCacheManagerFactoryBean.setShared(true);
//        return ehCacheManagerFactoryBean;
//    }

    @ConditionalOnMissingBean(PermissionGrantingStrategy.class)
    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new AdministrationDefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Autowired
    public void configureAclUtils(PermissionStringConverter permissionStringConverter){
        AclUtils.setup(permissionStringConverter);
    }

    @Bean
    @ConditionalOnMissingBean(PermissionEvaluator.class)
    public PermissionEvaluator permissionEvaluator(){
        return new VerboseAclPermissionEvaluator(aclService());
    }

    @ConditionalOnMissingBean(PermissionStringConverter.class)
    @Bean
    public PermissionStringConverter permissionStringConverter(){
        return new PermissionStringConverterImpl();
    }

    @ConditionalOnMissingBean(AclAuthorizationStrategy.class)
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        // only system user can change acl information, user RapidSecurityContext.executeAsSystemUser() for acl altering code
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority(Roles.SYSTEM));
    }

    @ConditionalOnMissingBean(MethodSecurityExpressionHandler.class)
    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler(PermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        //AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
        return expressionHandler;
    }

    @ConditionalOnMissingBean(RapidAclService.class)
    @Bean
    public RapidAclService rapidAclPermissionService(MutableAclService aclService){
        return new RapidAclServiceImpl(aclService);
    }

    @ConditionalOnMissingBean(LookupStrategy.class)
    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
    }

    @ConditionalOnMissingBean(JdbcMutableAclService.class)
    @Bean
    public JdbcMutableAclService aclService() {
        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
    }

}

