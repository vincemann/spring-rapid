package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.test.compare.LevelOnePropertyComparator;
import io.github.vincemann.springrapid.core.test.compare.FullComparator;
import io.github.vincemann.springrapid.core.test.compare.LevelOneFullComparator;
import io.github.vincemann.springrapid.core.test.compare.PropertyComparator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@TestConfiguration
public class EntityCompareAutoConfiguration {

    @Scope(SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(FullComparator.class)
    @Bean
    public FullComparator reflectionComparator(){
        return new LevelOneFullComparator();
    }

    @Scope(SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(PropertyComparator.class)
    @Bean
    public PropertyComparator propertyComparator(){
        return new LevelOnePropertyComparator();
    }

}
