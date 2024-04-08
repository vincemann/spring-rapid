package com.github.vincemann.springrapid.acl.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.acl.AclTemplateImpl;
import com.github.vincemann.springrapid.acl.AdminPermissionGrantingStrategy;
import com.github.vincemann.springrapid.auth.Roles;
import com.github.vincemann.springrapid.acl.service.PermissionStringConverter;
import com.github.vincemann.springrapid.acl.service.PermissionStringConverterImpl;
import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acl.service.RapidAclServiceImpl;
import com.github.vincemann.springrapid.acl.util.AclUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * Auto-configures AclBeans and Acl-Caching.
 */
@Configuration
public class AclAutoConfiguration {

    public AclAutoConfiguration() {

    }

    @Autowired
    DataSource dataSource;


    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(10000);
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineConfig());
        return cacheManager;
    }

    @Bean
    public SpringCacheBasedAclCache aclCache() {
        return new SpringCacheBasedAclCache(
                cacheManager().getCache("aclCache"),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }


    @Bean
    @ConditionalOnMissingBean(AclTemplate.class)
    public AclTemplate aclTemplate(){
        return new AclTemplateImpl();
    }


    @ConditionalOnMissingBean(PermissionGrantingStrategy.class)
    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new AdminPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Autowired
    public void configureAclUtils(PermissionStringConverter permissionStringConverter){
        AclUtils.setup(permissionStringConverter);
    }

    @Bean
    @ConditionalOnMissingBean(PermissionEvaluator.class)
    public PermissionEvaluator permissionEvaluator(){
        return new AclPermissionEvaluator(aclService());
    }

    @ConditionalOnMissingBean(PermissionStringConverter.class)
    @Bean
    public PermissionStringConverter permissionStringConverter(){
        return new PermissionStringConverterImpl();
    }

    @ConditionalOnMissingBean(AclAuthorizationStrategy.class)
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        // only system user can change acl information, use RapidSecurityContext.executeAsSystemUser() for acl altering code
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
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(),permissionGrantingStrategy());
    }

    @ConditionalOnMissingBean(JdbcMutableAclService.class)
    @Bean
    public JdbcMutableAclService aclService() {
        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
    }

}

