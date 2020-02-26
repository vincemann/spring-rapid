package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.test.equalChecker.ReflectionComparator;
import io.github.vincemann.generic.crud.lib.test.equalChecker.IgnoreEntitiesReflectionComparator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public ReflectionComparator reflectionComparator(){
        return new IgnoreEntitiesReflectionComparator();
    }
}
