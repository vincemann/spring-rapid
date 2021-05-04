package com.github.vincemann.springrapid.acldemo.controller.ownerLocator;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.slicing.WebComponent;

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
