package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.test.equalChecker.FuzzyComparator;
import io.github.vincemann.generic.crud.lib.test.equalChecker.IgnoreEntitiesFuzzyComparator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public FuzzyComparator fuzzyComparator(){
        return new IgnoreEntitiesFuzzyComparator();
    }
}
