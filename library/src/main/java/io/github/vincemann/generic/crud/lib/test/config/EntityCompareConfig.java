package io.github.vincemann.generic.crud.lib.test.config;

import io.github.vincemann.generic.crud.lib.test.compare.EntityPropertyComparator;
import io.github.vincemann.generic.crud.lib.test.compare.FullComparator;
import io.github.vincemann.generic.crud.lib.test.compare.EntityFullComparator;
import io.github.vincemann.generic.crud.lib.test.compare.PropertyComparator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@TestConfiguration
public class EntityCompareConfig {

    @Scope(SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(FullComparator.class)
    @Bean
    public FullComparator reflectionComparator(){
        return new EntityFullComparator();
    }

    @Scope(SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(PropertyComparator.class)
    @Bean
    public PropertyComparator propertyComparator(){
        return new EntityPropertyComparator();
    }

}
