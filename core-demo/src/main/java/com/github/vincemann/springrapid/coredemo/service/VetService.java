package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import org.springframework.stereotype.Component;

import java.util.Optional;

public interface VetService extends CrudService<Vet,Long> {

    public Optional<Vet> findByLastName(String lastName);
}
