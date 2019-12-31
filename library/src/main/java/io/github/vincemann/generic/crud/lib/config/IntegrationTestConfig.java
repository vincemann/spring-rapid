package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntity_Factory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestFactoryImpl;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.UrlParamId_DefaultUri_Factory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.UrlParamId_DefaultUri_Factory_Impl;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.requestEntityModification.TestRequestEntity_ModificationStrategy;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.requestEntityModification.TestRequestEntity_ModificationStrategy_Impl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//todo sketchy af needs refactoring
@Configuration
public class IntegrationTestConfig {

    @Bean
    public TestRequestEntity_Factory testRequestEntityFactory(TestRequestEntity_ModificationStrategy testRequestEntityModificationStrategy, UrlParamId_DefaultUri_Factory urlParamIdDefaultUriFactory){
        return new TestRequestFactoryImpl(urlParamIdDefaultUriFactory,testRequestEntityModificationStrategy);
    }

    @Bean
    public TestRequestEntity_ModificationStrategy testRequestEntityModificationStrategy(){
        return new TestRequestEntity_ModificationStrategy_Impl();
    }

    @Bean
    public UrlParamId_DefaultUri_Factory defaultUriFactory(@Qualifier("idUrlParamKey") String idUrlParamKey){
        return new UrlParamId_DefaultUri_Factory_Impl(idUrlParamKey);
    }
}
