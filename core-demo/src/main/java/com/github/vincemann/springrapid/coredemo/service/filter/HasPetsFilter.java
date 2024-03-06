package com.github.vincemann.springrapid.coredemo.service.filter;

import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HasPetsFilter implements EntityFilter<Owner> {

    @Override
    public void setArgs(String... args) throws BadEntityException {
        assertAmountArgs(0,args);
    }


    @Override
    public String getName() {
        return "hasPets";
    }

    @Transactional
    @Override
    public boolean match(Owner entity) {
        // should not trigger any initializing
        return !entity.getPets().isEmpty();
    }

    @Override
    public String toString() {
        return "HasPetsFilter{" +
                '}';
    }
}
