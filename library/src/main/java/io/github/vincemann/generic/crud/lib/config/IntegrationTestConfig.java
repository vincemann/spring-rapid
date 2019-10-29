package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestFactoryImpl;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.UrlParamIdDefaultUriFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.UrlParamUrlParamIdDefaultUriFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.testRequestEntityModificationStrategy.TestRequestEntityModificationStrategy;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.testRequestEntityModificationStrategy.TestRequestEntityModificationStrategyImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//todo sketchy af needs refactoring
@Configuration
public class IntegrationTestConfig {

    @Bean
    public TestRequestEntityFactory testRequestEntityFactory(TestRequestEntityModificationStrategy testRequestEntityModificationStrategy, UrlParamIdDefaultUriFactory urlParamIdDefaultUriFactory){
        return new TestRequestFactoryImpl(urlParamIdDefaultUriFactory,testRequestEntityModificationStrategy);
    }

    @Bean
    public TestRequestEntityModificationStrategy testRequestEntityModificationStrategy(){
        return new TestRequestEntityModificationStrategyImpl();
    }

    @Bean
    public UrlParamIdDefaultUriFactory defaultUriFactory(@Qualifier("idUrlParamKey") String idUrlParamKey){
        return new UrlParamUrlParamIdDefaultUriFactory(idUrlParamKey);
    }
}
