package com.github.vincemann.springrapid.coretest.config;


import com.github.vincemann.springrapid.core.config.RapidServiceAutoConfiguration;
import com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholderResolver;
import com.github.vincemann.springrapid.coretest.service.resolve.RapidEntityPlaceholderResolver;
import com.github.vincemann.springrapid.coretest.slicing.ServiceTestConfig;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

@ServiceTestConfig
@Import(RapidServiceAutoConfiguration.class)
public class RapidServiceTestAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(EntityPlaceholderResolver.class)
    public EntityPlaceholderResolver entityPlaceholderResolver(){
        return new RapidEntityPlaceholderResolver();
    }

}
