package io.github.vincemann.springrapid.coretest.config;

import io.github.vincemann.springrapid.coretest.compare.LevelOnePropertyComparator;
import io.github.vincemann.springrapid.coretest.compare.FullComparator;
import io.github.vincemann.springrapid.coretest.compare.LevelOneFullComparator;
import io.github.vincemann.springrapid.coretest.compare.PropertyComparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@TestConfiguration
@Slf4j
public class EntityCompareAutoConfiguration {

    public EntityCompareAutoConfiguration() {
        log.info("Created");
    }

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
