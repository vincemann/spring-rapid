package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclProperties;
import com.github.vincemann.springrapid.acl.framework.AdminDefaultPermissionGrantingStrategy;
import com.github.vincemann.springrapid.acl.framework.oidresolve.ObjectIdentityResolver;
import com.github.vincemann.springrapid.acl.framework.oidresolve.RapidObjectIdentityResolver;
import com.github.vincemann.springrapid.acl.service.AclPermissionService;
import com.github.vincemann.springrapid.acl.service.PermissionStringConverter;
import com.github.vincemann.springrapid.acl.service.RapidPermissionService;
import com.github.vincemann.springrapid.acl.service.RapidPermissionStringConverter;
import com.github.vincemann.springrapid.acl.util.AclUtils;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.acl.framework.NoModSecurityCheckAclAuthorizationStrategy;
import com.github.vincemann.springrapid.acl.framework.VerboseAclPermissionEvaluator;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
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
@ServiceConfig
@Slf4j
//@ConditionalOnClass(DataSource.class)
@AutoConfigureBefore({RapidAclExtensionsAutoConfiguration.class})
public class RapidAclAutoConfiguration {

    public RapidAclAutoConfiguration() {

    }

    @Autowired
    DataSource dataSource;


//    @Primary
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
        return new AdminDefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Autowired
    public void configureAclUtils(PermissionStringConverter permissionStringConverter){
        AclUtils.setPermissionStringConverter(permissionStringConverter);
    }

    @Bean
    @ConditionalOnMissingBean(PermissionEvaluator.class)
    public PermissionEvaluator permissionEvaluator(){
        return new VerboseAclPermissionEvaluator(aclService());
    }

    @ConditionalOnMissingBean(PermissionStringConverter.class)
    @Bean
    public PermissionStringConverter permissionStringConverter(){
        return new RapidPermissionStringConverter();
    }

    @ConditionalOnMissingBean(AclAuthorizationStrategy.class)
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        //admin is allowed to change all acl db tables, but he is not automatically allowed to do anything acl restricted bc of this statement
        //return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority(AuthorityName.ROLE_ADMIN.toString()));
        return new NoModSecurityCheckAclAuthorizationStrategy(
                new SimpleGrantedAuthority(Roles.ADMIN)
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

    @ConfigurationProperties(prefix="rapid-acl")
    @ConditionalOnMissingBean(AclProperties.class)
    @Bean
    public AclProperties rapidAclProperties() {
        return new AclProperties();
    }


    @ConditionalOnMissingBean(AclPermissionService.class)
    @Bean
    public AclPermissionService rapidAclPermissionService(MutableAclService aclService){
        return new RapidPermissionService(aclService);
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

    @ConditionalOnMissingBean(ObjectIdentityResolver.class)
    @Bean
    public ObjectIdentityResolver objectIdentityResolver(){
        return new RapidObjectIdentityResolver();
    }

}

