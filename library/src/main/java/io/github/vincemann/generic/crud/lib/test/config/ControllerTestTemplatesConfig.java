package io.github.vincemann.generic.crud.lib.test.config;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.*;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.*;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.urlParamId.BasicUrlParamIdRequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.urlParamId.UpdateUrlParamIdRequestEntityFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ControllerTestTemplatesConfig {

    //test templates dependencies are wired up here
    @Bean
    @UpdateTest
    public AbstractControllerTestConfigurationFactory updateTestConfigurationFactory(){
        return new UpdateControllerTestConfigurationFactory();
    }

    @Bean
    @CreateTest
    public AbstractControllerTestConfigurationFactory createTestConfigurationFactory(){
        return new CreateTestConfigurationFactory();
    }

    @Bean
    @FindTest
    public AbstractControllerTestConfigurationFactory findTestConfigurationFactory(){
        return new FindControllerTestConfigurationFactory();
    }

    @Bean
    @FindAllTest
    public AbstractControllerTestConfigurationFactory findAllTestConfigurationFactory(){
        return new FindAllControllerTestConfigurationFactory();
    }

    @Bean
    @DeleteTest
    public AbstractControllerTestConfigurationFactory deleteTestConfigurationFactory(){
        return new DeleteControllerTestConfigurationFactory();
    }





    @Bean
    @UpdateTest
    public RequestEntityFactory updateRequestEntityFactory(){
        return new UpdateUrlParamIdRequestEntityFactory();
    }

    @Bean
    @CreateTest
    public RequestEntityFactory createRequestEntityFactory(){
        return new BasicUrlParamIdRequestEntityFactory(SpringAdapterDtoCrudController.CREATE_METHOD_NAME);
    }

    @Bean
    @FindTest
    public RequestEntityFactory findRequestEntityFactory(){
        return new BasicUrlParamIdRequestEntityFactory(SpringAdapterDtoCrudController.FIND_METHOD_NAME);
    }

    @Bean
    @FindAllTest
    public RequestEntityFactory findAllRequestEntityFactory(){
        return new BasicUrlParamIdRequestEntityFactory(SpringAdapterDtoCrudController.FIND_ALL_METHOD_NAME);
    }

    @Bean
    @DeleteTest
    public RequestEntityFactory deleteRequestEntityFactory(){
        return new BasicUrlParamIdRequestEntityFactory(SpringAdapterDtoCrudController.DELETE_METHOD_NAME);
    }
}
