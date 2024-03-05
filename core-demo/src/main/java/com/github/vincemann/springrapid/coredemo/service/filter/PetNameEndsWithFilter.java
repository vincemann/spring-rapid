package com.github.vincemann.springrapid.coredemo.service.filter;

import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PetNameEndsWithFilter implements EntityFilter<Owner> {

    private String suffix;


    @Override
    public String getName() {
        return "petSuffix";
    }

    @Override
    public void setArgs(String... args) throws BadEntityException {
        assertAmountArgs(1,args);
        this.suffix = args[0];
    }

    @Override
    public boolean match(Owner entity) {
        for (Pet pet : entity.getPets()) {
            if (pet.getName().endsWith(suffix))
                return true;
        }
        return false;
    }
}
