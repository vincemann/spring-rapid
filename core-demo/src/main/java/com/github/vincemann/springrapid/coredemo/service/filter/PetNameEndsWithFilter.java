package com.github.vincemann.springrapid.coredemo.service.filter;

import com.github.vincemann.springrapid.core.service.EntityFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("hasPetSuffix")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PetNameEndsWithFilter implements EntityFilter<Owner> {

    private String suffix;

    @Override
    public void setArgs(String... args) throws BadEntityException {
        if (args.length != 1)
            throw new BadEntityException("need suffix argument for filter");
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
