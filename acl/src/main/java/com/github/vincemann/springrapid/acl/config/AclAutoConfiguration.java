package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.Role;
import com.github.vincemann.springrapid.acl.framework.LenientPermissionGrantingStrategy;
import com.github.vincemann.springrapid.acl.framework.NoModSecurityCheckAclAuthorizationStrategy;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.acl.service.SecurityContextMockAuthService;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

/**
 * Auto-configures AclBeans and Acl-Caching.
 */
@ServiceConfig
@Slf4j
//@ConditionalOnClass(DataSource.class)
@AutoConfigureBefore({AclExtensionAutoConfiguration.class})
public class AclAutoConfiguration {

    public AclAutoConfiguration() {
        log.info("Created");
    }

    @Autowired
    DataSource dataSource;

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
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(aclCacheManagerBean().getObject());
        ehCacheFactoryBean.setCacheName("aclCache");
        return ehCacheFactoryBean;
    }

    @ConditionalOnMissingBean(EhCacheManagerFactoryBean.class)
    @Bean
    @Primary
    public EhCacheManagerFactoryBean aclCacheManagerBean() {
        //for unit tests, es soll nicht immer wieder neuer manager erstellt werden, der dann cacheFactory mit selben name kreiert, was die rules violated
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setShared(true);
        return ehCacheManagerFactoryBean;
    }

    @ConditionalOnMissingBean(PermissionGrantingStrategy.class)
    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new LenientPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @ConditionalOnMissingBean(AclAuthorizationStrategy.class)
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        //admin is allowed to change all acl db tables, but he is not automatically allowed to do anything acl restricted bc of this statement
        //return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority(AuthorityName.ROLE_ADMIN.toString()));
        return new NoModSecurityCheckAclAuthorizationStrategy(
                new SimpleGrantedAuthority(Role.ADMIN)
        );
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


    @Bean
    @ConditionalOnMissingBean(MockAuthService.class)
    public MockAuthService mockAuthService(){
        return new SecurityContextMockAuthService();
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

    @ConditionalOnMissingBean(PermissionEvaluator.class)
    @Bean
    public PermissionEvaluator permissionEvaluator(){
        return new AclPermissionEvaluator(aclService());
    }

}

