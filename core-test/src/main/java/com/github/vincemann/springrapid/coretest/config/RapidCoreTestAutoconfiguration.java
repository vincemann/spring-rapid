package com.github.vincemann.springrapid.coretest.config;

import com.github.vincemann.springrapid.coretest.controller.TransactionalTestTemplate;
import com.github.vincemann.springrapid.coretest.slicing.TestConfig;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@TestConfig
public class RapidCoreTestAutoconfiguration {

    @Bean
    @ConditionalOnMissingBean(TransactionalTestTemplate.class)
    public TransactionalTestTemplate transactionalTestTemplate(){
        return new TransactionalTestTemplate();
    }

    @Autowired
    public void configureRapidTestUtil(TransactionalTestTemplate transactionalTestTemplate){
        TransactionalRapidTestUtil.setTransactionalTestTemplate(transactionalTestTemplate);
    }
}
