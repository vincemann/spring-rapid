package com.github.vincemann.springrapid.coretest.config;

import com.github.vincemann.springrapid.core.config.RapidCrudServiceLocatorAutoConfiguration;
import com.github.vincemann.springrapid.core.util.TransactionalTemplate;
import com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholderResolver;
import com.github.vincemann.springrapid.coretest.service.resolve.RapidEntityPlaceholderResolver;
import com.github.vincemann.springrapid.coretest.slicing.ServiceTestConfig;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@ServiceTestConfig
@Import(RapidCrudServiceLocatorAutoConfiguration.class)
public class RapidServiceTestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TransactionalTemplate.class)
    public TransactionalTemplate transactionalTemplate(){
        return new TransactionalTemplate();
    }

    @Autowired
    public void configureRapidTestUtil(TransactionalTemplate transactionalTemplate){
        TransactionalRapidTestUtil.setTransactionalTestTemplate(transactionalTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(EntityPlaceholderResolver.class)
    public EntityPlaceholderResolver entityPlaceholderResolver(){
        return new RapidEntityPlaceholderResolver();
    }

}
