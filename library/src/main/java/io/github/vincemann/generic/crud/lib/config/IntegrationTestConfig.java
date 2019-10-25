package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestFactoryImpl;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.DefaultUriFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.UrlParamDefaultUriFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.testRequestEntityModificationStrategy.TestRequestEntityModificationStrategy;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.testRequestEntityModificationStrategy.TestRequestEntityModificationStrategyImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//todo sketchy af needs refactoring
@Configuration
public class IntegrationTestConfig {

    public TestRequestEntityFactory testRequestEntityFactory(TestRequestEntityModificationStrategy testRequestEntityModificationStrategy, DefaultUriFactory defaultUriFactory){
        return new TestRequestFactoryImpl(defaultUriFactory,testRequestEntityModificationStrategy);
    }

    @Bean
    public TestRequestEntityModificationStrategy testRequestEntityModificationStrategy(){
        return new TestRequestEntityModificationStrategyImpl();
    }

    @Bean
    public DefaultUriFactory defaultUriFactory(@Qualifier("idUrlParamKey") String idUrlParamKey){
        return new UrlParamDefaultUriFactory(idUrlParamKey);
    }
}
