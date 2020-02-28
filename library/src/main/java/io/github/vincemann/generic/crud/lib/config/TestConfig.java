package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.test.deepCompare.ReflectionComparator;
import io.github.vincemann.generic.crud.lib.test.deepCompare.EntityReflectionComparator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public ReflectionComparator reflectionComparator(){
        return EntityReflectionComparator.EQUALS_FOR_ENTITIES();
    }
}
