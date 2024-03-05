package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import org.springframework.stereotype.Component;

import java.util.Optional;


public interface PetService extends CrudService<Pet,Long> {

    Optional<Pet> findByName(String petName);
}
