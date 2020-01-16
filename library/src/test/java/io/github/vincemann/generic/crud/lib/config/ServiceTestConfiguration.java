package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.equalChecker.PartialUpdateReflectionEqualChecker;
import io.github.vincemann.generic.crud.lib.test.equalChecker.ReflectionEqualChecker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.vincemann.generic.crud.lib.test.service.CrudServiceIntegrationTest.PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER;
/*
@Configuration
public class ServiceTestConfiguration {

    @Bean
    public EqualChecker defaultEqualChecker(){
        return new ReflectionEqualChecker<>();
    }

    @Bean
    public EqualChecker defaultPartialUpdateEqualChecker(){
        return new PartialUpdateReflectionEqualChecker<>();
    }

}
*/