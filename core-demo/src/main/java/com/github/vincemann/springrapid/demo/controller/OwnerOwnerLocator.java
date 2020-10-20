package com.github.vincemann.springrapid.demo.controller;

import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.slicing.components.WebComponent;
import com.github.vincemann.springrapid.demo.model.Owner;

import java.util.Optional;

@WebComponent
public class OwnerOwnerLocator implements OwnerLocator<Owner> {

    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(Owner.class);
    }

    @Override
    public Optional<String> find(Owner entity) {
        if (entity.getLastName()==null){
            return Optional.empty();
        }else {
            return Optional.of(entity.getLastName());
        }
    }
}
