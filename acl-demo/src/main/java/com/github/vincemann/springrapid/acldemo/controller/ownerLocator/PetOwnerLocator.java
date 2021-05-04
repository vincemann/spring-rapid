package com.github.vincemann.springrapid.acldemo.controller.ownerLocator;

import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.slicing.WebComponent;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@WebComponent
public class PetOwnerLocator implements OwnerLocator<Pet> {

    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(Pet.class);
    }

    @Transactional
    @Override
    public Optional<String> find(Pet pet) {
        return Optional.of(pet.getOwner().getUser().getEmail());
    }
}
