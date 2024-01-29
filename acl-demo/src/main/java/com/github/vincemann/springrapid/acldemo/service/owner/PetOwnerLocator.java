package com.github.vincemann.springrapid.acldemo.service.owner;

import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Component
public class PetOwnerLocator implements OwnerLocator<Pet> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(Pet.class);
    }

    @Transactional
    @Override
    public Optional<String> find(Pet pet) {
        return Optional.of(pet.getOwner().getUser().getContactInformation());
    }
}
