package com.github.vincemann.springrapid.coretest.config;

import com.github.vincemann.springrapid.core.util.TransactionalTemplate;
import com.github.vincemann.springrapid.coretest.slicing.TestConfig;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@TestConfig
public class RapidCoreTestAutoconfiguration {

    @Bean
    @ConditionalOnMissingBean(TransactionalTemplate.class)
    public TransactionalTemplate transactionalTemplate(){
        return new TransactionalTemplate();
    }

    @Autowired
    public void configureRapidTestUtil(TransactionalTemplate transactionalTemplate){
        TransactionalRapidTestUtil.setTransactionalTestTemplate(transactionalTemplate);
    }
}
